// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package org.jboss.test.ws.jbws775;


public class TextTranslator_quoteTranslation_RequestStruct {
    protected java.lang.String clientName;
    protected java.lang.String text;
    protected java.lang.String sourceLanguage;
    protected java.lang.String targetLanguage;
    
    public TextTranslator_quoteTranslation_RequestStruct() {
    }
    
    public TextTranslator_quoteTranslation_RequestStruct(java.lang.String clientName, java.lang.String text, java.lang.String sourceLanguage, java.lang.String targetLanguage) {
        this.clientName = clientName;
        this.text = text;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }
    
    public java.lang.String getClientName() {
        return clientName;
    }
    
    public void setClientName(java.lang.String clientName) {
        this.clientName = clientName;
    }
    
    public java.lang.String getText() {
        return text;
    }
    
    public void setText(java.lang.String text) {
        this.text = text;
    }
    
    public java.lang.String getSourceLanguage() {
        return sourceLanguage;
    }
    
    public void setSourceLanguage(java.lang.String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }
    
    public java.lang.String getTargetLanguage() {
        return targetLanguage;
    }
    
    public void setTargetLanguage(java.lang.String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }
}