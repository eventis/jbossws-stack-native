/*
 * JBossWS WS-Tools Generated Source
 *
 * Generation Date: Wed Jun 06 14:58:58 CEST 2007
 *
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 */

package org.jboss.test.ws.jbws1698;


public class  TelephoneNumber
{

protected java.lang.String areaCode;

protected java.lang.String number;
public TelephoneNumber(){}

public TelephoneNumber(java.lang.String areaCode, java.lang.String number){
this.areaCode=areaCode;
this.number=number;
}
public java.lang.String getAreaCode() { return areaCode ;}

public void setAreaCode(java.lang.String areaCode){ this.areaCode=areaCode; }

public java.lang.String getNumber() { return number ;}

public void setNumber(java.lang.String number){ this.number=number; }

}