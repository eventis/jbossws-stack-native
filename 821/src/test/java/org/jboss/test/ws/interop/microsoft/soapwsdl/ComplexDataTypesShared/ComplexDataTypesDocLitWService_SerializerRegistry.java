// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared;

import com.sun.xml.rpc.client.BasicService;
import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.encoding.simpletype.*;
import com.sun.xml.rpc.encoding.soap.*;
import com.sun.xml.rpc.encoding.literal.*;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import javax.xml.rpc.*;
import javax.xml.rpc.encoding.*;
import javax.xml.namespace.QName;

public class ComplexDataTypesDocLitWService_SerializerRegistry implements SerializerConstants {
    public ComplexDataTypesDocLitWService_SerializerRegistry() {
    }
    
    public TypeMappingRegistry getRegistry() {
        
        TypeMappingRegistry registry = BasicService.createStandardTypeMappingRegistry();
        TypeMapping mapping12 = registry.getTypeMapping(SOAP12Constants.NS_SOAP_ENCODING);
        TypeMapping mapping = registry.getTypeMapping(SOAPConstants.NS_SOAP_ENCODING);
        TypeMapping mapping2 = registry.getTypeMapping("");
        {
            QName type = new QName("http://schemas.datacontract.org/2004/07/System", "ArrayOfNullableOfdecimal");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfNullableOfdecimal_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfNullableOfdecimal.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetDerivedClass");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetDerivedClass_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetDerivedClass.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetStructSNResponse");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructSNResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructSNResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "ArrayOfanyType");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfanyType_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfanyType.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.datacontract.org/2004/07/XwsInterop.SoapWsdl.ComplexDataTypes.XmlFormatter.Service.Indigo", "ArrayOfPerson");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfPerson_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfPerson.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetEnumStringResponse");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetEnumStringResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetEnumStringResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.datacontract.org/2004/07/XwsInterop.SoapWsdl.ComplexDataTypes.XmlFormatter.Service.Indigo", "Name");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.Name_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.Name.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.datacontract.org/2004/07/XwsInterop.SoapWsdl.ComplexDataTypes.XmlFormatter.Service.Indigo", "Person");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.Person_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.Person.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetStructSNSAResponse");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructSNSAResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructSNSAResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetArrayInt1D");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayInt1D_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayInt1D.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetArrayInt1DResponse");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayInt1DResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayInt1DResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "ArrayOfint");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfint_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfint.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.datacontract.org/2004/07/XwsInterop.SoapWsdl.ComplexDataTypes.XmlFormatter.Service.Indigo", "Group");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.Group_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.Group.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetArrayDecimal1DResponse");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayDecimal1DResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayDecimal1DResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetArrayDateTime1D");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayDateTime1D_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayDateTime1D.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetEnumIntResponse");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetEnumIntResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetEnumIntResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetArrayString2D");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayString2D_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayString2D.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.datacontract.org/2004/07/XwsInterop.SoapWsdl.ComplexDataTypes.XmlFormatter.Service.Indigo", "IntSet");
            CombinedSerializer serializer = new LiteralSimpleTypeSerializer(type, "",
                org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.IntSet_Encoder.getInstance());
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.IntSet.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.datacontract.org/2004/07/System", "ArrayOfNullableOfdateTime");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfNullableOfdateTime_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfNullableOfdateTime.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetStructS1Response");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructS1Response_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructS1Response.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.datacontract.org/2004/07/XwsInterop.SoapWsdl.ComplexDataTypes.XmlFormatter.Service.Indigo", "Table");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.Table_LiteralSerializer(type, "", ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.Table.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "ArrayOfArrayOfstring");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfArrayOfstring_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfArrayOfstring.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetArray1D_SNResponse");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArray1D_SNResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArray1D_SNResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetArrayString2DResponse");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayString2DResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayString2DResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetStructSN");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructSN_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructSN.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.datacontract.org/2004/07/XwsInterop.SoapWsdl.ComplexDataTypes.XmlFormatter.Service.Indigo", "Furniture");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.Furniture_InterfaceSOAPSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.Furniture.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetArrayAnyType1DResponse");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayAnyType1DResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayAnyType1DResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetStructS1");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructS1_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructS1.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetDerivedClassResponse");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetDerivedClassResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetDerivedClassResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetArrayString1DResponse");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayString1DResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayString1DResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetArrayDecimal1D");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayDecimal1D_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayDecimal1D.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetArray1D_SN");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArray1D_SN_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArray1D_SN.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "ArrayOfshort");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfshort_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfshort.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "ArrayOfstring");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfstring_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.ArrayOfstring.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetStructSNSAS");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructSNSAS_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructSNSAS.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetArrayAnyType1D");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayAnyType1D_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayAnyType1D.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.datacontract.org/2004/07/XwsInterop.SoapWsdl.ComplexDataTypes.XmlFormatter.Service.Indigo", "Employee");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.Employee_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.Employee.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetStructSNSASResponse");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructSNSASResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructSNSASResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://schemas.datacontract.org/2004/07/XwsInterop.SoapWsdl.ComplexDataTypes.XmlFormatter.Service.Indigo", "BitMask");
            CombinedSerializer serializer = new LiteralSimpleTypeSerializer(type, "",
                org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.BitMask_Encoder.getInstance());
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.BitMask.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetEnumInt");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetEnumInt_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetEnumInt.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetEnumString");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetEnumString_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetEnumString.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetArrayString1D");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayString1D_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayString1D.class, type, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetArrayDateTime1DResponse");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayDateTime1DResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetArrayDateTime1DResponse.class, type, serializer);
        }
        {
            CombinedSerializer serializer = new LiteralFragmentSerializer(SchemaConstants.QNAME_TYPE_URTYPE, NOT_NULLABLE, "");
            registerSerializer(mapping2,javax.xml.soap.SOAPElement.class, SchemaConstants.QNAME_TYPE_URTYPE, serializer);
        }
        {
            QName type = new QName("http://tempuri.org/", "RetStructSNSA");
            CombinedSerializer serializer = new org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructSNSA_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,org.jboss.test.ws.interop.microsoft.soapwsdl.ComplexDataTypesShared.RetStructSNSA.class, type, serializer);
        }
        return registry;
    }
    
    private static void registerSerializer(TypeMapping mapping, java.lang.Class javaType, javax.xml.namespace.QName xmlType,
        Serializer ser) {
        mapping.register(javaType, xmlType, new SingletonSerializerFactory(ser),
            new SingletonDeserializerFactory((Deserializer)ser));
    }
    
}