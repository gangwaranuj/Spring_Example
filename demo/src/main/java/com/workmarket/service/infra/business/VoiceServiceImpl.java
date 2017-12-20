package com.workmarket.service.infra.business;

import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.common.template.voice.VoiceTemplate;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.requirement.AbstractRequirementDAO;
import com.workmarket.dao.voice.VoiceCallDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.voice.InboundVoiceCall;
import com.workmarket.domains.model.voice.VoiceCall;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.VoiceResponseDTO;
import com.workmarket.service.business.template.TemplateService;
import com.workmarket.service.exception.IllegalWorkAccessException;
import com.workmarket.service.infra.communication.VoiceAdapter;
import com.workmarket.service.infra.voice.VoiceApplication;
import com.workmarket.service.infra.voice.VoiceApplicationFactory;
import com.workmarket.service.infra.voice.VoiceApplicationScreen;
import com.workmarket.service.infra.voice.VoiceCommand;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.naming.OperationNotSupportedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoiceServiceImpl implements VoiceService {

	private static final Log logger = LogFactory.getLog(VoiceServiceImpl.class);

	@Autowired private VoiceAdapter voiceAdapter;
	@Autowired private NotificationTemplateFactory notificationTemplateFactory;
	@Autowired private TemplateService templateService;
	@Autowired private UserService userService;
    @Autowired private WorkService workService;

	@Autowired private VoiceApplicationFactory voiceApplicationFactory;

	@Autowired private VoiceCallDAO voiceCallDAO;
	@Autowired private AbstractRequirementDAO abstractRequirementDAO;

	// Register templates for notification type

	private interface VoiceTemplateMapper {
		public VoiceTemplate getTemplate(VoiceCall call);
	}

	@SuppressWarnings("serial")
	private final Map<String,VoiceTemplateMapper> templateMap = new HashMap<String, VoiceTemplateMapper>() {{
		put(NotificationType.RESOURCE_WORK_CONFIRM, new VoiceTemplateMapper() {
			public VoiceTemplate getTemplate(VoiceCall call) {
				return notificationTemplateFactory.buildWorkResourceConfirmationNotificationTemplate(
					call.getUser().getId(),
					call.getWork(),
                    workService.getAppointmentTime(call.getWork().getId())
				).getVoiceTemplate();
			}
		});

		put(NotificationType.RESOURCE_WORK_INVITED, new VoiceTemplateMapper() {
			public VoiceTemplate getTemplate(VoiceCall call) {
				return notificationTemplateFactory.buildWorkInvitationNotificationTemplate(
					call.getUser().getId(),
					call.getWork(),
					abstractRequirementDAO.getMandatoryRequirementCountByWorkId(call.getWork().getId())
				).getVoiceTemplate();
			}
		});

		put(NotificationType.RESOURCE_WORK_CHECKIN, new VoiceTemplateMapper() {
			public VoiceTemplate getTemplate(VoiceCall call) {
				// Kludge alert: Since the call is incoming, initial states might not know who the user is.
				// Force a system user and check to see if we're ready to assign a user.
				VoiceTemplate template = notificationTemplateFactory.buildWorkResourceCheckInNotificationTemplate(
					Constants.EMAIL_USER_ID_TRANSACTIONAL,
					call.getWork()
				).getVoiceTemplate();
				if (call.getUser() != null)
					template.setToId(call.getUser().getId());
				return template;
			}
		});
	}};

	@Override
	public String respond(VoiceResponseDTO dto) throws OperationNotSupportedException, IllegalWorkAccessException  {
		Assert.notNull(dto);
		Assert.notNull(dto.getCallId());

		VoiceCall call = voiceCallDAO.getByCallId(dto.getCallId());

		// If call doesn't exist, this is an inbound, user-initiated call.
		// Otherwise update with any new details.

		if (call == null) {
			call = BeanUtilities.newBean(InboundVoiceCall.class, dto);
			call.setNotificationType(new NotificationType(NotificationType.RESOURCE_WORK_CHECKIN));
			call.setCallId(dto.getCallId());

			// Do we know the user?
			List<User> users = userService.findUsersByPhoneNumber(StringUtilities.standardizePhoneNumber(dto.getFromNumber()));

			if (logger.isDebugEnabled())
				logger.debug(String.format("[voice] Found %d user with the phone number: %s", users.size(), StringUtilities.standardizePhoneNumber(dto.getFromNumber())));

			if (users.size() == 1) {
				call.setUser(users.get(0));
				call.setCallSubStatus("startprompt");
			}

			voiceCallDAO.saveOrUpdate(call);
		} else {
			call.setCallStatus(dto.getCallStatus());
			call.setCallDuration(dto.getCallDuration());
			call.setFromNumber(dto.getFromNumber());
			call.setToNumber(dto.getToNumber());

			if (dto.getRedirectToSubStatus() != null)
				call.setCallSubStatus(dto.getRedirectToSubStatus());
		}

		VoiceTemplate template;
		if (templateMap.containsKey(call.getNotificationType().getCode())) {
			template = templateMap.get(call.getNotificationType().getCode()).getTemplate(call);
		} else {
			throw new OperationNotSupportedException("Voice not implemented for notification type " + call.getNotificationType().getCode());
		}

		if (dto.getMsg() != null) {
			String type = call.getNotificationType().getCode();
			VoiceApplication app = voiceApplicationFactory.getApplication(type);
			if (app != null && app.hasScreen(call.getCallSubStatus())) {

				VoiceApplicationScreen screen = app.getScreen(call.getCallSubStatus());
				VoiceCommand command = screen.findCommand(dto.getMsg());
				if (command != null) {
					if (logger.isDebugEnabled())
						logger.debug(String.format("[voice] Execute: %s => %s with message: %s", call.getNotificationType().getCode(), call.getCallSubStatus(), dto.getMsg()));
					command.execute(call, dto.getMsg());
				}
			}
		}

		template.setCurrentState(call.getCallSubStatus());
		template.setCallbackURI(voiceAdapter.getCallbackURI());

		return templateService.render(template);
	}
}
