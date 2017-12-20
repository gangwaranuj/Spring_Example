package com.workmarket.service.business.integration.hooks.webhook;

import java.util.ArrayList;
import java.util.List;

public class WebHookInvocationContext {
  private List<String> webhookInvocationUuids;

  public WebHookInvocationContext() {
    this.webhookInvocationUuids = new ArrayList<>();
  }

  public List<String> getWebhookInvocationUuids() {
    return webhookInvocationUuids;
  }

  public void setWebhookInvocationUuids(final List<String> webhookInvocationUuids) {
    this.webhookInvocationUuids = webhookInvocationUuids;
  }

  public void add(final String uuid) {
    if (!webhookInvocationUuids.contains(uuid)) {
      webhookInvocationUuids.add(uuid);
    }
  }

  public void remove(final String uuid) {
    webhookInvocationUuids.remove(uuid);
  }

  public void clear() {
    webhookInvocationUuids.clear();
  }
}
