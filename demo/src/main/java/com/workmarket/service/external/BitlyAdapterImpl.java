package com.workmarket.service.external;

import com.rosaloves.bitlyj.Url;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;

import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.shorten;
import static java.lang.String.format;

public class BitlyAdapterImpl implements ShortUrlAdapter {

	@Value("${baseurl}")
	private String BASE_URL;
	@Value("${BITLY_KEY}")
	private String BITLY_KEY;
	@Value("${BITLY_USER_NAME}")
	private String BITLY_USER_NAME;

	private static final Log logger = LogFactory.getLog(BitlyAdapterImpl.class);

	private String getAbsoluteURL(String url) {
		return BASE_URL + url;
	}

	@Override
	public String getShortUrl(String url) {
		try {
			URI uri = new URI(url);
			if (!uri.isAbsolute()) {
				url = getAbsoluteURL(url);
			}

			Url shortUrl = as(BITLY_USER_NAME, BITLY_KEY).call(shorten(url));
			return shortUrl.getShortUrl();
		} catch (Exception e) {
			logger.error(format("Unable to create bit.ly URL for url=%s", url), e);
			return "";
		}
	}
}
