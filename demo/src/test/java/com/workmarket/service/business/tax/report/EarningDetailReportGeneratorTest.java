package com.workmarket.service.business.tax.report;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.AbstractTaxReportSet;
import com.workmarket.domains.model.tax.CanadaTaxEntity;
import com.workmarket.domains.model.tax.EarningDetailReportSet;
import com.workmarket.domains.model.tax.TaxReportDetailRow;
import com.workmarket.domains.model.tax.TaxReportSetStatusType;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.service.business.tax.TaxService;
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
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * User: iloveopt
 * Date: 1/8/14
 */

@RunWith(MockitoJUnitRunner.class)
public class EarningDetailReportGeneratorTest {
	@Mock protected TaxService taxService;
	@Mock protected TaxReportService taxReportService;
	@Mock protected WorkNegotiationService workNegotiationService;
	@Mock protected VaultServerService vaultServerService;
	@Mock protected VaultHelper vaultHelper;
	@Mock protected FeatureEvaluator featureEvaluator;

	@InjectMocks EarningDetailReportGenerator earningDetailReportGenerator;

	private List<TaxReportDetailRow> companiesWithEarnings;
	private EarningDetailReportSet earningDetailReportSet;
	private UsaTaxEntity usaTaxEntity;
	private CanadaTaxEntity canadaTaxEntity;
	private Company company;
	private Set<Long> companiesWithFirstTaxEntityInPeriod;
	private VaultKeyValuePair pair;

	@Before
	public void setup() throws Exception {
		earningDetailReportSet = mock(EarningDetailReportSet.class);
		company = mock(Company.class);
		usaTaxEntity = mock(UsaTaxEntity.class);
		canadaTaxEntity = mock(CanadaTaxEntity.class);
		companiesWithFirstTaxEntityInPeriod = Sets.newHashSet(1L, 2L, 3L);
		pair = mock(VaultKeyValuePair.class);
		when(featureEvaluator.hasFeature(anyLong(), anyString())).thenReturn(true);

		when(pair.isEmpty()).thenReturn(false);
		when(pair.getValue()).thenReturn("someTaxNumber");
		when(vaultHelper.buildKey(any(AbstractTaxEntity.class), anyString())).thenReturn("someKey");
		when(vaultServerService.get(anyString())).thenReturn(pair);
		when(vaultHelper.get(any(AbstractEntity.class), anyString(), anyString())).thenReturn(pair);

		when(earningDetailReportSet.getTaxReportSetStatusType()).thenReturn(new TaxReportSetStatusType(TaxReportSetStatusType.NEW));
		when(earningDetailReportSet.getTaxYear()).thenReturn(2013);
		earningDetailReportGenerator.setTaxReportSet(earningDetailReportSet);
		when(usaTaxEntity.getCountry()).thenReturn(AbstractTaxEntity.COUNTRY_USA);
		when(canadaTaxEntity.getCountry()).thenReturn(AbstractTaxEntity.COUNTRY_CANADA);
		when(usaTaxEntity.getStatus()).thenReturn(new TaxVerificationStatusType(TaxVerificationStatusType.APPROVED));
		when(canadaTaxEntity.getStatus()).thenReturn(new TaxVerificationStatusType(TaxVerificationStatusType.UNVERIFIED));
		when(usaTaxEntity.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(1000L);
		when(usaTaxEntity.getTaxName()).thenReturn("name");
		when(canadaTaxEntity.getTaxName()).thenReturn("name");

		companiesWithEarnings = Lists.newArrayList();
		for(long i = 1 ; i < 5; i ++) {
			TaxReportDetailRow row = new TaxReportDetailRow();
			row.setCompanyId(i);
			row.setWorkPayments(BigDecimal.valueOf(700));
			row.setBuyerCompanyId(1001L);
			row.setActiveTaxEntityId(i);
			companiesWithEarnings.add(row);
		}

		when(taxService.findAllCompaniesWithMultipleApprovedTaxEntities(any(DateRange.class))).thenReturn(Collections.EMPTY_SET);
		when(taxReportService.getAllCompaniesWithEarningsDetailInPeriod(any(DateRange.class), any(BigDecimal.class), ImmutableList.of(anyString()))).thenReturn(companiesWithEarnings);
		when(taxService.findActiveTaxEntityByCompanyForEarningReport(anyLong(), any(Calendar.class))).thenReturn(usaTaxEntity);
		when(workNegotiationService.findTotalAdditionalExpensesPaidToCompanyByBuyer(anyLong(), anyLong(), any(DateRange.class), eq(AccountServiceType.NON_VOR_SERVICE_TYPES))).thenReturn(BigDecimal.ONE);

	}

	@Test
	public void generateEarningDetailReport_success() {
		EarningDetailReportSet set = earningDetailReportGenerator.generateTaxReport();
		assertNotNull(set);
		verify(taxReportService).getAllCompaniesWithEarningsDetailInPeriod(any(DateRange.class), any(BigDecimal.class), eq(AccountServiceType.NON_VOR_SERVICE_TYPES));
		verify(workNegotiationService, times(4)).findTotalAdditionalExpensesPaidToCompanyByBuyer(anyLong(), anyLong(), any(DateRange.class), eq(AccountServiceType.NON_VOR_SERVICE_TYPES));
		verify(taxReportService,times(4)).saveTaxReport(any(AbstractTaxReport.class));
		verify(taxReportService,times(2)).saveTaxReportSet(any(AbstractTaxReportSet.class));
	}

	@Test
	public void generateEarningDetailReport_withEmptyNameTIN_wontSave() {
		when(usaTaxEntity.getTaxName()).thenReturn("");
		when(canadaTaxEntity.getTaxName()).thenReturn("");

		EarningDetailReportSet set = earningDetailReportGenerator.generateTaxReport();
		assertNotNull(set);
		verify(taxReportService).getAllCompaniesWithEarningsDetailInPeriod(any(DateRange.class), any(BigDecimal.class), eq(AccountServiceType.NON_VOR_SERVICE_TYPES));
		verify(workNegotiationService, times(4)).findTotalAdditionalExpensesPaidToCompanyByBuyer(anyLong(), anyLong(), any(DateRange.class), eq(AccountServiceType.NON_VOR_SERVICE_TYPES));
		verify(taxReportService, never()).saveTaxReport(any(AbstractTaxReport.class));
		verify(taxReportService, times(2)).saveTaxReportSet(any(AbstractTaxReportSet.class));
	}

	@Test
	public void processCompaniesWithFirstTimeTaxEntity_callsTaxReportSizeOncePerCompany() {
		when(taxService.getAllCompaniesWithFirstTaxEntityInPeriod(any(DateRange.class), anyInt())).thenReturn(companiesWithFirstTaxEntityInPeriod);
		earningDetailReportGenerator.setTaxReportSet(earningDetailReportSet);
		earningDetailReportGenerator.processCompaniesWithFirstTimeTaxEntity();
		verify(taxReportService, times(companiesWithFirstTaxEntityInPeriod.size())).findAllCompaniesWithNonVorEarningsDetailsForCompany(any(DateRange.class), anyLong(), eq(AccountServiceType.NON_VOR_SERVICE_TYPES));
	}

	@Test
	public void getAllCompaniesWithEarningsInPeriod() {
		earningDetailReportGenerator.getAllCompaniesWithEarningsInPeriod();
		verify(taxReportService, times(1)).getAllCompaniesWithEarningsDetailInPeriod(any(DateRange.class), any(BigDecimal.class), eq(AccountServiceType.NON_VOR_SERVICE_TYPES));
	}


	@Test(expected = UnsupportedOperationException.class)
	public void getEarningsInPeriodByCompany_fail() {
		earningDetailReportGenerator.getEarningsInPeriodByCompany(Calendar.getInstance(), Calendar.getInstance(), 1L);
	}
}
