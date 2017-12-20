package com.workmarket.api.v1.support;

import com.workmarket.api.v1.ApiV1Response;

import com.workmarket.api.v1.ApiV1ResponseMeta;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Assert {

	public static void assertSuccessfulResponseCode(ApiV1Response apiResponse) {
		assertEquals(HttpStatus.OK.value(), apiResponse.getMeta().getStatusCode());
	}

	public static void assertSuccessfulResponseField(ApiV1Response apiResponse) {
		assertHasResponse(apiResponse);
		assertTrue(apiResponse.getResponse() instanceof Map);
		Map responseMap = (Map)apiResponse.getResponse();
		assertTrue(responseMap.containsKey("successful"));
		assertTrue((Boolean)responseMap.get("successful"));
	}

	public static void assertNoErrors(ApiV1Response apiResponse) {
		ApiV1ResponseMeta meta = apiResponse.getMeta();

		if (!CollectionUtils.isEmpty(meta.getErrors())) {
			StringBuilder messages = new StringBuilder("Expected an empty error list! Error messages: {\n");

			for (Object error : meta.getErrors()) {
				if (error instanceof String) {
					messages.append("\tmessage: ").append(error).append("\n");
				}
				else if (error instanceof Map) {
					Map messageMap = (Map)error;
					messages.append("\tmessage: ").append(messageMap.get("message")).append("\n");
				}
				else {
					fail("unsupported type in errors: " + error.getClass().getName());
				}
			}

			messages.append("}");
			fail(messages.toString());
		}
	}

	public static void assertHasErrors(ApiV1Response apiResponse) {
		assertFalse(CollectionUtils.isEmpty(apiResponse.getMeta().getErrors()));
	}

	public static void assertHasResponse(ApiV1Response apiResponse) {
		assertNotNull(apiResponse.getResponse());

		if (apiResponse.getResponse() instanceof Map) {
			Map map = (Map)apiResponse.getResponse();
			assertFalse(CollectionUtils.isEmpty(map));
		}
		else if (apiResponse.getResponse() instanceof List) {
			List list = (List)apiResponse.getResponse();
			assertFalse(CollectionUtils.isEmpty(list));
		}
		else {
			fail("Expected list or map in response!");
		}
	}

	public static void assertHasIdNamePairs(ApiV1Response apiResponse) {
		assertHasResponse(apiResponse);
		assertTrue(apiResponse.getResponse() instanceof List);

		List itemList = (List)apiResponse.getResponse();
		for (Object item : itemList) {
			Map entry = (Map)item;
			assertTrue(entry.containsKey("id"));
			assertTrue(entry.containsKey("name"));
		}
	}
}
