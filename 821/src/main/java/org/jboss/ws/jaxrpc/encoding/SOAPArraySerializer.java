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
package org.jboss.ws.jaxrpc.encoding;

// $Id$

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.binding.BindingException;
import org.jboss.ws.jaxrpc.TypeMappingImpl;
import org.jboss.ws.metadata.ParameterMetaData;
import org.jboss.ws.utils.JavaUtils;
import org.jboss.xb.binding.NamespaceRegistry;
import org.w3c.dom.NamedNodeMap;
import java.util.Iterator;

/**
 * A Serializer that can handle SOAP encoded arrays.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 31-Oct-2005
 */
public class SOAPArraySerializer extends SerializerSupport
{
   // provide logging
   private static final Logger log = Logger.getLogger(SOAPArraySerializer.class);

   private SerializerSupport compSerializer;
   private NullValueSerializer nullSerializer;
   private boolean isArrayComponentType;
   private boolean xsiNamespaceInserted;
   private StringBuilder xmlFragment;

   public SOAPArraySerializer() throws BindingException
   {
      nullSerializer = new NullValueSerializer();
   }

   /**
    */
   public String serialize(QName xmlName, QName xmlType, Object value, SerializationContextImpl serContext, NamedNodeMap attributes) throws BindingException
   {
      log.debug("serialize: [xmlName=" + xmlName + ",xmlType=" + xmlType + ",valueType=" + value.getClass().getName() + "]");
      try
      {
         ParameterMetaData paramMetaData = (ParameterMetaData)serContext.getProperty(SerializationContextImpl.PROPERTY_PARAMETER_META_DATA);
         QName compXmlType = paramMetaData.getSOAPArrayCompType();
         QName compXmlName = paramMetaData.getXmlName();
         Class javaType = paramMetaData.getJavaType();

         Class compJavaType = javaType.getComponentType();
         isArrayComponentType = isArrayJavaType(compJavaType) && isArrayXmlType(compXmlType);
         while (compJavaType.getComponentType() != null && isArrayComponentType == false)
         {
            compJavaType = compJavaType.getComponentType();
            isArrayComponentType = isArrayJavaType(compJavaType) && isArrayXmlType(compXmlType);
         }

         TypeMappingImpl typeMapping = serContext.getTypeMapping();
         if (compXmlType == null)
         {
            compXmlType = typeMapping.getXMLType(compJavaType);
            paramMetaData.setSOAPArrayCompType(compXmlType);
         }

         if (compXmlType == null)
            throw new WSException("Cannot obtain component xmlType for: " + compJavaType);

         // Get the component type serializer factory
         log.debug("Get component serializer for: [javaType=" + compJavaType.getName() + ",xmlType=" + compXmlType + "]");
         SerializerFactoryBase compSerializerFactory = (SerializerFactoryBase)typeMapping.getSerializer(compJavaType, compXmlType);
         if (compSerializerFactory == null)
         {
            log.warn("Cannot obtain component serializer for: [javaType=" + compJavaType.getName() + ",xmlType=" + compXmlType + "]");
            compSerializerFactory = (SerializerFactoryBase)typeMapping.getSerializer(null, compXmlType);
         }
         if (compSerializerFactory == null)
            throw new WSException("Cannot obtain component serializer for: " + compXmlType);

         // Get the component type serializer
         compSerializer = (SerializerSupport)compSerializerFactory.getSerializer();

         // Get the corresponding wrapper type
         if (JavaUtils.isPrimitive(value.getClass()))
            value = JavaUtils.getWrapperValue(value);

         // register soapenc namespaces
         NamespaceRegistry nsReg = serContext.getNamespaceRegistry();
         nsReg.registerURI(Constants.URI_SOAP11_ENC, Constants.PREFIX_SOAP11_ENC);
         nsReg.registerURI(Constants.NS_SCHEMA_XSD, Constants.PREFIX_XSD);
         nsReg.registerURI(Constants.NS_SCHEMA_XSI, Constants.PREFIX_XSI);
         xmlFragment = new StringBuilder("<" + Constants.PREFIX_SOAP11_ENC + ":Array ");
         if (value instanceof Object[])
         {
            Object[] objArr = (Object[])value;
            String arrayDim = "" + objArr.length;

            // Get multiple array dimension
            Object[] subArr = (Object[])value;
            while (isArrayComponentType == false && subArr.length > 0 && subArr[0] instanceof Object[])
            {
               subArr = (Object[])subArr[0];
               arrayDim += "," + subArr.length;
            }

            compXmlType = serContext.getNamespaceRegistry().registerQName(compXmlType);
            String arrayType = Constants.PREFIX_SOAP11_ENC + ":arrayType='" + compXmlType.getPrefix() + ":" + compXmlType.getLocalPart() + "[" + arrayDim + "]'";
            xmlFragment.append(arrayType);
            // append namespaces
            Iterator it = nsReg.getRegisteredPrefixes();
            while(it.hasNext())
            {
               String nsPrefix = (String)it.next();
               xmlFragment.append(" xmlns:").append(nsPrefix).append("='").append(nsReg.getNamespaceURI(nsPrefix)).append("'");
            }

            xmlFragment.append(">");

            serializeArrayComponents(compXmlName, compXmlType, serContext, objArr);
         }
         else
         {
            throw new WSException("Unsupported array type: " + javaType);
         }
         xmlFragment.append("</" + Constants.PREFIX_SOAP11_ENC + ":Array>");

         log.debug("serialized: " + xmlFragment);
         return xmlFragment.toString();
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new BindingException(e);
      }
   }

   private void serializeArrayComponents(QName compXmlName, QName compXmlType, SerializationContextImpl serContext, Object[] objArr) throws BindingException
   {
      for (Object compValue : objArr)
      {
         if (isArrayComponentType == false && compValue instanceof Object[])
         {
            serializeArrayComponents(compXmlName, compXmlType, serContext, (Object[])compValue);
         }
         else
         {
            SerializerSupport ser = compSerializer;

            // Null component value
            if (compValue == null)
            {
               ser = nullSerializer;
               if (xsiNamespaceInserted == false)
               {
                  xsiNamespaceInserted = true;
                  int insIndex = ("<" + Constants.PREFIX_SOAP11_ENC + ":Array ").length();
                  xmlFragment.insert(insIndex, "xmlns:" + Constants.PREFIX_XSI + "='" + Constants.NS_SCHEMA_XSI + "' ");
               }
            }

            String compFragment = ser.serialize(compXmlName, compXmlType, compValue, serContext, null);
            xmlFragment.append(compFragment);
         }
      }
   }

   /** True for all array xmlTypes, i.e. nmtokens, base64Binary, hexBinary
    * 
    *  FIXME: This method should be removed as soon as we can reliably get the SOAP
    *  arrayType from wsdl + schema. 
    */
   private boolean isArrayXmlType(QName xmlType)
   {
      boolean isArrayType = Constants.TYPE_SOAP11_BASE64.equals(xmlType);
      isArrayType = isArrayType || Constants.TYPE_SOAP11_BASE64.equals(xmlType);
      isArrayType = isArrayType || Constants.TYPE_SOAP11_BASE64BINARY.equals(xmlType);
      isArrayType = isArrayType || Constants.TYPE_SOAP11_HEXBINARY.equals(xmlType);
      isArrayType = isArrayType || Constants.TYPE_SOAP11_NMTOKENS.equals(xmlType);
      isArrayType = isArrayType || Constants.TYPE_LITERAL_BASE64BINARY.equals(xmlType);
      isArrayType = isArrayType || Constants.TYPE_LITERAL_HEXBINARY.equals(xmlType);
      isArrayType = isArrayType || Constants.TYPE_LITERAL_NMTOKENS.equals(xmlType);
      return isArrayType;
   }

   /** True for all array javaTypes, i.e. String[], Byte[], byte[] 
    * 
    *  FIXME: This method should be removed as soon as we can reliably get the SOAP
    *  arrayType from wsdl + schema. 
    */
   private boolean isArrayJavaType(Class javaType)
   {
      boolean isBinaryType = String[].class.equals(javaType) || Byte[].class.equals(javaType) || byte[].class.equals(javaType);
      return isBinaryType;
   }

}