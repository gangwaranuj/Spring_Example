package com.workmarket.integration.autotask.proxy;

import com.autotask.ws.*;
import com.workmarket.integration.autotask.soap.SoapExchange;
import com.workmarket.domains.model.integration.autotask.AutotaskUser;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.springframework.ws.client.core.WebServiceTemplate;


public class AutotaskProxy extends AbstractWebServiceProxy implements ATWSSoap {

	public AutotaskProxy(String targetNamespace, WebServiceTemplate webServiceTemplate, AutotaskUser autotaskUser) {
		super(targetNamespace, webServiceTemplate, new UsernamePasswordCredentials(autotaskUser.getUserName(), autotaskUser.getPassword()));
	}

	public AutotaskProxy(String targetNamespace, WebServiceTemplate webServiceTemplate) {
		super(targetNamespace, webServiceTemplate);
	}

	public long createAttachment(Attachment attachment) {
		CreateAttachment createAttachment  = new CreateAttachment();
		createAttachment.setAttachment(attachment);
		SoapExchange<CreateAttachment, CreateAttachmentResponse> soapExchange = newSoapExchange();
		return soapExchange.submit(createAttachment).getCreateAttachmentResult();
	}

	public ATWSResponse delete(ArrayOfEntity entities) {
		Delete request = new Delete();
		request.setEntities(entities);
		SoapExchange<Delete, DeleteResponse> soapExchange = newSoapExchange();
		return soapExchange.submit(request).getDeleteResult();
	}

	public ATWSResponse query(String sXML) {
		Query query = new Query();
		query.setSXML(sXML);
		SoapExchange<Query, QueryResponse> soapExchange = newSoapExchange();
		return soapExchange.submit(query).getQueryResult();
	}

	public ATWSZoneInfo getZoneInfo(String username) {
		GetZoneInfo request = new GetZoneInfo();
		request.setUserName(username);
		SoapExchange<GetZoneInfo, GetZoneInfoResponse> soapExchange = newSoapExchange();
		return soapExchange.submit(request).getGetZoneInfoResult();
	}

	public ArrayOfField getUDFInfo(String psTable) {
		GetUDFInfo request = new GetUDFInfo();
		request.setPsTable(psTable);
		SoapExchange<GetUDFInfo, GetUDFInfoResponse> soapExchange = newSoapExchange();
		return soapExchange.submit(request).getGetUDFInfoResult();
	}

	public String deleteAttachment(long attachmentId) {
		DeleteAttachment request = new DeleteAttachment();
		request.setAttachmentId(attachmentId);
		SoapExchange<DeleteAttachment, DeleteAttachmentResponse> soapExchange = newSoapExchange();
		return soapExchange.submit(request).getDeleteAttachmentResult();
	}

	public Attachment getAttachment(long attachmentId) {
		GetAttachment request = new GetAttachment();
		request.setAttachmentId(attachmentId);
		SoapExchange<GetAttachment, GetAttachmentResponse> soapExchange = newSoapExchange();
		return soapExchange.submit(request).getGetAttachmentResult();
	}

	public ATWSResponse update(ArrayOfEntity entities) {
		Update request = new Update();
		request.setEntities(entities);
		SoapExchange<Update, UpdateResponse> soapExchange = newSoapExchange();
		return soapExchange.submit(request).getUpdateResult();
	}

	public ArrayOfField getFieldInfo(String psObjectType) {
		GetFieldInfo request = new GetFieldInfo();
		request.setPsObjectType(psObjectType);
		SoapExchange<GetFieldInfo, GetFieldInfoResponse> soapExchange = newSoapExchange();
		return soapExchange.submit(request).getGetFieldInfoResult();
	}

	public GetEntityInfoResponse getEntityInfo(GetEntityInfo parameters) {
		SoapExchange<GetEntityInfo, GetEntityInfoResponse> soapExchange = newSoapExchange();
		return soapExchange.submit(parameters);
	}

	public ATWSResponse getThresholdAndUsageInfo() {
		GetThresholdAndUsageInfo request = new GetThresholdAndUsageInfo();
		SoapExchange<GetThresholdAndUsageInfo, GetThresholdAndUsageInfoResponse> soapExchange = newSoapExchange();
		return soapExchange.submit(request).getGetThresholdAndUsageInfoResult();
	}

	public ATWSResponse create(ArrayOfEntity entities) {
		Create request = new Create();
		request.setEntities(entities);
		SoapExchange<Create, CreateResponse> soapExchange = newSoapExchange();
		return soapExchange.submit(request).getCreateResult();
	}
}
