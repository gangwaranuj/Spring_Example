package com.workmarket.web.controllers.admin;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.lessThan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.workmarket.domains.model.tax.EarningDetailReportSet;
import com.workmarket.domains.model.tax.EarningReportSet;
import com.workmarket.domains.model.tax.TaxForm1099Set;
import com.workmarket.domains.model.tax.TaxServiceReportSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.EarningDetailReport;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.domains.model.tax.TaxForm1099;
import com.workmarket.domains.model.tax.TaxServiceReport;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.BankingFileGenerationService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.account.JournalEntrySummaryService;
import com.workmarket.service.business.account.summary.AccountingSummaryService;
import com.workmarket.service.business.tax.TaxVerificationService;
import com.workmarket.service.business.tax.report.TaxReportService;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.vault.models.VaultKeyValuePair;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.web.controllers.BaseControllerUnitTest;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.views.CSVView;

import static com.workmarket.testutils.matchers.CommonMatchers.isUpperCase;
import static com.workmarket.testutils.matchers.CommonMatchers.matchesRegex;

@RunWith(MockitoJUnitRunner.class)
public class AdminAccountingControllerTest extends BaseControllerUnitTest {

	@Mock private CSVView mockView;
	@Mock private AccountRegisterService accountRegisterServicePrefundImpl;
	@Mock private JournalEntrySummaryService journalEntrySummaryService;
	@Mock private AccountingSummaryService accountingSummaryService;
	@Mock private BankingFileGenerationService bankingFileGenerationService;
	@Mock private TaxVerificationService taxVerificationService;
	@Mock private CompanyService companyService;
	@Mock private ProfileService profileService;
	@Mock private MessageBundleHelper messageHelper;
	@Mock private AssetManagementService assetManagementService;
	@Mock private UploadService uploadService;
	@Mock private RestTemplate restTemplate;
	@Mock private TaxReportService taxReportService;
	@Mock private BillingService billingService;
	@Mock protected VaultHelper vaultHelper;

	@InjectMocks AdminAccountingController controller;

	private EarningReportSet earningReportSet = mock(EarningReportSet.class);
	private EarningDetailReportSet earningDetailReportSet = mock(EarningDetailReportSet.class);
	private TaxForm1099Set taxForm1099Set = mock(TaxForm1099Set.class);
	private TaxServiceReportSet taxServiceReportSet = mock(TaxServiceReportSet.class);


	protected static class AdminAccountingControllerRequest {
		public static MockHttpServletRequestBuilder downloadForm1099csv(Long downloadId) {
			return MockMvcRequestBuilders.get("/admin/accounting/form_1099/download/" + downloadId);
		}
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		initController(controller);

		Map<Long, String> taxIdToTaxNumberMap = new HashMap<Long, String>();
		taxIdToTaxNumberMap.put(55L, "111223333");

		when(vaultHelper.mapEntityIdToFieldValue(any(List.class), any(Class.class), anyString())).thenReturn(taxIdToTaxNumberMap);
		when(vaultHelper.get(any(AbstractEntity.class), eq("taxNumber"), anyString())).thenReturn(new VaultKeyValuePair("", "111223333"));


		when(taxReportService.findEarningReportSet(anyLong())).thenReturn(earningReportSet);
		when(taxReportService.findEarningDetailReportSet(anyLong())).thenReturn(earningDetailReportSet);
		when(taxReportService.findTaxForm1099Set(anyLong())).thenReturn(taxForm1099Set);
		when(taxReportService.findTaxServiceDetailReportSet(anyLong())).thenReturn(taxServiceReportSet);
	}

	private void initForm(AbstractTaxReport form) {
		UsaTaxEntity taxEntity = new UsaTaxEntity();

		taxEntity.setId(55L);
		taxEntity.setBusinessFlag(false);

		form.setCompanyId(55L);
		form.setTaxEntity(taxEntity);
		form.setFirstName("Billy, Bob");
		form.setLastName("O'BrienCCCCCCCCCCCCCCCCCCCCCCC");
		form.setAddress("88 72nd st, apt. 77");
		form.setCity("New York");
		form.setPostalCode("11011");
		form.setState("ny");
		form.setCountry("USA");
		form.setBusinessFlag(false);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void downloadForm1099csv_IsOK() throws Exception {
		Model m = mock(Model.class);

		TaxForm1099 form = new TaxForm1099();

		initForm(form);
		form.setAmount(new BigDecimal(5000000));

		List<TaxForm1099> list = Lists.newArrayList(form);

		when(taxReportService.findAllTaxForm1099ForReportGeneration(anyLong())).thenReturn(list);

		controller.downloadForm1099csv(1L, m);

		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(m).addAttribute(eq(CSVView.CSV_MODEL_KEY), captor.capture());

		List arg = captor.getValue();

		assertEquals(2, arg.size());

		String[] model = (String[]) arg.get(1);

		verifyTaxId(model[0]);
		verifyFirstName(model[1]);
		verifyLastName(model[2]);
		verifyAddress(model[3]);
		verifyAmount(model[8]);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void downloadEarningsReportcsv_IsOK() throws Exception {
		Model m = mock(Model.class);

		EarningReport form = new EarningReport();

		initForm(form);

		form.setEarnings(new BigDecimal(5000000));
		form.setExpenses(new BigDecimal(77));

		List<EarningReport> list = Lists.newArrayList(form);

		when(taxReportService.findAllEarningReportForReportGeneration(anyLong())).thenReturn(list);

		controller.downloadEarningsReportcsv(1L, m);

		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(m).addAttribute(eq(CSVView.CSV_MODEL_KEY), captor.capture());

		List arg = captor.getValue();

		assertEquals(2, arg.size());

		String[] model = (String[]) arg.get(1);

		verifyTaxId(model[7]);
		verifyFirstName(model[0]);
		verifyLastName(model[1]);
		verifyAddress(model[2]);
		verifyAmount(model[8]);
		verifyAmount(model[9]);
		verifyAmount(model[10]);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void downloadEarningsDetailReportcsv_IsOK() throws Exception {
		Model m = mock(Model.class);

		EarningDetailReport form = new EarningDetailReport();

		initForm(form);

		form.setEarnings(new BigDecimal(5000000));
		form.setExpenses(new BigDecimal(77));
		form.setBuyerCompanyId(99L);

		List<EarningDetailReport> list = Lists.newArrayList(form);

		when(taxReportService.findAllEarningDetailReportForReportGeneration(anyLong())).thenReturn(list);

		controller.downloadEarningsDetailReportcsv(1L, m);

		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(m).addAttribute(eq(CSVView.CSV_MODEL_KEY), captor.capture());

		List arg = captor.getValue();

		assertEquals(2, arg.size());

		String[] model = (String[]) arg.get(1);

		verifyTaxId(model[1]);
		verifyFirstName(model[2]);
		verifyLastName(model[3]);
		verifyAddress(model[4]);
		verifyAmount(model[9]);
		verifyAmount(model[10]);
		verifyAmount(model[11]);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void downloadEarningsDetailReportcsv_BuyerCompanyNull_IsOK() throws Exception {
		Model m = mock(Model.class);

		EarningDetailReport form = new EarningDetailReport();

		initForm(form);

		form.setEarnings(new BigDecimal(5000000));
		form.setExpenses(new BigDecimal(77));

		List<EarningDetailReport> list = Lists.newArrayList(form);

		when(taxReportService.findAllEarningDetailReportForReportGeneration(anyLong())).thenReturn(list);

		controller.downloadEarningsDetailReportcsv(1L, m);

		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(m).addAttribute(eq(CSVView.CSV_MODEL_KEY), captor.capture());

		List arg = captor.getValue();

		assertEquals(1, arg.size());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void downloadTaxServiceDetailReportcsv_IsOK() throws Exception {
		Model m = mock(Model.class);

		TaxServiceReport form = new TaxServiceReport();

		initForm(form);

		form.setBuyerCompanyId(99L);
		form.setEarnings(new BigDecimal(5000000));
		form.setExpenses(new BigDecimal(77));

		List<TaxServiceReport> list = Lists.newArrayList(form);

		when(taxReportService.findAllTaxServiceReportForReportGeneration(anyLong())).thenReturn(list);

		controller.downloadTaxServiceDetailReportcsv(1L, m);

		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(m).addAttribute(eq(CSVView.CSV_MODEL_KEY), captor.capture());

		List arg = captor.getValue();

		assertEquals(2, arg.size());

		String[] model = (String[]) arg.get(1);

		verifyTaxId(model[1]);
		verifyFirstName(model[2]);
		verifyLastName(model[3]);
		verifyAddress(model[4]);
		verifyAmount(model[9]);
		verifyAmount(model[10]);
		verifyAmount(model[11]);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void downloadTaxServiceDetailReportcsv_BuyerCompanyNull_IsOK() throws Exception {
		Model m = mock(Model.class);

		TaxServiceReport form = new TaxServiceReport();

		initForm(form);

		form.setEarnings(new BigDecimal(5000000));
		form.setExpenses(new BigDecimal(77));

		List<TaxServiceReport> list = Lists.newArrayList(form);

		when(taxReportService.findAllTaxServiceReportForReportGeneration(anyLong())).thenReturn(list);

		controller.downloadTaxServiceDetailReportcsv(1L, m);

		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(m).addAttribute(eq(CSVView.CSV_MODEL_KEY), captor.capture());

		List arg = captor.getValue();

		assertEquals(1, arg.size());
	}

	private void verifyTaxId(final String taxId) {
		assertThat(taxId, matchesRegex("[0-9]{3}-[0-9]{2}-[0-9]{4}"));
	}

	private void verifyAddress(final String address) {
		assertThat(address, is(not(anyOf(containsString("'"), containsString(",")))));
		assertThat(address.length(), lessThan(31));
		assertThat(address, isUpperCase());
	}

	private void verifyAmount(final String amount) {
		assertThat(amount, is(not(anyOf(containsString("'"), containsString(",")))));
	}

	private void verifyLastName(final String lastName) {
		assertThat(lastName, is(not(anyOf(containsString("'"), containsString(",")))));
		assertThat(lastName.length(), lessThan(19));
		assertThat(lastName, isUpperCase());
	}

	private void verifyFirstName(final String firstName) {
		assertThat(firstName, is(not(anyOf(containsString("'"), containsString(",")))));
		assertThat(firstName.length(), lessThan(15));
		assertThat(firstName, isUpperCase());
	}
}
