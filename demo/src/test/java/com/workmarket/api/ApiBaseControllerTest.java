package com.workmarket.api;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Pagination;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.MessageBundleHelperImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class ApiBaseControllerTest extends BaseApiControllerTest {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	private ApiBaseController controller;

	private FulfillmentPayloadDTO result;
	private ApiV2Pagination pagination;

	@Before
	public void setup() {

		controller = new ApiBaseController();

		messageHelper = mock(MessageBundleHelperImpl.class);

		when(messageHelper.getMessage("api.worker.v2.unable_to_process")).thenReturn(
						"We are unable to process your request, please refers to results for more details.");

		when(messageHelper.getMessage("api.worker.v2.unable_to_validate")).thenReturn(
						"We are unable to validate your request, please refers to results for more details.");

		when(messageHelper.getMessage("page.not.found")).thenReturn("That page doesn't exist");

		when(messageHelper.getMessage("unauthorized")).thenReturn(null);

		when(messageHelper.getMessage("api.worker.v2.unable_to_validate")).thenReturn("Bad Parameter");

		controller.setMessageHelper(messageHelper);

		request = new MockHttpServletRequest();
		request.setScheme("http");
		request.setServerName("www.workmarket.com");
		request.setServerPort(80);
		request.setRequestURI("/worker/v2/assignments");

		response = new MockHttpServletResponse();

		pagination = new ApiV2Pagination.ApiPaginationBuilder().page(3L).totalPageCount(10L).build(request);

		result = new FulfillmentPayloadDTO();
		result.setPagination(pagination);
	}

	@Ignore
	@Test
	public void handleException_MethodArgumentNotValidException_ResponseCreated() {
		//ApiResponse apiResponse = controller.handleException(request, response, exception);
	}

	@Ignore
	@Test
	public void handleException_MessageSourceApiException_ResponseCreated() {
		//ApiResponse apiResponse = controller.handleException(request, response, exception);
	}

	@Ignore
	@Test
	public void handleException_HttpMessageNotReadableException_ResponseCreated() {
		//ApiResponse apiResponse = controller.handleException(request, response, exception);
	}

/*
		TODO - API - refactor to test BaseApiInterceptor
    @Test
    public void handleException_BindException_ResponseCreated() {

        BindException exception = new BindException(controller, "ApiController");

        exception.addError(new ObjectError("page", "Page needs to be an integer."));
        exception.addError(new ObjectError("status", "Required param missing."));
        exception.addError(new ObjectError("description", "Not enough mojo in this description."));

        ApiResponseV2Worker apiResponse = controller.handleBindException(request, response, exception);

        assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, response.getStatus());
        //assertEquals(exception.getMessage(), apiResponse.getMeta().getString(ApiController.METADATA_MESSAGE_KEY));
        assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, getMetaInt(ApiController.METADATA_CODE_KEY), apiResponse.getMeta();

        List errorList = apiResponse.getResults();

        assertEquals("Page needs to be an integer.", (String) errorList.get(0));
        assertEquals("Required param missing.", (String) errorList.get(1));
        assertEquals("Not enough mojo in this description.", (String) errorList.get(2));
    }

    @Test
    public void handleException_TypeMismatchException_ResponseCreated() {

        Integer badValue = 42;
        TypeMismatchException exception = new TypeMismatchException(badValue, String.class);

        ApiResponseV2Worker apiResponse = controller.handleException(request, response, exception);

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
        assertEquals(exception.getMessage(), apiResponse.getMeta().getString(ApiController.METADATA_MESSAGE_KEY));
        assertEquals(HttpStatus.SC_BAD_REQUEST,
                     getMetaInt(ApiController.METADATA_CODE_KEY), apiResponse.getMeta();
    }

    @Test
    public void handleException_MobileHttpException404_ResponseCreated() {

        MobileHttpException404 exception = new MobileHttpException404();
        exception.setMessageKey("page.not.found");

        ApiResponseV2Worker apiResponse = controller.handleException(request, response, exception);

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals("That page doesn't exist", apiResponse.getMeta().getString(ApiController.METADATA_MESSAGE_KEY));
        assertEquals(HttpStatus.SC_NOT_FOUND,
                     getMetaInt(ApiController.METADATA_CODE_KEY), apiResponse.getMeta();
    }

    @Test
    public void handleException_MobileHttpException401_ResponseCreated() {

        MobileHttpException401 exception = new MobileHttpException401();
        exception.setMessageKey("unauthorized");

        ApiResponseV2Worker apiResponse = controller.handleException(request, response, exception);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(exception.getMessageKey(), apiResponse.getMeta().getString(ApiController.METADATA_MESSAGE_KEY));
        assertEquals(HttpStatus.SC_UNAUTHORIZED,
                     getMetaInt(ApiController.METADATA_CODE_KEY), apiResponse.getMeta();
    }

    @Test
    public void handleException_IllegalArgumentException_ResponseCreated() {

        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument, yah");

        ApiResponseV2Worker apiResponse = controller.handleIllegalArgumentException(request, response, exception);

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
        assertEquals("Bad Parameter", apiResponse.getMeta().getString(ApiController.METADATA_MESSAGE_KEY));
        assertEquals(HttpStatus.SC_BAD_REQUEST,
                     getMetaInt(ApiController.METADATA_CODE_KEY), apiResponse.getMeta();

        List errorList = apiResponse.getResults();
        assertEquals(1, errorList.size());
        assertEquals(exception.getMessage(), errorList.get(0));
    }

    @Test
    public void handleException_GenericException_ResponseCreated() {

        NullPointerException exception = new NullPointerException("NULL");

        ApiResponseV2Worker apiResponse = controller.handleException(request, response, exception);

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals(exception.getMessage(), apiResponse.getMeta().getString(ApiController.METADATA_MESSAGE_KEY));
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR,
                     getMetaInt(ApiController.METADATA_CODE_KEY), apiResponse.getMeta();
    }
*/

	@Test
	public void generatePagination_noQueryString_resultReturned() {

		request.setQueryString("");

		ApiV2Pagination returnedPagination = new ApiV2Pagination.ApiPaginationBuilder().page(3L)
						.totalPageCount(10L)
						.build(request);

		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/assignments?page=4"),
								 toUriComponents(returnedPagination.getLinks().get("next")));
		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/assignments?page=2"),
								 toUriComponents(returnedPagination.getLinks().get("prev")));
		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/assignments?page=1"),
								 toUriComponents(returnedPagination.getLinks().get("first")));
		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/assignments?page=10"),
								 toUriComponents(returnedPagination.getLinks().get("last")));
	}

	@Test
	public void generatePagination_noPageParamInQueryString_resultReturned() {

		request.setQueryString("pageSize=10&status=available");

		ApiV2Pagination returnedPagination = new ApiV2Pagination.ApiPaginationBuilder().page(3L)
						.pageSize(10L)
						.totalPageCount(10L)
						.build(request);

		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/assignments?pageSize=10&status=available&page=4"),
								 toUriComponents(returnedPagination.getLinks().get("next")));
		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/assignments?pageSize=10&status=available&page=2"),
								 toUriComponents(returnedPagination.getLinks().get("prev")));
		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/assignments?pageSize=10&status=available&page=1"),
								 toUriComponents(returnedPagination.getLinks().get("first")));
		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/assignments?pageSize=10&status=available&page=10"),
								 toUriComponents(returnedPagination.getLinks().get("last")));
	}

	@Test
	public void generatePagination_pageParamInQueryString_resultReturned() {

		request.setQueryString("pageSize=10&page=3&status=paid");

		ApiV2Pagination returnedPagination = new ApiV2Pagination.ApiPaginationBuilder().page(3L)
						.pageSize(10L)
						.totalPageCount(10L)
						.build(request);

		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/assignments?pageSize=10&page=4&status=paid"),
								 toUriComponents(returnedPagination.getLinks().get("next")));
		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/assignments?pageSize=10&page=2&status=paid"),
								 toUriComponents(returnedPagination.getLinks().get("prev")));
		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/assignments?pageSize=10&page=1&status=paid"),
								 toUriComponents(returnedPagination.getLinks().get("first")));
		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/assignments?pageSize=10&page=10&status=paid"),
								 toUriComponents(returnedPagination.getLinks().get("last")));
	}

	private UriComponents toUriComponents(final String httpUrl) {

		return UriComponentsBuilder.fromHttpUrl(httpUrl).build();
	}
}
