package com.workmarket.web.filter;

import com.google.common.annotations.VisibleForTesting;

import com.newrelic.api.agent.NewRelic;
import com.workmarket.auth.AuthenticationClient;
import com.workmarket.common.core.RequestContext;
import com.workmarket.id.IdGenerator;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import rx.Observable;
import rx.Subscriber;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component(value = "requestIdFilter")
public class RequestIdFilter implements Filter {
    private final Log logger = LogFactory.getLog(RequestIdFilter.class);

    @Autowired private WebRequestContextProvider webRequestContextProvider;
    @Autowired private IdGenerator idGenerator;
    @Autowired private AuthenticationService authenticationService;
    @Autowired private AuthenticationClient authClient;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @VisibleForTesting
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public void doFilter(
            final ServletRequest servletRequest,
            final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {
        try {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            final String reqId = httpServletRequest.getHeader(RequestContext.REQUEST_ID);
            final Observable<String> reqIdObs;
            final String rayId = httpServletRequest.getHeader("Cf-Ray");
            if (!isBlank(rayId)) {
                reqIdObs = Observable.just(rayId);
            } else if (!isBlank(reqId)) {
                reqIdObs = Observable.just(reqId);
            } else {
                reqIdObs = idGenerator.next();
            }
            reqIdObs.subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() { }

                @Override
                public void onError(final Throwable throwable) {
                    logger.error(throwable);
                }

                @Override
                public void onNext(final String requestId) {
                    final String tenant = httpServletRequest.getHeader(RequestContext.TENANT);
                    final WebRequestContext webRequestContext =
                        webRequestContextProvider.getWebRequestContext(requestId, tenant == null ? "NOT_SET" : tenant);
                    webRequestContext.setRequestStartTime(System.currentTimeMillis());
                    webRequestContext.setClientIp(String.format(
                        "RA:[%s] XFF:[%s] TCIP[%s]",
                        servletRequest.getRemoteAddr(),
                        httpServletRequest.getHeader("X-Forwarded-For"),
                        httpServletRequest.getHeader("True-Client-IP")));
                    webRequestContext.setUserAgent(httpServletRequest.getHeader("User-Agent"));
                    final String jwt = httpServletRequest.getHeader(RequestContext.JWT);
                    if (!isBlank(jwt)) {
                        webRequestContext.setJwt(jwt);
                        return;
                    }
                    final HttpSession httpSession = httpServletRequest.getSession(false);
                    if (httpSession == null) {
                        webRequestContext.setJwt(null);
                        return;
                    }
                    final RequestContext requestContext = webRequestContextProvider.getRequestContext();
                    final String jwtBySession = validate(httpSession.getId(), requestContext);
                    webRequestContext.setJwt(jwtBySession);
                }
            });
        } catch (final Exception e) {
            logger.error(e);
        } finally {
            filterChain.doFilter(servletRequest, servletResponse);
            NewRelic.addCustomParameter("requestId", webRequestContextProvider.getWebRequestContext().getRequestId());
            NewRelic.addCustomParameter("userUuid", webRequestContextProvider.getWebRequestContext().getUserUuid());
            NewRelic.addCustomParameter("companyId", webRequestContextProvider.getWebRequestContext().getCompanyId());
            NewRelic.addCustomParameter("companyUuid", webRequestContextProvider.getWebRequestContext().getCompanyUuid());
            final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            httpServletResponse.setHeader(RequestContext.REQUEST_ID, webRequestContextProvider.getRequestContext().getRequestId());
            logger.info("Request for " + ((HttpServletRequest) servletRequest).getRequestURI() + " finished");
            webRequestContextProvider.clear();
        }
    }

    @Override
    public void destroy() {
        webRequestContextProvider.clear();
    }

    private String validate(final String sessionId, final RequestContext requestContext ) {
        try {
            return authClient.validate(sessionId, requestContext).toBlocking().single().getJwt();
        } catch (final RuntimeException e) {
            throw new RuntimeException("Failed to redeem auth token for jwt", e);
        }
    }
}
