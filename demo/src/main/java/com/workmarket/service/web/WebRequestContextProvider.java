package com.workmarket.service.web;

import com.google.common.base.Optional;
import com.workmarket.common.core.RequestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class WebRequestContextProvider {
    private static final Log logger = LogFactory.getLog(WebRequestContextProvider.class);

    private static ThreadLocal<WebRequestContext> threadLocalStore = new ThreadLocal<>();

    public WebRequestContext getWebRequestContext() {
        return getWebRequestContext(null, null);
    }
    public WebRequestContext getWebRequestContext(final String requestId, final String tenantId) {
        WebRequestContext webRequestContext = threadLocalStore.get();
        if (webRequestContext == null || isBlank(webRequestContext.getRequestId())) {
            webRequestContext = new WebRequestContext();
            threadLocalStore.set(webRequestContext);
        }

        if (requestId != null) {
            webRequestContext.setRequestId(requestId);
        }
        if (tenantId != null) {
            webRequestContext.setTenant(tenantId);
        }
        updateThreadName(webRequestContext);
        return webRequestContext;
    }

    public void setWebRequestContext(WebRequestContext webRequestContext) {
        if (webRequestContext == null) {
            webRequestContext = new WebRequestContext();
        }
        updateThreadName(webRequestContext);
        threadLocalStore.set(webRequestContext);
    }

    public void setRequestContext(final RequestContext context) {
        final WebRequestContext container = getWebRequestContext();
        container.setRequestId(context.getRequestId());
        container.setUserUuid(context.getUserId());
        container.setCompanyUuid(context.getCompanyId());
        container.setClientIp(context.getOriginIp());
        container.setTenant(context.getTenant());
        container.setJwt(context.getJwt());
        container.setJwtClaims(context.getJwtClaims().isPresent() ? context.getJwtClaims().get() : null);
        container.setUserAgent(context.getUserAgent());
        setWebRequestContext(container);
    }

    public RequestContext getRequestContext() {
        final WebRequestContext container = getWebRequestContext();
        if (isBlank(container.getRequestId())) {
            // FIXME(drew): want IdGenerator but @Autowiring doesn't fill it, AND doesn't fail.  Magical DI Framework FTL!
            String requestId = UUID.randomUUID().toString();
            container.setRequestId(requestId);
        }
        if (isBlank(container.getTenant())) {
            container.setTenant("NOT_SET");
        }
        final RequestContext ctx = new RequestContext(container.getRequestId(), container.getTenant());

        ctx.setUserId(container.getUserUuid());
        ctx.setCompanyId(container.getCompanyUuid());
        ctx.setOriginIp(container.getClientIp());
        ctx.setOrigin("monolith");
        ctx.setJwt(container.getJwt());
        ctx.setJwtClaims(Optional.fromNullable(container.getJwtClaims()));
        ctx.setUserAgent(container.getUserAgent());
        return ctx;
    }

    /**
     * Put webRequestContextProvider into the given WebRequestContextAware entity
     * @param entity
     */
    public void inject(final Object entity) {
        if (entity instanceof WebRequestContextAware) {
            WebRequestContextAware webRequestContextAware = (WebRequestContextAware) entity;
            webRequestContextAware.setWebRequestContext(getWebRequestContext());
        }
    }

    /**
     * Pull the webRequestContext out of the given WebRequestContextAware entity
     * @param entity
     */
    public void extract(final Object entity) {
        if (entity instanceof WebRequestContextAware) {
            WebRequestContextAware webRequestContextAware = (WebRequestContextAware) entity;
            this.setWebRequestContext(webRequestContextAware.getWebRequestContext());
        }
        else {
            logger.warn("WebRequestContext not transferable - new tracing will commence");
        }
    }

    public void clear() {
        removeThreadName();
        threadLocalStore.remove();
    }

    private void updateThreadName(final WebRequestContext webRequestContext) {
        String threadName = Thread.currentThread().getName();
        if (threadName.contains("~WM-")) {
            threadName = threadName.substring(0, threadName.indexOf("~WM-"));
        }
        Thread.currentThread().setName(threadName + "~WM-" + webRequestContext.getRequestId());
    }

    private void removeThreadName() {
        String threadName = Thread.currentThread().getName();
        if (threadName.contains("~WM-")) {
            Thread.currentThread().setName(threadName.substring(0, threadName.indexOf("~WM-")));
        }
    }
}
