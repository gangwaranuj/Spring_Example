package com.workmarket.api.v1.crm;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v1.ApiV1Response;
import com.workmarket.api.v1.model.ApiContactDTO;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientContactPagination;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang.StringUtils;
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

@Api(tags = "Addressbook")
@Controller("apiContactsController")
@RequestMapping(value = {"/v1/employer/crm/contacts", "/api/v1/crm/contacts"})
public class ContactsController extends ApiBaseController {
	private static final Logger logger = LoggerFactory.getLogger(ContactsController.class);

	@Autowired private CRMService crmService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private AuthenticationService authenticationService;

	/**
	 * Retrieves the pagination results.
	 * @param clientId is client company id
	 * @return ApiResponse
	 */
	@ApiOperation(value = "List contacts")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/list", method=RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<List<ApiContactDTO>> listContactsAction(@RequestParam(value="client_id", required=false) String clientId) {
		ApiV1Response<List<ApiContactDTO>> apiResponse = new ApiV1Response<>();
		MessageBundle bundle = messageHelper.newBundle();
		Long userId = authenticationService.getCurrentUser().getId();

		logger.debug("retrieving contact list for client_id={} and userId={}", new Object[] { clientId, userId });

		if (StringUtils.isNotEmpty(clientId)) {
			try {
				ClientContactPagination pagination = new ClientContactPagination();
				pagination.setReturnAllRows();
				pagination.addFilter(ClientContactPagination.FILTER_KEYS.CLIENT_COMPANY_ID, clientId);
				pagination = crmService.findAllClientContactsByUser(userId, pagination);

				List<ApiContactDTO> contacts = new LinkedList<>();
				for (ClientContact item : pagination.getResults()) {
					contacts.add(new ApiContactDTO.Builder()
						.withId(item.getId())
						.withName(item.getFullName())
						.build()
					);
				}

				apiResponse.setResponse(contacts);
			}
			catch (Exception ex) {
				logger.error("error retrieving contact list for requested client_id={} and userId={}", new Object[] {clientId, userId}, ex);
				messageHelper.addError(bundle, "api.v1.crm.contacts.list.error");
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
