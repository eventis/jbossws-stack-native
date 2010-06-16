/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
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
// $Id$
package org.jboss.ws.jaxrpc;

// $Id$

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLEncoder;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.Service;
import javax.xml.rpc.handler.HandlerInfo;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.EndpointMetaData;
import org.jboss.ws.metadata.ServiceMetaData;
import org.jboss.ws.metadata.j2ee.UnifiedHandlerMetaData;
import org.jboss.ws.metadata.j2ee.UnifiedInitParamMetaData;
import org.jboss.ws.metadata.j2ee.UnifiedPortComponentRefMetaData;
import org.jboss.ws.metadata.j2ee.UnifiedServiceRefMetaData;
import org.jboss.ws.metadata.j2ee.UnifiedHandlerMetaData.HandlerType;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.wsse.WSSecurityConfiguration;
import org.jboss.ws.server.ServiceEndpoint;
import org.jboss.ws.server.ServiceEndpointManager;
import org.jboss.ws.server.ServiceEndpointManagerFactory;

/**
 * This ServiceObjectFactory reconstructs a javax.xml.rpc.Service
 * for a given WSDL when the webservice client does a JNDI lookup
 * <p/>
 * It uses the information provided by the service-ref element in application-client.xml
 *
 * @author Thomas.Diesler@jboss.org
 * @since 15-April-2004
 */
public class ServiceObjectFactory implements ObjectFactory
{
   // provide logging
   private static final Logger log = Logger.getLogger(ServiceObjectFactory.class);

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
         UnifiedServiceRefMetaData serviceRefMetaData = null;
         RefAddr metaRefAddr = ref.get(ServiceReferenceable.SERVICE_REF_META_DATA);
         ByteArrayInputStream bais = new ByteArrayInputStream((byte[])metaRefAddr.getContent());
         try
         {
            ObjectInputStream ois = new ObjectInputStream(bais);
            serviceRefMetaData = (UnifiedServiceRefMetaData)ois.readObject();
            ois.close();
         }
         catch (IOException e)
         {
            throw new NamingException("Cannot unmarshall service ref meta data, cause: " + e.toString());
         }

         // reconstruct the resourceCl
         ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
         URL url = new URL((String)ref.get(ServiceReferenceable.DEPLOYMENT_URL).getContent());
         URLClassLoader resourceCL = new URLClassLoader(new URL[] { url }, contextCL);
         serviceRefMetaData.setResourceCL(resourceCL);

         // Rewrite the config-file content
         String configFile = serviceRefMetaData.getConfigFile();
         if (configFile != null)
         {
            try
            {
               new URL(configFile);
            }
            catch (MalformedURLException ex)
            {
               URL configURL = resourceCL.getResource(configFile);
               if (configURL != null)
               {
                  configFile = configURL.toExternalForm();
                  serviceRefMetaData.setConfigFile(configFile);
               }
            }
         }

         // Unmarshall the WSSecurityConfiguration
         WSSecurityConfiguration securityConfig = null;
         RefAddr wsseRefAddr = ref.get(ServiceReferenceable.SECURITY_CONFIG);
         if (wsseRefAddr != null)
         {
            bais = new ByteArrayInputStream((byte[])wsseRefAddr.getContent());
            try
            {
               ObjectInputStream ois = new ObjectInputStream(bais);
               securityConfig = (WSSecurityConfiguration)ois.readObject();
               ois.close();
            }
            catch (IOException e)
            {
               throw new NamingException("Cannot unmarshall security config, cause: " + e.toString());
            }
         }

         ServiceImpl jaxrpcService = null;
         URL wsdlURL = serviceRefMetaData.getWsdlURL();
         if (wsdlURL != null)
         {
            log.debug("Create jaxrpc service from wsdl");

            // Create the actual service object
            QName serviceName = serviceRefMetaData.getServiceQName();
            JavaWsdlMapping javaWsdlMapping = (JavaWsdlMapping)serviceRefMetaData.getJavaWsdlMapping();
            jaxrpcService = new ServiceImpl(serviceName, wsdlURL, javaWsdlMapping, securityConfig, serviceRefMetaData);

            // Setup endpoint meta data
            ServiceMetaData serviceMetaData = jaxrpcService.getServiceMetaData();
            for (UnifiedPortComponentRefMetaData pcRefMetaData : serviceRefMetaData.getPortComponentRefs())
            {
               String seiName = pcRefMetaData.getServiceEndpointInterface();
               for (EndpointMetaData epMetaData : serviceMetaData.getEndpoints())
               {
                  String epSEIName = epMetaData.getServiceEndpointInterfaceName();
                  if (seiName.equals(epSEIName) || epSEIName == null)
                  {
                     epMetaData.setServiceEndpointInterfaceName(seiName);
                     epMetaData.setProperties(pcRefMetaData.getCallProperties());
                  }
               }
            }
         }
         else
         {
            log.debug("Create jaxrpc service with no wsdl");
            jaxrpcService = new ServiceImpl(new QName(Constants.NS_JBOSSWS_URI, "AnonymousService"));
         }

         // Set any service level properties
         ServiceMetaData serviceMetaData = jaxrpcService.getServiceMetaData();
         serviceMetaData.setProperties(serviceRefMetaData.getCallProperties());

         // The web service client using a port-component-link, the contet is the URL to
         // the PortComponentLinkServlet that will return the actual endpoint address
         RefAddr pcLinkRef = ref.get(ServiceReferenceable.PORT_COMPONENT_LINK);
         if (pcLinkRef != null)
         {
            String pcLink = (String)pcLinkRef.getContent();
            log.debug("Resolving port-component-link: " + pcLink);

            // First try to obtain the endpoint address loacally
            String endpointAddress = null;
            try
            {
               ServiceEndpointManagerFactory factory = ServiceEndpointManagerFactory.getInstance();
               ServiceEndpointManager epManager = factory.getServiceEndpointManager();
               ServiceEndpoint serviceEndpoint = epManager.resolvePortComponentLink(pcLink);
               if (serviceEndpoint == null)
                  throw new WSException("Cannot resolve port-component-link: " + pcLink);

               endpointAddress = serviceEndpoint.getServiceEndpointInfo().getServerEndpointMetaData().getEndpointAddress();
            }
            catch (Exception ex)
            {
               // ignore, we are probably a remote client
            }

            // We may be remote in the esoteric case where an appclient uses the port-comonent-link feature
            if (endpointAddress == null)
            {
               String servletPath = (String)ref.get(ServiceReferenceable.PORT_COMPONENT_LINK_SERVLET).getContent();
               servletPath += "?pcLink=" + URLEncoder.encode(pcLink, "UTF-8");
               InputStream is = new URL(servletPath).openStream();
               BufferedReader br = new BufferedReader(new InputStreamReader(is));
               endpointAddress = br.readLine();
               is.close();
            }

            log.debug("Resolved to: " + endpointAddress);
            if (serviceMetaData.getEndpoints().size() == 1)
            {
               EndpointMetaData epMetaData = serviceMetaData.getEndpoints().get(0);
               epMetaData.setEndpointAddress(endpointAddress);
            }
            else
            {
               log.warn("Cannot set endpoint address for port-component-link, unsuported number of endpoints");
            }
         }

         /********************************************************
          * Setup the Proxy that implements the service-interface
          ********************************************************/

         // load the service interface class
         Class siClass = contextCL.loadClass(serviceRefMetaData.getServiceInterface());
         if (Service.class.isAssignableFrom(siClass) == false)
            throw new JAXRPCException("The service interface does not implement javax.xml.rpc.Service: " + siClass.getName());

         // load all service endpoint interface classes
         UnifiedPortComponentRefMetaData[] pcrArray = serviceRefMetaData.getPortComponentRefs();
         for (int i = 0; i < pcrArray.length; i++)
         {
            UnifiedPortComponentRefMetaData pcr = pcrArray[i];
            Class seiClass = contextCL.loadClass(pcr.getServiceEndpointInterface());
            if (Remote.class.isAssignableFrom(seiClass) == false)
               throw new IllegalArgumentException("The SEI does not implement java.rmi.Remote: " + seiClass.getName());
         }

         // Setup the handler chain
         setupHandlerChain(jaxrpcService, serviceRefMetaData);

         InvocationHandler handler = new ServiceProxy(jaxrpcService, siClass);
         return Proxy.newProxyInstance(contextCL, new Class[] { siClass, ServiceExt.class }, handler);
      }
      catch (Exception ex)
      {
         log.error("Cannot create service", ex);
         throw ex;
      }
   }

   /**
    * Setup the handler chain(s) for this service
    * <p/>
    * This registers a handler chain with the service for every endpoint
    * that has handlers configured in the <service-ref> element
    */
   private void setupHandlerChain(ServiceImpl jaxrpcService, UnifiedServiceRefMetaData serviceRefMetaData) throws Exception
   {
      List<EndpointMetaData> endpoints = jaxrpcService.getServiceMetaData().getEndpoints();
      for (EndpointMetaData epMetaData : endpoints)
      {
         QName portName = epMetaData.getName();

         HashSet handlerRoles = new HashSet();
         ArrayList handlerInfos = new ArrayList();
         for (UnifiedHandlerMetaData hMetaData : epMetaData.getHandlers(HandlerType.ALL))
         {
            handlerRoles.addAll(Arrays.asList(hMetaData.getSoapRoles()));

            ClassLoader cl = epMetaData.getClassLoader();
            Class hClass = cl.loadClass(hMetaData.getHandlerClass());

            HashMap hConfig = new HashMap();
            UnifiedInitParamMetaData[] params = hMetaData.getInitParams();
            for (int k = 0; k < params.length; k++)
            {
               UnifiedInitParamMetaData param = params[k];
               hConfig.put(param.getParamName(), param.getParamValue());
            }

            QName[] hHeaders = hMetaData.getSoapHeaders();
            HandlerInfo info = new HandlerInfo(hClass, hConfig, hHeaders);

            log.debug("Adding client side handler to endpoint '" + portName + "': " + info);
            handlerInfos.add(info);

            // register the handlers with the client engine
            if (handlerInfos.size() > 0)
               jaxrpcService.registerHandlerChain(portName, handlerInfos, handlerRoles);
         }
      }
   }
}