package com.workmarket.service.infra.sugar;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.model.Profile;
import com.workmarket.integration.webhook.RestClient;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.CompanyService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.dto.IndustryDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.WebUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BuyerSignUpAdapterImpl implements BuyerSignUpAdapter {

	private static final Log logger = LogFactory.getLog(BuyerSignUpAdapterImpl.class);

	@Value("${sugar.username}")
	private String USERNAME;
	@Value("${sugar.password}")
	private String PASSWORD;
	@Value("${sugar.client.id}")
	private String CLIENT_ID;
	@Value("${sugar.client.secret}")
	private String CLIENT_SECRET;
	@Value("${sugar.client.url}")
	private String BASEURL;

	@Autowired RestClient restClient;
	@Autowired CompanyService companyService;
	@Autowired IndustryService industryService;

	private static final String DEFAULT_ASSIGNED_USER_ID = Constants.RUSSELL_SACHS_SUGAR_ID; // Russell's user id
	private static final String GRANT_TYPE = "password";
	private static final String PLATFORM = "base";
	private static final String LEAD_SOURCE = "Organic";
	private static final String LEAD_SOURCE_DETAIL = "Inbound";
	private static final String AQUISITION_PROGRAM_NAME = "Buyer Signup";

	private String getToken() {

		ImmutableMap<String, String> AUTH_PARAMETER = ImmutableMap.<String, String>builder()
				.put("grant_type", GRANT_TYPE)
				.put("username", USERNAME)
				.put("password", PASSWORD)
				.put("client_id", CLIENT_ID)
				.put("client_secret", CLIENT_SECRET)
				.put("platform", PLATFORM)
				.build();

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");

		Optional<String> body = WebUtilities.formEncodeMap(AUTH_PARAMETER);
		try {
			ResponseEntity<String> response = restClient.httpPost(new URI(BASEURL + "/oauth2/token"), body.get(), headers);
			if (!response.hasBody()) {
				logger.error(String.format("No body on SugarCRM token response! (response=%s)", response));
				return StringUtils.EMPTY;
			}
			Map<String, String> result = Splitter.on(',').withKeyValueSeparator(":").split(StringUtilities.remove(response.getBody(), "{}\""));
			return StringUtilities.defaultString(result.get("access_token"), "");

		} catch (URISyntaxException e) {
			logger.error(e);
		}

		return StringUtils.EMPTY;
	}

	@Override
	public ResponseEntity<String> createLead(long companyId) {

		Company company = companyService.findCompanyById(companyId);
		User user = company.getCreatedBy();
		String industry = Industry.NONE.getName(); // If no industry, pass Industry.NONE
		Profile profile = user.getProfile();
		if (profile != null) {
			List<IndustryDTO> industryList = new ArrayList<>(industryService.getIndustryDTOsForProfile(profile.getId()));
			if (!industryList.isEmpty()) {
				industry = CollectionUtilities.first(industryList).getName();
			}
		}

		String pricingPlan = companyService.getCompanySignUpPricingPlan(companyId);

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("oauth-token", getToken());

		Map<String, String> parameters = CollectionUtilities.newStringMap(
			"account_name", company.getName(),
			"description", company.getEffectiveName(),
			"first_name", user.getFirstName(),
			"last_name", user.getLastName(),
			"wmpid_c", String.valueOf(companyId),
			"email1", user.getEmail(),
			"primary_address_street", company.getAddress() == null ? "NA" : company.getAddress().getAddress1(),
			"primary_address_city", company.getAddress() == null ? "NA" : company.getAddress().getCity(),
			"primary_address_state", (company.getAddress() == null || company.getAddress().getState() == null) ? "NA" : company.getAddress().getState().toString(),
			"primary_address_country", (company.getAddress() == null || company.getAddress().getCountry() == null) ? "NA" : company.getAddress().getCountry().toString(),
			"primary_address_postalcode", company.getAddress() == null ? "NA" : company.getAddress().getPostalCode(),
			"phone_work", profile == null ? "NA" : profile.getWorkPhone(),
			"mrkto2_industry_c", industry,
			"assigned_user_id", DEFAULT_ASSIGNED_USER_ID,
			"mkto_sync", "1",
			"dbsignup_c", "Yes",
			"lead_source", LEAD_SOURCE,
			"lead_source_description", LEAD_SOURCE_DETAIL,
			"leadsourcedetail_c", LEAD_SOURCE_DETAIL,
			"aquisitionprogramname_c", AQUISITION_PROGRAM_NAME,
			"pricingplan_c", StringUtilities.capitalizeFirstLetter(pricingPlan)
		);

		Optional<String> body = WebUtilities.formEncodeMap(parameters);
		ResponseEntity<String> response;

		try {
			response = restClient.httpPost(new URI(BASEURL + "/Leads"), body.get(), headers);
			logger.info(response);
			return response;
		} catch (URISyntaxException e) {
			logger.error(e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@Override
	public String getAccountOwner(String companyId) {

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("oauth-token", getToken());

		Map<String, String> parameters = CollectionUtilities.newStringMap(
				"filter[0][wmcompanyid_c]", companyId,
				"max_num", "1",
				"fields", "assigned_user_name"
		);

		Optional<String> body = WebUtilities.formEncodeMap(parameters);
		ResponseEntity<String> response;

		try {
			response = restClient.httpGet(new URI(BASEURL + "/Accounts?" + body.get()), headers);
			return StringUtilities.stripSugarAccountOwnerResponse(response.getBody());
		} catch (Exception e) {
			logger.error(e);
			return "NA";
		}
	}

}
