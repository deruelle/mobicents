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
package org.mobicents.media.server.system.classloader;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Manifest;

import org.jboss.classloader.plugins.filter.PatternClassFilter;
import org.jboss.classloader.spi.ClassLoaderPolicy;
import org.jboss.classloader.spi.DelegateLoader;
import org.jboss.classloader.spi.PackageInformation;
import org.jboss.classloader.spi.filter.ClassFilter;
import org.jboss.logging.Logger;
import org.jboss.virtual.VFSUtils;
import org.jboss.virtual.VirtualFile;

/**
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class MockClassLoaderPolicy extends ClassLoaderPolicy
{
   /**
    * The log
    */
   private static Logger log = Logger.getLogger(MockClassLoaderPolicy.class);

   /**
    * Tag for no manifest
    */
   private static final Manifest NO_MANIFEST = new Manifest();

   /**
    * The logical name of the policy
    */
   private String name;

   /**
    * The delegates
    */
   private List<? extends DelegateLoader> delegates;

   /**
    * Whether to import all
    */
   private boolean importAll;

   /**
    * The non JDK classes filter
    */
   private ClassFilter nonJDKFilter;

   /**
    * Virtual file root
    */
   private VirtualFile root;

   /**
    * Manifest cache
    */
   private Map<URL, Manifest> manifestCache = new ConcurrentHashMap<URL, Manifest>();

   /**
    * Create a new MockClassLoaderPolicy filtering org.jboss.* classes
    * with logical name "mock"
    */
   public MockClassLoaderPolicy()
   {
      this(null);
   }

   /**
    * Create a new MockClassLoaderPolicy filtering org.jboss.* classes
    *
    * @param name the logical name of the policy
    */
   public MockClassLoaderPolicy(String name)
   {
      this(name, new String[]{"org\\.jboss\\..+"},
            new String[]{"org/jboss/.+"},
            new String[]{"org\\.jboss", "org\\.jboss\\..*"});
   }

   /**
    * Create a new MockClassLoaderPolicy filtering org.jboss.* classes
    *
    * @param name             the name
    * @param classPatterns    the class patterns
    * @param resourcePatterns the resourcePatterns
    * @param packagePatterns  the packagePatterns
    */
   public MockClassLoaderPolicy(String name, String[] classPatterns, String[] resourcePatterns, String[] packagePatterns)
   {
      this(name, new PatternClassFilter(classPatterns, resourcePatterns, packagePatterns));
   }

   /**
    * Create a new MockClassLoaderPolicy filtering the given patterns
    *
    * @param name         the logical name of the policy
    * @param nonJDKFilter the filter for nonJDK classes
    * @throws IllegalArgumentException for a null filter
    */
   public MockClassLoaderPolicy(String name, ClassFilter nonJDKFilter)
   {
      if (name == null)
         name = "mock";
      this.name = name;
      if (nonJDKFilter == null)
         throw new IllegalArgumentException("Null filter");
      this.nonJDKFilter = nonJDKFilter;
   }

   public String getName()
   {
      return name;
   }

   public DelegateLoader getExported()
   {
      return null;
   }

   @Override
   public List<? extends DelegateLoader> getDelegates()
   {
      return delegates;
   }

   /**
    * Set the delegates
    *
    * @param delegates the delegate imports
    */
   public void setDelegates(List<? extends DelegateLoader> delegates)
   {
      this.delegates = delegates;
   }

   @Override
   public boolean isImportAll()
   {
      return importAll;
   }

   /**
    * Set the importAll.
    *
    * @param importAll the importAll.
    */
   public void setImportAll(boolean importAll)
   {
      this.importAll = importAll;
   }

   public VirtualFile getRoot()
   {
      return root;
   }

   public void setRoot(VirtualFile root)
   {
      this.root = root;
   }

   protected VirtualFile findChild(String path)
   {
      try
      {
         return root.getChild(path);
      }
      catch (Exception ignored)
      {
         // not found
      }
      return null;
   }

   protected VirtualFile findRoot(String path)
   {
      try
      {
         if (root.getChild(path) != null)
            return root;
      }
      catch (Exception ignored)
      {
         // not found
      }
      return null;
   }

   protected URL findFileURL(String path)
   {
      try
      {
         VirtualFile child = findChild(path);
         if (child != null)
            return child.toURL();
      }
      catch (Exception ignored)
      {
      }
      return null;
   }

   @Override
   public URL getResource(String path)
   {
      return findFileURL(path);
   }

   @Override
   public void getResources(String path, Set<URL> urls) throws IOException
   {
      URL url = findFileURL(path);
      if (url != null)
         urls.add(url);
   }

   @Override
   public PackageInformation getPackageInformation(String packageName)
   {
      String path = packageName.replace('.', '/');
      VirtualFile root = findRoot(path);
      Manifest manifest = null;
      URL rootURL = null;
      if (root != null)
      {
         try
         {
            rootURL = root.toURL();
            manifest = manifestCache.get(rootURL);
            if (manifest == null)
            {
               manifest = VFSUtils.getManifest(root);
               if (manifest == null)
                  manifestCache.put(rootURL, NO_MANIFEST);
               else
                  manifestCache.put(rootURL, manifest);
            }

            if (manifest == NO_MANIFEST)
               manifest = null;
         }
         catch (Exception ignored)
         {
            if (log.isTraceEnabled())
               log.trace("Unable to retrieve manifest for " + path + " url=" + rootURL + " error=" + ignored.getMessage());
         }
      }
      return new PackageInformation(packageName, manifest);
   }

   @Override
   protected ProtectionDomain getProtectionDomain(String className, String path)
   {
      VirtualFile clazz = findChild(path);
      if (clazz == null)
      {
         log.trace("Unable to determine class file for " + className);
         return null;
      }
      try
      {
         VirtualFile root = clazz.getVFS().getRoot();
         URL codeSourceURL = root.toURL();
         Certificate[] certs = null;
         CodeSource cs = new CodeSource(codeSourceURL, certs);
         PermissionCollection permissions = Policy.getPolicy().getPermissions(cs);
         return new ProtectionDomain(cs, permissions);
      }
      catch (Exception e)
      {
         throw new Error("Error determining protection domain for " + clazz, e);
      }
   }

   /*
    * Overridden to not expose classes in the nonJDKFilter
    * this is so we don't expose classes from the classpath
    * that we haven't explicitly declared in the policy
    */
   @Override
   protected ClassLoader isJDKRequest(String name)
   {
      if (nonJDKFilter.matchesClassName(name))
         return null;
      return super.isJDKRequest(name);
   }

   @Override
   public void toLongString(StringBuilder builder)
   {
      builder.append(" name=").append(name);
      builder.append(" root=").append(root);
      super.toLongString(builder);
   }

   @Override
   public String toString()
   {
      return name;
   }
}
