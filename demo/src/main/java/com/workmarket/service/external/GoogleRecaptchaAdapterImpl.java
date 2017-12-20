package com.workmarket.service.external;


import com.workmarket.service.external.vo.GoogleRecaptchaResponse;
import com.workmarket.service.infra.http.HttpRequestUtilCommand;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class GoogleRecaptchaAdapterImpl implements GoogleRecaptchaAdapter{

	private static final String CONTENT_TYPE = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";
	private static final Log logger = LogFactory.getLog(GoogleRecaptchaAdapterImpl.class);

	@Value("${google.recaptcha.secret.key}")
	private String GOOGLE_RECAPTCHA_SECRET_KEY;

	@Value("${google.recaptcha.host}")
	private String HOST;

	@Value("${google.recaptcha.uri}")
	private String URI;

	@Value("${google.recaptcha.timeout.connection}")
	private long CONNECTION_TIMEOUT;

	@Value("${google.recaptcha.timeout.socket}")
	private long SOCKET_TIMEOUT;

	private String queryString;

	@Override
	public GoogleRecaptchaResponse verify(final String userResponse) {

		GoogleRecaptchaResponse response = null;
		try {
			queryString = format("secret=%s&response=%s", GOOGLE_RECAPTCHA_SECRET_KEY, userResponse);
			response = new HttpRequestUtilCommand()
				.by(HttpMethod.POST)
				.toHost(HOST)
				.andUri(URI)
				.withQueryString(queryString)
				.usingHeaders(CollectionUtilities.newStringMap(CONTENT_TYPE, APPLICATION_JSON))
				.withConnectionTimeout(CONNECTION_TIMEOUT)
				.withSocketTimeout(SOCKET_TIMEOUT)
				.callAndReturn(GoogleRecaptchaResponse.class);

		} catch (Exception e) {
			logger.error(format("Unable to verify user response for reCAPTCHA: %s", userResponse), e);
		}
		return response;
	}
}
