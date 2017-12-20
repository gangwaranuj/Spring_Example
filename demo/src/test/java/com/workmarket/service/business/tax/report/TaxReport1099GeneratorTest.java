package com.workmarket.service.business.tax.report;

import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.AbstractTaxReportSet;
import com.workmarket.domains.model.tax.CanadaTaxEntity;
import com.workmarket.domains.model.tax.TaxForm1099Set;
import com.workmarket.domains.model.tax.TaxReportRow;
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
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class TaxReport1099GeneratorTest {

	@Mock protected TaxService taxService;
	@Mock protected TaxReportService taxReportService;
	@Mock protected WorkNegotiationService workNegotiationService;
	@Mock protected VaultServerService vaultServerService;
	@Mock protected VaultHelper vaultHelper;
	@Mock protected FeatureEvaluator featureEvaluator;

	@InjectMocks TaxReport1099Generator taxReport1099Generator;

	private List<TaxReportRow> companiesWithEarnings;
	private TaxForm1099Set taxForm1099Set;
	private UsaTaxEntity usaTaxEntity;
	private CanadaTaxEntity canadaTaxEntity;
	private Company company;
	private VaultKeyValuePair pair;

	@Before
	public void setup() throws Exception {
		taxForm1099Set = mock(TaxForm1099Set.class);
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

		when(taxForm1099Set.getTaxYear()).thenReturn(2013);
		when(taxForm1099Set.getTaxReportSetStatusType()).thenReturn(new TaxReportSetStatusType(TaxReportSetStatusType.NEW));
		taxReport1099Generator.setTaxReportSet(taxForm1099Set);
		when(usaTaxEntity.getCountry()).thenReturn(AbstractTaxEntity.COUNTRY_USA);
		when(usaTaxEntity.getStatus()).thenReturn(new TaxVerificationStatusType(TaxVerificationStatusType.APPROVED));
		when(canadaTaxEntity.getStatus()).thenReturn(new TaxVerificationStatusType(TaxVerificationStatusType.UNVERIFIED));
		when(canadaTaxEntity.getCountry()).thenReturn(AbstractTaxEntity.COUNTRY_CANADA);
		when(usaTaxEntity.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(1000L);
		when(usaTaxEntity.getTaxName()).thenReturn("name");
		when(canadaTaxEntity.getTaxName()).thenReturn("name");

		companiesWithEarnings = Lists.newArrayList();
		companiesWithEarnings.add(new TaxReportRow().setCompanyId(1L).setEarnings(BigDecimal.valueOf(700)).setActiveTaxEntityId(1L));
		companiesWithEarnings.add(new TaxReportRow().setCompanyId(2L).setEarnings(BigDecimal.valueOf(700)).setActiveTaxEntityId(2L));
		companiesWithEarnings.add(new TaxReportRow().setCompanyId(3L).setEarnings(BigDecimal.valueOf(700)).setActiveTaxEntityId(3L));
		companiesWithEarnings.add(new TaxReportRow().setCompanyId(4L).setEarnings(BigDecimal.valueOf(700)).setActiveTaxEntityId(4L));

		when(taxService.findAllCompaniesWithMultipleApprovedTaxEntities(any(DateRange.class))).thenReturn(Collections.EMPTY_SET);
		when(taxReportService.getAllCompaniesWithEarningsInPeriod(any(DateRange.class), any(BigDecimal.class), eq(AccountServiceType.VOR_SERVICE_TYPES))).thenReturn(companiesWithEarnings);
		when(taxService.findActiveTaxEntityByCompanyForEarningReport(anyLong(), any(Calendar.class))).thenReturn(usaTaxEntity);
		Map<String, BigDecimal> expensesMap = Maps.newHashMap();
		expensesMap.put(AccountServiceType.VENDOR_OF_RECORD, BigDecimal.ONE);
		when(workNegotiationService.findTotalAdditionalExpensesPaidToCompany(anyLong(), any(DateRange.class))).thenReturn(expensesMap);
	}

	@Test
	public void generateTaxReport_success() {
		TaxForm1099Set set = taxReport1099Generator.generateTaxReport();
		assertNotNull(set);
		verify(workNegotiationService, times(4)).findTotalAdditionalExpensesPaidToCompany(anyLong(), any(DateRange.class));
		verify(taxReportService,times(4)).saveTaxReport(any(AbstractTaxReport.class));
		verify(taxReportService,times(2)).saveTaxReportSet(any(AbstractTaxReportSet.class));
	}

	@Test
	public void generateTaxReport_withTINEmptyName_wontSave1099() {
		when(usaTaxEntity.getTaxName()).thenReturn("");
		when(canadaTaxEntity.getTaxName()).thenReturn("");

		TaxForm1099Set set = taxReport1099Generator.generateTaxReport();
		assertNotNull(set);
		verify(workNegotiationService, times(4)).findTotalAdditionalExpensesPaidToCompany(anyLong(), any(DateRange.class));
		verify(taxReportService, never()).saveTaxReport(any(AbstractTaxReport.class));
		verify(taxReportService, times(2)).saveTaxReportSet(any(AbstractTaxReportSet.class));
	}

	@Test
	public void findTaxEntityForTaxReport_returnsUSAOnly() {
		UsaTaxEntity taxEntity = taxReport1099Generator.findTaxEntityForTaxReport(1L);
		assertNotNull(taxEntity);
		assertEquals(taxEntity.getCountry(), AbstractTaxEntity.COUNTRY_USA);
	}

	@Test
	public void findTaxEntityForTaxReport_wontReturnCanadaEntity() {
		when(taxService.findActiveTaxEntityByCompanyForEarningReport(anyLong(), any(Calendar.class))).thenReturn(canadaTaxEntity);
		UsaTaxEntity taxEntity = taxReport1099Generator.findTaxEntityForTaxReport(1L);
		assertNull(taxEntity);
	}

	@Test
	public void getApprovedTaxEntitiesByCompanyId() {
		taxReport1099Generator.getApprovedTaxEntitiesByCompanyId(1L);
		verify(taxService).findAllUsaApprovedTaxEntitiesByCompanyId(eq(1L));
	}

	@Test
	public void getEarningsInPeriodByCompany() {
		taxReport1099Generator.getEarningsInPeriodByCompany(Calendar.getInstance(), Calendar.getInstance(), 1L);
		verify(taxReportService).getEarningsInPeriodByCompany(any(Calendar.class), any(Calendar.class), anyLong(), eq(AccountServiceType.VOR_SERVICE_TYPES));
	}
}
