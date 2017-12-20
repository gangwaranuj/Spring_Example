package com.workmarket.service.infra.audit;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.service.business.integration.hooks.webhook.WebHookInvocationContext;
import com.workmarket.service.business.integration.hooks.webhook.WebHookInvocationProvider;
import com.workmarket.service.infra.security.SecurityContext;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.webhook.relay.WebhookRelayClient;
import com.workmarket.webhook.relay.gen.Message.SendRequest;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * This listener is called every time an entity is updated or inserted. It will update
 * <p/>
 * created_on
 * modified_on
 * created_by
 * modified_by
 * <p/>
 * filled based on current user and current time.
 */
@Component
public class AuditInterceptor extends EmptyInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(AuditInterceptor.class);

	private static final long serialVersionUID = 1L;
	@Autowired private SecurityContext securityContext;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private WebHookInvocationProvider webHookInvocationProvider;

	private WebhookRelayClient webhookRelayClient;

	@PostConstruct
	public void postConstruct() {
		webhookRelayClient = new WebhookRelayClient();
	}

	/**
	 * Prepend requestId and userUuid in an SQL comment to aid in tracking/tracing culprit queries
	 *
	 * @param sql
	 *
	 * @return sql with a comment at prefix e.g. -- requestId=123 userUuid=abc
	 */
	@Override
	public String onPrepareStatement(String sql) {

		final StringBuilder queryTrackingString = new StringBuilder();
		final String requestId = webRequestContextProvider.getWebRequestContext().getRequestId() != null ? webRequestContextProvider.getWebRequestContext().getRequestId() : "";
		final String userUuid = webRequestContextProvider.getWebRequestContext().getUserUuid() != null ? webRequestContextProvider.getWebRequestContext().getUserUuid() : "";
		final String companyUuid = webRequestContextProvider.getWebRequestContext().getCompanyUuid() != null ? webRequestContextProvider.getWebRequestContext().getCompanyUuid() : "";
		queryTrackingString
			.append("/* requestId=")
			.append(requestId.replaceAll("[\n\r]",""))
			.append(" userUuid=")
			.append(userUuid.replaceAll("[\n\r]",""))
			.append(" companyUuid=")
			.append(companyUuid.replaceAll("[\n\r]",""))
			.append("*/ ");

		return queryTrackingString.append(sql).toString();
	}

	@Override
	public void afterTransactionCompletion(Transaction tx) {
		final WebHookInvocationContext context = webHookInvocationProvider.getWebHookInvocationContext();
		if (CollectionUtils.isNotEmpty(context.getWebhookInvocationUuids())) {
			if (tx.wasCommitted()) { // guarantee webhook history has been commited before sending to the service
				sendWebhooksAsync(context.getWebhookInvocationUuids());
			}
			logger.info("Clearing webhook context.");
			context.clear(); // clear on error or success
		}
	}

	private void sendWebhooksAsync(final List<String> webhookInvocationUuid) {
		webhookRelayClient.sendAsync(
				SendRequest.newBuilder()
						.addAllWebhookInvocationUuid(webhookInvocationUuid)
						.build(),
				webRequestContextProvider.getRequestContext())
				.subscribeOn(Schedulers.io()) // return immediately as the request sends the requests async
				.subscribe(new Action1<String>() {
					@Override
					public void call(final String status) {
						logger.info("Successful webhook send to service: " + status);
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(final Throwable throwable) {
						logger.error("Error sending webhooks invocations UUIDs " + webhookInvocationUuid, throwable);
					}
				});
	}

	public boolean onFlushDirty(
			Object entity,
			Serializable id,
			Object[] currentState,
			Object[] previousState,
			String[] propertyNames,
			Type[] types) {

		boolean modified = false;
		if (entity.getClass().isAnnotationPresent(AuditChanges.class)) {
				for (int i = 0; i < propertyNames.length; i++) {
					if ("createdOn".equals(propertyNames[i]) && previousState != null) {
						currentState[i] = previousState[i];
					}
					if ("modifiedOn".equals(propertyNames[i])) {
						currentState[i] = Calendar.getInstance();
						modified = true;
					}
					if ("creatorId".equals(propertyNames[i]) && previousState != null) {
						currentState[i] = previousState[i];
					}
					if ("modifierId".equals(propertyNames[i])) {
						currentState[i] = securityContext.getCurrentUserId();
						modified = true;
					}
				}
		}

		return modified;
	}

	public boolean onSave(
			Object entity,
			Serializable id,
			Object[] state,
			String[] propertyNames,
			Type[] types) {
		boolean modified = false;
		if (entity.getClass().isAnnotationPresent(AuditChanges.class)) {
				for (int i = 0; i < propertyNames.length; i++) {
					if ("createdOn".equals(propertyNames[i])) {
						state[i] = Calendar.getInstance();
						modified = true;
					}
					if ("modifiedOn".equals(propertyNames[i])) {
						state[i] = Calendar.getInstance();
						modified = true;
					}
					if ("creatorId".equals(propertyNames[i])) {
						state[i] = securityContext.getCurrentUserId();
						modified = true;
					}
					if ("modifierId".equals(propertyNames[i])) {
						state[i] = securityContext.getCurrentUserId();
						modified = true;
					}
				}
		}

		return modified;
	}
}
