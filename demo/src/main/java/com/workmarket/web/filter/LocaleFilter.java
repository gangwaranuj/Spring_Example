package com.workmarket.web.filter;

import com.workmarket.service.locale.LocaleService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component(value = "localeFilter")
public class LocaleFilter implements Filter {
    public static final String LOCALE_SESSION_ID = "WM_LOCALE_CODE";
    public static final String FORMAT_SESSION_ID = "WM_FORMAT_CODE";
    public static final String LOCALE_QUERY_NAME = "lang";
    public static final String FORMAT_QUERY_NAME = "format";
    public static final String DEFAULT_LOCALE = "en_US";
    public static final String DEFAULT_FORMAT = "US";

    private final Log logger = LogFactory.getLog(LocaleFilter.class);

    @Autowired private LocaleService localeService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(
            final ServletRequest servletRequest,
            final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        if (request.getServletPath().equals("/health_test")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        try {
            final HttpSession session = request.getSession();

            final String localeCode = servletRequest.getParameter(LOCALE_QUERY_NAME);
            final String formatCode = servletRequest.getParameter(FORMAT_QUERY_NAME);

            if (localeCode != null) {
                session.setAttribute(LOCALE_SESSION_ID, localeService.getValidLocaleCode(localeCode));
            }

            if (session.getAttribute(LOCALE_SESSION_ID) == null) {
                session.setAttribute(LOCALE_SESSION_ID, DEFAULT_LOCALE);
            }

            if (formatCode != null) {
                session.setAttribute(FORMAT_SESSION_ID, localeService.getValidFormatCode(formatCode));
            }

            if (session.getAttribute(FORMAT_SESSION_ID) == null) {
                session.setAttribute(FORMAT_SESSION_ID, DEFAULT_FORMAT);
            }

        } catch (Exception e) {
            logger.error(e);
        } finally {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }
}
