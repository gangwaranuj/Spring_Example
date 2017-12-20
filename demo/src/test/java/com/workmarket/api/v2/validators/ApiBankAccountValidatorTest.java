package com.workmarket.api.v2.validators;

import com.workmarket.api.v2.model.ApiBankAccountDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.banking.BankRouting;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.service.infra.business.InvariantDataService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiBankAccountValidatorTest {

    InvariantDataService dataService;

    SecurityContextFacade securityContext;

    @Before
    public void setUp() {
        this.dataService = mock(InvariantDataService.class);
        this.securityContext = mock(SecurityContextFacade.class);
    }

    @Test
    public void supports() throws Exception {
        final ApiBankAccountValidator validator = new ApiBankAccountValidator(dataService, securityContext);

        assertTrue(validator.supports(ApiBankAccountDTO.class));
        assertFalse(validator.supports(Object.class));
    }

    @Test
    public void testValidPayPalAccount() throws Exception {
        final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder()
            .setType(ApiBankAccountDTO.Type.PPA)
            .setCountry(Country.CANADA)
            .setName("foo@bar.com")
            .build();

        final Errors errors = validate(dto);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidACHAccount() throws Exception {
        final BankRouting bankDetails = mock(BankRouting.class);
        final ExtendedUserDetails currentUser = createCurrentUser("foo@bar.com");
        final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder()
            .setAccountType(ApiBankAccountDTO.AccountType.CHECKING)
            .setType(ApiBankAccountDTO.Type.ACH)
            .setName("Account Holder Name")
            .setBankName("Bank of America")
            .setAccountNumber("0123456789")
            .setRoutingNumber("011000138")
            .setCountry(Country.USA)
            .build();

        when(securityContext.getCurrentUser())
            .thenReturn(currentUser);

        when(dataService.getBankRouting(eq("011000138"), eq(Country.USA)))
            .thenReturn(bankDetails);

        final Errors errors = validate(dto);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void testInvalidEmailPayPalAccount() throws Exception {
        final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder()
            .setType(ApiBankAccountDTO.Type.PPA)
            .setCountry(Country.CANADA)
            .setName("foobar")
            .build();

        final Errors errors = validate(dto);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.hasFieldErrors("name"));
    }

    @Test
    public void testInvalidType() throws Exception {
        final ApiBankAccountDTO dto = new ApiBankAccountDTO.Builder()
            .setCountry(Country.CANADA)
            .setName("foobar")
            .build();

        final Errors errors = validate(dto);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.hasFieldErrors("type"));
    }

    private Errors validate(final ApiBankAccountDTO dto)
    {
        final Errors errors = new BindException(dto, "dto");
        final ApiBankAccountValidator validator = new ApiBankAccountValidator(dataService, securityContext);

        validator.validate(dto, errors);

        return errors;
    }

    private ExtendedUserDetails createCurrentUser(final String email) {
        final Collection<GrantedAuthority> authorities = Collections.<GrantedAuthority>emptyList();
        final ExtendedUserDetails currentUser = new ExtendedUserDetails("test", "test", authorities);

        currentUser.setId(123L);
        currentUser.setEmail(email);

        return currentUser;
    }
}