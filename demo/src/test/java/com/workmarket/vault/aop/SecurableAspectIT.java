package com.workmarket.vault.aop;

import com.workmarket.dao.tax.AbstractTaxReportDAO;
import com.workmarket.dao.tax.TaxEntityDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.tax.ForeignTaxEntity;
import com.workmarket.domains.payments.dao.BankAccountDAO;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.tax.report.TaxReportService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.vault.services.VaultServerService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@Transactional
public class SecurableAspectIT extends BaseServiceIT {
	private static final String ACCOUNT_NUMBER = "678910";
	private static final Long BANK_ID = 1L;
	private static final Long ID = 2L;
	private static final String ACCOUNT_NUMBER_KEY = String.format("%s:%s:%s", "BankAccount", BANK_ID, "accountNumber");
	private static final String CANADIAN_TAX_KEY = String.format("%s:%s:%s", "CanadaTaxEntity", ID, "taxNumber");
	private static final String FOREIGN_TAX_KEY = String.format("%s:%s:%s", "ForeignTaxEntity", ID, "taxNumber");
	private static final String USA_TAX_KEY = String.format("%s:%s:%s", "UsaTaxEntity", ID, "taxNumber");

	@Autowired BankAccountDAO bankAccountDAO;
	@Autowired TaxEntityDAO taxEntityDAO;
	@Autowired VaultServerService vaultServerService;
	@Autowired AbstractTaxReportDAO abstractTaxReportDAO;
	@Autowired TaxReportService taxReportService;
	@Autowired VaultHelper vaultHelper;

	@Test
	public void shouldSecureBankAccount() {
		BankAccount bankAccount = new BankAccount();
		bankAccount.setId(BANK_ID);
		bankAccount.setAccountNumber(ACCOUNT_NUMBER);
		bankAccountDAO.saveOrUpdate(bankAccount);
		vaultHelper.secureEntity(bankAccount);
		assertEquals(bankAccount.getAccountNumber(), ((BankAccount) bankAccountDAO.get(bankAccount.getId())).getAccountNumber());
	}

	@Test
	public void shouldSecureCanadianTaxAccount() {
		Company company = newCompany();
		ForeignTaxEntity taxEntity = new ForeignTaxEntity();
		String taxNumber = UUID.randomUUID().toString();
		taxEntity.setId(ID);
		taxEntity.setTaxNumber(taxNumber);
		taxEntity.setCompany(company);
		taxEntityDAO.saveOrUpdate(taxEntity);
		vaultHelper.secureEntity(taxEntity);
		assertEquals(taxEntity.getTaxNumber(), taxEntityDAO.get(taxEntity.getId()).getTaxNumber());
	}
}
