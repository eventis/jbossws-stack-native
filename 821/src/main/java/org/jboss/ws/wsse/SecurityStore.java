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
package org.jboss.ws.wsse;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import org.jboss.logging.Logger;

/**
 * <code>SecurityStore</code> holds and loads the keystore and truststore required for encyption and signing.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @author <a href="mailto:jason.greene@jboss.com">Magesh Kumar B</a>
 * @version $Revision$
 */
public class SecurityStore
{
   private static Logger log = Logger.getLogger(SecurityStore.class);

   private KeyStore keyStore;

   private String keyStorePassword;

   private KeyStore trustStore;

   private String trustStorePassword;

   public SecurityStore() throws WSSecurityException
   {
      this(null, null, null, null, null, null);
   }

   public SecurityStore(URL keyStoreURL, String keyStoreType, String keyStorePassword) throws WSSecurityException
   {
      loadKeyStore(keyStoreURL, keyStoreType, keyStorePassword);
      loadTrustStore(keyStoreURL, keyStoreType, keyStorePassword);
   }

   public SecurityStore(URL keyStoreURL, String keyStoreType, String keyStorePassword, URL trustStoreURL, String trustStoreType, String trustStorePassword)
         throws WSSecurityException
   {
      loadKeyStore(keyStoreURL, keyStoreType, keyStorePassword);
      loadTrustStore(trustStoreURL, trustStoreType, trustStorePassword);
   }

   private void loadKeyStore(URL keyStoreURL, String keyStoreType, String keyStorePassword) throws WSSecurityException
   {
      if (keyStorePassword == null)
         keyStorePassword = System.getProperty("org.jboss.ws.wsse.keyStorePassword");

      keyStore = loadStore("org.jboss.ws.wsse.keyStore", "Keystore", keyStoreURL, keyStoreType, keyStorePassword);
      this.keyStorePassword = keyStorePassword;
   }

   private void loadTrustStore(URL trustStoreURL, String trustStoreType, String trustStorePassword) throws WSSecurityException
   {
      if (trustStorePassword == null)
         trustStorePassword = System.getProperty("org.jboss.ws.wsse.trustStorePassword");

      trustStore = loadStore("org.jboss.ws.wsse.trustStore", "Truststore", trustStoreURL, trustStoreType, trustStorePassword);
      this.trustStorePassword = trustStorePassword;
   }

   private KeyStore loadStore(String property, String type, URL storeURL, String storeType, String storePassword) throws WSSecurityException
   {
      if (storeURL == null)
      {
         String defaultStore = System.getProperty(property);
         if (defaultStore == null)
         {
            return null;
         }

         File storeFile = new File(defaultStore);
         try
         {
            storeURL = storeFile.toURL();
         }
         catch (MalformedURLException e)
         {
            throw new WSSecurityException("Problems loading " + type + ": " + e.getMessage(), e);
         }
      }

      if (storeType == null)
         storeType = System.getProperty(property + "Type");
      if (storeType == null)
         storeType = "jks";

      storePassword = getPassword(storePassword);
      try
      {
         log.debug("loadStore: " + storeURL);
         InputStream stream = storeURL.openStream();
         KeyStore keyStore = KeyStore.getInstance(storeType);
         keyStore.load(stream, storePassword.toCharArray());

         return keyStore;
      }
      catch (Exception e)
      {
         throw new WSSecurityException("Problems loading " + type + ": " + e.getMessage(), e);
      }
   }

   /**
    * This method examines the password for the presence of a encryption algorithm, if found
    * decrypts and returns the password, else returns the password as is.
    */
   private String getPassword(String password) throws WSSecurityException
   {
      if (password.charAt(0) == '{')
      {
         StringTokenizer tokenizer = new StringTokenizer(password, "{}");
         String keyStorePasswordCmdType = tokenizer.nextToken();
         String keyStorePasswordCmd = tokenizer.nextToken();
         if (keyStorePasswordCmdType.equals("EXT"))
         {
            password = execPasswordCmd(keyStorePasswordCmd);
         }
         else if (keyStorePasswordCmdType.equals("CLASS"))
         {
            password = invokePasswordClass(keyStorePasswordCmd);
         }
         else
         {
            throw new WSSecurityException("Unknown keyStorePasswordCmdType: " + keyStorePasswordCmdType);
         }
      }

      return password;
   }

   private String execPasswordCmd(String keyStorePasswordCmd) throws WSSecurityException
   {
      log.debug("Executing command: " + keyStorePasswordCmd);
      try
      {
         Runtime rt = Runtime.getRuntime();
         Process p = rt.exec(keyStorePasswordCmd);
         InputStream stdin = p.getInputStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
         String password = reader.readLine();
         stdin.close();
         int exitCode = p.waitFor();
         log.debug("Command exited with: " + exitCode);
         return password;
      }
      catch (Exception e)
      {
         throw new WSSecurityException("Problems executing password command: " + keyStorePasswordCmd, e);
      }
   }

   private String invokePasswordClass(String keyStorePasswordCmd) throws WSSecurityException
   {
      String password = null;
      String classname = keyStorePasswordCmd;
      String ctorArg = null;
      int colon = keyStorePasswordCmd.indexOf(':');
      if (colon > 0)
      {
         classname = keyStorePasswordCmd.substring(0, colon);
         ctorArg = keyStorePasswordCmd.substring(colon + 1);
      }
      log.debug("Loading class: " + classname + ", ctorArg=" + ctorArg);
      try
      {
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         Class c = loader.loadClass(classname);
         Object instance = null;
         if (ctorArg != null)
         {
            Class[] sig = { String.class };
            Constructor ctor = c.getConstructor(sig);
            Object[] args = { ctorArg };
            instance = ctor.newInstance(args);
         }
         else
         {
            instance = c.newInstance();
         }
         try
         {
            log.debug("Checking for toCharArray");
            Class[] sig = {};
            Method toCharArray = c.getMethod("toCharArray", sig);
            Object[] args = {};
            log.debug("Invoking toCharArray");
            password = new String((char[])toCharArray.invoke(instance, args));
         }
         catch (NoSuchMethodException e)
         {
            log.debug("No toCharArray found, invoking toString");
            password = instance.toString();
         }
      }
      catch (Exception e)
      {
         throw new WSSecurityException("Problems loading or invoking Password class : " + classname, e);
      }
      return password;
   }

   public static byte[] getSubjectKeyIdentifier(X509Certificate cert)
   {
      // Maybee we should make one ourselves if it isn't there?
      byte[] encoded = cert.getExtensionValue("2.5.29.14");
      if (encoded == null)
         return null;

      // We need to skip 4 bytes [(OCTET STRING) (LENGTH)[(OCTET STRING) (LENGTH) (Actual data)]]
      int trunc = encoded.length - 4;

      byte[] identifier = new byte[trunc];
      System.arraycopy(encoded, 4, identifier, 0, trunc);

      return identifier;
   }

   public X509Certificate getCertificate(String alias) throws WSSecurityException
   {
      if (keyStore == null)
      {
         throw new WSSecurityException("KeyStore not set.");
      }

      X509Certificate cert;
      try
      {
         cert = (X509Certificate)keyStore.getCertificate(alias);
      }
      catch (Exception e)
      {
         throw new WSSecurityException("Problems retrieving cert: " + e.getMessage(), e);
      }

      if (cert == null)
         throw new WSSecurityException("Certificate (" + alias + ") not in keystore");

      return cert;
   }

   public X509Certificate getCertificateBySubjectKeyIdentifier(byte[] identifier) throws WSSecurityException
   {
      if (identifier == null)
         return null;

      if (keyStore == null)
      {
         throw new WSSecurityException("KeyStore not set.");
      }

      try
      {
         Enumeration i = keyStore.aliases();

         while (i.hasMoreElements())
         {
            String alias = (String)i.nextElement();
            Certificate cert = keyStore.getCertificate(alias);
            if (!(cert instanceof X509Certificate))
               continue;

            byte[] subjectKeyIdentifier = getSubjectKeyIdentifier((X509Certificate)cert);
            if (subjectKeyIdentifier == null)
               continue;

            if (Arrays.equals(identifier, subjectKeyIdentifier))
               return (X509Certificate)cert;
         }
      }
      catch (KeyStoreException e)
      {
         throw new WSSecurityException("Problems retrieving cert: " + e.getMessage(), e);
      }

      return null;
   }

   public X509Certificate getCertificateByIssuerSerial(String issuer, String serial) throws WSSecurityException
   {
      if (keyStore == null)
      {
         throw new WSSecurityException("KeyStore not set.");
      }

      try
      {
         Enumeration i = keyStore.aliases();

         while (i.hasMoreElements())
         {
            String alias = (String)i.nextElement();
            Certificate cert = keyStore.getCertificate(alias);
            if (!(cert instanceof X509Certificate))
               continue;

            X509Certificate x509 = (X509Certificate)cert;
            if (issuer.equals(x509.getIssuerDN().toString()) && serial.equals(x509.getSerialNumber().toString()))
               return x509;
         }
      }
      catch (KeyStoreException e)
      {
         throw new WSSecurityException("Problems retrieving cert: " + e.getMessage(), e);
      }

      return null;
   }

   public PrivateKey getPrivateKey(String alias) throws WSSecurityException
   {
      if (keyStore == null)
      {
         throw new WSSecurityException("KeyStore not set.");
      }

      PrivateKey key;
      try
      {
         key = (PrivateKey)keyStore.getKey(alias, getPassword(keyStorePassword).toCharArray());
      }
      catch (Exception e)
      {
         throw new WSSecurityException("Problems retrieving private key: " + e.getMessage(), e);
      }

      if (key == null)
         throw new WSSecurityException("Private key (" + alias + ") not in keystore");

      return key;
   }

   public PrivateKey getPrivateKey(X509Certificate cert) throws WSSecurityException
   {
      if (keyStore == null)
      {
         throw new WSSecurityException("KeyStore not set.");
      }

      try
      {
         String alias = keyStore.getCertificateAlias(cert);
         return getPrivateKey(alias);
      }
      catch (Exception e)
      {
         throw new WSSecurityException("Problems retrieving private key: " + e.getMessage(), e);
      }
   }

   public void validateCertificate(X509Certificate cert) throws WSSecurityException
   {
      try
      {
         cert.checkValidity();
      }
      catch (Exception e)
      {
         log.debug("Certificate is invalid", e);
         throw new FailedAuthenticationException();
      }

      if (keyStore == null)
      {
         throw new WSSecurityException("TrustStore not set.");
      }

      // Check for the exact entry in the truststore first, then fallback to a CA check
      try
      {
         if (trustStore.getCertificateAlias(cert) != null)
         {
            return;
         }
      }
      catch (KeyStoreException e)
      {
         throw new WSSecurityException("Problems searching truststore", e);
      }

      List list = new ArrayList(1);
      list.add(cert);

      CertPath cp;
      CertPathValidator cpv;
      PKIXParameters parameters;

      try
      {
         cp = CertificateFactory.getInstance("X.509").generateCertPath(list);
         cpv = CertPathValidator.getInstance("PKIX");
         parameters = new PKIXParameters(trustStore);

         // We currently don't support CRLs
         parameters.setRevocationEnabled(false);
      }
      catch (Exception e)
      {
         throw new WSSecurityException("Problems setting up certificate validation", e);
      }

      try
      {
         cpv.validate(cp, parameters);
      }
      catch (CertPathValidatorException cpve)
      {
         log.debug("Certificate is invalid:", cpve);
         throw new FailedAuthenticationException();
      }
      catch (InvalidAlgorithmParameterException e)
      {
         throw new WSSecurityException("Problems setting up certificate validation", e);
      }
   }
}