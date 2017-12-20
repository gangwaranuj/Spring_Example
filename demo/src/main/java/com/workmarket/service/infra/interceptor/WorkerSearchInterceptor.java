package com.workmarket.service.infra.interceptor;

import com.workmarket.domains.model.User;
import com.workmarket.search.request.TrackableSearchRequest;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

/**
 * Author: rocio
 */
public class WorkerSearchInterceptor {

	@Autowired private EventFactory eventFactory;
	@Autowired private EventRouter eventRouter;
	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired private UserService userService;

	public Object logSearch(ProceedingJoinPoint pjp) throws Throwable {
		Object returnValue = pjp.proceed();
		Object[] arguments = pjp.getArgs();

		if (isNotEmpty(arguments)) {
			Object firstArgument = arguments[0];

			if (firstArgument instanceof TrackableSearchRequest) {
				PeopleSearchRequest peopleSearchRequest;
				TrackableSearchRequest trackableSearchRequest = (TrackableSearchRequest) firstArgument;
				peopleSearchRequest = trackableSearchRequest.getRequest();
				if (peopleSearchRequest != null) {
					if (returnValue != null && returnValue instanceof PeopleSearchResponse) {
						User user = userService.getUser(peopleSearchRequest.getUserId());
						if (user != null && user.getCompany() != null && featureEvaluator.hasFeature(user.getCompany().getId(), "trackUserSearch")) {
							eventRouter.sendEvent(eventFactory.buildSearchRequestEvent(trackableSearchRequest, (PeopleSearchResponse) returnValue, user.getCompany().getId()));
						}
					}
				}
			}
		}

		return returnValue;
	}
}