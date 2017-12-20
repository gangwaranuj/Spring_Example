package com.workmarket.api.v1;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.internal.RequestToken;
import com.workmarket.api.internal.service.ApiService;
import com.workmarket.api.v1.model.ApiAuthorizationDTO;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.models.MessageBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = "Authorization")
@Controller("apiAuthorizationController")
@RequestMapping(value = {"/v1/employer/authorization", "/api/v1/authorization"})
public class AuthorizationController extends ApiBaseController {
	private static final Logger logger = LoggerFactory.getLogger(AuthorizationController.class);

	@Autowired private ApiService apiService;

	@ApiOperation(value = "Request new access token")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/request", method = POST)
	@ResponseBody
	public ApiV1Response<ApiAuthorizationDTO> request(@RequestParam("token") String token, @RequestParam("secret") String secret) {
		ApiV1Response<ApiAuthorizationDTO> apiResponse = new ApiV1Response<>();
		MessageBundle bundle = messageHelper.newBundle();

		try {
			String accessToken = apiService.getAccessToken(new RequestToken(token, secret));
			apiResponse.setResponse(new ApiAuthorizationDTO.Builder()
				.withAccessToken(accessToken)
				.build());
		}
		catch (HttpException401 ex) {
			// Set errors.
			messageHelper.addError(bundle, "api.v1.authorization.request.invalid");
			logger.warn("authorization request failed for token={}. reason: {}", token, secret, ex);
			throw new ApiV1UnauthorizedException(ex);
		}
		catch (Exception ex) {
			// Set errors.
			bundle.addError(ex.getMessage());

			logger.error("error processing authorization request for token={}. reason: {}", token, secret, ex);
			throw new ApiV1UnauthorizedException(ex);
		}

		if (bundle.hasErrors()) {
			apiResponse.getMeta().setErrorMessages(bundle.getErrors());
		}

		return apiResponse;
	}
}