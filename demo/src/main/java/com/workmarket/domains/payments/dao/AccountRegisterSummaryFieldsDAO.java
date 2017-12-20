package com.workmarket.domains.payments.dao;

import com.google.common.base.Optional;
import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;

/**
 * Created by nick on 2013-06-01 5:10 PM
 */
public interface AccountRegisterSummaryFieldsDAO extends DAOInterface<AccountRegisterSummaryFields> {

	public Optional<AccountRegisterSummaryFields> findAccountRegisterSummaryByCompanyId(Long companyId);

}
