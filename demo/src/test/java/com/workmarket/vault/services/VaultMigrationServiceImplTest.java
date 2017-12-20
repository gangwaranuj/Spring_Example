package com.workmarket.vault.services;

import com.google.common.collect.Lists;
import com.workmarket.dao.tax.TaxEntityDAO;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.payments.dao.BankAccountDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VaultMigrationServiceImplTest {
	private static final String ROUTER_NUMBER = "1";
	private static final String ACCOUNT_NUMBER = "2";
	private static final String TAX_NUMBER = "3";
	private static final Long ID = 11L;

	@Mock BankAccountDAO bankAccountDAO;
	@Mock TaxEntityDAO taxEntityDAO;
	@Mock VaultHelper vaultHelper;
	@Mock VaultServerService vaultServerService;
	@InjectMocks VaultMigrationServiceImpl service;

	@Mock BankAccount bankAccount;
	@Mock UsaTaxEntity taxEntity;

	@Before
	public void setup() {
		bankAccount.setId(ID);
		bankAccount.setAccountNumber(ACCOUNT_NUMBER);
		bankAccount.setRoutingNumber(ROUTER_NUMBER);
		taxEntity.setId(ID);
		taxEntity.setTaxNumber(TAX_NUMBER);
		when(bankAccountDAO.get(any(List.class))).thenReturn(new ArrayList<AbstractBankAccount>() {{ add(bankAccount); }});
		when(taxEntityDAO.get(any(List.class))).thenReturn(new ArrayList<AbstractTaxEntity>() {{ add(taxEntity); }});
	}

	@Test
	public void shouldNotCallVaulOnNull() throws Exception {
		service.migrateBankAccounts(null);
		service.migrateTaxEntities(null, true, true);
		verify(vaultServerService, never()).post(any(List.class));
	}

	@Test
	public void shouldNotCallVaulOnEmpty() throws Exception {
		service.migrateBankAccounts(new ArrayList<Long>());
		service.migrateTaxEntities(new ArrayList<Long>(), true, true);
		verify(vaultServerService, never()).post(any(List.class));
	}

	@Test
	public void shouldCallPostForBankAccount() throws Exception {
		service.migrateBankAccounts(Lists.newArrayList(1L));
		verify(vaultServerService, times(1)).post(any(List.class));
	}

	@Test
	public void shouldPartitionBankAccountIds() throws Exception {
		List<Long> ids = new ArrayList<>();
		for (int i = 0; i < 51; i++) {
			ids.add(Long.valueOf(i));
		}

		service.migrateBankAccounts(ids);
		verify(vaultServerService, times(2)).post(any(List.class));
	}

	@Test
	public void shouldCallPostTaxEntity() throws Exception {
		service.migrateTaxEntities(Lists.newArrayList(1L), true, true);
		verify(vaultServerService, times(1)).post(any(List.class));
	}

	@Test
	public void shouldPartitionTaxAccountIds() throws Exception {
		List<Long> ids = new ArrayList<>();
		for (int i = 0; i < 51; i++) {
			ids.add(Long.valueOf(i));
		}

		service.migrateTaxEntities(ids, true, true);
		verify(vaultServerService, times(2)).post(any(List.class));
	}
}