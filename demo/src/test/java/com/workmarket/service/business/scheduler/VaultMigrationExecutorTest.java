package com.workmarket.service.business.scheduler;

import com.google.common.collect.ImmutableList;

import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.vault.models.VaultKeyValuePair;
import com.workmarket.vault.services.VaultHelper;

import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VaultMigrationExecutorTest {

  @Mock
  Log logger;

  @Mock
  TaxService taxService;
  @Mock
  BankingService bankingService;
  @Mock
  VaultHelper vaultHelper;
  @InjectMocks
  VaultMigrationExecutor executor;

  @Before
  public void setup() {
    executor.setLogger(logger);
  }

  @Test
  public void shouldLogErrorsIfBankAccountNumberObfuscatedFromDate() throws Exception {
    when(bankingService.getAllBankAccountsFrom(any(Calendar.class))).thenReturn(ImmutableList.of(new BankAccount()));
    when(vaultHelper.multiGet(any(List.class), eq("accountNumber")))
        .thenReturn(ImmutableList.of(new VaultKeyValuePair("some-id", "xxxxxxx")));

    executor.verifyBankAccountNumbersFromCreatedDate("2016-10-10");

    verify(logger).error("[vault log] Key some-id is obfuscated!");
  }

  @Test
  public void shouldLogErrorsIfBankAccountNumberObfuscatedFromModifiedDate() throws Exception {
    when(bankingService.getAllBankAccountsFromModifiedOn(any(Calendar.class)))
        .thenReturn(ImmutableList.of(new BankAccount()));
    when(vaultHelper.multiGet(any(List.class), eq("accountNumber")))
        .thenReturn(ImmutableList.of(new VaultKeyValuePair("some-id", "xxxxxxx")));

    executor.verifyBankAccountNumbersFromModifiedDate("2016-10-10");

    verify(logger).error("[vault log] Key some-id is obfuscated!");
  }

  @Test
  public void shouldLogErrorsIfTaxNumberObfuscatedFromModifiedDate() throws Exception {
    when(taxService.findAllTaxEntitiesFromModifiedDate(any(Calendar.class)))
        .thenReturn(new ArrayList(ImmutableList.of(new UsaTaxEntity())));
    when(vaultHelper.multiGet(any(List.class), eq("taxNumber")))
        .thenReturn(ImmutableList.of(new VaultKeyValuePair("some-id", "xxxxxxx")));

    executor.verifyTaxNumbersFromModifiedDate("2016-10-10");

    verify(logger).error("[vault log] Key some-id is obfuscated!");
  }

  @Test
  public void shouldLogErrorsIfTaxNumberObfuscatedFromId() throws Exception {
    when(taxService.findAllAccountsFromId(any(Long.class)))
        .thenReturn(new ArrayList(ImmutableList.of(new UsaTaxEntity())));
    when(vaultHelper.multiGet(any(List.class), eq("taxNumber")))
        .thenReturn(ImmutableList.of(new VaultKeyValuePair("some-id", "xxxxxxx")));

    executor.verifyTaxNumbersFromId(1L);

    verify(logger).error("[vault log] Key some-id is obfuscated!");
  }
}