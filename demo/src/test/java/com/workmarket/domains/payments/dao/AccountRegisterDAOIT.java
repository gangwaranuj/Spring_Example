package com.workmarket.domains.payments.dao;

import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by joshlevine on 3/11/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@TransactionConfiguration
public class AccountRegisterDAOIT extends BaseServiceIT {

	@Autowired AccountRegisterDAO accountRegisterDAO;
	@Autowired TransactionTemplate transactionTemplate;

	private ExecutorService transactionExecutor = new ThreadPoolExecutor(2, 2, 0,TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10));


	@Test
	public void testAccountRegisterLockTimeout() {
		final Callable<AccountRegister> lockAccountRegister = new Callable<AccountRegister>() {
			@Override
			public AccountRegister call() {
				return getAccountRegister(1L);
			}
		};

		transactionExecutor.submit(lockAccountRegister);
		transactionExecutor.submit(lockAccountRegister);

		transactionExecutor.shutdown();

		try {
			transactionExecutor.awaitTermination(70, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Transactional
	private AccountRegister getAccountRegister(final long companyId) {
		return transactionTemplate.execute(new TransactionCallback<AccountRegister>() {
			@Override
			public AccountRegister doInTransaction(TransactionStatus status) {
				AccountRegister accountRegister = accountRegisterDAO.findByCompanyId(companyId, true);
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return accountRegister;
			}
		});
	}
}
