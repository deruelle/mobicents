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
package org.mobicents.media.server.system.deployers.legacy;

import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.structure.StructureMetaData;
import org.jboss.deployers.spi.structure.ContextInfo;
import org.jboss.deployers.vfs.plugins.structure.AbstractVFSStructureDeployer;
import org.jboss.deployers.vfs.spi.structure.StructureContext;
import org.jboss.deployers.vfs.spi.structure.VFSStructuralDeployers;
import org.jboss.deployers.vfs.spi.structure.helpers.AbstractStructureDeployer;
import org.jboss.virtual.VirtualFile;

/**
 * Legacy directory structure.
 * See JBAS-5900 for more details.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
@Deprecated
public class DirectoryStructure extends AbstractVFSStructureDeployer
{
   public DirectoryStructure()
   {
      setRelativeOrder(Integer.MAX_VALUE);
   }

   public boolean determineStructure(StructureContext context) throws DeploymentException
   {
      try
      {
         VirtualFile file = context.getFile();
         // jar structure should already handle top level dirs
         if (context.isTopLevel() == false && isLeaf(file) == false && isMetadataPath(context) == false)
         {
            List<VirtualFile> children = file.getChildren();
            if (children != null && children.isEmpty() == false)
            {
               VFSStructuralDeployers structuralDeployers = context.getDeployers();

               // get top
               while (context.getParentContext() != null)
                  context = context.getParentContext();

               for (VirtualFile child : children)
                  structuralDeployers.determineStructure(child, context);
            }
         }
         return false;
      }
      catch (Exception e)
      {
         throw DeploymentException.rethrowAsDeploymentException("Error determining structure.", e);
      }
   }

   /**
    * Is the current context already part of metadata path.
    *
    * @param context the current structure context
    * @return true if already part of parent's context metadata path
    */
   protected boolean isMetadataPath(StructureContext context)
   {
      StructureContext parentContext = context.getParentContext();
      if (parentContext == null)
         return false;

      StructureMetaData smd = parentContext.getMetaData();
      ContextInfo info = smd.getContext("");
      List<String> metadataPaths = info.getMetaDataPath();
      if (metadataPaths != null && metadataPaths.isEmpty() == false)
      {
         String relativePath = AbstractStructureDeployer.getRelativePath(context.getParent(), context.getFile());
         for (String path : metadataPaths)
            if (relativePath.equalsIgnoreCase(path))
               return true;
      }

      return false;
   }
}
