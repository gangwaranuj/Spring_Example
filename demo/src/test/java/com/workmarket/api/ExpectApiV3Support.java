package com.workmarket.api;

import com.workmarket.api.v3.response.ApiV3ResponseMeta;
import com.workmarket.api.v3.response.ApiV3ResponseResult;
import com.workmarket.api.v3.response.meta.*;
import com.workmarket.api.v3.response.result.ApiV3ResponseResultErrors;

import static org.junit.Assert.assertEquals;

/**
 * Base class of "expects" for API V3.
 *
 * ExpectApiV3Support provides API expectations for API V3 response classes.
 * You can request upgrade or downgrade to Base API functionality by modifying the expectations.
 *
 * Created by joshlevine on 12/23/16.
 */
public class ExpectApiV3Support extends ExpectApiV2Support {
	public void expectStatusCode(int expectedStatusCode, ApiV3ResponseMeta responseMeta) {
		super.expectStatusCode(expectedStatusCode, responseMeta);
		assertEquals("Expected API V1 Support", expectedStatusCode, getMetaInt(ApiJSONPayloadMap.META_V1_STATUS_CODE, responseMeta));
		assertEquals("Expected API V2 Support", expectedStatusCode, getMetaInt(ApiJSONPayloadMap.META_V2_STATUS_CODE, responseMeta));
		assertEquals("Expected API V3 Support", expectedStatusCode, getMetaInt(ApiJSONPayloadMap.META_V3_STATUS_CODE, responseMeta));
	}

	/**
	 * Checks that the given ApiV3ResponseMeta instance implements all expected interfaces.
	 * To extend ApiV3ResponseMetaSupport create an ApiV3ResponseMetaMyProperty interface, then expect it here.
	 *
	 * @see com.workmarket.api.v3.response.ApiV3ResponseMeta
	 *
	 * @param responseMeta
	 */
	public void expectApiV3ResponseMetaSupport(ApiV3ResponseMeta responseMeta) {
		super.expectApiResponseMetaSupport(responseMeta);
		assertEquals("Expected API V3 Convenience: statusCode", true,  responseMeta instanceof ApiV3ResponseMetaStatusCode);
		assertEquals("Expected API V3 Convenience: responseTime", true,  responseMeta instanceof ApiV3ResponseMetaResponseTime);
		assertEquals("Expected API V3 Convenience: timestamp", true,  responseMeta instanceof ApiV3ResponseMetaTimestamp);
		assertEquals("Expected API V3 Convenience: requestId", true,  responseMeta instanceof ApiV3ResponseMetaRequestId);
		assertEquals("Expected API V3 Convenience: clientRequestId", true,  responseMeta instanceof ApiV3ResponseMetaClientRequestId);
	}

	/**
	 * Checks that the given ApiV3ResponseResult instance implements all expected interfaces
	 * To extend ApiV3ResponseResult create an ApiV3ResponseResultMyProperty interface, then expect it here.
	 *
	 * @param responseResult
	 */
	public void expectApiV3ResponseResult(ApiV3ResponseResult responseResult) {
		super.expectApiResponseResult(responseResult);
		assertEquals("Expected API V3 Convenience: errors", true,  responseResult instanceof ApiV3ResponseResultErrors);
	}
}
