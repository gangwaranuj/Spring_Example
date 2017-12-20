package com.workmarket.service.infra.http;

import org.springframework.stereotype.Service;

@Service
public class HttpRequestCommandFactoryImpl implements HttpRequestCommandFactory {
	@Override
	public HttpRequestCommand createDeleteCommand() {
		return new HttpRequestDeleteCommand();
	}

	@Override
	public HttpRequestCommand createPostCommand() {
		return new HttpRequestPostCommand();
	}

	@Override
	public HttpRequestCommand createGetCommand() {
		return new HttpRequestGetCommand();
	}
}
