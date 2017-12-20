package com.workmarket.integration.mbo;

import com.google.common.collect.ImmutableMap;
import com.sforce.ws.ConnectionException;
import com.workmarket.service.business.integration.mbo.SalesForceClientImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = {
				"classpath:/spring/webservices-test.xml"
		})
public class SalesForceClientIT {

	@Test
	public void userUpdate() throws ConnectionException {
		SalesForceClientImpl salesForceClient = new SalesForceClientImpl();

		salesForceClient.endpoint = "https://test.salesforce.com/services/Soap/c/28.0/";
		salesForceClient.username = "api02@mbopartners.com.dev1";
		salesForceClient.password = "t!mMKgV@2QWB";
		salesForceClient.securityToken = "VDJFwK31zrdqL6rB60hp0XSYL";

		salesForceClient.updateUser("{99b144df-ac00-4993-bdf7-124379168767}", ImmutableMap.<String, Object>of("overview", "Gone fishing", "title", "Tzar"));
	}

	@Test
	public void login() throws ConnectionException {
		SalesForceClientImpl salesForceClient = new SalesForceClientImpl();

		salesForceClient.endpoint = "https://test.salesforce.com/services/Soap/c/28.0/";
		salesForceClient.username = "api02@mbopartners.com.dev1";
		salesForceClient.password = "t!mMKgV@2QWB";
		salesForceClient.securityToken = "VDJFwK31zrdqL6rB60hp0XSYL";

		salesForceClient.getUserInformation("{1b9f3123-f8b7-4d56-ad87-e2716033469b}");
		salesForceClient.getUserInformation("{1b9f3123-f8b7-4d56-ad87-e2716033469b}");   // LEAD
		salesForceClient.getUserInformation("{d181846d-82cb-4b47-91d1-609521042de0}");
	}
}
