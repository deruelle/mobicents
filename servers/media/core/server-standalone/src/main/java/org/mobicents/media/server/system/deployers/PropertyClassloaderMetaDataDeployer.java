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
package org.mobicents.media.server.system.deployers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jboss.classloading.plugins.metadata.PackageCapability;
import org.jboss.classloading.plugins.metadata.PackageRequirement;
import org.jboss.classloading.spi.metadata.Capability;
import org.jboss.classloading.spi.metadata.ClassLoadingMetaData;
import org.jboss.classloading.spi.metadata.Requirement;
import org.jboss.classloading.spi.version.Version;
import org.jboss.classloading.spi.version.VersionRange;
import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.virtual.VirtualFile;

/**
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class PropertyClassloaderMetaDataDeployer extends AbstractVFSParsingDeployer<ClassLoadingMetaData>
{
   public PropertyClassloaderMetaDataDeployer()
   {
      super(ClassLoadingMetaData.class);
      setSuffix("-clmd.properties");
   }

   protected ClassLoadingMetaData parse(VFSDeploymentUnit unit, VirtualFile file, ClassLoadingMetaData clmd) throws Exception
   {
      Properties properties = new Properties();
      InputStream inputStream = file.openStream();
      try
      {
         properties.load(inputStream);
      }
      finally
      {
         inputStream.close();
      }
      ClassLoadingMetaData metaData = new ClassLoadingMetaData();
      metaData.setName(unit.getName());
      metaData.setDomain(properties.getProperty("domain", unit.getName()));
      metaData.setParentDomain(properties.getProperty("parent_domain"));
      metaData.setCapabilities(getCapabilities(properties));
      List<Requirement> requirements = getRequirements(properties);
      metaData.setRequirements(requirements);
      metaData.setImportAll(requirements == null || requirements.isEmpty());
      return metaData;
   }

   protected List<Capability> getCapabilities(Properties properties)
   {
      List<Capability> list = new ArrayList<Capability>();
      String capabilities = properties.getProperty("capabilities");
      if (capabilities != null)
      {
         String[] split = capabilities.split("\\|");
         for (String c : split)
         {
            String[] pv = c.split(":");
            String pckg = pv[0];
            Version version = pv.length > 1 ? Version.parseVersion(pv[1]) : Version.DEFAULT_VERSION;
            list.add(new PackageCapability(pckg, version));
         }
      }
      return list;
   }

   protected List<Requirement> getRequirements(Properties properties)
   {
      List<Requirement> list = new ArrayList<Requirement>();
      String requirements = properties.getProperty("requirements");
      if (requirements != null)
      {
         String[] split = requirements.split("\\|");
         for (String c : split)
         {
            String[] pv = c.split(":");
            String pckg = pv[0];
            VersionRange range = VersionRange.ALL_VERSIONS;
            if (pv.length > 1)
            {
               String[] vr = pv[1].split(",");
               Version low = Version.parseVersion(vr[0].substring(1));
               Version high = Version.parseVersion(vr[1].substring(0, vr[1].length() - 1));
               range = new VersionRange(low, vr[0].charAt(0) == '[', high, vr[1].charAt(vr[1].length() - 1) == ']');
            }
            list.add(new PackageRequirement(pckg, range));
         }
      }
      return list;
   }
}
