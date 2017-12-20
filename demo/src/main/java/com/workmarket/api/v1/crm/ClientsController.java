package com.workmarket.api.v1.crm;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v1.ApiV1Response;
import com.workmarket.api.v1.model.ApiClientDTO;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.infra.business.AuthenticationService;
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
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;

@Api(tags = "Addressbook")
@Controller("apiClientsController")
@RequestMapping(value = {"/v1/employer/crm/clients", "/api/v1/crm/clients"})
public class ClientsController extends ApiBaseController {
	private static final Logger logger = LoggerFactory.getLogger(ClientsController.class);

	@Autowired private CRMService crmService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private AuthenticationService authenticationService;

	/**
	 * List clients.
	 * @return ApiResponse
	 */
	@ApiOperation(value = "List clients")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/list", method=RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<List<ApiClientDTO>> listClientsAction() {
		ApiV1Response<List<ApiClientDTO>> apiResponse = new ApiV1Response<>();
		MessageBundle bundle = messageHelper.newBundle();
		Long userId = authenticationService.getCurrentUser().getId();

		logger.debug("retrieving client list for userId={}", userId);

		try {
			List<ClientCompany> results = crmService.findAllClientCompanyByUser(userId);

			if (!results.isEmpty()) {
				List<ApiClientDTO> clients = new LinkedList<>();

				for (ClientCompany item : results) {
					clients.add(new ApiClientDTO.Builder()
						.withId(item.getId())
						.withName(item.getName())
						.withCustomerId(item.getCustomerId())
						.build()
					);
				}

				apiResponse.setResponse(clients);
			}
		}
		catch (Exception ex) {
			logger.error("Error retrieving client list for userId={}",
					new Object[] { userId }, ex);
			messageHelper.addError(bundle, "api.v1.crm.clients.list.error");
		}

		if (bundle.hasErrors()) {
			apiResponse.getMeta().setErrorMessages(bundle.getErrors());
		}

		return apiResponse;
	}
}
