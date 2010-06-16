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
package org.jboss.test.ws.soap;

import java.net.URL;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import junit.framework.Test;

import org.jboss.test.ws.JBossWSTest;
import org.jboss.test.ws.JBossWSTestSetup;
import org.jboss.ws.soap.SOAPBodyElementRpc;

/** Test call on a SOAPConnection
 *
 * @author Jason T. Greene
 * @author Thomas.Diesler@jboss.org
 */
public class SOAPConnectionTestCase extends JBossWSTest
{
   private final String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jbossws-samples-jsr109pojo-rpc";
   private static final String TARGET_NAMESPACE = "http://org.jboss.ws/samples/jsr109pojo";

   public static Test suite()
   {
      return JBossWSTestSetup.newTestSetup(SOAPConnectionTestCase.class, "jbossws-samples-jsr109pojo-rpc.war");
   }

   public void testConnectString() throws Exception
   {
      doConnect(TARGET_ENDPOINT_ADDRESS);
   }

   public void testConnectURL() throws Exception
   {
      doConnect(new URL(TARGET_ENDPOINT_ADDRESS));
   }

   private void doConnect(Object endPoint) throws Exception
   {
      SOAPMessage request = buildMessage();
      SOAPConnection connection = SOAPConnectionFactory.newInstance().createConnection();
      SOAPMessage response = connection.call(request, endPoint);
      validateResponse(response);
   }

   private SOAPMessage buildMessage() throws Exception
   {
      SOAPMessage message = MessageFactory.newInstance().createMessage();
      SOAPPart sp = message.getSOAPPart();
      SOAPEnvelope envelope = sp.getEnvelope();
      SOAPBody bdy = envelope.getBody();

      SOAPElement sbe = bdy.addChildElement(new SOAPBodyElementRpc(envelope.createName("echoString", "ns1", TARGET_NAMESPACE)));
      sbe.addChildElement(envelope.createName("String_1")).addTextNode("Hello");
      sbe.addChildElement(envelope.createName("String_2")).addTextNode("world!");

      return message;
   }

   private void validateResponse(SOAPMessage response) throws Exception
   {
      SOAPBody body = response.getSOAPBody();
      SOAPEnvelope env = response.getSOAPPart().getEnvelope();

      Name rpcName = env.createName("echoStringResponse", "ns1", TARGET_NAMESPACE);
      Iterator childElements = body.getChildElements(rpcName);

      SOAPElement bodyChild = (SOAPElement)childElements.next();
      Name resName = env.createName("result");
      SOAPElement resElement = (SOAPElement)bodyChild.getChildElements(resName).next();
      String value = resElement.getValue();

      assertEquals("Helloworld!", value);
   }
}