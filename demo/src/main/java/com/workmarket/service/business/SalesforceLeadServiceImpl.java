package com.workmarket.service.business;

import com.amazonaws.util.json.JSONObject;
import com.workmarket.configuration.SalesforceConfigurationService;
import com.workmarket.domains.model.Company;
import com.workmarket.web.forms.RegisterUserForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class SalesforceLeadServiceImpl implements SalesforceLeadService {

	@Autowired private SalesforceConfigurationService salesforceConfigurationService;
	@Autowired private CompanyService companyService;

	private final String GRANT_TYPE = "password";
	private final String LEAD_SOURCE = "Organic";
	private final String LEAD_SOURCE_DETAIL = "Transactional";

	private static final Log logger = LogFactory.getLog(SalesforceLeadServiceImpl.class);

	@Override
	public String authenticateToken() {
		try {
			HttpClient client = HttpClientBuilder.create().build();

			HttpPost post = new HttpPost(salesforceConfigurationService.getSALESFORCE_AUTH_URL());
			post.setHeader("Accept", "*/*");
			post.setHeader("Content-type", "application/x-www-form-urlencoded");

			List<NameValuePair> formParams = new ArrayList<>();
			formParams.add(new BasicNameValuePair("grant_type", GRANT_TYPE));
			formParams.add(new BasicNameValuePair("client_id", salesforceConfigurationService.getSALESFORCE_ID()));
			formParams.add(new BasicNameValuePair("client_secret", salesforceConfigurationService.getSALESFORCE_KEY()));
			formParams.add(new BasicNameValuePair("username", salesforceConfigurationService.getSALESFORCE_USERNAME()));
			formParams.add(new BasicNameValuePair("password", salesforceConfigurationService.getSALESFORCE_PASSWORD()));

			post.setEntity(new UrlEncodedFormEntity(formParams));

			HttpResponse response = client.execute(post);

			if (response.getStatusLine().getStatusCode() != 200) {
				logger.error("Failed to establish auth token.");
				return null;
			}

			String jsonString = EntityUtils.toString(response.getEntity());

			JSONObject result = new JSONObject(jsonString);

			return result.get("access_token").toString();

		} catch (Exception e) {
			logger.error("Failed to establish auth token.");
			return null;

		}
	}

	@Override
	public void generateBuyerLead(String authToken, RegisterUserForm form) {
		try {
			HttpClient client = HttpClientBuilder.create().build();

			HttpPost post = new HttpPost(salesforceConfigurationService.getSALESFORCE_LEADS_URL());
			post.setHeader("Accept",  "*/*");
			post.setHeader("Content-type", "application/json");

			String authBearer = "Bearer " + authToken;
			post.setHeader("Authorization", authBearer);

			Company company = companyService.findCompanyByName(form.getCompanyName());

			if (company == null) {
				logger.error("Failed to generate lead.");
				return;
			}

			JSONObject data = new JSONObject();
			data.put("FirstName", form.getFirstName());
			data.put("LastName", form.getLastName());
			data.put("Company", form.getCompanyName());
			data.put("Phone", form.getWorkPhone());
			data.put("City", form.getCity());
			data.put("Country", form.getCountry());
			data.put("PostalCode", form.getPostalCode());
			data.put("State", form.getState());
			data.put("Street", form.getAddress1());
			data.put("Email", form.getUserEmail());
			data.put("LeadSource", LEAD_SOURCE);
			data.put("Lead_Source_Details__c", LEAD_SOURCE_DETAIL);
			data.put("WMP_Account_ID__c", company.getId());

			post.setEntity(new StringEntity(data.toString()));

			HttpResponse response = client.execute(post);

			if (response.getStatusLine().getStatusCode() != 201) {
				logger.error("Failed to generate lead.");
				return;
			}
		} catch (Exception e) {
			logger.error("Failed to generate lead.");
			return;
		}
	}

	@Override
	public void generateBuyerLead(String authToken,
				      String firstName,
				      String lastName,
				      String companyName,
				      String workPhone,
				      String city,
				      String country,
				      String postalCode,
				      String state,
				      String address,
				      String userEmail,
				      Long companyId,
				      String title,
				      String function) {
		try {
			HttpClient client = HttpClientBuilder.create().build();

			HttpPost post = new HttpPost(salesforceConfigurationService.getSALESFORCE_LEADS_URL());
			post.setHeader("Accept",  "*/*");
			post.setHeader("Content-type", "application/json");

			String authBearer = "Bearer " + authToken;
			post.setHeader("Authorization", authBearer);

			JSONObject data = new JSONObject();
			data.put("FirstName", firstName);
			data.put("LastName", lastName);
			data.put("Company", companyName);
			data.put("Phone", workPhone);
			data.put("City", city);
			data.put("Country", country);
			data.put("PostalCode", postalCode);
			data.put("State", state);
			data.put("Street", address);
			data.put("Email", userEmail);
			data.put("Function__c", function);
			data.put("Title", title);
			data.put("LeadSource", LEAD_SOURCE);
			data.put("Lead_Source_Details__c", LEAD_SOURCE_DETAIL);
			data.put("WMP_Account_ID__c", companyId);

			post.setEntity(new StringEntity(data.toString()));

			HttpResponse response = client.execute(post);

			if (response.getStatusLine().getStatusCode() != 201) {
				logger.error("Failed to generate lead.");
			}
		} catch (Exception e) {
			logger.error("Failed to generate lead.");
		}
	}
}
