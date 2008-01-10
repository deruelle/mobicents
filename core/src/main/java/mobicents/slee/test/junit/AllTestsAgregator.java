package org.mobicents.slee.test.junit;

import java.io.File;
import java.lang.reflect.Method;

import java.util.regex.Pattern;

//import org.apache.log4j.Logger;
import org.mobicents.slee.runtime.cache.tests.CacheAllTests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTestsAgregator {

	//private static final Logger log=Logger.getLogger(AllTestsAgregator.class);
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
		String mobicents_HOME=System.getProperty("MOBICENTS_HOME");
		File rootDir=new File(mobicents_HOME+"/classes");
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
		TestSuite suite=new TestSuite();
		
		
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
    					testClass=CacheAllTests.class.forName(packageName+"."+className);
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
