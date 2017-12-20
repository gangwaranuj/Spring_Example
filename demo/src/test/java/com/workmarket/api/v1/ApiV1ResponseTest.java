package com.workmarket.api.v1;

import com.workmarket.api.v1.model.ApiTalentPoolDTO;
import junit.framework.TestCase;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ApiV1ResponseTest extends TestCase {

    @Test
    public void testApiV1ResponseErrorListConstructor() throws Exception {
        final List<String> errors    = Arrays.asList("error.foo", "error.bar");
        final ApiV1Response response =  new ApiV1Response(errors);

        assertNotNull(response.getMeta());
        assertNotNull(response.getResponse());
        assertEquals(errors, response.getMeta().getErrors());
    }

    @Test
    public void testBuildApiV1ResponseWithStatus() throws Exception {
        final ApiV1Response<ApiV1ResponseStatus> response1 = ApiV1Response.of(true);
        final ApiV1Response<ApiV1ResponseStatus> response2 = ApiV1Response.of(false);

        assertNotNull(response1.getMeta());
        assertNotNull(response2.getMeta());

        assertNotNull(response1.getResponse());
        assertNotNull(response1.getResponse());

        assertTrue(response1.getResponse().isSuccessful());
        assertFalse(response2.getResponse().isSuccessful());
    }

    @Test
    public void testBuildApiV1StatusResponseWithErrors() throws Exception {
        final List<String> errors1 = Arrays.asList("error.foo", "error.bar");
        final List<String> errors2 = Arrays.asList("error.baz", "error.foobar");

        final ApiV1Response<ApiV1ResponseStatus> response1 = ApiV1Response.of(true, errors1);
        final ApiV1Response<ApiV1ResponseStatus> response2 = ApiV1Response.of(false, errors2);

        assertNotNull(response1.getMeta());
        assertNotNull(response2.getMeta());

        assertNotNull(response1.getResponse());
        assertNotNull(response1.getResponse());

        assertTrue(response1.getResponse().isSuccessful());
        assertFalse(response2.getResponse().isSuccessful());

        assertEquals(errors1, response1.getMeta().getErrors());
        assertEquals(errors2, response2.getMeta().getErrors());
    }

    @Test
    public void testBuildApiV1StatusResponseWithStatusCode() throws Exception {
        final ApiV1Response<ApiV1ResponseStatus> response1 = ApiV1Response.of(true, HttpStatus.SC_BAD_REQUEST);
        final ApiV1Response<ApiV1ResponseStatus> response2 = ApiV1Response.of(false, HttpStatus.SC_FORBIDDEN);

        assertNotNull(response1.getMeta());
        assertNotNull(response2.getMeta());

        assertNotNull(response1.getResponse());
        assertNotNull(response1.getResponse());

        assertTrue(response1.getResponse().isSuccessful());
        assertFalse(response2.getResponse().isSuccessful());

        assertEquals(HttpStatus.SC_BAD_REQUEST, response1.getMeta().getStatusCode());
        assertEquals(HttpStatus.SC_FORBIDDEN, response2.getMeta().getStatusCode());
    }

    @Test
    public void testBuildApiV1StatusResponseWithErrorsAndStatusCode() throws Exception {
        final List<String> errors1 = Arrays.asList("error.bad_request");
        final List<String> errors2 = Arrays.asList("error.forbidden");

        final ApiV1Response<ApiV1ResponseStatus> response1 = ApiV1Response.of(true, errors1, HttpStatus.SC_BAD_REQUEST);
        final ApiV1Response<ApiV1ResponseStatus> response2 = ApiV1Response.of(false, errors2, HttpStatus.SC_FORBIDDEN);

        assertNotNull(response1.getMeta());
        assertNotNull(response2.getMeta());

        assertNotNull(response1.getResponse());
        assertNotNull(response1.getResponse());

        assertTrue(response1.getResponse().isSuccessful());
        assertFalse(response2.getResponse().isSuccessful());

        assertEquals(errors1, response1.getMeta().getErrors());
        assertEquals(errors2, response2.getMeta().getErrors());

        assertEquals(HttpStatus.SC_BAD_REQUEST, response1.getMeta().getStatusCode());
        assertEquals(HttpStatus.SC_FORBIDDEN, response2.getMeta().getStatusCode());
    }

    @Test
    public void testSetApiV1ResponseSuccessful() throws Exception {
        final ApiV1Response apiResponse = new ApiV1Response();

        assertNotNull(apiResponse.getMeta());
        assertNotNull(apiResponse.getResponse());

        assertSame(apiResponse, apiResponse.setSuccessful(true));
        assertTrue(apiResponse.getResponse() instanceof ApiV1ResponseStatus);

        final ApiV1ResponseStatus status = (ApiV1ResponseStatus) apiResponse.getResponse();

        assertTrue(status.isSuccessful());
    }

    @Test
    public void testSetApiV1ResponseValues() throws Exception {
        final ApiV1Response apiResponse = new ApiV1Response();
        final ApiV1ResponseMeta responseMeta = new ApiV1ResponseMeta();
        final ApiV1ResponseStatus responseStatus = new ApiV1ResponseStatus();

        assertNotNull(apiResponse.getMeta());
        assertNotNull(apiResponse.getResponse());

        assertSame(apiResponse, apiResponse.setMeta(responseMeta));
        assertSame(apiResponse, apiResponse.setResponse(responseStatus));

        assertSame(responseMeta, apiResponse.getMeta());
        assertSame(responseStatus, apiResponse.getResponse());
    }

    @Test
    public void testWrapApiV1ResponseStatus() throws Exception {
        final List<String> errors    = Arrays.asList("error.foo");
        final ApiV1ResponseMeta meta = new ApiV1ResponseMeta(errors);
        final ApiV1ResponseStatus status = new ApiV1ResponseStatus(false);
        final ApiV1Response response =  new ApiV1Response(status, meta);

        final ApiV1Response<ApiTalentPoolDTO> wrapped = ApiV1Response.wrap(ApiTalentPoolDTO.class, response);

        assertSame(response.getMeta(), response.getMeta());
        assertEquals(errors, response.getMeta().getErrors());
    }
}
