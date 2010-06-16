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

import org.jboss.test.ws.JBossWSTest;
import org.jboss.ws.soap.NameImpl;
import org.jboss.ws.soap.SOAPContentElement;

import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

/**
 * Test the SOAPContentElement
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Oct-2004
 */
public class SOAPContentElementTestCase extends JBossWSTest
{

   /** Test that we can lazily create the SOAP tree
    */
   public void testChildNodeAccess() throws Exception
   {
      Name name = new NameImpl("Order", "tns", "http://someURI");

      String xmlFragment =
      "<tns:Order xmlns:tns='http://someURI'>" +
       "<OrderItem>Ferarri</OrderItem>" +
       "<OrderItem>Lamborgini</OrderItem>" +
       "<OrderItem>JBoss Support</OrderItem>" +
       "<Customer>" +
        "<Name>Thomas</Name>" +
       "</Customer>" +
      "</tns:Order>";

      SOAPContentElement soapEl = new SOAPContentElement(name);
      soapEl.setXMLFragment(xmlFragment);

      assertEquals(name, soapEl.getElementName());

      Iterator it = soapEl.getChildElements();
      SOAPElement child1 = (SOAPElement)it.next();
      assertEquals("OrderItem", child1.getNodeName());
      assertEquals("Ferarri", child1.getValue());

      SOAPElement child2 = (SOAPElement)it.next();
      assertEquals("OrderItem", child2.getNodeName());
      assertEquals("Lamborgini", child2.getValue());

      SOAPElement child3 = (SOAPElement)it.next();
      assertEquals("OrderItem", child3.getNodeName());
      assertEquals("JBoss Support", child3.getValue());

      SOAPElement child4 = (SOAPElement)it.next();
      assertEquals("Customer", child4.getNodeName());
      SOAPElement custName = (SOAPElement)child4.getChildElements().next();
      assertEquals("Name", custName.getNodeName());
      assertEquals("Thomas", custName.getValue());
   }

   /** Test that we can lazily load text content
    */
   public void testTextContent() throws Exception
   {
      Name name = new NameImpl("Order", "tns", "http://someURI");
      String xmlFragment = "<tns:Order xmlns:tns='http://someURI'>No child element</tns:Order>";

      SOAPContentElement soapEl = new SOAPContentElement(name);
      soapEl.setXMLFragment(xmlFragment);

      assertEquals(name, soapEl.getElementName());
      assertEquals("No child element", soapEl.getValue());
   }

   /** Test that we can lazily load empty elements
    */
   public void testEmptyElement() throws Exception
   {
      Name name = new NameImpl("Order", "tns", "http://someURI");
      String xmlFragment = "<tns:Order xmlns:tns='http://someURI'/>";

      SOAPContentElement soapEl = new SOAPContentElement(name);
      soapEl.setXMLFragment(xmlFragment);

      assertEquals(name, soapEl.getElementName());
      assertFalse(soapEl.hasChildNodes());
      assertNull(soapEl.getValue());
   }

   /** Test that we get an error on bad element names
    */
   public void testBadName() throws Exception
   {
      Name name = new NameImpl("Order", "tns", "http://someURI");
      String xmlFragment = "<tns:BadName xmlns:tns='http://someURI'>No child element</tns:BadName>";

      SOAPContentElement soapEl = new SOAPContentElement(name);
      soapEl.setXMLFragment(xmlFragment);

      try
      {
         assertEquals(name, soapEl.getElementName());
         assertEquals("No child element", soapEl.getValue());
         fail("Content root name does not match element name");
      }
      catch (Exception ignore)
      {
         // ignore;
      }
   }

   /** Test access to element attributes
    */
   public void testAttributeAccess() throws Exception
   {
      Name name = new NameImpl("Bar", "ns2", "http://org.jboss.ws/header2");
      String xmlFragment = "<ns2:Bar xmlns:ns2='http://org.jboss.ws/header2' foo='Kermit'>SomeOtherValue</ns2:Bar>";

      SOAPContentElement soapEl = new SOAPContentElement(name);
      soapEl.setXMLFragment(xmlFragment);
      assertEquals(name, soapEl.getElementName());

      assertEquals("Kermit", soapEl.getAttributeValue(new NameImpl("foo")));
      assertEquals("SomeOtherValue", soapEl.getValue());
   }

   /** Test access to text node
    */
   public void testTextNodeAccess() throws Exception
   {
      String envStr =
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
         "<env:Header/>" +
         "<env:Body>" +
         "<ns1:hello xmlns:ns1='http://handlerservice1.org/wsdl'>" +
         "<String_1>world</String_1>" +
         "</ns1:hello>" +
         "</env:Body>" +
         "</env:Envelope>";

      MessageFactory factory = MessageFactory.newInstance();
      SOAPMessage soapMessage = factory.createMessage(null, new ByteArrayInputStream(envStr.getBytes()));
      SOAPBody soapBody = soapMessage.getSOAPBody();

      SOAPElement elem = (SOAPElement)soapBody.getChildElements().next();
      SOAPElement arg = (SOAPElement)elem.getChildElements().next();
      Text text = (Text)arg.getChildElements().next();

      String value = text.getValue() + "::SOAP header was added";

      // Replace the text node
      text.detachNode();
      arg.addTextNode(value);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      soapMessage.writeTo(baos);

      String expEnv = "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'><env:Header/><env:Body><ns1:hello xmlns:ns1='http://handlerservice1.org/wsdl'><String_1>world::SOAP header was added</String_1></ns1:hello></env:Body></env:Envelope>";
      assertEquals(expEnv, baos.toString());
   }
}