package com.workmarket.domains.model.integration.webhook.util;

import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.service.business.dto.integration.ParsedWebHookDTO;
import com.workmarket.webhook.relay.gen.Message.ContentType;
import com.workmarket.webhook.relay.gen.Message.Method;
import com.workmarket.webhook.relay.gen.Message.WebhookAsset;
import com.workmarket.webhook.relay.gen.Message.WebhookInvocation;

import org.apache.commons.lang.StringUtils;

public class WebHookInvocationBuilder {
  public static WebhookInvocation build(
      final ParsedWebHookDTO dto,
      final WebHook webhook,
      final RequestContext requestContext,
      final String assetUuid,
      final String assetMimeType,
      final String s3BucketName) {
    final WebhookInvocation.Builder builder = WebhookInvocation.newBuilder();

    if (StringUtils.isNotBlank(assetUuid)
        && StringUtils.isNotBlank(assetMimeType)
        && StringUtils.isNotBlank(s3BucketName)) {
      builder.addAsset(WebhookAsset.newBuilder()
          .setAssetUuid(assetUuid)
          .setAssetMimeType(assetMimeType)
          .setS3BucketName(s3BucketName));
    }

    return builder
        .setBody(dto.getBody())
        .setCompanyUuid(webhook.getWebHookClient().getCompany().getUuid())
        .setContentType(ContentType.valueOf(webhook.getContentType().name()))
        .setCreatedBy(requestContext.getUserId())
        .setMethod(Method.valueOf(webhook.getMethodType().name()))
        .setRequestId(requestContext.getRequestId())
        .setUrl(dto.getUri().toString())
        .setWebhookName(webhook.getIntegrationEventType().getCode())
        .setWebhookUuid(webhook.getUuid())
        .putAllParameter(dto.getVariables())
        .putAllHeader(dto.getHeaders())
        .build();
  }
}
