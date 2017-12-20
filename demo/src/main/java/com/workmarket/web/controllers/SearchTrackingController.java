package com.workmarket.web.controllers;

import com.google.common.collect.Lists;
import com.workmarket.search.tracking.SearchTrackingClient;
import com.workmarket.search.tracking.model.SearchTrackingAction;
import com.workmarket.search.tracking.model.SearchTrackingSearch;
import com.workmarket.search.tracking.model.SearchTrackingSession;
import com.workmarket.service.web.WebRequestContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import rx.Observer;

import javax.annotation.PostConstruct;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping("/searchtracking")
public class SearchTrackingController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(SearchTrackingController.class);

	private static final Observer<Object> ON_ERROR_OBSERVER = new Observer<Object>() {
		@Override
		public void onCompleted() { }

		@Override
		public void onError(Throwable throwable) {
			logger.info(throwable.getMessage());
		}

		@Override
		public void onNext(Object o) { }
	};

	@Autowired private WebRequestContextProvider webRequestContextProvider;

	private SearchTrackingClient searchTrackingClient;

	@PostConstruct
	private void initializeClient() {
		searchTrackingClient = new SearchTrackingClient();
	}

	@RequestMapping(
		value = "/session",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public void trackSession(final @RequestBody SearchTrackingSession[] sessions) {

		if (sessions == null) {
			logger.info("searchTracking session requestBody empty");
		} else {
			searchTrackingClient.writeSearchTrackingSession(
					Lists.newArrayList(sessions),
					webRequestContextProvider.getRequestContext()
			).subscribe(ON_ERROR_OBSERVER);
		}
	}

	@RequestMapping(
		value = "/search",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public void trackSearch(final @RequestBody SearchTrackingSearch[] searches) {

		if (searches == null) {
			logger.info("searchTracking search requestBody empty");
		} else {
			searchTrackingClient.writeSearchTrackingSearch(
					Lists.newArrayList(searches),
					webRequestContextProvider.getRequestContext()
			).subscribe(ON_ERROR_OBSERVER);
		}
	}

	@RequestMapping(
		value = "/action",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public void trackAction(final @RequestBody SearchTrackingAction[] actions) {

		if (actions == null) {
			logger.info("searchTracking actions requestBody empty");
		} else {
			searchTrackingClient.writeSearchTrackingAction(
					Lists.newArrayList(actions),
					webRequestContextProvider.getRequestContext()
			).subscribe(ON_ERROR_OBSERVER);
		}
	}
}
