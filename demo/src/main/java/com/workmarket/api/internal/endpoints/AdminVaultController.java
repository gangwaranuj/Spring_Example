package com.workmarket.api.internal.endpoints;

import com.google.common.collect.Lists;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.exceptions.ResourceNotFoundException;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.vault.models.VaultKeyValuePair;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.vault.web.models.VaultResponse;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = {"vault"}, hidden=true)
@Controller
@RequestMapping(value = {"/admin/v2/vault"})
public class AdminVaultController extends ApiBaseController {
	@Autowired private SecurityContextFacade securityContextFacade;
	@Autowired private BankingService bankingService;
	@Autowired private VaultHelper vaultHelper;
	@Autowired private TaxService taxService;

	@ApiOperation(value = "Get bank account by id")
	@RequestMapping(value = "/bankAccounts/{id}/accountNumber", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response<VaultKeyValuePair> getAccountNumber(@PathVariable(value = "id") long id) throws Exception {
		AbstractBankAccount account = bankingService.findBankAccount(id);
		if (account == null) {
			throw new ResourceNotFoundException(String.format("Resource with ID %d does not exist", id));
		}

		String defaultValue = account instanceof BankAccount
			? ((BankAccount)account).getAccountNumberSanitized() : account.getBankAccountSecureNumber();
		return getResponse(account, "accountNumber", defaultValue);
	}

	@ApiOperation(value = "Get tax number by id")
	@RequestMapping(value = "/taxEntities/{id}/taxNumber", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response<VaultKeyValuePair> getTaxNumber(@PathVariable(value = "id") long id) throws Exception {
		List<? extends AbstractTaxEntity> entities = taxService.findTaxEntitiesById(Lists.newArrayList(id));
		if (CollectionUtils.isEmpty(entities) || entities.size() != 1) {
			throw new ResourceNotFoundException(String.format("Resource with ID %d does not exist", id));
		}

		return getResponse(entities.get(0), "taxNumber", entities.get(0).getTaxNumberSanitized());
	}

	private ApiV2Response getResponse(AbstractEntity entity, String field, String defaultValue) throws Exception {
		VaultKeyValuePair pair = vaultHelper.get(entity, field, defaultValue);

		if (pair.isEmpty()) {
			throw new ResourceNotFoundException(String.format("Entity field not found for ID %d", entity.getId()));
		}

		return ApiV2Response.valueWithResult(new VaultResponse(pair.getValue()));
	}

	@ModelAttribute("currentUser")
	protected ExtendedUserDetails getCurrentExtendedUserDetails() {
		return securityContextFacade == null ? null : securityContextFacade.getCurrentUser();
	}
}
