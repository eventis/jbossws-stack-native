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

// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package org.jboss.test.ws.jaxrpc.jbws663;

public interface SMSTextMessagingSoapBare extends java.rmi.Remote {
    public org.jboss.test.ws.jaxrpc.jbws663.SendMessageResponse sendMessage(org.jboss.test.ws.jaxrpc.jbws663.SendMessage parameters) throws 
         java.rmi.RemoteException;
    public org.jboss.test.ws.jaxrpc.jbws663.SendMessagesBulkResponse sendMessagesBulk(org.jboss.test.ws.jaxrpc.jbws663.SendMessagesBulk parameters) throws 
         java.rmi.RemoteException;
    public org.jboss.test.ws.jaxrpc.jbws663.TrackMessageResponse trackMessage(org.jboss.test.ws.jaxrpc.jbws663.TrackMessage parameters) throws 
         java.rmi.RemoteException;
    public org.jboss.test.ws.jaxrpc.jbws663.TrackMessagesBulkResponse trackMessagesBulk(org.jboss.test.ws.jaxrpc.jbws663.TrackMessagesBulk parameters) throws 
         java.rmi.RemoteException;
    public org.jboss.test.ws.jaxrpc.jbws663.GetSupportedCarriersResponse getSupportedCarriers(org.jboss.test.ws.jaxrpc.jbws663.GetSupportedCarriers parameters) throws 
         java.rmi.RemoteException;
    public org.jboss.test.ws.jaxrpc.jbws663.GetCountryCodesResponse getCountryCodes(org.jboss.test.ws.jaxrpc.jbws663.GetCountryCodes parameters) throws 
         java.rmi.RemoteException;
    public org.jboss.test.ws.jaxrpc.jbws663.GetRemainingHitsResponse getRemainingHits(org.jboss.test.ws.jaxrpc.jbws663.GetRemainingHits parameters) throws 
         java.rmi.RemoteException;
}
