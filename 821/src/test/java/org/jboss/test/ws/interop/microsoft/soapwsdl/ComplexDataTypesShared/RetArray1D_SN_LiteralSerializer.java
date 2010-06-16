// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared;

import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.encoding.literal.*;
import com.sun.xml.rpc.encoding.literal.DetailFragmentDeserializer;
import com.sun.xml.rpc.encoding.simpletype.*;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.encoding.soap.SOAP12Constants;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;

public class RetArray1D_SN_LiteralSerializer extends LiteralObjectSerializerBase implements Initializable  {
    private static final javax.xml.namespace.QName ns3_inArray1D_SN_QNAME = new QName("http://tempuri.org/", "inArray1D_SN");
    private static final javax.xml.namespace.QName ns5_ArrayOfPerson_TYPE_QNAME = new QName("http://schemas.datacontract.org/2004/07/XwsInterop.SoapWsdl.ComplexDataTypes.XmlFormatter.Service.Indigo", "ArrayOfPerson");
    private CombinedSerializer ns5_myArrayOfPerson_LiteralSerializer;
    
    public RetArray1D_SN_LiteralSerializer(javax.xml.namespace.QName type, java.lang.String encodingStyle) {
        this(type, encodingStyle, false);
    }
    
    public RetArray1D_SN_LiteralSerializer(javax.xml.namespace.QName type, java.lang.String encodingStyle, boolean encodeType) {
        super(type, true, encodingStyle, encodeType);
    }
    
    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        ns5_myArrayOfPerson_LiteralSerializer = (CombinedSerializer)registry.getSerializer("", org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfPerson.class, ns5_ArrayOfPerson_TYPE_QNAME);
    }
    
    public java.lang.Object doDeserialize(XMLReader reader,
        SOAPDeserializationContext context) throws java.lang.Exception {
        org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArray1D_SN instance = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArray1D_SN();
        java.lang.Object member=null;
        javax.xml.namespace.QName elementName;
        java.util.List values;
        java.lang.Object value;
        
        reader.nextElementContent();
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns3_inArray1D_SN_QNAME)) {
                member = ns5_myArrayOfPerson_LiteralSerializer.deserialize(ns3_inArray1D_SN_QNAME, reader, context);
                instance.setInArray1D_SN((org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfPerson)member);
                reader.nextElementContent();
            }
        }
        
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        return (java.lang.Object)instance;
    }
    
    public void doSerializeAttributes(java.lang.Object obj, XMLWriter writer, SOAPSerializationContext context) throws java.lang.Exception {
        org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArray1D_SN instance = (org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArray1D_SN)obj;
        
    }
    public void doSerialize(java.lang.Object obj, XMLWriter writer, SOAPSerializationContext context) throws java.lang.Exception {
        org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArray1D_SN instance = (org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArray1D_SN)obj;
        
        ns5_myArrayOfPerson_LiteralSerializer.serialize(instance.getInArray1D_SN(), ns3_inArray1D_SN_QNAME, null, writer, context);
    }
}