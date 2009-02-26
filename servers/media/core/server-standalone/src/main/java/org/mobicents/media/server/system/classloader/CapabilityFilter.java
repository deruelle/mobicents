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

import java.util.Set;

import org.jboss.classloader.plugins.ClassLoaderUtils;
import org.jboss.classloader.spi.filter.ClassFilter;
import org.jboss.classloading.spi.metadata.Capability;
import org.jboss.classloading.spi.metadata.ExportPackages;

/**
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class CapabilityFilter implements ClassFilter
{
   private Capability capability;

   public CapabilityFilter(Capability capability)
   {
      if (capability == null)
         throw new IllegalArgumentException("Null capability.");

      this.capability = capability;
   }

   public boolean matchesClassName(String className)
   {
      return matchesPackageName(ClassLoaderUtils.getClassPackageName(className));
   }

   public boolean matchesResourcePath(String resourcePath)
   {
      return matchesPackageName(ClassLoaderUtils.getResourcePackageName(resourcePath));
   }

   public boolean matchesPackageName(String packageName)
   {
      if (capability instanceof ExportPackages)
      {
         ExportPackages ep = (ExportPackages)capability;
         Set<String> packages = ep.getPackageNames(null);
         if (packages != null && packages.contains(packageName))
            return true;
      }

      return false;
   }
}
