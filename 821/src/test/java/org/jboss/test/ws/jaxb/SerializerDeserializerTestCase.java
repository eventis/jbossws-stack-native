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
package org.jboss.test.ws.jaxb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.TypeMappingRegistry;

import org.jboss.test.ws.JBossWSTest;
import org.jboss.ws.Constants;
import org.jboss.ws.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.jaxrpc.TypeMappingImpl;
import org.jboss.ws.jaxrpc.TypeMappingRegistryImpl;
import org.jboss.ws.jaxrpc.encoding.DeserializerFactoryBase;
import org.jboss.ws.jaxrpc.encoding.DeserializerSupport;
import org.jboss.ws.jaxrpc.encoding.SerializationContextImpl;
import org.jboss.ws.jaxrpc.encoding.SerializerFactoryBase;
import org.jboss.ws.jaxrpc.encoding.SerializerSupport;
import org.jboss.xb.binding.NamespaceRegistry;

/**
 * Test simple type serialization/deserialization
 *
 * [TODO] please provide additional tests as you add more types
 *
 * @author Thomas.Diesler@jboss.org
 * @since 18-Oct-2004
 */
public class SerializerDeserializerTestCase extends JBossWSTest
{
   private QName xmlName = new QName("http://org.jboss.ws", "root", "tns");

   private SerializationContextImpl serContext;
   private NamespaceRegistry nsRegistry;
   private TypeMappingImpl typeMapping;

   protected void setUp() throws Exception
   {
      TypeMappingRegistry tmRegistry = new TypeMappingRegistryImpl();
      typeMapping = (TypeMappingImpl)tmRegistry.getDefaultTypeMapping();

      serContext = new SerializationContextImpl();
      nsRegistry = new NamespaceRegistry();

      serContext.setTypeMapping(typeMapping);
      serContext.setNamespaceRegistry(nsRegistry);
   }

   public void testStringType() throws Exception
   {
      QName xmlType = Constants.TYPE_LITERAL_STRING;
      String value = "Hello World!";

      SerializerFactoryBase serializerFactory = (SerializerFactoryBase)typeMapping.getSerializer(String.class, xmlType);
      SerializerSupport ser = (SerializerSupport)serializerFactory.getSerializer();
      String xmlFragment = ser.serialize(xmlName, xmlType, value, serContext, null);
      assertNotNull(xmlFragment);

      DeserializerFactoryBase deserializerFactory = (DeserializerFactoryBase)typeMapping.getDeserializer(String.class, xmlType);
      DeserializerSupport des = (DeserializerSupport)deserializerFactory.getDeserializer();
      String out = (String)des.deserialize(xmlName, xmlType, xmlFragment, serContext);

      assertEquals(value, out);
   }

   public void testDateTime() throws Exception
   {
      QName xmlType = Constants.TYPE_LITERAL_DATETIME;
      Calendar value = new GregorianCalendar(2004, 10, 20, 14, 53, 25);

      SerializerFactoryBase serializerFactory = (SerializerFactoryBase)typeMapping.getSerializer(Calendar.class, xmlType);
      SerializerSupport ser = (SerializerSupport)serializerFactory.getSerializer();
      String xmlFragment = ser.serialize(xmlName, xmlType, value, serContext, null);
      assertNotNull(xmlFragment);

      DeserializerFactoryBase deserializerFactory = (DeserializerFactoryBase)typeMapping.getDeserializer(Calendar.class, xmlType);
      DeserializerSupport des = (DeserializerSupport)deserializerFactory.getDeserializer();
      Calendar out = (Calendar)des.deserialize(xmlName, xmlType, xmlFragment, serContext);

      assertEquals(value.getTime(), out.getTime());
      assertEquals(value.getTimeZone().getRawOffset(), out.getTimeZone().getRawOffset());
   }

   public void testInteger() throws Exception
   {
      QName xmlType = Constants.TYPE_LITERAL_INTEGER;
      BigInteger value = new BigInteger("12345678901234567890");

      SerializerFactoryBase serializerFactory = (SerializerFactoryBase)typeMapping.getSerializer(BigInteger.class, xmlType);
      SerializerSupport ser = (SerializerSupport)serializerFactory.getSerializer();
      String xmlFragment = ser.serialize(xmlName, xmlType, value, serContext, null);
      assertNotNull(xmlFragment);

      DeserializerFactoryBase deserializerFactory = (DeserializerFactoryBase)typeMapping.getDeserializer(BigInteger.class, xmlType);
      DeserializerSupport des = (DeserializerSupport)deserializerFactory.getDeserializer();
      BigInteger out = (BigInteger)des.deserialize(xmlName, xmlType, xmlFragment, serContext);

      assertEquals(value, out);
   }

   public void testDecimal() throws Exception
   {
      QName xmlType = Constants.TYPE_LITERAL_DECIMAL;
      BigDecimal value = new BigDecimal("12345678901234567890");

      SerializerFactoryBase serializerFactory = (SerializerFactoryBase)typeMapping.getSerializer(BigDecimal.class, xmlType);
      SerializerSupport ser = (SerializerSupport)serializerFactory.getSerializer();
      String xmlFragment = ser.serialize(xmlName, xmlType, value, serContext, null);
      assertNotNull(xmlFragment);

      DeserializerFactoryBase deserializerFactory = (DeserializerFactoryBase)typeMapping.getDeserializer(BigDecimal.class, xmlType);
      DeserializerSupport des = (DeserializerSupport)deserializerFactory.getDeserializer();
      BigDecimal out = (BigDecimal)des.deserialize(xmlName, xmlType, xmlFragment, serContext);

      assertEquals(value, out);
   }

   public void testQName() throws Exception
   {
      QName xmlType = Constants.TYPE_LITERAL_QNAME;
      QName value = new QName("http://some-ns", "localPart", "ns1");

      SerializerFactoryBase serializerFactory = (SerializerFactoryBase)typeMapping.getSerializer(QName.class, xmlType);
      SerializerSupport ser = (SerializerSupport)serializerFactory.getSerializer();
      String xmlFragment = ser.serialize(xmlName, xmlType, value, serContext, null);
      assertNotNull(xmlFragment);

      // serialization registers the prefix
      assertEquals("ns1", nsRegistry.getPrefix("http://some-ns"));
      nsRegistry.unregisterURI("http://some-ns");

      DeserializerFactoryBase deserializerFactory = (DeserializerFactoryBase)typeMapping.getDeserializer(QName.class, xmlType);
      DeserializerSupport des = (DeserializerSupport)deserializerFactory.getDeserializer();
      QName out = (QName)des.deserialize(xmlName, xmlType, xmlFragment, serContext);

      assertEquals(value, out);
   }

   public void testURI() throws Exception
   {
      QName xmlType = Constants.TYPE_LITERAL_ANYURI;
      URI value = new URI("http://someURI:that:has:more:parts");

      SerializerFactoryBase serializerFactory = (SerializerFactoryBase)typeMapping.getSerializer(URI.class, xmlType);
      SerializerSupport ser = (SerializerSupport)serializerFactory.getSerializer();
      String xmlFragment = ser.serialize(xmlName, xmlType, value, serContext, null);
      assertNotNull(xmlFragment);

      DeserializerFactoryBase deserializerFactory = (DeserializerFactoryBase)typeMapping.getDeserializer(URI.class, xmlType);
      DeserializerSupport des = (DeserializerSupport)deserializerFactory.getDeserializer();
      URI out = (URI)des.deserialize(xmlName, xmlType, xmlFragment, serContext);

      assertEquals(value, out);
   }

   public void testBase64Binary() throws Exception
   {
      QName xmlType = Constants.TYPE_LITERAL_BASE64BINARY;
      byte[] value = new String("Some base64 binary string").getBytes();

      SerializerFactoryBase serializerFactory = (SerializerFactoryBase)typeMapping.getSerializer(byte[].class, xmlType);
      SerializerSupport ser = (SerializerSupport)serializerFactory.getSerializer();
      String xmlFragment = ser.serialize(xmlName, xmlType, value, serContext, null);
      assertNotNull(xmlFragment);

      DeserializerFactoryBase deserializerFactory = (DeserializerFactoryBase)typeMapping.getDeserializer(byte[].class, xmlType);
      DeserializerSupport des = (DeserializerSupport)deserializerFactory.getDeserializer();
      byte[] out = (byte[])des.deserialize(xmlName, xmlType, xmlFragment, serContext);

      assertEquals(new String(value), new String(out));
   }

   public void testHexBinary() throws Exception
   {
      QName xmlType = Constants.TYPE_LITERAL_HEXBINARY;
      byte[] value = new String("Some hex binary string").getBytes();

      SerializerFactoryBase serializerFactory = (SerializerFactoryBase)typeMapping.getSerializer(byte[].class, xmlType);
      SerializerSupport ser = (SerializerSupport)serializerFactory.getSerializer();
      String xmlFragment = ser.serialize(xmlName, xmlType, value, serContext, null);
      assertNotNull(xmlFragment);

      DeserializerFactoryBase deserializerFactory = (DeserializerFactoryBase)typeMapping.getDeserializer(byte[].class, xmlType);
      DeserializerSupport des = (DeserializerSupport)deserializerFactory.getDeserializer();
      byte[] out = (byte[])des.deserialize(xmlName, xmlType, xmlFragment, serContext);

      assertEquals(new String(value), new String(out));
   }

}