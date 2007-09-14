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
package org.jboss.ws.extensions.policy.deployer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.ws.policy.AndCompositeAssertion;
import org.apache.ws.policy.Assertion;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.apache.ws.policy.XorCompositeAssertion;
import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.extensions.policy.deployer.domainAssertion.AssertionDeployer;
import org.jboss.ws.extensions.policy.deployer.domainAssertion.NopAssertionDeployer;
import org.jboss.ws.extensions.policy.deployer.domainAssertion.WSSecurityAssertionDeployer;
import org.jboss.ws.extensions.policy.deployer.exceptions.UnsupportedAlternativeException;
import org.jboss.ws.extensions.policy.deployer.exceptions.UnsupportedAssertionException;
import org.jboss.ws.extensions.policy.deployer.exceptions.UnsupportedPolicyException;
import org.jboss.ws.metadata.umdm.ExtensibleMetaData;

/**
 * @author Stefano Maestri <mailto:stefano.maestri@javalinux.it>
 *
 * since 27/04/2007
 */
public class PolicyDeployer
{
   private final static Logger log = Logger.getLogger(PolicyDeployer.class);
   private static PolicyDeployer me;
   private Map<String, Class<? extends AssertionDeployer>> domainDeployerMap = new HashMap<String, Class<? extends AssertionDeployer>>();

   static
   {
      me = new PolicyDeployer();
      me.domainDeployerMap.put("http://www.jboss.com/ws-security/schema/jboss-ws-security_1_0.xsd", WSSecurityAssertionDeployer.class);
      me.domainDeployerMap.put("http://schemas.xmlsoap.org/ws/2005/02/rm/policy", NopAssertionDeployer.class);
      me.domainDeployerMap.put("http://docs.oasis-open.org/ws-rx/wsrmp/200702", NopAssertionDeployer.class);
   }

   //hide constructor
   PolicyDeployer()
   {
   }

   public static PolicyDeployer getInstance()
   {
      return me;
   }

   //for test
   public static PolicyDeployer newInstance(Map<String, Class<? extends AssertionDeployer>> customDomainMap)
   {
      PolicyDeployer instance = new PolicyDeployer();
      instance.domainDeployerMap = customDomainMap;
      return instance;

   }

   //for tools
   public static PolicyDeployer newInstanceForTools()
   {
      PolicyDeployer instance = new PolicyDeployer();
      instance.domainDeployerMap.put("http://www.jboss.com/ws-security/schema/jboss-ws-security_1_0.xsd", NopAssertionDeployer.class);
      instance.domainDeployerMap.put("http://schemas.xmlsoap.org/ws/2005/02/rm/policy", NopAssertionDeployer.class);
      instance.domainDeployerMap.put("http://docs.oasis-open.org/ws-rx/wsrmp/200702", NopAssertionDeployer.class);
      return instance;

   }

   @SuppressWarnings("unchecked")
   public Policy deployServerside(Policy policy, ExtensibleMetaData extMetaData) throws UnsupportedPolicyException
   {
      if (policy == null)
         throw new WSException("Cannot deploy null policy!");

      List<Assertion> returnedPolicyTerms = new LinkedList<Assertion>();

      if (!policy.isNormalized())
      {
         policy = (Policy)policy.normalize();
      }

      //in normal form we have just one wsp:ExactlyOne element containg unbounded wsp:All (alternative)
      XorCompositeAssertion exactlyOne = (XorCompositeAssertion)policy.getTerms().get(0);
      log.debug("####" + exactlyOne.getClass());
      log.debug("####" + exactlyOne.getTerms());
      for (AndCompositeAssertion alternative : (List<AndCompositeAssertion>)exactlyOne.getTerms())
      {
         log.debug("alternative");
         try
         {
            deployAlternativeServerSide(alternative, extMetaData);
            returnedPolicyTerms.add(alternative);
         }
         catch (UnsupportedAlternativeException e)
         {
            log.debug("Unsupported Alternative");
            //policy is unsupported only if it have all alternative unsupported
         }

      }
      if (returnedPolicyTerms.size() == 0)
      {
         if (log.isDebugEnabled())
         {
            log.debug("XorComposite zero element...Policy not supported");
         }
         throw new UnsupportedPolicyException();
      }
      policy.getTerms().clear();
      policy.addTerms(returnedPolicyTerms);
      return policy;
   }

   /**
    * Policy deployer method for client side: delegates to the right domain deployer
    * and fails if one or more policy assertions are not supported.
    * 
    * @param policy
    * @param extMetaData
    * @throws UnsupportedPolicyException
    */
   @SuppressWarnings("unchecked")
   public void deployClientSide(Policy policy, ExtensibleMetaData extMetaData) throws UnsupportedPolicyException
   {
      if (policy == null)
         throw new WSException("Cannot deploy null policy!");

      if (!policy.isNormalized())
      {
         policy = (Policy)policy.normalize();
      }
      //in normal form we have just one wsp:ExactlyOne element containg unbounded wsp:All (alternative)
      XorCompositeAssertion exactlyOne = (XorCompositeAssertion)policy.getTerms().get(0);
      for (AndCompositeAssertion alternative : (List<AndCompositeAssertion>)exactlyOne.getTerms())
      {
         for (Assertion assertion : (List<Assertion>)alternative.getTerms())
         {
            if (assertion instanceof PrimitiveAssertion)
            {
               try
               {
                  deployAssertionClientSide((PrimitiveAssertion)assertion, extMetaData);
               }
               catch (UnsupportedAssertionException e)
               {
                  log.error("Unsupported assertion!");
                  throw new UnsupportedPolicyException();
               }
            }
            else if (assertion instanceof Policy) //inner policy to be verified
            {
               deployClientSide((Policy)assertion, extMetaData);
            }
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void deployAlternativeServerSide(AndCompositeAssertion alternative, ExtensibleMetaData extMetaData) throws UnsupportedAlternativeException
   {
      for (Assertion assertion : (List<Assertion>)alternative.getTerms())
      {

         try
         {
            if (assertion instanceof PrimitiveAssertion)
            {
               deployAssertionServerSide((PrimitiveAssertion)assertion, extMetaData);
            }
            else if (assertion instanceof Policy) //inner policy to be verified
            {
               deployServerside((Policy)assertion, extMetaData);
            }
            else
            {
               if (log.isDebugEnabled())
               {
                  log.debug("Unknown Alternative type....Alternative not supported");
               }
               throw new UnsupportedAlternativeException();
            }

         }
         catch (UnsupportedAssertionException e)
         {
            //If there is al least one unsupported assertion the alternative isn't supported
            throw new UnsupportedAlternativeException();
         }
         catch (UnsupportedPolicyException ep)
         {
            //If there is al least one unsupported assertion the alternative isn't supported
            throw new UnsupportedAlternativeException();
         }
      }
   }

   private void deployAssertionServerSide(PrimitiveAssertion assertion, ExtensibleMetaData extMetaData) throws UnsupportedAssertionException
   {
      AssertionDeployer deployer = getDomainDeployerInstance(assertion.getName().getNamespaceURI());
      deployer.deployServerSide(assertion, extMetaData);
   }

   private void deployAssertionClientSide(PrimitiveAssertion assertion, ExtensibleMetaData extMetaData) throws UnsupportedAssertionException
   {
      AssertionDeployer deployer = getDomainDeployerInstance(assertion.getName().getNamespaceURI());
      deployer.deployClientSide(assertion, extMetaData);
   }

   /**
    * @param namespace to be used to get the associated assertion deployer
    * @return AssertionDeployer instance
    * @throws UnsupportedAssertionException
    */
   private AssertionDeployer getDomainDeployerInstance(String namespace) throws UnsupportedAssertionException
   {
      try
      {
         if (!domainDeployerMap.containsKey(namespace))
         {
            if (log.isDebugEnabled())
            {
               log.debug("Unknown namespace: '" + namespace + "' - Assertion not supported");
            }
            throw new UnsupportedAssertionException();
         }
         return domainDeployerMap.get(namespace).newInstance();
      }
      catch (Exception e)
      {
         throw new UnsupportedAssertionException();
      }
   }
}
