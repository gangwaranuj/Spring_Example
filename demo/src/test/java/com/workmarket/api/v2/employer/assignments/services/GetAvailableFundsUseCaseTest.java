package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.AvailableFundsApiDTO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.domains.payments.service.AccountRegisterService;

import com.workmarket.thrift.work.WorkActionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetAvailableFundsUseCaseTest {
    private final long expectedUserId = 123L;
    private final long expectedCompanyId = 456L;
    private final BigDecimal expectedSpendingLimit = BigDecimal.valueOf(789D);
    private final BigDecimal expectedAplLimit = BigDecimal.valueOf(987D);

    private User mockUser;
    private Company mockCompany;

    @Mock private AuthenticationService mockAuthenticationService;
    @Mock private AccountRegisterService mockAccountRegisterServicePrefundImpl;
    @Mock private PricingService mockPricingService;
    @Mock private CompanyService mockCompanyService;
    @Mock private ProfileService mockProfileService;

    @InjectMocks GetAvailableFundsUseCase getAvailableFundsUseCase = new GetAvailableFundsUseCase();

    @Before
    public void setUp() {
        mockUser = new User();
        mockUser.setId(expectedUserId);

        mockCompany = new Company();
        mockCompany.setId(expectedCompanyId);

        when(mockAuthenticationService.getCurrentUser()).thenReturn(mockUser);
        when(mockAccountRegisterServicePrefundImpl.calcSufficientBuyerFundsByCompany(anyLong())).thenReturn(expectedSpendingLimit);
        when(mockPricingService.calculateRemainingAPBalance(anyLong())).thenReturn(expectedAplLimit);
        when(mockCompanyService.hasPaymentTermsEnabled(anyLong())).thenReturn(true);
        when(mockProfileService.findCompany(expectedUserId)).thenReturn(mockCompany);
    }

    @Test
    public void when_init_then_get_user() throws WorkActionException {
        getAvailableFundsUseCase.init();

        final long expectedId = getAvailableFundsUseCase.user.getId();
        final long actualId = mockUser.getId();

        assertEquals(expectedId, actualId);
    }

    @Test
    public void when_prepare_then_get_available_funds() throws WorkActionException {
        getAvailableFundsUseCase.init();
        getAvailableFundsUseCase.prepare();

        assertEquals(expectedSpendingLimit, getAvailableFundsUseCase.spendingLimit);
        assertEquals(expectedAplLimit, getAvailableFundsUseCase.aplLimit);
    }

    @Test
    public void when_finish_then_load_available_funds() throws WorkActionException {
        getAvailableFundsUseCase.init();
        getAvailableFundsUseCase.prepare();
        getAvailableFundsUseCase.finish();

        AvailableFundsApiDTO availableFundsApiDTO = getAvailableFundsUseCase.availableFundsApiDTOBuilder.build();

        assertEquals(expectedSpendingLimit, availableFundsApiDTO.getSpendingLimit());
        assertEquals(expectedAplLimit, availableFundsApiDTO.getAplLimit());
    }
}
