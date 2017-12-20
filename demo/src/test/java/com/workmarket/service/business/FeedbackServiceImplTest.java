package com.workmarket.service.business;


import com.google.common.collect.Lists;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.dao.state.WorkSubStatusDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroupAssociation;
import com.workmarket.domains.model.feedback.FeedbackConcern;
import com.workmarket.domains.model.feedback.FeedbackPriority;
import com.workmarket.domains.model.feedback.FeedbackType;
import com.workmarket.domains.model.feedback.FeedbackUserGroupAssociation;
import com.workmarket.domains.work.facade.service.WorkFacadeService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.WorkTemplateService;
import com.workmarket.domains.work.service.follow.WorkFollowService;
import com.workmarket.domains.work.service.route.RoutingStrategyService;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.business.dto.FeedbackDTO;
import com.workmarket.service.business.dto.UploadDTO;
import com.workmarket.service.business.dto.WorkCustomFieldGroupDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.web.forms.work.WorkAssetForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackServiceImplTest {

	@Mock WorkFacadeService workFacadeService;
	@Mock WorkSubStatusService workSubStatusService;
	@Mock UserGroupService userGroupService;
	@Mock WorkRoutingService workRoutingService;
	@Mock RoutingStrategyService routingStrategyService;
	@Mock AssetManagementService assetManagementService;
	@Mock CustomFieldService customFieldService;
	@Mock WorkCustomFieldDAO workCustomFieldDAO;
	@Mock UserDAO userDAO;
	@Mock LookupEntityDAO lookupEntityDAO;
	@Mock CompanyDAO companyDAO;
	@Mock WorkSubStatusDAO workSubStatusDAO;
	@Mock EmailTemplateFactory emailTemplateFactory;
	@Mock NotificationService notificationService;
	@Mock AuthenticationService authenticationService;
	@Mock WorkTemplateService workTemplateService;
	@Mock WorkFollowService workFollowService;
	@Mock EventRouter eventRouter;

	@InjectMocks FeedbackServiceImpl feedbackService;

	private FeedbackDTO feedback;
	private FeedbackConcern concern;
	private WorkAssetForm workAssetForm;
	private FeedbackPriority priority;
	private FeedbackType feedbackType;
	private WorkCustomFieldGroup workCustomFieldGroup;
	private WorkCustomField workCustomField;
	private FeedbackUserGroupAssociation feedbackUserGroupAssociation;
	private WorkCustomFieldGroupAssociation workCustomFieldGroupAssociation;
	private Work work;
	private WorkTemplate template;
	private User support;
	private User system;
	private Company company;
	private final String FEEDBACK_TYPE = FeedbackType.PLATFORM;

	@Before
	public void setUp() throws Exception {
		workAssetForm = mock(WorkAssetForm.class);

		concern = mock(FeedbackConcern.class);
		when(concern.getDescription()).thenReturn("desc");
		when(concern.getCode()).thenReturn("code");

		priority = mock(FeedbackPriority.class);
		when(priority.getCode()).thenReturn("priority");
		when(priority.getDescription()).thenReturn("Priority");

		feedbackType = mock(FeedbackType.class);
		when(feedbackType.getDescription()).thenReturn("Type");
		when(feedbackType.getCode()).thenReturn("type");
		when(feedbackType.getMappedTemplateId()).thenReturn(RandomUtilities.nextLong());

		feedback = mock(FeedbackDTO.class);
		when(feedback.getDescription()).thenReturn("Desc");
		when(feedback.getTitle()).thenReturn("Title");
		when(feedback.getConcern()).thenReturn(concern);
		when(feedback.getPriority()).thenReturn(priority);
		when(feedback.getType()).thenReturn(FEEDBACK_TYPE);
		when(feedback.getUserAgent()).thenReturn("Browser");
		when(feedback.getUserId()).thenReturn(RandomUtilities.nextLong());

		work = mock(Work.class);
		when(work.getId()).thenReturn(RandomUtilities.nextLong());

		workCustomField = mock(WorkCustomField.class);
		when(workCustomField.getId()).thenReturn(RandomUtilities.nextLong());
		when(workCustomField.getName()).thenReturn(RandomUtilities.generateAlphaString(5));

		workCustomFieldGroup = mock(WorkCustomFieldGroup.class);
		when(workCustomFieldGroup.getName()).thenReturn(RandomUtilities.generateAlphaString(5));
		when(workCustomFieldGroup.getActiveWorkCustomFields()).thenReturn(Lists.newArrayList(workCustomField));

		doNothing().when(notificationService).sendNotification(any(EmailTemplate.class));

		when(workFacadeService.saveOrUpdateWork(anyLong(), any(WorkDTO.class))).thenReturn(work);

		feedbackUserGroupAssociation = mock(FeedbackUserGroupAssociation.class);
		when(feedbackUserGroupAssociation.getCode()).thenReturn(FEEDBACK_TYPE);
		when(feedbackUserGroupAssociation.getUserGroupId()).thenReturn(RandomUtilities.nextLong());
		when(feedbackUserGroupAssociation.getDescription()).thenReturn(FEEDBACK_TYPE);

		when(userGroupService.findGroupById(anyLong())).thenReturn(new UserGroup());
		when(lookupEntityDAO.findByCode(FeedbackUserGroupAssociation.class, FEEDBACK_TYPE)).thenReturn(feedbackUserGroupAssociation);
		when(workSubStatusService.findSystemWorkSubStatus(anyString())).thenReturn(new WorkSubStatusType());
		when(customFieldService.saveOrUpdateWorkFieldGroup(anyLong(), any(WorkCustomFieldGroupDTO.class))).thenReturn(workCustomFieldGroup);
		when(customFieldService.findWorkCustomFieldGroups(anyLong())).thenReturn(Lists.newArrayList(workCustomFieldGroup));

		template = mock(WorkTemplate.class);
		workCustomFieldGroupAssociation = mock(WorkCustomFieldGroupAssociation.class);
		when(template.getWorkCustomFieldGroupAssociations()).thenReturn(new HashSet<WorkCustomFieldGroupAssociation>());

		when(lookupEntityDAO.findByCode(FeedbackType.class, FEEDBACK_TYPE)).thenReturn(feedbackType);
		when(workTemplateService.findWorkTemplateById(anyLong())).thenReturn(template);

		company = mock(Company.class);
		when(company.getId()).thenReturn(RandomUtilities.nextLong());

		support = mock(User.class);
		when(support.getId()).thenReturn(RandomUtilities.nextLong());
		when(support.getCompany()).thenReturn(company);

		system = mock(User.class);
		when(system.getId()).thenReturn(Constants.WORKMARKET_SYSTEM_USER_ID);
		when(system.getCompany()).thenReturn(company);

		when(userDAO.findUserByEmail(Constants.SUPPORT_EMAIL)).thenReturn(support);
		when(userDAO.findUserById(Constants.WORKMARKET_SYSTEM_USER_ID)).thenReturn(system);


	}

	@Test
	public void createFeedback_Enhancement() {
		when(feedback.getType()).thenReturn(FeedbackType.ENHANCEMENT);
		feedbackService.convertFeedbackToWorkAndSend(feedback);

		verify(userDAO, times(1)).findUserById(eq(Constants.WORKMARKET_SYSTEM_USER_ID));
		verify(workFacadeService, times(1)).saveOrUpdateWork(eq(Constants.WORKMARKET_SYSTEM_USER_ID), any(WorkDTO.class));
	}
	@Test
	public void createFeedback_Product() {
		when(feedback.getType()).thenReturn(FeedbackType.PRODUCT);
		feedbackService.convertFeedbackToWorkAndSend(feedback);

		verify(userDAO, times(1)).findUserById(eq(Constants.WORKMARKET_SYSTEM_USER_ID));
		verify(workFacadeService, times(1)).saveOrUpdateWork(eq(Constants.WORKMARKET_SYSTEM_USER_ID), any(WorkDTO.class));
	}

	@Test
	public void createFeedback_Business() {
		when(feedback.getType()).thenReturn(FeedbackType.BUSINESS);
		feedbackService.convertFeedbackToWorkAndSend(feedback);

		verify(userDAO, times(1)).findUserByEmail(eq(Constants.SUPPORT_EMAIL));
		verify(workFacadeService, times(1)).saveOrUpdateWork(anyLong(), any(WorkDTO.class));
	}
	@Test
	public void createFeedback_Platform() {
		when(feedback.getType()).thenReturn(FeedbackType.PLATFORM);
		feedbackService.convertFeedbackToWorkAndSend(feedback);

		verify(userDAO, times(1)).findUserByEmail(eq(Constants.SUPPORT_EMAIL));
		verify(workFacadeService, times(1)).saveOrUpdateWork(anyLong(), any(WorkDTO.class));
	}

	@Test
	public void createFeedback_withDefaultValues_success() {
		feedbackService.convertFeedbackToWorkAndSend(feedback);
		verify(workFacadeService, times(1)).saveOrUpdateWork(anyLong(), any(WorkDTO.class));
		verify(routingStrategyService, times(1)).addGroupIdsRoutingStrategy(anyLong(), anySet(), anyInt(), anyBoolean());
	}

	@Test
	public void createFeedback_withoutSubStatus_successAndCreatesStatus() {
		feedbackService.convertFeedbackToWorkAndSend(feedback);
		verify(workFacadeService, times(1)).saveOrUpdateWork(anyLong(), any(WorkDTO.class));
		verify(routingStrategyService, times(1)).addGroupIdsRoutingStrategy(anyLong(), anySet(), anyInt(), anyBoolean());
		verify(workSubStatusDAO, times(1)).saveOrUpdate(any(WorkSubStatusType.class));
	}

	@Test
	public void createFeedback_withAssets_successAndCreatesAssets() throws Exception{
		List<WorkAssetForm> forms = Lists.newArrayList();
		when(workAssetForm.getMimeType()).thenReturn("any");
		when(workAssetForm.getName()).thenReturn("testf");
		when(workAssetForm.getUuid()).thenReturn(UUID.randomUUID().toString());
		forms.add(workAssetForm);
		when(feedback.getAttachments()).thenReturn(forms);
		feedbackService.convertFeedbackToWorkAndSend(feedback);
		verify(assetManagementService, times(1)).addUploadToWork(any(UploadDTO.class), anyLong());
		verify(routingStrategyService, times(1)).addGroupIdsRoutingStrategy(anyLong(), anySet(), anyInt(), anyBoolean());
	}

	@Test
	public void createFeedback_nullAssets_success() throws Exception {
		when(feedback.getAttachments()).thenReturn(null);
		feedbackService.convertFeedbackToWorkAndSend(feedback);
		verify(assetManagementService, times(0)).addUploadToWork(any(UploadDTO.class), anyLong());
		verify(routingStrategyService, times(1)).addGroupIdsRoutingStrategy(anyLong(), anySet(), anyInt(), anyBoolean());
	}

	@Test
	public void createFeedback_nullTemplate_success() throws Exception {
		when(feedbackType.getMappedTemplateId()).thenReturn(null);
		feedbackService.convertFeedbackToWorkAndSend(feedback);
		verify(routingStrategyService, times(1)).addGroupIdsRoutingStrategy(anyLong(), anySet(), anyInt(), anyBoolean());
	}
}
