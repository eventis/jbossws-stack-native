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
package org.jboss.test.ws.tools.jbws_204.sei;

import org.jboss.test.ws.tools.jbws_204.wscompile.simpletypes.USMidwest;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * JBWS -204 : Java to XSDSchema comprehensive test collection
 * The SEI that tests enumerated types in Simple Types
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   May 25, 2005 
 */

public interface SimpleTypeEnumerationSEI extends Remote
{
     public void testEnumeration( USMidwest midwest) throws RemoteException;
}



