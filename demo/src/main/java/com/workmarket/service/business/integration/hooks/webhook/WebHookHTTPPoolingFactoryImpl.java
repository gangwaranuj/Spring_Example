package com.workmarket.service.business.integration.hooks.webhook;

import com.google.api.client.repackaged.com.google.common.annotations.VisibleForTesting;
import com.google.api.client.util.Maps;
import com.google.common.base.Optional;

import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.customfield.SavedWorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroupAssociation;
import com.workmarket.domains.model.integration.IntegrationEventType;
import com.workmarket.domains.model.integration.webhook.SalesforceWebHookClient;
import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.domains.model.integration.webhook.WebHookHeader;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.feature.vo.FeatureToggleAndStatus;
import com.workmarket.helpers.WMCallable;
import com.workmarket.id.IdGenerator;
import com.workmarket.integration.webhook.RestClient;
import com.workmarket.integration.webhook.WebHookDispatchField;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.dto.PaymentSummaryDTO;
import com.workmarket.service.business.dto.integration.ParsedWebHookDTO;
import com.workmarket.service.business.pay.PaymentSummaryService;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.security.SecurityContext;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.StringUtilities;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.xpath.operations.Bool;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;

import javax.annotation.PostConstruct;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class WebHookHTTPPoolingFactoryImpl implements WebHookHTTPPoolingFactory {

	// Hystrix config
	private static final String COMMAND_GROUP = "webhook";
	private static final int TIMEOUT_IN_MILLIS = 60000; // 1 minute
	private static final String THREAD_POOL_KEY_FORMAT = "threadpool-companyid-%s";

	@Autowired RestClient restClient;
	@Autowired CustomFieldService customFieldService;
	@Autowired PaymentSummaryService paymentSummaryService;
	@Autowired WorkService workService;
	@Autowired AddressService addressService;
	@Autowired WorkNegotiationService workNegotiationService;
	@Autowired IdGenerator idGenerator;
	@Autowired SecurityContext securityContext;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private WebHookIntegrationService webHookIntegrationService;
	@Autowired FeatureEntitlementService featureEntitlementService;
	@Autowired AuthenticationService authenticationService;

	private WMMetricRegistryFacade metricFacade;

	private static final Logger logger = LoggerFactory.getLogger(WebHookHTTPPoolingFactoryImpl.class);

	@PostConstruct
	void init() {
		metricFacade = new WMMetricRegistryFacade(metricRegistry, "webhook");
	}

	private String getFieldValue(AbstractWork work, WebHookDispatchField fieldType, Map<String, String> contextVariables, WebHook webHook) {
		// match general variables
		PaymentSummaryDTO paymentSummaryDTO;
		WorkResource workResource;
		// For WORK_NEGOTIATION_REQUEST events (which are applies / counteroffers) we also want to populate resource fields
		if (webHook.getIntegrationEventType().getCode().equals(IntegrationEventType.WORK_NEGOTIATION_REQUEST)) {
			Assert.notNull(contextVariables);
			Long negotiationId = Long.valueOf(contextVariables.get(WebHookDispatchField.NEGOTIATION_ID.getFieldName()));
			workResource = workService.findWorkResource(workNegotiationService.findById(negotiationId).getRequestedBy().getId(), work.getId());
		} else {
			workResource = workService.findActiveWorkResource(work.getId());
		}

		switch (fieldType) {
			case NOW:
				return webHook.getWebHookClient().formatDate(Calendar.getInstance());

			case OWNER_ID:
				if (work.getBuyer() != null) {
					return work.getBuyer().getUserNumber();
				}

				return StringUtils.EMPTY;

			case OWNER_EMAIL:
				if (work.getBuyer() != null) {
					return work.getBuyer().getEmail();
				}

				return StringUtils.EMPTY;

			case CLIENT_NAME:
				if (work.getClientCompany() != null && work.getClientCompany().getName() != null) {
					return work.getClientCompany().getName();
				}

				return StringUtils.EMPTY;

			case CLIENT_ID:
				if (work.getClientCompany() != null) {
					return work.getClientCompany().getId().toString();
				}

				return StringUtils.EMPTY;

			case CLIENT_CUSTOMER_ID:
				if (work.getClientCompany() != null && work.getClientCompany().getCustomerId() != null) {
					return work.getClientCompany().getCustomerId();
				}

				return StringUtils.EMPTY;

			case PROJECT_ID:
				if (work.getProject() != null) {
					return work.getProject().getId().toString();
				}

				return StringUtils.EMPTY;

			case PROJECT_NAME:
				if (work.getProject() != null) {
					return StringUtils.defaultString(work.getProject().getName());
				}

				return StringUtils.EMPTY;

			case ASSIGNMENT_ID:
				return StringUtils.defaultString(work.getWorkNumber());

			case STATUS:
				return StringUtils.defaultString(work.getWorkStatusType().getCode());

			case WORK_START_TIME:
				return webHook.getWebHookClient().formatDate(work.getScheduleFrom());

			case WORK_END_TIME:
				return webHook.getWebHookClient().formatDate(work.getScheduleThrough());

			case PRICING_TYPE:
				return StringUtils.defaultString(work.getPricingStrategyType().name());

			case PRICING_FLAT_PRICE:
				if (work.getPricingStrategy().getFullPricingStrategy().getFlatPrice() != null)
					return work.getPricingStrategy().getFullPricingStrategy().getFlatPrice().toPlainString();

				return StringUtils.EMPTY;

			case PRICING_PER_HOUR_PRICE:
				if (PricingStrategyType.BLENDED_PER_HOUR.equals(work.getPricingStrategy().getFullPricingStrategy().getPricingStrategyType())) {
						if (work.getPricingStrategy().getFullPricingStrategy().getInitialPerHourPrice() != null)
							return work.getPricingStrategy().getFullPricingStrategy().getInitialPerHourPrice().toPlainString();
				} else if(work.getPricingStrategy().getFullPricingStrategy().getPerHourPrice() != null) {
					return work.getPricingStrategy().getFullPricingStrategy().getPerHourPrice().toPlainString();
				}

				return StringUtils.EMPTY;

			case PRICING_MAX_NUMBER_OF_HOURS:
				if (PricingStrategyType.BLENDED_PER_HOUR.equals(work.getPricingStrategy().getFullPricingStrategy().getPricingStrategyType())) {
					if (work.getPricingStrategy().getFullPricingStrategy().getInitialNumberOfHours() != null)
						return work.getPricingStrategy().getFullPricingStrategy().getInitialNumberOfHours().toPlainString();
				} else if(work.getPricingStrategy().getFullPricingStrategy().getMaxNumberOfHours() != null) {
					return work.getPricingStrategy().getFullPricingStrategy().getMaxNumberOfHours().toPlainString();
				}

				return StringUtils.EMPTY;

			case PRICING_PER_UNIT_PRICE:
				if (work.getPricingStrategy().getFullPricingStrategy().getPerUnitPrice() != null)
					return work.getPricingStrategy().getFullPricingStrategy().getPerUnitPrice().toPlainString();

				return StringUtils.EMPTY;

			case PRICING_MAX_NUMBER_OF_UNITS:
				if (work.getPricingStrategy().getFullPricingStrategy().getMaxNumberOfUnits() != null)
					return work.getPricingStrategy().getFullPricingStrategy().getMaxNumberOfUnits().toPlainString();

				return StringUtils.EMPTY;

			case PRICING_ADDITIONAL_PER_HOUR_PRICE:
				if (work.getPricingStrategy().getFullPricingStrategy().getAdditionalPerHourPrice() != null)
					return work.getPricingStrategy().getFullPricingStrategy().getAdditionalPerHourPrice().toPlainString();

				return StringUtils.EMPTY;

			case PRICING_MAX_ADDITIONAL_NUMBER_OF_HOURS:
				if (work.getPricingStrategy().getFullPricingStrategy().getMaxBlendedNumberOfHours() != null)
					return work.getPricingStrategy().getFullPricingStrategy().getMaxBlendedNumberOfHours().toPlainString();

				return StringUtils.EMPTY;

			case RESOLUTION:
				return StringUtils.defaultString(work.getResolution());

			case HOURS_WORKED:
				paymentSummaryDTO = paymentSummaryService.generatePaymentSummaryForWork(work.getId());

				if (paymentSummaryDTO != null && paymentSummaryDTO.getHoursWorked() != null)
					return paymentSummaryDTO.getHoursWorked().toPlainString();

				return StringUtils.EMPTY;

			case UNITS_COMPLETED:
				paymentSummaryDTO = paymentSummaryService.generatePaymentSummaryForWork(work.getId());

				if (paymentSummaryDTO != null && paymentSummaryDTO.getUnitsProcessed() != null) {
					return paymentSummaryDTO.getUnitsProcessed().toPlainString();
				}

				return StringUtils.EMPTY;

			case EXPENSE_REIMBURSEMENT:
				return work.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses().toPlainString();

			case BONUS:
				return work.getPricingStrategy().getFullPricingStrategy().getBonus().toPlainString();

			case TOTAL_COST:
				paymentSummaryDTO = paymentSummaryService.generatePaymentSummaryForWork(work.getId());

				if (paymentSummaryDTO != null && paymentSummaryDTO.getTotalCost() != null)
					return paymentSummaryDTO.getTotalCost().toPlainString();

				return StringUtils.EMPTY;

			case OVERRIDE_PRICE:
				if (work.getPricingStrategy().getFullPricingStrategy().getOverridePrice() != null)
					return work.getPricingStrategy().getFullPricingStrategy().getOverridePrice().toPlainString();

				return StringUtils.EMPTY;

			case INVOICE_ID:
				if (work.getInvoice() != null)
					return work.getInvoice().getId().toString();

				return StringUtils.EMPTY;

			case STATEMENT_ID:
				if (work instanceof Work && ((Work) work).getStatementId() != null)
					return ((Work) work).getStatementId().toString();

				return StringUtils.EMPTY;

			case RESOURCE_ID:
				if (workResource != null) {
					return workResource.getUser().getUserNumber();
				}
				return StringUtils.EMPTY;

			case RESOURCE_UUID:
				if (workResource != null && workResource.getUser() != null ) {
					return StringUtils.defaultString(workResource.getUser().getUuid());
				}

				return StringUtils.EMPTY;

			case RESOURCE_FIRST_NAME:
				if (workResource != null) {
					return StringUtils.defaultString(workResource.getUser().getFirstName());
				}
				return StringUtils.EMPTY;

			case RESOURCE_LAST_NAME:
				if (workResource != null) {
					return StringUtils.defaultString(workResource.getUser().getLastName());
				}
				return StringUtils.EMPTY;

			case RESOURCE_OVERVIEW:
				if (workResource != null && workResource.getUser() != null && workResource.getUser().getProfile() != null) {
					return StringUtils.defaultString(workResource.getUser().getProfile().getOverview());
				}

				return StringUtils.EMPTY;

			case RESOURCE_COMPANY_ID:
				if (workResource != null && workResource.getUser() != null && workResource.getUser().getCompany() != null) {
					return StringUtils.defaultString(workResource.getUser().getCompany().getCompanyNumber());
				}

				return StringUtils.EMPTY;

			case RESOURCE_COMPANY_NAME:
				if (workResource != null && workResource.getUser() != null && workResource.getUser().getCompany() != null) {
					return StringUtils.defaultString(workResource.getUser().getCompany().getName());
				}
				
				return StringUtils.EMPTY;

			case RESOURCE_COMPANY_UUID:
				if (workResource != null && workResource.getUser() != null && workResource.getUser().getCompany() != null) {
					return StringUtils.defaultString(workResource.getUser().getCompany().getUuid());
				}

				return StringUtils.EMPTY;

			case RESOURCE_PHONE:
				if (workResource != null) {
					Profile profile = workResource.getUser().getProfile();
					if (profile.getWorkPhoneInternationalCode() != null) {
						return StringUtilities.formatPhoneNumber(profile.getWorkPhone(), profile.getWorkPhoneInternationalCode().getCallingCodeId(), profile.getWorkPhoneExtension());
					} else {
						return StringUtilities.formatPhoneNumber(profile.getWorkPhone(), null, profile.getWorkPhoneExtension());
					}
				}

				return StringUtils.EMPTY;

			case RESOURCE_MOBILE:
				if (workResource != null) {
					Profile profile = workResource.getUser().getProfile();
					if (profile.getMobilePhoneInternationalCode() != null) {
						return StringUtilities.formatPhoneNumber(profile.getMobilePhone(), profile.getMobilePhoneInternationalCode().getCallingCodeId(), null);
					} else {
						return StringUtilities.formatPhoneNumber(profile.getMobilePhone(), null, null);
					}
				}

				return StringUtils.EMPTY;

			case RESOURCE_ADDRESS1:
				if (workResource != null) {
					Address address = addressService.findById(workResource.getUser().getProfile().getAddressId());
					if (address != null) {
						return StringUtils.defaultString(address.getAddress1());
					}
				}

				return StringUtils.EMPTY;

			case RESOURCE_ADDRESS2:
				if (workResource != null) {
					Address address = addressService.findById(workResource.getUser().getProfile().getAddressId());
					if (address != null) {
						return StringUtils.defaultString(address.getAddress2());
					}
				}

				return StringUtils.EMPTY;

			case RESOURCE_CITY:
				if (workResource != null) {
					Address address = addressService.findById(workResource.getUser().getProfile().getAddressId());
					if (address != null) {
						return address.getCity();
					}
				}

				return StringUtils.EMPTY;

			case RESOURCE_STATE:
				if (workResource != null) {
					Address address = addressService.findById(workResource.getUser().getProfile().getAddressId());
					if (address != null) {
						return address.getState().getName();
					}
				}

				return StringUtils.EMPTY;

			case RESOURCE_POSTAL_CODE:
				if (workResource != null) {
					Address address = addressService.findById(workResource.getUser().getProfile().getAddressId());
					if (address != null) {
						return address.getPostalCode();
					}
				}

				return StringUtils.EMPTY;

			case RESOURCE_COUNTRY:
				if (workResource != null) {
					Address address = addressService.findById(workResource.getUser().getProfile().getAddressId());
					if (address != null) {
						return address.getCountry().getName();
					}
				}

				return StringUtils.EMPTY;

			case RESOURCE_EMAIL:
				if (workResource != null) {
					return workResource.getUser().getEmail();
				}

				return StringUtils.EMPTY;

			case SUPPORT_CONTACT_FIRST_NAME:
				if (work.getBuyerSupportUser() != null) {
					  return StringUtils.defaultString(work.getBuyerSupportUser().getFirstName());
				}
				return StringUtils.EMPTY;

			case SUPPORT_CONTACT_LAST_NAME:
				if (work.getBuyerSupportUser() != null) {
					return StringUtils.defaultString(work.getBuyerSupportUser().getLastName());
				}
				return StringUtils.EMPTY;

			case SUPPORT_CONTACT_EMAIL:
				if (work.getBuyerSupportUser() != null) {
					return StringUtils.defaultString(work.getBuyerSupportUser().getEmail());
				}
				return StringUtils.EMPTY;
		}

		// match event specific variables
		if (contextVariables != null && contextVariables.containsKey(fieldType.getFieldName())) {
			return contextVariables.get(fieldType.getFieldName());
		}

		return StringUtils.EMPTY;
	}

	private String parseVariable(AbstractWork work, WebHook webHook, Map<String, String> contextVariables, Map<Long, String> customFieldMap, String variable) {
		if (variable.startsWith(WebHook.CUSTOM_FIELD_PREFIX)) {
			Long customFieldId;

			try {
				customFieldId = Long.valueOf(variable.substring(WebHook.CUSTOM_FIELD_PREFIX.length()));
			} catch (NumberFormatException ex) {
				return StringUtils.EMPTY;
			}

			if (customFieldMap.containsKey(customFieldId)) {
				return customFieldMap.get(customFieldId);
			}
		} else {
			WebHookDispatchField field = checkNotNull(WebHookDispatchField.findByFieldName(variable));

			if (field.getEligibleEvents().contains(webHook.getIntegrationEventType().getCode())) {
				return getFieldValue(work, field, contextVariables, webHook);
			}
		}

		return StringUtils.EMPTY;
	}

	private Map<Long, String> buildCustomFieldMap(AbstractWork work) {
		Map<Long, String> customFieldMap = new HashMap<>();

		for (WorkCustomFieldGroupAssociation workCustomFieldGroupAssociation : customFieldService.findAllByWork(work.getId())) {
			for (SavedWorkCustomField savedWorkCustomField : workCustomFieldGroupAssociation.getSavedWorkCustomFields()) {
				customFieldMap.put(savedWorkCustomField.getWorkCustomField().getId(), savedWorkCustomField.getValue());
			}
		}

		return customFieldMap;
	}

	@Override
	public Optional<ParsedWebHookDTO> buildHook(AbstractWork work, WebHook webHook, Map<String, String> contextVariables) {
		return buildHook(work, webHook, contextVariables, new HashMap<String, String>());
	}


	@Override
	public Optional<ParsedWebHookDTO> buildHook(AbstractWork work, WebHook webHook, Map<String, String> contextVariables, Map<String, String> authenticationHeaders) {
		ParsedWebHookDTO parsedWebHookDTO = new ParsedWebHookDTO();
		Map<Long, String> customFieldMap = buildCustomFieldMap(work);
		final Map<String, String> variableMap = Maps.newHashMap();

		try {
			final String body = webHook.getBody();
			final Matcher matcher = WebHook.VARIABLE_PATTERN.matcher(body);

			while (matcher.find()) {
				String variable = webHook.getBody().substring(matcher.start() + 2, matcher.end() - 1);
				String maybeValue = parseVariable(work, webHook, contextVariables, customFieldMap, variable);

				if (WebHook.ContentType.JSON.equals(webHook.getContentType())) {
					maybeValue = JSONObject.quote(maybeValue);
					maybeValue = maybeValue.substring(1, maybeValue.length() - 1);
				} else if (WebHook.ContentType.FORM_ENCODED.equals(webHook.getContentType())) {
					maybeValue = URLEncoder.encode(maybeValue, "UTF-8");
				}

				final String value = maybeValue;

				if(maybeValue.length() < 1024) {
					logger.debug("WEBHOOK[" + webHook.getIntegrationEventType().getCode() + "] " +
						"- Applying variable to webhook body: " + matcher.group() + "[" + value + "]");
				}

				variableMap.put(variable, maybeValue);
			}

			final Map<String, String> actualVariableMap = featureEntitlementService.getFeatureToggleForCurrentUser("webhook.relay")
				.map(new Func1<FeatureToggleAndStatus, Boolean>() {
					@Override
					public Boolean call(FeatureToggleAndStatus featureToggleAndStatus) {
						return featureToggleAndStatus.getStatus().getSuccess() &&
							featureToggleAndStatus.getFeatureToggle() != null &&
							BooleanUtils.toBoolean(featureToggleAndStatus.getFeatureToggle().getValue());
					}
				})
				.map(new Func1<Boolean, Map<String, String>>() {
					@Override
					public Map<String, String> call(Boolean toggleIsOn) {
						if(toggleIsOn) {
							Map<String, String> redactedVariableMap = Maps.newHashMap();
							redactedVariableMap.putAll(variableMap);
							redactedVariableMap.remove(WebHookDispatchField.FILE_DATA.getFieldName());
							redactedVariableMap.remove(WebHookDispatchField.FILE_DATA_BASE_64.getFieldName());
							redactedVariableMap.remove(WebHookDispatchField.FILE_DATA_BYTE_LENGTH.getFieldName());
							redactedVariableMap.remove((WebHookDispatchField.FILE_DATA_RAW.getFieldName()));
						}
						return variableMap;
					}
				})
				.toBlocking().singleOrDefault(variableMap);

			// TODO - this is shit
			String resultingBody = body;
			for(String variable : actualVariableMap.keySet()) {
				resultingBody = resultingBody.replace("${" + variable + "}", actualVariableMap.get(variable));
			}

			parsedWebHookDTO.setBody(resultingBody);
		} catch (Exception ex) {
			logger.info(String.format("Error parsing body for webhook [id=%d]", webHook.getId()), ex);
			return Optional.absent();
		}

		try {
			String url = webHook.getUrl().toString();
			Matcher matcher = WebHook.VARIABLE_PATTERN.matcher(url);

			while (matcher.find()) {
				String variable = webHook.getUrl().toString().substring(matcher.start() + 2, matcher.end() - 1);
				String value = parseVariable(work, webHook, contextVariables, customFieldMap, variable);

				url = url.replace(matcher.group(), URLEncoder.encode(value, "UTF-8"));

				// A bug in Salesforce requires . to be URL encoded
				if (webHook.getWebHookClient() instanceof SalesforceWebHookClient) {
					url = url.replace(".", "%2E");
				}

				variableMap.put(variable, value);
			}

			parsedWebHookDTO.setVariables(variableMap);
			parsedWebHookDTO.setUri(new URI(url));
		} catch (Exception ex) {
			logger.info(String.format("Error parsing URL for webhook [id=%d]", webHook.getId()), ex);
			return Optional.absent();
		}

		try {
			Map<String, String> headers = new HashMap<>();

			headers.put(WebHook.CONTENT_TYPE, webHook.getContentType().getValue());

			for (WebHookHeader webHookHeader : webHook.getWebHookHeaders()) {
				String value = webHookHeader.getValue();
				Matcher matcher = WebHook.VARIABLE_PATTERN.matcher(value);

				while (matcher.find()) {
					String variable = webHookHeader.getValue().substring(matcher.start() + 2, matcher.end() - 1);

					value = value.replace(matcher.group(), StringUtilities.escapeQuotes(parseVariable(work, webHook, contextVariables, customFieldMap, variable)));
				}

				headers.put(webHookHeader.getName(), value);
			}

			// do not need to parse, these are added by authentication scheme
			headers.putAll(authenticationHeaders);

			parsedWebHookDTO.setHeaders(headers);
		} catch (Exception ex) {
			logger.info(String.format("Error parsing headers for webhook [id=%d]", webHook.getId()), ex);
			return Optional.absent();
		}

		return Optional.fromNullable(parsedWebHookDTO);
	}

	@Override
	public HttpStatus launchHook(WebHook webHook, ParsedWebHookDTO parsedWebHookDTO) {
		WebHook.MethodType method = webHook.getMethodType();
		HttpStatus status;

		metricFacade.meter("send.event." + webHook.getIntegrationEventType().getCode()).mark();

		final Request request = buildHttpRequest(method, parsedWebHookDTO);

		if (request != null) {
			status = runHystrixCommandAndReturnStatus(request, webHook, TIMEOUT_IN_MILLIS,
					new WMCallable<HttpResponse>(webRequestContextProvider) {
						@Override
						public HttpResponse apply() throws Exception {
							return request.execute().returnResponse();
						}
					});
		} else {
			status = HttpStatus.METHOD_NOT_ALLOWED;
		}

		if (status.series() == HttpStatus.Series.SUCCESSFUL) {
			logger.info("Successful status code [ " + status + " ] for " + webHook);
			metricFacade.meter("send.success." + webHook.getIntegrationEventType().getCode()).mark();
		} else {
			logger.info(String.format(
					"Webhook error status code [%s] WEBHOOK[%s] REQUEST[%s]", status, webHook, parsedWebHookDTO));
			metricFacade.meter("send.error." + webHook.getIntegrationEventType().getCode()).mark();
		}

		return status;
	}

	@VisibleForTesting
	protected HttpStatus runHystrixCommandAndReturnStatus(
			final Request request,
			final WebHook webHook,
			final int timeoutInMillis,
			final WMCallable<HttpResponse> callable) {
		try {
			final HttpResponse response = runCommand(
					buildSetter(timeoutInMillis, webHook),
					callable
			).onErrorResumeNext(new Func1<Throwable, Observable<? extends HttpResponse>>() {
				@Override
				public Observable<? extends HttpResponse> call(final Throwable throwable) {
					logger.error("Exception in webhook callout: " + throwable.getMessage(), throwable);
					if (throwable instanceof HystrixRuntimeException) {
						return Observable.error(throwable.getCause());
					}
					return Observable.error(throwable);
				}
			}).toBlocking().single();
			return HttpStatus.valueOf(response.getStatusLine().getStatusCode());
		} catch (final Exception e) {
			logger.error("Exception in webhook callout: " + e.getMessage(), e);
			if (e.getCause() instanceof TimeoutException) {
				return HttpStatus.REQUEST_TIMEOUT;
			} else {
				return HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} finally {
			if (request != null) {
				try {
					request.abort();
					logger.info("Force closed request " + request);
				} catch (final Exception e) {
					logger.error("Error aborting connection", e);
				}
			}
		}
	}

	private Observable<HttpResponse> runCommand(
			final HystrixCommand.Setter setter,
			final WMCallable<HttpResponse> caller) {
		return new HystrixCommand<HttpResponse>(setter) {
			@Override
			protected HttpResponse run() throws Exception {
				return caller.call();
			}
		}.toObservable();
	}

	private HystrixCommand.Setter buildSetter(final int timeoutInMillis, final WebHook webHook) {
		final Long webhookClientCompanyId = getClientCompanyId(webHook);
		return HystrixCommand.Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey(COMMAND_GROUP))
				.andCommandKey(com.netflix.hystrix.HystrixCommandKey.Factory.asKey(
						webHook.getIntegrationEventType().getCode()))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey( // separate thread pools per company
						String.format(THREAD_POOL_KEY_FORMAT, webhookClientCompanyId)))
				.andThreadPoolPropertiesDefaults(
						HystrixThreadPoolProperties.Setter()
								.withMaxQueueSize(10000)
								.withQueueSizeRejectionThreshold(9999)
								.withCoreSize(1))
				.andCommandPropertiesDefaults(
						HystrixCommandProperties.Setter()
								.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
								.withCircuitBreakerEnabled(false)
								.withExecutionTimeoutInMilliseconds(timeoutInMillis));
	}

	Long getClientCompanyId(final WebHook webHook) {
		if (webHook.getWebHookClient() != null && webHook.getWebHookClient().getCompany() != null) {
			return webHook.getWebHookClient().getCompany().getId();
		}

		final Optional<Long> clientCompanyId = webHookIntegrationService.getWebhookClientCompanyId(webHook.getId());

		if (clientCompanyId.isPresent()) {
			return clientCompanyId.get();
		}

		logger.error("Missing client company ID to 0L for webhook sending");
		return 0L;
	}

	@VisibleForTesting
	protected Request buildHttpRequest(final WebHook.MethodType method, final ParsedWebHookDTO parsedWebHookDTO) {
		Request request;
		switch (method) {
			case POST:
				request = Request.Post(parsedWebHookDTO.getUri());
				break;
			case PATCH:
				request = Request.Patch(parsedWebHookDTO.getUri());
				break;
			case PUT:
				request = Request.Put(parsedWebHookDTO.getUri());
				break;
			case DELETE:
				request = Request.Delete(parsedWebHookDTO.getUri());
				break;
			default:
				request = null;
				break;
		}

		if (request != null) {
			// Exceptions are thrown when a DELETE method has an enclosing body.
			if (!method.equals(WebHook.MethodType.DELETE)) {
				request.bodyString(parsedWebHookDTO.getBody(), contentType(parsedWebHookDTO.getHeaders()));
			}
			for (final Map.Entry<String, String> ent : parsedWebHookDTO.getHeaders().entrySet()) {
				request.setHeader(ent.getKey(), ent.getValue());
			}
		}

		return request;
	}

	@VisibleForTesting
	protected ContentType contentType(final Map<String, String> headers) {
		if (MapUtils.isNotEmpty(headers)) {
			for (final Map.Entry<String, String> ent : headers.entrySet()) {
				if ("content-type".equals(ent.getKey().toLowerCase())) {
					return ContentType.create(ent.getValue());
				}
			}
		}

		// Most webhook mime-types in prod are JSON, so let's just default here
		return ContentType.APPLICATION_JSON;
	}
}
