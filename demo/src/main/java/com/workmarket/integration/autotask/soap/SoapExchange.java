package com.workmarket.integration.autotask.soap;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.transport.http.CommonsHttpMessageSender;

import javax.xml.transform.TransformerException;
import java.io.IOException;

import static com.workmarket.integration.autotask.util.StringUtil.uncap;

/**
 * @param <Request>
 * @param <Response>
 */
public class SoapExchange<Request, Response> {
	private static final Log log = LogFactory.getLog(SoapExchange.class);
	private final WebServiceTemplate webServiceTemplate;
	private final String targetNameSpace;
	private final Credentials credentials;

	public SoapExchange(String targetNamespace, WebServiceTemplate webServiceTemplate, Credentials credentials) {
		this.targetNameSpace = targetNamespace;
		this.webServiceTemplate = webServiceTemplate;
		this.credentials = credentials;
	}

	public Response submit(Request request) {
		if (credentials != null) {
			HttpClient client = new HttpClient();
			client.getParams().setAuthenticationPreemptive(true);
			client.getState().setCredentials(AuthScope.ANY, credentials);

			CommonsHttpMessageSender messageSender = new CommonsHttpMessageSender();
			messageSender.setHttpClient(client);
			webServiceTemplate.setMessageSender(messageSender);
		}

		MessageCallback msgCbk = new MessageCallback(targetNameSpace, uncap(request.getClass().getSimpleName()));
		@SuppressWarnings("unchecked")
		Response response = (Response) webServiceTemplate.marshalSendAndReceive(request, msgCbk);
		webServiceTemplate.afterPropertiesSet();
		String errorMsg = ((SoapFaultResolver) webServiceTemplate.getFaultMessageResolver()).getFaultMessage();
		if (errorMsg != null) {
			log.error("Fault message: " + errorMsg);
			// TODO: throw exception?
		}
		return response;
	}

	private class MessageCallback implements WebServiceMessageCallback {
		private String targetNamespace;
		private String soapAction;

		public MessageCallback(String targetNamespace, String soapAction) {
			this.targetNamespace = targetNamespace;
			this.soapAction = soapAction;
		}

		@Override
		public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
			((SoapMessage) message).setSoapAction(targetNamespace + soapAction);
		}
	}
}
