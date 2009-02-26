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
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

/**
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class HDScanner implements Runnable
{
   private static Logger log = Logger.getLogger(HDScanner.class);
   private ScheduledExecutorService scanExecutor;
   private ScheduledFuture activeScan;
   private String scanThreadName = "HDScanner";
   private long initialDelay = 5000;
   private long scanPeriod = 10000;

   private MainDeployerHelper helper;
   private VirtualFile root;
   private Set<String> files = new HashSet<String>();

   public HDScanner(MainDeployerHelper helper, String path) throws IOException
   {
      File dir = new File(path);
      if (dir.exists() == false)
         throw new IOException("No such path: " + path);
      if (dir.isDirectory() == false)
         throw new IOException("Path doesn't denote directory: " + dir);

      this.helper = helper;
      this.root = VFS.createNewRoot(dir.toURI());
   }

   public void setScanThreadName(String scanThreadName)
   {
      this.scanThreadName = scanThreadName;
   }

   public void setInitialDelay(long initialDelay)
   {
      this.initialDelay = initialDelay;
   }

   public void setScanPeriod(long scanPeriod)
   {
      this.scanPeriod = scanPeriod;
   }

   public void start() throws Exception
   {
      // Default to a single thread executor
      if (scanExecutor == null)
      {
         scanExecutor = Executors.newSingleThreadScheduledExecutor(
               new ThreadFactory()
               {
                  public Thread newThread(Runnable r)
                  {
                     return new Thread(r, scanThreadName);
                  }
               }
         );
      }
      activeScan = scanExecutor.scheduleWithFixedDelay(this, initialDelay, scanPeriod, TimeUnit.MILLISECONDS);
   }

   public void stop()
   {
      if (activeScan != null)
      {
         activeScan.cancel(true);
         activeScan = null;
      }
   }

   public void run()
   {
      try
      {
         scan();
      }
      catch (Throwable e)
      {
         System.err.println("Scan failed: " + e);
      }
   }

   protected void scan() throws Throwable
   {
      Set<String> tmp = new HashSet<String>();
      List<VirtualFile> children = root.getChildren();
      for (VirtualFile file : children)
      {
         String fileName = file.getName();
         if (files.contains(fileName) == false)
         {
            helper.deploy(file);
            log.info("Deploy: " + file);
         }
         else
         {
            if (helper.hasBeenModified(fileName))
            {
               helper.redeploy(file);
               log.info("Redeploy: " + file);
            }
            files.remove(fileName);
         }
         tmp.add(fileName);
      }
      for (String fileName : files)
      {
         helper.undeploy(fileName);
         log.info("Undeploy: " + fileName);
      }
      files.clear();
      files.addAll(tmp);
      helper.process();
   }
}
