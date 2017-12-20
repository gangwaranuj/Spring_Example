package com.workmarket.service.business;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.kafka.KafkaClient;
import com.workmarket.core.notification.gen.Messages.Dimension;
import com.workmarket.core.notification.gen.Messages.DimensionValuePair;
import com.workmarket.core.notification.gen.Messages.GetPrefs;
import com.workmarket.core.notification.gen.Messages.GetResp;
import com.workmarket.core.notification.gen.Messages.ModPref;
import com.workmarket.core.notification.gen.Messages.SetPrefs;
import com.workmarket.core.notification.gen.Messages.Status;
import com.workmarket.core.notification.gen.Messages.Type;
import com.workmarket.core.notification.gen.Messages.TypeToValue;
import com.workmarket.core.notification.gen.Messages.TypeValue;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.notification.UserNotificationPreferenceDAO;
import com.workmarket.dao.user.PersonaPreferenceDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.notification.UserNotificationPreference;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.work.service.part.HibernateTrialWrapper;
import com.workmarket.feature.FeatureToggleClient;
import com.workmarket.feature.gen.Messages;
import com.workmarket.feature.gen.Messages.FeatureToggle;
import com.workmarket.feature.vo.FeatureToggleAndStatus;
import com.workmarket.jan20.IsEqual;
import com.workmarket.jan20.IsEqualUtil;
import com.workmarket.jan20.IterableIsEqual;
import com.workmarket.jan20.Trial;
import com.workmarket.jan20.TrialResult;
import com.workmarket.notification.NotificationClient;
import com.workmarket.service.business.dto.NotificationPreferenceDTO;
import com.workmarket.service.web.WebRequestContextProvider;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import rx.Observable;
import rx.functions.Func1;

import static com.workmarket.common.kafka.KafkaUtil.getStringObjectMap;
import static com.workmarket.jan20.IsEqualUtil.checkNullity;
import static com.workmarket.jan20.IsEqualUtil.startCompare;

/**
 * Manages user notification preferences.
 */
@Component
public class UserNotificationPrefsService {
	private static final Logger logger = LoggerFactory.getLogger(UserNotificationPrefsService.class);
	private static final String TRIAL_LOG_TOPIC = "notification-experiment";
	private static final String DISPATCHER_SUFFIX = ".dispatcher";

	private static final Set<String> MANAGE_FUNDS_NOTIFICATIONS = Sets.newHashSet(
		NotificationType.MONEY_WITHDRAWN,
		NotificationType.MONEY_DEPOSITED,
		NotificationType.MONEY_CREDIT_CARD_RECEIPT);

	private static final Set<String> INVOICE_EMAILS = Sets.newHashSet(
		NotificationType.INVOICE_DUE_3_DAYS,
		NotificationType.INVOICE_DUE_24_HOURS,
		NotificationType.INVOICE_CREATED_ON_ASSIGNMENT,
		NotificationType.MY_INVOICES_DUE_3_DAYS,
		NotificationType.MY_INVOICES_DUE_24_HOURS);

	@VisibleForTesting // would be private
	static final Set<String> PAYMENT_ACCESS_NOTIFICATIONS = Sets.newHashSet(
		NotificationType.STATEMENT_REMINDER,
		NotificationType.SUBSCRIPTION_REMINDER,
		NotificationType.LOCKED_INVOICE_DUE_REMINDER_MY_ACCOUNT,
		NotificationType.INVOICE_DUE_REMINDER_MY_ACCOUNT);

	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private HibernateTrialWrapper hibernateWrapper;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private UserDAO userDAO;
	@Autowired private UserNotificationPreferenceDAO userNotificationPreferenceDAO;
	@Autowired private PersonaPreferenceDAO personaPreferenceDAO;
	@Autowired private NotificationClient notificationsClient;

	static KafkaClient KAFKA_CLIENT;

	private FeatureToggleClient featureToggleClient;
	private LoadingCache<String, Trial.WhichReturn> experimentModeCache;
	private Trial trial;

	@PostConstruct
	private void init() {
		// so we don't hammer the daylights out of the feature toggle service for something that
		// doesn't change that often
		experimentModeCache = CacheBuilder.newBuilder()
			.initialCapacity(1)
			.expireAfterWrite(5, TimeUnit.SECONDS)
			.build(new CacheLoader<String, Trial.WhichReturn>() {
				@Override
				public Trial.WhichReturn load(final String experimentName) throws Exception {
					return featureToggleWhichReturn(experimentName);
				}
			});
		final ThreadPoolExecutor stateExperimentExecutor = new ThreadPoolExecutor(
			1, 30, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100));
		trial = new Trial(stateExperimentExecutor, metricRegistry, "notificationsprefs.succession",
			new Supplier<Trial.WhichReturn>() {
				@Override
				public Trial.WhichReturn get() {
					return getCachedWhichReturn("notificationprefs.succession");
				}
			});
		featureToggleClient = new FeatureToggleClient();
	}

	private Trial.WhichReturn getCachedWhichReturn(final String experimentName) {
		try {
			return experimentModeCache.get(experimentName);
		} catch (final ExecutionException e) {
			logger.error("cache get failed, CONTROL!");
			return Trial.WhichReturn.CONTROL;
		}
	}

	@Autowired
	public void setKafkaClient(@Qualifier("AppKafkaClient") final KafkaClient client) {
		KAFKA_CLIENT = client;
	}

	private Trial.WhichReturn featureToggleWhichReturn(final String experimentName) {
		try {
			final FeatureToggleAndStatus res = featureToggleClient.get(
				experimentName,
				ImmutableList.<Messages.DimensionValuePair>of(),
				webRequestContextProvider.getRequestContext()).toBlocking().single();
			if (!res.getStatus().getSuccess()) {
				logger.error("request returned failure, CONTROL!");
				return Trial.WhichReturn.CONTROL;
			}
			final FeatureToggle featureToggle = res.getFeatureToggle();
			final String value = featureToggle == null ? "CONTROL_ONLY" : featureToggle.getValue();
			final Trial.WhichReturn whichReturn = Trial.WhichReturn.valueOf(value);
			logger.debug("auth experimient mode, {}", whichReturn);
			return whichReturn;
		} catch (final Exception e) {
			logger.error("Error getting feature toggle data", e);
			return Trial.WhichReturn.CONTROL;
		}
	}

	private IsEqual<Throwable> makeBothOrNeitherThrow(final String method) {
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

	public List<UserNotificationPreferencePojo> findByUserWithDefault(final Long userId) {

		final User user = userDAO.getUser(userId);
		final String userUuid = user.getUuid();
		final String companyUuid = user.getCompany().getUuid();

		final Callable<Observable<List<UserNotificationPreferencePojo>>> control = new Callable<Observable<List<UserNotificationPreferencePojo>>>() {
			@Override
			public Observable<List<UserNotificationPreferencePojo>> call() throws Exception {
				final List<NotificationType> types = userNotificationPreferenceDAO.findByUserWithDefault(userId);
				final ImmutableList.Builder<UserNotificationPreferencePojo> pojos = ImmutableList.builder();
				for (final NotificationType type : types) {
					pojos.add(new UserNotificationPreferencePojoBuilder()
						.setNotificationType(type.getCode())
						.setBullhornFlag(type.getBullhornFlag())
						.setDispatchBullhornFlag(type.getDispatchBullhornFlag())
						.setEmailFlag(type.getEmailFlag())
						.setDispatchEmailFlag(type.getDispatchEmailFlag())
						.setPushFlag(type.getPushFlag())
						.setDispatchPushFlag(type.getDispatchPushFlag())
						.setSmsFlag(type.getSmsFlag())
						.setDispatchSmsFlag(type.getDispatchSmsFlag())
						.setVoiceFlag(type.getVoiceFlag())
						.setDispatchVoiceFlag(type.getDispatchVoiceFlag())
						.setFollowFlag(type.getFollowFlag())
						.build());
				}
				final List<UserNotificationPreferencePojo> build = pojos.build();
				return Observable.just(build);
			}
		};

		final Callable<Observable<List<UserNotificationPreferencePojo>>> experiment = new Callable<Observable<List<UserNotificationPreferencePojo>>>() {
			@Override
			public Observable<List<UserNotificationPreferencePojo>> call() throws Exception {
				return newFindByUserWithDefault(userUuid, companyUuid);
			}
		};

		try {
			return trial.doTrial(control, experiment, makeAllPrefsIsEqual(userUuid, "findByUserWithDefault"),
				"find_by_user_with_default").toBlocking().singleOrDefault(null);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@VisibleForTesting // would be private
	Observable<List<UserNotificationPreferencePojo>> newFindByUserWithDefault(
		final String userUuid,
		final String companyUuid) {
		return loadNotificationPrefs(userUuid, companyUuid, true)
			.map(new Func1<Map<String, UserNotificationPreferencePojo>, List<UserNotificationPreferencePojo>>() {
				@Override
				public List<UserNotificationPreferencePojo> call(final Map<String, UserNotificationPreferencePojo> prefMap) {
					return ImmutableList.copyOf(prefMap.values());
				}
			});
	}


	private IsEqual<TrialResult<List<UserNotificationPreferencePojo>>> makeAllPrefsIsEqual(
		final String userUuid,
		final String method) {

		final IsEqual<UserNotificationPreferencePojo> unppIsEqual = makeUserNotificationPreferencePojoIsEqual(userUuid, method);

		final IterableIsEqual<UserNotificationPreferencePojo> byKey = unppIsEqual.pairwiseEqual().correlateByKey(
			new Function<UserNotificationPreferencePojo, String>() {
				@Override
				public String apply(final UserNotificationPreferencePojo userNotificationPreferencePojo) {
					return userNotificationPreferencePojo.getNotificationType();
				}
			});

		byKey.apply(ImmutableList.<UserNotificationPreferencePojo>of(), ImmutableList.<UserNotificationPreferencePojo>of());
		return Trial.makeIsEqual(makeBothOrNeitherThrow(method), new IsEqual<List<UserNotificationPreferencePojo>>() {
			@Override
			public boolean apply(final List<UserNotificationPreferencePojo> control, final List<UserNotificationPreferencePojo> experiment) {
				final boolean apply = byKey.apply(control, experiment);
				if (!apply) {
					KAFKA_CLIENT.send(TRIAL_LOG_TOPIC, getStringObjectMap(
						userUuid,
						control,
						experiment, "userNotificationPrefPojoIsEqual",
						ImmutableList.of("Pref lists mismatched"), method));
				}
				return apply;
			}
		}.pairwiseEqual());
	}

	public UserNotificationPreferencePojo findByUserAndNotificationType(
		final Long userId,
		final String notificationTypeCode) {
		if (userId == null || StringUtils.isBlank(notificationTypeCode)) {
			return null;
		}

		final User user = userDAO.getUser(userId);
		final String userUuid = user.getUuid();
		final String companyUuid = user.getCompany().getUuid();
		final Callable<Observable<UserNotificationPreferencePojo>> control =
			new Callable<Observable<UserNotificationPreferencePojo>>() {
				@Override
				public Observable<UserNotificationPreferencePojo> call() throws Exception {
					final UserNotificationPreference byUserAndNotificationType = userNotificationPreferenceDAO
						.findByUserAndNotificationType(userId, notificationTypeCode);
					if (byUserAndNotificationType == null) {
						return Observable.just(null);
					}
					final UserNotificationPreferencePojo value = convertEntityToPOJO(byUserAndNotificationType);
					return Observable.just(value);
				}
			};

		final Callable<Observable<UserNotificationPreferencePojo>> experiment =
			new Callable<Observable<UserNotificationPreferencePojo>>() {
				@Override
				public Observable<UserNotificationPreferencePojo> call() throws Exception {
					return newFindByUserAndNotificationType(userUuid, companyUuid, notificationTypeCode);
				}
			};

		try {
			return trial.doTrial(control, experiment, userNotificationPrefPojoIsEqual(
				userUuid, "findByUserAndNotificationType"), "findByUserAndType")
				.toBlocking().singleOrDefault(null);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@VisibleForTesting // would be private
	Observable<UserNotificationPreferencePojo> newFindByUserAndNotificationType(
		final String userUuid,
		final String companyUuid,
		final String notificationTypeCode) {
		return loadNotificationPrefs(userUuid, companyUuid, false)
			.map(new Func1<Map<String, UserNotificationPreferencePojo>, UserNotificationPreferencePojo>() {
					 @Override
					 public UserNotificationPreferencePojo call(final Map<String, UserNotificationPreferencePojo> map) {
						 return map.get(notificationTypeCode);
					 }
				 }
			);
	}

	public void setPaymentCenterAndEmailsNotificationPrefs(final Long userId, final Boolean giveAccess) {
		final User user = userDAO.getUser(userId);
		final String userUuid = user.getUuid();
		final String companyUuid = user.getCompany().getUuid();

		final Callable<Observable<Boolean>> control = new Callable<Observable<Boolean>>() {
			@Override
			public Observable<Boolean> call() throws Exception {
				for (String notification : PAYMENT_ACCESS_NOTIFICATIONS) {
					final UserNotificationPreference preference = userNotificationPreferenceDAO.findByUserAndNotificationType(userId, notification);
					if (preference != null && preference.getUser() != null) {
						preference.setEmailFlag(giveAccess);
						if (!giveAccess) {
							preference.setBullhornFlag(Boolean.FALSE);
							preference.setPushFlag(Boolean.FALSE);
						}
						userNotificationPreferenceDAO.saveOrUpdate(preference);
					}
				}

				for (String notification : INVOICE_EMAILS) {
					final UserNotificationPreference preference = userNotificationPreferenceDAO.findByUserAndNotificationType(userId, notification);
					if (preference != null && preference.getUser() != null) {
						preference.setEmailFlag(giveAccess);
						userNotificationPreferenceDAO.saveOrUpdate(preference);
					}
				}
				return Observable.just(true);
			}
		};

		final Callable<Observable<Boolean>> experiment = new Callable<Observable<Boolean>>() {
			@Override
			public Observable<Boolean> call() throws Exception {
				return newSetPaymentCenterAndEmailsNotificationPrefs(userUuid, companyUuid, giveAccess);
			}
		};
		try {
			trial.doTrial(control, experiment, makeBooleanTrialIsEqual("setPaymentCenterAndEmailsNotificationPrefs"),
				"set_payment_center_and_email_n10ns_prefs")
				.toBlocking().singleOrDefault(null);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@VisibleForTesting // would be private
	Observable<Boolean> newSetPaymentCenterAndEmailsNotificationPrefs(
		final String userUuid,
		final String companyUuid,
		final Boolean giveAccess) {
		return loadNotificationPrefs(userUuid, companyUuid, false)
			.map(new Func1<Map<String, UserNotificationPreferencePojo>, Boolean>() {
				@Override
				public Boolean call(final Map<String, UserNotificationPreferencePojo> map) {
					final SetPrefs.Builder builder = SetPrefs.newBuilder();
					final DimensionValuePair.Builder userDvp = DimensionValuePair.newBuilder()
						.setDimension(Dimension.USER)
						.setObjectId(userUuid);

					for (final String code : PAYMENT_ACCESS_NOTIFICATIONS) {
						if (map.containsKey(code) && map.get(code).getEmailFlag() != giveAccess) {
							builder.addModPref(ModPref.newBuilder()
								.setState(giveAccess)
								.setCode(code)
								.setDimensionValuePair(userDvp)
								.setType(Type.EMAIL));
						}
						if (!giveAccess) {
							final ModPref bullhorn = ModPref.newBuilder()
								.setState(false)
								.setType(Type.BULLHORN)
								.setDimensionValuePair(userDvp)
								.setCode(code)
								.build();
							builder.addModPref(bullhorn);
							builder.addModPref(bullhorn.toBuilder().setType(Type.PUSH));
						}
					}

					for (final String code : INVOICE_EMAILS) {
						if (map.containsKey(code) && map.get(code).getEmailFlag() != giveAccess) {
							builder.addModPref(ModPref.newBuilder()
								.setState(giveAccess)
								.setCode(code)
								.setDimensionValuePair(userDvp)
								.setType(Type.EMAIL));
						}
					}

					return notificationsClient
						.setPreferences(builder.build(), webRequestContextProvider.getRequestContext())
						.toBlocking().singleOrDefault(null).getSuccess();
				}
			});
	}

	private IsEqual<TrialResult<UserNotificationPreferencePojo>> userNotificationPrefPojoIsEqual(
		final String userUuid,
		final String method) {
		return Trial.makeIsEqual(makeBothOrNeitherThrow("userNotificationPrefPojoIsEqual"),
			makeUserNotificationPreferencePojoIsEqual(userUuid, method).pairwiseEqual());
	}

	private IsEqual<UserNotificationPreferencePojo> makeUserNotificationPreferencePojoIsEqual(
		final String userUuid,
		final String method) {
		return new IsEqual<UserNotificationPreferencePojo>() {
			@Override
			public boolean apply(final UserNotificationPreferencePojo control, final UserNotificationPreferencePojo experiment) {
				final List<String> mismatches = new ArrayList<>();
				final IsEqualUtil.MismatchConsumer consumer = IsEqualUtil.consumeToList(mismatches);
				final boolean success = checkNullity(control, experiment, consumer)
					&& startCompare(consumer)
					.dotEquals(control.getBullhornFlag(), experiment.getBullhornFlag(), "bullhornFlag")
					.dotEquals(control.getDispatchBullhornFlag(), experiment.getDispatchBullhornFlag(), "dispatchBullhornFlag")
					.dotEquals(control.getEmailFlag(), experiment.getEmailFlag(), "emailFlag")
					.dotEquals(control.getDispatchEmailFlag(), experiment.getDispatchEmailFlag(), "dispatchEmailFlag")
					.dotEquals(control.getPushFlag(), experiment.getPushFlag(), "pushFlag")
					.dotEquals(control.getDispatchPushFlag(), experiment.getDispatchPushFlag(), "pushFlag")
					.dotEquals(control.getSmsFlag(), experiment.getSmsFlag(), "smsFlag")
					.dotEquals(control.getDispatchSmsFlag(), experiment.getDispatchSmsFlag(), "smsFlag")
					.dotEquals(control.getVoiceFlag(), experiment.getVoiceFlag(), "voiceFlag")
					.dotEquals(control.getDispatchVoiceFlag(), experiment.getDispatchVoiceFlag(), "voiceFlag")
					.dotEquals(control.getNotificationType(), experiment.getNotificationType(), "type")
					.get();
				if (success) {
					return true;
				}
				KAFKA_CLIENT.send(TRIAL_LOG_TOPIC, getStringObjectMap(
					userUuid,
					control,
					experiment, "userNotificationPrefPojoIsEqual",
					mismatches, method));
				return false;
			}
		};
	}

	private UserNotificationPreferencePojo convertEntityToPOJO(final UserNotificationPreference pref) {
		return new UserNotificationPreferencePojoBuilder()
			.setNotificationType(pref.getNotificationType().getCode())
			.setEmailFlag(pref.getEmailFlag())
			.setFollowFlag(pref.getFollowFlag())
			.setBullhornFlag(pref.getBullhornFlag())
			.setSmsFlag(pref.getSmsFlag())
			.setVoiceFlag(pref.getVoiceFlag())
			.setPushFlag(pref.getPushFlag())
			.setDispatchEmailFlag(pref.getDispatchEmailFlag())
			.setDispatchBullhornFlag(pref.getDispatchBullhornFlag())
			.setDispatchSmsFlag(pref.getDispatchSmsFlag())
			.setDispatchVoiceFlag(pref.getDispatchVoiceFlag())
			.setDispatchPushFlag(pref.getDispatchPushFlag())
			.build();
	}


	public void removeSMSNotifications(final Long userId) {
		final User user = userDAO.getUser(userId);
		final String userUuid = user.getUuid();
		final String companyUuid = user.getCompany().getUuid();

		final Callable<Observable<Boolean>> control = new Callable<Observable<Boolean>>() {
			@Override
			public Observable<Boolean> call() throws Exception {
				List<UserNotificationPreference> preferences = userNotificationPreferenceDAO.findByUser(userId);
				for (UserNotificationPreference pref : preferences) {
					if (pref.getSmsFlag().equals(Boolean.TRUE)) {
						pref.setSmsFlag(Boolean.FALSE);
					}
				}
				return Observable.just(true);
			}
		};

		final Callable<Observable<Boolean>> experiment = new Callable<Observable<Boolean>>() {
			@Override
			public Observable<Boolean> call() throws Exception {
				return newRemoveSMSNotifications(userUuid, companyUuid);
			}
		};

		try {
			trial.doTrial(control, experiment, makeBooleanTrialIsEqual("removeSMSNotifications"),
				"remove_sms_notifications");
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@VisibleForTesting // would be private
	Observable<Boolean> newRemoveSMSNotifications(final String userUuid, final String companyUuid) {
		final Observable<Map<String, UserNotificationPreferencePojo>> prefs = loadNotificationPrefs(
			userUuid, companyUuid, false);
		return prefs.map(
			new Func1<Map<String, UserNotificationPreferencePojo>, Boolean>() {
				@Override
				public Boolean call(final Map<String, UserNotificationPreferencePojo> map) {
					final SetPrefs.Builder setPrefs = SetPrefs.newBuilder();
					for (final Entry<String, UserNotificationPreferencePojo> entry : map.entrySet()) {
						if (entry.getValue().getSmsFlag()) {
							setPrefs.addModPref(ModPref.newBuilder()
								.setCode(entry.getKey())
								.setType(Type.SMS)
								.setState(false)
								.setDimensionValuePair(DimensionValuePair.newBuilder()
									.setObjectId(userUuid)
									.setDimension(Dimension.USER)));
						}
					}
					final Status updateResp = notificationsClient.setPreferences(
						setPrefs.build(), webRequestContextProvider.getRequestContext()).toBlocking()
						.singleOrDefault(null);
					return updateResp.getSuccess();
				}
			});
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

	@VisibleForTesting // would be private
	Observable<Map<String, UserNotificationPreferencePojo>> loadNotificationPrefs(
		final String userUuid,
		final String companyUuid,
		final boolean loadDefaults) {
		final GetPrefs.Builder builder = GetPrefs.newBuilder()
			.addDimensionValuePair(DimensionValuePair.newBuilder()
				.setDimension(Dimension.COMPANY)
				.setObjectId(companyUuid))
			.addDimensionValuePair(DimensionValuePair.newBuilder()
				.setDimension(Dimension.USER)
				.setObjectId(userUuid));
		if (loadDefaults) {
			builder
				.addDimensionValuePair(DimensionValuePair.newBuilder()
					.setDimension(Dimension.DEFAULT)
					.setObjectId("-1"));
		}
		final Observable<GetResp> result = notificationsClient.getPreferences(builder
			.build(), webRequestContextProvider.getRequestContext());


		return result.map(new Func1<GetResp, Map<String, UserNotificationPreferencePojo>>() {
			@Override
			public Map<String, UserNotificationPreferencePojo> call(final GetResp getResp) {
				final Map<String, UserNotificationPreferencePojoBuilder> builders = Maps.newHashMap();

				for (final Entry<String, TypeValue> entry : getResp.getPref().entrySet()) {
					final String rawNotificationTypeCode = entry.getKey();
					final String notificationTypeCode;
					final boolean isDispatcher;
					if (rawNotificationTypeCode.endsWith(DISPATCHER_SUFFIX)) {
						isDispatcher = true;
						// trim off the trailing DISPATCHER_SUFFIX
						notificationTypeCode = rawNotificationTypeCode
							.substring(0, rawNotificationTypeCode.length() - DISPATCHER_SUFFIX.length());
					} else {
						notificationTypeCode = rawNotificationTypeCode;
						isDispatcher = false;
					}

					if (!builders.containsKey(notificationTypeCode)) {
						builders.put(notificationTypeCode, new UserNotificationPreferencePojoBuilder()
							.setNotificationType(notificationTypeCode));
					}
					final UserNotificationPreferencePojoBuilder builder = builders.get(notificationTypeCode);
					if (!isDispatcher) {
						handleNonDispatcherPrefs(entry, builder);

					} else {
						handleDispatcherPrefs(entry, builder);
					}
				}

				final ImmutableMap.Builder<String, UserNotificationPreferencePojo> mapBuilder = ImmutableMap.builder();
				for (final Entry<String, UserNotificationPreferencePojoBuilder> builderEntry : builders.entrySet()) {
					mapBuilder.put(builderEntry.getKey(), builderEntry.getValue().build());
				}

				return mapBuilder.build();
			}
		});
	}

	private void handleDispatcherPrefs(
		final Entry<String, TypeValue> entry,
		final UserNotificationPreferencePojoBuilder builder) {
		final TypeValue dispatcherPref = entry.getValue();
		for (final TypeToValue tv : dispatcherPref.getTypeValueList()) {
			switch (tv.getType()) {
				case EMAIL:
					builder.setDispatchEmailFlag(tv.getValue());
					break;
				case SMS:
					builder.setDispatchSmsFlag(tv.getValue());
					break;
				case VOICE:
					builder.setDispatchVoiceFlag(tv.getValue());
					break;
				case PUSH:
					builder.setDispatchPushFlag(tv.getValue());
					break;
				case BULLHORN:
					builder.setDispatchBullhornFlag(tv.getValue());
					break;
			}

		}
	}

	private void handleNonDispatcherPrefs(
		final Entry<String, TypeValue> entry,
		final UserNotificationPreferencePojoBuilder builder) {
		// FIXME not doing anything with follow!
		final TypeValue regPref = entry.getValue();
		for (final TypeToValue tv : regPref.getTypeValueList()) {
			switch (tv.getType()) {
				case EMAIL:
					builder.setEmailFlag(tv.getValue());
					break;
				case SMS:
					builder.setSmsFlag(tv.getValue());
					break;
				case VOICE:
					builder.setVoiceFlag(tv.getValue());
					break;
				case PUSH:
					builder.setPushFlag(tv.getValue());
					break;
				case BULLHORN:
					builder.setBullhornFlag(tv.getValue());
					break;
			}
		}
	}

	public void setPrefs(final Long userId, final NotificationPreferenceDTO[] notificationPreferenceDTOs) {
		final PersonaPreference pref = personaPreferenceDAO.get(userId);
		final boolean isDispatcher = pref != null && pref.isDispatcher();

		final User user = userDAO.getUser(userId);
		final String userUuid = user.getUuid();

		final Callable<Observable<Boolean>> control = new Callable<Observable<Boolean>>() {
			@Override
			public Observable<Boolean> call() throws Exception {
				for (final NotificationPreferenceDTO notificationPreferenceDTO : notificationPreferenceDTOs) {

					UserNotificationPreference pref = userNotificationPreferenceDAO.findByUserAndNotificationType(
						userId, notificationPreferenceDTO.getNotificationTypeCode());
					if (pref == null) {
						pref = new UserNotificationPreference();
						pref.setNotificationType(new NotificationType(notificationPreferenceDTO.getNotificationTypeCode()));
					}

					pref.setUser(user);
					if (isDispatcher) {
						pref.setDispatchEmailFlag(notificationPreferenceDTO.getEmailFlag());
						pref.setDispatchBullhornFlag(notificationPreferenceDTO.getBullhornFlag());
						pref.setDispatchPushFlag(notificationPreferenceDTO.getPushFlag());
						pref.setDispatchSmsFlag(notificationPreferenceDTO.getSmsFlag());
						pref.setDispatchVoiceFlag(notificationPreferenceDTO.getVoiceFlag());
					} else {
						pref.setEmailFlag(notificationPreferenceDTO.getEmailFlag());
						pref.setBullhornFlag(notificationPreferenceDTO.getBullhornFlag());
						pref.setPushFlag(notificationPreferenceDTO.getPushFlag());
						pref.setSmsFlag(notificationPreferenceDTO.getSmsFlag());
						pref.setVoiceFlag(notificationPreferenceDTO.getVoiceFlag());
						pref.setFollowFlag(notificationPreferenceDTO.getFollowFlag());
					}
					userNotificationPreferenceDAO.saveOrUpdate(pref);
				}
				return Observable.just(true);
			}
		};

		final Callable<Observable<Boolean>> experiment = new Callable<Observable<Boolean>>() {
			@Override
			public Observable<Boolean> call() throws Exception {
				return newSetPrefs(userUuid, notificationPreferenceDTOs, isDispatcher);
			}
		};

		try {
			trial.doTrial(control, experiment, makeBooleanTrialIsEqual("setPrefs"), "set_prefs");
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@VisibleForTesting // would be private
	Observable<Boolean> newSetPrefs(
		final String userUuid,
		final NotificationPreferenceDTO[] notificationPreferenceDTOs,
		final boolean isDispatcher) {
		final SetPrefs.Builder builder = SetPrefs.newBuilder();

		final DimensionValuePair.Builder userDvp = DimensionValuePair.newBuilder()
			.setDimension(Dimension.USER)
			.setObjectId(userUuid);

		for (final NotificationPreferenceDTO pref : notificationPreferenceDTOs) {
			final ModPref baseMod = ModPref.newBuilder()
				.setDimensionValuePair(userDvp)
				.setCode(pref.getNotificationTypeCode() + (isDispatcher ? DISPATCHER_SUFFIX : ""))
				.build();

			builder
				.addModPref(baseMod.toBuilder()
					.setType(Type.EMAIL)
					.setState(pref.getEmailFlag()))
				.addModPref(baseMod.toBuilder()
					.setType(Type.VOICE)
					.setState(pref.getVoiceFlag()))
				.addModPref(baseMod.toBuilder()
					.setType(Type.BULLHORN)
					.setState(pref.getBullhornFlag()))
				.addModPref(baseMod.toBuilder()
					.setType(Type.PUSH)
					.setState(pref.getVoiceFlag()))
				.addModPref(baseMod.toBuilder()
					.setType(Type.SMS)
					.setState(pref.getSmsFlag()));
		}
		final Status updateResp = notificationsClient.setPreferences(
			builder.build(), webRequestContextProvider.getRequestContext()).toBlocking()
			.singleOrDefault(null);
		return Observable.just(updateResp.getSuccess());
	}

	public void setManageBankAndFundsNotificationPrefs(final Long userId, final Boolean giveAccess) {
		final User user = userDAO.getUser(userId);
		final String userUuid = user.getUuid();
		final String companyUuid = user.getCompany().getUuid();

		final Callable<Observable<Boolean>> control = new Callable<Observable<Boolean>>() {
			@Override
			public Observable<Boolean> call() throws Exception {
				for (String notification : MANAGE_FUNDS_NOTIFICATIONS) {
					final UserNotificationPreference preference = userNotificationPreferenceDAO.findByUserAndNotificationType(
						userId, notification);
					if (preference != null && preference.getUser() != null) {
						preference.setEmailFlag(giveAccess);
						if (!giveAccess) {
							preference.setPushFlag(Boolean.FALSE);
							preference.setBullhornFlag(Boolean.FALSE);
						}
						userNotificationPreferenceDAO.saveOrUpdate(preference);
					}
				}
				return Observable.just(true);
			}
		};

		final Callable<Observable<Boolean>> experiment = new Callable<Observable<Boolean>>() {
			@Override
			public Observable<Boolean> call() throws Exception {
				return newSetManageBankAndFundsNotificationPrefs(userUuid, companyUuid, giveAccess);
			}
		};
		try {
			trial.doTrial(control, experiment, makeBooleanTrialIsEqual("setManageBankAndFundsNotificationPrefs"),
				"set_manage_bank_and_funds_notification_prefs")
				.toBlocking().singleOrDefault(null);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@VisibleForTesting // would be private
	Observable<Boolean> newSetManageBankAndFundsNotificationPrefs(
		final String userUuid,
		final String companyUuid,
		final Boolean giveAccess) {
		return loadNotificationPrefs(userUuid, companyUuid, false)
			.map(new Func1<Map<String, UserNotificationPreferencePojo>, Boolean>() {
				@Override
				public Boolean call(final Map<String, UserNotificationPreferencePojo> map) {
					final SetPrefs.Builder builder = SetPrefs.newBuilder();
					final DimensionValuePair.Builder userDvp = DimensionValuePair.newBuilder()
						.setDimension(Dimension.USER)
						.setObjectId(userUuid);

					for (final String code : MANAGE_FUNDS_NOTIFICATIONS) {
						if (map.containsKey(code) && map.get(code).getEmailFlag() != giveAccess) {
							builder.addModPref(ModPref.newBuilder()
								.setState(giveAccess)
								.setCode(code)
								.setDimensionValuePair(userDvp)
								.setType(Type.EMAIL));
						}
						if (!giveAccess) {
							final ModPref bullhorn = ModPref.newBuilder()
								.setState(false)
								.setType(Type.BULLHORN)
								.setDimensionValuePair(userDvp)
								.setCode(code)
								.build();
							builder.addModPref(bullhorn);
							builder.addModPref(bullhorn.toBuilder().setType(Type.PUSH));
						}
					}
					return notificationsClient
						.setPreferences(builder.build(), webRequestContextProvider.getRequestContext())
						.toBlocking().singleOrDefault(null).getSuccess();
				}
			});
	}

	//TODO
	public Set<User> findUsersByCompanyAndNotificationType(final Long companyId, final String notificationCode) {
		Assert.notNull(companyId);

		final Callable<Observable<Set<User>>> control = new Callable<Observable<Set<User>>>() {
			@Override
			public Observable<Set<User>> call() throws Exception {
				final Set<User> value = Sets.newHashSet(userNotificationPreferenceDAO.findUsersByCompanyAndNotificationType(
					companyId, notificationCode));
				return Observable.just(value);
			}
		};

		final Callable<Observable<Set<User>>> experiment = new Callable<Observable<Set<User>>>() {
			@Override
			public Observable<Set<User>> call() throws Exception {
				final Set<User> value = Sets.newHashSet();
				//FIXME -- fill me in
				return Observable.just(value);
			}

		};

		try {
			return trial.doTrial(control, experiment, makeUserSetTrialIsEqual("findUsersByCompanyAndNotificationType"),
				"find_users_by_company_and_notification_type")
				.toBlocking().singleOrDefault(null);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private IsEqual<User> makeUserIsEqual(final String method) {
		return new IsEqual<User>() {
			@Override
			public boolean apply(final User control, final User experiment) {

				final List<String> mismatches = new ArrayList<>();
				final IsEqualUtil.MismatchConsumer consumer = IsEqualUtil.consumeToList(mismatches);
				final boolean success = checkNullity(control, experiment, consumer)
					&& startCompare(consumer)
					.dotEquals(control.getUuid(), experiment.getUuid(), "uuid")
					.get();
				if (success) {
					return true;
				}
				KAFKA_CLIENT.send(TRIAL_LOG_TOPIC, getStringObjectMap(
					control == null ? "--empty--" : control.getUuid(),
					experiment == null ? "--empty--" : experiment.getUuid(),
					"userIsEqual",
					mismatches, method));
				return false;
			}
		};
	}


	private IsEqual<TrialResult<Set<User>>> makeUserSetTrialIsEqual(final String method) {
		final IsEqual<Iterable<User>> userIterableIsEqual = makeUserIsEqual(method).pairwiseEqual().correlateByKey(
			new Function<User, String>() {
				@Override
				public String apply(final User user) {
					return user.getUserNumber();
				}
			}
		);
		// Because Java 7's type system isn't quite up to snuff all the time.  Or somewhere in the stack of things,
		// there's not a proper ? extends X, or ? super X where it ought to be.
		final IsEqual<Set<User>> castIt = new IsEqual<Set<User>>() {
			@Override
			public boolean apply(final Set<User> control, final Set<User> experiment) {
				return userIterableIsEqual.apply(control, experiment);
			}
		};

		return Trial.makeIsEqual(makeBothOrNeitherThrow(method), castIt.pairwiseEqual());
	}
}

