/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.ws.metadata.umdm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.jboss.logging.Logger;
import org.jboss.ws.api.util.BundleUtils;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.wsf.spi.deployment.UnifiedVirtualFile;

/**
 * The top level meta data.
 *
 * <h4>Thread safety</h4>
 * <p>A <code>UnifiedMetaData</code> instance may be shared accross threads provided that the following conditions are met:
 * <ol>
 * <li>{@link #eagerInitialize() eagerInitialize()} is called from a single thread on startup</li>
 * <li>Multi-thread access is limited to read-only calls</li>
 * </ol></p>
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @since 12-May-2005
 */
public class UnifiedMetaData implements InitalizableMetaData
{
   private static final ResourceBundle bundle = BundleUtils.getBundle(UnifiedMetaData.class);
   // provide logging
   private static Logger log = Logger.getLogger(UnifiedMetaData.class);

   // The canonical deployment name
   private String deploymentName;
   // The modules class loader
   private ClassLoader classLoader;
   // The resource loader
   private UnifiedVirtualFile vfsRoot;
   // The optional security domain
   private String securityDomain;
   // The implementation version
   private static String implementationVersion;
   // True if this is a final release
   private static boolean isFinalRelease;
   // Map<String, WSDLDefinitions> the wsdl-file to the wsdl Document
   // Note the same wsdl can be used in multiple webservice descriptions
   Map<String, WSDLDefinitions> wsdlMap = new HashMap<String, WSDLDefinitions>();
   // Maps the jaxrpc-mapping-file to {@link JavaWsdlMapping} object
   // Note the same jaxrpc-mapping.xml can be used in multiple webservice descriptions
   Map<String, JavaWsdlMapping> jaxrpcMap = new HashMap<String, JavaWsdlMapping>();
   // The list of service meta data
   private List<ServiceMetaData> services = new ArrayList<ServiceMetaData>();
   // Used by eager initialization
   private boolean eagerInitialized;
   // Used by validate
   private boolean validated;

   public UnifiedMetaData(UnifiedVirtualFile vfsRoot)
   {
      this(vfsRoot, SecurityActions.getContextClassLoader());
   }

   public UnifiedMetaData(UnifiedVirtualFile vfsRoot, ClassLoader classLoader)
   {
      if (vfsRoot == null)
         throw new IllegalArgumentException(BundleUtils.getMessage(bundle, "VFS_ROOT_CANNOT_BE_NULL"));

      this.vfsRoot = vfsRoot;
      this.classLoader = classLoader;
   }

   public ClassLoader getClassLoader()
   {
      if (classLoader == null)
         throw new IllegalStateException(BundleUtils.getMessage(bundle, "CLASS_LOADER_NOT_AVAILABLE"));

      return classLoader;
   }

   public void setRootFile(UnifiedVirtualFile vfsRoot)
   {
      this.vfsRoot = vfsRoot;
   }

   public UnifiedVirtualFile getRootFile()
   {
      return vfsRoot;
   }

   public void setClassLoader(ClassLoader classLoader)
   {
      this.classLoader = classLoader;
   }

   public String getDeploymentName()
   {
      return deploymentName;
   }

   public void setDeploymentName(String deploymentName)
   {
      this.deploymentName = deploymentName;
   }

   public String getSecurityDomain()
   {
      return securityDomain;
   }

   public void setSecurityDomain(String domain)
   {
      String prefix = "java:/jaas/";
      if (domain != null && domain.startsWith(prefix))
         domain = domain.substring(prefix.length());

      this.securityDomain = domain;
   }

   public List<ServiceMetaData> getServices()
   {
      return new ArrayList<ServiceMetaData>(services);
   }

   public void addService(ServiceMetaData serviceMetaData)
   {
      services.add(serviceMetaData);
   }

   public void addWsdlDefinition(String wsdlFile, WSDLDefinitions wsdlDefinitions)
   {
      wsdlMap.put(wsdlFile, wsdlDefinitions);
   }

   public WSDLDefinitions getWsdlDefinition(String wsdlFile)
   {
      return wsdlMap.get(wsdlFile);
   }

   public void addMappingDefinition(String jaxrpcFile, JavaWsdlMapping javaWsdlMapping)
   {
      jaxrpcMap.put(jaxrpcFile, javaWsdlMapping);
   }

   public JavaWsdlMapping getMappingDefinition(String jaxrpcFile)
   {
      return jaxrpcMap.get(jaxrpcFile);
   }

   public void validate()
   {
      if (validated == false)
      {
         for (ServiceMetaData service : services)
         {
            service.validate();
         }
         validated = true;
      }
   }

   public boolean isEagerInitialized()
   {
      return eagerInitialized;
   }

   /**
    * Eagerly initialize all cache values that are normally lazy-loaded. This
    * allows for concurrent read-only access to a <code>UnifiedMetaData</code>
    * instance. This method, however, must be called from a single thread.
    */
   public synchronized void eagerInitialize()
   {
      if (eagerInitialized == false)
      {
         log.debug("Eagerly initialize the meta data model");
         for (ServiceMetaData service : services)
         {
            service.eagerInitialize();
         }
         eagerInitialized = true;
      }
   }

   public static String getImplementationVersion()
   {
      if (implementationVersion == null)
      {
         implementationVersion = UnifiedMetaData.class.getPackage().getImplementationVersion();
         if (implementationVersion != null)
            isFinalRelease = new StringTokenizer(implementationVersion).nextToken().endsWith(".GA");
      }
      return implementationVersion;
   }

   public static boolean isFinalRelease()
   {
      getImplementationVersion();
      return isFinalRelease;
   }

   public String toString()
   {
      StringBuilder buffer = new StringBuilder("\nUnifiedMetaData: ");
      buffer.append("\n implementation: " + getImplementationVersion());
      buffer.append("\n deploymentName: " + getDeploymentName());
      buffer.append("\n securityDomain: " + getSecurityDomain());
      //buffer.append("\n resourceLoader: " + resourceLoader);
      //buffer.append("\n classLoader: " + classLoader);
      buffer.append("\n");

      for (ServiceMetaData serviceMetaData : services)
      {
         buffer.append(serviceMetaData);
      }
      return buffer.toString();
   }
}
