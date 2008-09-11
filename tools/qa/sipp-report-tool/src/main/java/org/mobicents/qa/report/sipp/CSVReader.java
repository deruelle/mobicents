/*
CSVReader read CSV File

Must be combined with your own code.

 There is another CSVReader at: at http://ostermiller.org/utils/ExcelCSV.html
If this CSVReader is not suitable for you, try that one. <br> There is one written in C# at
 http://www.csvreader.com/

 Future ideas:

  1. allow specify various comment chars that mean the rest the line should be ignored. e.g. ; ! #. These chars have to be in quotes in data then.

  2. allow \ to be used for quoting characters.

 */
package org.mobicents.qa.report.sipp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Read CSV (Comma Separated Value) files. This format is used my Microsoft Word and Excel. Fields are separated by commas, and enclosed in quotes if they contain commas or quotes. Embedded quotes are doubled. Embedded spaces do not normally require surrounding quotes. The last field on the line is not followed by a comma. Null fields are represented by two commas in a row. We optionally trim
 * leading and trailing spaces on fields, even inside quotes. File must normally end with a single CrLf, other wise you will get a null when trying to read a field on older JVMs.
 * 
 * @author copyright (c) 2002-2008 Roedy Green, Canadian Mind Products
 */
public final class CSVReader {
    // ------------------------------ FIELDS ------------------------------

    /**
     * true if want debugging output and debugging test harness code
     */
    private static final boolean DEBUGGING = false;

    /**
     * e.g. \n \r\n or \r, whatever system uses to separate lines in a text file. Only used inside multiline fields. The file itself should use Windows format \r \n, though \n by itself will also work.
     */
    private static final String lineSeparator = System.getProperty("line.separator");

    /**
     * parser: We have just hit a quote, might be doubled or might be last one.
     */
    private static final int AFTER_END_QUOTE = 3;

    /**
     * category of end of line char.
     */
    private static final int EOL = 0;

    /**
     * parser: We are in the middle of an ordinary field.
     */
    private static final int IN_PLAIN = 1;

    /**
     * parser: e are in middle of field surrounded in quotes.
     */
    private static final int IN_QUOTED = 2;

    /**
     * category of ordinary character
     */
    private static final int ORDINARY = 1;

    /**
     * category of the quote mark "
     */
    private static final int QUOTE = 2;

    /**
     * parser: We are in blanks before the field.
     */
    private static final int SEEKING_START = 0;

    /**
     * category of the separator, e.g. comma, semicolon or tab.
     */
    private static final int SEPARATOR = 3;

    /**
     * parser: We are in blanks after the field looking for the separator
     */
    private static final int SKIPPING_TAIL = 4;

    /**
     * category of characters treated as white space.
     */
    private static final int WHITESPACE = 4;

    /**
     * Reader source of the CSV fields to be read.
     */
    private BufferedReader r;

    /**
     * state of the parser's finite state automaton. The line we are parsing. null means none read yet. Line contains unprocessed chars. Processed ones are removed.
     */
    private String line = null;

    /**
     * false means next EOL marks an empty field true means next EOL marks the end of all fields.
     */
    private boolean allFieldsDone = true;

    /**
     * true if reader should allow quoted fields to span more than one line. Microsoft Excel sometimes generates files like this.
     */
    private final boolean allowMultiLineFields;

    /**
     * true if reader should trim lead/trail whitespace from fields returned.
     */
    private final boolean trim;

    /**
     * quote character, usually '\"' '\'' for SOL used to enclose fields containing a separator character.
     */
    private final char quote;

    /**
     * field separator character, usually ',' in North America, ';' in Europe and sometimes '\t' for tab.
     */
    private final char separator;

    /**
     * How many lines we have read so far. Used in error messages.
     */
    private int lineCount = 0;

    // -------------------------- PUBLIC INSTANCE METHODS --------------------------
    /**
     * convenience Constructor, default to comma separator, " for quote, no multiline fields, with trimming.
     * 
     * @param r
     *                input Reader source of CSV Fields to read.
     */
    public CSVReader(Reader r) {
	this(r, ',', '\"', false, true);
    }

    /**
     * Constructor
     * 
     * @param r
     *                input Reader source of CSV Fields to read.
     * @param separator
     *                field separator character, usually ',' in North America, ';' in Europe and sometimes '\t' for tab.
     * @param quote
     *                char to use to enclose fields containing a separator, usually '\"'
     * @param allowMultiLineFields
     *                true if reader should allow quoted fields to span more than one line. Microsoft Excel sometimes generates files like this.
     * @param trim
     *                true if reader should trim lead/trailing whitespace e.g. blanks, Cr, Lf. Tab off fields.
     */
    public CSVReader(Reader r, char separator, char quote, boolean allowMultiLineFields, boolean trim) {
	/* convert Reader to BufferedReader if necessary */
	if (r instanceof BufferedReader) {
	    this.r = (BufferedReader) r;
	} else {
	    this.r = new BufferedReader(r);
	}
	if (this.r == null) {
	    throw new IllegalArgumentException("invalid Reader");
	}

	this.separator = separator;
	this.quote = quote;
	this.allowMultiLineFields = allowMultiLineFields;
	this.trim = trim;
    }

    /**
     * Close the Reader.
     * 
     * @throws IOException
     *                 if problems closing
     */
    public void close() throws IOException {
	if (r != null) {
	    r.close();
	    r = null;
	}
    }

    /**
     * Read one field from the CSV file. You can also use methods like getInt and getDouble to parse the String for you. You can use getAllFieldsInLine to read the entire line including the EOL.
     * 
     * @return String value, even if the field is numeric. Surrounded and embedded double quotes are stripped. possibly "". null means end of line. Normally you use skiptoNextLine to start the next line rather then using get to read the eol.
     * 
     * @throws EOFException
     *                 at end of file after all the fields have been read.
     * @throws IOException
     *                 Some problem reading the file, possibly malformed data.
     * @noinspection UnusedLabel
     */
    public String get() throws IOException {
	StringBuffer field = new StringBuffer(allowMultiLineFields ? 512 : 64);
	/* we implement the parser as a finite state automaton with five states. */

	int state = SEEKING_START;/*
				     * start seeking, even if partway through a line
				     */
	/* don't need to maintain state between fields. */

	while (true) {
	    getLineIfNeeded();

	    charLoop:
	    /* loop for each char in the line to find a field */
	    /* guaranteed to leave early by hitting EOL */
	    for (int i = 0; i < line.length(); i++) {
		char c = line.charAt(i);
		int category = categorise(c);
		if (false) {
		    // for debugging
		    System.out.println("char:" + c + " state:" + state + " field:" + field.length());
		}
		switch (state) {
		case SEEKING_START: {/* in blanks before field */
		    switch (category) {
		    case WHITESPACE:
			/* ignore */
			break;

		    case QUOTE:
			state = IN_QUOTED;
			break;

		    case SEPARATOR:
			/* end of empty field */
			line = line.substring(i + 1);
			return "";

		    case EOL:
			/* end of line */
			if (allFieldsDone) {
			    /* null to mark end of line */
			    line = null;
			    return null;
			} else {
			    /* empty field, usually after a comma */
			    allFieldsDone = true;
			    line = line.substring(i);
			    return "";
			}

		    case ORDINARY:
			field.append(c);
			state = IN_PLAIN;
			break;
		    }
		    break;
		}
		case IN_PLAIN: {/* in middle of ordinary field */
		    switch (category) {
		    case QUOTE:
			throw new IOException("Malformed CSV stream. Missing quote at start of field on line " + lineCount);

		    case SEPARATOR:
			/* done */
			line = line.substring(i + 1);
			return maybeTrim(field);

		    case EOL:
			line = line.substring(i);/* push EOL back */
			allFieldsDone = true;
			return maybeTrim(field);

		    case WHITESPACE:
			field.append(' ');
			break;

		    case ORDINARY:
			field.append(c);
			break;
		    }
		    break;
		}

		case IN_QUOTED: {/* in middle of field surrounded in quotes */
		    switch (category) {
		    case QUOTE:
			state = AFTER_END_QUOTE;
			break;

		    case EOL:
			if (allowMultiLineFields) {
			    field.append(lineSeparator);
			    // we are done with that line, but not with
			    // the
			    // field.
			    // We don't want to return a null
			    // to mark the end of the line.
			    line = null;
			    // will read next line and seek the end of
			    // the
			    // quoted field.
			    // with state = IN_QUOTED.
			    break charLoop;
			} else {
			    // no multiline fields allowed
			    allFieldsDone = true;
			    throw new IOException("Malformed CSV stream. Missing quote (\") after field on line " + lineCount);
			}
		    case WHITESPACE:
			field.append(' ');
			break;

		    case SEPARATOR:
		    case ORDINARY:
			field.append(c);
			break;
		    }
		    break;
		}

		case AFTER_END_QUOTE: {
		    /*
		     * In situation like this "xxx" which may turn out to be xxx""xxx" or "xxx", We find out here.
		     */
		    switch (category) {
		    case QUOTE:
			/* was a double quote, e.g. a literal " */
			field.append(c);
			state = IN_QUOTED;
			break;

		    case SEPARATOR:
			/* we are done with field. */
			line = line.substring(i + 1);
			return maybeTrim(field);

		    case EOL:
			line = line.substring(i);/* push back eol */
			allFieldsDone = true;
			return maybeTrim(field);

		    case WHITESPACE:
			/* ignore trailing spaces up to separator */
			state = SKIPPING_TAIL;
			break;

		    case ORDINARY:
			throw new IOException("Malformed CSV stream, missing separator after field on line " + lineCount);
		    }
		    break;
		}

		case SKIPPING_TAIL: {
		    /* in spaces after field seeking separator */

		    switch (category) {
		    case SEPARATOR:
			/* we are done. */
			line = line.substring(i + 1);
			return maybeTrim(field);

		    case EOL:
			line = line.substring(i);/* push back eol */
			allFieldsDone = true;
			return maybeTrim(field);

		    case WHITESPACE:
			/* ignore trailing spaces up to separator */
			break;

		    case QUOTE:
		    case ORDINARY:
			throw new IOException("Malformed CSV stream, missing separator after field on line " + lineCount);
		    }
		    break;
		}
		}// end switch(state)
	    }// end charLoop
	}// end lineLoop
    }// end get

    /**
     * Get all fields in the line. This reads only one line, not the whole file.
     * 
     * @return Array of strings, one for each field. Possibly empty, but never null.
     * 
     * @throws EOFException
     *                 if run off the end of the file.
     * @throws IOException
     *                 if some problem reading the file.
     */
    public String[] getAllFieldsInLine() throws IOException {
	List<String> al = new ArrayList<String>(30);
	do {
	    String field = get();
	    if (field == null) {
		break;
	    }
	    al.add(field);
	} while (true);
	return al.toArray(new String[al.size()]);
    }

    /**
     * Read one double field from the CSV file.
     * 
     * @return double value, empty field returns 0, as does end of line.
     * 
     * @throws EOFException
     *                 at end of file after all the fields have been read.
     * @throws IOException
     *                 Some problem reading the file, possibly malformed data.
     * @throws NumberFormatException,
     *                 if field does not contain a well-formed double.
     * @noinspection UnusedDeclaration
     */
    public double getDouble() throws IOException, NumberFormatException {
	String s = get();
	if (s == null) {
	    return 0;
	}
	if (!trim) {
	    s = s.trim();
	}
	if (s.length() == 0) {
	    return 0;
	}
	return Double.parseDouble(s);
    }

    /**
     * Read one float field from the CSV file.
     * 
     * @return float value, empty field returns 0, as does end of line.
     * 
     * @throws EOFException
     *                 at end of file after all the fields have been read.
     * @throws IOException
     *                 Some problem reading the file, possibly malformed data.
     * @throws NumberFormatException,
     *                 if field does not contain a well-formed float.
     * @noinspection UnusedDeclaration
     */
    public float getFloat() throws IOException, NumberFormatException {
	String s = get();
	if (s == null) {
	    return 0;
	}
	if (!trim) {
	    s = s.trim();
	}
	if (s.length() == 0) {
	    return 0;
	}
	return Float.parseFloat(s);
    }

    /**
     * Read one integer field from the CSV file
     * 
     * @return int value, empty field returns 0, as does end of line.
     * 
     * @throws EOFException
     *                 at end of file after all the fields have been read.
     * @throws IOException
     *                 Some problem reading the file, possibly malformed data.
     * @throws NumberFormatException,
     *                 if field does not contain a well-formed int.
     * @noinspection UnusedDeclaration
     */
    public int getInt() throws IOException, NumberFormatException {
	String s = get();
	// end of line returns 0
	if (s == null) {
	    return 0;
	}
	if (!trim) {
	    s = s.trim();
	}
	if (s.length() == 0) {
	    return 0;
	}
	return Integer.parseInt(s);
    }

    /**
     * Read one long field from the CSV file
     * 
     * @return long value, empty field returns 0, as does end of line.
     * 
     * @throws EOFException
     *                 at end of file after all the fields have been read.
     * @throws IOException
     *                 Some problem reading the file, possibly malformed data.
     * @throws NumberFormatException,
     *                 if field does not contain a well-formed long.
     * @noinspection UnusedDeclaration
     */
    public long getLong() throws IOException, NumberFormatException {
	String s = get();
	if (s == null) {
	    return 0;
	}
	if (!trim) {
	    s = s.trim();
	}

	if (s.length() == 0) {
	    return 0;
	}
	return Long.parseLong(s);
    }

    /**
     * Skip over fields you don't want to process.
     * 
     * @param fields
     *                How many field you want to bypass reading. The newline counts as one field.
     * 
     * @throws EOFException
     *                 at end of file after all the fields have been read.
     * @throws IOException
     *                 Some problem reading the file, possibly malformed data.
     * @noinspection UnusedDeclaration
     */
    public void skip(int fields) throws IOException {
	if (fields <= 0) {
	    return;
	}
	for (int i = 0; i < fields; i++) {
	    // throw results away
	    get();
	}
    }

    /**
     * Skip over remaining fields on this line you don't want to process.
     * 
     * @throws EOFException
     *                 at end of file after all the fields have been read.
     * @throws IOException
     *                 Some problem reading the file, possibly malformed data.
     */
    public void skipToNextLine() throws IOException {
	if (line == null) {
	    getLineIfNeeded();
	}
	line = null;
    }

    // -------------------------- OTHER METHODS --------------------------

    /**
     * categorise a character for the finite state machine.
     * 
     * @param c
     *                the character to categorise
     * 
     * @return integer representing the character's category.
     */
    private int categorise(char c) {
	switch (c) {
	case ' ':
	case '\r':
	case 0xff:
	    return WHITESPACE;

	case '\n':
	    return EOL;/* artificially applied to end of line */

	default:
	    if (c == quote)// might be ' or " or something else
	    {
		return QUOTE;
	    } else if (c == separator/* might be , or ; dynamically determined so can't use as case label */) {
		return SEPARATOR;
	    }
	    /* do our tests in crafted order, hoping for an early return */
	    else if ('!' <= c && c <= '~')/* includes A-Z a-z 0-9 common punctuation */
	    {
		return ORDINARY;
	    } else if (0x00 <= c && c <= 0x20) {
		return WHITESPACE;
	    } else if (Character.isWhitespace(c)) {
		return WHITESPACE;
	    } else {
		return ORDINARY;
	    }
	}
    }

    /**
     * Make sure a line is available for parsing. Does nothing if there already is one.
     * 
     * @throws EOFException
     *                 if hit the end of the file.
     */
    private void getLineIfNeeded() throws IOException {
	if (line == null) {
	    if (r == null) {
		throw new IllegalArgumentException("attempt to use a closed CSVReader");
	    }
	    allFieldsDone = false;
	    line = r.readLine();/* this strips platform specific line ending */
	    if (line == null) /*
				 * null means EOF, yet another inconsistent Java convention.
				 */
	    {
		throw new EOFException();
	    } else {
		line += '\n';/* apply standard line end for parser to find */
		lineCount++;
	    }
	}
    }

    /**
     * Trim the string, but only if we are in trimming mode.
     * 
     * @param field
     *                StringBuffer to be trimmed.
     * 
     * @return String or trimmed string.
     */
    private String maybeTrim(StringBuffer field) {
	final String s = field.toString();

	if (trim) {
	    return s.trim();
	} else {
	    return s;
	}
    }

    // --------------------------- main() method ---------------------------

    /**
     * Test driver
     * 
     * @param args
     *                not used
     * 
     * @noinspection InfiniteLoopStatement
     */
    public static void main(String[] args) {
	if (DEBUGGING) {
	    try {
		// read test file
		CSVReader csv = new CSVReader(new FileReader("test.csv"), ',', '\"', true, false);
		try {
		    while (true) {
			System.out.println("--> " + csv.get());
		    }
		} catch (EOFException e) {
		    // normal termination.
		}
		csv.close();
	    } catch (IOException e) {
		e.printStackTrace();
		System.out.println(e.getMessage());
	    }
	}// end if
    }// end main
}// end CSVReader class.
