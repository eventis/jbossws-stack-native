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
package org.jboss.ws.tools.helpers;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.util.xml.DOMUtils;
import org.jboss.util.xml.DOMWriter;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.jaxrpc.Style;
import org.jboss.ws.metadata.EndpointMetaData;
import org.jboss.ws.metadata.OperationMetaData;
import org.jboss.ws.metadata.ParameterMetaData;
import org.jboss.ws.metadata.ServiceMetaData;
import org.jboss.ws.metadata.UnifiedMetaData;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaXmlTypeMapping;
import org.jboss.ws.metadata.wsdl.NCName;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLException;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.tools.Configuration;
import org.jboss.ws.tools.JavaToWSDL;
import org.jboss.ws.tools.JavaWriter;
import org.jboss.ws.tools.NamespacePackageMapping;
import org.jboss.ws.tools.WSDLToJava;
import org.jboss.ws.tools.WSDotXMLCreator;
import org.jboss.ws.tools.Configuration.GlobalConfig;
import org.jboss.ws.tools.Configuration.JavaToWSDLConfig;
import org.jboss.ws.tools.Configuration.WSDLToJavaConfig;
import org.jboss.ws.tools.XSDTypeToJava.VAR;
import org.jboss.ws.tools.client.ServiceCreator;
import org.jboss.ws.tools.interfaces.WSDotXMLCreatorIntf;
import org.jboss.ws.tools.mapping.MappingFileGenerator;
import org.jboss.ws.utils.IOUtils;
import org.jboss.ws.utils.JavaUtils;

/**
 *  Helper class used by the command line tool "jbossws"
 *  and ant task "wstools"
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 19, 2005
 */
public class ToolsHelper
{
   private static Logger log = Logger.getLogger(ToolsHelper.class);

   /**
    * Java To WSDL Generation [Serverside Generation]
    *
    * @param config
    * @param outDir
    * @throws IOException
    */
   public void handleJavaToWSDLGeneration(Configuration config, String outDir) throws IOException
   {
      JavaToWSDLConfig j2wc = config.getJavaToWSDLConfig(false);
      JavaToWSDL jwsdl = new JavaToWSDL(Constants.NS_WSDL11);
      jwsdl.setServiceName(j2wc.serviceName);
      jwsdl.setTargetNamespace(j2wc.targetNamespace);
      jwsdl.setTypeNamespace(j2wc.typeNamespace);
      jwsdl.setOperationMap(j2wc.operations);

      if ("document".equals(j2wc.wsdlStyle))
         jwsdl.setStyle(Style.DOCUMENT);
      else if ("rpc".equals(j2wc.wsdlStyle))
         jwsdl.setStyle(Style.RPC);
      else throw new WSException("Unrecognized Style:" + j2wc.wsdlStyle);

      if ("wrapped".equals(j2wc.parameterStyle))
         jwsdl.setParameterStyle(ParameterStyle.WRAPPED);
      else if ("bare".equals(j2wc.parameterStyle))
         jwsdl.setParameterStyle(ParameterStyle.BARE);
      else throw new WSException("Unrecognized Parameter Style:" + j2wc.parameterStyle);

      Class endpointClass = loadClass(j2wc.endpointName);

      if (endpointClass == null)
         throw new WSException("Endpoint " + j2wc.endpointName + " cannot be loaded");

      //Take care of passing global config details
      GlobalConfig gcfg = config.getGlobalConfig(false);
      if (gcfg != null)
      {
         if (gcfg.packageNamespaceMap != null)
            jwsdl.setPackageNamespaceMap(gcfg.packageNamespaceMap);
      }
      WSDLDefinitions wsdl = jwsdl.generate(endpointClass);
      //Create the WSDL Directory
      createDir(outDir + "/wsdl");
      String wsdlPath = outDir + "/wsdl/" + j2wc.serviceName + ".wsdl";
      //Generate the WSDL
      Writer fw = IOUtils.getCharsetFileWriter(new File(wsdlPath), Constants.DEFAULT_XML_CHARSET);
      wsdl.write(fw, Constants.DEFAULT_XML_CHARSET);
      fw.close();

      //Generate the Mapping File
      if (j2wc.mappingFileNeeded)
      {
         UnifiedMetaData unifiedMetaData = jwsdl.getUnifiedMetaData();
         JavaWsdlMapping mapping = jwsdl.getJavaWsdlMapping();

         createWrapperTypes(j2wc, outDir, unifiedMetaData, mapping, endpointClass);
         Writer writer = IOUtils.getCharsetFileWriter(new File(outDir + "/" + j2wc.mappingFileName), Constants.DEFAULT_XML_CHARSET);
         writer.write(Constants.XML_HEADER);
         writer.write(DOMWriter.printNode(DOMUtils.parse(mapping.serialize()), true));
         writer.close();
      }

      //         MappingFileGenerator mgf = new MappingFileGenerator(wsdl,jwsdl.getTypeMapping() );
      //         mgf.setPackageName(endpointClass.getPackage().getName());
      //         mgf.setServiceEndpointInterface(endpointClass);
      //         mgf.setServiceName(j2wc.serviceName);
      //         mgf.setTypeNamespace(j2wc.typeNamespace);
      //         //mgf.generate();
      //         JavaWsdlMapping jwm = mgf.generate();
      //         fw = new FileWriter(outDir + "/" + j2wc.mappingFileName);
      //         fw.write(DOMWriter.printNode(DOMUtils.parse(jwm.serialize()), true));
      //         fw.close();
      //
      //         //Generate the Request/Response structures also
      //         if(j2wc.wsdlStyle.equals("document"))
      //         {
      //            String seiName = endpointClass.getName();
      //            mgf.generateJavaSourceFileForRequestResponseStruct(new File(outDir),
      //                         jwm.getServiceEndpointInterfaceMapping(seiName),
      //                         wsdl.getWsdlTypes().getSchemaModel(),j2wc.typeNamespace );
      //         }
      //      }

      //Generate the webservices.xml file
      if (j2wc.wsxmlFileNeeded)
      {
         WSDotXMLCreatorIntf wscr = new WSDotXMLCreator();
         wscr.setTargetNamespace(j2wc.targetNamespace);
         //wscr.setLocation(new File(outDir).toURL());
         wscr.setSeiName(j2wc.endpointName);
         wscr.setServiceName(j2wc.serviceName);
         //Get the portname from wsdl definitions
         WSDLService wsdlService = wsdl.getService(new NCName(j2wc.serviceName));
         String portName = wsdlService.getEndpoints()[0].getName().toString();
         //wscr.setPortName(j2wc.serviceName + "Port");
         wscr.setPortName(portName);
         //wscr.setMappingFileName(j2wc.mappingFileName);
         if (j2wc.servletLink != null)
         {
            wscr.setMappingFile("WEB-INF/" + j2wc.mappingFileName);
            wscr.setWsdlFile("WEB-INF/wsdl/" + j2wc.serviceName + ".wsdl");
            wscr.setServletLink(j2wc.servletLink);
         }
         else
         {
            wscr.setMappingFile("META-INF/" + j2wc.mappingFileName);
            wscr.setWsdlFile("META-INF/wsdl/" + j2wc.serviceName + ".wsdl");
            wscr.setEjbLink(j2wc.ejbLink);
         }
         wscr.setAppend(j2wc.wsxmlFileAppend);
         wscr.generateWSXMLDescriptor(new File(outDir + "/webservices.xml"));
      }
   }

   private void createWrapperTypes(JavaToWSDLConfig j2wc, String outDir, UnifiedMetaData unifiedMetaData, JavaWsdlMapping mapping, Class endpointClass)
         throws IOException
   {
      Map<QName, JavaXmlTypeMapping> index = indexMappingTypes(mapping);

      EndpointMetaData endpoint = null;
      for (ServiceMetaData service : unifiedMetaData.getServices())
      {
         endpoint = service.getEndpointByServiceEndpointInterface(j2wc.endpointName);
         if (endpoint != null)
            break;
      }

      if (endpoint == null)
         throw new WSException("Could not find endpoint in metadata: " + j2wc.endpointName);

      if (endpoint.getStyle() != Style.DOCUMENT || endpoint.getParameterStyle() != ParameterStyle.WRAPPED)
         return;

      String packageName = endpointClass.getPackage().getName();
      ClassLoader classLoader = unifiedMetaData.getClassLoader();

      for (OperationMetaData operation : endpoint.getOperations())
      {
         for (ParameterMetaData parameter : operation.getParameters())
         {
            String name = endpointClass.getSimpleName() + "_" + operation.getXmlName().getLocalPart() + "_RequestStruct";
            createWrapperType(parameter, name, packageName, index, classLoader, outDir);
         }

         ParameterMetaData returnParameter = operation.getReturnParameter();
         if (returnParameter != null)
         {
            String name = endpointClass.getSimpleName() + "_" + operation.getXmlName().getLocalPart() + "_ResponseStruct";
            createWrapperType(returnParameter, name, packageName, index, classLoader, outDir);
         }
      }

      // Filter generated wrapper type package (org.jboss.ws)
      mapping.removePackageMappingsByPackageType("org.jboss.ws.jaxrpc");
   }

   private void createWrapperType(ParameterMetaData parameter, String name, String packageName, Map<QName, JavaXmlTypeMapping> mappingIndex, ClassLoader classLoader,
         String outDir) throws IOException
   {
      List<String> wrappedVariables = parameter.getWrappedVariables();

      if (wrappedVariables == null)
         return;

      List<VAR> vars = new ArrayList<VAR>();
      List<String> wrappedTypes = parameter.getWrappedTypes();
      for (int i = 0; i < wrappedVariables.size(); i++)
      {
         String typeName = JavaUtils.convertJVMNameToSourceName(wrappedTypes.get(i), classLoader);
         vars.add(new VAR(wrappedVariables.get(i), typeName, false));
      }

      JavaWriter writer = new JavaWriter();
      writer.createJavaFile(new File(outDir), name + ".java", packageName, vars, null, null, false, null);

      JavaXmlTypeMapping type = mappingIndex.get(parameter.getXmlType());
      if (type == null)
         throw new WSException("JAX-RPC mapping metadata is missing a wrapper type: " + parameter.getXmlType());

      type.setJavaType(packageName + "." + name);
   }

   private Map<QName, JavaXmlTypeMapping> indexMappingTypes(JavaWsdlMapping mapping)
   {
      Map<QName, JavaXmlTypeMapping> index = new HashMap<QName, JavaXmlTypeMapping>();
      for (JavaXmlTypeMapping type : mapping.getJavaXmlTypeMappings())
      {
         QName qname = type.getRootTypeQName();
         if (qname == null)
            continue;

         index.put(qname, type);
      }

      return index;
   }

   /**
    * Client Side Generation [WSDL To Java]
    *
    * @param config
    * @param outDir
    */
   public void handleWSDLToJavaGeneration(Configuration config, String outDir)
   {
      WSDLToJavaConfig w2jc = config.getWSDLToJavaConfig(false);
      GlobalConfig glc = config.getGlobalConfig(false);

      WSDLToJava wsdlToJava = new WSDLToJava();
      wsdlToJava.setTypeMapping(new LiteralTypeMapping());

      WSDLDefinitions wsdl = null;
      try
      {
         URL wsdlURL = null;
         try
         {
            wsdlURL = new URL(w2jc.wsdlLocation);
         }
         catch (MalformedURLException e)
         {
            // ignore
         }

         if (wsdlURL == null)
         {
            File wsdlFile = new File(w2jc.wsdlLocation);
            if (wsdlFile.exists())
            {
               wsdlURL = wsdlFile.toURL();
            }
         }

         if (wsdlURL == null)
         {
            ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
            wsdlURL = ctxLoader.getResource(w2jc.wsdlLocation);
         }

         if (wsdlURL == null)
            throw new IllegalArgumentException("Cannot load wsdl: " + w2jc.wsdlLocation);

         wsdl = wsdlToJava.convertWSDL2Java(wsdlURL);
         if (glc != null)
            wsdlToJava.setPackageNamespaceMap(glc.packageNamespaceMap);

         wsdlToJava.setUnwrap(w2jc.unwrap);
         wsdlToJava.generateSEI(wsdl, new File(outDir));

         //Generate the Service File
         this.generateServiceFile(getPackageName(wsdl, glc), wsdl, outDir);
         //Generate the Mapping File
         if (w2jc.mappingFileNeeded)
         {
            MappingFileGenerator mgf = new MappingFileGenerator(wsdl, new LiteralTypeMapping());
            mgf.setPackageName(getPackageName(wsdl, glc));
            mgf.setServiceName(wsdl.getServices()[0].getName().toString());
            mgf.setUnwrap(w2jc.unwrap);
            //mgf.generate();

            JavaWsdlMapping jwm = mgf.generate();
            Writer writer = IOUtils.getCharsetFileWriter(new File(outDir + "/" + w2jc.mappingFileName), Constants.DEFAULT_XML_CHARSET);
            writer.write(Constants.XML_HEADER);
            writer.write(DOMWriter.printNode(DOMUtils.parse(jwm.serialize()), true));
            writer.close();
         }
      }
      catch (MalformedURLException e)
      {
         throw new WSException(e);
      }
      catch (WSDLException e)
      {
         throw new WSException(e);
      }
      catch (IOException e)
      {
         throw new WSException(e);
      }
   }

   //PRIVATE METHODS
   private Class loadClass(String cls)
   {
      Class clazz = null;
      try
      {
         clazz = Thread.currentThread().getContextClassLoader().loadClass(cls);
      }
      catch (ClassNotFoundException e)
      {
         log.error("Cannot load endpoint:" + e.getLocalizedMessage());
      }
      return clazz;
   }

   private void generateServiceFile(String packageName, WSDLDefinitions wsdl, String location) throws IOException
   {
      ServiceCreator sc = new ServiceCreator();
      sc.setPackageName(packageName);
      sc.setDirLocation(new File(location));
      sc.setWsdl(wsdl);
      sc.createServiceDescriptor();
   }

   private String getPackageName(WSDLDefinitions wsdl, GlobalConfig glc)
   {
      String targetNamespace = wsdl.getTargetNamespace();
      //Get it from global config
      if (glc != null && glc.packageNamespaceMap != null)
      {
         Map<String, String> map = glc.packageNamespaceMap;
         Iterator iter = map.keySet().iterator();
         while (iter.hasNext())
         {
            String pkg = (String)iter.next();
            String ns = map.get(pkg);
            if (ns.equals(targetNamespace))
               return pkg;
         }
      }

      return NamespacePackageMapping.getJavaPackageName(wsdl.getTargetNamespace());
   }

   private void createDir(String path)
   {
      File file = new File(path);
      if (file.exists() == false)
         file.mkdirs();
   }
}