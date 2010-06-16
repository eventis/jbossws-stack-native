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
package org.jboss.ws.server;

import java.util.List;

import javax.management.ObjectName;
import javax.xml.rpc.handler.HandlerInfo;

import org.jboss.ws.utils.ObjectNameFactory;

/**
 * MBean interface.
 * @since 15-April-2004
 */
public interface ServiceEndpointManagerMBean
{
   // default object name
   static final ObjectName OBJECT_NAME = ObjectNameFactory.create("jboss.ws:service=ServiceEndpointManager");
   
   String getWebServiceHost();
   void setWebServiceHost(String host);

   int getWebServicePort();
   void setWebServicePort(int port);

   int getWebServiceSecurePort();
   void setWebServiceSecurePort(int port);

   boolean isAlwaysModifySOAPAddress();
   void setAlwaysModifySOAPAddress(boolean modify);

   List<HandlerInfo> getHandlerInfos(ObjectName sepID);
   void setHandlerInfos(ObjectName sepID, List<HandlerInfo> infos);

   void startServiceEndpoint(ObjectName sepID) throws Exception;
   void stopServiceEndpoint(ObjectName sepID) throws Exception;
}