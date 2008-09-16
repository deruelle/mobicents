package org.mobicents.qa.report.sipp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.encoders.ImageEncoder;
import org.jfree.chart.encoders.KeypointPNGEncoderAdapter;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

public class Report {

    private static Logger logger = Logger.getLogger(Report.class.getName());

    private enum FieldType {
	DOUBLE, TIME, DATE, INVALID
    };

    private static Set<String> selectedCategories = new HashSet<String>();

    static {
	String[] selected = new String[] { "TargetRate", "CallRate", "IncomingCall", "CurrentCall", "SuccessfulCall", "FailedCall", "FailedMaxUDPRetrans", "FailedUnexpectedMessage", "DeadCallMsgs", "Retransmissions", "ResponseTime1", "ResponseTime1StDev", "CallLength", "CallLengthStDev" };

	for (String category : selected) {
	    selectedCategories.add(category);
	    selectedCategories.add(category.concat("(C)"));
	    selectedCategories.add(category.concat("(P)"));
	}
    }

    private static String referenceCategory = "ElapsedTime(C)";

    private static boolean allCharts = false;

    private static boolean singleFile = false;

    private static boolean printCharts = false;

    private static boolean bigCharts = false;

    private static void printInfo() {
	logger.info("Usage: java -jar 'thisFile' [options] [file1 ... fileN]");
	logger.info("Usage: If no files are specified, all .csv files in current directory are used");
	logger.info("Option: -a - ALL    - Generates charts for all the categories");
	logger.info("Option: -b - BIG    - Generates charts with 1 pixel for each elapsed second");
	logger.info("Option: -d - DEBUG  - Extra information during program execution");
	logger.info("Option: -h - HELP   - Shows this info and exits");
	logger.info("Option: -p - PRINT  - Print chart images in a subfolder");
    }

    public static void main(String[] args) {

	// Setup Log4j
	Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%c %-5p %m%n")));
	logger.setLevel(Level.INFO);
	logger.info("Sipp Report Tool starting ... ");

	// Search for -d flag
	for (String string : args) {
	    if ("-d".equals(string)) {
		logger.setLevel(Level.DEBUG);
		logger.debug("Debug level set");
		break;
	    }
	}

	// Pring arguments
	if (logger.isDebugEnabled()) {
	    logger.debug(Arrays.toString(selectedCategories.toArray()));

	    String s = "Arguments:_";
	    for (String string : args) {
		s += string + "_|_";
	    }
	    logger.debug(s);
	}

	// Get filenames
	Set<String> filenames = new HashSet<String>();
	for (String string : args) {
	    if (string.charAt(0) != '-') {
		filenames.add(string);
		logger.debug("File to open: " + string);
	    } else {
		if ("-a".equals(string)) {
		    allCharts = true;
		    logger.info("All charts set");
		    continue;
		}
		if ("-p".equals(string)) {
		    printCharts = true;
		    logger.info("Print chart images set");
		    continue;
		}
		if ("-b".equals(string)) {
		    bigCharts = true;
		    logger.info("Big chart set");
		    continue;
		}
		if (("-h".equals(string)) || ("--help".equals(string))) {
		    printInfo();
		    return;
		}
	    }
	}

	if (filenames.isEmpty()) {
	    try {
		URL url = Report.class.getProtectionDomain().getCodeSource().getLocation();
		File myDir = new File(URLDecoder.decode(url.getFile(), "UTF-8")).getAbsoluteFile().getParentFile();
		File[] files = myDir.listFiles(new FilenameFilter() {

		    public boolean accept(File file, String s) {
			if (s.endsWith(".csv")) {
			    return true;
			} else {
			    return false;
			}
		    }
		});

		for (File file : files) {
		    filenames.add(file.getAbsolutePath());
		}

	    } catch (UnsupportedEncodingException e) { // should not happen
		logger.warn("Could not get current dir");
	    }
	}

	if (filenames.isEmpty()) {
	    printInfo();
	} else {
	    if (filenames.size() == 1) {
		singleFile = true;
		logger.debug("Single file mode - set");
	    }

	    // Create the report
	    for (String string : filenames) {
		createReports(string);
	    }
	    logger.info("Done. Oh yeah!");
	}
    }

    public static void createReports(String filename) {
	try {

	    // Create a CSV reader
	    OpenCsvReader csv = new OpenCsvReader(new FileReader(filename), ';', '\"');

	    // Get categories
	    String[] categories = csv.readNext();
	    if (logger.isDebugEnabled()) {
		logger.debug("Categories read from CSV: " + Arrays.toString(categories));
	    }

	    // Get values
	    List<String[]> values = csv.readAll();
	    int rows = values.size();
	    
	    csv.close();

	    // Get reference categories (elapsed time)
	    double[] referenceData = null;
	    for (int i = 0; i < categories.length; i++) {
		if (referenceCategory.equals(categories[i])) {
		    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		    double timeReference = timeFormat.parse(values.get(0)[i]).getTime();

		    int n = 0;
		    referenceData = new double[rows];
		    for (String[] value : values) {
			try {
			    referenceData[n++] = (timeFormat.parse(value[i]).getTime() - timeReference) / 1000;
			} catch (ParseException e) {
			    logger.warn("Unexpeted parse exception for field " + categories[i], e);
			}
		    }
		}
	    }

	    if (referenceData == null) {
		logger.error("Invalid reference category: " + referenceCategory);
		return;
	    }

	    // convert to categories
	    Map<String, XYDataset> categoryValues = new LinkedHashMap<String, XYDataset>();
	    Map<String, FieldType> categoryTypes = new LinkedHashMap<String, FieldType>();

	    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss:SSS");
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

	    for (int i = 0; i < categories.length; i++) {
		if (!selectedCategories.contains(categories[i]) && !allCharts) {
		    logger.debug("Category " + categories[i] + " does not belong to selected categories. Dropping.");
		    continue;
		}

		// check for usable colums
		try {
		    Double.parseDouble(values.get(0)[i]);
		    categoryTypes.put(categories[i], FieldType.DOUBLE);
		    logger.debug("Category " + categories[i] + " is of Double type (" + values.get(0)[i] + ")");
		} catch (NumberFormatException nfe) {
		    try {
			timeFormat.parse(values.get(0)[i]);
			categoryTypes.put(categories[i], FieldType.TIME);
			logger.debug("Category " + categories[i] + " is of Time type (" + values.get(0)[i] + ")");
		    } catch (ParseException pe) {
			try {
			    dateFormat.parse(values.get(0)[i]);
			    categoryTypes.put(categories[i], FieldType.DATE);
			    logger.warn("Date format not implemented for field " + categories[i] + " (" + values.get(0)[i] + ")");
			    continue;
			} catch (ParseException e) {
			    categoryTypes.put(categories[i], FieldType.INVALID);
			    logger.warn("Column " + categories[i] + " is not in numeric format (" + values.get(0)[i] + "): " + e.getMessage());
			    continue;
			}
		    }
		}

		// convert values
		DefaultXYDataset dataset = new DefaultXYDataset();
		double timeReference = 0;
		if (categoryTypes.get(categories[i]) == FieldType.TIME) {
		    timeReference = timeFormat.parse(values.get(0)[i]).getTime();
		}

		int n = 0;
		double[] valueData = new double[rows];

		switch (categoryTypes.get(categories[i])) {
		case DOUBLE:
		    for (String[] value : values) {
			valueData[n++] = Double.parseDouble(value[i]);
		    }
		    break;
		case TIME:
		    boolean isStDev = categories[i].contains("StDev");
		    for (String[] value : values) {
			try {
			    double myTimeValue = (timeFormat.parse(value[i]).getTime() - timeReference) / 1000;

			    if (isStDev && (myTimeValue < 0)) {
				valueData[n++] = Double.NaN;
			    } else {
				valueData[n++] = myTimeValue;
			    }
			} catch (ParseException e) {
			    logger.warn("Unexpeted parse exception for field " + categories[i], e);
			}
		    }
		    break;
		case DATE:
		    break;
		default:
		    break;
		}

		dataset.addSeries(categories[i], new double[][] { referenceData, valueData });
		categoryValues.put(categories[i], dataset);
	    }

	    if (categoryValues.isEmpty()) {
		logger.warn("No categories to be written to file.");
		return;
	    }

	    // print available columns
	    if (logger.isDebugEnabled()) {
		logger.debug("Writting categories: " + Arrays.toString(categoryValues.keySet().toArray()));
	    }
	    logger.info("Writting report '" + (singleFile ? "report.pdf" : filename.replaceAll(".csv", ".pdf")) + "'  ...");

	    // Write files (1600 is the default value because it looks prettier in my display)
	    int referenceSize = bigCharts ? new Double(referenceData[referenceData.length - 1]).intValue() : 1600;
	    int imageSizeX = referenceSize;
	    int imageSizeY = 800;

	    Document document = new Document();
	    document.setPageSize(new Rectangle(imageSizeX, imageSizeY));
	    document.setMargins(0, 0, 0, 0);
	    PdfWriter.getInstance(document, new FileOutputStream(singleFile ? "report.pdf" : filename.replaceAll(".csv", ".pdf")));
	    document.open();

	    for (String category : categoryValues.keySet()) {
		String title = category.replace("(P)", " (Periodic)").replace("(C)", " (Cumulative)");
		String xLabel = referenceCategory.replace("(P)", "").replace("(C)", "") + " (seconds)";
		String yLabel = category.replace("(P)", "").replace("(C)", "");
		switch (categoryTypes.get(category)) {
		case DOUBLE:
		    if (category.contains("Rate")) {
			yLabel += " (call/s)";
			title = title.replace("(Cumulative)", "(Average)");
		    } else if (category.contains("(P)")) {
			yLabel += " per second";
		    }
		    break;
		case TIME:
		    yLabel += " (seconds)";
		    title = title.replace("(Cumulative)", "(Average)");
		    break;
		case DATE:
		    break;
		case INVALID:
		    break;
		default:
		    break;
		}

		JFreeChart chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, categoryValues.get(category), PlotOrientation.VERTICAL, false, false, false);
		BufferedImage image = chart.createBufferedImage(imageSizeX, imageSizeY);

		document.add(Image.getInstance(image, null));
		logger.debug("Wrote category chart " + category);

		if (printCharts) {
		    String newDirName = singleFile ? "charts" : filename.split("_")[1];
		    ImageEncoder encoder = new KeypointPNGEncoderAdapter();
		    new File(newDirName).mkdir();
		    String chartFile = newDirName + File.separator + category + "Chart.png";
		    encoder.encode(image, new FileOutputStream(chartFile));
		    logger.debug("Wrote category chart to file " + category);
		}
	    }

	    document.close();

	} catch (FileNotFoundException e) {
	    logger.error("Unable to open file: " + filename, e);
	} catch (IOException e) {
	    logger.warn("IOException in csv file: " + filename, e);
	} catch (DocumentException e) {
	    logger.warn("DocumentException: " + e.getMessage(), e);
	} catch (ParseException e) {
	    logger.warn("ParseException: " + e.getMessage(), e);
	}

    }

}
