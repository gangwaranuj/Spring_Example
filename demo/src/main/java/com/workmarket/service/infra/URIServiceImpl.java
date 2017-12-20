package com.workmarket.service.infra;

import com.workmarket.service.external.ShortUrlAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class URIServiceImpl implements URIService {

	@Autowired private ShortUrlAdapter linkShortenerAdapter;

	@Override
	public String getShortUrl(String url) {
		return linkShortenerAdapter.getShortUrl(url);
	}
}
