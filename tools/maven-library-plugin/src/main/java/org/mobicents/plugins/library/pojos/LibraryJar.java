package org.mobicents.plugins.library.pojos;

import java.io.File;

/**
 * 
 * LibraryJar.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class LibraryJar {

  private File file;
  private String description;

  public LibraryJar(File file, String description)
  {
    this.file = file;
    this.description = description;
  }
  
  public LibraryJar(File file)
  {
    this.file = file;
  }
  
  public File getFile()
  {
    return file;
  }
  
  public String toXmlEntry()
  {
    return "\t\t<jar>\r\n" +
        (this.description != null ? "\t\t\t<description>" + this.description + "</description>\r\n" : "") +
        "\t\t\t<jar-name>jars/" + this.file.getName() + "</jar-name>\r\n" +
    		"\t\t</jar>\r\n";
  }
}
