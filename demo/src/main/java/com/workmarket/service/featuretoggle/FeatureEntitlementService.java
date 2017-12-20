package com.workmarket.service.featuretoggle;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.model.User;
import com.workmarket.feature.FeatureToggleClient;
import com.workmarket.feature.gen.Messages.FeatureToggle;
import com.workmarket.feature.gen.Messages.Dimension;
import com.workmarket.feature.gen.Messages.DimensionValuePair;
import com.workmarket.feature.gen.Messages.FeatureToggles;
import com.workmarket.feature.gen.Messages.Status;
import com.workmarket.feature.vo.FeatureToggleAndStatus;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.functions.Action1;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service which wraps the {@link FeatureToggleClient}. Gives you the ability to grab all entitlements, or a specific
 * entitlement value for a {@link User}. To use this service, you must have the {@link User}'s UUID and company UUID
 * set.
 */
@Service
public class FeatureEntitlementService {
	private static final Logger logger = LoggerFactory.getLogger(FeatureEntitlementService.class);

	@Autowired
	private WebRequestContextProvider webRequestContextProvider;

	@Autowired
	private UserService userService;

	@Autowired
	private MetricRegistry metricRegistry;

	@Autowired
	private FeatureToggleClient client;
	@Autowired
	AuthenticationService authenticationService;

	private WMMetricRegistryFacade metricFacade;

	private Meter byId;
	private Meter byUser;
	private Meter specific;
	private Meter byIdSpecific;
	private Meter byUuidSpecific;
	private Meter byIdError;
	private Meter byUserError;
	private Meter specificError;
	private Meter byIdSpecificError;
	private Meter byUuidSpecificError;

	/**
	 * Given some user, build dimension query dimensions. Added is always USER UUID and COMPANY UUID. Both are required.
	 * Calling this method with missing values will result in an {@link IllegalArgumentException}.
	 *
	 * @param user
	 * @return list of {@link DimensionValuePair}s to be added as query dimensions.
	 */
	@VisibleForTesting
	static List<DimensionValuePair> genDimensionQueriesFromUser(final User user) {
		Preconditions.checkArgument(user != null, "User must be set.");
		Preconditions.checkArgument(StringUtils.isNotBlank(user.getUuid()), "User UUID must be set.");
		Preconditions.checkArgument(user.getCompany() != null, "Company must be set.");
		Preconditions.checkArgument(StringUtils.isNotBlank(user.getCompany().getUuid()), "Company UUID must be set.");

		return ImmutableList.of(
				DimensionValuePair.newBuilder()
						.setObjectId(user.getUuid())
						.setDimension(Dimension.USER).build(),
				DimensionValuePair.newBuilder()
						.setDimension(Dimension.COMPANY)
						.setObjectId(user.getCompany().getUuid()).build());
	}

	/**
	 * Initialize metric facade.
	 */
	@PostConstruct
	@VisibleForTesting
	void init() {
		metricFacade = new WMMetricRegistryFacade(metricRegistry, "featuretoggle");
		byId = metricFacade.meter("get.byid.request");
		byUser = metricFacade.meter("get.byuser.request");
		specific = metricFacade.meter("get.specific.request");
		byIdSpecific = metricFacade.meter("get.byidspecific.request");
		byUuidSpecific = metricFacade.meter("get.byuuidspecific.request");
		byIdError = metricFacade.meter("get.byid.error");
		byUserError = metricFacade.meter("get.byuser.error");
		specificError = metricFacade.meter("get.specific.error");
		byIdSpecificError = metricFacade.meter("get.byidspecific.error");
		byUuidSpecificError = metricFacade.meter("get.byuuidspecific.error");
	}

	/**
	 * Get all feature toggle entitlements for a given user, by id.
	 *
	 * @param userId
	 * @return
	 */
	public Observable<FeatureToggles> getFeatureToggles(final long userId) {
		try {
			byId.mark();
			final User user = userService.findUserById(userId);
			if (user == null) {
				return Observable.error(new IllegalStateException("Cannot find user " + userId));
			}
			return getFeatureToggles(user);
		} catch (final Exception e) {
			byIdError.mark();
			return Observable.error(e);
		}
	}

	/**
	 * Get all feature toggle entitlements for a given user.
	 *
	 * @param user
	 * @return
	 */
	public Observable<FeatureToggles> getFeatureToggles(final User user) {
		try {
			byUser.mark();
			final RequestContext ctx = webRequestContextProvider.getRequestContext();
			final List<DimensionValuePair> dimensionValuePairs = genDimensionQueriesFromUser(user);
			return client.getAllToggles(dimensionValuePairs, ctx);
		} catch (final Exception e) {
			byUserError.mark();
			return Observable.error(e);
		}
	}

	/**
	 * Get a specific feature toggle entitlement for a given user and feature toggle name.
	 *
	 * @param user
	 * @param toggleName
	 * @return
	 */
	public Observable<FeatureToggleAndStatus> getFeatureToggle(final User user, final String toggleName) {
		try {
			Preconditions.checkArgument(StringUtils.isNotBlank(toggleName), "Toggle name must be set.");
			specific.mark();
			final RequestContext ctx = webRequestContextProvider.getRequestContext();
			final List<DimensionValuePair> dimensionValuePairs = genDimensionQueriesFromUser(user);
			return client.get(toggleName, dimensionValuePairs, ctx);
		} catch (final Exception e) {
			specificError.mark();
			return Observable.error(e);
		}
	}

	/**
	 * Get a specific feature toggle entitlement for a given user and feature toggle name, by user ID.
	 *
	 * @param userId
	 * @param toggleName
	 * @return
	 */
	public Observable<FeatureToggleAndStatus> getFeatureToggle(final Long userId, final String toggleName) {
		try {
			byIdSpecific.mark();
			Preconditions.checkArgument(StringUtils.isNotBlank(toggleName), "Toggle name must be set.");
			final User user = userService.findUserById(userId);
			if (user == null) {
				return Observable.error(new IllegalStateException("Cannot find user " + userId));
			}
			final RequestContext ctx = webRequestContextProvider.getRequestContext();
			final List<DimensionValuePair> dimensionValuePairs = genDimensionQueriesFromUser(user);
			return client.get(toggleName, dimensionValuePairs, ctx);
		} catch (final Exception e) {
			byIdSpecificError.mark();
			return Observable.error(e);
		}
	}

	/**
	 * Get feature toggle for the current user.
	 *
	 * @param toggleName
	 * @return
	 */
	public Observable<FeatureToggleAndStatus> getFeatureToggleForCurrentUser(final String toggleName) {
		if (authenticationService.getCurrentUserId() != null) {
			return getFeatureToggle(authenticationService.getCurrentUserId(), toggleName);
		} else {
			return getFeatureToggle(webRequestContextProvider.getWebRequestContext().getUserUuid(), toggleName);
		}
	}

	/**
	 * Convenience method to return whether the value of a feature toggle is boolean true.
	 *
	 * @param userUuid
	 * @param toggleName
	 * @return
	 */
	public Observable<FeatureToggleAndStatus> getFeatureToggle(final String userUuid, final String toggleName) {
		try {
			byUuidSpecific.mark();
			Preconditions.checkArgument(StringUtils.isNotBlank(toggleName), "Toggle name must be set.");
			final User user = userService.findUserByUuid(userUuid);
			if (user == null) {
				return Observable.error(new IllegalStateException("Cannot find user by uuid " + userUuid));
			}
			final RequestContext ctx = webRequestContextProvider.getRequestContext();
			final List<DimensionValuePair> dimensionValuePairs = genDimensionQueriesFromUser(user);
			return client.get(toggleName, dimensionValuePairs, ctx);
		} catch (final Exception e) {
			byUuidSpecificError.mark();
			return Observable.error(e);
		}
	}

	/**
	 * Convenience method to return whether the value of a feature toggle is boolean true.
	 *
	 * @param userId
	 * @param toggleName
	 * @return
	 */
	public boolean hasFeatureToggle(final Long userId, final String toggleName) {
		final AtomicReference<Boolean> result = new AtomicReference<>(false);
		getFeatureToggle(userId, toggleName)
				.subscribe(new Action1<FeatureToggleAndStatus>() {
					@Override
					public void call(final FeatureToggleAndStatus featureToggleAndStatus) {
						if (featureToggleAndStatus != null && featureToggleAndStatus.getFeatureToggle() != null) {
							final String value = featureToggleAndStatus.getFeatureToggle().getValue();
							result.set("true".equals(value) || "1".equals(value) || "on".equals(value));
						}
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(final Throwable throwable) {
						logger.error("Error fetching entitlement", throwable);
					}
				});
		return result.get();
	}

	/**
	 * Returns whether a toggle is enabled with percent rollout strategy.
	 *
	 * @param toggleName
	 * @return
	 */
	public boolean hasPercentRolloutFeatureToggle(final String toggleName) {
		final FeatureToggleAndStatus res = client.get(
			toggleName,
			ImmutableList.<DimensionValuePair>of(),
			webRequestContextProvider.getRequestContext())
			.toBlocking()
			.singleOrDefault(new FeatureToggleAndStatus(
				Status.newBuilder().setSuccess(false).build(),
				null));
		if (!res.getStatus().getSuccess()) {
			logger.error("request returned failure, no {}!", toggleName);
			return false;
		}
		final FeatureToggle featureToggle = res.getFeatureToggle();
		final String value = featureToggle == null ? "false" : featureToggle.getValue();
		final boolean hasFeatureToggle = "true".equals(value) || "1".equals(value) || "on".equals(value);
		return hasFeatureToggle;
	}
}
