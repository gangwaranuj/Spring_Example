package com.workmarket.service.business.tax.report;

import com.google.common.collect.Lists;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.AbstractTaxReportSet;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.domains.model.tax.TaxReportDetailRow;
import com.workmarket.domains.model.tax.TaxReportSetStatusType;
import com.workmarket.domains.model.tax.TaxServiceReportSet;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.utility.DateUtilities;
import com.workmarket.vault.models.VaultKeyValuePair;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.vault.services.VaultServerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TaxServiceReportGeneratorTest {

	@Mock protected TaxService taxService;
	@Mock protected TaxReportService taxReportService;
	@Mock protected WorkNegotiationService workNegotiationService;
	@Mock protected VaultServerService vaultServerService;
	@Mock protected VaultHelper vaultHelper;
	@Mock protected FeatureEvaluator featureEvaluator;

	@InjectMocks TaxServiceReportGenerator taxReportGenerator;

	private TaxServiceReportSet taxServiceReportSet;
	private Company company;
	private VaultKeyValuePair pair;

	@Before
	public void setup() throws Exception {
		taxServiceReportSet = mock(TaxServiceReportSet.class);
		company = mock(Company.class);
		when(company.getId()).thenReturn(1L);

		pair = mock(VaultKeyValuePair.class);

		when(vaultHelper.buildKey(any(AbstractTaxEntity.class), anyString())).thenReturn("someKey");
		when(vaultServerService.get(anyString())).thenReturn(pair);
		when(vaultHelper.get(any(AbstractEntity.class), anyString(), anyString())).thenReturn(pair);

		when(taxServiceReportSet.getTaxReportSetStatusType()).thenReturn(new TaxReportSetStatusType(TaxReportSetStatusType.NEW));
		when(taxServiceReportSet.getTaxYear()).thenReturn(2013);
		taxReportGenerator.setTaxReportSet(taxServiceReportSet);
	}

	@Test
	public void generateTaxReport() {
		TaxServiceReportSet set = taxReportGenerator.generateTaxReport();
		assertNotNull(set);
		verify(taxReportService,times(2)).saveTaxReportSet(any(AbstractTaxReportSet.class));

	}

	@Test
	public void splitTaxReport() {
		List<UsaTaxEntity> taxEntities = Lists.newArrayListWithExpectedSize(2);
		UsaTaxEntity usaTaxEntity = new UsaTaxEntity();
		usaTaxEntity.setEffectiveDate(DateUtilities.newCalendar(2012, Calendar.MARCH, 28, 21, 43, 0));
		usaTaxEntity.setCompany(company);
		usaTaxEntity.setTaxName("name");

		UsaTaxEntity usaTaxEntity2 = new UsaTaxEntity();
		usaTaxEntity2.setEffectiveDate(DateUtilities.newCalendar(2013, Calendar.JANUARY, 1, 0, 0, 0));
		usaTaxEntity2.setCompany(company);
		usaTaxEntity2.setTaxName("name");

		taxEntities.add(usaTaxEntity);
		taxEntities.add(usaTaxEntity2);
		when(taxService.findAllUsaApprovedTaxEntitiesByCompanyId(anyLong())).thenReturn(taxEntities);

		TaxReportDetailRow taxReportRow = new TaxReportDetailRow();
		taxReportRow.setEarnings(BigDecimal.TEN)
			.setExpenses(BigDecimal.ZERO)
			.setCompanyId(1L);

		when(taxReportService.getEarningsInPeriodByCompany(any(Calendar.class), any(Calendar.class), anyLong(), eq(AccountServiceType.TAX_SERVICE_1099_SERVICE_TYPE))).thenReturn(taxReportRow);
		when(workNegotiationService.findTotalAdditionalExpensesPaidToCompanyByBuyer(anyLong(), anyLong(), any(DateRange.class), eq(AccountServiceType.TAX_SERVICE_1099_SERVICE_TYPE))).thenReturn(BigDecimal.ZERO);

		Map<AbstractTaxEntity, DateRange> map = taxReportGenerator.findAllApprovedTaxEntitiesDateRangesByCompany(1L);
		assertTrue(map.size() == 2);
		taxReportGenerator.splitTaxReport(1L, map);
		verify(taxReportService, atLeast(1)).saveTaxReport(any(EarningReport.class));
	}

	@Test
	public void splitTaxReport_savesOnlyOneReport() {

		List<UsaTaxEntity> taxEntities = Lists.newArrayListWithExpectedSize(2);
		UsaTaxEntity usaTaxEntity = new UsaTaxEntity();
		usaTaxEntity.setEffectiveDate(DateUtilities.newCalendar(2012, Calendar.MARCH, 28, 21, 43, 0));
		usaTaxEntity.setCompany(company);
		usaTaxEntity.setTaxName("name");

		UsaTaxEntity usaTaxEntity2 = new UsaTaxEntity();
		usaTaxEntity2.setEffectiveDate(DateUtilities.newCalendar(2014, Calendar.JANUARY, 14, 0, 0, 0));
		usaTaxEntity2.setCompany(company);
		usaTaxEntity2.setTaxName("name");

		taxEntities.add(usaTaxEntity);
		taxEntities.add(usaTaxEntity2);
		when(taxService.findAllUsaApprovedTaxEntitiesByCompanyId(anyLong())).thenReturn(taxEntities);

		TaxReportDetailRow taxReportRow = new TaxReportDetailRow();
		taxReportRow.setEarnings(BigDecimal.TEN)
			.setExpenses(BigDecimal.ZERO)
			.setCompanyId(1L);

		when(taxReportService.getEarningsInPeriodByCompany(any(Calendar.class), any(Calendar.class), anyLong(), eq(AccountServiceType.TAX_SERVICE_1099_SERVICE_TYPE))).thenReturn(taxReportRow);
		when(workNegotiationService.findTotalAdditionalExpensesPaidToCompanyByBuyer(anyLong(), anyLong(), any(DateRange.class), eq(AccountServiceType.TAX_SERVICE_1099_SERVICE_TYPE))).thenReturn(BigDecimal.ZERO);

		Map<AbstractTaxEntity, DateRange> map = taxReportGenerator.findAllApprovedTaxEntitiesDateRangesByCompany(1L);
		assertTrue(map.size() == 2);
		taxReportGenerator.splitTaxReport(1L, map);
		verify(taxReportService, times(1)).saveTaxReport(any(EarningReport.class));
	}

	@Test
	public void splitTaxReport_savesOnlyOneReportWhenOtherIsZero() {

		List<UsaTaxEntity> taxEntities = Lists.newArrayListWithExpectedSize(2);
		UsaTaxEntity usaTaxEntity = new UsaTaxEntity();
		usaTaxEntity.setEffectiveDate(DateUtilities.newCalendar(2013, Calendar.JANUARY, 1, 0, 0, 0));
		usaTaxEntity.setCompany(company);
		usaTaxEntity.setTaxName("name");

		UsaTaxEntity usaTaxEntity2 = new UsaTaxEntity();
		usaTaxEntity2.setEffectiveDate(DateUtilities.newCalendar(2013, Calendar.JULY, 14, 0, 0, 0));
		usaTaxEntity2.setCompany(company);
		usaTaxEntity2.setTaxName("name");

		taxEntities.add(usaTaxEntity);
		taxEntities.add(usaTaxEntity2);
		when(taxService.findAllUsaApprovedTaxEntitiesByCompanyId(anyLong())).thenReturn(taxEntities);

		// first tax entity has $10 earnings
		TaxReportDetailRow entity1TaxReportRow = new TaxReportDetailRow();
		entity1TaxReportRow.setEarnings(BigDecimal.TEN)
			.setExpenses(BigDecimal.ZERO)
			.setCompanyId(1L);

		// second tax entity has zero earnings
		TaxReportDetailRow entity2TaxReportRow = new TaxReportDetailRow();
		entity2TaxReportRow.setEarnings(BigDecimal.ZERO)
			.setExpenses(BigDecimal.ZERO)
			.setCompanyId(1L);

		// return $10 earnings report row when calculating for first tax entity and $0 on the second call
		when(taxReportService.getEarningsInPeriodByCompany(any(Calendar.class), any(Calendar.class), anyLong(), eq(AccountServiceType.TAX_SERVICE_1099_SERVICE_TYPE))).thenReturn(entity1TaxReportRow, entity2TaxReportRow);
		when(workNegotiationService.findTotalAdditionalExpensesPaidToCompanyByBuyer(anyLong(), anyLong(), any(DateRange.class), eq(AccountServiceType.TAX_SERVICE_1099_SERVICE_TYPE))).thenReturn(BigDecimal.ZERO);

		// both tax entities found for date range
		Map<AbstractTaxEntity, DateRange> map = taxReportGenerator.findAllApprovedTaxEntitiesDateRangesByCompany(1L);
		assertTrue(map.size() == 2);

		// only one report row is saved
		taxReportGenerator.splitTaxReport(1L, map);
		verify(taxReportService, times(1)).saveTaxReport(any(EarningReport.class));
	}
}
