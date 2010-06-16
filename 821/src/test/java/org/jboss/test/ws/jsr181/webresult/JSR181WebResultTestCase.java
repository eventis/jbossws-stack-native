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
package org.jboss.test.ws.jsr181.webresult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import junit.framework.Test;

import org.jboss.test.ws.JBossWSTest;
import org.jboss.test.ws.JBossWSTestSetup;
import org.jboss.ws.jaxrpc.CallImpl;
import org.jboss.ws.jaxrpc.ServiceFactoryImpl;
import org.jboss.ws.metadata.OperationMetaData;
import org.jboss.ws.soap.NameImpl;

/**
 * Test the JSR-181 annotation: javax.jws.webresult
 *
 * @author Thomas.Diesler@jboss.org
 * @since 07-Oct-2005
 */
public class JSR181WebResultTestCase extends JBossWSTest
{
   private String endpointURL = "http://" + getServerHost() + ":8080/jbossws-jsr181-webresult/TestService";
   private String targetNS = "http://webresult.jsr181.ws.test.jboss.org/jaws";

   public static Test suite()
   {
      return JBossWSTestSetup.newTestSetup(JSR181WebResultTestCase.class, "jbossws-jsr181-webresult.war");
   }

   public void testLocateCustomer() throws Exception
   {
      QName serviceName = new QName(targetNS, "CustomerServiceService");
      QName portName = new QName(targetNS, "CustomerServicePort");
      URL wsdlURL = new URL(endpointURL + "?wsdl");

      File mappingFile = new File("resources/jsr181/webresult/jaxrpc-mapping.xml");
      assertTrue(mappingFile.exists());

      ServiceFactoryImpl factory = new ServiceFactoryImpl();
      Service service = factory.createService(wsdlURL, serviceName, mappingFile.toURL());
      CallImpl call = (CallImpl)service.createCall(portName, "locateCustomer");

      USAddress addr = new USAddress("Wall Street");
      CustomerRecord retObj = (CustomerRecord)call.invoke(new Object[]{"Mickey", "Mouse", addr});
      assertEquals("Mickey", retObj.getFirstName());
      assertEquals("Mouse", retObj.getLastName());
      assertEquals("Wall Street", retObj.getAddress().getAddress());
   }

   public void testMessageAccess() throws Exception
   {
      MessageFactory msgFactory = MessageFactory.newInstance();
      SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();

      String reqEnv =
      "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
      " <env:Header/>" +
      " <env:Body>" +
      "  <ns1:locateCustomer xmlns:ns1='" + targetNS + "'>" +
      "   <FirstName>Mickey</FirstName>" +
      "   <LastName>Mouse</LastName>" +
      "   <Address>" +
      "     <ns1:address>Wall Street</ns1:address>" +
      "   </Address>" +
      "  </ns1:locateCustomer>" +
      " </env:Body>" +
      "</env:Envelope>";
      SOAPMessage reqMsg = msgFactory.createMessage(null, new ByteArrayInputStream(reqEnv.getBytes()));

      URL epURL = new URL(endpointURL);
      SOAPMessage resMsg = con.call(reqMsg, epURL);

      NameImpl name = new NameImpl(new QName(targetNS, "locateCustomerResponse"));
      SOAPElement soapElement = (SOAPElement)resMsg.getSOAPBody().getChildElements(name).next();
      soapElement = (SOAPElement)soapElement.getChildElements(new NameImpl("CustomerRecord")).next();
      assertNotNull("Expected CustomerRecord", soapElement);
   }
}