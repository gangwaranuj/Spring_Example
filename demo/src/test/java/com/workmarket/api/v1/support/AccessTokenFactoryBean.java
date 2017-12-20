package com.workmarket.api.v1.support;

import com.workmarket.api.v1.ApiV1Response;
import com.workmarket.api.v1.ApiV1ResponseMeta;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class AccessTokenFactoryBean implements FactoryBean<String> {
	private RestTemplate restTemplate;
	private String apiUrl;
	private String token;
	private String secret;
	private String accessToken;
	private String outputFormat;

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	@Override
	public String getObject() throws BeansException {
		if (StringUtils.hasText(accessToken)) {
			return accessToken;
		}
		else {
			return resolveAccessToken(token, secret);
		}
	}

	protected String resolveAccessToken(String token, String secret) {
		MultiValueMap<String,String> postData = new LinkedMultiValueMap<String, String>();
		postData.add("token", token);
		postData.add("secret", secret);
		postData.add("output_format", outputFormat);

		ApiV1Response apiResponse = restTemplate.postForObject(apiUrl + "/authorization/request", postData, ApiV1Response.class);
		ApiV1ResponseMeta meta = apiResponse.getMeta();

		if (meta.getErrors().isEmpty()) {
			Map responseMap = (Map)apiResponse.getResponse();
			String accessToken = (String)responseMap.get("access_token");

			if (StringUtils.hasText(accessToken)) {
				return accessToken;
			}
			else {
				throw new BeanCreationException("API response does not contain \"access_token\" value!");
			}
		}
		else {
			@SuppressWarnings("unchecked")
			Map<String,String> errors = (Map)meta.getErrors().iterator().next();
			String message = errors.get("message");
			throw new BeanCreationException(message);
		}
	}

	@Override
	public Class<?> getObjectType() {
		return String.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
