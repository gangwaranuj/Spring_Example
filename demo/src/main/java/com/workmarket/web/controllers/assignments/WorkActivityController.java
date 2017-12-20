package com.workmarket.web.controllers.assignments;

import com.google.common.collect.ImmutableSet;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.LogEntry;
import com.workmarket.thrift.work.WorkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/assignments")
public class WorkActivityController extends BaseWorkController {

	private static final Logger logger = LoggerFactory.getLogger(WorkActivityController.class);

	@RequestMapping(
		value = "/{workNumber}/activities",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody List<LogEntry> getActivities(
		@PathVariable String workNumber,
		HttpServletResponse response) {

		List<LogEntry> activities = null;

		try {
			final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(WorkRequestInfo.CHANGE_LOG_INFO));

			if (workResponse.getWork() != null) {
				activities = workResponse.getWork().getChangelog();
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.error(String.format("Error fetching activities for workNumber %s: ", workNumber), e);
		}

		return maskUsers(activities);
	}

	/* Note: We can't expose ids to the client, and since we don't need any actor data client side,
	 * we can just remove the entire actor object.
	 * We need to build an editor to do the type conversion automagically with Spring.
	 */
	private List<LogEntry> maskUsers(List<LogEntry> activities) {
		if (activities != null) {
			for (LogEntry activity : activities) {
				maskUsers(activity);
			}
		}

		return activities;
	}

	private LogEntry maskUsers(LogEntry activity) {
		if (activity != null) {
			activity.setActor(null);
		}

		return activity;
	}
}
