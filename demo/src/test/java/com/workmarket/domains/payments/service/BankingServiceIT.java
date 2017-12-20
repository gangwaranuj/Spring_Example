package com.workmarket.domains.payments.service;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.dao.BankAccountDAO;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@Transactional
public class BankingServiceIT extends BaseServiceIT {
  private static String ACCOUNT_NUMBER = "827391742";

  @Autowired
  BankAccountDAO bankAccountDAO;
  @Autowired
  BankingService bankingService;

  Company company;

  @Before
  public void setup() {
    company = newCompany();
  }

  @Test
  public void shouldReturnAccountsGreaterThanModifiedOn() {
    final BankAccount bankAccount = createBankAccount();
    final List<BankAccount> accounts = bankingService.getAllBankAccountsFromModifiedOn(bankAccount.getModifiedOn());
    assertEquals(1, accounts.size());
    assertEquals(bankAccount, accounts.get(0));
  }

  @Test
  public void shouldReturnAccountsGreaterThanCreatedOn() {
    final BankAccount bankAccount = createBankAccount();
    final List<BankAccount> accounts = bankingService.getAllBankAccountsFrom(bankAccount.getCreatedOn());
    assertEquals(1, accounts.size());
    assertEquals(bankAccount, accounts.get(0));
  }

  private BankAccount createBankAccount() {
    final BankAccount bankAccount = new BankAccount();
    bankAccount.setAccountNumber(ACCOUNT_NUMBER);
    bankAccount.setActiveFlag(true);
    bankAccount.setBankName("Acme Inc.");
    bankAccount.setNameOnAccount("Acme Inc.");
    bankAccount.setBankAccountType(new BankAccountType(BankAccountType.CHECKING));
    bankAccount.setCompany(company);
    bankAccount.setConfirmedFlag(true);
    bankAccount.setCreatedOn(Calendar.getInstance());
    bankAccount.setCreatorId(1L);
    bankAccount.setModifierId(1L);
    bankAccount.setCountry(Country.USA_COUNTRY);
    bankAccountDAO.saveOrUpdate(bankAccount);
    return bankAccount;
  }
}