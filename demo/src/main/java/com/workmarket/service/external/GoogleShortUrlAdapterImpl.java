package com.workmarket.service.external;

import com.workmarket.service.external.vo.GoogleShortUrlResponse;
import com.workmarket.service.infra.http.HttpRequestUtilCommand;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

import static java.lang.String.format;

@Service
public class GoogleShortUrlAdapterImpl implements ShortUrlAdapter {

	@Value("${google.shorturl.secret}")
	private String GOOGLE_SECRET;

	@Value("${google.shorturl.host}")
	private String HOST;

	@Value("${google.shorturl.uri}")
	private String URI;

	@Value("${baseurl}")
	private String BASE_URL;

	@Value("${google.shorturl.timeout.connection}")
	private long connectionTimeout;

	@Value("${google.shorturl.timeout.socket}")
	private long socketTimeout;

	private String queryString;

	private static final String CONTENT_TYPE = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";

	private static final Log logger = LogFactory.getLog(GoogleShortUrlAdapterImpl.class);

	@Override
	public String getShortUrl(String longUrl) {
		String shortUrl = "";

		try {
			longUrl = getAbsoluteURL(longUrl);

			GoogleShortUrlResponse response = new HttpRequestUtilCommand()
					.by(HttpMethod.POST)
					.toHost(HOST)
					.andUri(URI)
					.withQueryString(queryString)
					.body(generateRequestBodyFrom(longUrl))
					.usingHeaders(getHeaders())
					.withConnectionTimeout(connectionTimeout)
					.withSocketTimeout(socketTimeout)
					.callAndReturn(GoogleShortUrlResponse.class);

			shortUrl = response.getShortUrl();
		} catch (Exception e) {
			logger.error(format("Unable to create Google short URL for url=%s", longUrl), e);
		}

		return shortUrl;
	}

	private String generateRequestBodyFrom(String longUrl) throws IOException {
		return new ObjectMapper().writeValueAsString(new Config(longUrl));
	}

	private String getAbsoluteURL(String url) {
		return BASE_URL + url;
	}

	private Map<String, String> getHeaders() {
		return CollectionUtilities.newStringMap(CONTENT_TYPE, APPLICATION_JSON);
	}

	private class Config {
		private String longUrl;

		public Config(String longUrl) {
			this.longUrl = longUrl;
		}

		public String getLongUrl() {
			return longUrl;
		}

		public void setLongUrl(String longUrl) {
			this.longUrl = longUrl;
		}
	}

	@PostConstruct
	private void init() {
		queryString = "key=" + GOOGLE_SECRET;
	}
}
