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

import java.util.List;

import org.jboss.classloader.spi.ClassLoaderPolicy;
import org.jboss.classloader.spi.DelegateLoader;
import org.jboss.classloader.spi.filter.FilteredDelegateLoader;
import org.jboss.classloading.spi.dependency.Module;
import org.jboss.classloading.spi.dependency.policy.ClassLoaderPolicyModule;
import org.jboss.classloading.spi.metadata.Capability;
import org.jboss.classloading.spi.metadata.ClassLoadingMetaData;
import org.jboss.classloading.spi.metadata.Requirement;
import org.jboss.deployers.plugins.classloading.AbstractClassLoaderDescribeDeployer;
import org.jboss.deployers.plugins.classloading.AbstractDeploymentClassLoaderPolicyModule;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.mobicents.media.server.system.classloader.CapabilityFilter;
import org.mobicents.media.server.system.classloader.MockClassLoaderPolicy;

/**
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class ClassloadingDescribeDeployer extends AbstractClassLoaderDescribeDeployer
{
   protected ClassLoaderPolicyModule createModule(final DeploymentUnit unit, ClassLoadingMetaData metaData) throws DeploymentException
   {
      if (unit instanceof VFSDeploymentUnit == false)
         throw new IllegalArgumentException("Can only take VFS deployments: " + unit);

      return new AbstractDeploymentClassLoaderPolicyModule(unit)
      {
         protected ClassLoaderPolicy determinePolicy()
         {
            MockClassLoaderPolicy policy = new MockClassLoaderPolicy(unit.getSimpleName());
            VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit)unit;
            policy.setRoot(vfsUnit.getRoot());
            policy.setDelegates(getDelegates());
            return policy;
         }

         public DelegateLoader getDelegateLoader(Module module, Requirement requirement)
         {
            List<Capability> capabilities = getCapabilities();
            if (capabilities != null && capabilities.isEmpty() == false)
            {
               for (Capability capability : capabilities)
               {
                  if (capability.resolves(this, requirement))
                     return new FilteredDelegateLoader(getPolicy(), new CapabilityFilter(capability));
               }
               // none of the capabilities match - don't put it as delegate
               return null;
            }
            return super.getDelegateLoader(module, requirement);
         }
      };
   }
}
