// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package org.jboss.test.ws.jaxrpc.jbws663;


public class MessageStatus {
    protected int statusCode;
    protected java.lang.String statusText;
    protected java.lang.String statusExtra;
    
    public MessageStatus() {
    }
    
    public MessageStatus(int statusCode, java.lang.String statusText, java.lang.String statusExtra) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.statusExtra = statusExtra;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    
    public java.lang.String getStatusText() {
        return statusText;
    }
    
    public void setStatusText(java.lang.String statusText) {
        this.statusText = statusText;
    }
    
    public java.lang.String getStatusExtra() {
        return statusExtra;
    }
    
    public void setStatusExtra(java.lang.String statusExtra) {
        this.statusExtra = statusExtra;
    }
}