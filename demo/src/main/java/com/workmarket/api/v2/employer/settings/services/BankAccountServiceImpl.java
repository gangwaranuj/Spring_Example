package com.workmarket.api.v2.employer.settings.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.workmarket.api.v2.employer.assignments.services.UseCaseFactory;
import com.workmarket.api.v2.employer.settings.models.ACHBankAccountDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.utility.CollectionUtilities;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BankAccountServiceImpl implements BankAccountService {

	@Autowired UseCaseFactory useCaseFactory;
	@Autowired private SecurityContextFacade securityContextFacade;
	@Autowired protected AuthenticationService authenticationService;

	@Override
	public ACHBankAccountDTO save(ACHBankAccountDTO achBankAccountDTO) throws ValidationException, BeansException {
		return useCaseFactory
			.getUseCase(CreateBankAccountUseCase.class, achBankAccountDTO)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public ImmutableList<String> findAllAdminUserNames() {
		Set<String> userNames = Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);
		ExtendedUserDetails userDetails = securityContextFacade.getCurrentUser();

		if (!userDetails.getCompanyIsIndividual()) {
			Set<com.workmarket.domains.model.User> adminUsers = authenticationService.findAllAdminAndControllerUsersByCompanyId(userDetails.getCompanyId());
			userNames.addAll(CollectionUtilities.newListPropertyProjection(adminUsers, "fullName"));
		}

		if (!Constants.DEFAULT_COMPANY_NAME.equals(userDetails.getCompanyName())) {
			userNames.add(userDetails.getCompanyName());
		}

		userNames.add(userDetails.getFullName());

		return ImmutableList.copyOf(userNames);
	}

}
