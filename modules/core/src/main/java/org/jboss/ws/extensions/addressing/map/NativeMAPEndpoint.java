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
package org.jboss.ws.extensions.addressing.map;

import java.util.LinkedList;
import java.util.List;

import javax.xml.ws.addressing.EndpointReference;

import org.jboss.wsf.common.addressing.MAPEndpoint;
import org.w3c.dom.Element;

/**
 * 
 * @author Andrew Dinn - adinn@redhat.com
 * @author alessio.soldano@jboss.com
 * @since 25-May-2009
 *
 */
public class NativeMAPEndpoint implements MAPEndpoint
{
   private EndpointReference implementation;

   NativeMAPEndpoint(EndpointReference implementation)
   {
      this.implementation = implementation;
   }

   public String getAddress()
   {
      return implementation.getAddress().getURI().toString();
   }

   public void addReferenceParameter(Element element)
   {
      implementation.getReferenceParameters().addElement(element);
   }
   
   public List<Object> getReferenceParameters()
   {
      List<Object> list = new LinkedList<Object>();
      if (implementation.getReferenceParameters() != null)
      {
         list.addAll(implementation.getReferenceParameters().getElements());
      }
      return list;
   }

   EndpointReference getImplementation()
   {
      return implementation;
   }

}