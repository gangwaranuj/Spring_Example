package com.workmarket.api.helpers;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ianha on 4/10/15.
 */
@Service
public class HttpServletRequestAwareServiceImpl implements HttpServletRequestAwareService {
    @Override
    public String getQueryString() {
        return getRequest().getQueryString();
    }

    @Override
    public String getRequestUrl() {
        return getRequest().getRequestURL().toString();
    }

    @Override
    public String getServerPath() {
        return getRequestUrl().replace(getRequest().getRequestURI(), "");
    }

    @Override
    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
}
