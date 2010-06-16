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
package org.jboss.ws.jaxrpc;

// $Id$

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.jboss.util.NotImplementedException;
import org.jboss.ws.Constants;
import org.jboss.ws.jaxrpc.encoding.Base64DeserializerFactory;
import org.jboss.ws.jaxrpc.encoding.Base64SerializerFactory;
import org.jboss.ws.jaxrpc.encoding.CalendarDeserializerFactory;
import org.jboss.ws.jaxrpc.encoding.CalendarSerializerFactory;
import org.jboss.ws.jaxrpc.encoding.DateDeserializerFactory;
import org.jboss.ws.jaxrpc.encoding.DateSerializerFactory;
import org.jboss.ws.jaxrpc.encoding.ElementDeserializerFactory;
import org.jboss.ws.jaxrpc.encoding.ElementSerializerFactory;
import org.jboss.ws.jaxrpc.encoding.HexDeserializerFactory;
import org.jboss.ws.jaxrpc.encoding.HexSerializerFactory;
import org.jboss.ws.jaxrpc.encoding.QNameDeserializerFactory;
import org.jboss.ws.jaxrpc.encoding.QNameSerializerFactory;
import org.jboss.ws.jaxrpc.encoding.SOAPElementDeserializerFactory;
import org.jboss.ws.jaxrpc.encoding.SOAPElementSerializerFactory;
import org.jboss.ws.jaxrpc.encoding.SimpleDeserializerFactory;
import org.jboss.ws.jaxrpc.encoding.SimpleSerializerFactory;
import org.w3c.dom.Element;

/**
 * This is the representation of a type mapping.
 * This TypeMapping implementation supports the encoded encoding style.
 *
 * The TypeMapping instance maintains a tuple of the type
 * {XML typeQName, Java Class, SerializerFactory, DeserializerFactory}.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Oct-2004
 */
public class EncodedTypeMapping extends TypeMappingImpl
{

   /**
    * Construct the default encoded type mapping.
    * Registers javaTypes for all standard XMLSchema types specified by JAXRPC.
    *
    * Note, the order of registered types is important
    * The last xmlType wins for a given javaType
    *
    */
   public EncodedTypeMapping()
   {
      registerStandardLiteralTypes();
      registerStandardSOAP11EncodedTypes();

      // register mapping for xsd:anyType
      registerInternal(SOAPElement.class, Constants.TYPE_SOAP11_ANYTYPE, new SOAPElementSerializerFactory(), new SOAPElementDeserializerFactory());
      registerInternal(Element.class, Constants.TYPE_SOAP11_ANYTYPE, new ElementSerializerFactory(), new ElementDeserializerFactory());
   }

   private void registerStandardSOAP11EncodedTypes()
   {
      registerInternal(BigDecimal.class, Constants.TYPE_SOAP11_DECIMAL, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      registerInternal(BigInteger.class, Constants.TYPE_SOAP11_POSITIVEINTEGER, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(BigInteger.class, Constants.TYPE_SOAP11_NEGATIVEINTEGER, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(BigInteger.class, Constants.TYPE_SOAP11_NONPOSITIVEINTEGER, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(BigInteger.class, Constants.TYPE_SOAP11_NONNEGATIVEINTEGER, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(BigInteger.class, Constants.TYPE_SOAP11_UNSIGNEDLONG, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(BigInteger.class, Constants.TYPE_SOAP11_INTEGER, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      registerInternal(Date.class, Constants.TYPE_SOAP11_DATETIME, new DateSerializerFactory(), new DateDeserializerFactory());

      registerInternal(Calendar.class, Constants.TYPE_SOAP11_DATE, new CalendarSerializerFactory(), new CalendarDeserializerFactory());
      registerInternal(Calendar.class, Constants.TYPE_SOAP11_TIME, new CalendarSerializerFactory(), new CalendarDeserializerFactory());
      registerInternal(Calendar.class, Constants.TYPE_SOAP11_DATETIME, new CalendarSerializerFactory(), new CalendarDeserializerFactory());

      registerInternal(QName.class, Constants.TYPE_SOAP11_QNAME, new QNameSerializerFactory(), new QNameDeserializerFactory());

      registerInternal(String.class, Constants.TYPE_SOAP11_ANYSIMPLETYPE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(String.class, Constants.TYPE_SOAP11_DURATION, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(String.class, Constants.TYPE_SOAP11_GDAY, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(String.class, Constants.TYPE_SOAP11_GMONTH, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(String.class, Constants.TYPE_SOAP11_GMONTHDAY, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(String.class, Constants.TYPE_SOAP11_GYEAR, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(String.class, Constants.TYPE_SOAP11_GYEARMONTH, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(String.class, Constants.TYPE_SOAP11_ID, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(String.class, Constants.TYPE_SOAP11_LANGUAGE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(String.class, Constants.TYPE_SOAP11_NAME, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(String.class, Constants.TYPE_SOAP11_NCNAME, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(String.class, Constants.TYPE_SOAP11_NMTOKEN, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(String.class, Constants.TYPE_SOAP11_NORMALIZEDSTRING, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(String.class, Constants.TYPE_SOAP11_TOKEN, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(String.class, Constants.TYPE_SOAP11_STRING, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      registerInternal(String[].class, Constants.TYPE_SOAP11_NMTOKENS, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      registerInternal(URI.class, Constants.TYPE_SOAP11_ANYURI, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      registerInternal(boolean.class, Constants.TYPE_SOAP11_BOOLEAN, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(Boolean.class, Constants.TYPE_SOAP11_BOOLEAN, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      registerInternal(byte.class, Constants.TYPE_SOAP11_BYTE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(Byte.class, Constants.TYPE_SOAP11_BYTE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      registerInternal(byte[].class, Constants.TYPE_SOAP11_HEXBINARY, new HexSerializerFactory(), new HexDeserializerFactory());
      registerInternal(Byte[].class, Constants.TYPE_SOAP11_HEXBINARY, new HexSerializerFactory(), new HexDeserializerFactory());
      registerInternal(byte[].class, Constants.TYPE_SOAP11_BASE64BINARY, new Base64SerializerFactory(), new Base64DeserializerFactory());
      registerInternal(Byte[].class, Constants.TYPE_SOAP11_BASE64BINARY, new Base64SerializerFactory(), new Base64DeserializerFactory());
      registerInternal(byte[].class, Constants.TYPE_SOAP11_BASE64, new Base64SerializerFactory(), new Base64DeserializerFactory());
      registerInternal(Byte[].class, Constants.TYPE_SOAP11_BASE64, new Base64SerializerFactory(), new Base64DeserializerFactory());

      registerInternal(double.class, Constants.TYPE_SOAP11_DOUBLE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(Double.class, Constants.TYPE_SOAP11_DOUBLE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      registerInternal(float.class, Constants.TYPE_SOAP11_FLOAT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(Float.class, Constants.TYPE_SOAP11_FLOAT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      registerInternal(int.class, Constants.TYPE_SOAP11_UNSIGNEDSHORT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(Integer.class, Constants.TYPE_SOAP11_UNSIGNEDSHORT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(int.class, Constants.TYPE_SOAP11_INT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(Integer.class, Constants.TYPE_SOAP11_INT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      registerInternal(long.class, Constants.TYPE_SOAP11_UNSIGNEDINT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(Long.class, Constants.TYPE_SOAP11_UNSIGNEDINT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(long.class, Constants.TYPE_SOAP11_LONG, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(Long.class, Constants.TYPE_SOAP11_LONG, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      registerInternal(short.class, Constants.TYPE_SOAP11_UNSIGNEDBYTE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(Short.class, Constants.TYPE_SOAP11_UNSIGNEDBYTE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(short.class, Constants.TYPE_SOAP11_SHORT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      registerInternal(Short.class, Constants.TYPE_SOAP11_SHORT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
   }

   /**
    * Returns the encodingStyle URIs (as String[]) supported by this TypeMapping instance.
    * A TypeMapping that contains only encoding style independent serializers and deserializers
    * returns null from this method.
    *
    * @return Array of encodingStyle URIs for the supported encoding styles
    */
   public String[] getSupportedEncodings()
   {
      return new String[] { "encoded" };
   }

   /**
    * Sets the encodingStyle URIs supported by this TypeMapping instance. A TypeMapping that contains only encoding
    * independent serializers and deserializers requires null as the parameter for this method.
    *
    * @param encodingStyleURIs Array of encodingStyle URIs for the supported encoding styles
    */
   public void setSupportedEncodings(String[] encodingStyleURIs)
   {
      throw new NotImplementedException();
   }
}