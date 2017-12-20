package com.workmarket.integration.autotask.proxy;

import com.workmarket.domains.model.integration.autotask.AutotaskUser;
import com.workmarket.service.infra.business.AuthenticationService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Created by nick on 2012-12-21 6:06 PM
 */
@Service
public class AutotaskProxyFactoryImpl implements AutotaskProxyFactory {

	@Autowired AuthenticationService authenticationService;

	@Value("${autotask.target.namespace}")
	private String targetNamespace;

	@Autowired @Qualifier("autotaskWebServiceTemplate")
	ObjectFactory<WebServiceTemplate> templateFactory;

	@Autowired @Qualifier("autotaskWebServiceTemplateSoapv11")
	ObjectFactory<WebServiceTemplate> templateFactorySoapv11;

	@Override
	public AutotaskProxy newInstance(AutotaskUser user) {
		Assert.notNull(user);
		WebServiceTemplate webServiceTemplate = templateFactory.getObject();
		if(user.hasZoneUrl()){
			webServiceTemplate.setDefaultUri(user.getZoneUrl());
		}

		return new AutotaskProxy(targetNamespace, webServiceTemplate, user);
	}

	@Override
	public AutotaskProxy newInstance(){
		//Use soap version 1.1 call here -- specific for getZoneurl on autotask integration
		WebServiceTemplate webServiceTemplate = templateFactorySoapv11.getObject();

		return new AutotaskProxy(targetNamespace, webServiceTemplate);
	}

	@Override
	public AutotaskProxy newInstance(String userName, String password, String zoneUrl) {
		Assert.hasText(userName);
		Assert.hasText(password);

		return newInstance(new AutotaskUser(authenticationService.getCurrentUser(), userName, password, zoneUrl));
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	public ObjectFactory<WebServiceTemplate> getTemplateFactory() {
		return templateFactory;
	}

	public void setTemplateFactory(ObjectFactory<WebServiceTemplate> templateFactory) {
		this.templateFactory = templateFactory;
	}
}
