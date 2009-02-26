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

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import org.jboss.deployers.client.spi.DeployerClient;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.vfs.spi.client.VFSDeployment;
import org.jboss.deployers.vfs.spi.client.VFSDeploymentFactory;
import org.jboss.deployers.vfs.spi.structure.modified.StructureModificationChecker;
import org.jboss.virtual.VirtualFile;

/**
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class MainDeployerHelper
{
   private DeployerClient deployerClient;
   private VFSDeploymentFactory factory;
   private StructureModificationChecker checker;

   private Map<String, VFSDeployment> deployments;

   public MainDeployerHelper(DeployerClient deployerClient)
   {
      this.deployerClient = deployerClient;
      this.deployments = new HashMap<String, VFSDeployment>();
   }

   protected VFSDeploymentFactory getDeploymentFactory()
   {
      if (factory == null)
         factory = VFSDeploymentFactory.getInstance();
      return factory;
   }

   protected VFSDeployment createDeployment(VirtualFile file)
   {
      return getDeploymentFactory().createVFSDeployment(file);
   }

   protected VFSDeployment handleDeployment(VirtualFile file) throws DeploymentException
   {
      VFSDeployment deployment = createDeployment(file);
      deployerClient.addDeployment(deployment);
      return deployments.put(file.getName(), deployment);
   }

   public boolean hasBeenModified(String fileName) throws IOException
   {
      VFSDeployment deployment = deployments.get(fileName);
      return deployment != null && checker.hasStructureBeenModified(deployment);
   }

   public void deploy(VirtualFile file) throws DeploymentException
   {
      handleDeployment(file);
   }

   public void redeploy(VirtualFile file) throws DeploymentException
   {
      handleDeployment(file);
   }

   public void undeploy(String fileName) throws DeploymentException
   {
      VFSDeployment deployment = deployments.get(fileName);
      if (deployment != null)
         deployerClient.removeDeployment(deployment);
   }

   public void process() throws DeploymentException
   {
      deployerClient.process();
      deployerClient.checkComplete();
   }

   public void setChecker(StructureModificationChecker checker)
   {
      this.checker = checker;
   }
}
