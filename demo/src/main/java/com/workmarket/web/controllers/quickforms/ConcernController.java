package com.workmarket.web.controllers.quickforms;

import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.model.note.concern.AssessmentConcern;
import com.workmarket.domains.model.note.concern.Concern;
import com.workmarket.domains.model.note.concern.InvitationConcern;
import com.workmarket.domains.model.note.concern.ProfileConcern;
import com.workmarket.domains.model.note.concern.RecruitingCampaignConcern;
import com.workmarket.domains.model.note.concern.UserGroupConcern;
import com.workmarket.domains.model.note.concern.WorkConcern;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.ClientSvcService;
import com.workmarket.service.business.InvitationService;
import com.workmarket.service.business.RecruitingService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.RegistrationConcernDTO;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.infra.EncryptionService;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.AssessmentConcernValidator;
import com.workmarket.web.validators.InvitationConcernValidator;
import com.workmarket.web.validators.ProfileConcernValidator;
import com.workmarket.web.validators.RecruitingCampaignConcernValidator;
import com.workmarket.web.validators.UserGroupConcernValidator;
import com.workmarket.web.validators.WorkConcernValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/quickforms")
public class ConcernController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(ConcernController.class);
	protected Map<Class, Validator> validators;
	@Autowired private ClientSvcService clientSvcService;
	@Autowired private EncryptionService encryptionService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private UserGroupService userGroupService;
	@Autowired private AssessmentService assessmentService;
	@Autowired private UserService userService;
	@Autowired private RecruitingService recruitingService;
	@Autowired private InvitationService invitationService;
	@Autowired private WorkService workService;
	@Autowired private AssessmentConcernValidator assessmentConcernValidator;
	@Autowired private InvitationConcernValidator invitationConcernValidator;
	@Autowired private ProfileConcernValidator profileConcernValidator;
	@Autowired private RecruitingCampaignConcernValidator recruitingCampaignConcernValidator;
	@Autowired private UserGroupConcernValidator userGroupConcernValidator;
	@Autowired private WorkConcernValidator workConcernValidator;

	@PostConstruct
	private void initializeValidators() {
		validators = new ImmutableMap.Builder<Class, Validator>()
			.put(AssessmentConcern.class, assessmentConcernValidator)
			.put(InvitationConcern.class, invitationConcernValidator)
			.put(ProfileConcern.class, profileConcernValidator)
			.put(RecruitingCampaignConcern.class, recruitingCampaignConcernValidator)
			.put(UserGroupConcern.class, userGroupConcernValidator)
			.put(WorkConcern.class, workConcernValidator)
			.build();
	}

	@ModelAttribute("concern")
	public Concern populateConcern(
		@RequestParam(value = "id", required = false, defaultValue = "null") Long entityId,
		@RequestParam(value = "encrypted_id", required = false) String encryptedId,
		@RequestParam(value = "type") String type,
		@RequestParam(value = "content", required = false) String content,
		@RequestParam(value = "name", required = false) String name,
		@RequestParam(value = "email", required = false) String email) {

		Concern concern = createConcern(type);

		if (concern != null) {
			if (StringUtils.hasText(encryptedId)) {
				entityId = encryptionService.decryptId(encryptedId);
			}

			concern.setId(entityId);
			concern.setContent(content);

			if (concern instanceof InvitationConcern) {
				InvitationConcern invitationConcern = (InvitationConcern) concern;
				invitationConcern.setUserName(name);
				invitationConcern.setEmail(email);
			} else if (concern instanceof RecruitingCampaignConcern) {
				RecruitingCampaignConcern campaignConcern = (RecruitingCampaignConcern) concern;
				campaignConcern.setUserName(name);
				campaignConcern.setEmail(email);
			}
		}

		return concern;
	}

	@RequestMapping(
		value = "concern",
		method = GET)
	public String index() {
		return "web/partials/quickforms/concern";
	}

	@RequestMapping(
		value = "/concern",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder doConcern(
		@ModelAttribute("concern") Concern concern, BindingResult result) {

		MessageBundle bundle = messageHelper.newBundle();

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		if (concern != null) {
			// Query the entity before validating otherwise it will always fail
			// Note that reportConcern performs this validation anyway so this may not be necessary and it may be better not to do it
			if ("work".equals(concern.getType())) {
				WorkConcern workConcern = ((WorkConcern) concern);
				workConcern.setWork(((Work) workService.findWork(concern.getId())));
			} else if ("profile".equalsIgnoreCase(concern.getType())) {
				ProfileConcern profileConcern = ((ProfileConcern) concern);
				profileConcern.setUser(userService.findUserById(concern.getId()));
			} else if ("group".equals(concern.getType())) {
				UserGroupConcern userGroupConcern = ((UserGroupConcern) concern);
				userGroupConcern.setGroup(userGroupService.findGroupById(concern.getId()));
			} else if ("invitation".equals(concern.getType())) {
				InvitationConcern invitationConcern = ((InvitationConcern) concern);
				invitationConcern.setInvitation(invitationService.findInvitationById(concern.getId()));
			} else if ("campaign".equals(concern.getType())) {
				RecruitingCampaignConcern recruitingCampaignConcern = ((RecruitingCampaignConcern) concern);
				recruitingCampaignConcern.setCampaign(recruitingService.findRecruitingCampaign(getCurrentUser().getCompanyId(), concern.getId()));
			} else if ("assessment".equals(concern.getType())) {
				AssessmentConcern assessmentConcern = ((AssessmentConcern) concern);
				assessmentConcern.setAssessment(assessmentService.findAssessment(concern.getId()));
			}
			Validator validator = validators.get(concern.getClass());
			validator.validate(concern, result);

			if (result.hasErrors()) {
				messageHelper.setErrors(bundle, result);
			} else {
				try {
					reportConcern(concern);
					messageHelper.addSuccess(bundle, "quickforms.concern.success");
					response.setSuccessful(true);
				} catch (Exception ex) {
					logger.error("error reporting concern", ex);
					messageHelper.addError(bundle, "quickforms.concern.failure");
					return response.setMessages(bundle.getErrors());
				}
			}
		}

		if (bundle.hasErrors()) {
			return response.setMessages(bundle.getErrors());
		}

		return response.setMessages(bundle.getSuccess());
	}

	protected void reportConcern(Concern concern) throws Exception {
		if (concern instanceof WorkConcern) {
			clientSvcService.reportWork(concern.getId(), concern.getContent());
		} else if (concern instanceof ProfileConcern) {
			clientSvcService.reportProfile(concern.getId(), concern.getContent());
		} else if (concern instanceof UserGroupConcern) {
			clientSvcService.reportUserGroup(concern.getId(), concern.getContent());
		} else if (concern instanceof InvitationConcern) {
			RegistrationConcernDTO dto = new RegistrationConcernDTO();
			InvitationConcern invitationConcern = (InvitationConcern) concern;
			dto.setUserName(invitationConcern.getUserName());
			dto.setEmail(invitationConcern.getEmail());
			dto.setMessage(invitationConcern.getContent());
			clientSvcService.reportInvitation(concern.getId(), dto);
		} else if (concern instanceof RecruitingCampaignConcern) {
			RegistrationConcernDTO dto = new RegistrationConcernDTO();
			RecruitingCampaignConcern campaignConcern = (RecruitingCampaignConcern) concern;
			dto.setUserName(campaignConcern.getUserName());
			dto.setEmail(campaignConcern.getEmail());
			dto.setMessage(campaignConcern.getContent());
			clientSvcService.reportRecruitingCampaign(getCurrentUser().getCompanyId(), concern.getId(), dto);
		} else if (concern instanceof AssessmentConcern) {
			clientSvcService.reportAssessment(concern.getId(), concern.getContent());
		} else {
			throw new IllegalArgumentException("Unsupported concern: " + concern.getClass().getName());
		}
	}

	protected Concern createConcern(String type) {
		if ("work".equals(type)) {
			return new WorkConcern();
		} else if ("profile".equalsIgnoreCase(type)) {
			return new ProfileConcern();
		} else if ("group".equals(type)) {
			return new UserGroupConcern();
		} else if ("invitation".equals(type)) {
			return new InvitationConcern();
		} else if ("campaign".equals(type)) {
			return new RecruitingCampaignConcern();
		} else if ("assessment".equals(type)) {
			return new AssessmentConcern();
		}

		return null;
	}
}
