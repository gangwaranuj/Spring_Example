package com.workmarket.service.business;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.dao.state.WorkSubStatusDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroupAssociation;
import com.workmarket.domains.model.customfield.WorkCustomFieldType;
import com.workmarket.domains.model.feedback.FeedbackConcern;
import com.workmarket.domains.model.feedback.FeedbackPriority;
import com.workmarket.domains.model.feedback.FeedbackType;
import com.workmarket.domains.model.feedback.FeedbackUserGroupAssociation;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.facade.service.WorkFacadeService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.WorkTemplateService;
import com.workmarket.domains.work.service.follow.WorkFollowService;
import com.workmarket.domains.work.service.route.RoutingStrategyService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.business.dto.FeedbackDTO;
import com.workmarket.service.business.dto.UploadDTO;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.dto.WorkCustomFieldGroupDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.forms.work.WorkAssetForm;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.CoreMatchers.equalTo;

@Service
public class FeedbackServiceImpl implements FeedbackService {

	@Autowired private WorkFacadeService workFacadeService;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private RoutingStrategyService workRoutingService;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private CustomFieldService customFieldService;
	@Autowired private WorkCustomFieldDAO workCustomFieldDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private LookupEntityDAO lookupEntityDAO;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private WorkSubStatusDAO workSubStatusDAO;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WorkTemplateService workTemplateService;
	@Autowired private WorkFollowService workFollowService;
	@Autowired private EventRouter eventRouter;

	private static final Logger logger = LoggerFactory.getLogger(FeedbackServiceImpl.class);

	private static final String GROUP_NAME = "Feedback Information";

	@Override
	public List<FeedbackConcern> getFeedbackConcerns() {
		return lookupEntityDAO.findLookupEntities(FeedbackConcern.class);
	}

	@Override
	public List<FeedbackPriority> getFeedbackPriorities() {
		return lookupEntityDAO.findLookupEntities(FeedbackPriority.class);
	}

	@Override
	public void convertFeedbackToWorkAndSend(FeedbackDTO feedback) {
		User user;
		if (FeedbackType.PLATFORM.equalsIgnoreCase(feedback.getType()) || FeedbackType.BUSINESS.equalsIgnoreCase(feedback.getType())) {
			user = userDAO.findUserByEmail(Constants.SUPPORT_EMAIL);
		}
		else {
			user = userDAO.findUserById(Constants.WORKMARKET_SYSTEM_USER_ID);
		}

		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

		Work work = convertFeedbackDTOToWork(feedback, user.getId());
		saveAttachments(feedback.getAttachments(), work);

		HashMap<String, String> fieldKeyValue = buildCustomFieldMap(feedback);
		saveCustomFields(work, user, fieldKeyValue);


		WorkSubStatusType workSubStatusType = getOrCreateSubStatus(feedback.getConcern().getCode(), user.getCompany());
		if (workSubStatusType != null) {
			workSubStatusService.addSubStatus(work.getId(), workSubStatusType.getId(), StringUtils.EMPTY);
		}

		UserGroup userGroup = getUserGroup(feedback.getType());
		if (userGroup != null) {
			workRoutingService.addGroupIdsRoutingStrategy(work.getId(), Sets.newHashSet(userGroup.getId()), 0, false);
		}
	}

	private HashMap<String, String> buildCustomFieldMap(FeedbackDTO feedback) {
		User submittingUser = userDAO.getUser(feedback.getUserId());
		Company company = companyDAO.findById(feedback.getCompanyId());
		String feedbackPriority = feedback.getPriority().getCode();
		String priority = (feedbackPriority.equals("-1")) ? StringUtils.EMPTY : feedbackPriority;

		HashMap<String, String> fieldKeyValue = Maps.newHashMap();
		fieldKeyValue.put("User Agent", feedback.getUserAgent());
		if (submittingUser != null) {
			fieldKeyValue.put("User Name", submittingUser.getFirstName() + " " + submittingUser.getLastName());
			fieldKeyValue.put("User Email", submittingUser.getEmail());
		}
		if (company != null) {
			fieldKeyValue.put("User Company", company.getName());
		}
		fieldKeyValue.put("Priority", priority);
		fieldKeyValue.put("Feedback Type", feedback.getType());
		return fieldKeyValue;
	}

	private Work convertFeedbackDTOToWork(FeedbackDTO feedback, Long userId) {
		FeedbackType feedbackType = lookupEntityDAO.findByCode(FeedbackType.class, feedback.getType());
		WorkTemplate template = null;

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle(StringUtilities.stripXSSAndEscapeHtml(feedback.getTitle()));
		workDTO.setDescription(StringUtilities.stripXSSAndEscapeHtml(feedback.getDescription()));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((long) (PricingStrategyType.INTERNAL.ordinal() + 1));
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(new DateTime().toString());
		workDTO.setIndustryId(Constants.WM_TIME_INDUSTRY_ID);
		workDTO.setUseMaxSpendPricingDisplayModeFlag(false);
		workDTO.setShowInFeed(false);
		workDTO.setIsOnsiteAddress(false);

		if (feedbackType != null && feedbackType.getMappedTemplateId() != null) {
			template = workTemplateService.findWorkTemplateById(feedbackType.getMappedTemplateId());
			if (template != null) {
				workDTO.setWorkTemplateId(template.getId());
			}
		}

		Work work = workFacadeService.saveOrUpdateWork(userId, workDTO);

		if (template != null) {
			int count = 1;
			for (WorkCustomFieldGroupAssociation ga : template.getWorkCustomFieldGroupAssociations()) {
				customFieldService.addWorkCustomFieldGroupToWork(ga.getWorkCustomFieldGroup().getId(), work.getId(), count++);
			}

			List<Long> followerIds = (List<Long>) CollectionUtils.collect(workFollowService.getWorkFollowers(template.getId()), new BeanToPropertyValueTransformer("id"));
			if (!followerIds.isEmpty()) {
				workFollowService.saveFollowers(work.getId(), followerIds, true);
			}
		}

		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));
		eventRouter.sendEvent(new UserSearchIndexEvent(userId));

		return work;
	}

	private UserGroup getUserGroup(final String name) {
		//Under the assumption that the user group association exists for the 4 different groups,
		FeedbackUserGroupAssociation association = lookupEntityDAO.findByCode(FeedbackUserGroupAssociation.class, name);
		UserGroup userGroup = null;

		if (association == null || association.getUserGroupId() == null) {
			logger.error(String.format("[feedback] could not find user group for mapping %s", name));
		} else {
			userGroup = userGroupService.findGroupById(association.getUserGroupId());
		}

		return userGroup;
	}

	private WorkCustomFieldGroup getOrCreateFeedbackFieldGroup(User user, HashMap<String, String> fieldKeyValue) {
		//Try to get the custom field group, just create one if it doesn't exist
		//Create a custom field group for priority, browser user-agent, os, other info, etc
		List<WorkCustomFieldGroup> customFieldGroupList = customFieldService.findWorkCustomFieldGroups(Constants.WM_COMPANY_ID);
		WorkCustomFieldGroup informationGroup = Iterables.find(customFieldGroupList, new Predicate<WorkCustomFieldGroup>() {
			@Override
			public boolean apply(@Nullable WorkCustomFieldGroup workCustomFieldGroup) {
				return (workCustomFieldGroup.getName().equals(GROUP_NAME));
			}
		}, null);

		//If not found, we should create it here
		if (informationGroup == null) {
			WorkCustomFieldGroupDTO groupDTO = new WorkCustomFieldGroupDTO();
			groupDTO.setName(GROUP_NAME);
			groupDTO.setPosition(0);

			List<WorkCustomFieldDTO> fieldDTOList = Lists.newArrayList();
			Integer position = 0;
			for (String key : fieldKeyValue.keySet()) {
				WorkCustomFieldDTO fieldDTO = new WorkCustomFieldDTO();
				fieldDTO.setShowOnDashboard(true);
				fieldDTO.setVisibleToResourceFlag(true);
				fieldDTO.setShowOnSentStatus(true);
				fieldDTO.setName(key);
				fieldDTO.setPosition(position++);
				fieldDTO.setDefaultValue("");
				fieldDTO.setValue("");
				fieldDTO.setWorkCustomFieldTypeCode(WorkCustomFieldType.RESOURCE);
				fieldDTOList.add(fieldDTO);
			}
			groupDTO.setWorkCustomFields(fieldDTOList);
			try {
				informationGroup = customFieldService.saveOrUpdateWorkFieldGroup(user.getId(), groupDTO);
				for (WorkCustomField field : informationGroup.getActiveWorkCustomFields()) {
					field.setWorkCustomFieldGroup(informationGroup);
					workCustomFieldDAO.saveOrUpdate(field);
				}
			} catch (Exception e) {
				logger.error("[feedback] Error saving custom field group ", e);
			}
		}
		return informationGroup;
	}

	private void saveCustomFields(Work work, User user, HashMap<String, String> fieldKeyValue) {
		WorkCustomFieldGroup informationGroup = getOrCreateFeedbackFieldGroup(user, fieldKeyValue);

		//populate custom field group
		CustomFieldGroup customFieldGroup = new CustomFieldGroup();
		for (Map.Entry<String, String> entry : fieldKeyValue.entrySet()) {
			CustomField customField = new CustomField();
			customField.setValue(entry.getValue());
			customField.setName(entry.getKey());
			customFieldGroup.addToFields(customField);
		}

		//save custom field group
		List<WorkCustomFieldDTO> dtos = Lists.newArrayList();

		if (informationGroup != null) {
			for (WorkCustomField field : informationGroup.getActiveWorkCustomFields()) {

				long fieldId = field.getId();
				CustomField submittedField = selectFirst(customFieldGroup.getFields(),
						having(on(CustomField.class).getName(), equalTo(field.getName()))
				);
				if (submittedField == null) {
					continue;
				}
				String submittedValue = submittedField.getValue();
				dtos.add(new WorkCustomFieldDTO(fieldId, submittedValue));
			}

			customFieldService.addWorkCustomFieldGroupToWork(informationGroup.getId(), work.getId(), 0);
			customFieldService.saveWorkCustomFieldsForWorkAndIndex(dtos.toArray(new WorkCustomFieldDTO[dtos.size()]), work.getId());
		}
	}

	private WorkSubStatusType getOrCreateSubStatus (String code, Company company) {
		WorkSubStatusType workSubStatusType = workSubStatusService.findCustomWorkSubStatus(code, company.getId());
		if (workSubStatusType == null) {
			workSubStatusType = new WorkSubStatusType();
			workSubStatusType.setActive(true);
			workSubStatusType.setCode(code);
			workSubStatusType.setDescription(String.format("Feedback %s", code));
			workSubStatusType.setSubStatusType(WorkSubStatusType.SubStatusType.ASSIGNMENT);
			workSubStatusType.setTriggeredBy(WorkSubStatusType.TriggeredBy.SYSTEM);
			workSubStatusType.setAlert(true);
			workSubStatusType.setClientVisible(true);
			workSubStatusType.setResourceVisible(true);
			workSubStatusType.setActionResolvable(true);
			workSubStatusType.setUserResolvable(true);
			workSubStatusType.setCustom(true);
			workSubStatusType.setCompany(company);
			workSubStatusDAO.saveOrUpdate(workSubStatusType);
		}
		return workSubStatusType;
	}

	private void saveAttachments(List<WorkAssetForm> assetFormList, Work work) {
		if (assetFormList == null) {
			return;
		}
		for (WorkAssetForm assetForm : assetFormList) {
			UploadDTO uploadDTO = new UploadDTO();

			uploadDTO.setName(assetForm.getName());
			uploadDTO.setMimeType(assetForm.getMimeType());
			uploadDTO.setDescription(assetForm.getDescription());
			uploadDTO.setUploadUuid(assetForm.getUuid());
			uploadDTO.setAssociationType(WorkAssetAssociationType.ATTACHMENT);
			try {
				assetManagementService.addUploadToWork(uploadDTO, work.getId());
			} catch (HostServiceException e) {
				logger.error(String.format("[feedback] Error storing upload %s", assetForm.getUuid()), e);
			}
		}
	}
}
