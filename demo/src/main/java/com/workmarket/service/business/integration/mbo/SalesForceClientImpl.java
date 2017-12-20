package com.workmarket.service.business.integration.mbo;

import com.google.common.collect.Lists;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.GetUserInfoResult;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.SaveResult;
import com.sforce.soap.enterprise.sobject.Account;
import com.sforce.soap.enterprise.sobject.Contact;
import com.sforce.soap.enterprise.sobject.Feed__c;
import com.sforce.soap.enterprise.sobject.Lead;
import com.sforce.soap.enterprise.sobject.Opportunity;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.apache.commons.lang.builder.ToStringStyle.MULTI_LINE_STYLE;

@Service
public class SalesForceClientImpl implements SalesForceClient {

	private static final Logger logger = LoggerFactory.getLogger(SalesForceClientImpl.class);
	private static String[] queries = new String[3];

	@Autowired SubscriptionService subscriptionService;
	@Autowired WorkService workService;

	static {
		queries[0] = "SELECT ID, FirstName, LastName, PersonEmail, PersonTitle, Phone, PersonMailingStreet, PersonMailingCity, PersonMailingState, PersonMailingPostalCode, PersonMailingCountry, Description, fs_industryExpertiseWMID__pc  FROM Account WHERE fs_aDGUID__pc = '%s'";
		queries[1] = "SELECT ID, FirstName, LastName, Email, Secondary_Email__c, Phone, Title, MailingStreet, MailingCity, MailingState, MailingPostalCode, MailingCountry FROM Contact WHERE fs_aDGUID__c = '%s'";
		queries[2] = "SELECT ID, FirstName, LastName, Email, Phone, Title, Street, City, State, PostalCode, Country, Description FROM Lead WHERE fs_aDGUID__c = '%s' AND IsConverted = FALSE";
	}

	@Value("${mbo.salesforce.endpoint}")
	public String endpoint;
	@Value("${mbo.salesforce.username}")
	public String username;
	@Value("${mbo.salesforce.password}")
	public String password;
	@Value("${mbo.salesforce.securityToken}")
	public String securityToken;
	@Value("${mbo.feedDescription}")
	public String feedDescription;
	@Value("${mbo.opportunityRecordTypeId}")
	public String opportunityRecordTypeId;

	@Override
	public String getLeadId(String guid) throws ConnectionException {

		EnterpriseConnection connection = null;

		try {
			connection = new EnterpriseConnection(getConnectorConfig());

			if (logger.isDebugEnabled()) {
				logger.debug(getSessionInfo(connection));
			}

			QueryResult queryResult = connection.query(String.format("SELECT ID FROM Lead WHERE fs_aDGUID__c = '%s'", guid));

			if (queryResult != null && queryResult.getRecords().length > 0) {
				Lead lead = (Lead) queryResult.getRecords()[0];
				return lead.getId();
			} else {
				return null;
			}

		} finally {
			safeLogout(connection);
		}
	}

	@Override
	public String getAccountId(String guid) throws ConnectionException {

		EnterpriseConnection connection = null;

		try {
			connection = new EnterpriseConnection(getConnectorConfig());

			logger.debug(getSessionInfo(connection));

			QueryResult queryResult = connection.query(String.format("SELECT ID FROM Account WHERE fs_aDGUID__pc = '%s'", guid));

			if (queryResult != null && queryResult.getRecords().length > 0) {
				Account account = (Account) queryResult.getRecords()[0];
				return account.getId();
			} else {
				return null;
			}

		} finally {
			safeLogout(connection);
		}
	}

	@Override
	public String getContactAccountId(String guid) throws ConnectionException {

		EnterpriseConnection connection = null;

		try {
			connection = new EnterpriseConnection(getConnectorConfig());
			logger.debug(getSessionInfo(connection));

			QueryResult queryResult = connection.query(String.format("SELECT Account.Id FROM Contact WHERE fs_aDGUID__c = '%s'", guid));

			if (queryResult != null && queryResult.getRecords().length > 0) {
				Contact contact = (Contact) queryResult.getRecords()[0];
				return contact.getAccountId();
			} else {
				return null;
			}
		} finally {
			safeLogout(connection);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateUser(String guid, Map<String, Object> update) throws ConnectionException {
		EnterpriseConnection connection = null;

		try {
			connection = new EnterpriseConnection(getConnectorConfig());

			if (logger.isTraceEnabled()) {
				logger.trace(getSessionInfo(connection));
			}

			// first let's update the account
			QueryResult accountQueryResult = connection.query(String.format("SELECT Id FROM Account WHERE fs_aDGUID__pc = '%s'", guid));

			if (accountQueryResult != null && accountQueryResult.getRecords().length > 0) {
				Account account = (Account) accountQueryResult.getRecords()[0];

				for (String field : update.keySet()) {
					if ("title".equals(field)) {
						account.setPersonTitle((String) update.get(field));
					} else if ("overview".equals(field)) {
						account.setFs_overview__pc((String) update.get(field));
					} else if ("skills".equals(field)) {
						account.setFs_skills__pc(flattenSkills((List<Map<String, String>>) update.get(field)));
					}
				}

				SaveResult[] results = connection.update(new SObject[]{account});

				if (!results[0].getSuccess()) {
					logger.warn("unable to update account in salesforce: " + results[0]);
				}
			}

			// let's update the lead
			QueryResult leadQueryResult = connection.query(String.format("SELECT ID FROM Lead WHERE fs_aDGUID__c = '%s' AND IsConverted = FALSE", guid));

			if (leadQueryResult != null && leadQueryResult.getRecords().length > 0) {
				Lead lead = (Lead) leadQueryResult.getRecords()[0];

				for (String field : update.keySet()) {
					if ("address1".equals(field)) {
						lead.setStreet((String) update.get(field));
					} else if ("city".equals(field)) {
						lead.setCity((String) update.get(field));
					} else if ("state".equals(field)) {
						lead.setState((String) update.get(field));
					} else if ("postalCode".equals(field)) {
						lead.setPostalCode((String) update.get(field));
					} else if ("title".equals(field)) {
						lead.setTitle((String) update.get(field));
					} else if ("overview".equals(field)) {
						lead.setFs_overview__c((String) update.get(field));
					} else if ("skills".equals(field)) {
						lead.setFs_skills__c(flattenSkills((List<Map<String, String>>) update.get(field)));
					}
				}

				SaveResult[] results = connection.update(new SObject[]{lead});

				if (!results[0].getSuccess()) {
					logger.warn("unable to update lead in salesforce: " + results[0]);
				}
			}
		} finally {
			safeLogout(connection);
		}
	}

	private String flattenSkills(List<Map<String, String>> skills) {
		List<String> skillNames = Lists.newArrayList();
		for(Map<String, String> map : skills) {
			skillNames.add(map.get("name"));
		}
		return StringUtils.join(skillNames, ", ");
	}

	@Override
	public UserProfileDTO getUserInformation(String guid) throws ConnectionException {
		long start = nanoTime();

		EnterpriseConnection connection = null;

		try {
			connection = new EnterpriseConnection(getConnectorConfig());

			if (logger.isTraceEnabled()) {
				logger.trace(getSessionInfo(connection));
			}

			QueryResult result = findUser(guid, connection);

			if (result != null) {
				SObject[] records = result.getRecords();

				for (int i = 0; i < records.length; ++i) {
					Object user = records[i];

					if (user instanceof Contact) {
						return toProfile((Contact) user);

					} else if (user instanceof Lead) {
						return toProfile((Lead) user);

					} else if (user instanceof Account) {
						return toProfile((Account) user);

					} else {
						throw new RuntimeException("Unexpected salesforce web service response: " + user);
					}
				}
			}
			return null;
		} finally {
			safeLogout(connection);
			logger.debug(String.format("completed lookup for %s in %dms", guid, NANOSECONDS.toMillis(nanoTime() - start)));
		}
	}

	@Override
	public void createFeed(Long workId, MboProfile mboProfile) throws ConnectionException {
		Work work = workService.findWork(workId);
		createFeed(work.getWorkNumber(), mboProfile);
	}

	@Override
	public void createFeed(String workNumber, MboProfile mboProfile) throws ConnectionException {

		Feed__c feed = new Feed__c();
		feed.setFs_description__c(feedDescription);

		// Prefer account ID to lead ID if both are present
		if (StringUtils.isNotBlank(mboProfile.getAccountId())) {
			feed.setRs_recipientAccount__c(mboProfile.getAccountId());
		} else {
			feed.setRs_recipientLead__c(mboProfile.getLeadId());
		}

		feed.setFs_url__c("https://www.workmarket.com/assignments/details/" + workNumber);

		EnterpriseConnection connection = null;

		try {
			connection = new EnterpriseConnection(getConnectorConfig());

			if (logger.isTraceEnabled()) {
				logger.trace(getSessionInfo(connection));
			}

			SaveResult[] results = connection.create(new SObject[]{feed});

			if (logger.isTraceEnabled()) {
				if (results != null) {
					for (SaveResult result : results) {
						logger.trace(String.format("create feed for %s result %s", workNumber, result));
					}
				}
			}
		} finally {
			safeLogout(connection);
		}
	}

	@Override
	public void createOpportunity(AbstractWork work, MboProfile mboProfile) throws ConnectionException {

		Opportunity opportunity = new Opportunity();

		opportunity.setRecordTypeId(opportunityRecordTypeId);
		opportunity.setStageName("Pending");
		opportunity.setName(work.getTitle());
		opportunity.setFs_wMAssignmentID__c(work.getWorkNumber());
		opportunity.setFs_woStart__c(work.getScheduleFrom());
		opportunity.setCloseDate(work.getScheduleThrough() != null ? work.getScheduleThrough() : work.getScheduleFrom());
		opportunity.setFs_woEnd__c(work.getScheduleThrough() != null ? work.getScheduleThrough() : work.getScheduleFrom());

		FullPricingStrategy pricing = work.getPricingStrategy().getFullPricingStrategy();
		opportunity.setFs_pricingType__c(work.getPricingStrategyType().getDescription());

		if (work.getPricingStrategyType() == PricingStrategyType.FLAT) {
			opportunity.setFn_pUnitCost__c(pricing.getFlatPrice().doubleValue());
		}
		else if (work.getPricingStrategyType() == PricingStrategyType.PER_HOUR) {
			opportunity.setFn_pUnitCost__c(pricing.getPerHourPrice().doubleValue());
			opportunity.setFn_pUnitAllowed__c(pricing.getMaxNumberOfHours().doubleValue());
		}
		else if (work.getPricingStrategyType() == PricingStrategyType.PER_UNIT) {
			opportunity.setFn_pUnitCost__c(pricing.getPerUnitPrice().doubleValue());
			opportunity.setFn_pUnitAllowed__c(pricing.getMaxNumberOfUnits().doubleValue());
		}
		else if (work.getPricingStrategyType() == PricingStrategyType.BLENDED_PER_HOUR) {
			opportunity.setFn_pUnitAllowed__c(pricing.getInitialNumberOfHours().doubleValue());
			opportunity.setFn_pUnitCost__c(pricing.getInitialPerHourPrice().doubleValue());
			opportunity.setFn_sUnitCost__c(pricing.getAdditionalPerHourPrice().doubleValue());
			opportunity.setFs_sUnitAllowed__c(pricing.getMaxBlendedNumberOfHours().doubleValue());
		}

		// only set the WM Company ID if the buyer is an MBO subscriber
		if(subscriptionService.hasMboServiceType(work.getCompany().getId())) {
			opportunity.setFs_wmCompanyId__c(work.getCompany().getCompanyNumber());
		}

		// Prefer account ID to lead ID if both are present
		if (StringUtils.isNotBlank(mboProfile.getAccountId())) {
			opportunity.setAccountId(mboProfile.getAccountId());
		} else {
			opportunity.setRs_leadAssociate__c(mboProfile.getLeadId());
		}

		opportunity.setRs_client__c(mboProfile.getContactParentAccountId());

		EnterpriseConnection connection = null;

		try {
			connection = new EnterpriseConnection(getConnectorConfig());

			if (logger.isTraceEnabled()) {
				logger.trace(getSessionInfo(connection));
			}

			SaveResult[] results = connection.create(new SObject[]{opportunity});

			if (logger.isTraceEnabled()) {
				if (results != null) {
					for (SaveResult result : results) {
						logger.trace(String.format("create opportunity for %s result %s", work.getId(), result));
					}
				}
			}
		} finally {
			safeLogout(connection);
		}
	}

	private UserProfileDTO toProfile(Lead user) {
		UserProfileDTO profile = new UserProfileDTO();

		profile.setSessionId(user.getId());
		profile.setManageWork(user.getId().startsWith("003"));
		profile.setFindWork(!profile.getManageWork());
		profile.setFirstName(user.getFirstName());
		profile.setLastName(user.getLastName());
		profile.setEmail(user.getEmail());
		profile.setMobilePhone(user.getPhone());
		profile.setJobTitle(user.getTitle());
		profile.setWorkPhone(user.getPhone());
		profile.setStatus(MboProfile.PREREGISTERED);

		if (StringUtils.isNotBlank(user.getPostalCode())) {
			profile.setAddress1(user.getStreet());
			profile.setCity(user.getCity());
			profile.setState(user.getState());
			profile.setPostalCode(user.getPostalCode());
			profile.setCountry(StringUtilities.defaultString(user.getCountry(), "US"));
		}

		if (logger.isTraceEnabled()) {
			logger.trace("converted Lead to " + ReflectionToStringBuilder.toString(profile, MULTI_LINE_STYLE));
		}

		return profile;
	}

	private UserProfileDTO toProfile(Contact contact) {

		UserProfileDTO profile = new UserProfileDTO();

		profile.setSessionId(contact.getId());
		profile.setManageWork(contact.getId().startsWith("003"));
		profile.setFindWork(!profile.getManageWork());

		profile.setFirstName(contact.getFirstName());
		profile.setLastName(contact.getLastName());
		profile.setEmail(contact.getEmail());
		profile.setJobTitle(contact.getTitle());
		profile.setWorkPhone(contact.getPhone());
		profile.setStatus(MboProfile.NORMAL);

		if (StringUtils.isNotBlank(contact.getMailingPostalCode())) {
			profile.setAddress1(contact.getMailingStreet());
			profile.setCity(contact.getMailingCity());
			profile.setState(contact.getMailingState());
			profile.setPostalCode(contact.getMailingPostalCode());
			profile.setCountry(StringUtilities.defaultString(contact.getMailingCountry(), "US"));
		}

		if (logger.isTraceEnabled()) {
			logger.trace("converted Contact to " + ReflectionToStringBuilder.toString(profile, MULTI_LINE_STYLE));
		}

		return profile;
	}

	private UserProfileDTO toProfile(Account account) {

		UserProfileDTO profile = new UserProfileDTO();

		profile.setSessionId(account.getId());
		profile.setManageWork(account.getId().startsWith("003"));
		profile.setFindWork(!profile.getManageWork());

		profile.setFirstName(account.getFirstName());
		profile.setLastName(account.getLastName());
		profile.setEmail(account.getPersonEmail());
		profile.setAddress1(account.getPersonMailingStreet());
		profile.setJobTitle(account.getPersonTitle());
		profile.setWorkPhone(account.getPhone());
		profile.setOverview(account.getDescription());
		profile.setStatus(MboProfile.NORMAL);

		if (StringUtils.isNotBlank(account.getPersonMailingPostalCode())) {
			profile.setCity(account.getPersonMailingCity());
			profile.setState(account.getPersonMailingState());
			profile.setPostalCode(account.getPersonMailingPostalCode());
			profile.setCountry(account.getPersonMailingCountry());
			profile.setCountry(StringUtilities.defaultString(account.getPersonMailingCountry(), "US"));
		}

		if (StringUtils.isNotEmpty(account.getFs_industryExpertiseWMID__pc())) {
			String[] stringIds = account.getFs_industryExpertiseWMID__pc().split("[,;]");
			Long[] longIds = new Long[stringIds.length];

			for (int i = 0; i < stringIds.length; i++) {
				longIds[i] = Long.valueOf(stringIds[i]);
			}

			profile.setIndustryIds(longIds);
		}

		if (logger.isTraceEnabled()) {
			logger.trace("converted Account to " + ReflectionToStringBuilder.toString(profile, MULTI_LINE_STYLE));
		}

		return profile;
	}

	private QueryResult findUser(String guid, EnterpriseConnection connection) throws ConnectionException {

		for (String query : queries) {
			QueryResult qr = connection.query(String.format(query, guid));

			if (qr.getSize() == 1) {
				return qr;
			}
		}

		return null;
	}

	private String getSessionInfo(EnterpriseConnection connection) throws ConnectionException {
		GetUserInfoResult userInfo = connection.getUserInfo();

		return "UserID: " + userInfo.getUserId()
				+ ", Name: " + userInfo.getUserFullName()
				+ ", Email: " + userInfo.getUserEmail()
				+ ", SessionID: " + connection.getConfig().getSessionId()
				+ ", AuthEndPoint: " + connection.getConfig().getAuthEndpoint()
				+ ", ServiceEndPoint: " + connection.getConfig().getServiceEndpoint();
	}

	private ConnectorConfig getConnectorConfig() {
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(username);
		config.setPassword(password + securityToken);
		config.setAuthEndpoint(endpoint);

		return config;

	}

	private void safeLogout(EnterpriseConnection connection) {
		if (connection != null) {
			try {
				connection.logout();
			} catch (ConnectionException ex) {
				logger.warn("unable to close salesforce connection", ex);
			}
		}
	}


}