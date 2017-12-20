package com.workmarket.service.business.integration.event;

import com.google.api.client.repackaged.com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.dao.integration.autotask.AutotaskUserCustomFieldsPreferenceDAO;
import com.workmarket.dao.note.NoteDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.integration.autotask.AutotaskTicket;
import com.workmarket.domains.model.integration.autotask.AutotaskUser;
import com.workmarket.domains.model.integration.autotask.AutotaskUserCustomFieldsPreference;
import com.workmarket.domains.model.integration.webhook.SalesforceWebHookClient;
import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.domains.model.integration.webhook.util.WebHookInvocationBuilder;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.dao.state.WorkSubStatusTypeAssociationDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.ScheduleNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.feature.vo.FeatureToggleAndStatus;
import com.workmarket.integration.autotask.proxy.AutotaskProxyFactory;
import com.workmarket.integration.webhook.WebHookDispatchField;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.PaymentSummaryDTO;
import com.workmarket.service.business.dto.integration.ParsedWebHookDTO;
import com.workmarket.service.business.dto.integration.SalesforceAccessTokenDTO;
import com.workmarket.service.business.integration.hooks.autotask.AutotaskIntegrationService;
import com.workmarket.service.business.integration.hooks.webhook.SalesforceWebHookIntegrationService;
import com.workmarket.service.business.integration.hooks.webhook.WebHookHTTPPoolingFactory;
import com.workmarket.service.business.integration.hooks.webhook.WebHookIntegrationService;
import com.workmarket.service.business.integration.hooks.webhook.WebHookInvocationProvider;
import com.workmarket.service.business.integration.mbo.SalesForceClient;
import com.workmarket.service.business.pay.PaymentSummaryService;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.file.AWSConfigData;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.service.infra.security.SecurityContext;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.SerializationUtilities;
import com.workmarket.webhook.relay.WebhookRelayClient;
import com.workmarket.webhook.relay.gen.Message.SaveRequest;
import com.workmarket.webhook.relay.gen.Message.WebhookInvocation;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

/**
 * Created by nick on 2012-12-24 2:30 PM
 * service for integration events
 * <p/>
 * NOTE: assume that any of these could be called outside of ThreadLocal, so there is NO access to the securityContext here
 */
@Service
public class IntegrationListenerServiceImpl implements IntegrationListenerService {

	private static final Logger logger = LoggerFactory.getLogger(IntegrationListenerServiceImpl.class);
	@Autowired WorkService workService;
	@Autowired PaymentSummaryService paymentSummaryService;
	@Autowired AutotaskIntegrationService autotaskIntegrationService;
	@Autowired AutotaskProxyFactory autotaskProxyFactory;
	@Autowired RegistrationService registrationService;
	@Autowired AutotaskUserCustomFieldsPreferenceDAO autotaskUserCustomFieldsPreferenceDAO;
	@Autowired WebHookHTTPPoolingFactory webHookHTTPPoolingFactory;
	@Autowired CompanyService companyService;
	@Autowired WorkSubStatusTypeAssociationDAO workSubStatusTypeAssociationDAO;
	@Autowired WorkNegotiationService workNegotiationService;
	@Autowired NoteDAO noteDAO;
	@Autowired AssetManagementService assetService;
	@Autowired AuthenticationService authenticationService;
	@Autowired WebHookIntegrationService webHookIntegrationService;
	@Autowired SalesforceWebHookIntegrationService salesforceWebHookIntegrationService;
	@Autowired RedisAdapter redisAdapter;
	@Autowired SalesForceClient salesForceClient;
	@Autowired ProfileService profileService;
	@Autowired WorkNoteService workNoteService;
	@Autowired UserService userService;
	@Autowired WorkSubStatusService workSubStatusService;
	@Autowired WorkResourceService workResourceService;
	@Autowired FeatureEntitlementService featureEntitlementService;
	@Autowired SecurityContext securityContext;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired FeatureEvaluator featureEvaluator;
	@Autowired WebRequestContextProvider webRequestContextProvider;
	@Autowired WebHookInvocationProvider webHookInvocationProvider;
	@Resource(name="aWSConfigData") private AWSConfigData aWSConfigData;

	private WMMetricRegistryFacade metricFacade;
	private WebhookRelayClient webhookRelayClient = new WebhookRelayClient();

	@PostConstruct
	@VisibleForTesting
	void init() {
		metricFacade = new WMMetricRegistryFacade(metricRegistry, "webhook");
	}

	@Override
	public boolean doAuthentication(WebHook webHook, Map <String, String> authenticationHeaders, boolean skipCache) {
		if (webHook.getWebHookClient() instanceof SalesforceWebHookClient) {
			SalesforceWebHookClient salesforceWebHookClient = (SalesforceWebHookClient)webHook.getWebHookClient();

			String key = RedisFilters.webHookSalesforceKeyFor(salesforceWebHookClient.getCompany().getId());

			Optional<Object> cacheAccessTokenOptional = redisAdapter.get(key, SalesforceWebHookClient.ACCESS_TOKEN);
			String accessToken;

			if (skipCache || !cacheAccessTokenOptional.isPresent()) {
				Optional<SalesforceAccessTokenDTO> salesforceAuthentication = salesforceWebHookIntegrationService.getSalesforceAccessToken(salesforceWebHookClient);

				if (!salesforceAuthentication.isPresent()) {
					return false;
				}

				accessToken = salesforceAuthentication.get().getAccess_token();

				redisAdapter.set(key, SalesforceWebHookClient.ACCESS_TOKEN,
						accessToken,
						SalesforceWebHookClient.ACCESS_TOKEN_LIFESPAN);
			} else {
				accessToken = String.valueOf(cacheAccessTokenOptional.get());
			}

			authenticationHeaders.put(
					SalesforceWebHookClient.AUTH_HEADER_NAME,
					format(SalesforceWebHookClient.AUTH_HEADER_VALUE, accessToken)
			);
		}

		return true;
	}

	@Override
	public boolean doRequest(WebHook webHook, ParsedWebHookDTO parsedWebHookDTO) {
		return webHookHTTPPoolingFactory.launchHook(webHook, parsedWebHookDTO).series() == HttpStatus.Series.SUCCESSFUL;
	}

	@Override
	public boolean runWebHook(final AbstractWork work, final WebHook webHook, final Map<String, String> contextVariables) {
		final Map <String, String> authenticationHeaders = new HashMap<>();

		if (!doAuthentication(webHook, authenticationHeaders, false)) {
			webHookIntegrationService.handleError(webHook);
			return false;
		}

		final Optional<ParsedWebHookDTO> parsedWebHookDTO = webHookHTTPPoolingFactory.buildHook(work, webHook, contextVariables,
				authenticationHeaders);

		if (!parsedWebHookDTO.isPresent()) {
			logger.error(format("[integration] Error parsing webhook ID=%d, workId=%d", webHook.getId(), work.getId()));
			return false;
		}

		final Observable<FeatureToggleAndStatus> toggle =
				featureEntitlementService.getFeatureToggleForCurrentUser("webhook.relay");

    try {
      return toggle.map(new Func1<FeatureToggleAndStatus, Boolean>() {
        @Override
        public Boolean call(FeatureToggleAndStatus toggleResult) {
          if (toggleResult.getStatus().getSuccess()
            && toggleResult.getFeatureToggle() != null
            && Boolean.valueOf(toggleResult.getFeatureToggle().getValue())) { // send via Webhook Relay Service
            final ParsedWebHookDTO dto = parsedWebHookDTO.get();
            final WebhookInvocation webhookInvocation =
              WebHookInvocationBuilder.build(dto, webHook, webRequestContextProvider.getRequestContext(),
                contextVariables.get(WebHookDispatchField.FILE_UUID.getFieldName()),
                contextVariables.get("file_mime_type"),
                contextVariables.get("file_s3_bucket_name"));
            final List<WebhookInvocation> result = webhookRelayClient.save(SaveRequest.newBuilder()
              .addWebhookInvocation(webhookInvocation).build(), webRequestContextProvider.getRequestContext())
              .toList().toBlocking().single(); // we rely on this to throw a runtime exception on error so we don't commit the transaction
             webHookInvocationProvider.getWebHookInvocationContext().add(result.get(0).getUuid()); // cache for later sending
            return true;
          } else {
            return IntegrationListenerServiceImpl.this.doRequestLegacy(webHook, parsedWebHookDTO, authenticationHeaders);
          }
        }
      }).toBlocking().single();
    } catch (Exception e) {
			return IntegrationListenerServiceImpl.this.doRequestLegacy(webHook, parsedWebHookDTO, authenticationHeaders);
    }
  }

	private boolean doRequestLegacy(
			final WebHook webHook,
			final Optional<ParsedWebHookDTO> parsedWebHookDTO,
			final Map<String, String> authenticationHeaders) {
		if (!doRequest(webHook, parsedWebHookDTO.get())) {
			// try again after with a fresh authentication
			if (!doAuthentication(webHook, authenticationHeaders, true)) {
				webHookIntegrationService.handleError(webHook);
				return false;
			}

			if (!doRequest(webHook, parsedWebHookDTO.get())) {
				webHookIntegrationService.handleError(webHook);
				return false;
			}
		}

		webHookIntegrationService.clearErrors(webHook);
		return true;
	}

	@Override
	public boolean onWorkCreated(Long workId, Long buyerId, Long autotaskId) {
		AutotaskUser autotaskUser = getAutotaskUser(checkNotNull(buyerId));

		Assert.notNull(autotaskUser);

		Map<String, AutotaskUserCustomFieldsPreference> preferenceMap = getAutotaskCustomUserPreference(autotaskUser.getId());

		if (preferenceMap == null) {
			logger.error(format("[autotask] onWorkCreated - Autotask userId=%d preference not found, workId=%d",
					autotaskUser.getId(), workId));
			return false;
		}

		AutotaskTicket ticket = new AutotaskTicket()
				.setTicketId(checkNotNull(autotaskId));

		AbstractWork work = checkNotNull(workService.findWork(workId));

		boolean isSuccess = autotaskIntegrationService.updateTicketOnWorkCreated(autotaskUser, ticket, work, preferenceMap);
		logger.info(format("[autotask] onWorkCreated - updated workId=%d, successful=%b", workId, isSuccess));

		return isSuccess;
	}

	@Override
	public boolean onWorkCreated(Long workId, Long webHookId) {
		Assert.notNull(workId);
		Assert.notNull(webHookId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Map<String, String> contextVariables = Maps.newHashMap();

		User creator = userService.findUserById(work.getCreatorId());
		if (shouldSuppressWebhook(creator, webHook)) {
			return true;
		}

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onWorkAccepted(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		WorkResource workResource = checkNotNull(workService.findActiveWorkResource(workId));
		Boolean isAutotask = (Boolean) checkNotNull(eventArguments.get(IntegrationEvent.IS_AUTOTASK));
		boolean isWebHook = eventArguments.get(IntegrationEvent.WEBHOOK_ID) != null;
		Long resourceId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.RESOURCE_ID));
		boolean notifyMbo = Boolean.TRUE.equals(eventArguments.get(IntegrationEvent.NOTIFY_MBO));
		boolean isSuccess = false;

		if (isAutotask) {
			AutotaskUser autotaskUser = getAutotaskUser(work.getBuyer().getId());
			Map<String, AutotaskUserCustomFieldsPreference> preferenceMap = getAutotaskCustomUserPreference(autotaskUser.getId());
			AutotaskTicket autotaskTicket = getAutotaskTicketByUserAndWork(autotaskUser, workId);

			if (preferenceMap == null) {
				logger.error(format("[autotask] onWorkAccepted - Autotask userId=%d preference not found, workId=%d", autotaskUser.getId(), workId));
				return isSuccess;
			}

			isSuccess = autotaskIntegrationService.updateTicketOnWorkAccepted(autotaskUser, autotaskTicket, work, workResource, preferenceMap);
			logger.info(format("[autotask] onWorkAccepted - updated workId=%d, resourceId=%d, successful=%b", workId, resourceId, isSuccess));
		} else if (isWebHook) {
			Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
			WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
			Map<String, String> contextVariables = Maps.newHashMap();

			isSuccess = runWebHook(work, webHook, contextVariables);
		}

		// we notify MBO if buyer or seller are using MBO services
		if (notifyMbo) {
			try {
				salesForceClient.createOpportunity(work, profileService.findMboProfile(resourceId));
			} catch (Exception e) {
				logger.error("[MBO] onWorkAccepted - error creating opportunity", e);
			}
		}

		return isSuccess;
	}

	@Override
	public boolean onWorkComplete(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Boolean isAutotask = (Boolean) checkNotNull(eventArguments.get(IntegrationEvent.IS_AUTOTASK));
		boolean isSuccess;

		if (isAutotask) {
			PaymentSummaryDTO paymentDTO = checkNotNull(paymentSummaryService.generatePaymentSummaryForWork(workId));

			AutotaskUser autotaskUser = getAutotaskUser(work.getBuyer().getId());
			Map<String, AutotaskUserCustomFieldsPreference> preferenceMap = getAutotaskCustomUserPreference(autotaskUser.getId());
			AutotaskTicket autotaskTicket = getAutotaskTicketByUserAndWork(autotaskUser, workId);

			if (preferenceMap == null) {
				logger.error(format("[autotask] onWorkComplete - Autotask userId=%d preference not found, workId=%d", autotaskUser.getId(), workId));
				return false;
			}

			isSuccess = autotaskIntegrationService.updateTicketOnWorkComplete(autotaskUser, autotaskTicket, work,
					paymentDTO, preferenceMap);
			logger.info(format("[autotask] onWorkComplete - updated workId=%d, successful=%b", workId, isSuccess));
		} else {
			Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
			WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
			Map<String, String> contextVariables = Maps.newHashMap();

			isSuccess = runWebHook(work, webHook, contextVariables);
		}
		return isSuccess;
	}

	@Override
	public boolean onWorkApproved(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Boolean isAutotask = (Boolean) checkNotNull(eventArguments.get(IntegrationEvent.IS_AUTOTASK));
		boolean isSuccess;

		if (isAutotask) {
			PaymentSummaryDTO paymentDTO = checkNotNull(paymentSummaryService.generatePaymentSummaryForWork(workId));

			AutotaskUser autotaskUser = getAutotaskUser(work.getBuyer().getId());
			Map<String, AutotaskUserCustomFieldsPreference> preferenceMap = getAutotaskCustomUserPreference(autotaskUser.getId());
			AutotaskTicket autotaskTicket = getAutotaskTicketByUserAndWork(autotaskUser, workId);

			if (preferenceMap == null) {
				logger.error(format("[autotask] onWorkApproved - Autotask userId=%d preference not found, workId=%d", autotaskUser.getId(), workId));
				return false;
			}

			isSuccess = autotaskIntegrationService.updateTicketOnWorkApproved(autotaskUser, autotaskTicket, work,
					paymentDTO, preferenceMap);
			logger.info(format("[autotask] onWorkApproved - updated workId=%d, successful=%b", workId, isSuccess));
		} else {
			Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
			WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
			Map<String, String> contextVariables = Maps.newHashMap();

			isSuccess = runWebHook(work, webHook, contextVariables);
		}

		return isSuccess;
	}

	@Override
	public boolean onWorkSent(Long workId, Map<String, Object> eventArguments) {
		return onWork(workId, eventArguments);
	}

	@Override
	public boolean onWorkPaid(Long workId, Map<String, Object> eventArguments) {
		return onWork(workId, eventArguments);
	}

	@Override
	public boolean onWorkVoided(Long workId, Map<String, Object> eventArguments) {
		return onWork(workId, eventArguments);
	}

	@Override
	public boolean onWorkCancelled(Long workId, Map<String, Object> eventArguments) {
		return onWork(workId, eventArguments);
	}

	@Override
	public boolean onWorkConfirmed(Long workId, Map<String, Object> eventArguments) {
		return onWork(workId, eventArguments);
	}

	@Override
	public boolean onWorkCustomFieldsUpdated(Long workId, Map<String, Object> eventArguments) {
		return onWork(workId, eventArguments);
	}

	private boolean onWork(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();

		if (shouldSuppressWebhook(eventArguments, webHook)) {
			return true;
		}

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Map<String, String> contextVariables = Maps.newHashMap();

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onCheckIn(Long workId, Map<String, Object> eventArguments) {
		return doCheckInOut(workId, eventArguments, true);
	}

	@Override
	public boolean onCheckOut(Long workId, Map<String, Object> eventArguments) {
		return doCheckInOut(workId, eventArguments, false);
	}

	@Override
	public boolean onAttachmentAdded(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = workService.findWork(workId);
		Boolean isAutotask = (Boolean) checkNotNull(eventArguments.get(IntegrationEvent.IS_AUTOTASK));
		Long assetId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.ASSET_ID));
		final Asset asset = assetService.findAssetById(assetId);
		boolean isSuccess;

		if (isAutotask) {
			AutotaskUser autotaskUser = getAutotaskUser(work.getBuyer().getId());
			AutotaskTicket autotaskTicket = getAutotaskTicketByUserAndWork(autotaskUser, workId);

			isSuccess = autotaskIntegrationService.updateTicketOnAttachmentData(autotaskUser, autotaskTicket, asset);
			logger.info(format("[autotask] onAttachmentAdded - updated workId=%d, AssetId=%d, successful=%b", workId, assetId, isSuccess));
		} else {
			Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
			WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
			User creator = userService.findCreatorByAssetId(assetId);

			if (shouldSuppressWebhook(creator, webHook)) {
				return true;
			}

			final Map<String, String> contextVariables = Maps.newHashMap();
			final RemoteFileType type =
					asset.getAvailability().hasGuestAvailability() ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE;
			final String s3BucketName = type.getName() + aWSConfigData.getRemoteFileEnvironment();

			contextVariables.put(WebHookDispatchField.FILE_NAME.getFieldName(), asset.getName());
			contextVariables.put(WebHookDispatchField.FILE_DESCRIPTION.getFieldName(), asset.getDescription());
			contextVariables.put(WebHookDispatchField.FILE_UUID.getFieldName(), asset.getUUID());
			contextVariables.put(WebHookDispatchField.FILE_DATA.getFieldName(), getVariablePlaceholder(WebHookDispatchField.FILE_DATA.getFieldName()));
			contextVariables.put(WebHookDispatchField.FILE_DATA_BASE_64.getFieldName(), getVariablePlaceholder(WebHookDispatchField.FILE_DATA_BASE_64.getFieldName()));
			contextVariables.put(WebHookDispatchField.FILE_DATA_RAW.getFieldName(), getVariablePlaceholder(WebHookDispatchField.FILE_DATA_RAW.getFieldName()));
			contextVariables.put(WebHookDispatchField.FILE_DATA_BYTE_LENGTH.getFieldName(), getVariablePlaceholder(WebHookDispatchField.FILE_DATA_BYTE_LENGTH.getFieldName()));
			contextVariables.put("file_mime_type", asset.getMimeType());
			contextVariables.put("file_s3_bucket_name", s3BucketName);

			final Observable<FeatureToggleAndStatus> toggle =
					featureEntitlementService.getFeatureToggleForCurrentUser("webhook.relay");

			toggle.subscribe(new Action1<FeatureToggleAndStatus>() {
				@Override
				public void call(final FeatureToggleAndStatus toggle) {
					// Databind attachment data info to the template if we are not sending to Webhook Relay Service. If not,
					// the service will use the asset UUID to fetch and databind on sending.
					if (!toggle.getStatus().getSuccess()
							|| toggle.getFeatureToggle() == null
							|| !Boolean.valueOf(toggle.getFeatureToggle().getValue())) {
						putAssetVariablesForLegacy(contextVariables, asset.getUUID());
					}

				}
			}, new Action1<Throwable>() {
				@Override
				public void call(final Throwable throwable) {
					logger.error("Error fetching feature toggle", throwable);
					putAssetVariablesForLegacy(contextVariables, asset.getUUID());
				}
			});

			isSuccess = runWebHook(work, webHook, contextVariables);
		}

		return isSuccess;
	}

	private static String getVariablePlaceholder(String variable) {
		return String.format("${%s}", variable);
	}

	private void putAssetVariablesForLegacy(final Map<String, String> contextVariables, String assetUuid) {
		try {
			URL url = new URL(assetService.getAuthorizedUriByUuid(assetUuid));
			InputStream in = url.openStream();
			byte[] bytes = FileCopyUtils.copyToByteArray(in);
			in.close();

			contextVariables.put(WebHookDispatchField.FILE_DATA.getFieldName(), SerializationUtilities.encodeBase64(bytes));
			contextVariables.put(WebHookDispatchField.FILE_DATA_BASE_64.getFieldName(), SerializationUtilities.encodeBase64(bytes));
			contextVariables.put(WebHookDispatchField.FILE_DATA_RAW.getFieldName(), new String(bytes, Charsets.ISO_8859_1));
			contextVariables.put(WebHookDispatchField.FILE_DATA_BYTE_LENGTH.getFieldName(), String.valueOf(bytes.length));
		} catch (Exception e) {
			contextVariables.put(WebHookDispatchField.FILE_DATA.getFieldName(), null);
			contextVariables.put(WebHookDispatchField.FILE_DATA_BASE_64.getFieldName(), null);
			contextVariables.put(WebHookDispatchField.FILE_DATA_RAW.getFieldName(), null);
			contextVariables.put(WebHookDispatchField.FILE_DATA_BYTE_LENGTH.getFieldName(), null);
		}
	}

	@Override
	public boolean onAttachmentRemoved(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long assetId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.ASSET_ID));
		Asset asset = assetService.findAssetById(assetId);
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		User modifier = userService.findModifierByAssetId(assetId);

		if (shouldSuppressWebhook(modifier, webHook)) {
			return true;
		}

		Map<String, String> contextVariables = Maps.newHashMap();

		contextVariables.put(WebHookDispatchField.FILE_NAME.getFieldName(), asset.getName());
		contextVariables.put(WebHookDispatchField.FILE_DESCRIPTION.getFieldName(), asset.getDescription());
		contextVariables.put(WebHookDispatchField.FILE_UUID.getFieldName(), asset.getUUID());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onNoteAdded(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = workService.findWork(workId);
		Boolean isAutotask = (Boolean) checkNotNull(eventArguments.get(IntegrationEvent.IS_AUTOTASK));
		Long noteId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NOTE_ID));
		boolean isSuccess;

		if (isAutotask) {
			Note note = workNoteService.findNoteById(noteId);

			AutotaskUser autotaskUser = getAutotaskUser(work.getBuyer().getId());
			AutotaskTicket autotaskTicket = getAutotaskTicketByUserAndWork(autotaskUser, workId);

			//check if notes are disabled via settings
			isSuccess = Boolean.TRUE;
			if (autotaskIntegrationService.findAutotaskUserPreference(autotaskUser.getId(), NotificationType.AUTOTASK_NOTES_ENABLED)) {
				isSuccess = autotaskIntegrationService.updateTicketOnNoteAdded(autotaskUser, autotaskTicket, note);
			}

			logger.info(format("[autotask] onNoteAdded - updated workId=%d, noteId=%d, successful=%b", workId, noteId, isSuccess));
		} else {
			Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));

			WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
			Note note =
				workNoteService.findNoteById(
						(Long) checkNotNull(eventArguments.get(IntegrationEvent.NOTE_ID))
				);
			User creator = userService.findUserById(note.getCreatorId());

			if (shouldSuppressWebhook(creator, webHook)) {
				return true;
			}

			Map<String, String> contextVariables = Maps.newHashMap();

			contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), note.getContent());

			isSuccess = runWebHook(work, webHook, contextVariables);
		}

		return isSuccess;
	}

	@Override
	public boolean onLabelAdded(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		Long workSubStatusTypeAssociationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WORK_SUBSTATUS_TYPE_ASSOCIATION_ID));

		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		WorkSubStatusTypeAssociation workLabelAssociation = workSubStatusService.getAssociation(workSubStatusTypeAssociationId);
		User creator = userService.findCreatorByWorkLabelAssociationId(workLabelAssociation.getId());

		if (shouldSuppressWebhook(creator, webHook)) {
			return true;
		}

		String labelCode = workLabelAssociation.getWorkSubStatusType().getCode();
		Set<String> negotiationLabelCodes = Sets.newHashSet(
				WorkSubStatusType.BONUS,
				WorkSubStatusType.BUDGET_INCREASE,
				WorkSubStatusType.EXPENSE_REIMBURSEMENT,
				WorkSubStatusType.RESCHEDULE_REQUEST
		);

		Map<String, String> contextVariables = Maps.newHashMap();

		contextVariables.put(WebHookDispatchField.LABEL_NAME.getFieldName(), workLabelAssociation.getWorkSubStatusType().getDescription());
		contextVariables.put(WebHookDispatchField.LABEL_ID.getFieldName(), String.valueOf(workLabelAssociation.getWorkSubStatusType().getId()));
		contextVariables.put(WebHookDispatchField.IS_NEGOTIATION.getFieldName(), String.valueOf(negotiationLabelCodes.contains(labelCode)));

		WorkNote note = workLabelAssociation.getTransitionNote();

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (note == null) ? StringUtils.EMPTY : StringUtils.defaultString(note.getContent()));

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onLabelRemoved(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		Long workSubStatusTypeAssociationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WORK_SUBSTATUS_TYPE_ASSOCIATION_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		WorkSubStatusTypeAssociation workLabelAssociation = workSubStatusService.getAssociation(workSubStatusTypeAssociationId);
		User modifier = userService.findModifierByWorkLabelAssociationId(workLabelAssociation.getId());

		if (shouldSuppressWebhook(modifier, webHook)) {
			return true;
		}

		String labelCode = workLabelAssociation.getWorkSubStatusType().getCode();
		Set<String> negotiationLabelCodes = Sets.newHashSet(
				WorkSubStatusType.BONUS,
				WorkSubStatusType.BUDGET_INCREASE,
				WorkSubStatusType.EXPENSE_REIMBURSEMENT,
				WorkSubStatusType.RESCHEDULE_REQUEST
		);

		Map<String, String> contextVariables = Maps.newHashMap();

		contextVariables.put(WebHookDispatchField.LABEL_NAME.getFieldName(), workLabelAssociation.getWorkSubStatusType().getDescription());
		contextVariables.put(WebHookDispatchField.LABEL_ID.getFieldName(), String.valueOf(workLabelAssociation.getWorkSubStatusType().getId()));
		contextVariables.put(WebHookDispatchField.IS_NEGOTIATION.getFieldName(), String.valueOf(negotiationLabelCodes.contains(labelCode)));

		WorkNote note = workLabelAssociation.getTransitionNote();

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (note == null) ? StringUtils.EMPTY : StringUtils.defaultString(note.getContent()));

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onWorkRescheduleRequested(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);
		AbstractWork work = checkNotNull(workService.findWork(workId));

		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));

		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		AbstractWorkNegotiation negotiation = workNegotiationService.findById(negotiationId);
		User creator = userService.findCreatorByWorkNegotiationId(negotiation.getId());

		if (shouldSuppressWebhook(creator, webHook)) {
			return true;
		}

		Map<String, String> contextVariables = Maps.newHashMap();

		if (negotiation instanceof ScheduleNegotiation) {
			contextVariables.putAll(getScheduleNegotiationFields(webHook, (ScheduleNegotiation) negotiation));
		}

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (negotiation.getNote() == null) ? StringUtils.EMPTY : negotiation.getNote().getContent());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onWorkRescheduleApproved(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));

		Map<String, String> contextVariables = Maps.newHashMap();
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onWorkRescheduleDeclined(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));
		AbstractWorkNegotiation negotiation = workNegotiationService.findById(negotiationId);

		Map<String, String> contextVariables = Maps.newHashMap();

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (negotiation.getDeclineNote() == null) ? StringUtils.EMPTY : negotiation.getDeclineNote().getContent());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onBudgetIncreaseAdded(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));
		AbstractWorkNegotiation negotiation = workNegotiationService.findById(negotiationId);

		Map<String, String> contextVariables = Maps.newHashMap();

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (negotiation.getNote() == null) ? StringUtils.EMPTY : negotiation.getNote().getContent());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onBudgetIncreaseRequested(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));
		AbstractWorkNegotiation negotiation = workNegotiationService.findById(negotiationId);

		Map<String, String> contextVariables = Maps.newHashMap();

		if (negotiation instanceof WorkBudgetNegotiation && ((WorkBudgetNegotiation) negotiation).getFullPricingStrategy() != null) {
			FullPricingStrategy fullPricingStrategy = ((WorkBudgetNegotiation) negotiation).getFullPricingStrategy();
			contextVariables.putAll(getPriceNegotiationFields(fullPricingStrategy));
		}

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (negotiation.getNote() == null) ? StringUtils.EMPTY : negotiation.getNote().getContent());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onBudgetIncreaseApproved(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));

		Map<String, String> contextVariables = Maps.newHashMap();
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onBudgetIncreaseDeclined(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));
		AbstractWorkNegotiation negotiation = workNegotiationService.findById(negotiationId);

		Map<String, String> contextVariables = Maps.newHashMap();

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (negotiation.getDeclineNote() == null) ? StringUtils.EMPTY : negotiation.getDeclineNote().getContent());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onExpenseReimbursementAdded(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));
		AbstractWorkNegotiation negotiation = workNegotiationService.findById(negotiationId);

		Map<String, String> contextVariables = Maps.newHashMap();

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (negotiation.getNote() == null) ? StringUtils.EMPTY : negotiation.getNote().getContent());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onExpenseReimbursementRequested(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));
		AbstractWorkNegotiation negotiation = workNegotiationService.findById(negotiationId);

		Map<String, String> contextVariables = Maps.newHashMap();

		if (negotiation instanceof WorkExpenseNegotiation &&
				((WorkExpenseNegotiation) negotiation).getFullPricingStrategy().getAdditionalExpenses() != null) {
			// subtract the already approved expenses to get the requested amount
			BigDecimal requestedExpenses = ((WorkExpenseNegotiation) negotiation).getFullPricingStrategy().getAdditionalExpenses().subtract(
					work.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses());
			contextVariables.put(WebHookDispatchField.AMOUNT.getFieldName(), requestedExpenses.toPlainString());
		}

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (negotiation.getNote() == null) ? StringUtils.EMPTY : negotiation.getNote().getContent());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onExpenseReimbursementApproved(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));
		BigDecimal amount = (BigDecimal) checkNotNull(eventArguments.get(IntegrationEvent.AMOUNT));

		Map<String, String> contextVariables = Maps.newHashMap();

		contextVariables.put(WebHookDispatchField.AMOUNT.getFieldName(), amount.toPlainString());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onExpenseReimbursementDeclined(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));
		AbstractWorkNegotiation negotiation = workNegotiationService.findById(negotiationId);

		Map<String, String> contextVariables = Maps.newHashMap();

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (negotiation.getDeclineNote() == null) ? StringUtils.EMPTY : negotiation.getDeclineNote().getContent());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onBonusAdded(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));
		AbstractWorkNegotiation negotiation = workNegotiationService.findById(negotiationId);

		Map<String, String> contextVariables = Maps.newHashMap();

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (negotiation.getNote() == null) ? StringUtils.EMPTY : negotiation.getNote().getContent());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onBonusRequested(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));
		AbstractWorkNegotiation negotiation = workNegotiationService.findById(negotiationId);

		Map<String, String> contextVariables = Maps.newHashMap();

		if (negotiation instanceof WorkBonusNegotiation && ((WorkBonusNegotiation) negotiation).getFullPricingStrategy().getBonus() != null) {
			// subtract the already approved bonuses to get the requested amount
			BigDecimal requestedBonus = ((WorkBonusNegotiation) negotiation).getFullPricingStrategy().getBonus().subtract(
					work.getPricingStrategy().getFullPricingStrategy().getBonus());
			contextVariables.put(WebHookDispatchField.AMOUNT.getFieldName(), requestedBonus.toPlainString());
		}

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (negotiation.getNote() == null) ? StringUtils.EMPTY : negotiation.getNote().getContent());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onBonusApproved(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));
		BigDecimal amount = (BigDecimal) checkNotNull(eventArguments.get(IntegrationEvent.AMOUNT));

		Map<String, String> contextVariables = Maps.newHashMap();

		contextVariables.put(WebHookDispatchField.AMOUNT.getFieldName(), amount.toPlainString());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onBonusDeclined(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));
		AbstractWorkNegotiation negotiation = workNegotiationService.findById(negotiationId);

		Map<String, String> contextVariables = Maps.newHashMap();

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (negotiation.getDeclineNote() == null) ? StringUtils.EMPTY : negotiation.getDeclineNote().getContent());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onWorkNegotiationRequested(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));
		WorkNegotiation negotiation = (WorkNegotiation) workNegotiationService.findById(negotiationId);

		Map<String, String> contextVariables = Maps.newHashMap();

		if (negotiation.isPriceNegotiation()) {
			contextVariables.putAll(getPriceNegotiationFields(negotiation.getFullPricingStrategy()));
		}

		if (negotiation.isScheduleNegotiation()) {
			contextVariables.putAll(getScheduleNegotiationFields(webHook, negotiation));
		}

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (negotiation.getNote() == null) ? StringUtils.EMPTY : negotiation.getNote().getContent());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onWorkNegotiationApproved(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));

		Map<String, String> contextVariables = Maps.newHashMap();
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	@Override
	public boolean onWorkNegotiationDeclined(Long workId, Map<String, Object> eventArguments) {
		Assert.notNull(workId);

		AbstractWork work = checkNotNull(workService.findWork(workId));
		Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
		WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
		Long negotiationId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.NEGOTIATION_ID));
		AbstractWorkNegotiation negotiation = workNegotiationService.findById(negotiationId);

		Map<String, String> contextVariables = Maps.newHashMap();

		contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (negotiation.getDeclineNote() == null) ? StringUtils.EMPTY : negotiation.getDeclineNote().getContent());
		contextVariables.put(WebHookDispatchField.NEGOTIATION_ID.getFieldName(), negotiationId.toString());

		return runWebHook(work, webHook, contextVariables);
	}

	private boolean doCheckInOut(Long workId, Map<String, Object> eventArguments, boolean isCheckIn) {
		Assert.notNull(workId);

		AbstractWork work = workService.findWork(workId);
		Boolean isAutotask = (Boolean) checkNotNull(eventArguments.get(IntegrationEvent.IS_AUTOTASK));
		WorkResource resource = checkNotNull(workService.findActiveWorkResource(workId));
		boolean isSuccess = false;

		if (isAutotask) {
			AutotaskUser autotaskUser = getAutotaskUser(work.getBuyer().getId());
			Map<String, AutotaskUserCustomFieldsPreference> preferenceMap = getAutotaskCustomUserPreference(autotaskUser.getId());
			AutotaskTicket autotaskTicket = getAutotaskTicketByUserAndWork(autotaskUser, workId);

			if (preferenceMap == null) {
				logger.error(format("[autotask] onCheckIn - Autotask userId=%d preference not found, workId=%d", autotaskUser.getId(), workId));
				return isSuccess;
			}

			isSuccess = autotaskIntegrationService.updateTicketToCheckInOut(autotaskUser, autotaskTicket, resource, preferenceMap);
			logger.info(format("[autotask] onCheckIn - updated workId=%d, successful=%b", workId, isSuccess));
		} else {
			Long webHookId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.WEBHOOK_ID));
			Long timeTrackingId = (Long) checkNotNull(eventArguments.get(IntegrationEvent.TIME_TRACKING_ID));
			WebHook webHook = webHookIntegrationService.getWebHook(webHookId).get();
			WorkResourceTimeTracking timeTracking = checkNotNull(workResourceService.findTimeTrackingEntryById(timeTrackingId));
			Map<String, String> contextVariables = Maps.newHashMap();

			contextVariables.put(WebHookDispatchField.CHECK_IN_OUT_ID.getFieldName(), String.valueOf(timeTrackingId));
			contextVariables.put(WebHookDispatchField.CHECKED_IN_ON.getFieldName(), webHook.getWebHookClient().formatDate(timeTracking.getCheckedInOn()));

			if (!isCheckIn) {
				contextVariables.put(WebHookDispatchField.CHECKED_OUT_ON.getFieldName(), webHook.getWebHookClient().formatDate(timeTracking.getCheckedOutOn()));
				contextVariables.put(WebHookDispatchField.NOTE.getFieldName(), (timeTracking.getNote() == null) ? StringUtils.EMPTY : timeTracking.getNote().getContent());
			}

			isSuccess = runWebHook(work, webHook, contextVariables);
		}

		return isSuccess;
	}

	/**
	 * @param userId
	 * @return
	 */
	private AutotaskUser getAutotaskUser(Long userId) {
		Optional<User> apiUserOpt = registrationService.getApiUserByUserId(userId);
		checkState(apiUserOpt.isPresent());
		Optional<AutotaskUser> autotaskUserOpt = autotaskIntegrationService.findAutotaskUserByUserId(apiUserOpt.get().getId());
		checkState(autotaskUserOpt.isPresent());
		return autotaskUserOpt.get();
	}

	private Map<String, AutotaskUserCustomFieldsPreference> getAutotaskCustomUserPreference(Long autotaskUserId) {
		Optional<List<AutotaskUserCustomFieldsPreference>> optPreferences = autotaskUserCustomFieldsPreferenceDAO.findAllPreferencesByAutotaskUser(autotaskUserId);
		Map<String, AutotaskUserCustomFieldsPreference> preferenceMap = new HashMap<>();

		if (optPreferences.isPresent()) {
			for (AutotaskUserCustomFieldsPreference preference : optPreferences.get()) {
				preferenceMap.put(preference.getIntegrationCustomField().getCode(), preference);
			}
			return preferenceMap;
		}

		return null;
	}

	private AutotaskTicket getAutotaskTicketByUserAndWork(AutotaskUser user, Long workId) {
		Optional<AutotaskTicket> ticketOpt = autotaskIntegrationService.findAutotaskTicketByWorkId(user, workId);
		checkState(ticketOpt.isPresent());
		return ticketOpt.get();
	}

	@VisibleForTesting
	protected boolean shouldSuppressWebhook(Map<String, Object> eventArguments, WebHook webhook) {
		final Boolean isApiTriggered = (Boolean) eventArguments.get(IntegrationEvent.IS_API_TRIGGERED);
		final boolean toggle =
				featureEntitlementService.hasFeatureToggle(securityContext.getCurrentUserId(), "webhook.suppressapievents");
		final boolean webhookClientSuppress =
				webhook.getWebHookClient() != null && webhook.getWebHookClient().isSuppressApiEvents();
		boolean result = false;

		if (isApiTriggered != null && isApiTriggered) {
			if (toggle) {
				result = webhook.suppressApiEvents();
			} else {
				result = webhookClientSuppress;
			}
		}

		if (result) {
			logger.info(String.format(
					"Suppress webhook: [id: %s, apiTriggered: %s, featureToggle: %s, webhookClientToggle: %s, webhookToggle: %s]",
					webhook.getId(), isApiTriggered, toggle, webhookClientSuppress, webhook.isSuppressApiEvents()));
			metricFacade.meter("suppress." + webhook.getIntegrationEventType().getCode()).mark();
		}

		return result;
	}

	@VisibleForTesting
	protected boolean shouldSuppressWebhook(final User user, final WebHook webhook) {
		final Boolean isApiTriggered = user.isApiEnabled();
		final boolean toggle =
				featureEntitlementService.hasFeatureToggle(authenticationService.getCurrentUserWithFallback().getId(), "webhook.suppressapievents");
		final boolean webhookClientSuppress =
				webhook.getWebHookClient() != null && webhook.getWebHookClient().isSuppressApiEvents();
		boolean result = false;

		if (isApiTriggered) {
			if (toggle) {
				result = webhook.suppressApiEvents();
			} else {
				result = webhookClientSuppress;
			}
		}

		if (result) {
			logger.info(String.format(
					"Suppress webhook: [id: %s, apiTriggered: %s, featureToggle: %s, webhookClientToggle: %s, webhookToggle: %s]",
					webhook.getId(), isApiTriggered, toggle, webhookClientSuppress, webhook.isSuppressApiEvents()));
			metricFacade.meter("suppress." + webhook.getIntegrationEventType().getCode()).mark();
		}

		return result;
	}

	private Map<String, String> getPriceNegotiationFields(final FullPricingStrategy fullPricingStrategy) {
		Map<String, String> keyValuePairs = Maps.newHashMap();

		if (fullPricingStrategy.getPricingStrategyType() == PricingStrategyType.FLAT) {
			keyValuePairs.put(WebHookDispatchField.PROPOSED_FLAT_PRICE.getFieldName(), fullPricingStrategy.getFlatPrice().toPlainString());
		} else if (fullPricingStrategy.getPricingStrategyType() == PricingStrategyType.PER_HOUR) {
			keyValuePairs.put(WebHookDispatchField.PROPOSED_MAX_INITIAL_HOURS.getFieldName(), fullPricingStrategy.getMaxNumberOfHours().toPlainString());
		} else if (fullPricingStrategy.getPricingStrategyType() == PricingStrategyType.PER_UNIT) {
			keyValuePairs.put(WebHookDispatchField.PROPOSED_MAX_UNITS.getFieldName(), fullPricingStrategy.getMaxNumberOfUnits().toPlainString());
		} else if (fullPricingStrategy.getPricingStrategyType() == PricingStrategyType.BLENDED_PER_HOUR) {
			keyValuePairs.put(WebHookDispatchField.PROPOSED_MAX_INITIAL_HOURS.getFieldName(), fullPricingStrategy.getInitialNumberOfHours().toPlainString());
			keyValuePairs.put(WebHookDispatchField.PROPOSED_MAX_ADDITIONAL_HOURS.getFieldName(), fullPricingStrategy.getMaxBlendedNumberOfHours().toPlainString());
		}

		if (fullPricingStrategy.getAdditionalExpenses() != null) {
			keyValuePairs.put(WebHookDispatchField.PROPOSED_EXPENSE_REIMBURSEMENT_AMOUNT.getFieldName(), fullPricingStrategy.getAdditionalExpenses().toPlainString());
		}

		return keyValuePairs;
	}

	private Map<String, String> getScheduleNegotiationFields(final WebHook webHook, final ScheduleNegotiation negotiation) {
		Map<String, String> keyValuePairs = Maps.newHashMap();

		keyValuePairs.put(WebHookDispatchField.PROPOSED_START_DATE_TIME.getFieldName(),
			webHook.getWebHookClient().formatDate(negotiation.getScheduleFrom()));

		if (negotiation.getScheduleRangeFlag()) {
			keyValuePairs.put(WebHookDispatchField.PROPOSED_END_DATE_TIME.getFieldName(),
				webHook.getWebHookClient().formatDate(negotiation.getScheduleThrough()));
		}

		return keyValuePairs;
	}
}
