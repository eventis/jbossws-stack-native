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

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.JAXWSAConstants;

import org.jboss.logging.Logger;
import org.jboss.ws.jaxrpc.Style;
import org.jboss.ws.metadata.EndpointMetaData;
import org.jboss.ws.metadata.OperationMetaData;

/**
 * Derive the operation meta data from incomming SOAP message
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 22-Nov-2005
 */
public class SOAPMessageDispatcher
{
   // provide logging
   private static Logger log = Logger.getLogger(SOAPMessageDispatcher.class);

   /** Get the operation meta data for a given SOAP message
    */
   public OperationMetaData getDispatchDestination(EndpointMetaData epMetaData, SOAPMessage soapMessage) throws SOAPException
   {
      OperationMetaData opMetaData = null;

      // Dispatch based on wsa:Action
      SOAPMessageContextImpl msgContext = MessageContextAssociation.peekMessageContext();
      AddressingProperties inProps = (AddressingProperties)msgContext.getProperty(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
      if (inProps != null && inProps.getAction() != null)
      {
         String wsaAction = inProps.getAction().getURI().toASCIIString();
         for (OperationMetaData opAux : epMetaData.getOperations())
         {
            if (wsaAction.equals(opAux.getSOAPAction()))
            {
               opMetaData = opAux;
               log.debug("Use wsa:Action dispatch: " + wsaAction);
               break;
            }
         }
      }
      // Dispatch based on SOAPBodyElement name
      if (opMetaData == null)
      {
         SOAPBody soapBody = soapMessage.getSOAPBody();

         Iterator bodyChildren = soapBody.getChildElements();
         if (bodyChildren.hasNext() == false)
         {
            if (epMetaData.getStyle() == Style.RPC)
               throw new SOAPException("Empty SOAP body with no child element not supported for RPC");
               
            // [JBWS-1125] Support empty soap body elements
            for (OperationMetaData opAux : epMetaData.getOperations())
            {
               if (opAux.getParameters().size() == 0)
               {
                  log.debug ("Dispatching empty SOAP body");
                  opMetaData = opAux;
                  break;
               }
            }
         }
         else
         {
            SOAPBodyElement soapBodyElement = (SOAPBodyElement)bodyChildren.next();
            if (bodyChildren.hasNext())
               throw new SOAPException("SOAPBody has more than on child element");

            Name soapName = soapBodyElement.getElementName();
            QName xmlElementName = new QName(soapName.getURI(), soapName.getLocalName());
            opMetaData = epMetaData.getOperation(xmlElementName);
         }
      }

      // Dispatch to a generic operation that takes an org.w3c.dom.Element
      if (opMetaData == null)
      {
         for (OperationMetaData opAux : epMetaData.getOperations())
         {
            if (opAux.isMessageEndpoint())
            {
               log.debug("Use generic message style dispatch");
               opMetaData = opAux;
               break;
            }
         }
      }

      log.debug("getDispatchDestination: " + (opMetaData != null ? opMetaData.getXmlName() : null));
      return opMetaData;
   }
}