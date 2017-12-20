package com.workmarket.api.v2.worker.controllers;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.exceptions.ForbiddenException;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.worker.model.ApiPersonaModeDTO;
import com.workmarket.api.v2.worker.model.PersonaMode;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@Api(tags = {"PersonaMode"})
public class PersonaModeController extends ApiBaseController {
	public static final String USER_PERSONA_MODE_REQUIRED = "user.personaMode.required";
	public static final String USER_PERSONA_MODE_NOT_ALLOWED = "user.personaMode.notAllowed";
	public static final String ACL_DISPATCHER = "ACL_DISPATCHER";

	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private UserService userService;

	public PersonaModeController() {
	}

	@ResponseBody
	@RequestMapping(
		value = "/worker/v2/persona/modes",
		method = GET
	)
	@ApiOperation("Retrieve all possible persona modes")
	public ApiV2Response<PersonaMode> listAllowedPersonaModes() {
		return ApiV2Response.valueWithResults(ImmutableList.copyOf(getAllowedPersonaModes()));
	}

	@ResponseBody
	@RequestMapping(
		value = "/worker/v2/persona/get",
		method = GET
	)
	@ApiOperation("Get the current persona mode")
	public ApiV2Response<ApiPersonaModeDTO> getPersonaMode() {

		final Long userId = getCurrentUser().getId();
		final ApiPersonaModeDTO.Builder builder = new ApiPersonaModeDTO.Builder();
		final Optional<PersonaPreference> optionalPersonaPreference = userService.getPersonaPreference(userId);

		if (!optionalPersonaPreference.isPresent()) {
			return ApiV2Response.valueWithResult(builder.build());
		}

		final PersonaPreference personaPreference = optionalPersonaPreference.get();

		if (personaPreference.isBuyer()) {
			builder.personaMode(PersonaMode.BUYER);
		} else if (personaPreference.isSeller()) {
			builder.personaMode(PersonaMode.SELLER);
		} else if (personaPreference.isDispatcher()) {
			builder.personaMode(PersonaMode.DISPATCHER);
		}

		return ApiV2Response.valueWithResult(builder.build());
	}

	@ResponseBody
	@RequestMapping(
		value = "/worker/v2/persona/set",
		method = POST
	)
	@ApiOperation("Set persona mode")
	public ApiV2Response<ApiPersonaModeDTO> setPersonaMode(
		@RequestBody final ApiPersonaModeDTO dto) throws ForbiddenException, BindException {

		final PersonaPreference personaPreference = new PersonaPreference();
		final Set<PersonaMode> modes = getAllowedPersonaModes();
		final PersonaMode mode = dto.getPersonaMode();

		if (mode == null) {
			throw modeNotAllowedException(dto, USER_PERSONA_MODE_REQUIRED, modes);
		}

		// protect mode - https://workmarket.atlassian.net/browse/APP-23311
		if (!modes.contains(mode)) {
			throw modeNotAllowedException(dto, USER_PERSONA_MODE_NOT_ALLOWED, modes);
		}

		switch (mode) {
			case BUYER: {
				personaPreference.setBuyer(true);
				break;
			}
			case SELLER: {
				personaPreference.setSeller(true);
				break;
			}
			case DISPATCHER: {
				personaPreference.setDispatcher(true);
				break;
			}
		}

		personaPreference.setUserId(getCurrentUser().getId());
		userService.saveOrUpdatePersonaPreference(personaPreference);
		authenticationService.refreshSessionForUser(getCurrentUser().getId());

		return ApiV2Response.valueWithResult(dto);
	}

	private Set<PersonaMode> getAllowedPersonaModes() {
		final ExtendedUserDetails user = getCurrentUser();
		final Set<PersonaMode> modes = new HashSet<>();

		if (user.isManageWork() && !user.isEmployeeWorker()) {
			modes.add(PersonaMode.BUYER);
		}

		if (user.isFindWork()) {
			modes.add(PersonaMode.SELLER);
		}

		if (user.hasAnyRoles(ACL_DISPATCHER)) {
			modes.add(PersonaMode.DISPATCHER);
		}

		return ImmutableSet.copyOf(modes);
	}

	private BindException modeNotAllowedException(final ApiPersonaModeDTO dto, final String code, final Set<PersonaMode> modes) {
		final String modesStr = Joiner.on(", ").join(modes);
		final BindingResult bindingResult = new BeanPropertyBindingResult(dto, "personaMode");
		final String message = String.format("Invalid persona mode. Allowed options are : [%s]", modesStr);
		final ObjectError error = new ObjectError("personaMode", new String[]{code}, new String[]{modesStr}, message);

		bindingResult.addError(error);

		return new BindException(bindingResult);
	}
}
