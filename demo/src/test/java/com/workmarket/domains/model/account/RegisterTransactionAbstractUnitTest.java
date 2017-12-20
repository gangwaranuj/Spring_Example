package com.workmarket.domains.model.account;

import com.workmarket.service.business.accountregister.AddFundsToGeneral;
import com.workmarket.service.business.accountregister.AddFundsToProject;
import com.workmarket.service.business.accountregister.RegisterTransactionExecutor;

import static org.junit.Assert.assertFalse;
import org.junit.Test;

/**
 * Date: 8/9/13
 * Time: 2:26 PM
 */
public class RegisterTransactionAbstractUnitTest {

	RegisterTransactionExecutor registerTransactionsAbstract;

	@Test
	public void test_AddFundsToGeneralImpl_defaultPendingFlag_false(){
		registerTransactionsAbstract = (RegisterTransactionExecutor) new AddFundsToGeneral();
		assertFalse(registerTransactionsAbstract.isPending());
	}

	@Test
	public void test_AddFundsToProjectImpl_defaultPendingFlag_false(){
		registerTransactionsAbstract = (RegisterTransactionExecutor) new AddFundsToProject();
		assertFalse(registerTransactionsAbstract.isPending());
	}

}
