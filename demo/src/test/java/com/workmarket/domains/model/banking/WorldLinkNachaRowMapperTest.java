package com.workmarket.domains.model.banking;

import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.postalcode.Country;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorldLinkNachaRowMapperTest {

	@InjectMocks WorldLinkNachaRowMapper worldLinkNachaRowMapper;

	private BankAccountTransaction bankAccountTransaction;
	private BankAccount bankAccount;
	private RegisterTransactionType registerTransactionType;

	@Before
	public void setUp() throws Exception {
		bankAccountTransaction = mock(BankAccountTransaction.class);
		bankAccount = mock(BankAccount.class);
		registerTransactionType = mock(RegisterTransactionType.class);

		when(registerTransactionType.getCode()).thenReturn(RegisterTransactionType.REMOVE_FUNDS);
		when(bankAccountTransaction.getRegisterTransactionType()).thenReturn(registerTransactionType);
		when(bankAccountTransaction.getBankAccount()).thenReturn(bankAccount);
		when(bankAccountTransaction.getAmount()).thenReturn(new BigDecimal(-10));
		when(bankAccount.getAccountNumber()).thenReturn("7373728130282");
		when(bankAccount.getCountry()).thenReturn(Country.CANADA_COUNTRY);
		when(bankAccount.getNameOnAccount()).thenReturn("Name on account");
		when(bankAccount.getBankName()).thenReturn("Bank of WM");
		when(bankAccount.getAccountNumberSanitized()).thenReturn("7373728130282");
	}

	@Test(expected = IllegalArgumentException.class)
	public void mapRow_withNullArguments_fail() throws Exception {
		worldLinkNachaRowMapper.mapRow(null, "");
	}

	@Test
	public void mapRow_contains_WL() throws Exception {
		String[] row = worldLinkNachaRowMapper.mapRow(bankAccountTransaction, "7373728130282");
		assertNotNull(row);
		assertTrue(ArrayUtils.contains(row, "WL"));
	}

	@Test
	public void mapRow_contains_ACH() throws Exception {
		String[] row = worldLinkNachaRowMapper.mapRow(bankAccountTransaction, "7373728130282");
		assertNotNull(row);
		assertTrue(ArrayUtils.contains(row, "ACH"));
	}

	@Test
	public void mapRow_contains_theAmount() throws Exception {
		String[] row = worldLinkNachaRowMapper.mapRow(bankAccountTransaction, "7373728130282");
		assertNotNull(row);
		assertTrue(ArrayUtils.contains(row, "10"));
	}

	@Test
	public void mapRow_contains_theAccountNumber() throws Exception {
		String[] row = worldLinkNachaRowMapper.mapRow(bankAccountTransaction, "7373728130282");
		assertNotNull(row);
		assertTrue(ArrayUtils.contains(row, "7373728130282"));
	}

	@Test
	public void fillEmptyCellsWithDelimiter_withNullArguments_returnsNull() throws Exception {
		assertNull(worldLinkNachaRowMapper.fillEmptyCellsWithDelimiter(null));
	}

	@Test
	public void fillEmptyCellsWithDelimiter() throws Exception {
		String[] array = new String[6];
		array[0] = "some word";
		array[4] = "more words";
		assertNotNull(worldLinkNachaRowMapper.fillEmptyCellsWithDelimiter(array));
		assertTrue(array[1] == StringUtils.EMPTY);
		assertTrue(array[2] == StringUtils.EMPTY);
		assertTrue(array[3] == StringUtils.EMPTY);
		assertFalse(array[4] == StringUtils.EMPTY);
	}

	@Test
	public void sanitizeAndTruncate_withEmptyString_success() throws Exception {
		assertEquals("", worldLinkNachaRowMapper.sanitizeAndTruncate("", 4));
	}

	@Test
	public void sanitizeAndTruncate_withLongerSize_success() throws Exception {
		assertEquals("more", worldLinkNachaRowMapper.sanitizeAndTruncate("morethan4chars", 4));
	}

	@Test
	public void sanitizeAndTruncate_withProhibitedChars_success() throws Exception {
		assertEquals("more", worldLinkNachaRowMapper.sanitizeAndTruncate("moret!#han4chars", 4));
	}

	@Test
	public void sanitizeAndTruncate_withProhibitedCharsAndLongerSize_success() throws Exception {
		assertEquals("morethan4chars", worldLinkNachaRowMapper.sanitizeAndTruncate("moret!#han4chars", 40));
	}
}