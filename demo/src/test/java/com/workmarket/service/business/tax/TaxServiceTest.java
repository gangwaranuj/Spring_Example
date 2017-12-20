package com.workmarket.service.business.tax;

import com.google.api.client.util.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.tax.TaxEntityDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.CanadaTaxEntity;
import com.workmarket.domains.model.tax.EarningDetailReport;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.vault.services.VaultHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * User: iloveopt
 * Date: 3/7/14
 */

@RunWith(MockitoJUnitRunner.class)
public class TaxServiceTest {

	@InjectMocks TaxServiceImpl taxService;

	@Mock TaxEntityDAO taxEntityDAO;
	@Mock UserDAO userDAO;
	@Mock VaultHelper vaultHelper;
	private User user;
	private Company company;

	@Before
	public void setup() {

		user = mock(User.class);
		company = mock(Company.class);

		when(user.getId()).thenReturn(1L);
		when(user.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(1L);

		when(userDAO.get(anyLong())).thenReturn(user);
	}


	@Test
	public void hasTaxEntityPendingApproval_existingTaxEntity_returnTrue() {
		String companyId = user.getCompany().getId().toString();

		when(taxEntityDAO.hasTaxEntityPendingApproval(anyLong(), anyString())).thenReturn(true);
		assertTrue(taxService.hasTaxEntityPendingApproval(user.getId(), companyId));
		verify(taxEntityDAO).hasTaxEntityPendingApproval(eq(user.getId()), eq(companyId));
		verify(taxEntityDAO, never()).hasTaxEntityPendingApproval(anyLong());
	}

	@Test
	public void hasTaxEntityPendingApproval_hasTaxEntity_returnFalse() {
		when(taxEntityDAO.hasTaxEntityPendingApproval(anyLong())).thenReturn(false);
		assertFalse(taxService.hasTaxEntityPendingApproval(user.getId(), null));
		verify(taxEntityDAO).hasTaxEntityPendingApproval(eq(user.getId()));
		verify(taxEntityDAO, never()).hasTaxEntityPendingApproval(anyLong(), anyString());
	}

	@Test
	public void hasTaxEntityPendingApproval_hasTaxEntity_returnTrue() {
		when(taxEntityDAO.hasTaxEntityPendingApproval(anyLong())).thenReturn(true);
		assertTrue(taxService.hasTaxEntityPendingApproval(user.getId(), null));
		verify(taxEntityDAO).hasTaxEntityPendingApproval(eq(user.getId()));
		verify(taxEntityDAO, never()).hasTaxEntityPendingApproval(anyLong(), anyString());
	}

	@Test
	public void getFormattedTaxNumber() {

		String usaEIN = "11111111";
		UsaTaxEntity usaEINEntity = new UsaTaxEntity();
		usaEINEntity.setBusinessFlag(true);
		usaEINEntity.setTaxNumber(usaEIN);
		assertEquals(usaEINEntity.getFormattedTaxNumber(), taxService.getFormattedTaxNumber(usaEINEntity, usaEIN));

		String usaSSN = "222222222";
		UsaTaxEntity usaSSNEntity = new UsaTaxEntity();
		usaSSNEntity.setBusinessFlag(false);
		usaSSNEntity.setTaxNumber(usaSSN);
		assertEquals(usaSSNEntity.getFormattedTaxNumber(), taxService.getFormattedTaxNumber(usaSSNEntity, usaSSN));

		String canadaBN = "333333333";
		CanadaTaxEntity canadaBNEntity = new CanadaTaxEntity();
		canadaBNEntity.setBusinessFlag(true);
		canadaBNEntity.setTaxNumber(canadaBN);
		assertEquals(canadaBNEntity.getFormattedTaxNumber(), taxService.getFormattedTaxNumber(canadaBNEntity, canadaBN));

		String canadaSIN = "444444444";
		CanadaTaxEntity canadaSINEntity = new CanadaTaxEntity();
		canadaSINEntity.setBusinessFlag(false);
		canadaSINEntity.setTaxNumber(canadaSIN);
		assertEquals(canadaSINEntity.getFormattedTaxNumber(), taxService.getFormattedTaxNumber(canadaSINEntity, canadaSIN));
	}

	@Test
	public void getTaxIdToTaxNumberMapFromVault() {

		int numReports = 350;
		List taxReports = Lists.newArrayList();
		for(int i = 0;i < numReports;i++) {
			EarningDetailReport report = new EarningDetailReport();
			report.setId(new Long(i));
			report.setTaxEntity(new UsaTaxEntity());
			taxReports.add(report);
		}
		taxService.getTaxIdToTaxNumberMapFromVault(taxReports);

		verify(vaultHelper, times(numReports/TaxServiceImpl.VAULT_MAX_MULTI_GET)).mapEntityIdToFieldValue(anyList(), eq(AbstractTaxEntity.class), eq("taxNumber"));
	}
}
