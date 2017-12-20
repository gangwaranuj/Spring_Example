package com.workmarket.api.v2.employer.settings.services;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.v2.employer.settings.models.ACHBankAccountDTO;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Service;

@Service
public interface BankAccountService {
	ACHBankAccountDTO save(ACHBankAccountDTO builder) throws ValidationException, BeansException;
	ImmutableList<String> findAllAdminUserNames();
}
