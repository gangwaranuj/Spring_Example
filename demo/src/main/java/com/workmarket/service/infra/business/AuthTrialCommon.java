package com.workmarket.service.infra.business;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Callables;
import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages.UserStatus;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.kafka.KafkaClient;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.work.service.part.HibernateTrialWrapper;
import com.workmarket.feature.FeatureToggleClient;
import com.workmarket.feature.gen.Messages.DimensionValuePair;
import com.workmarket.feature.gen.Messages.FeatureToggle;
import com.workmarket.feature.vo.FeatureToggleAndStatus;
import com.workmarket.id.IdGenerator;
import com.workmarket.jan20.IsEqual;
import com.workmarket.jan20.IsEqualUtil;
import com.workmarket.jan20.Trial;
import com.workmarket.jan20.Trial.WhichReturn;
import com.workmarket.jan20.TrialResult;
import com.workmarket.service.web.WebRequestContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import rx.Observable;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.workmarket.common.kafka.KafkaUtil.getStringObjectMap;
import static com.workmarket.jan20.IsEqualUtil.checkNullity;
import static com.workmarket.jan20.IsEqualUtil.startCompare;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Common bits of Authentication/Authorization succession.
 */
@Component
public class AuthTrialCommon {
	private static final Logger logger = LoggerFactory.getLogger(AuthTrialCommon.class);
	private static final Callable<Observable<Boolean>> NOOP_CONTROL = Callables.returning(Observable.just(true));

	static final String TRIAL_LOG_TOPIC = "auth-experiment";

	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private CommitHook commitHook;
	@Autowired private HibernateTrialWrapper hibernateWrapper;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private IdGenerator idGenerator;
	@Autowired private AuthenticationClient authClient;

	static KafkaClient KAFKA_CLIENT;

	private FeatureToggleClient featureToggleClient;
	private LoadingCache<String, WhichReturn> experimentModeCache;
	private Trial stateTrial;

	@PostConstruct
	private void init() {
		// so we don't hammer the daylights out of the feature toggle service for something that
		// doesn't change that often
		experimentModeCache = CacheBuilder.newBuilder()
			.initialCapacity(1)
			.expireAfterWrite(5, TimeUnit.SECONDS)
			.build(new CacheLoader<String, WhichReturn>() {
				@Override
				public WhichReturn load(final String experimentName) throws Exception {
					return featureToggleWhichReturn(experimentName);
				}
			});
		final ThreadPoolExecutor stateExperimentExecutor = new ThreadPoolExecutor(
			1, 30, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100));
		stateTrial = new Trial(stateExperimentExecutor, metricRegistry, "auth.succession", new Supplier<WhichReturn>() {
			@Override
			public WhichReturn get() {
				return getCachedWhichReturn("succession.authuserstate");
			}
		});
		featureToggleClient = new FeatureToggleClient();
		authClient = new AuthenticationClient();
	}

	private WhichReturn getCachedWhichReturn(final String experimentName) {
		try {
			return experimentModeCache.get(experimentName);
		} catch (final ExecutionException e) {
			logger.error("cache get failed, CONTROL!");
			return WhichReturn.CONTROL;
		}
	}

	@Autowired
	public void setKafkaClient(@Qualifier("AppKafkaClient") final KafkaClient client) {
		KAFKA_CLIENT = client;
	}

	IsEqual<Throwable> makeBothOrNeitherThrow(final String method) {
		return new IsEqual<Throwable>() {
			@Override
			public boolean apply(final Throwable a, final Throwable b) {
				final boolean result = ((a == null) == (b == null));
				if (!result) {
					if (b != null) {
						logger.error("exception thrown in experiment", b);
					}
					KAFKA_CLIENT.send(TRIAL_LOG_TOPIC, getStringObjectMap(
						a, b, "bothOrNeitherThrow", ImmutableList.<String>of(), method));
				}
				return result;
			}
		};
	}

	/**
	 * Make sure we have a valid request context no matter how we get here.
	 */
	public RequestContext getApiContext() {
		final RequestContext orgCtx = webRequestContextProvider.getRequestContext();
		final String requestId;
		if (isBlank(orgCtx.getRequestId())) {
			requestId = idGenerator.next().toBlocking().singleOrDefault(UUID.randomUUID().toString());
		} else {
			requestId = orgCtx.getRequestId();
		}
		final RequestContext ctx = new RequestContext(requestId, orgCtx.getTenant());
		ctx.setOriginIp(orgCtx.getOriginIp());
		if (isBlank(ctx.getUserId())) {
			ctx.setUserId("monolith");
		}
		ctx.setOrigin(orgCtx.getOrigin());
		if (isBlank(ctx.getOrigin())) {
			ctx.setOrigin("monolith");
		}
		ctx.setUserId(orgCtx.getUserId());
		if (isBlank(ctx.getUserId())) {
			ctx.setUserId("system");
		}
		return ctx;
	}

	private WhichReturn featureToggleWhichReturn(final String experimentName) {
		try {
			final RequestContext ctx = getApiContext();
			ctx.setOriginIp("monolith-internal");
			ctx.setOrigin("monolith");
			ctx.setUserId("system");
			final FeatureToggleAndStatus res = featureToggleClient.get(
				experimentName,
				ImmutableList.<DimensionValuePair>of(),
				ctx).toBlocking().single();
			if (!res.getStatus().getSuccess()) {
				logger.error("request returned failure, CONTROL!");
				return WhichReturn.CONTROL;
			}
			final FeatureToggle featureToggle = res.getFeatureToggle();
			final String value = featureToggle == null ? "CONTROL_ONLY" : featureToggle.getValue();
			final WhichReturn whichReturn = WhichReturn.valueOf(value);
			logger.debug("auth experimient mode, {}", whichReturn);
			return whichReturn;
		} catch (final Exception e) {
			logger.error("Error getting feature toggle data", e);
			return WhichReturn.CONTROL;
		}
	}

	private IsEqual<Boolean> makeBooleanIsEqual(final String method) {
		return new IsEqual<Boolean>() {
			@Override
			public boolean apply(final Boolean control, final Boolean experiment) {
				final List<String> mismatches = new ArrayList<>();
				final IsEqualUtil.MismatchConsumer consumer = IsEqualUtil.consumeToList(mismatches);
				final boolean success = checkNullity(control, experiment, consumer)
					&& startCompare(consumer)
					.dotEquals(control, experiment, "bool")
					.get();
				if (success) {
					return true;
				}
				KAFKA_CLIENT.send(TRIAL_LOG_TOPIC, getStringObjectMap(control, experiment, "passwordMatchesIsEqual",
					mismatches, method));
				return false;
			}
		};
	}

	private IsEqual<TrialResult<Boolean>> makeBooleanTrialIsEqual(final String method) {
		return Trial.makeIsEqual(makeBothOrNeitherThrow(method), makeBooleanIsEqual(method).pairwiseEqual());
	}

	static UserStatus convertStatusType(final UserStatusType status) {
		switch (status.getCode()) {
			case UserStatusType.PENDING:
				return UserStatus.PENDING;
			case UserStatusType.DELETED:
				return UserStatus.DELETED;
			case UserStatusType.APPROVED:
				return UserStatus.APPROVED;
			case UserStatusType.SUSPENDED:
				return UserStatus.SUSPENDED;
			case UserStatusType.DEACTIVATED:
				return UserStatus.DEACTIVATED;
			case UserStatusType.HOLD:
				return UserStatus.HOLD;
			case UserStatusType.LOCKED:
				return UserStatus.LOCKED;
			default:
				throw new RuntimeException("Invalid user status type!");
		}
	}
	/*
	 * Useful when there's no control to run, we've only added a new execution path.  For this use case, if CONTROL,
	 * failures don't propagate.  If EXPERIMENT, they do.
	 */
	boolean runNoOpControlExperiment(
		final Callable<Observable<Boolean>> experiment,
		final String experimentName,
		final Trial trial) {
		try {
			return trial.doTrial(NOOP_CONTROL, experiment, makeBooleanTrialIsEqual(experimentName),
				experimentName)
				.toBlocking().single();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	Trial getStateTrial() {
		return stateTrial;
	}

	CommitHook getCommitHook() {
		return commitHook;
	}
}
