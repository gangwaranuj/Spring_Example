package com.workmarket.service.infra.voice;

import com.google.common.collect.ImmutableMap;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.voice.VoiceCall;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkPagination;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.exception.IllegalWorkAccessException;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VoiceApplicationFactoryImpl implements VoiceApplicationFactory {

	@Autowired private WorkService workService;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private UserDAO userDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private TWorkFacadeService tWorkFacadeService;

	private final Map<String, VoiceApplication> voiceApplications = ImmutableMap.of(
			NotificationType.RESOURCE_WORK_CONFIRM, buildResourceConfirmationApplication(),
			NotificationType.RESOURCE_WORK_INVITED, buildResourceInvitationApplication(),
			NotificationType.RESOURCE_WORK_CHECKIN, buildResourceCheckinApplication()
	);

	/**
	 * Convenience initializer to build an error state command that increments the failed prompts counter
	 * and conditionally plays the <code>exceededStatus</code> screen in the event that the user
	 * has maxed out their failed attempts (e.g. and hang up on them) or plays the <code>replayStatus</code>
	 * screen to optionally replay the prompt or move them along elsewhere.
	 */
	private static VoiceCommand newExceededFailedPromptsCommand(final String exceededStatus, final String replayStatus) {
		return new VoiceCommand() {
			public void execute(VoiceCall call, String msg) {
				call.setFailedPrompts(call.getFailedPrompts() + 1);
				if (call.didExceedFailedPrompts()) {
					call.setCallSubStatus(exceededStatus);
				} else {
					call.setCallSubStatus(replayStatus);
				}
			}
		};
	}

	@Override
	public VoiceApplication getApplication(String name) {
		if (voiceApplications.containsKey(name))
			return voiceApplications.get(name);
		return null;
	}

	@Override
	public VoiceApplication buildResourceInvitationApplication() {
		VoiceApplicationScreen screen = new VoiceApplicationScreen("start");
		screen.addCommand("0", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				call.setCallSubStatus("clientsvc");
			}
		});

		screen.addCommand("1", new VoiceCommand() {
			public void execute(VoiceCall call, String msg) throws IllegalWorkAccessException  {
			if(call.getWork().isAssignToFirstResourceEnabled() && call.getWork().isSent()){
				tWorkFacadeService.acceptWork(call.getUser().getId(), call.getWork().getId());
				call.setCallSubStatus("accept");
			}
			}
		});

		screen.addCommand("2", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				if (call.getWork().isSent()) {
					workService.declineWork(call.getUser().getId(), call.getWork().getId());
					call.setCallSubStatus("decline");
				}
			}
		});

		screen.addCommand("3", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				call.setCallSubStatus("description");
			}
		});

		VoiceApplication app = new VoiceApplication();
		app.addScreen(screen);
		return app;
	}

	@Override
	public VoiceApplication buildResourceConfirmationApplication() {
		VoiceApplicationScreen screen = new VoiceApplicationScreen("start");
		screen.addCommand("0", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				call.setCallSubStatus("clientsvc");
			}
		});

		screen.addCommand("1", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				workService.confirmWorkResource(call.getUser().getId(), call.getWork().getId());
				call.setCallSubStatus("confirm");
			}
		});

		screen.addCommand("2", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				workService.abandonWork(call.getUser().getId(), call.getWork().getId(), null);

				call.setCallSubStatus("cancel");
			}
		});


		VoiceApplication app = new VoiceApplication();
		app.addScreen(screen);
		return app;
	}

	@Override
	public VoiceApplication buildResourceCheckinApplication() {
		VoiceApplication app = new VoiceApplication();

		// Screen: start

		VoiceApplicationScreen screen = new VoiceApplicationScreen("start");
		screen.addCommand("0", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				call.setCallSubStatus("clientsvc");
			}
		});

		screen.addCommand("\\d+", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				User user = userDAO.findUserByUserNumber(msg, false);
				if (user == null) {
					call.setFailedPrompts(call.getFailedPrompts() + 1);
					if (call.didExceedFailedPrompts()) {
						call.setCallSubStatus("invaliduserboot");
					} else {
						call.setCallSubStatus("invaliduser");
					}
					return;
				}

				call.setUser(user);
				call.setFailedPrompts(0);

				WorkPagination pagination = new WorkPagination();
				pagination.setResultsLimit(1);
				pagination = workService.findWorkByWorkResource(user.getId(), pagination);

				if (pagination.getRowCount() == 0) {
					call.setCallSubStatus("nowork");
				} else if (pagination.getRowCount() == 1) {
					call.setWork(pagination.getResults().get(0));
					call.setCallSubStatus("menu");
				} else {
					call.setCallSubStatus("findwork");
				}
			}
		});

		screen.addCommand(".*", newExceededFailedPromptsCommand("invaliduserboot", "invaliduser"));

		app.addScreen(screen);

		// Screen: startprompt

		screen = new VoiceApplicationScreen("startprompt");
		screen.addCommand("0", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				call.setCallSubStatus("clientsvc");
			}
		});

		screen.addCommand("\\d+", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				User user = userDAO.findUserByUserNumber(msg, false);
				if (user == null || !call.getUser().equals(user)) {
					call.setFailedPrompts(call.getFailedPrompts() + 1);
					if (call.didExceedFailedPrompts()) {
						call.setCallSubStatus("invaliduserboot");
					} else {
						call.setCallSubStatus("invaliduser");
					}
					return;
				}

				call.setFailedPrompts(0);

				WorkPagination pagination = new WorkPagination();
				pagination.setResultsLimit(1);
				pagination = workService.findWorkByWorkResource(user.getId(), pagination);

				if (pagination.getRowCount() == 0) {
					call.setCallSubStatus("nowork");
				} else if (pagination.getRowCount() == 1) {
					call.setWork(pagination.getResults().get(0));
					call.setCallSubStatus("menu");
				} else {
					call.setCallSubStatus("findwork");
				}
			}
		});

		screen.addCommand(".*", newExceededFailedPromptsCommand("invaliduserboot", "invaliduser"));

		app.addScreen(screen);

		// Screen: findwork

		screen = new VoiceApplicationScreen("findwork");
		screen.addCommand("0", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				call.setCallSubStatus("clientsvc");
			}
		});

		screen.addCommand("\\d+", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				Work work = workDAO.findWorkByWorkNumber(msg);
				if (work == null) {
					call.setFailedPrompts(call.getFailedPrompts() + 1);
					if (call.didExceedFailedPrompts()) {
						call.setCallSubStatus("invalidworkboot");
					} else {
						call.setCallSubStatus("invalidwork");
					}
					return;
				}

				if (!workService.isUserActiveResourceForWork(call.getUser().getId(), work.getId())) {
					call.setFailedPrompts(call.getFailedPrompts() + 1);
					if (call.didExceedFailedPrompts()) {
						call.setCallSubStatus("invalidworkboot");
					} else {
						call.setCallSubStatus("invalidwork");
					}
					return;
				}

				call.setWork(work);
				call.setCallSubStatus("menu");
				call.setFailedPrompts(0);
			}
		});

		screen.addCommand(".*", newExceededFailedPromptsCommand("invalidworkboot", "invalidwork"));

		app.addScreen(screen);

		// Screen: menu

		screen = new VoiceApplicationScreen("menu");
		screen.addCommand("0", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				call.setCallSubStatus("clientsvc");
			}
		});

		screen.addCommand("1", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				if(call.getWork().isCheckinRequired()) {
					tWorkFacadeService.checkInActiveResource(new TimeTrackingRequest()
							.setWorkId(call.getWork().getId())
							.setDate(DateUtilities.getCalendarNow()));
					call.setCallSubStatus("updated");
				}
			}
		});

		screen.addCommand("2", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				if(call.getWork().isCheckinRequired()) {
					tWorkFacadeService.checkOutActiveResource(new TimeTrackingRequest()
							.setWorkId(call.getWork().getId())
							.setDate(DateUtilities.getCalendarNow()));
					call.setCallSubStatus("updated");
				}
			}
		});

		screen.addCommand("3", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				if(call.getWork().isResourceConfirmationRequired()) {
					workService.confirmWorkResource(call.getUser().getId(), call.getWork().getId());
					call.setCallSubStatus("updated");
				}
			}
		});


		screen.addCommand("9", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				workSubStatusService.addSystemSubStatus(call.getWork().getId(), WorkSubStatusType.GENERAL_PROBLEM, StringUtils.EMPTY);
				call.setCallSubStatus("updated");
			}
		});

		screen.addCommand(".*", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				call.setCallSubStatus("invalid");
			}
		});
		app.addScreen(screen);

		// Screen: updated

		screen = new VoiceApplicationScreen("updated");
		screen.addCommand("0", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				call.setCallSubStatus("clientsvc");
			}
		});

		screen.addCommand(".*", new VoiceCommand() {
			public void execute(VoiceCall call, String msg)  {
				call.setWork(null);
				call.setCallSubStatus("findwork");
			}
		});
		app.addScreen(screen);

		return app;
	}
}
