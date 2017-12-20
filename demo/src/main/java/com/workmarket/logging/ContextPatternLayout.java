package com.workmarket.logging;

import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This file was taken here: http://lifeinide.blogspot.ca/2011_06_01_archive.html
 *
 * @author l0co@wp.pl
 */
public class ContextPatternLayout extends PatternLayout {
	protected String host;

	protected String getUsername() {
		return "username";
	}

	protected String getHostname() {
		if (host == null) {
			try {
				InetAddress addr = InetAddress.getLocalHost();
				this.host = addr.getHostName();
			} catch (UnknownHostException e) {
				this.host = "localhost";
			}
		}

		return host;
	}

	@Override
	protected PatternParser createPatternParser(String pattern) {
		return new PatternParser(pattern) {
			@Override
			protected void finalizeConverter(char c) {
				PatternConverter pc = null;

				switch (c) {
					case 'X':
						pc = new PatternConverter() {
							@Override
							protected String convert(LoggingEvent event) {
								final WebRequestContextProvider context = new WebRequestContextProvider();
								final String requestId = StringUtils.defaultIfEmpty(context.getWebRequestContext().getRequestId(), "none");
								final String userId = StringUtils.defaultIfEmpty(context.getWebRequestContext().getUserUuid(), "none");
								final String companyId = StringUtils.defaultIfEmpty(String.valueOf(context.getWebRequestContext().getCompanyId()), "none");
								final String companyUuid = StringUtils.defaultIfEmpty(String.valueOf(context.getWebRequestContext().getCompanyUuid()), "none");
								return String.format("requestId=%s userUuid=%s companyId=%s companyUuid=%s", requestId, userId, companyId, companyUuid);
							}
						};
						break;
					case 'u':
						pc = new PatternConverter() {
							@Override
							protected String convert(LoggingEvent event) {
								return getUsername();
							}
						};
						break;

					case 'h':
						pc = new PatternConverter() {
							@Override
							protected String convert(LoggingEvent event) {
								return getHostname();
							}
						};
						break;
				}

				if (pc == null) {
					super.finalizeConverter(c);
				} else {
					addConverter(pc);
				}
			}
		};
	}
}
