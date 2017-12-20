package com.workmarket.service.business.integration.hooks.autotask;

import com.autotask.ws.*;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.integration.autotask.AutotaskTicketWorkAssociationDAO;
import com.workmarket.dao.integration.autotask.AutotaskUserCustomFieldsPreferenceDAO;
import com.workmarket.dao.integration.autotask.AutotaskUserDAO;
import com.workmarket.dao.integration.autotask.AutotaskUserPreferenceDAO;
import com.workmarket.dao.note.NoteDAO;
import com.workmarket.domains.work.dao.WorkResourceTimeTrackingDAO;
import com.workmarket.integration.autotask.proxy.AutotaskProxy;
import com.workmarket.integration.autotask.proxy.AutotaskProxyFactory;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.integration.IntegrationCustomField;
import com.workmarket.domains.model.integration.autotask.*;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.dto.PaymentSummaryDTO;
import com.workmarket.service.business.dto.integration.AutotaskUserCustomFieldsPreferenceDTO;
import com.workmarket.service.business.dto.integration.AutotaskUserDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookIntegrationService;
import com.workmarket.service.business.queue.integration.IntegrationEventService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.exception.integration.AutotaskAuthenticationException;
import com.workmarket.service.exception.integration.AutotaskCustomFieldsException;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.integration.autotask.AutotaskQueryXmlBuilder;
import com.workmarket.utility.integration.autotask.Condition;
import com.workmarket.web.forms.mmw.AutotaskUserForm;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.ws.WebServiceException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by nick on 2012-12-21 10:45 AM
 */

@Service
public class AutotaskIntegrationServiceImpl implements AutotaskIntegrationService {

	private static final Log logger = LogFactory.getLog(AutotaskIntegrationServiceImpl.class);

	@Autowired AutotaskUserDAO autotaskUserDAO;
	@Autowired AutotaskTicketWorkAssociationDAO autotaskTicketWorkAssociationDAO;
	@Autowired AutotaskProxyFactory autotaskProxyFactory;
	@Autowired UserService userService;
	@Autowired WorkResourceTimeTrackingDAO workResourceTimeTrackingDAO;
	@Autowired NoteDAO noteDAO;
	@Autowired WorkService workService;
	@Autowired AutotaskUserPreferenceDAO autotaskUserPreferenceDAO;
	@Autowired IntegrationEventService integrationEventService;
	@Autowired WebHookIntegrationService webHookIntegrationService;
	@Autowired AutotaskUserCustomFieldsPreferenceDAO autotaskUserCustomFieldsPreferenceDAO;
	@Autowired RegistrationService registrationService;
	@Autowired CompanyDAO companyDAO;

	@Value("${baseurl}")
	private String BASE_URL;


	private static final Map<String, String> autotaskProps = Maps.newHashMap();
	public static final String WM_STATUS = "wmStatus";

	private static final Integer AUTOTASK_TICKET_TYPE = 4;
	private static final Integer AUTOTASK_PUBLISH_NOTES_NON_INTERNAL = 1;
	private static final Integer AUTOTASK_PUBLISH_NOTES_INTERNAL = 2;
	private static final Integer AUTOTASK_PUBLISH_ATTACHMENTS_NON_INTERNAL = 1;
	private static final Integer AUTOTASK_PUBLISH_ATTACHMENTS_INTERNAL = 2;
	private static final String AUTOTASK_ATTACHMENT_TYPE_FILE_LINK = "FILE_LINK";
	private static final String AUTOTASK_ATTACHMENT_TYPE_URL = "URL";
	private static final String AUTOTASK_ATTACHMENT_TYPE_RAW = "FILE_ATTACHMENT";
	private static final Integer AUTOTASK_NOTE_TYPE_TASK_NOTE = 3;

	static {
		try {
			Properties props = PropertiesLoaderUtils.loadAllProperties("autotask.properties");
			for (String navElem : props.stringPropertyNames()) {
				autotaskProps.put(navElem, props.getProperty(navElem));
			}
		} catch (IOException ex) {
			logger.error("Failed to load autotask detail navigation mappings", ex);
		}
	}

	public Map<String, String> getAutotaskProps() {
		return autotaskProps;
	}

	@Override
	public Optional<AutotaskUser> findAutotaskUserByUserId(Long userId) {
		Assert.notNull(userId);
		return autotaskUserDAO.findUserByUserId(userId);
	}

	@Override
	public void saveOrUpdateAutotaskUser(AutotaskUserDTO dto, boolean hasNotesEnabled, boolean isNotesInternal, boolean isAttachmentsInternal,
	                                     Map<String, AutotaskUserCustomFieldsPreferenceDTO> preferenceMap) throws AutotaskAuthenticationException, AutotaskCustomFieldsException {
		Assert.notNull(dto);
		User user = userService.getUser(dto.getUserId());
		Assert.notNull(user);
		AutotaskUser newUser = new AutotaskUser(user, dto.getUserName(), dto.getPassword(), dto.getZoneUrl());

		Optional<AutotaskUser> existing = autotaskUserDAO.findUserByUserId(dto.getUserId());

		if (existing.isPresent()) {
			if(!dto.isUpdatePassword()) {
				String password = existing.get().getPassword();
				newUser.setPassword(password);
			}
			BeanUtilities.copyProperties(existing.get(), newUser, new String[]{"id", "user"});
			autotaskUserDAO.saveOrUpdate(existing.get());
			saveOrUpdateAutotaskUserPreference(existing.get(), new NotificationType(NotificationType.AUTOTASK_NOTES_ENABLED), hasNotesEnabled);
			saveOrUpdateAutotaskUserPreference(existing.get(), new NotificationType(NotificationType.AUTOTASK_NOTES_INTERNAL), isNotesInternal);
			saveOrUpdateAutotaskUserPreference(existing.get(), new NotificationType(NotificationType.AUTOTASK_ATTACHMENTS_INTERNAL), isAttachmentsInternal);
			newUser = existing.get();
		} else {
			autotaskUserDAO.saveOrUpdate(newUser);
			saveOrUpdateAutotaskUserPreference(newUser, new NotificationType(NotificationType.AUTOTASK_NOTES_ENABLED), hasNotesEnabled);
			saveOrUpdateAutotaskUserPreference(newUser, new NotificationType(NotificationType.AUTOTASK_NOTES_INTERNAL), isNotesInternal);
			saveOrUpdateAutotaskUserPreference(newUser, new NotificationType(NotificationType.AUTOTASK_ATTACHMENTS_INTERNAL), isAttachmentsInternal);
		}

		for( Map.Entry<String, AutotaskUserCustomFieldsPreferenceDTO> entry :  preferenceMap.entrySet()) {

			if(entry.getValue().getCustomFieldValue().isEmpty()) {
				if(entry.getValue().isEnabled()) {
					throw new AutotaskCustomFieldsException();
				} else {
					entry.getValue().setCustomFieldValue(getAutotaskProps().get(entry.getKey()));
				}
			}

			Optional<AutotaskUserCustomFieldsPreference> optPreference =
					autotaskUserCustomFieldsPreferenceDAO.findByAutotaskUserAndIntegrationCustomFieldCode(newUser.getId(), entry.getKey());
			if(optPreference.isPresent()) {
				optPreference.get().setCustomFieldName(entry.getValue().getCustomFieldValue());
				optPreference.get().setEnabled(entry.getValue().isEnabled());
				autotaskUserCustomFieldsPreferenceDAO.saveOrUpdate(optPreference.get());
			} else {
				AutotaskUserCustomFieldsPreference preference = new AutotaskUserCustomFieldsPreference();
				preference.setAutotaskUser(newUser);
				preference.setEnabled(entry.getValue().isEnabled());
				preference.setCustomFieldName(entry.getValue().getCustomFieldValue());
				preference.setIntegrationCustomField(new IntegrationCustomField(entry.getKey()));

				autotaskUserCustomFieldsPreferenceDAO.saveOrUpdate(preference);
			}
		}
	}

	@Override
	public void updateAutotaskTicket(AutotaskTicket autotaskTicket, Long workId) {
		Assert.notNull(autotaskTicket);
		Assert.notNull(workId);

		Optional<AutotaskTicketWorkAssociation> existing =
				autotaskTicketWorkAssociationDAO.findAutotaskTicketWorkAssociationByTicketIdAndWorkId(autotaskTicket.getTicketId(), workId);
		if (existing.isPresent()) {
			existing.get().getTicket().setTicketNumber(autotaskTicket.getTicketNumber());
			autotaskTicketWorkAssociationDAO.saveOrUpdate(existing.get());
		}
	}

	@Override
	public Optional<String> findZoneUrl(String userName) {

		AutotaskProxy proxy = autotaskProxyFactory.newInstance();
		Optional<String> result = Optional.absent();
		try {
			ATWSZoneInfo zoneInfo = proxy.getZoneInfo(userName);
			if (zoneInfo != null && StringUtils.isNotBlank(zoneInfo.getURL()))
				result = Optional.fromNullable(zoneInfo.getURL());
		} catch (WebServiceException e) {
			logger.warn("[autotask] error getting zone URL: ", e);
		}
		return result;
	}

	@Override
	public Optional<String> getThresholdAndUsageInfo(String userName, String password, String zoneUrl) {
		AutotaskProxy proxy = autotaskProxyFactory.newInstance(userName, password, zoneUrl);
		Optional<String> result = Optional.absent();
		try {
			ATWSResponse response = proxy.getThresholdAndUsageInfo();
			if (isSuccessful(response))
				result = Optional.fromNullable(response.getEntityReturnInfoResults().getEntityReturnInfo().get(0).getMessage());
		} catch (WebServiceException e) {
			logger.warn("[autotask] error getting threshold and usage info: ", e);
		}
		return result;
	}

	@Override
	public List<Entity> processATWSResponse(ATWSResponse response) {
		if (!isSuccessful(response)) {
			return null;
		}

		return response.getEntityResults().getEntity();
	}

	/**
	 * TODO: when external_work_number becomes a real feature, this should go in one of the Work services
	 *
	 * @param workId
	 * @param externalWorkNumber
	 */
	@Override
	public void createExternalWorkNumberForWork(Long workId, Long externalWorkNumber) {
		AutotaskTicketWorkAssociation assoc = new AutotaskTicketWorkAssociation()
				.setWorkId(checkNotNull(workId))
				.setTicket(new AutotaskTicket()
						.setTicketId(checkNotNull(externalWorkNumber)));
		autotaskTicketWorkAssociationDAO.saveOrUpdate(assoc);
	}

	@Override
	public Optional<String> getThresholdAndUsageInfo(Long autotaskId) {
		Assert.notNull(autotaskId);
		AutotaskUser autotaskUser = autotaskUserDAO.get(autotaskId);
		Assert.notNull(autotaskUser);
		return getThresholdAndUsageInfo(autotaskUser.getUserName(), autotaskUser.getPassword(), autotaskUser.getZoneUrl());
	}

	@Override
	public boolean validateCredentials(String userName, String password, String zoneUrl) {
		return getThresholdAndUsageInfo(userName, password, zoneUrl).isPresent();
	}


	public boolean isCreatedByAutotask(Long workId) {
		Assert.notNull(workId);
		return autotaskTicketWorkAssociationDAO.findAutotaskTicketWorkAssociationByWorkId(workId).isPresent();
	}

	@Override
	public Optional<AutotaskTicket> findAutotaskTicketByWorkId(AutotaskUser autotaskUser, Long workId) {
		Assert.notNull(autotaskUser);
		Assert.notNull(workId);

		Optional<AutotaskTicket> result = Optional.absent();
		Optional<AutotaskTicketWorkAssociation> associationOpt = autotaskTicketWorkAssociationDAO.findAutotaskTicketWorkAssociationByWorkId(workId);

		if (associationOpt.isPresent())
			result = Optional.of(associationOpt.get().getTicket());

		return result;
	}

	@Override
	public boolean updateTicketUserDefinedFields(AutotaskUser autotaskUser, Long ticketId, ArrayOfUserDefinedField fieldsArray) {

		String queryXML = new AutotaskQueryXmlBuilder("ticket").addCondition("TicketId", Condition.Operations.EQUALS, String.valueOf(ticketId)).build();
		try {
			ATWSResponse response = autotaskProxyFactory.newInstance(autotaskUser).query(queryXML);
			List<Entity> entities = processATWSResponse(response);

			return entities.size() > 0 && updateTicketUserDefinedFields(autotaskUser, (Ticket) entities.get(0), fieldsArray);

		} catch (WebServiceException e) {
			logger.warn(String.format("[autotask] error updating UDFs for user %d", autotaskUser.getUserId()), e);
		}
		return false;
	}

	@Override
	public boolean updateTicketUserDefinedFields(AutotaskUser autotaskUser, Ticket ticket, ArrayOfUserDefinedField fieldsArray){
		AutotaskProxy proxy = autotaskProxyFactory.newInstance(autotaskUser);

		ticket.setUserDefinedFields(fieldsArray);

		ArrayOfEntity arrayOfEntity = new ArrayOfEntity();
		(arrayOfEntity.getEntity()).add(ticket);
		ATWSResponse response;
		try {
			response = proxy.update(arrayOfEntity);
		} catch (WebServiceException e) {
			logger.warn(String.format("[autotask] error updating UDFs for user %d", autotaskUser.getUserId()), e);
			return false;
		}

		if (isSuccessful(response)) {
			return true;
		} else {
			List<ATWSError> atwsErrors = (response.getErrors()).getATWSError();
			String errorMessages = null;
			for (ATWSError atwsError : atwsErrors) {
				logger.error(String.format("[integration] updateTicketUserDefinedFields - errorMessage=%s", atwsError.getMessage()));
				errorMessages += atwsError.getMessage() + "\n";
			}
			Assert.isNull(errorMessages, "\n[Autotask - Integration]\n" +  errorMessages);
			return false;
		}
	}

	@Override
	public AutotaskUser findAutotaskUserByCompanyId(Long companyId) {
		Assert.notNull(companyId);
		return autotaskUserDAO.findUserByCompanyId(companyId);
	}

	@Override
	public List<AutotaskUserCustomFieldsPreference> findAutotaskCustomFieldPreferencesByAutotaskUser(Long companyId) {
		Assert.notNull(companyId);
		AutotaskUser autotaskUser = autotaskUserDAO.findUserByCompanyId(companyId);
		Assert.notNull(autotaskUser);

		List<AutotaskUserCustomFieldsPreference> preferences = autotaskUserCustomFieldsPreferenceDAO.findAllPreferencesByAutotaskUser(autotaskUser.getId()).get();
		Assert.notEmpty(preferences);

		return preferences;
	}

	@Override
	public AutotaskUserForm populateAutotaskUserForm(Long companyId, Long userId) {
		AutotaskUserForm form = new AutotaskUserForm();
		Optional<User> apiUserOpt = registrationService.getApiUserByUserId(userId);
		boolean hasApiUser = apiUserOpt.isPresent();

		if (hasApiUser) {
			Optional<AutotaskUser> atUserOpt = findAutotaskUserByUserId(apiUserOpt.get().getId());
			if (atUserOpt.isPresent()) {
				AutotaskUserDTO dto = AutotaskUserDTO.newDTO(atUserOpt.get());
				dto.setPassword("******");
				form.setAutotaskUser(dto);
				form.setNotesEnabled(findAutotaskUserPreference(atUserOpt.get().getId(), NotificationType.AUTOTASK_NOTES_ENABLED));

				form.setNotesInternal(form.isNotesEnabled()?
						findAutotaskUserPreference(atUserOpt.get().getId(),  NotificationType.AUTOTASK_NOTES_INTERNAL) : Boolean.FALSE);
				form.setAttachmentsInternal(findAutotaskUserPreference(atUserOpt.get().getId(), NotificationType.AUTOTASK_ATTACHMENTS_INTERNAL));

				List<AutotaskUserCustomFieldsPreference> types = findAutotaskCustomFieldPreferencesByAutotaskUser(companyId);
				Map<String, AutotaskUserCustomFieldsPreferenceDTO> typesMap = Maps.newHashMap();

				for(AutotaskUserCustomFieldsPreference type : types) {
					String fieldCode = type.getIntegrationCustomField().getCode();
					String fieldCustomValue = type.getCustomFieldName();
					boolean isFieldEnabled = type.isEnabled();
					AutotaskUserCustomFieldsPreferenceDTO customFieldsPreferenceDTOdto =
							new AutotaskUserCustomFieldsPreferenceDTO(fieldCode, fieldCustomValue, isFieldEnabled);
					typesMap.put(fieldCode, customFieldsPreferenceDTOdto);
				}

				form.setPreferenceMap(typesMap);
			} else {
				form.setAutotaskUser(new AutotaskUserDTO());
				form.setNotesEnabled(false);
				form.setNotesInternal(false);
				form.setAttachmentsInternal(false);
			}
		}
		form.setHasApiUser(hasApiUser);
		return form;
	}

	@Override
	public boolean updateTicketOnWorkCreated(AutotaskUser autotaskUser, AutotaskTicket autotaskTicket, AbstractWork work,  Map<String, AutotaskUserCustomFieldsPreference> preferenceMap) {
		Ticket ticket = fetchTicketByTicketId(autotaskUser, autotaskTicket.getTicketId());

		if (autotaskTicket.getTicketNumber() == null) {
			autotaskTicket.setTicketNumber((String) ticket.getTicketNumber());
			updateAutotaskTicket(autotaskTicket, work.getId());
		}

		ArrayOfUserDefinedField fields = new ArrayOfUserDefinedField();

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_WORK_ID));
		UserDefinedField field = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_WORK_ID).getCustomFieldName());
		field.setValue(work.getWorkNumber());

		fields.getUserDefinedField().add(field);

		return updateTicketUserDefinedFields(autotaskUser, ticket, fields);
	}

	@Override
	public boolean updateTicketOnWorkAccepted(AutotaskUser autotaskUser, AutotaskTicket autotaskTicket, AbstractWork work, WorkResource resource, Map<String, AutotaskUserCustomFieldsPreference> preferenceMap) {
		Ticket ticket = fetchTicketByTicketId(autotaskUser, autotaskTicket.getTicketId());
		ArrayOfUserDefinedField fieldArray = new ArrayOfUserDefinedField();
		User resourceUser = resource.getUser();

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_STATUS));
		UserDefinedField wmStatusField = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_STATUS).getCustomFieldName());
		wmStatusField.setValue(work.getWorkStatusType().getCode());
		fieldArray.getUserDefinedField().add(wmStatusField);

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_RESOURCE_ID));
		if(preferenceMap.get(IntegrationCustomField.WM_RESOURCE_ID).isEnabled()) {
			UserDefinedField wmResourceIdField= generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_RESOURCE_ID).getCustomFieldName());
			wmResourceIdField.setValue(String.valueOf(resource.getUser().getUserNumber()));
			fieldArray.getUserDefinedField().add(wmResourceIdField);
		}

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_RESOURCE_FIRST_NAME));
		if(preferenceMap.get(IntegrationCustomField.WM_RESOURCE_FIRST_NAME).isEnabled()){
			UserDefinedField wmResourceFirstNameField = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_RESOURCE_FIRST_NAME).getCustomFieldName());
			wmResourceFirstNameField.setValue(resourceUser.getFirstName());
			fieldArray.getUserDefinedField().add(wmResourceFirstNameField);
		}

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_RESOURCE_LAST_NAME));
		if(preferenceMap.get(IntegrationCustomField.WM_RESOURCE_LAST_NAME).isEnabled()){
			UserDefinedField wmResourceLastNameField = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_RESOURCE_LAST_NAME).getCustomFieldName());
			wmResourceLastNameField.setValue(resourceUser.getLastName());
			fieldArray.getUserDefinedField().add(wmResourceLastNameField);
		}

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_RESOURCE_EMAIL));
		if(preferenceMap.get(IntegrationCustomField.WM_RESOURCE_EMAIL).isEnabled()){
			UserDefinedField wmResourceEmailField = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_RESOURCE_EMAIL).getCustomFieldName());
			wmResourceEmailField.setValue(resourceUser.getEmail());
			fieldArray.getUserDefinedField().add(wmResourceEmailField);
		}

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_RESOURCE_PHONE));
		if(preferenceMap.get(IntegrationCustomField.WM_RESOURCE_PHONE).isEnabled()){
			UserDefinedField wmResourcePhoneField = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_RESOURCE_PHONE).getCustomFieldName());
			wmResourcePhoneField.setValue(resourceUser.getProfile().getMobilePhone());
			fieldArray.getUserDefinedField().add(wmResourcePhoneField);
		}

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_RESOURCE_PHONE_MOBILE));
		if(preferenceMap.get(IntegrationCustomField.WM_RESOURCE_PHONE_MOBILE).isEnabled()){
			UserDefinedField wmResourcePhone2Field = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_RESOURCE_PHONE_MOBILE).getCustomFieldName());
			wmResourcePhone2Field.setValue(resourceUser.getProfile().getWorkPhone());
			fieldArray.getUserDefinedField().add(wmResourcePhone2Field);
		}

		return updateTicketUserDefinedFields(autotaskUser, ticket, fieldArray);
	}

	@Override
	public boolean updateTicketToCheckInOut(AutotaskUser autotaskUser, AutotaskTicket autotaskTicket, WorkResource resource, Map<String, AutotaskUserCustomFieldsPreference> preferenceMap) {
		Ticket ticket = fetchTicketByTicketId(autotaskUser, autotaskTicket.getTicketId());
		ArrayOfUserDefinedField fieldArray = new ArrayOfUserDefinedField();
		WorkResourceTimeTracking checkInOut = workService.findLatestTimeTrackRecordByWorkResource(resource.getId());
		boolean hasData = false;


		Assert.notNull(checkInOut);

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_CHECKEDIN_ON));
		if(null != checkInOut.getCheckedInOn() && preferenceMap.get(IntegrationCustomField.WM_CHECKEDIN_ON).isEnabled()){
			UserDefinedField checkedInOnField = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_CHECKEDIN_ON).getCustomFieldName());
			checkedInOnField.setValue(DateUtilities.formatDateForTimeZone(checkInOut.getCheckedInOn().getTime(), Constants.WM_TIME_ZONE));
			fieldArray.getUserDefinedField().add(checkedInOnField);
			hasData = true;
		}

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_CHECKEDOUT_ON));
		if(null != checkInOut.getCheckedOutOn() && preferenceMap.get(IntegrationCustomField.WM_CHECKEDOUT_ON).isEnabled()){
			UserDefinedField checkedOutOnField = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_CHECKEDOUT_ON).getCustomFieldName());
			checkedOutOnField.setValue(DateUtilities.formatDateForTimeZone(checkInOut.getCheckedOutOn().getTime(), Constants.WM_TIME_ZONE));
			fieldArray.getUserDefinedField().add(checkedOutOnField);
			hasData = true;
		}

		return !hasData || updateTicketUserDefinedFields(autotaskUser, ticket, fieldArray);
	}

	@Override
	public boolean updateTicketOnWorkComplete(AutotaskUser autotaskUser, AutotaskTicket autotaskTicket, AbstractWork work, PaymentSummaryDTO payment, Map<String, AutotaskUserCustomFieldsPreference> preferenceMap) {
		ATWSResponse response = fetchATWSResponseByTicketId(autotaskUser, autotaskTicket.getTicketId());
		Ticket ticket = getTicketFromATWSResponse(response);
		ArrayOfUserDefinedField fieldArray = new ArrayOfUserDefinedField();
		boolean hasData = false;

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_RESOLUTION));
		if(preferenceMap.get(IntegrationCustomField.WM_RESOLUTION).isEnabled()) {
			UserDefinedField resolutionField = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_RESOLUTION).getCustomFieldName());
			resolutionField.setValue(work.getResolution());
			fieldArray.getUserDefinedField().add(resolutionField);
			hasData = true;
		}

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_MAX_SPEND_LIMIT));
		if(null != payment.getMaxSpendLimit() && preferenceMap.get(IntegrationCustomField.WM_MAX_SPEND_LIMIT).isEnabled()){
			UserDefinedField maxSpendLimit = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_MAX_SPEND_LIMIT).getCustomFieldName());
			maxSpendLimit.setValue(String.valueOf(NumberUtilities.roundMoney(payment.getMaxSpendLimit())));
			fieldArray.getUserDefinedField().add(maxSpendLimit);
			hasData = true;
		}

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_ACTUAL_SPEND_LIMIT));
		if(payment.getActualSpendLimit() != null && preferenceMap.get(IntegrationCustomField.WM_ACTUAL_SPEND_LIMIT).isEnabled()){
			UserDefinedField actualSpendLimit = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_ACTUAL_SPEND_LIMIT).getCustomFieldName());
			actualSpendLimit.setValue(String.valueOf(NumberUtilities.roundMoney(payment.getActualSpendLimit())));
			fieldArray.getUserDefinedField().add(actualSpendLimit);
			hasData = true;
		}

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_HOURS_WORKED));
		if(payment.getHoursWorked() != null && preferenceMap.get(IntegrationCustomField.WM_HOURS_WORKED).isEnabled()){
			UserDefinedField hoursWorked = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_HOURS_WORKED).getCustomFieldName());
			hoursWorked.setValue(String.valueOf(payment.getHoursWorked()));
			fieldArray.getUserDefinedField().add(hoursWorked);
			hasData = true;
		}

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_TOTAL_COST));
		if(payment.getTotalCost() != null && preferenceMap.get(IntegrationCustomField.WM_TOTAL_COST).isEnabled()){
			UserDefinedField totalCost = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_TOTAL_COST).getCustomFieldName());
			totalCost.setValue(String.valueOf(NumberUtilities.roundMoney(payment.getTotalCost())));
			fieldArray.getUserDefinedField().add(totalCost);
			hasData = true;
		}

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_SPENT_LIMIT));
		if(payment.getActualSpendLimit() != null && preferenceMap.get(IntegrationCustomField.WM_SPENT_LIMIT).isEnabled()){
			UserDefinedField spendLimit = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_SPENT_LIMIT).getCustomFieldName());
			spendLimit.setValue(String.valueOf(NumberUtilities.roundMoney(payment.getActualSpendLimit())));
			fieldArray.getUserDefinedField().add(spendLimit);
			hasData = true;
		}

		Optional<BigDecimal> additionalExpensesOpt = getAdditionalExpenses(payment, work);
		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_ADDITIONAL_EXPENSES));
		if (additionalExpensesOpt.isPresent() && preferenceMap.get(IntegrationCustomField.WM_ADDITIONAL_EXPENSES).isEnabled()) {
			UserDefinedField additionalExpenses = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_ADDITIONAL_EXPENSES).getCustomFieldName());
			additionalExpenses.setValue(String.valueOf(NumberUtilities.roundMoney(additionalExpensesOpt.get())));
			fieldArray.getUserDefinedField().add(additionalExpenses);
			hasData = true;
		}

		return !hasData || updateTicketUserDefinedFields(autotaskUser, ticket, fieldArray);
	}

	@Override
	public boolean updateTicketOnWorkApproved(AutotaskUser autotaskUser, AutotaskTicket autotaskTicket, AbstractWork work, PaymentSummaryDTO payment, Map<String, AutotaskUserCustomFieldsPreference> preferenceMap) {
		Ticket ticket = fetchTicketByTicketId(autotaskUser, autotaskTicket.getTicketId());
		ArrayOfUserDefinedField fieldArray = new ArrayOfUserDefinedField();
		boolean hasData = false;

		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_SPENT_LIMIT));
		if(payment.getActualSpendLimit() != null && preferenceMap.get(IntegrationCustomField.WM_SPENT_LIMIT).isEnabled()){
			UserDefinedField spendLimitField = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_SPENT_LIMIT).getCustomFieldName());
			spendLimitField.setValue(String.valueOf(NumberUtilities.roundMoney(payment.getActualSpendLimit())));
			fieldArray.getUserDefinedField().add(spendLimitField);
			hasData = true;
		}

		Optional<BigDecimal> additionalExpensesOpt = getAdditionalExpenses(payment, work);
		Assert.isTrue(preferenceMap.containsKey(IntegrationCustomField.WM_ADDITIONAL_EXPENSES));
		if (additionalExpensesOpt.isPresent() && preferenceMap.get(IntegrationCustomField.WM_ADDITIONAL_EXPENSES).isEnabled()) {
			UserDefinedField additionalExpenses = generateUDFFieldFromATProps(preferenceMap.get(IntegrationCustomField.WM_ADDITIONAL_EXPENSES).getCustomFieldName());
			additionalExpenses.setValue(String.valueOf(NumberUtilities.roundMoney(additionalExpensesOpt.get())));
			fieldArray.getUserDefinedField().add(additionalExpenses);
			hasData = true;
		}

		return !hasData || updateTicketUserDefinedFields(autotaskUser, ticket, fieldArray);
	}

	private Optional<BigDecimal> getAdditionalExpenses(PaymentSummaryDTO payment, AbstractWork work) {
		if (payment.getAdditionalExpenses() != null && payment.getAdditionalExpenses().compareTo(BigDecimal.ZERO) == 1)
			return Optional.of(payment.getAdditionalExpenses());
		else if (work.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses() != null &&
				work.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses().compareTo(BigDecimal.ZERO) == 1)
			return Optional.of(work.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses());
		else
			return Optional.absent();
	}

	@Override
	public boolean updateTicketOnAttachmentData(AutotaskUser autotaskUser, AutotaskTicket autotaskTicket, Asset asset){
		boolean isAttachmentsInternal = findAutotaskUserPreference(autotaskUser.getId(), NotificationType.AUTOTASK_ATTACHMENTS_INTERNAL);

		AutotaskProxy autotaskProxy = autotaskProxyFactory.newInstance(autotaskUser);
		Long atAttachmentStoreId = -1L;

		Attachment atAttachment = new Attachment();
		AttachmentInfo attachmentInfo = new AttachmentInfo();

		attachmentInfo.setTitle(asset.getName());
		attachmentInfo.setFullPath(BASE_URL + asset.getUri());
		attachmentInfo.setFileSize(asset.getFileByteSize());
		attachmentInfo.setParentID(autotaskTicket.getTicketId());
		attachmentInfo.setParentType(AUTOTASK_TICKET_TYPE);
		attachmentInfo.setPublish(isAttachmentsInternal? AUTOTASK_PUBLISH_ATTACHMENTS_INTERNAL : AUTOTASK_PUBLISH_ATTACHMENTS_NON_INTERNAL);
		attachmentInfo.setType(AUTOTASK_ATTACHMENT_TYPE_URL);

		atAttachment.setInfo(attachmentInfo);

		try {
			atAttachmentStoreId = autotaskProxy.createAttachment(atAttachment);
		} catch (WebServiceException ex) {
			logger.warn(String.format("[autotask] error updating attachment to autotask for user:%d assetId:%d", autotaskUser.getUserId(), asset.getId()), ex);
		}

		return atAttachmentStoreId >= 0;
	}

	@Override
	public boolean findAutotaskUserPreference(Long autotaskUserId, String notification_code) {
		Assert.notNull(autotaskUserId);
		Assert.notNull(notification_code);

		Optional<AutotaskUserPreference> preferenceOptional = autotaskUserPreferenceDAO.findByAutotaskUserAndNotificationType(autotaskUserId, notification_code);

		return preferenceOptional.isPresent()? preferenceOptional.get().getFlag() : Boolean.TRUE;
	}

	@Override
	public void saveOrUpdateAutotaskUserPreference(AutotaskUser autotaskUser, NotificationType notificationType, boolean flag) {
		Assert.notNull(autotaskUser);
		Assert.notNull(notificationType);
		Optional<AutotaskUserPreference> existingPreference = autotaskUserPreferenceDAO.findByAutotaskUserAndNotificationType(autotaskUser.getId(), notificationType.getCode());

		if(!existingPreference.isPresent()) {
			AutotaskUserPreference preference = new AutotaskUserPreference();
			preference.setAutotaskUser(autotaskUser);
			preference.setNotificationType(notificationType);
			preference.setFlag(flag);
			autotaskUserPreferenceDAO.saveOrUpdate(preference);
		} else {
			existingPreference.get().setFlag(flag);
			autotaskUserPreferenceDAO.saveOrUpdate(existingPreference.get());
		}
	}

	@Override
	public boolean updateTicketOnNoteAdded(AutotaskUser autotaskUser, AutotaskTicket autotaskTicket, Note note) {
		boolean isNotesInternal = findAutotaskUserPreference(autotaskUser.getId(), NotificationType.AUTOTASK_NOTES_INTERNAL);

		TicketNote ticketNote = new TicketNote();
		AutotaskProxy proxy = autotaskProxyFactory.newInstance(autotaskUser);
		User noteCreator = userService.findUserById(note.getCreatorId());

		ticketNote.setTicketID(autotaskTicket.getTicketId());
		ticketNote.setPublish(isNotesInternal? AUTOTASK_PUBLISH_NOTES_INTERNAL : AUTOTASK_PUBLISH_NOTES_NON_INTERNAL);
		ticketNote.setNoteType(AUTOTASK_NOTE_TYPE_TASK_NOTE);
		ticketNote.setDescription(note.getContent());
		ticketNote.setLastActivityDate(note.getCreatedOn());
		ticketNote.setTitle(String.format("Added By %s %s via Work Market", noteCreator.getFirstName(), noteCreator.getLastName()));

		ArrayOfEntity arrayOfEntity = new ArrayOfEntity();
		arrayOfEntity.getEntity().add(ticketNote);
		ATWSResponse response;
		try {
			response = proxy.create(arrayOfEntity);
		} catch (WebServiceException e) {
			logger.warn("[autotask] Error creating Autotask note: ", e);
			return false;
		}

		return isSuccessful(response);
	}


	private boolean isSuccessful(ATWSResponse response) {
		return (response != null && response.getReturnCode() == 1);
	}

	private UserDefinedField generateUDFFieldFromATProps(String element) {
		UserDefinedField field = new UserDefinedField();
		field.setName(element);

		Assert.hasText(field.getName());

		return field;
	}

	private Ticket fetchTicketByTicketId(AutotaskUser autotaskUser, Long ticketId) {
		ATWSResponse response = fetchATWSResponseByTicketId(autotaskUser, ticketId);
		return getTicketFromATWSResponse(response);
	}

	private ATWSResponse fetchATWSResponseByTicketId(AutotaskUser autotaskUser, Long ticketId) {
		AutotaskProxy proxy = autotaskProxyFactory.newInstance(autotaskUser);

		ATWSResponse response;
		try {
			response = proxy.query(new AutotaskQueryXmlBuilder(AutotaskQueryXmlBuilder.ENTITY_TICKET)
					.addCondition("id", Condition.Operations.EQUALS, ticketId.toString())
					.build());
		} catch (WebServiceException e) {
			logger.warn("[autotask] Error fetching Autotask ticket: ", e);
			return null;
		}
		Assert.isTrue(isSuccessful(response));

		return response;
	}

	private Ticket getTicketFromATWSResponse(ATWSResponse response) {
		Assert.isTrue(isSuccessful(response));
		List<Entity> tickets = response.getEntityResults().getEntity();
		Assert.isTrue(CollectionUtils.size(tickets) == 1);

		return (Ticket) tickets.get(0);
	}

}
