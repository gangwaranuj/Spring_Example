package com.workmarket.integration.autotask.soap;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.FaultMessageResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 
 * @author <a href="mailto:alex.kirillov.007@gmail.com?subject=com.ekahau.i.api.soap.SoapFaultResolver>Alex Kirillov</a>
 *
 */
public class SoapFaultResolver implements FaultMessageResolver {
    private String namespace;
    private String namespaceUrl;
    private String faultCode;
    private String faultString;
    private String faultMessage;

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setNamespaceUrl(String namespaceUrl) {
        this.namespaceUrl = namespaceUrl;
    }

    public void setFaultCode(String faultCode) {
        this.faultCode = faultCode;
    }

    public void setFaultString(String faultString) {
        this.faultString = faultString;
    }

    public void resolveFault(WebServiceMessage webServiceMessage) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        webServiceMessage.writeTo(out);
        errorMessage(new String(out.toByteArray()));
        out.close();
    }
        
    private void errorMessage(String payload) {
        if(payload == null || payload.length() == 0) {
            faultMessage = "Fault message is invalid";
            return;
        }
        StringBuffer buffer  = new StringBuffer();
        /*try {
            Document doc 	 = DocumentHelper.parseText(payload);
            XPathUtils xpath = XPathUtils.newInstance(namespace, namespaceUrl, doc);
            String ftCode 	 = xpath.getValueFor(faultCode);
            String message 	 = xpath.getValueFor(faultString);
            
            if(message.contains("\n")) {
               message = message.substring(0, message.indexOf("\n"));
            }
            
            buffer.append("SoapFault: ").append(ftCode).append(" [ ").append(message).append(" ]");
        }
        catch (DocumentException e) {
            buffer.append(e.getMessage());
        }
        finally {
            faultMessage = buffer.toString();
        }*/
    }

    public final String getFaultMessage() {
        return faultMessage;
    }
}
