package com.workmarket.web.filter;

import com.workmarket.service.locale.LocaleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.FilterChain;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LocaleFilterTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;

    @Mock private FilterChain filterChain;
    @Mock private LocaleService localeService;

    @InjectMocks private LocaleFilter localeFilter = spy(new LocaleFilter());

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        session = new MockHttpSession();

        request.setSession(session);
    }

    @Test
    public void set_to_passed_in_values() throws Exception {
        final String localeCode = "es_ES";
        final String formatCode = "CA";

        when(localeService.getValidLocaleCode(localeCode)).thenReturn(localeCode);
        when(localeService.getValidFormatCode(formatCode)).thenReturn(formatCode);
        request.setParameter(LocaleFilter.LOCALE_QUERY_NAME, localeCode);
        request.setParameter(LocaleFilter.FORMAT_QUERY_NAME, formatCode);
        localeFilter.doFilter(request, response, filterChain);
        assertSame("Locale should be passed in value", localeCode, session.getAttribute(LocaleFilter.LOCALE_SESSION_ID));
        assertSame("Format should be passed in value", formatCode, session.getAttribute(LocaleFilter.FORMAT_SESSION_ID));
    }

    @Test
    public void check_default_values_are_set() throws Exception {
        localeFilter.doFilter(request, response, filterChain);
        assertSame("Locale should be passed in value", LocaleFilter.DEFAULT_LOCALE, session.getAttribute(LocaleFilter.LOCALE_SESSION_ID));
        assertSame("Format should be passed in value", LocaleFilter.DEFAULT_FORMAT, session.getAttribute(LocaleFilter.FORMAT_SESSION_ID));
    }

    @Test
    public void set_to_invalid_values_keep_default() throws Exception {
        final String localeCode = "ex_EX";
        final String formatCode = "asd";

        when(localeService.getValidLocaleCode(localeCode)).thenReturn(LocaleFilter.DEFAULT_LOCALE);
        when(localeService.getValidFormatCode(formatCode)).thenReturn(LocaleFilter.DEFAULT_FORMAT);
        request.setParameter(LocaleFilter.LOCALE_QUERY_NAME, localeCode);
        request.setParameter(LocaleFilter.FORMAT_QUERY_NAME, formatCode);
        localeFilter.doFilter(request, response, filterChain);
        assertSame("Locale should be passed in value", LocaleFilter.DEFAULT_LOCALE, session.getAttribute(LocaleFilter.LOCALE_SESSION_ID));
        assertSame("Format should be passed in value", LocaleFilter.DEFAULT_FORMAT, session.getAttribute(LocaleFilter.FORMAT_SESSION_ID));
    }
}
