/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.mobicents.media.server.bootstrap;

import java.io.File;
import java.net.URL;

import org.jboss.kernel.Kernel;
import org.jboss.kernel.plugins.bootstrap.basic.BasicBootstrap;
import org.jboss.kernel.plugins.deployment.xml.BasicXMLDeployer;
import org.jboss.util.StringPropertyReplacer;

/**
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author <a href="mailto:amit.bhayani@jboss.com">amit bhayani</a>
 */
public class Main
{
   private static Main main;
   
   public static final String MMS_HOME = "mms.home.dir"; 

   private Kernel kernel;
   private BasicXMLDeployer kernelDeployer;

   public static void main(String[] args)
   {
      main = new Main();
      main.configure(args);
   }

   protected void configure(String[] args)
   {
      int index = 0;
      String mmsHome;
      // demos.home
      if (System.getenv("MMS_HOME") == null)
      {
        
         if (args.length > index)
        	 mmsHome = args[index++];
         else
        	 mmsHome = ".";

         System.setProperty(MMS_HOME, mmsHome);
      } else{
    	  mmsHome = System.getenv("MMS_HOME");
    	  System.setProperty(MMS_HOME, mmsHome);
      }

      try
      {
         String rootString;
         if (args.length > index)
            rootString = args[index];
         else
            rootString = "${"+MMS_HOME+"}/bootstrap/bootstrap-beans.xml";

         URL rootURL = getURL(rootString);
         System.out.println("Using bootstrap: " + rootURL.toExternalForm());

         BasicBootstrap bootstrap = new BasicBootstrap();
         bootstrap.run();
         kernel = bootstrap.getKernel();

         kernelDeployer = new BasicXMLDeployer(kernel);
         kernelDeployer.deploy(rootURL);
         kernelDeployer.validate();
      }
      catch (Throwable t)
      {
         t.printStackTrace();
      }
   }

   private static Main getMain()
   {
      if (main == null)
         throw new IllegalArgumentException("Main in not initialized, should run Main.main()!");

      return main;
   }

   public static URL getURL(String url) throws Exception
   {
      // replace ${} inputs
      url = StringPropertyReplacer.replaceProperties(url, System.getProperties());
      File file = new File(url);
      if (file.exists() == false)
         throw new IllegalArgumentException("No such file: " + url);

      return file.toURI().toURL();
   }

   public static void deploy(String... urls) throws Throwable
   {
      if (urls == null)
         return;

      for (String urlString : urls)
      {
         URL url = getURL(urlString);
         getMain().kernelDeployer.deploy(url);
         System.out.println("Deployed URL: " + url);
      }
   }

   public static void validate() throws Throwable
   {
      getMain().kernelDeployer.validate();
   }

   public static void undeploy(String... urls) throws Throwable
   {
      if (urls == null)
         return;

      for (String urlString : urls)
      {
         URL url = getURL(urlString);
         getMain().kernelDeployer.undeploy(url);
         System.out.println("Undeployed URL: " + url);
      }
   }

   protected void registerShutdownThread()
   {
      Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownThread()));
   }

   private class ShutdownThread implements Runnable
   {
      public void run()
      {
         kernelDeployer.shutdown();
         kernelDeployer = null;

         kernel.getController().shutdown();
         kernel = null;
      }
   }
}
