package com.workmarket.api.internal.endpoints;


import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.feature.gen.Messages.FeatureToggle;
import com.workmarket.feature.gen.Messages.FeatureToggles;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import rx.Observable;
import rx.functions.Func1;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class FeatureEntitlementsController {
	private static final Logger logger = LoggerFactory.getLogger(FeatureEntitlementsController.class);
	private FeatureEntitlementService featureEntitlementService;

	@Autowired
	private SecurityContextFacade securityContextFacade;

	/**
	 * ctor.
	 *
	 * @param featureEntitlementService
	 */
	@Autowired
	public FeatureEntitlementsController(final FeatureEntitlementService featureEntitlementService) {
		this.featureEntitlementService = featureEntitlementService;
	}

	@RequestMapping(value = "/featureEntitlements", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, String> getEntitlements() {
		final ExtendedUserDetails eud = securityContextFacade == null
				? null
				: securityContextFacade.getCurrentUser();

		if (eud == null) {
			return ImmutableMap.of();
		}

		return featureEntitlementService.getFeatureToggles(eud.getId())
				.onErrorResumeNext(new Func1<Throwable, Observable<? extends FeatureToggles>>() {
					@Override
					public Observable<? extends FeatureToggles> call(final Throwable throwable) {
						logger.error("Could not get feature entitlements for user " + eud.getId(), throwable);
						return Observable.just(FeatureToggles.getDefaultInstance());
					}
				})
				.map(new Func1<FeatureToggles, Map<String, String>>() {
					@Override
					public Map<String, String> call(final FeatureToggles toggles) {
						final ImmutableMap.Builder<String, String> entitlementsBuilder = ImmutableMap.builder();
						for (final String name : toggles.getFeatureToggles().keySet()) {
							final FeatureToggle toggle = toggles.getFeatureToggles().get(name);
							entitlementsBuilder.put(name, toggle.getValue());
						}
						return entitlementsBuilder.build();
					}
				})
				.toBlocking()
				.single();
	}
}
