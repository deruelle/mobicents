package tests;
/*
 * Mobicents: The Open Source VoIP Middleware Platform
 *
 * Copyright 2003-2006, Mobicents, 
 * and individual contributors as indicated
 * by the @authors tag. See the copyright.txt 
 * in the distribution for a full listing of   
 * individual contributors.
 *
 * This is free software; you can redistribute it
 * and/or modify it under the terms of the 
 * GNU General Public License (GPL) as
 * published by the Free Software Foundation; 
 * either version 2 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that 
 * it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the 
 * GNU General Public
 * License along with this software; 
 * if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, 
 * Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
import java.io.File;
import java.lang.reflect.Method;

import java.util.logging.Logger;
import java.util.regex.Pattern;

//import org.apache.log4j.Logger;
import org.apache.cactus.ServletTestSuite;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This is copy of a class written for Mobicents, its purpose is to agreageta all junit tests in one suite so there is one output.
 * @author baranowb
 *
 */
public class AllTestsAgregator extends TestCase{

	private static final Logger log=Logger.getLogger(AllTestsAgregator.class.getCanonicalName());
	//pattern - ZNameTest.class
	//Should this be configurable?
	public static final String testClassNamePattern="Z\\w+Test\\.class";
	public final static String classes="classes";
	public static void main(String[] args)
	{
		AllTestsAgregator.suite();
	}
	public static Test suite()
	{
		//HERE WE HAVE TO MAKE LIST OF ALL FILES Z*Test.java 
		//AND RETRIEVE  THEIR suite() - agregated in one suite
		//WE HAVE TO ITERATE THROUGH ALL PACKAGES.... urps
		
		
		TestSuite suite=new TestSuite();
		
		//OR IS THERE ANY BETTER WAY OF DOING THAT? Travers relative path from this *.class file?
		
		String rootFQDN=AllTestsAgregator.class.getResource("AllTestsAgregator.class").getFile().toString();

		File rootDir=new File(rootFQDN.substring(0,rootFQDN.indexOf("classes"))+"classes");
		if(!rootDir.exists())
		{
			System.out.println("ERROR DIR["+rootDir+"] DOES NOT EXIST");
			
			return suite;
		}

		suite.addTest(digInDir(rootDir));
		
		return suite;
	}
	
	/**
	 *  Digs into dir in search for junit tests that match pattern. If it finds it adds their 
	 * suite to returned suite.<br>
	 * It digs until it getts to bottom (recursive)
	 * @param dir
	 * @return - Test suite.
	 */
	private static Test digInDir(File dir)
	{
		ServletTestSuite suite = new ServletTestSuite();
		
		
		String[] files;  // The names of the files in the directory.
        files = dir.list();
        for (int i = 0; i < files.length; i++) {
            File f;  // One of the files in the directory.
            f = new File(dir, files[i]);
            if ( f.isDirectory() ) {
                   // Call listContents() recursively to
                   // list the contents of the directory, f.
                suite.addTest(digInDir(f));
            }
            else {
                
            	//LETS SEE THE NAME
            	//System.out.println("NAME["+f.getName()+"]");
            	String fileName=f.getName();
            	
            	if(Pattern.matches(testClassNamePattern,fileName))
    			{
    				//LETS loose .class
    				String className=fileName.split("\\.")[0];
    		
    				//WE NEED PACKAGE NAME
    				String packageName="";
    				String parentName=f.getParentFile().getPath();
    				
    				
    				int index=parentName.indexOf(classes);
    				//HERE WE WILL STRIP mobicent_home/classes/ prefix from path, +1 is for "\"
    				packageName=parentName.substring(index+classes.length()+1,parentName.length());

    				packageName=packageName.replace(File.separator.charAt(0),".".charAt(0));
    				
    				//WE NEED INSTANCE OF THIS CLASS
    				Class testClass=null;
    				try {
    					testClass=AllTestsAgregator.class.forName(packageName+"."+className);
    				} catch (ClassNotFoundException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    				if(testClass==null)
    				{
    					System.out.println("TESTS CLASS IS NULL");
    					continue;
    				}
    				Method method=null;
					try {
						method = testClass.getMethod("suite",null);
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				if(method!=null)
    				{
    					System.out.println("ADDDING["+testClass+"]");
    					//LETS LOAD SUITE, this can be done automaticaly, we just have to specify class
    					suite.addTestSuite(testClass);
    				}else
    				{
    					System.out.println("CLASS["+testClass+"] MATCHES PATTERN BUT DOESNT HAVE suite() METHOD!!");
    				}
    					
    				
    			}
    			else
    			{
    				//DO NOTHING
    				continue;
    			}
            	
            	
            	
            }
        }
		
		
		return suite;
	}
}
