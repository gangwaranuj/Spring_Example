package com.workmarket.api.v2.model;

import com.workmarket.domains.banking.util.BankRoutingUtil;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.banking.PayPalAccount;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.model.BankAccountDTO;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.junit.Test;

import static org.junit.Assert.*;

public class ApiBankAccountDTOTest {
    @Test
    public void testCreateFromPayPalAccountEntity() throws Exception {
        final PayPalAccount account = new PayPalAccount();

        account.setId(10L);
        account.setConfirmedFlag(true);
        account.setEmailAddress("foo@bar.com");
        account.setCountry(Country.CANADA_COUNTRY);

        final ApiBankAccountDTO apiBankAccountDTO = new ApiBankAccountDTO.Builder(account).build();
        final BankAccountDTO bankAccountDTO = apiBankAccountDTO.toBankAccountDTO();

        assertEquals(Country.CANADA, bankAccountDTO.getCountryCode());
        assertEquals(AbstractBankAccount.PAYPAL, bankAccountDTO.getType());
        assertEquals("foo@bar.com", bankAccountDTO.getEmailAddress());
    }

    @Test
    public void testPayPalToBankAccountDTO() throws Exception {
        final ApiBankAccountDTO apiBankAccountDTO = new ApiBankAccountDTO.Builder()
            .setType(ApiBankAccountDTO.Type.PPA)
            .setCountry(Country.CANADA)
            .setName("foo@bar.com")
            .build();

        final BankAccountDTO bankAccountDTO = apiBankAccountDTO.toBankAccountDTO();

        assertEquals(Country.CANADA, bankAccountDTO.getCountryCode());
        assertEquals(AbstractBankAccount.PAYPAL, bankAccountDTO.getType());
        assertEquals("foo@bar.com", bankAccountDTO.getEmailAddress());
        assertEquals("PayPal", bankAccountDTO.getBankName());
    }

    @Test
    public void testAchUSToBankAccountDTO() throws Exception {
        final ApiBankAccountDTO apiBankAccountDTO = new ApiBankAccountDTO.Builder()
            .setAccountType(ApiBankAccountDTO.AccountType.CHECKING)
            .setType(ApiBankAccountDTO.Type.ACH)
            .setAccountNumber("12345678")
            .setName("US profile name")
            .setCountry(Country.USA)
            .setRoutingNumber("1234")
            .setBankName("Bank X")
            .build();

        final BankAccountDTO bankAccountDTO = apiBankAccountDTO.toBankAccountDTO();

        assertNull(bankAccountDTO.getBranchNumber());
        assertNull(bankAccountDTO.getInstitutionNumber());

        assertEquals(Country.USA, bankAccountDTO.getCountryCode());
        assertEquals(AbstractBankAccount.ACH, bankAccountDTO.getType());

        assertEquals("Bank X", bankAccountDTO.getBankName());
        assertEquals("1234", bankAccountDTO.getRoutingNumber());
        assertEquals("12345678", bankAccountDTO.getAccountNumber());
        assertEquals("US profile name", bankAccountDTO.getNameOnAccount());
        assertEquals(BankAccountType.CHECKING, bankAccountDTO.getBankAccountTypeCode());
    }

    @Test
    public void testAchCAToBankAccountDTO() throws Exception {
        final ApiBankAccountDTO apiBankAccountDTO = new ApiBankAccountDTO.Builder()
            .setAccountType(ApiBankAccountDTO.AccountType.CHECKING)
            .setType(ApiBankAccountDTO.Type.ACH)
            .setTransitBranchNumber("0001")
            .setFinancialInstNumber("10")
            .setAccountNumber("87654321")
            .setName("CAN profile name")
            .setCountry(Country.CANADA)
            .setRoutingNumber("4321")
            .setBankName("Bank Y")
            .build();

        final BankAccountDTO bankAccountDTO = apiBankAccountDTO.toBankAccountDTO();

        assertEquals(Country.CANADA, bankAccountDTO.getCountryCode());
        assertEquals(AbstractBankAccount.ACH, bankAccountDTO.getType());

        assertEquals("Bank Y", bankAccountDTO.getBankName());
        assertEquals("4321", bankAccountDTO.getRoutingNumber());
        assertEquals("87654321", bankAccountDTO.getAccountNumber());
        assertEquals("CAN profile name", bankAccountDTO.getNameOnAccount());
        assertEquals(BankAccountType.CHECKING, bankAccountDTO.getBankAccountTypeCode());

        assertEquals("0001", bankAccountDTO.getBranchNumber());
        assertEquals("10", bankAccountDTO.getInstitutionNumber());
    }

    @Test
    public void testAchWithAccountHolder() throws Exception {
        final ApiBankAccountDTO apiBankAccountDTO = new ApiBankAccountDTO.Builder()
            .setAccountType(ApiBankAccountDTO.AccountType.CHECKING)
            .setAccountHolder("CAN Account Holder")
            .setType(ApiBankAccountDTO.Type.ACH)
            .setAccountNumber("87654321")
            .setRoutingNumber("4321")
            .setCountry(Country.USA)
            .setBankName("Bank Y")
            .build();

        final BankAccountDTO bankAccountDTO = apiBankAccountDTO.toBankAccountDTO();

        assertEquals(Country.USA, bankAccountDTO.getCountryCode());
        assertEquals(AbstractBankAccount.ACH, bankAccountDTO.getType());

        assertNull(bankAccountDTO.getBranchNumber());
        assertNull(bankAccountDTO.getInstitutionNumber());

        assertEquals("Bank Y", bankAccountDTO.getBankName());
        assertEquals("4321", bankAccountDTO.getRoutingNumber());
        assertEquals("87654321", bankAccountDTO.getAccountNumber());
        assertEquals("CAN Account Holder", bankAccountDTO.getNameOnAccount());
        assertEquals(BankAccountType.CHECKING, bankAccountDTO.getBankAccountTypeCode());
    }

    @Test
    public void testBuildAchBankAccountUsaFromEntity() throws Exception {
        final BankAccount entity = new BankAccount();
        final Calendar createdOn = Calendar.getInstance();
        final Calendar confirmedOn = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        createdOn.setTime(dateFormat.parse("2017-01-01 12:12:12"));
        confirmedOn.setTime(dateFormat.parse("2017-02-02 12:12:12"));

        entity.setId(10L);
        entity.setConfirmedFlag(true);
        entity.setRoutingNumber("4568");
        entity.setCreatedOn(createdOn);
        entity.setConfirmedOn(confirmedOn);
        entity.setBankName("First Union Bank");
        entity.setNameOnAccount("Gomer Pyle");
        entity.setAccountNumber("56AIDK290");
        entity.setCountry(Country.USA_COUNTRY);
        entity.setBankAccountType(new BankAccountType(BankAccountType.CHECKING));

        final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder(entity).build();

        assertEquals(Country.USA, dto.getCountry());
        assertEquals(ApiBankAccountDTO.Type.ACH, dto.getType());

        assertNull(dto.getFinancialInstNumber());
        assertNull(dto.getTransitBranchNumber());

        assertTrue(dto.getVerified());
        assertEquals(new Long(10), dto.getId());
        assertEquals("4568", dto.getRoutingNumber());
        assertEquals("First Union Bank", dto.getBankName());
        assertEquals("XXXXXXXXK290", dto.getAccountNumber());
        assertEquals("First Union Bank", dto.getBankName());
        assertEquals(new Long(createdOn.getTimeInMillis()), dto.getCreatedOn());
        assertEquals(new Long(confirmedOn.getTimeInMillis()), dto.getConfirmedOn());
        assertEquals(ApiBankAccountDTO.AccountType.CHECKING, dto.getAccountType());
        assertEquals("Gomer Pyle", dto.getAccountHolder());
    }

    @Test
    public void testBuildAchBankAccountCanFromEntity() throws Exception {
        final BankAccount entity = new BankAccount();

        entity.setId(20L);
        entity.setBankName("CIBC");
        entity.setConfirmedFlag(false);
        entity.setAccountNumber("87654321");
        entity.setNameOnAccount("Julius Bike");
        entity.setCountry(Country.CANADA_COUNTRY);
        entity.setBankAccountType(new BankAccountType(BankAccountType.CHECKING));
        entity.setRoutingNumber(BankRoutingUtil.buildRoutingNumber( "12345", "789"));

        final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder(entity).build();

        assertEquals(Country.CANADA, dto.getCountry());
        assertEquals(ApiBankAccountDTO.Type.ACH, dto.getType());
        assertEquals(ApiBankAccountDTO.AccountType.CHECKING, dto.getAccountType());

        assertFalse(dto.getVerified());
        assertEquals(new Long(20), dto.getId());
        assertEquals("CIBC", dto.getBankName());
        assertEquals("CIBC (4321)", dto.getName());
        assertEquals("789", dto.getFinancialInstNumber());
        assertEquals("12345", dto.getTransitBranchNumber());
        assertEquals("XXXXXXXX4321", dto.getAccountNumber());
        assertEquals("Julius Bike", dto.getAccountHolder());
    }
}