package com.workmarket.web.filter;

import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages.Status;
import com.workmarket.auth.gen.Messages.ValidationResponse;
import com.workmarket.common.core.RequestContext;
import com.workmarket.id.IdGenerator;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;

import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rx.Observable;
import rx.Subscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestIdFilterTest {
    public static final String ID = UUID.randomUUID().toString();

    @Mock
    WebRequestContextProvider webRequestContextProvider;
    @Mock IdGenerator idGenerator;
    @InjectMocks RequestIdFilter requestIdFilter = spy(new RequestIdFilter());

    @Mock HttpServletRequest servletRequest;
    @Mock HttpServletResponse servletResponse;
    @Mock FilterChain filterChain;
    @Mock AuthenticationService authenticationService;
    @Mock MockHttpSession httpSession;
    @Mock AuthenticationClient authenticationClient;

    @Before
    public void setup() {
        when(webRequestContextProvider.getWebRequestContext()).thenCallRealMethod();
        when(webRequestContextProvider.getWebRequestContext(any(String.class), any(String.class))).thenCallRealMethod();
        when(webRequestContextProvider.getRequestContext()).thenCallRealMethod();
        doCallRealMethod().when(webRequestContextProvider).setWebRequestContext(any(WebRequestContext.class));

        requestIdFilter.setIdGenerator(idGenerator);
        when(idGenerator.next()).thenReturn(
            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    subscriber.onNext(ID);
                    subscriber.onCompleted();
                }
            })
        );
        httpSession = new MockHttpSession(null, "123");
    }

    @Test
    public void setRequestId_withGuid_shouldSetIdInContext() throws Exception {
        WebRequestContext webRequestContext = webRequestContextProvider.getWebRequestContext();
        requestIdFilter.doFilter(servletRequest, servletResponse, filterChain);
        assertSame("Expect requestId to match generated requestId", ID, webRequestContext.getRequestId());
    }

    @Test
    public void setRequestId_withGuid_shouldListenToRayIdHeader() throws Exception {
        WebRequestContext webRequestContext = webRequestContextProvider.getWebRequestContext();
        String rayId = "ray-id";
        when(servletRequest.getHeader(eq(RequestContext.REQUEST_ID))).thenReturn(rayId);
        requestIdFilter.doFilter(servletRequest, servletResponse, filterChain);
        assertSame("Expect requestId to inherit from X-WM_REQUESTID header", rayId, webRequestContext.getRequestId());
    }

    @Test
    public void clear_onExitOfFilterChain_shouldClearContext() throws Exception {
        requestIdFilter.doFilter(servletRequest, servletResponse, filterChain);
        verify(webRequestContextProvider).clear();
    }

    @Test
    public void setJwtForValidSessionID() throws Exception {
        final WebRequestContext webRequestContext = webRequestContextProvider.getWebRequestContext();
        when(servletRequest.getSession(false)).thenReturn(httpSession);
        when(authenticationClient.validate(eq("123"), (RequestContext) anyObject())).thenReturn(
            Observable.just(ValidationResponse.newBuilder().setJwt("abc").setStatus(
                Status.newBuilder().setSuccess(true).build()).build()));
        requestIdFilter.doFilter(servletRequest, servletResponse, filterChain);
        assertEquals("abc", webRequestContext.getJwt());
    }

    @Test
    public void setJwtForInvalidSessionID() throws Exception {
        final WebRequestContext webRequestContext = webRequestContextProvider.getWebRequestContext();
        webRequestContext.setJwt("");
        when(servletRequest.getSession(false)).thenReturn(httpSession);
        when(authenticationClient.validate(eq("123"), (RequestContext) anyObject())).thenReturn(
            Observable.just(ValidationResponse.newBuilder().setStatus(
                Status.newBuilder().setSuccess(false).build()).build()));
        requestIdFilter.doFilter(servletRequest, servletResponse, filterChain);
        assertEquals("", webRequestContext.getJwt());
    }
}
