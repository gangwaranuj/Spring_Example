package com.workmarket.service.business.integration.hooks.webhook;

import com.google.common.base.Optional;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.integration.webhook.WebHookClientDAO;
import com.workmarket.integration.webhook.RestClient;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.integration.webhook.SalesforceWebHookClient;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.dto.integration.SalesforceAccessTokenDTO;
import com.workmarket.service.business.dto.integration.SalesforceRefreshTokenDTO;
import com.workmarket.service.business.dto.integration.SalesforceWebHookClientDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.WebUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Service
public class SalesforceWebHookIntegrationServiceImpl implements SalesforceWebHookIntegrationService {
	private static final Logger logger = LoggerFactory.getLogger(SalesforceWebHookIntegrationService.class);

	@Autowired JsonSerializationService jsonSerializationService;

	@Autowired RestClient restClient;

	@Autowired
	CompanyDAO companyDAO;
	@Autowired
	WebHookClientDAO webHookClientDAO;

	@Value("${salesforce.key}") private String consumerKey;
	@Value("${salesforce.secret}") private String consumerSecret;
	@Value("${salesforce.callback}") private String callbackUrl;

	public static final Map<String, String> AUTH_HEADERS = CollectionUtilities.newStringMap(
			"Content-Type", "application/x-www-form-urlencoded"
	);

	@Override
	public SalesforceWebHookClient saveSalesforceRefreshToken(String refreshToken, Long companyId, Boolean isSandbox) {
		Assert.notNull(companyId);

		Company company = companyDAO.get(companyId);

		Assert.notNull(company);

		Optional<SalesforceWebHookClient> salesforceWebHookClientOptional = webHookClientDAO.findSalesforceWebHookClientByCompany(companyId);

		SalesforceWebHookClient salesforceWebHookClient;

		if (salesforceWebHookClientOptional.isPresent()) {
			salesforceWebHookClient = salesforceWebHookClientOptional.get();
		} else {
			salesforceWebHookClient = new SalesforceWebHookClient();
		}

		salesforceWebHookClient.setRefreshToken(refreshToken);
		salesforceWebHookClient.setCompany(company);
		salesforceWebHookClient.setSandbox(isSandbox);
		webHookClientDAO.saveOrUpdate(salesforceWebHookClient);
		return salesforceWebHookClient;
	}

	@Override
	public SalesforceWebHookClient saveSalesforceSettings(SalesforceWebHookClientDTO salesforceWebHookClientDTO, Long companyId) {
		Assert.notNull(companyId);

		Company company = companyDAO.get(companyId);

		Assert.notNull(company);

		Optional<SalesforceWebHookClient> salesforceWebHookClientOptional = webHookClientDAO.findSalesforceWebHookClientByCompany(companyId);

		SalesforceWebHookClient salesforceWebHookClient;

		if (salesforceWebHookClientOptional.isPresent()) {
			salesforceWebHookClient = salesforceWebHookClientOptional.get();
		} else {
			salesforceWebHookClient = new SalesforceWebHookClient();
		}

		salesforceWebHookClient.setDateFormat(salesforceWebHookClientDTO.getDateFormat());
		salesforceWebHookClient.setSuppressApiEvents(salesforceWebHookClientDTO.isSuppressApiEvents());
		salesforceWebHookClient.setCompany(company);
		webHookClientDAO.saveOrUpdate(salesforceWebHookClient);
		return salesforceWebHookClient;
	}

	@Override
	public Optional<SalesforceWebHookClient> findSalesforceSettings(Long companyId) {
		Assert.notNull(companyId);
		return webHookClientDAO.findSalesforceWebHookClientByCompany(companyId);
	}

	@Override
	public Optional<SalesforceAccessTokenDTO> getSalesforceAccessToken(SalesforceWebHookClient salesforceWebHookClient) {

		Map<String, String> parameters = CollectionUtilities.newStringMap(
				"grant_type", "refresh_token",
				"client_id", consumerKey,
				"client_secret", consumerSecret,
				"refresh_token", salesforceWebHookClient.getRefreshToken(),
				"format", "json"
		);

		Optional<String> body = WebUtilities.formEncodeMap(parameters);

		if (!body.isPresent()) {
			return Optional.absent();
		}

		ResponseEntity<String> response;

		try {
			response = restClient.httpPost(
					new URI(salesforceWebHookClient.isSandbox() ? "https://test.salesforce.com/services/oauth2/token" : "https://login.salesforce.com/services/oauth2/token"),
					body.get(),
					AUTH_HEADERS);
		} catch (URISyntaxException e) {
			return Optional.absent();
		}

		Optional<SalesforceAccessTokenDTO> salesforceAccessTokenDTOOptional = Optional.fromNullable(jsonSerializationService.fromJson(response.getBody(), SalesforceAccessTokenDTO.class));

		if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
			String message = "Failed to get Salesforce access token for company " + salesforceWebHookClient.getCompany().getId();

			if (salesforceAccessTokenDTOOptional.isPresent()) {
				message += " due to error_code=" + salesforceAccessTokenDTOOptional.get().getError()
						+ ", error_description=" + salesforceAccessTokenDTOOptional.get().getError_description();
			}

			logger.error(message);

			return Optional.absent();
		}

		return salesforceAccessTokenDTOOptional;
	}

	@Override
	public Optional<SalesforceRefreshTokenDTO> getSalesforceRefreshToken(String code, Boolean isSandbox) {
		Map<String, String> parameters = CollectionUtilities.newStringMap(
				"grant_type", "authorization_code",
				"client_id", consumerKey,
				"client_secret", consumerSecret,
				"redirect_uri", callbackUrl,
				"code", code,
				"format", "json"
		);

		Optional<String> body = WebUtilities.formEncodeMap(parameters);

		if (!body.isPresent()) {
			return Optional.absent();
		}

		ResponseEntity<String> response;

		try {
			response = restClient.httpPost(
					new URI(isSandbox ? "https://test.salesforce.com/services/oauth2/token": "https://login.salesforce.com/services/oauth2/token"),
					body.get(),
					AUTH_HEADERS);
		} catch (URISyntaxException e) {
			return Optional.absent();
		}

		Optional<SalesforceRefreshTokenDTO> salesforceRefreshTokenDTOOptional = Optional.fromNullable(jsonSerializationService.fromJson(response.getBody(), SalesforceRefreshTokenDTO.class));

		if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
			String message = "Failed to get Salesforce refresh token for code " + code;

			if (salesforceRefreshTokenDTOOptional.isPresent()) {
				message += " due to error_code=" + salesforceRefreshTokenDTOOptional.get().getError()
						+ ", error_description=" + salesforceRefreshTokenDTOOptional.get().getError_description();
			}

			logger.error(message);

			return Optional.absent();
		}

		return salesforceRefreshTokenDTOOptional;
	}

	@Override
	public String getSalesforceConsumerKey() {
		return consumerKey;
	}

	@Override
	public String getSalesforceCallbackUrl() {
		return callbackUrl;
	}
}
