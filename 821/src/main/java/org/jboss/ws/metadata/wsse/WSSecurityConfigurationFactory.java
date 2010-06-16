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
package org.jboss.ws.metadata.wsse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import org.jboss.logging.Logger;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.ObjectModelFactory;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.UnmarshallingContext;
import org.xml.sax.Attributes;

/**
 * A JBossXB Object Model Factory that represets a JBoss WS-Security
 * configuration. See the jboss-ws-security_1_0.xsd file for more info.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @version $Revision$
 */
public class WSSecurityConfigurationFactory implements ObjectModelFactory
{

   public static String SERVER_RESOURCE_NAME = "jboss-wsse-server.xml";

   public static String CLIENT_RESOURCE_NAME = "jboss-wsse-client.xml";

   private static HashMap<String, String> options = new HashMap<String, String>(6);

   static
   {
      options.put("key-store-file", "setKeyStoreFile");
      options.put("key-store-type", "setKeyStoreType");
      options.put("key-store-password", "setKeyStorePassword");
      options.put("trust-store-file", "setTrustStoreFile");
      options.put("trust-store-type", "setTrustStoreType");
      options.put("trust-store-password", "setTrustStorePassword");
   }

   // provide logging
   private static final Logger log = Logger.getLogger(WSSecurityConfigurationFactory.class);

   // Hide constructor
   private WSSecurityConfigurationFactory()
   {
   }

   /**
    * Create a new instance of a jaxrpc-mapping factory
    */
   public static WSSecurityConfigurationFactory newInstance()
   {
      return new WSSecurityConfigurationFactory();
   }

   /**
    * Factory method for JavaWsdlMapping
    */
   public WSSecurityConfiguration parse(URL configurationFile) throws IOException
   {
      if (configurationFile == null)
      {
         throw new IllegalArgumentException("URL cannot be null");
      }

      // setup the XML binding Unmarshaller
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      InputStream is = configurationFile.openStream();
      try
      {
         WSSecurityConfiguration configuration = (WSSecurityConfiguration) unmarshaller.unmarshal(is, this, null);
         return configuration;
      }
      catch (JBossXBException e)
      {
         log.error("Could not parse " + configurationFile + ":", e);
         IOException ioex = new IOException("Cannot parse: " + configurationFile);
         Throwable cause = e.getCause();
         if (cause != null)
            ioex.initCause(cause);
         throw ioex;
      }
      finally
      {
         is.close();
      }
   }

   /**
    * This method is called on the factory by the object model builder when the
    * parsing starts.
    */
   public Object newRoot(Object root, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      return new WSSecurityConfiguration();
   }

   public Object completeRoot(Object root, UnmarshallingContext ctx, String uri, String name)
   {
      return root;
   }

   public void setValue(WSSecurityConfiguration configuration, UnmarshallingContext navigator, String namespaceURI,
         String localName, String value)
   {
      log.trace("setValue: [obj=" + configuration + ",value=" + value + "]");
      String method = (String) options.get(localName);
      if (method == null)
         return;

      // Dispatch to propper initializer
      try
      {
         WSSecurityConfiguration.class.getMethod(method, new Class[] {String.class}).invoke(configuration, new Object[]{value});
      }
      catch (Exception e)
      {
         log.error("Could not set option: " + method + " to: " + value, e);
      }
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(WSSecurityConfiguration configuration, UnmarshallingContext navigator, String namespaceURI,
         String localName, Attributes attrs)
   {
      log.trace("newChild: " + localName);
      if ("config".equals(localName))
      {
         return new Config();
      }
      if ("port".equals(localName))
      {
         return new Port(attrs.getValue("", "name"));
      }
      return null;
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(WSSecurityConfiguration configuration, Config defaultConfig, UnmarshallingContext navigator,
         String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + configuration + ",child=" + defaultConfig + "]");
      configuration.setDefaultConfig(defaultConfig);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(WSSecurityConfiguration configuration, Port port, UnmarshallingContext navigator, String namespaceURI,
         String localName)
   {
      log.trace("addChild: [obj=" + configuration + ",child=" + port + "]");
      configuration.addPort(port);
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(Config config, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("newChild: " + localName);
      if ("sign".equals(localName))
      {
         // By default, we alwyas include a timestamp
         boolean includeTimestamp = true;
         String value = attrs.getValue("", "includeTimestamp");
         if (value != null)
            includeTimestamp = (Boolean) SimpleTypeBindings.unmarshal(SimpleTypeBindings.XS_BOOLEAN_NAME, value, null);

         boolean includeFaults = false;
         value = attrs.getValue("", "includeFaults");
         if (value != null)
            includeFaults = (Boolean) SimpleTypeBindings.unmarshal(SimpleTypeBindings.XS_BOOLEAN_NAME, value, null);

         return new Sign(attrs.getValue("", "type"), attrs.getValue("", "alias"), includeTimestamp, includeFaults);
      }
      else if ("encrypt".equals(localName))
      {
         boolean includeFaults = false;
         String value = attrs.getValue("", "includeFaults");
         if (value != null)
            includeFaults = (Boolean) SimpleTypeBindings.unmarshal(SimpleTypeBindings.XS_BOOLEAN_NAME, value, null);

         return new Encrypt(attrs.getValue("", "type"), attrs.getValue("", "alias"), attrs.getValue("", "algorithm"), includeFaults);
      }
      else if ("timestamp".equals(localName))
      {
         return new Timestamp(attrs.getValue("", "ttl"));
      }
      else if ("requires".equals(localName))
      {
         return new Requires();
      }
      else if ("username".equals(localName))
      {
         return new Username();
      }

      return null;
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(Config config, Encrypt encrypt, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + config + ",child=" + encrypt + "]");
      config.setEncrypt(encrypt);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(Config config, Sign sign, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + config + ",child=" + sign + "]");
      config.setSign(sign);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(Config config, Timestamp timestamp, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + config + ",child=" + timestamp + "]");
      config.setTimestamp(timestamp);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(Config config, Username username, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + config + ",child=" + username + "]");
      config.setUsername(username);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(Config config, Requires requires, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + config + ",child=" + requires + "]");
      config.setRequires(requires);
   }


   private Object handleTargets(Object object, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("newChild: " + localName);
      if ("target".equals(localName))
      {
         Target target = new Target(attrs.getValue("", "type"));
         if ("true".equals(attrs.getValue("", "contentOnly")))
            target.setContentOnly(true);

         return target;
      }
      return null;
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(Encrypt encrypt, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      return handleTargets(encrypt, navigator, namespaceURI, localName, attrs);
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(Sign sign, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      return handleTargets(sign, navigator, namespaceURI, localName, attrs);
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(Requires requires, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("newChild: " + localName);
      if ("signature".equals(localName))
      {
         boolean includeFaults = false;
         String value = attrs.getValue("", "includeFaults");
         if (value != null)
            includeFaults = (Boolean) SimpleTypeBindings.unmarshal(SimpleTypeBindings.XS_BOOLEAN_NAME, value, null);

         return new RequireSignature(includeFaults);
      }
      else if ("encryption".equals(localName))
      {
         boolean includeFaults = false;
         String value = attrs.getValue("", "includeFaults");
         if (value != null)
            includeFaults = (Boolean) SimpleTypeBindings.unmarshal(SimpleTypeBindings.XS_BOOLEAN_NAME, value, null);

         return new RequireEncryption(includeFaults);
      }
      else if ("timestamp".equals(localName))
      {
         return new RequireTimestamp(attrs.getValue("", "maxAge"));
      }

      return null;
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(RequireSignature requireSignature, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      return handleTargets(requireSignature, navigator, namespaceURI, localName, attrs);
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(RequireEncryption requireEncryption, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      return handleTargets(requireEncryption, navigator, namespaceURI, localName, attrs);
   }

   public void setValue(Target target, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      log.trace("setValue: [obj=" + target + ",value=" + value + "]");

      target.setValue(value);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(Encrypt encrypt, Target target, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + encrypt + ",child=" + target + "]");
      encrypt.addTarget(target);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(Sign sign, Target target, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + sign + ",child=" + target + "]");
      sign.addTarget(target);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(Requires requires, RequireEncryption requireEncryption, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + requires + ",child=" + requireEncryption + "]");
      requires.setRequireEncryption(requireEncryption);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(Requires requires, RequireSignature requireSignature, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + requires + ",child=" + requireSignature + "]");
      requires.setRequireSignature(requireSignature);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(Requires requires, RequireTimestamp requireTimestamp, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + requires + ",child=" + requireTimestamp + "]");
      requires.setRequireTimestamp(requireTimestamp);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(RequireEncryption requireEncryption, Target target, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + requireEncryption + ",child=" + target + "]");
      requireEncryption.addTarget(target);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(RequireSignature requireSignature, Target target, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + requireSignature + ",child=" + target + "]");
      requireSignature.addTarget(target);
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(Port port, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("newChild: " + localName);
      if ("operation".equals(localName))
      {
         return new Operation(attrs.getValue("", "name"));
      }
      else if ("config".equals(localName))
      {
         return new Config();
      }
      return null;
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(Port port, Operation operation, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + port + ",child=" + operation + "]");
      port.addOperation(operation);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(Port port, Config config, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + port + ",child=" + config + "]");
      port.setDefaultConfig(config);
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(Operation operation, UnmarshallingContext navigator, String namespaceURI, String localName,
         Attributes attrs)
   {
      log.trace("newChild: " + localName);
      if ("config".equals(localName))
      {
         return new Config();
      }
      return null;
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(Operation operation, Config config, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + operation + ",child=" + config + "]");
      operation.setConfig(config);
   }
}