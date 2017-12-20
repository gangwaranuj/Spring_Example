package com.workmarket.service.business.scheduler;

import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.payments.dao.AccountRegisterDAO;
import com.workmarket.domains.payments.dao.AccountRegisterSummaryFieldsDAO;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;

/**
 * This test is ignored by default as given the state of our DB I cannot guarantee it will run consistently
 * in our environment. I'm keeping it  because this is the test case for a bug that was found so I am
 * able to verify the bug is fixed and also because we really should have more/better tests around this process
 * and this can serve as a foundation for expanding out test cases.
 */

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@TransactionConfiguration
@Transactional
public class AccountRegisterReconcileExecutorIT extends BaseServiceIT {
	private static final String REG_BALANCE = "registerTransaction-Balance:";

	@Autowired private AccountRegisterReconcileExecutor accountRegisterReconcileExecutor;
	@Autowired private AccountRegisterDAO accountRegisterDAO;

	@Resource(name = "simpleJdbcTemplate")
	private JdbcTemplate jdbcTemplate;

	@Test
	public void run_compute_differences_multiple_times_verify_state_is_not_maintained() {
		List<Long> accountRegisterIds = jdbcTemplate.queryForList("select id from account_register limit 10", Long.class);

		if (CollectionUtils.isNotEmpty(accountRegisterIds)) {
			Map<Long, String> differences1 = accountRegisterReconcileExecutor.reconcile(accountRegisterIds);

			if (differences1.size() > 1) {
				Long accountRegisterId = null;
				String difference = null;
				// find an entry with a mis-matched available cash
				for (Map.Entry<Long, String> entry : differences1.entrySet()) {
					// find an entry that doesn't reconcile available cash only
					if (entry.getValue().startsWith("Company:") && !entry.getValue().contains("accountsPayableBalanceFromRegister")) {
						accountRegisterId = entry.getKey();
						difference = entry.getValue();
						break;
					}
				}

				// find how much our available cash is off by
				if (accountRegisterId != null && difference != null) {
					int equalsIndex = difference.indexOf(REG_BALANCE);
					if (equalsIndex > 0) {
						String regTranAmount = difference.substring(equalsIndex + REG_BALANCE.length(), difference.indexOf(" ", equalsIndex + REG_BALANCE.length())).trim();
						BigDecimal availableCash = new BigDecimal(regTranAmount);

						// now make our adjustment - for this we will update the account register directly
						AccountRegister accountRegister = accountRegisterService.getAccountRegisterById(accountRegisterId);
						if (accountRegister != null) {
							AccountRegisterSummaryFields summaryFields = accountRegister.getAccountRegisterSummaryFields();
							if (summaryFields != null) {
								summaryFields.setAvailableCash(availableCash);
								accountRegisterDAO.saveOrUpdate(accountRegister);
							}
						}
						// now run a second time - we are expecting that the record we adjusted should not
						// be returned since we matched the register transaction result with the account register
						Map<Long, String> differences2 = accountRegisterReconcileExecutor.reconcile(accountRegisterIds);
						assertFalse(differences2.containsKey(accountRegisterId));
					}
				}
			}
		}
	}
}
