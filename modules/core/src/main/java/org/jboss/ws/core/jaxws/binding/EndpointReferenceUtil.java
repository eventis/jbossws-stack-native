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
package org.jboss.ws.core.jaxws.binding;

import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

/**
 * Transforms an EPR to an instance of a given EndpointReference class 
 * 
 * @since 12-Jan-2009
 * @author alessio.soldano@jboss.com
 *
 */
public class EndpointReferenceUtil {
   
   public static <T extends EndpointReference> T transform(Class<T> clazz, EndpointReference epr) {
       assert epr != null;
       if (clazz.isAssignableFrom(W3CEndpointReference.class)) {
           if (epr instanceof W3CEndpointReference) {
               return (T) epr;
           }
           else
           {
              throw new WebServiceException("Unsupported EndpointReference: " + epr);
           }
       }
       //transformations from different types of EndpointReference could be supported in future...
       
       throw new WebServiceException("EndpointReference of type " + clazz + " not supported.");
   }
}