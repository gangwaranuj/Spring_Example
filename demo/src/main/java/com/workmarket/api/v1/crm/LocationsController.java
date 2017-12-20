package com.workmarket.api.v1.crm;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v1.ApiV1Response;
import com.workmarket.api.v1.model.ApiLocationDTO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;

@Api(tags = "Locations")
@Controller("apiLocationsController")
@RequestMapping(value = {"/v1/employer/crm/locations", "/api/v1/crm/locations"})
public class LocationsController extends ApiBaseController {
	private static final Logger logger = LoggerFactory.getLogger(LocationsController.class);

	@Autowired private AuthenticationService authenticationService;
	@Autowired private CRMService crmService;
	@Autowired private UserService userService;
	@Autowired private MessageBundleHelper messageHelper;

	/**
	 * List locations.
	 * @param clientCompanyId is client company id
	 * @return ApiResponse
	 */
	@ApiOperation(value = "List locations")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/list", method=RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<List<ApiLocationDTO>> listLocationsAction(@RequestParam(value="client_id", required=false) Long clientCompanyId) {
		ApiV1Response<List<ApiLocationDTO>> apiResponse = new ApiV1Response<>();
		MessageBundle bundle = messageHelper.newBundle();

		logger.debug("retrieving location list for client_id={}", clientCompanyId);

		if (clientCompanyId != null) {
			try {
				User currentUser = userService.findUserById(authenticationService.getCurrentUser().getId());
				List<ClientLocation> locations = crmService.findAllLocationsByClientCompany(
						currentUser.getCompany().getId(), clientCompanyId);

				if (!locations.isEmpty()) {
					List<ApiLocationDTO> locationDTOList = new LinkedList<>();

					for (ClientLocation location : locations) {
						locationDTOList.add(new ApiLocationDTO.Builder()
							.withId(location.getId())
							.withName(StringUtilities.defaultString(location.getName(), location.getAddress().getAddress1()))
							.withLocationNumber(location.getLocationNumber())
							.withInstructions(location.getInstructions())
							.withAddress1(location.getAddress().getAddress1())
							.withAddress2(location.getAddress().getAddress2())
							.withCity(location.getAddress().getCity())
							.withState(location.getAddress().getState().getShortName())
							.withZip(location.getAddress().getPostalCode())
							.withCountry(location.getAddress().getCountry().getId())
							.withLatitude(location.getAddress().getLatitude() == null ? null : location.getAddress().getLatitude().toString())
							.withLongitude(location.getAddress().getLongitude() == null ? null : location.getAddress().getLongitude().toEngineeringString())
							.build()
						);
					}

					apiResponse.setResponse(locationDTOList);
				}
			}
			catch (Exception ex) {
				logger.error("error listing locations for requested client_id={}", new Object[] {clientCompanyId}, ex);
				messageHelper.addError(bundle, "api.v1.crm.locations.list.error");
			}
		}
		else {
			messageHelper.addError(bundle, "api.v1.crm.invalid.clientId");
		}

		if (bundle.hasErrors()) {
			apiResponse.getMeta().setErrorMessages(bundle.getErrors());
		}

		return apiResponse;
	}
}