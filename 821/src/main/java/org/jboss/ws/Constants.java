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
package org.jboss.ws;


import javax.xml.namespace.QName;
import javax.xml.soap.Name;

import org.jboss.ws.soap.NameImpl;

/**
 * A collection of constants relevant to JBossWS
 *
 * @author Thomas.Diesler@jboss.org
 * @author Anil.Saldhana@jboss.org
 * @since 10-Oct-2004
 */
public interface Constants
{
   /** Header for XML Documents */
   static final String XML_HEADER = "<?xml version='1.0' encoding='UTF-8'?>";

   /** Default charset for XML Documents */
   static final String DEFAULT_XML_CHARSET = "UTF-8";

   /** JBossWS namespace URI */
   static final String NS_JBOSSWS_URI = "http://www.jboss.org/jbossws";
   /** XML Namespace */
   static final String NS_XML = "http://www.w3.org/XML/1998/namespace";
   /** XMLSchema namespace http://www.w3.org/2001/XMLSchema */
   static final String NS_SCHEMA_XSD = "http://www.w3.org/2001/XMLSchema";
   /** XMLSchema instance namespace http://www.w3.org/2001/XMLSchema-instance */
   static final String NS_SCHEMA_XSI = "http://www.w3.org/2001/XMLSchema-instance";
   /** SOAP-1.1 namespace http://schemas.xmlsoap.org/wsdl/soap/ */
   static final String NS_SOAP11 = "http://schemas.xmlsoap.org/wsdl/soap/";
   /** SOAP-1.1 envelope namespace http://schemas.xmlsoap.org/soap/envelope/ */
   static final String NS_SOAP11_ENV = "http://schemas.xmlsoap.org/soap/envelope/";
   /** SOAP-1.2 envelope namespace http://www.w3.org/2003/05/soap-envelope */
   static final String NS_SOAP12_ENV = "http://www.w3.org/2003/05/soap-envelope";
   /** The namespace for the SwA mime type */
   static final String NS_SWA_MIME = "http://schemas.xmlsoap.org/wsdl/mime/";
   /** Default namespace for WSDL-1.1 http://schemas.xmlsoap.org/wsdl/ */
   static final String NS_WSDL11 = "http://schemas.xmlsoap.org/wsdl/";
   /** Default namespace for WSDL-2.0 http://www.w3.org/TR/wsdl20 */
   static final String NS_WSDL20 = "http://www.w3.org/2003/11/wsdl";
   /** The namespace for the MTOM content type attribute. */
   static final String NS_XML_MIME = "http://www.w3.org/2005/05/xmlmime";
   /** The namespace for XOP. */
   static final String NS_XOP = "http://www.w3.org/2004/08/xop/include";

   /** SOAP-1.1 encoding URI */
   static final String URI_SOAP11_ENC = "http://schemas.xmlsoap.org/soap/encoding/";
   /** Literal encoding URI */
   static final String URI_LITERAL_ENC = "";
   /** WSDL 2.0 Encoding Rules */
   static final String URI_STYLE_RPC = "http://www.w3.org/2004/03/wsdl/style/rpc";
   static final String URI_STYLE_IRI = "http://www.w3.org/2004/03/wsdl/style/iri"; //represents doc
   /**Style of WSDL */
   static final String RPC_LITERAL = "RPC/Literal";
   static final String DOCUMENT_LITERAL = "Document/Literal";

   // Some prefixes
   static final String PREFIX_ENV = "env";
   static final String PREFIX_XMIME = "xmime";
   static final String PREFIX_SOAP = "soap";
   static final String PREFIX_SOAP11_ENC = "soap11-enc";
   static final String PREFIX_TNS = "tns";
   static final String PREFIX_WSDL = "wsdl";
   static final String PREFIX_XOP = "xop";
   static final String PREFIX_XSD = "xsd";
   static final String PREFIX_XSI = "xsi";
   static final String PREFIX_XML = "xml";

   /** SOAP-1.1 roles */
   static final String URI_SOAP11_NEXT_ACTOR = "http://schemas.xmlsoap.org/soap/actor/next";

   /** SOAP-1.1 attributes */
   static final String SOAP11_ATTR_ACTOR = "actor";
   static final String SOAP11_ATTR_ROLE = "role";
   static final String SOAP11_ATTR_MUST_UNDERSTAND = "mustUnderstand";

   /** SOAP-1.1 fault codes */
   static final QName SOAP11_FAULT_CODE_CLIENT = new QName(NS_SOAP11_ENV, "Client", PREFIX_ENV);
   static final QName SOAP11_FAULT_CODE_SERVER = new QName(NS_SOAP11_ENV, "Server", PREFIX_ENV);
   static final QName SOAP11_FAULT_CODE_VERSION_MISMATCH = new QName(NS_SOAP11_ENV, "VersionMismatch", PREFIX_ENV);
   static final QName SOAP11_FAULT_CODE_MUST_UNDERSTAND = new QName(NS_SOAP11_ENV, "MustUnderstand", PREFIX_ENV);

   /** SOAP-1.1 elements */
   static final Name SOAP11_ENVELOPE = new NameImpl("Envelope", PREFIX_ENV, NS_SOAP11_ENV);
   static final Name SOAP11_HEADER = new NameImpl("Header", PREFIX_ENV, NS_SOAP11_ENV);
   static final Name SOAP11_BODY = new NameImpl("Body", PREFIX_ENV, NS_SOAP11_ENV);
   static final Name SOAP11_FAULT = new NameImpl("Fault", PREFIX_ENV, NS_SOAP11_ENV);

   /** SOAP-1.2 roles */
   static final String URI_SOAP12_ROLE_NONE = "http://www.w3.org/2003/05/soap-envelope/role/none";
   static final String URI_SOAP12_ROLE_NEXT = "http://www.w3.org/2003/05/soap-envelope/role/next";
   static final String URI_SOAP12_ROLE_ULTIMATE_RECEIVER = "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver";

   /** The default RPC return parameter name */
   static final String DEFAULT_RPC_RETURN_NAME = "result"; // FIXME: According to JSR-181 this should be 'return'

   /** Standard Literal XML types */
   public static final QName TYPE_LITERAL_ANYSIMPLETYPE = new QName(NS_SCHEMA_XSD, "anySimpleType", PREFIX_XSD);
   public static final QName TYPE_LITERAL_ANYTYPE = new QName(NS_SCHEMA_XSD, "anyType", PREFIX_XSD);
   public static final QName TYPE_LITERAL_ANYURI = new QName(NS_SCHEMA_XSD, "anyURI", PREFIX_XSD);
   public static final QName TYPE_LITERAL_BASE64BINARY = new QName(NS_SCHEMA_XSD, "base64Binary", PREFIX_XSD);
   public static final QName TYPE_LITERAL_BOOLEAN = new QName(NS_SCHEMA_XSD, "boolean", PREFIX_XSD);
   public static final QName TYPE_LITERAL_BYTE = new QName(NS_SCHEMA_XSD, "byte", PREFIX_XSD);
   public static final QName TYPE_LITERAL_DATE = new QName(NS_SCHEMA_XSD, "date", PREFIX_XSD);
   public static final QName TYPE_LITERAL_DATETIME = new QName(NS_SCHEMA_XSD, "dateTime", PREFIX_XSD);
   public static final QName TYPE_LITERAL_DECIMAL = new QName(NS_SCHEMA_XSD, "decimal", PREFIX_XSD);
   public static final QName TYPE_LITERAL_DOUBLE = new QName(NS_SCHEMA_XSD, "double", PREFIX_XSD);
   public static final QName TYPE_LITERAL_DURATION = new QName(NS_SCHEMA_XSD, "duration", PREFIX_XSD);
   public static final QName TYPE_LITERAL_FLOAT = new QName(NS_SCHEMA_XSD, "float", PREFIX_XSD);
   public static final QName TYPE_LITERAL_GDAY = new QName(NS_SCHEMA_XSD, "gDay", PREFIX_XSD);
   public static final QName TYPE_LITERAL_GMONTH = new QName(NS_SCHEMA_XSD, "gMonth", PREFIX_XSD);
   public static final QName TYPE_LITERAL_GMONTHDAY = new QName(NS_SCHEMA_XSD, "gMonthDay", PREFIX_XSD);
   public static final QName TYPE_LITERAL_GYEAR = new QName(NS_SCHEMA_XSD, "gYear", PREFIX_XSD);
   public static final QName TYPE_LITERAL_GYEARMONTH = new QName(NS_SCHEMA_XSD, "gYearMonth", PREFIX_XSD);
   public static final QName TYPE_LITERAL_HEXBINARY = new QName(NS_SCHEMA_XSD, "hexBinary", PREFIX_XSD);
   public static final QName TYPE_LITERAL_ID = new QName(NS_SCHEMA_XSD, "ID", PREFIX_XSD);
   public static final QName TYPE_LITERAL_INT = new QName(NS_SCHEMA_XSD, "int", PREFIX_XSD);
   public static final QName TYPE_LITERAL_INTEGER = new QName(NS_SCHEMA_XSD, "integer", PREFIX_XSD);
   public static final QName TYPE_LITERAL_LANGUAGE = new QName(NS_SCHEMA_XSD, "language", PREFIX_XSD);
   public static final QName TYPE_LITERAL_LONG = new QName(NS_SCHEMA_XSD, "long", PREFIX_XSD);
   public static final QName TYPE_LITERAL_NAME = new QName(NS_SCHEMA_XSD, "Name", PREFIX_XSD);
   public static final QName TYPE_LITERAL_NCNAME = new QName(NS_SCHEMA_XSD, "NCName", PREFIX_XSD);
   public static final QName TYPE_LITERAL_NEGATIVEINTEGER = new QName(NS_SCHEMA_XSD, "negativeInteger", PREFIX_XSD);
   public static final QName TYPE_LITERAL_NMTOKEN = new QName(NS_SCHEMA_XSD, "NMTOKEN", PREFIX_XSD);
   public static final QName TYPE_LITERAL_NMTOKENS = new QName(NS_SCHEMA_XSD, "NMTOKENS", PREFIX_XSD);
   public static final QName TYPE_LITERAL_NONNEGATIVEINTEGER = new QName(NS_SCHEMA_XSD, "nonNegativeInteger", PREFIX_XSD);
   public static final QName TYPE_LITERAL_NONPOSITIVEINTEGER = new QName(NS_SCHEMA_XSD, "nonPositiveInteger", PREFIX_XSD);
   public static final QName TYPE_LITERAL_NORMALIZEDSTRING = new QName(NS_SCHEMA_XSD, "normalizedString", PREFIX_XSD);
   public static final QName TYPE_LITERAL_POSITIVEINTEGER = new QName(NS_SCHEMA_XSD, "positiveInteger", PREFIX_XSD);
   public static final QName TYPE_LITERAL_QNAME = new QName(NS_SCHEMA_XSD, "QName", PREFIX_XSD);
   public static final QName TYPE_LITERAL_SHORT = new QName(NS_SCHEMA_XSD, "short", PREFIX_XSD);
   public static final QName TYPE_LITERAL_STRING = new QName(NS_SCHEMA_XSD, "string", PREFIX_XSD);
   public static final QName TYPE_LITERAL_TIME = new QName(NS_SCHEMA_XSD, "time", PREFIX_XSD);
   public static final QName TYPE_LITERAL_TOKEN = new QName(NS_SCHEMA_XSD, "token", PREFIX_XSD);
   public static final QName TYPE_LITERAL_UNSIGNEDBYTE = new QName(NS_SCHEMA_XSD, "unsignedByte", PREFIX_XSD);
   public static final QName TYPE_LITERAL_UNSIGNEDINT = new QName(NS_SCHEMA_XSD, "unsignedInt", PREFIX_XSD);
   public static final QName TYPE_LITERAL_UNSIGNEDLONG = new QName(NS_SCHEMA_XSD, "unsignedLong", PREFIX_XSD);
   public static final QName TYPE_LITERAL_UNSIGNEDSHORT = new QName(NS_SCHEMA_XSD, "unsignedShort", PREFIX_XSD);

   /** Standard SOAP-1.1 encoded XML types */
   public static final QName TYPE_SOAP11_ANYSIMPLETYPE = new QName(URI_SOAP11_ENC, "anySimpleType", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_ANYTYPE = new QName(URI_SOAP11_ENC, "anyType", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_ANYURI = new QName(URI_SOAP11_ENC, "anyURI", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_BASE64 = new QName(URI_SOAP11_ENC, "base64", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_BASE64BINARY = new QName(URI_SOAP11_ENC, "base64Binary", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_BOOLEAN = new QName(URI_SOAP11_ENC, "boolean", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_BYTE = new QName(URI_SOAP11_ENC, "byte", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_DATE = new QName(URI_SOAP11_ENC, "date", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_DATETIME = new QName(URI_SOAP11_ENC, "dateTime", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_DECIMAL = new QName(URI_SOAP11_ENC, "decimal", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_DOUBLE = new QName(URI_SOAP11_ENC, "double", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_DURATION = new QName(URI_SOAP11_ENC, "duration", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_FLOAT = new QName(URI_SOAP11_ENC, "float", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_GDAY = new QName(URI_SOAP11_ENC, "gDay", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_GMONTH = new QName(URI_SOAP11_ENC, "gMonth", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_GMONTHDAY = new QName(URI_SOAP11_ENC, "gMonthDay", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_GYEAR = new QName(URI_SOAP11_ENC, "gYear", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_GYEARMONTH = new QName(URI_SOAP11_ENC, "gYearMonth", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_HEXBINARY = new QName(URI_SOAP11_ENC, "hexBinary", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_ID = new QName(URI_SOAP11_ENC, "ID", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_INT = new QName(URI_SOAP11_ENC, "int", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_INTEGER = new QName(URI_SOAP11_ENC, "integer", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_LANGUAGE = new QName(URI_SOAP11_ENC, "language", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_LONG = new QName(URI_SOAP11_ENC, "long", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_NAME = new QName(URI_SOAP11_ENC, "Name", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_NCNAME = new QName(URI_SOAP11_ENC, "NCName", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_NEGATIVEINTEGER = new QName(URI_SOAP11_ENC, "negativeInteger", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_NMTOKEN = new QName(URI_SOAP11_ENC, "NMTOKEN", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_NMTOKENS = new QName(URI_SOAP11_ENC, "NMTOKENS", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_NONNEGATIVEINTEGER = new QName(URI_SOAP11_ENC, "nonNegativeInteger", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_NONPOSITIVEINTEGER = new QName(URI_SOAP11_ENC, "nonPositiveInteger", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_NORMALIZEDSTRING = new QName(URI_SOAP11_ENC, "normalizedString", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_POSITIVEINTEGER = new QName(URI_SOAP11_ENC, "positiveInteger", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_QNAME = new QName(URI_SOAP11_ENC, "QName", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_SHORT = new QName(URI_SOAP11_ENC, "short", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_STRING = new QName(URI_SOAP11_ENC, "string", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_TIME = new QName(URI_SOAP11_ENC, "time", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_TOKEN = new QName(URI_SOAP11_ENC, "token", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_UNSIGNEDBYTE = new QName(URI_SOAP11_ENC, "unsignedByte", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_UNSIGNEDINT = new QName(URI_SOAP11_ENC, "unsignedInt", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_UNSIGNEDLONG = new QName(URI_SOAP11_ENC, "unsignedLong", PREFIX_SOAP11_ENC);
   public static final QName TYPE_SOAP11_UNSIGNEDSHORT = new QName(URI_SOAP11_ENC, "unsignedShort", PREFIX_SOAP11_ENC);

   /** Encoded mime type namespace for internall and DII use */
   static final String NS_ATTACHMENT_MIME_TYPE = "http://www.jboss.org/jbossws/attachment/mimetype";

   /** Attachment Types */
   static final QName TYPE_MIME_APPLICATION_XML = new QName(NS_ATTACHMENT_MIME_TYPE, "application_xml");
   static final QName TYPE_MIME_IMAGE_JPEG = new QName(NS_ATTACHMENT_MIME_TYPE, "image_jpeg");
   static final QName TYPE_MIME_IMAGE_GIF = new QName(NS_ATTACHMENT_MIME_TYPE, "image_gif");
   static final QName TYPE_MIME_MULTIPART_MIXED = new QName(NS_ATTACHMENT_MIME_TYPE, "multipart_mixed");
   static final QName TYPE_MIME_TEXT_PLAIN = new QName(NS_ATTACHMENT_MIME_TYPE, "text_plain");
   static final QName TYPE_MIME_TEXT_XML = new QName(NS_ATTACHMENT_MIME_TYPE, "text_xml");

   static final QName TYPE_XMIME_DEFAULT = new QName(NS_XML_MIME, "base64Binary");   
   
   /** For out of bound transport (i.e. in headers); http://www.w3.org/2004/08/wsdl/feature/AD/data */
   static final String WSDL_PROPERTY_APPLICATION_DATA = "http://www.w3.org/2004/08/wsdl/feature/AD/data";
   /** The key to the original message part name */
   static final String WSDL_PROPERTY_MESSAGE_NAME = "http://www.jboss.org/jbossws/messagename";
   /** Key to the inbound message name */
   static final String WSDL_PROPERTY_MESSAGE_NAME_IN = "http://www.jboss.org/jbossws/messagename/in";
   /** Key to the outbound message name */
   static final String WSDL_PROPERTY_MESSAGE_NAME_OUT = "http://www.jboss.org/jbossws/messagename/out";
   /** Key to the inboudn wsa action */
   static final String WSDL_PROPERTY_ACTION_IN = "http://www.jboss.org/jbossws/wsa/actionIn";
   /** Key to the outbound wsa action */
   static final String WSDL_PROPERTY_ACTION_OUT = "http://www.jboss.org/jbossws/wsa/actionOut";

   static final String WSDL_PROPERTY_EVENTSOURCE = "http://www.jboss.org/jbossws/wse/isEventSource";

   /** The key to the original message part name */
   static final String WSDL_PROPERTY_PART_NAME = "http://www.jboss.org/jbossws/partname";
   /** The key to the message part type in case a part does not reference an element; http://www.jboss.org/jbossws/rpc/xmltype */
   static final String WSDL_PROPERTY_RPC_XMLTYPE = "http://www.jboss.org/jbossws/rpc/xmltype";
   /** Used as WSDL 2.0 property string to provide support for WSDL 1.1 mime types */
   static final String WSDL_PROPERTY_WSDL11_MIME_TYPE = NS_ATTACHMENT_MIME_TYPE;
   /** Indicate that the operation has zero arguments */
   static final String WSDL_PROPERTY_ZERO_ARGS = "http://www.jboss.org/jbossws/zero-args";
   /** Indicate that the operation has a void return*/
   static final String WSDL_PROPERTY_VOID_RETURN = "http://www.jboss.org/jbossws/void-return";
   /** Indicates that an output is a return parameter */
   static final String WSDL_PROPERTY_RETURN_PART = "http://www.jboss.org/jbossws/return-part";

   static final QName WSDL_ATTRIBUTE_WSA_ACTION = new QName("http://www.w3.org/2005/08/addressing", "Action");

   static final QName WSDL_ATTRIBUTE_WSE_EVENTSOURCE = new QName("http://schemas.xmlsoap.org/ws/2004/08/eventing", "EventSource");

   /** WSDL-2.0 exchange patterns */
   static final String WSDL20_PATTERN_IN_ONLY = "http://www.w3.org/2004/08/wsdl/in-only";
   static final String WSDL20_PATTERN_ROUST_IN_ONLY = "http://www.w3.org/2004/08/wsdl/robust-in-only";
   static final String WSDL20_PATTERN_IN_OUT = "http://www.w3.org/2004/08/wsdl/in-out";
   static final String WSDL20_PATTERN_IN_OPTIONAL_OUT = "http://www.w3.org/2004/08/wsdl/in-opt-out";
   static final String WSDL20_PATTERN_OUT_ONLY = "http://www.w3.org/2004/08/wsdl/out-only";
   static final String WSDL20_PATTERN_ROBUST_OUT_ONLY = "http://www.w3.org/2004/08/wsdl/robust-out-only";
   static final String WSDL20_PATTERN_OUT_IN = "http://www.w3.org/2004/08/wsdl/out-in";
   static final String WSDL20_PATTERN_OUT_OPT_IN = "http://www.w3.org/2004/08/wsdl/out-opt-in";
}