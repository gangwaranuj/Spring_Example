package com.workmarket.service.infra.http;

public interface HttpRequestCommandFactory {
	HttpRequestCommand createDeleteCommand();
	HttpRequestCommand createPostCommand();
	HttpRequestCommand createGetCommand();
}
