package com.workmarket.service.business.integration.hooks.webhook;

import org.springframework.stereotype.Component;

@Component
public class WebHookInvocationProvider {
  private static ThreadLocal<WebHookInvocationContext> threadLocalStore = new ThreadLocal<>();

  public WebHookInvocationContext getWebHookInvocationContext() {
    WebHookInvocationContext context = threadLocalStore.get();
    if (context == null) {
      context = new WebHookInvocationContext();
      threadLocalStore.set(context);
    }

    return context;
  }
}
