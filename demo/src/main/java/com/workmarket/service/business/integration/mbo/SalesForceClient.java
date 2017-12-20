package com.workmarket.service.business.integration.mbo;

import com.sforce.ws.ConnectionException;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.work.model.AbstractWork;

import java.util.Map;

public interface SalesForceClient {

	public UserProfileDTO getUserInformation(String guid) throws ConnectionException;

	public void createOpportunity(AbstractWork work, MboProfile profile) throws ConnectionException;

	public void createFeed(Long workId, MboProfile mboProfile) throws ConnectionException;

	public void createFeed(String workNumber, MboProfile mboProfile) throws ConnectionException;

	public void updateUser(String guid, Map<String, Object> update) throws ConnectionException;

	public String getAccountId(String guid) throws ConnectionException;

	public String getContactAccountId(String guid) throws ConnectionException;

	public String getLeadId(String guid) throws ConnectionException;
}