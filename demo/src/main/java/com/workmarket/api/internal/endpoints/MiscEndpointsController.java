package com.workmarket.api.internal.endpoints;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.helpers.AccountServices;
import com.workmarket.api.helpers.LeaderboardService;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.internal.model.UserRegistration;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.domains.model.User;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.security.SecurityContext;
import com.workmarket.web.helpers.MessageBundleHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.TreeMap;

@Api(tags = {"register"})
@Controller
public class MiscEndpointsController extends ApiBaseController {

	@Autowired private AccountServices accountServices;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private ExtendedUserDetailsService extendedUserDetailsService;
	@Autowired private LeaderboardService leaderboardService;
	@Autowired protected MessageBundleHelper messageHelper;

	@Value("${api.exceptions.internal_server_error}")
	private String INTERNAL_SERVER_ERROR_MSG;

	@Value("${api.exceptions.unparseable_request}")
	private String UNPARSEABLE_REQUEST;

	@Value("${api.exceptions.failed_validation}")
	private String FAILED_VALIDATION;

	@Autowired private MessageSource messageSource;

	@Autowired @Qualifier("userRegistrationValidator")
	private Validator validator;

	private static final Log logger = LogFactory.getLog(MiscEndpointsController.class);
	private final SecurityContext securityContext = new SecurityContext();

	@ApiOperation(value = "Get some information about this internal API")
	@RequestMapping(value = "/api/v2.api", method = RequestMethod.GET)
	@ResponseBody
	public ApiV2Response internalEndpointsInfo() {
		TreeMap<String, String> commands = new TreeMap<>();
		commands.put("register.api", "use this command to create a basic account. minimum payload is email. " +
				"ex: /api/v2/register.api?email=xxx. " +
				"Avaiable options: returnPassword (default=false), autoConfirmEmail (default=false), sendConfirmEmail (default=false)");


		return ApiV2Response.valueWithResult(commands);
	}

	@ApiOperation(value = "View the leaderboard")
	@RequestMapping(value = "/api/v2/leaderboard.api", method = RequestMethod.GET)
	@ResponseBody
	public ApiV2Response getLeaderboard() {
		return ApiV2Response.valueWithResult(leaderboardService.getLeaderboardJSON().toString());
	}

	@ApiOperation(value = "Register a user")
	@RequestMapping(value = "/api/v2/register.api", method = RequestMethod.GET)
	@ResponseBody public ApiV2Response<UserRegistration> registerUser(@ApiParam @Valid UserRegistration user) throws Exception {

		setDefaultUserContext();
		logger.debug("running register with payload: " + user);

		user.setPassword(RandomStringUtils.randomAlphanumeric(8));
		User createdUser = accountServices.registerNewUser(user.toUserDTO(), user.toAddressDTO(),
		    user.getSendConfirmEmail(), user.isOnboardCompleted(), user.getAutoConfirmEmail());

		user.setUserNumber(createdUser.getUserNumber());
		user.setUserId(createdUser.getId().intValue());
		if (!user.getReturnPassword()) {
			user.setPassword(null);
		}

		return ApiV2Response.valueWithResult(user);
	}

	private void setDefaultUserContext() {
		User user = new User();
		user.setId(1798L); // gruen's user_id so we can track these down...
		securityContext.setCurrentUser(user);
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
}
