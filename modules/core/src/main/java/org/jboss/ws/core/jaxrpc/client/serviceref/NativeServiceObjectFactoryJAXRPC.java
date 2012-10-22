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
package org.jboss.ws.core.jaxrpc.client.serviceref;

import static org.jboss.ws.NativeMessages.MESSAGES;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.xml.namespace.QName;
import javax.xml.rpc.Service;

import org.jboss.logging.Logger;
import org.jboss.ws.NativeLoggers;
import org.jboss.ws.common.Constants;
import org.jboss.ws.core.jaxrpc.client.ServiceExt;
import org.jboss.ws.core.jaxrpc.client.ServiceImpl;
import org.jboss.ws.core.jaxrpc.client.ServiceProxy;
import org.jboss.ws.core.server.PortComponentResolver;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMappingFactory;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.wsf.spi.SPIProvider;
import org.jboss.wsf.spi.SPIProviderResolver;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.management.EndpointRegistry;
import org.jboss.wsf.spi.management.EndpointRegistryFactory;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedCallPropertyMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedPortComponentRefMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedServiceRefMetaData;

/**
 * This ServiceObjectFactory reconstructs a javax.xml.rpc.Service
 * for a given WSDL when the webservice client does a JNDI lookup
 * <p/>
 * It uses the information provided by the service-ref element in application-client.xml
 *
 * @author Thomas.Diesler@jboss.org
 * @since 15-April-2004
 */
public final class NativeServiceObjectFactoryJAXRPC implements ObjectFactory
{
   // provide logging
   private static final Logger log = Logger.getLogger(NativeServiceObjectFactoryJAXRPC.class);

   /**
    * Creates an object using the location or reference information specified.
    * <p/>
    *
    * @param obj         The possibly null object containing location or reference
    *                    information that can be used in creating an object.
    * @param name        The name of this object relative to <code>nameCtx</code>,
    *                    or null if no name is specified.
    * @param nameCtx     The context relative to which the <code>name</code>
    *                    parameter is specified, or null if <code>name</code> is
    *                    relative to the default initial context.
    * @param environment The possibly null environment that is used in
    *                    creating the object.
    * @return The object created; null if an object cannot be created.
    * @throws Exception if this object factory encountered an exception
    *                   while attempting to create an object, and no other object factories are
    *                   to be tried.
    * @see javax.naming.spi.NamingManager#getObjectInstance
    * @see javax.naming.spi.NamingManager#getURLContext
    */
   public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception
   {
      try
      {
         Reference ref = (Reference)obj;

         // Unmarshall the ServiceRefMetaData
         UnifiedServiceRefMetaData serviceRef = null;
         RefAddr metaRefAddr = ref.get(NativeServiceReferenceableJAXRPC.SERVICE_REF_META_DATA);
         ByteArrayInputStream bais = new ByteArrayInputStream((byte[])metaRefAddr.getContent());
         try
         {
            ObjectInputStream ois = new ObjectInputStream(bais);
            serviceRef = (UnifiedServiceRefMetaData)ois.readObject();
            ois.close();
         }
         catch (IOException ex)
         {
            NamingException ne = new NamingException();
            ne.setRootCause(ex);
            throw ne;
         }

         ServiceImpl jaxrpcService = null;
         URL wsdlLocation = serviceRef.getWsdlLocation();
         if (wsdlLocation != null)
         {
            if (log.isDebugEnabled())
               log.debug("Create jaxrpc service from wsdl");

            // Create the actual service object
            QName serviceName = serviceRef.getServiceQName();
            JavaWsdlMapping javaWsdlMapping = getJavaWsdlMapping(serviceRef);
            jaxrpcService = new ServiceImpl(serviceName, wsdlLocation, javaWsdlMapping, serviceRef);
         }
         else
         {
            if (log.isDebugEnabled())
               log.debug("Create jaxrpc service with no wsdl");
            jaxrpcService = new ServiceImpl(new QName(Constants.NS_JBOSSWS_URI, "AnonymousService"));
         }

         ServiceMetaData serviceMetaData = jaxrpcService.getServiceMetaData();

         // Set any service level properties
         if (serviceRef.getCallProperties().size() > 0)
         {
            Properties callProps = new Properties();
            serviceMetaData.setProperties(callProps);
            for (UnifiedCallPropertyMetaData prop : serviceRef.getCallProperties())
               callProps.setProperty(prop.getPropName(), prop.getPropValue());
         }

         // The web service client using a port-component-link, the contet is the URL to
         // the PortComponentLinkServlet that will return the actual endpoint address
         RefAddr pcLinkRef = ref.get(NativeServiceReferenceableJAXRPC.PORT_COMPONENT_LINK);
         if (pcLinkRef != null)
         {
            String pcLink = (String)pcLinkRef.getContent();
            log.debug("Resolving port-component-link: " + pcLink);

            // First try to obtain the endpoint address loacally
            String endpointAddress = null;
            try
            {
               SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
               EndpointRegistry epRegistry = spiProvider.getSPI(EndpointRegistryFactory.class).getEndpointRegistry();
               Endpoint endpoint = epRegistry.resolve( new PortComponentResolver(pcLink) );
               if (endpoint == null)
                  throw MESSAGES.cannotResolvePortComponentLink(pcLink);

               ServerEndpointMetaData sepMetaData = endpoint.getAttachment(ServerEndpointMetaData.class);
               endpointAddress = sepMetaData.getEndpointAddress();
            }
            catch (Throwable ex)
            {
               // ignore, we are probably a remote client
            }

            // We may be remote in the esoteric case where an appclient uses the port-comonent-link feature
            if (endpointAddress == null)
            {
               String servletPath = (String)ref.get(NativeServiceReferenceableJAXRPC.PORT_COMPONENT_LINK_SERVLET).getContent();
               servletPath += "?pcLink=" + URLEncoder.encode(pcLink, "UTF-8");
               InputStream is = new URL(servletPath).openStream();
               BufferedReader br = new BufferedReader(new InputStreamReader(is));
               endpointAddress = br.readLine();
               br.close();
               is.close();
            }

            if (log.isDebugEnabled())
               log.debug("Resolved to: " + endpointAddress);
            if (serviceMetaData.getEndpoints().size() == 1)
            {
               EndpointMetaData epMetaData = serviceMetaData.getEndpoints().get(0);
               epMetaData.setEndpointAddress(endpointAddress);
            }
            else
            {
               NativeLoggers.JAXRPC_LOGGER.cannotSetEndpointAddressForPCL();
            }
         }

         narrowPortSelection(serviceRef, serviceMetaData);

         /********************************************************
          * Setup the Proxy that implements the service-interface
          ********************************************************/

         // load the service interface class
         ClassLoader contextCL = SecurityActions.getContextClassLoader();
         Class<?> siClass = SecurityActions.loadClass(contextCL, serviceRef.getServiceInterface());
         if (Service.class.isAssignableFrom(siClass) == false)
            throw MESSAGES.interfaceDoesNotImplementJAXRPCService(siClass.getName());

         // load all service endpoint interface classes
         for (UnifiedPortComponentRefMetaData pcr : serviceRef.getPortComponentRefs())
         {
            String seiName = pcr.getServiceEndpointInterface();
            if (seiName != null)
            {
               Class<?> seiClass = contextCL.loadClass(seiName);
               if (Remote.class.isAssignableFrom(seiClass) == false)
                  throw new IllegalArgumentException(MESSAGES.notImplementRemote(seiClass.getName()));
            }
         }

         // Setup the handler chain
         setupHandlerChain(jaxrpcService);

         InvocationHandler handler = new ServiceProxy(jaxrpcService, siClass);
         return Proxy.newProxyInstance(contextCL, new Class[] { siClass, ServiceExt.class }, handler);
      }
      catch (Exception ex)
      {
         NativeLoggers.JAXRPC_LOGGER.cannotCreateService(ex);
         throw ex;
      }
   }

   /**
    * Narrow available endpoints by <port-component-ref> declarations. 
    * Service.getPort(SEI) must be able to retrieve a distinct port definition.
    */
   protected void narrowPortSelection(UnifiedServiceRefMetaData serviceRef, ServiceMetaData serviceMetaData)
   {
      if (serviceMetaData.getEndpoints().size() > 1)
      {
         Map<String, UnifiedPortComponentRefMetaData> pcrefs = new HashMap<String, UnifiedPortComponentRefMetaData>();
         for (UnifiedPortComponentRefMetaData pcref : serviceRef.getPortComponentRefs())
         {
            String seiName = pcref.getServiceEndpointInterface();

            // Constraint#1: within a service-ref it's not allowed to use a SEI across different pcref's
            if (pcrefs.get(seiName) != null)
               throw MESSAGES.notAllowedToUseInServiceRef(seiName);
            
            pcrefs.put(seiName, pcref);
         }

         // Constraint#2: A pcref may only match one EndpointMetaData
         for (String sei : pcrefs.keySet())
         {
            // Narrow available endpoints by port-component-ref declaration
            List<QName> narrowedEndpoints = new ArrayList<QName>();

            UnifiedPortComponentRefMetaData pcref = pcrefs.get(sei);

            // Constraint#3: Port selection only applies when both SEI and QName are given
            if (pcref.getServiceEndpointInterface() != null && pcref.getPortQName() != null)
            {
               List<QName> pcRef2EndpointMapping = new ArrayList<QName>();
               for (EndpointMetaData epMetaData : serviceMetaData.getEndpoints())
               {
                  if (pcref.getServiceEndpointInterface().equals(epMetaData.getServiceEndpointInterfaceName()))
                  {
                     pcRef2EndpointMapping.add(epMetaData.getPortName());
                  }
               }

               for (QName q : pcRef2EndpointMapping)
               {
                  EndpointMetaData mappedEndpoint = serviceMetaData.getEndpoint(q);
                  if (!pcref.getPortQName().equals(mappedEndpoint.getPortName()))
                     narrowedEndpoints.add(q);
               }

               // Constraint: Dont exclude all of them ;)
               if (pcRef2EndpointMapping.size() > 0 && (pcRef2EndpointMapping.size() == narrowedEndpoints.size()))
                  throw MESSAGES.failedToNarrowavailableEpsByPcRef();

               for (QName q : narrowedEndpoints)
               {
                  EndpointMetaData removed = serviceMetaData.removeEndpoint(q);
                  if (log.isDebugEnabled())
                     log.debug("Narrowed endpoint " + q + "(" + removed + ")");
               }
            }
            else
            {
               // TODO: In case there is more then one EMPD this should cause an exception
               NativeLoggers.JAXRPC_LOGGER.unableToNarrowPortSelection(pcref);
            }
         }
      }
   }

   /**
    * Setup the handler chain(s) for this service
    */
   private void setupHandlerChain(ServiceImpl jaxrpcService) throws Exception
   {
      List<EndpointMetaData> endpoints = jaxrpcService.getServiceMetaData().getEndpoints();
      for (EndpointMetaData epMetaData : endpoints)
      {
         jaxrpcService.setupHandlerChain(epMetaData);
      }
   }

   private JavaWsdlMapping getJavaWsdlMapping(UnifiedServiceRefMetaData serviceRef)
   {
      JavaWsdlMapping javaWsdlMapping = null;
      if (serviceRef.getMappingFile() != null)
      {
         String mappingFile = serviceRef.getMappingFile();
         try
         {
            JavaWsdlMappingFactory mappingFactory = JavaWsdlMappingFactory.newInstance();
            URL mappingURL = serviceRef.getVfsRoot().findChild(mappingFile).toURL();
            javaWsdlMapping = mappingFactory.parse(mappingURL);
         }
         catch (Exception e)
         {
            throw MESSAGES.cannotUnmarshallJAXRPCMapping(mappingFile, e);
         }
      }
      return javaWsdlMapping;
   }
}