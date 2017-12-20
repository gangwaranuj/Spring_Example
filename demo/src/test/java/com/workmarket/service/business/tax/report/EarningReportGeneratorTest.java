package com.workmarket.service.business.tax.report;

import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.AbstractTaxReportSet;
import com.workmarket.domains.model.tax.CanadaTaxEntity;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.domains.model.tax.EarningReportSet;
import com.workmarket.domains.model.tax.TaxReportRow;
import com.workmarket.domains.model.tax.TaxReportSetStatusType;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class EarningReportGeneratorTest {

	@Mock protected TaxService taxService;
	@Mock protected TaxReportService taxReportService;
	@Mock protected WorkNegotiationService workNegotiationService;
	@Mock protected VaultServerService vaultServerService;
	@Mock protected VaultHelper vaultHelper;
	@Mock protected FeatureEvaluator featureEvaluator;

	@InjectMocks EarningReportGenerator earningReportGenerator;

	private List<TaxReportRow> companiesWithEarnings;
	private EarningReportSet earningReportSet;
	private UsaTaxEntity usaTaxEntity;
	private CanadaTaxEntity canadaTaxEntity;
	private Company company;
	private VaultKeyValuePair pair;

	@Before
	public void setup() throws Exception {
		earningReportSet = mock(EarningReportSet.class);
		company = mock(Company.class);
		usaTaxEntity = mock(UsaTaxEntity.class);
		canadaTaxEntity = mock(CanadaTaxEntity.class);
		pair = mock(VaultKeyValuePair.class);

		when(featureEvaluator.hasFeature(anyLong(), anyString())).thenReturn(true);

		when(pair.isEmpty()).thenReturn(false);
		when(pair.getValue()).thenReturn("someTaxNumber");
		when(vaultHelper.buildKey(any(AbstractTaxEntity.class), anyString())).thenReturn("someKey");
		when(vaultServerService.get(anyString())).thenReturn(pair);
		when(vaultHelper.get(any(AbstractEntity.class), anyString(), anyString())).thenReturn(pair);

		when(earningReportSet.getTaxReportSetStatusType()).thenReturn(new TaxReportSetStatusType(TaxReportSetStatusType.NEW));
		when(earningReportSet.getTaxYear()).thenReturn(2013);
		earningReportGenerator.setTaxReportSet(earningReportSet);
		when(usaTaxEntity.getCountry()).thenReturn(AbstractTaxEntity.COUNTRY_USA);
		when(usaTaxEntity.getStatus()).thenReturn(new TaxVerificationStatusType(TaxVerificationStatusType.APPROVED));
		when(usaTaxEntity.isBusinessNameFlag()).thenReturn(false);
		when(canadaTaxEntity.isBusinessNameFlag()).thenReturn(false);
		when(canadaTaxEntity.getStatus()).thenReturn(new TaxVerificationStatusType(TaxVerificationStatusType.UNVERIFIED));
		when(canadaTaxEntity.getCountry()).thenReturn(AbstractTaxEntity.COUNTRY_CANADA);
		when(usaTaxEntity.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(1000L);
		when(usaTaxEntity.getTaxName()).thenReturn("name");
		when(canadaTaxEntity.getTaxName()).thenReturn("name");

		companiesWithEarnings = Lists.newArrayList();
		companiesWithEarnings.add(new TaxReportRow().setCompanyId(1L).setWorkPayments(BigDecimal.valueOf(700)).setActiveTaxEntityId(1L));
		companiesWithEarnings.add(new TaxReportRow().setCompanyId(2L).setWorkPayments(BigDecimal.valueOf(700)).setActiveTaxEntityId(2L));
		companiesWithEarnings.add(new TaxReportRow().setCompanyId(3L).setWorkPayments(BigDecimal.valueOf(700)).setActiveTaxEntityId(3L));
		companiesWithEarnings.add(new TaxReportRow().setCompanyId(4L).setWorkPayments(BigDecimal.valueOf(700)).setActiveTaxEntityId(4L));

		when(taxService.findAllCompaniesWithMultipleApprovedTaxEntities(any(DateRange.class))).thenReturn(Collections.EMPTY_SET);
		when(taxReportService.getAllCompaniesWithEarningsInPeriod(any(DateRange.class), any(BigDecimal.class), eq(AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES))).thenReturn(companiesWithEarnings);
		when(taxService.findActiveTaxEntityByCompanyForEarningReport(anyLong(), any(Calendar.class))).thenReturn(usaTaxEntity);
		Map<String, BigDecimal> expensesMap = Maps.newHashMap();
		expensesMap.put(AccountServiceType.VENDOR_OF_RECORD, BigDecimal.ONE);
		when(workNegotiationService.findTotalAdditionalExpensesPaidToCompany(anyLong(), any(DateRange.class))).thenReturn(expensesMap);
	}

	@Test
	public void generateTaxReport() {
		EarningReportSet set = earningReportGenerator.generateTaxReport();
		assertNotNull(set);
		verify(workNegotiationService, times(4)).findTotalAdditionalExpensesPaidToCompany(anyLong(), any(DateRange.class));
		verify(taxReportService,times(4)).saveTaxReport(any(AbstractTaxReport.class));
		verify(taxReportService,times(2)).saveTaxReportSet(any(AbstractTaxReportSet.class));

	}

	@Test
	public void findTaxEntityForTaxReport_returnsUSAEntity() {
		AbstractTaxEntity taxEntity = earningReportGenerator.findTaxEntityForTaxReport(1L);
		assertNotNull(taxEntity);
		assertEquals(taxEntity.getCountry(), AbstractTaxEntity.COUNTRY_USA);
	}

	@Test
	public void findTaxEntityForTaxReport_returnsCanadaEntity() {
		when(taxService.findActiveTaxEntityByCompanyForEarningReport(anyLong(), any(Calendar.class))).thenReturn(canadaTaxEntity);
		AbstractTaxEntity taxEntity = earningReportGenerator.findTaxEntityForTaxReport(1L);
		assertNotNull(taxEntity);
	}

	@Test
	public void getApprovedTaxEntitiesByCompanyId() {
		earningReportGenerator.getApprovedTaxEntitiesByCompanyId(1L);
		verify(taxService).findAllApprovedTaxEntitiesByCompanyId(eq(1L));
	}

	@Test
	public void getEarningsInPeriodByCompany() {
		earningReportGenerator.getEarningsInPeriodByCompany(Calendar.getInstance(), Calendar.getInstance(), 1L);
		verify(taxReportService).getEarningsInPeriodByCompany(any(Calendar.class), any(Calendar.class), anyLong(), eq(AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES));
	}

	@Test
	public void splitTaxReport() {
		Company company = new Company();
		company.setId(1L);
		List<AbstractTaxEntity> taxEntities = Lists.newArrayListWithExpectedSize(2);
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
		when(taxService.findAllApprovedTaxEntitiesByCompanyId(anyLong())).thenReturn(taxEntities);

		TaxReportRow taxReportRow = new TaxReportRow();
		taxReportRow.setEarnings(BigDecimal.TEN);
		taxReportRow.setCompanyId(1L);
		taxReportRow.setItemizedAmount(BigDecimal.TEN, RegisterTransactionType.RESOURCE_WORK_PAYMENT, AccountServiceType.VENDOR_OF_RECORD);
		when(taxReportService.getEarningsInPeriodByCompany(any(Calendar.class), any(Calendar.class), anyLong(), eq(AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES))).thenReturn(taxReportRow);

		Map<AbstractTaxEntity, DateRange> map = earningReportGenerator.findAllApprovedTaxEntitiesDateRangesByCompany(1L);
		assertTrue(map.size() == 2);
		earningReportGenerator.splitTaxReport(1L, map);
		verify(taxReportService, atLeast(1)).saveTaxReport(any(EarningReport.class));
	}

	@Test
	public void splitTaxReport_savesOnlyOneReport() {
		Company company = new Company();
		company.setId(1L);
		List<AbstractTaxEntity> taxEntities = Lists.newArrayListWithExpectedSize(2);
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
		when(taxService.findAllApprovedTaxEntitiesByCompanyId(anyLong())).thenReturn(taxEntities);

		TaxReportRow taxReportRow = new TaxReportRow();
		taxReportRow.setEarnings(BigDecimal.TEN);
		taxReportRow.setCompanyId(1L);
		taxReportRow.setItemizedAmount(BigDecimal.TEN, RegisterTransactionType.RESOURCE_WORK_PAYMENT, AccountServiceType.VENDOR_OF_RECORD);
		when(taxReportService.getEarningsInPeriodByCompany(any(Calendar.class), any(Calendar.class), anyLong(), eq(AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES))).thenReturn(taxReportRow);

		Map<AbstractTaxEntity, DateRange> map = earningReportGenerator.findAllApprovedTaxEntitiesDateRangesByCompany(1L);
		assertTrue(map.size() == 2);
		earningReportGenerator.splitTaxReport(1L, map);
		verify(taxReportService, times(1)).saveTaxReport(any(EarningReport.class));
	}
}
