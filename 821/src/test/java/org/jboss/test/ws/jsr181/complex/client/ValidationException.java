// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package org.jboss.test.ws.jsr181.complex.client;


public class ValidationException extends org.jboss.test.ws.jsr181.complex.client.RegistrationException {
    private long[] failiedCustomers;
    
    
    public ValidationException(java.lang.String message, long[] failiedCustomers) {
        super(message);
        this.failiedCustomers = failiedCustomers;
    }
    
    public long[] getFailiedCustomers() {
        return failiedCustomers;
    }
}