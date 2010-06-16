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
package org.jboss.ws.soap;

// $Id$

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.rpc.Stub;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.addressing.EndpointReference;

import org.jboss.logging.Logger;
import org.jboss.remoting.Client;
import org.jboss.remoting.InvokerLocator;
import org.jboss.remoting.util.socket.RemotingSSLSocketFactory;
import org.jboss.ws.binding.soap.SOAPMessageMarshaller;
import org.jboss.ws.binding.soap.SOAPMessageUnMarshaller;
import org.jboss.ws.jaxrpc.StubExt;

/**
 * SOAPConnection implementation
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 *
 * @since 02-Feb-2005
 */
public class SOAPConnectionImpl extends SOAPConnection
{
   // provide logging
   private static Logger log = Logger.getLogger(SOAPConnectionImpl.class);
   private static Logger msgLog = Logger.getLogger("jbossws.SOAPMessage");

   private Map<String, Object> config = new HashMap<String, Object>();

   private static Map<String, String> metadataMap = new HashMap<String, String>();
   static
   {
      metadataMap.put(Stub.USERNAME_PROPERTY, "http.basic.username");
      metadataMap.put(Stub.PASSWORD_PROPERTY, "http.basic.password");
   }
   private static Map<String, String> configMap = new HashMap<String, String>();
   static
   {
      //  Remoting 2.0.0
      //  configMap.put(StubExt.PROPERTY_KEY_STORE, SSLSocketBuilder.REMOTING_KEY_STORE_FILE_PATH);
      configMap.put(StubExt.PROPERTY_KEY_STORE, RemotingSSLSocketFactory.REMOTING_KEY_STORE_FILE_PATH);
      configMap.put(StubExt.PROPERTY_KEY_STORE_PASSWORD, RemotingSSLSocketFactory.REMOTING_KEY_STORE_PASSWORD);
      configMap.put(StubExt.PROPERTY_KEY_STORE_TYPE, RemotingSSLSocketFactory.REMOTING_KEY_STORE_TYPE);
      configMap.put(StubExt.PROPERTY_TRUST_STORE, RemotingSSLSocketFactory.REMOTING_TRUST_STORE_FILE_PATH);
      configMap.put(StubExt.PROPERTY_TRUST_STORE_PASSWORD, RemotingSSLSocketFactory.REMOTING_TRUST_STORE_PASSWORD);
      configMap.put(StubExt.PROPERTY_TRUST_STORE_TYPE, RemotingSSLSocketFactory.REMOTING_TRUST_STORE_TYPE);
   }

   private boolean closed;

   public SOAPConnectionImpl()
   {
	      // HTTPClientInvoker conect sends gratuitous POST
	      // http://jira.jboss.com/jira/browse/JBWS-711
	      config.put(Client.ENABLE_LEASE, false);
   }

   /**
    * Sends the given message to the specified endpoint and blocks until it has
    * returned the response.
    */
   public SOAPMessage call(SOAPMessage reqMessage, Object endpoint) throws SOAPException
   {
      return call(reqMessage, endpoint, false);
   }

   /**
    * Sends the given message to the specified endpoint.
    */
   public SOAPMessage call(SOAPMessage reqMessage, Object endpoint, boolean oneway) throws SOAPException
   {
      if (reqMessage == null)
         throw new IllegalArgumentException("Given SOAPMessage cannot be null");
      if (endpoint == null)
         throw new IllegalArgumentException("Given endpoint cannot be null");

      if (closed)
         throw new SOAPException("SOAPConnection is already closed");

      InvokerLocator locator;
      Client remotingClient;
      String targetAddress;
      Map callProps;

      if (endpoint instanceof EndpointInfo)
      {
         EndpointInfo epInfo = (EndpointInfo)endpoint;
         targetAddress = epInfo.getTargetAddress();
         callProps = epInfo.getProperties();

         if (callProps.containsKey(StubExt.PROPERTY_CLIENT_TIMEOUT))
         {
            Object timeout = callProps.get(StubExt.PROPERTY_CLIENT_TIMEOUT);
            int qmIndex = targetAddress.indexOf("?");
            targetAddress += (qmIndex < 0 ? "?" : "&") + "timeout=" + timeout;
         }
      }
      else if (endpoint instanceof EndpointReference)
      {
         EndpointReference epr = (EndpointReference)endpoint;
         targetAddress = epr.getAddress().toString();
         callProps = null;
      }
      else
      {
         targetAddress = endpoint.toString();
         callProps = null;
      }

      try
      {
         // Get the invoker from Remoting for a given endpoint address
         log.debug("Get locator for: " + endpoint);
         locator = new InvokerLocator(targetAddress);
      }
      catch (MalformedURLException e)
      {
         throw new SOAPException("Malformed endpoint address", e);
      }

      Map metadata = getRemotingMetaData(reqMessage, targetAddress, callProps);

      try
      {
         remotingClient = new Client(locator, "saaj", config);
         // Remoting 2.0.0
         // remotingClient.connect();
         remotingClient.setMarshaller(new SOAPMessageMarshaller());
         remotingClient.setUnMarshaller(oneway == false ? new SOAPMessageUnMarshaller() : null);
      }
      catch (Exception e)
      {
         throw new SOAPException("Could not setup remoting client", e);
      }

      try
      {
         // debug the outgoing message
         if(msgLog.isDebugEnabled())
         {
            SOAPEnvelope soapReqEnv = reqMessage.getSOAPPart().getEnvelope();
            String envStr = SAAJElementWriter.printSOAPElement((SOAPElementImpl)soapReqEnv, true);
            msgLog.debug("Remoting meta data: " + metadata);
            msgLog.debug("Outgoing SOAPMessage\n" + envStr);
         }

         SOAPMessage resMessage = null;
         if (oneway == true)
         {
            remotingClient.invokeOneway(reqMessage, metadata, false);
         }
         else
         {
            resMessage = (SOAPMessage)remotingClient.invoke(reqMessage, metadata);
         }

         // debug the incomming response message
         if (resMessage != null && msgLog.isDebugEnabled())
         {
            SOAPEnvelope soapResEnv = resMessage.getSOAPPart().getEnvelope();
            String envStr = SAAJElementWriter.printSOAPElement((SOAPElementImpl)soapResEnv, true);
            msgLog.debug("Incomming Response SOAPMessage\n" + envStr);
         }

         return resMessage;
      }
      catch (Throwable t)
      {
         throw new SOAPException("Could not transmit message", t);
      }
   }

   /** Closes this SOAPConnection
    */
   public void close() throws SOAPException
   {
      if (closed)
         throw new SOAPException("SOAPConnection is already closed");

      closed = true;
   }

   private Map getRemotingMetaData(SOAPMessage reqMessage, String targetAddress, Map callProps) throws SOAPException
   {
      // R2744 A HTTP request MESSAGE MUST contain a SOAPAction HTTP header field
      // with a quoted value equal to the value of the soapAction attribute of
      // soapbind:operation, if present in the corresponding WSDL description.

      // R2745 A HTTP request MESSAGE MUST contain a SOAPAction HTTP header field
      // with a quoted empty string value, if in the corresponding WSDL description,
      // the soapAction attribute of soapbind:operation is either not present, or
      // present with an empty string as its value.
	   
	   // R1109 The value of the SOAPAction HTTP header field in a HTTP request MESSAGE MUST be a quoted string.
      MimeHeaders mimeHeaders = reqMessage.getMimeHeaders();
      String[] action = mimeHeaders.getHeader("SOAPAction");
      if (action != null && action.length > 0)
      {
         String soapAction = action[0];

         if (soapAction.startsWith("\"") == false || soapAction.endsWith("\"") == false)
            soapAction = "\"" + soapAction + "\"";

         mimeHeaders.setHeader("SOAPAction", soapAction);
      }
      else
      {
         mimeHeaders.setHeader("SOAPAction", "\"\"");
      }

      if (reqMessage.saveRequired())
         reqMessage.saveChanges();

      Map<String, Object> metadata = new HashMap<String, Object>();
      Properties props = new Properties();
      metadata.put("HEADER", props);

      Iterator i = mimeHeaders.getAllHeaders();
      while (i.hasNext())
      {
         MimeHeader header = (MimeHeader)i.next();
         String currentValue = props.getProperty(header.getName());

         /*
          * Coalesce multiple headers into one
          *
          * From HTTP/1.1 RFC 2616:
          *
          * Multiple message-header fields with the same field-name MAY be
          * present in a message if and only if the entire field-value for that
          * header field is defined as a comma-separated list [i.e., #(values)].
          * It MUST be possible to combine the multiple header fields into one
          * "field-name: field-value" pair, without changing the semantics of
          * the message, by appending each subsequent field-value to the first,
          * each separated by a comma.
          */
         if (currentValue != null)
         {
            props.put(header.getName(), currentValue + "," + header.getValue());
         }
         else
         {
            props.put(header.getName(), header.getValue());
         }
      }

      if (callProps != null)
      {
         Iterator it = callProps.entrySet().iterator();

         // Get authentication type, default to BASIC authetication
         String authType = (String)callProps.get(StubExt.PROPERTY_AUTH_TYPE);
         if (authType == null)
            authType = StubExt.PROPERTY_AUTH_TYPE_BASIC;

         while (it.hasNext())
         {
            Map.Entry entry = (Map.Entry)it.next();
            String key = (String)entry.getKey();
            Object val = entry.getValue();

            // pass properties to remoting meta data
            if (metadataMap.containsKey(key))
            {
               String remotingKey = metadataMap.get(key);
               if (key.equals(Stub.USERNAME_PROPERTY) || key.equals(Stub.PASSWORD_PROPERTY))
               {
                  if (authType.equals(StubExt.PROPERTY_AUTH_TYPE_BASIC))
                  {
                     metadata.put(remotingKey, val);
                  }
                  else
                  {
                     log.warn("Ignore '" + key + "' with auth typy: " + authType);
                  }
               }
               else
               {
                  metadata.put(remotingKey, val);
               }
            }
            
            // pass properties to remoting client config
            if (configMap.containsKey(key))
            {
               String remotingKey = configMap.get(key);
               config.put(remotingKey, val);
            }
         }
      }

      return metadata;
   }
}