/*
 * JBossWS WS-Tools Generated Source
 *
 * Generation Date: Thu Jul 26 15:56:31 BST 2007
 *
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 */

package org.jboss.test.ws.jbws1723;


public class  BillingAccount
{

protected java.lang.String sortCode;

protected java.lang.String accountNumber;
public BillingAccount(){}

public BillingAccount(java.lang.String sortCode, java.lang.String accountNumber){
this.sortCode=sortCode;
this.accountNumber=accountNumber;
}
public java.lang.String getSortCode() { return sortCode ;}

public void setSortCode(java.lang.String sortCode){ this.sortCode=sortCode; }

public java.lang.String getAccountNumber() { return accountNumber ;}

public void setAccountNumber(java.lang.String accountNumber){ this.accountNumber=accountNumber; }

}