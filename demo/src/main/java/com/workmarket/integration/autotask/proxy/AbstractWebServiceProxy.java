package com.workmarket.integration.autotask.proxy;

import org.apache.commons.httpclient.Credentials;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.workmarket.integration.autotask.soap.SoapExchange;

public abstract class AbstractWebServiceProxy {
	private final WebServiceTemplate webServiceTemplate;
	private final String targetNamespace;
	private final Credentials credentials;

	public AbstractWebServiceProxy(String targetNamespace, WebServiceTemplate webServiceTemplate, Credentials credentials) {
		this.targetNamespace = targetNamespace;
		this.webServiceTemplate = webServiceTemplate;
		this.credentials = credentials;
	}

	public AbstractWebServiceProxy(String targetNamespace, WebServiceTemplate webServiceTemplate){
		this.targetNamespace = targetNamespace;
		this.webServiceTemplate = webServiceTemplate;
		this.credentials = null;
	}

	public <Request, Response> SoapExchange<Request, Response> newSoapExchange() {
		return new SoapExchange<Request, Response>(targetNamespace, webServiceTemplate, credentials);
	}

	public <Request, Response> Response doSoapExchange(Request request) {
		SoapExchange<Request, Response> soap = new SoapExchange<Request, Response>(targetNamespace, webServiceTemplate, credentials);
		return soap.submit(request);
	}

	public WebServiceTemplate getWebServiceTemplate() {
		return this.webServiceTemplate;
	}

	public String getTargetNamespace() {
		return this.targetNamespace;
	}
}