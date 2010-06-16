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
package org.jboss.ws.eventing.element;

// $Id$

import java.util.Date;

/**
 * @author Heiko Braun, <heiko@openj.net>
 * @since 24-Nov-2005
 */
public class SubscribeResponse
{
   private org.jboss.ws.eventing.element.EndpointReference subscriptionManager;
   private Date expires;

   public EndpointReference getSubscriptionManager()
   {
      return subscriptionManager;
   }

   public void setSubscriptionManager(EndpointReference subscriptionManager)
   {
      this.subscriptionManager = subscriptionManager;
   }

   public Date getExpires()
   {
      return expires;
   }

   public void setExpires(Date expires)
   {
      this.expires = expires;
   }

}