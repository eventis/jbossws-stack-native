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
package org.jboss.ws.extensions.wsrm.policy;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.policy.AndCompositeAssertion;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.apache.ws.policy.XorCompositeAssertion;
import org.jboss.ws.extensions.policy.deployer.domainAssertion.AssertionDeployer;
import org.jboss.ws.extensions.policy.deployer.exceptions.UnsupportedAssertionException;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ExtensibleMetaData;
import org.jboss.ws.metadata.wsrm.DeliveryAssuranceMetaData;
import org.jboss.ws.metadata.wsrm.PortMetaData;
import org.jboss.ws.metadata.wsrm.ReliableMessagingMetaData;

/**
 * Reliable messaging policy deployer
 * 
 * @author richard.opalka@jboss.com
 */
public final class WSRMPolicyAssertionDeployer implements AssertionDeployer
{

   private static final String WSRMP_NS = "http://docs.oasis-open.org/ws-rx/wsrmp/200702";
   private static final QName EXACTLY_ONCE = new QName(WSRMP_NS, "ExactlyOnce");
   private static final QName AT_LEAST_ONCE = new QName(WSRMP_NS, "AtLeastOnce");
   private static final QName AT_MOST_ONCE = new QName(WSRMP_NS, "AtMostOnce");
   private static final QName IN_ORDER = new QName(WSRMP_NS, "InOrder");
   private static final String FALSE = "false";
   private static final String TRUE = "true";
   
   /*
    * @see org.jboss.ws.extensions.policy.deployer.domainAssertion.AssertionDeployer#deployClientSide(org.apache.ws.policy.PrimitiveAssertion, org.jboss.ws.metadata.umdm.ExtensibleMetaData)
    */
   public void deployClientSide(PrimitiveAssertion assertion, ExtensibleMetaData extMetaData)
   throws UnsupportedAssertionException
   {
      deploy(assertion, extMetaData);
   }
   
   /*
    * @see org.jboss.ws.extensions.policy.deployer.domainAssertion.AssertionDeployer#deployServerSide(org.apache.ws.policy.PrimitiveAssertion, org.jboss.ws.metadata.umdm.ExtensibleMetaData)
    */
   public void deployServerSide(PrimitiveAssertion assertion, ExtensibleMetaData extMetaData) throws UnsupportedAssertionException
   {
      deploy(assertion, extMetaData);
   }
   
   private static void deploy(PrimitiveAssertion assertion, ExtensibleMetaData extMetaData)
   throws UnsupportedAssertionException
   {
      if (extMetaData instanceof EndpointMetaData)
      {
         EndpointMetaData endpointMD = (EndpointMetaData) extMetaData;
         
         // prepare wsrm metadata
         ReliableMessagingMetaData wsrmMD = endpointMD.getConfig().getReliableMessaging(); 
         if (wsrmMD == null)
         {
            wsrmMD = new ReliableMessagingMetaData();
         }
         
         // construct new port metadata
         PortMetaData portMD = new PortMetaData();
         portMD.setPortName(endpointMD.getPortName());
         List<PrimitiveAssertion> wsrmpAssertions = getWSRMPAssertions(assertion);
         portMD.setDeliveryAssurance(constructDeliveryAssurance(wsrmpAssertions));
         
         // ensure port does not exists yet
         for (PortMetaData pMD : wsrmMD.getPorts())
         {
            assert ! pMD.getPortName().equals(portMD.getPortName());
         }
         
         // set up port WSRMP metadata
         wsrmMD.getPorts().add(portMD);
      }
   }

   private static DeliveryAssuranceMetaData constructDeliveryAssurance(List<PrimitiveAssertion> assertions)
   throws UnsupportedAssertionException
   {
      if (assertions.size() == 1)
      {
         QName assertionQN = assertions.get(0).getName();
         assertIsWSRMPAssertion(assertionQN);
         
         DeliveryAssuranceMetaData deliveryMD = new DeliveryAssuranceMetaData();
         deliveryMD.setInOrder(FALSE);
         deliveryMD.setQuality(assertionQN.getLocalPart());
         return deliveryMD;
      }
      if (assertions.size() == 2)
      {
         QName firstAssertionQN = assertions.get(0).getName();
         assertIsWSRMPAssertion(firstAssertionQN);
         QName secondAssertionQN = assertions.get(1).getName();
         assertIsWSRMPAssertion(secondAssertionQN);
         
         boolean firstIsInOrder = firstAssertionQN.equals(IN_ORDER);
         
         DeliveryAssuranceMetaData deliveryMD = new DeliveryAssuranceMetaData();
         deliveryMD.setInOrder(TRUE);
         if (firstIsInOrder)
         {
            deliveryMD.setQuality(secondAssertionQN.getLocalPart());
         }
         else
         {
            deliveryMD.setQuality(firstAssertionQN.getLocalPart());
         }
         
         return deliveryMD;
      }
      
      throw new IllegalArgumentException();
   }
   
   private static void assertIsWSRMPAssertion(QName assertionQN) throws UnsupportedAssertionException
   {
      if (assertionQN.equals(EXACTLY_ONCE)
         || assertionQN.equals(AT_LEAST_ONCE)
         || assertionQN.equals(AT_MOST_ONCE)
         || assertionQN.equals(IN_ORDER))
      {
         return; // recognized assertion - silently return
      }
      
      throw new UnsupportedAssertionException();
   }
   
   private static List<PrimitiveAssertion> getWSRMPAssertions(PrimitiveAssertion assertion)
   {
      Policy policy = (Policy)assertion.getTerms().get(0);
      XorCompositeAssertion xor = (XorCompositeAssertion)policy.getTerms().get(0);
      AndCompositeAssertion and = (AndCompositeAssertion)xor.getTerms().get(0);
      List<?> primitiveAssertions = and.getTerms();

      List<PrimitiveAssertion> retVal = new LinkedList<PrimitiveAssertion>();
      for (int i = 0; i < primitiveAssertions.size(); i++)
      {
         retVal.add((PrimitiveAssertion)primitiveAssertions.get(i));
      }
      
      return retVal;
   }

}
