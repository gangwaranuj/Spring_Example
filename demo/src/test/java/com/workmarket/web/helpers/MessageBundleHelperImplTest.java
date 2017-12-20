package com.workmarket.web.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageBundleHelperImplTest {

	@Mock private MessageSource messageSource;

	@InjectMocks MessageBundleHelperImpl messageBundleHelper;

	final private static String XSS_MESSAGE = "<script>alert(document.cookie);</script>";
	final private static String XSS_CLEAN_MESSAGE = "&lt;script&gt;alert(document.cookie);&lt;/script&gt;";

	@Test
	public void getMessage_escapesParamXSS() {
		when(messageSource.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenThrow(new NoSuchMessageException(""));
		String result = messageBundleHelper.getMessage(XSS_MESSAGE);

		assertEquals(XSS_CLEAN_MESSAGE, result);
	}

	@Test
	public void getMessage_escapesMessageXSS() {
		when(messageSource.getMessage(XSS_MESSAGE, new Object[] {}, null)).thenReturn(XSS_MESSAGE);
		when(messageSource.getMessage(XSS_CLEAN_MESSAGE, new Object[] {}, null)).thenReturn(XSS_CLEAN_MESSAGE);
		String result = messageBundleHelper.getMessage(XSS_MESSAGE);

		assertEquals(XSS_CLEAN_MESSAGE, result);
	}

	@Test
	public void getMessage_preventsDoubleEncodingParam() {
		when(messageSource.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenThrow(new NoSuchMessageException(""));
		String result = messageBundleHelper.getMessage(XSS_CLEAN_MESSAGE);

		assertEquals(XSS_CLEAN_MESSAGE, result);
	}

	@Test
	public void getMessage_preventsDoubleEncodingMessage() {
		when(messageSource.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn(XSS_CLEAN_MESSAGE);
		String result = messageBundleHelper.getMessage("");

		assertEquals(XSS_CLEAN_MESSAGE, result);
	}
}

