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
package org.jboss.ws.core.soap.attachment;

import java.io.OutputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.ParameterList;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.core.soap.utils.SOAPElementWriter;

/**
 * MultipartRelatedEncoder encodes a SOAPMessage
 * into a multipart/related stream.
 *
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 */
public class MultipartRelatedSwAEncoder extends MultipartRelatedEncoder
{
   /**
    * Construct a MultipartRelatedEncoder from the specified <code>SOAPMessage</code>.
    * There is minimal overhead on construction because all source streams are read
    * only on a call to {@link #writeTo(OutputStream)}.
    *
    * @param soapMessage the SOAP message to be sent as a root part
    */
   public MultipartRelatedSwAEncoder(SOAPMessage soapMessage) throws SOAPException
   {
      super(soapMessage);
   }

   public void encodeMultipartRelatedMessage() throws SOAPException, MessagingException
   {
      ParameterList p = new ParameterList();
      p.set("type", MimeConstants.TYPE_TEXT_XML);
      p.set("start", MimeConstants.ROOTPART_CID);

      MimeMultipart multipart = new MimeMultipart("related" + p);
      MimeBodyPart rootPart = new MimeBodyPart();

      /*
       * TODO - For now we build the root part content from a serialized string of the
       * DOM tree, in the future, this should utilize a DataHandler, and a DataContentHandler
       * to marshall the message. In this way the root part can be lazily written to the output
       * stream.
       */
      SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
      String envStr = SOAPElementWriter.writeElement(soapEnv, false);
      rootPart.setText(envStr, "UTF-8");

      rootPart.setContentID(MimeConstants.ROOTPART_CID);
      rootPart.setHeader(MimeConstants.CONTENT_TYPE, MimeConstants.TYPE_XML_UTF8);
      rootPart.setHeader(MimeConstants.CONTENT_TRANSFER_ENCODING, MimeConstants.TEXT_8BIT_ENCODING);

      multipart.addBodyPart(rootPart);

      addAttachmentParts(multipart);

      this.multipart = multipart;
   }
}
