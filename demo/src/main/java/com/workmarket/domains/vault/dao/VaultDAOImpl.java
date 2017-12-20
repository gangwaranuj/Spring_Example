package com.workmarket.domains.vault.dao;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by ant on 2/25/15.
 */
@Repository(value="vaultDAO")
public class VaultDAOImpl implements VaultDAO {

	private RestTemplate restTemplate = new RestTemplate();

	@Override
	public void put(String className, long id, Map<String,String> properties) {
		Assert.notNull(className);
		Assert.notEmpty(properties);
		JSONObject payload = new JSONObject(properties);
		restTemplate.postForLocation("http://vault.workmarket.com/{class}/{id}", payload, Map.class, className, id);
	}

	@Override
	public Map<String,String> get(String securedMode, String className, long id) {
		Assert.notNull(securedMode);
		Assert.notNull(className);
		Map<String, String> resultMap = restTemplate.getForObject("http://vault.workmarket.com/{class}/{id}", Map.class, className, id);
		return resultMap;
	}

}
