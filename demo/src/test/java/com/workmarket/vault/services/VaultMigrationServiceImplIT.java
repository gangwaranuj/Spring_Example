package com.workmarket.vault.services;

import com.google.common.collect.Lists;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class VaultMigrationServiceImplIT extends BaseServiceIT {
	@Autowired BankingService bankingService;
	@Autowired @Qualifier("vaultMigrationServiceImpl") VaultMigrationService vaultMigrationService;
	@Autowired @Qualifier("vaultServerServiceRedisImpl") VaultServerService vaultServerService;
	@Autowired VaultHelper vaultHelper;

	List<Long> partition;

	@Before
	public void setup() throws Exception{
		List<Long> allIds = bankingService.getAllIds();
		partition = Lists.partition(allIds, 10).get(0);
		for (Long id : partition) {
			vaultServerService.remove(String.format("BankAccount:%s:accountNumber", id.toString()));
		}
	}

	@Test
	public void shouldMigratePartitionedBankAccountValuesToVault() throws Exception {
		for (Long id : partition) {
			assertTrue(vaultServerService.get(String.format("BankAccount:%s:accountNumber", id.toString())).isEmpty());
		}

		vaultMigrationService.migrateBankAccounts(Lists.newArrayList(partition));

		for (Long id : partition) {
			assertFalse(vaultServerService.get(String.format("BankAccount:%s:accountNumber", id.toString())).isEmpty());
		}
	}

}