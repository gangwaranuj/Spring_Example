package com.workmarket.service.business.tax.report;

import com.google.common.base.Optional;
import com.workmarket.dao.UserDAO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.tax.AbstractTaxReportDAO;
import com.workmarket.dao.tax.EarningReportDownloadAuditDAO;
import com.workmarket.dao.tax.TaxForm1099DownloadAuditDAO;
import com.workmarket.dao.tax.TaxReportSetDAO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.EarningDetailReport;
import com.workmarket.domains.model.tax.EarningDetailReportSet;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.domains.model.tax.EarningReportSet;
import com.workmarket.domains.model.tax.NewEarningReport;
import com.workmarket.domains.model.tax.TaxForm1099;
import com.workmarket.domains.model.tax.TaxForm1099DownloadAudit;
import com.workmarket.domains.model.tax.TaxForm1099Set;
import com.workmarket.domains.model.tax.TaxServiceReport;
import com.workmarket.domains.model.tax.TaxServiceReportSet;
import com.workmarket.domains.work.dao.WorkNegotiationDAO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.TaxReportGenerationEvent;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.business.template.TemplateService;
import com.workmarket.service.exception.IllegalEntityAccessException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class TaxReportServiceTest {

	@Mock private UserDAO userDAO;
	@Mock private AbstractTaxReportDAO abstractTaxReportDAO;
	@Mock private TaxForm1099DownloadAuditDAO taxForm1099AuditDAO;
	@Mock private AuthenticationService authenticationService;
	@Mock private TaxService taxService;
	@Mock private TaxReportSetDAO<TaxForm1099Set> taxReportSetDAO;
	@Mock private TaxReportSetDAO<EarningReportSet> earningReportSetDAO;
	@Mock private TaxReportSetDAO<EarningDetailReportSet> earningDetailReportSetDAO;
	@Mock private TaxReportSetDAO<TaxServiceReportSet> taxServiceReportSetDAO;
	@Mock private EventFactory eventFactory;
	@Mock private EventRouter eventRouter;
	@Mock private WorkNegotiationDAO workNegotiationDAO;
	@Mock private com.workmarket.common.template.pdf.PDFTemplateFactory PDFTemplateFactory;
	@Mock private TemplateService templateService;
	@Mock private EarningReportDownloadAuditDAO earningReportAuditDAO;
	@Mock private SecurityContextFacade securityFacade;
	@Mock private UserRoleService userRoleService;

	@InjectMocks TaxReportServiceImpl taxReportService;

	private User user;
	private User userWithTaxReport;
	private Company company;
	private Company companyWithTaxReport;
	private User internalUser;
	private TaxForm1099 taxForm1099;
	private TaxForm1099Set taxForm1099Set;
	private EarningReport earningReport;
	private List<EarningDetailReport> earningDetailReportSet;
	private NewEarningReport newEarningReport;
	private EarningDetailReport earningDetailReport;
	private ExtendedUserDetails userDetails;


	private long earningReportSetId = 1L;
	private long taxServiceDetailReportSetId = 1L;

	private long companyWithTaxReportId = 1001L;
	private long companyWithoutTaxReportId = 1000L;

	private long userWithTaxReportId = 3L;
	private long internalUserId = 2L;
	private long userWithoutTaxReportId = 1L;

	private int taxYear = 2012;

	@Before
	public void setup() {
		user = mock(User.class);
		userWithTaxReport = mock(User.class);
		internalUser = mock(User.class);

		company = mock(Company.class);
		companyWithTaxReport = mock(Company.class);

		taxForm1099 = mock(TaxForm1099.class);
		taxForm1099Set = mock(TaxForm1099Set.class);
		earningReport = mock(EarningReport.class);
		newEarningReport = mock(NewEarningReport.class);
		earningDetailReport = mock(EarningDetailReport.class);

		userDetails = mock(ExtendedUserDetails.class);

		List<EarningDetailReport> earningDetailReportSet = new ArrayList<>();
		earningDetailReportSet.add(earningDetailReport);

		when(userDAO.get(userWithoutTaxReportId)).thenReturn(user);
		when(userDAO.get(internalUserId)).thenReturn(internalUser);
		when(userDAO.get(userWithTaxReportId)).thenReturn(userWithTaxReport);

		when(userDAO.findUserById(userWithoutTaxReportId)).thenReturn(user);
		when(userDAO.findUserById(internalUserId)).thenReturn(internalUser);
		when(userDAO.findUserById(userWithTaxReportId)).thenReturn(userWithTaxReport);

		when(user.getCompany()).thenReturn(company);
		when(userWithTaxReport.getCompany()).thenReturn(companyWithTaxReport);

		when(user.isInternalUser()).thenReturn(false);
		when(user.hasAclRole(anyLong())).thenReturn(true);
		when(userRoleService.hasAnyAclRole(user, AclRole.ACL_ADMIN, AclRole.ACL_CONTROLLER)).thenReturn(true);
		when(userRoleService.isInternalUser(user)).thenReturn(false);
		when(userWithTaxReport.isInternalUser()).thenReturn(false);
		when(internalUser.isInternalUser()).thenReturn(true);
		when(userRoleService.isInternalUser(internalUser)).thenReturn(true);

		when(company.getId()).thenReturn(companyWithoutTaxReportId);
		when(companyWithTaxReport.getId()).thenReturn(companyWithTaxReportId);

		when(abstractTaxReportDAO.get(eq(TaxForm1099.class), anyLong())).thenReturn(taxForm1099);
		when(abstractTaxReportDAO.get(eq(EarningReport.class), anyLong())).thenReturn(earningReport);
		when(abstractTaxReportDAO.get(eq(EarningDetailReport.class), anyLong())).thenReturn(earningDetailReport);
		when(abstractTaxReportDAO.findEarningReportForUser(eq(companyWithTaxReportId), anyInt())).thenReturn(earningReport);
		when(abstractTaxReportDAO.isTax1099ReportYearAvailable(anyInt())).thenReturn(true);
		when(abstractTaxReportDAO.isEarningReportYearAvailable(anyInt())).thenReturn(true);
		when(abstractTaxReportDAO.isEarningDetailReportYearAvailable(anyInt())).thenReturn(true);
		when(abstractTaxReportDAO.isTaxServiceDetailReportYearAvailable(anyInt())).thenReturn(true);
		when(abstractTaxReportDAO.findAvailableEarningDetailReportsByCompanyAndReportSetId(1L, 1000L, earningReportSetId)).thenReturn(earningDetailReportSet);

		when(taxForm1099.getCompanyId()).thenReturn(companyWithTaxReportId);
		when(earningReport.getCompanyId()).thenReturn(companyWithTaxReportId);

		when(taxForm1099.getTaxFormPDFName()).thenCallRealMethod();
		when(taxForm1099.getTaxForm1099Set()).thenReturn(taxForm1099Set);
		when(taxForm1099.getTaxForm1099Set()).thenReturn(taxForm1099Set);
		when(taxForm1099Set.getTaxYear()).thenReturn(taxYear);

	}

	@Test
	public void findAvailable1099s() {
		List<TaxForm1099> taxForms = taxReportService.findAvailable1099s(1L);
		verify(userDAO, times(1)).get(1L);
		verify(abstractTaxReportDAO, times(1)).findAvailable1099s(1000L);
		assertNotNull(taxForms);
	}

	@Test
	public void findAvailableEarningReports() throws IllegalEntityAccessException {
		List<EarningReport> taxForms = taxReportService.findAvailableEarningReports(1L);
		verify(userDAO, times(1)).get(1L);
		verify(abstractTaxReportDAO, times(1)).findAvailableEarningReports(1000L);
		assertNotNull(taxForms);
	}

	@Test
	public void findAvailableEarningDetailReports() throws IllegalEntityAccessException {
		List<EarningDetailReport> reports = taxReportService.findAvailableEarningDetailReportsByUserIdAndReportSetId(1L, earningReportSetId);
		verify(userDAO).get(1L);
		verify(abstractTaxReportDAO).findAvailableEarningDetailReportsByCompanyAndReportSetId(1L, 1000L, earningReportSetId);
		assertNotNull(reports);
	}

	@Test
	public void findAvailableTaxServiceDetailReports() throws IllegalEntityAccessException {
		List<TaxServiceReport> reports = taxReportService.findAvailableTaxServiceDetailReports(1L);
		verify(userDAO).get(1L);
		verify(abstractTaxReportDAO).findAvailableTaxServiceDetailReportsByTaxEntityCompany(1000L);
		assertNotNull(reports);

	}

	@Test
	public void get1099Form_withInternalUser() throws Exception {
		when(authenticationService.getCurrentUser()).thenReturn(internalUser);
		Optional<TaxForm1099> taxReport = taxReportService.get1099Form(1L);
		assertTrue(taxReport.isPresent());
		assertEquals(taxReport.get(), taxForm1099);
	}

	@Test(expected = IllegalEntityAccessException.class)
	public void get1099Form_withNonInternalUserAndNotOwner() throws Exception {
		when(authenticationService.getCurrentUser()).thenReturn(user);
		taxReportService.get1099Form(1L);
	}

	@Test
	public void get1099Form_withNonInternalUserAndOwner() throws Exception {
		when(authenticationService.getCurrentUser()).thenReturn(userWithTaxReport);
		Optional<TaxForm1099> taxReport = taxReportService.get1099Form(1L);
		assertTrue(taxReport.isPresent());
		assertEquals(taxReport.get(), taxForm1099);
		verify(taxForm1099AuditDAO, times(1)).saveOrUpdate(any(TaxForm1099DownloadAudit.class));
	}

	@Test
	public void get1099Form_withNonExistingForm() throws Exception {
		when(authenticationService.getCurrentUser()).thenReturn(user);
		when(abstractTaxReportDAO.get(eq(TaxForm1099.class), anyLong())).thenReturn(null);
		Optional<TaxForm1099> taxReport = taxReportService.get1099Form(1L);
		assertFalse(taxReport.isPresent());
	}

	@Test
	public void findEarningReportForUser_withNonInternalUserAndNotExistent() throws Exception {
		Optional<EarningReport> earningReport = taxReportService.findEarningReportForUser(userWithoutTaxReportId, 2012);
		assertFalse(earningReport.isPresent());
	}

	@Test
	public void findEarningReportForUser_withNonInternalUserAndOwner() throws Exception {
		when(authenticationService.getCurrentUser()).thenReturn(userWithTaxReport);
		when(securityFacade.getCurrentUser()).thenReturn(userDetails);
		when(userDetails.isMasquerading()).thenReturn(Boolean.FALSE);
		Optional<EarningReport> earningReport = taxReportService.findEarningReportForUser(userWithTaxReportId, 2012);
		assertTrue(earningReport.isPresent());
	}

	@Test
	public void findEarningReportForUser_withNonInternalUserAndOwnerMasquerading() throws Exception {
		when(authenticationService.getCurrentUser()).thenReturn(userWithTaxReport);
		when(securityFacade.getCurrentUser()).thenReturn(userDetails);
		when(userDetails.isMasquerading()).thenReturn(Boolean.TRUE);
		Optional<EarningReport> earningReport = taxReportService.findEarningReportForUser(userWithTaxReportId, 2012);
		assertTrue(earningReport.isPresent());
	}

	@Test(expected = IllegalEntityAccessException.class)
	public void findEarningReportForUser_withNonInternalUserAndNotTheOwner() throws Exception {
		when(authenticationService.getCurrentUser()).thenReturn(user);
		Optional<EarningReport> earningReport = taxReportService.findEarningReportForUser(userWithTaxReportId, 2012);
		assertTrue(earningReport.isPresent());
	}

	@Test
	public void isEarningReportYearAvailable() {
		taxReportService.isEarningReportYearAvailableToGenerate(2011);
		verify(abstractTaxReportDAO, times(1)).isEarningReportYearAvailable(2011);
	}

	@Test
	public void isTax1099ReportYearAvailable() {
		taxReportService.isTax1099ReportYearAvailableToGenerate(2011);
		verify(abstractTaxReportDAO, times(1)).isTax1099ReportYearAvailable(2011);
	}

	@Test
	public void isEarningDetailReportYearAvailable() {
		taxReportService.isEarningDetailReportYearAvailableToGenerate(2011);
		verify(abstractTaxReportDAO, times(1)).isEarningDetailReportYearAvailable(2011);
	}

	@Test
	public void isTaxServiceDetailReportYearAvailable() {
		taxReportService.isTaxServiceDetailReportYearAvailableToGenerate(2011);
		verify(abstractTaxReportDAO, times(1)).isTaxServiceDetailReportYearAvailable(2011);
	}


	@Test
	public void findTaxForm1099SetById() {
		taxReportService.findTaxForm1099SetById(1L);
		verify(taxReportSetDAO, times(1)).get(1L);
	}

	@Test
	public void saveTaxReport() {
		taxReportService.saveTaxReport(new TaxForm1099());
		verify(abstractTaxReportDAO).saveOrUpdate(any(AbstractTaxReport.class));
	}

	@Test
	public void saveTaxReportSet_withEarningReport() {
		taxReportService.saveTaxReportSet(new EarningReportSet());
		verify(earningReportSetDAO).saveOrUpdate(any(EarningReportSet.class));
	}

	@Test
	public void saveTaxReportSet_with1099() {
		taxReportService.saveTaxReportSet(new TaxForm1099Set());
		verify(taxReportSetDAO).saveOrUpdate(any(TaxForm1099Set.class));
	}

	@Test
	public void saveTaxReportSet_withTaxServiceReport() {
		taxReportService.saveTaxReportSet(new TaxServiceReportSet());
		verify(taxServiceReportSetDAO).saveOrUpdate(any(TaxServiceReportSet.class));
	}

	@Test
	public void getAllCompaniesWithEarningsInPeriod() {
		taxReportService.getAllCompaniesWithEarningsInPeriod(new DateRange(), BigDecimal.TEN, AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES);
		verify(abstractTaxReportDAO).getAllCompaniesWithEarningsInPeriod(any(DateRange.class), eq(BigDecimal.TEN), eq(AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES));
	}

	@Test
	public void getEarningsInPeriodByCompany() {
		taxReportService.getEarningsInPeriodByCompany(Calendar.getInstance(), Calendar.getInstance(), 1L, AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES);
		verify(abstractTaxReportDAO).getEarningsInPeriodByCompany(any(Calendar.class), any(Calendar.class), eq(1L), eq(AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES));
	}

	@Test
	public void getEarningReportPdfView_before2013_getEarningReportPDFMasquerading() throws Exception {
		when(authenticationService.getCurrentUser()).thenReturn(userWithTaxReport);
		EarningReportSet earningReportSet = mock(EarningReportSet.class);
		when(earningReport.getEarningReportSet()).thenReturn(earningReportSet);
		when(earningReportSet.getTaxYear()).thenReturn(2012);
		when(securityFacade.getCurrentUser()).thenReturn(userDetails);
		when(userDetails.isMasquerading()).thenReturn(Boolean.TRUE);
		taxReportService.getEarningReportPdfView(1L);
		verify(abstractTaxReportDAO).get(eq(EarningReport.class), eq(1L));
		verify(PDFTemplateFactory).earningReportPDFTemplate(eq(earningReport));
	}

	@Test
	public void getEarningReportPdfView_before2013_getEarningReportPDF() throws Exception {
		when(authenticationService.getCurrentUser()).thenReturn(userWithTaxReport);
		EarningReportSet earningReportSet = mock(EarningReportSet.class);
		when(earningReport.getEarningReportSet()).thenReturn(earningReportSet);
		when(earningReportSet.getTaxYear()).thenReturn(2012);
		when(securityFacade.getCurrentUser()).thenReturn(userDetails);
		when(userDetails.isMasquerading()).thenReturn(Boolean.FALSE);
		taxReportService.getEarningReportPdfView(1L);
		verify(abstractTaxReportDAO).get(eq(EarningReport.class), eq(1L));
		verify(PDFTemplateFactory).earningReportPDFTemplate(eq(earningReport));
	}

	@Test
	public void getEarningReportPdfView_after2013_getNewEarningReportPDFMasquerading() throws Exception {
		when(authenticationService.getCurrentUser()).thenReturn(userWithTaxReport);
		EarningReportSet earningReportSet = mock(EarningReportSet.class);
		when(earningReport.getEarningReportSet()).thenReturn(earningReportSet);
		when(earningReportSet.getTaxYear()).thenReturn(2013);
		when(securityFacade.getCurrentUser()).thenReturn(userDetails);
		when(userDetails.isMasquerading()).thenReturn(Boolean.TRUE);
		taxReportService.getEarningReportPdfView(1L);
		verify(abstractTaxReportDAO).get(eq(EarningReport.class), eq(1L));
	}

	@Test
	public void getEarningReportPdfView_after2013_getNewEarningReportPDF() throws Exception {
		when(authenticationService.getCurrentUser()).thenReturn(userWithTaxReport);
		EarningReportSet earningReportSet = mock(EarningReportSet.class);
		when(earningReport.getEarningReportSet()).thenReturn(earningReportSet);
		when(earningReportSet.getTaxYear()).thenReturn(2013);
		when(securityFacade.getCurrentUser()).thenReturn(userDetails);
		when(userDetails.isMasquerading()).thenReturn(Boolean.FALSE);
		taxReportService.getEarningReportPdfView(1L);
		verify(abstractTaxReportDAO).get(eq(EarningReport.class), eq(1L));
	}

	@Test
	public void generateTaxForm1099Report() throws Exception {
		TaxForm1099Set taxForm1099Set = taxReportService.generateTaxForm1099Report(2013);
		assertNotNull(taxForm1099Set);
		verify(abstractTaxReportDAO).isTax1099ReportYearAvailable(eq(2013));
		verify(taxReportSetDAO).saveOrUpdate(any(TaxForm1099Set.class));
		verify(eventRouter).sendEvent(any(TaxReportGenerationEvent.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateTaxForm1099Report_withYearLessThan2011() throws Exception {
		taxReportService.generateTaxForm1099Report(2010);
	}


	@Test
	public void generateEarningsReport() throws Exception {
		EarningReportSet earningReportSet = taxReportService.generateEarningsReport(2013);
		assertNotNull(earningReportSet);
		verify(abstractTaxReportDAO).isEarningReportYearAvailable(eq(2013));
		verify(earningReportSetDAO).saveOrUpdate(any(EarningReportSet.class));
		verify(eventRouter).sendEvent(any(TaxReportGenerationEvent.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateEarningsReport_withYearLessThan2011() throws Exception {
		taxReportService.generateEarningsReport(2010);
	}

	@Test
	public void generateEarningsDetailReport() throws Exception {
		EarningDetailReportSet earningDetailReportSet = taxReportService.generateEarningsDetailReport(2013);
		assertNotNull(earningDetailReportSet);
		verify(abstractTaxReportDAO).isEarningDetailReportYearAvailable(eq(2013));
		verify(earningDetailReportSetDAO).saveOrUpdate(any(EarningDetailReportSet.class));
		verify(eventRouter).sendEvent(any(TaxReportGenerationEvent.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateEarningsDetailReport_withYearLessThan2011() throws Exception {
		taxReportService.generateEarningsDetailReport(2010);
	}

	@Test
	public void generateTaxServiceDetailReport() throws Exception {
		TaxServiceReportSet taxServiceReportSet = taxReportService.generateTaxServiceDetailReport(2013);
		assertNotNull(taxServiceReportSet);
		verify(abstractTaxReportDAO).isTaxServiceDetailReportYearAvailable(eq(2013));
		verify(taxServiceReportSetDAO).saveOrUpdate(any(TaxServiceReportSet.class));
		verify(eventRouter).sendEvent(any(TaxReportGenerationEvent.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateTaxServiceDetailReport_withYearLessThan2011() throws Exception {
		taxReportService.generateTaxServiceDetailReport(2010);
	}

	@Test
	public void findAllEarningDetailReport() {
		taxReportService.findAllEarningDetailReports();
		verify(earningDetailReportSetDAO).findAllTaxReportSets();
	}

	@Test
	public void findAllEarningDetailReportByEarningReportSetId() {
		taxReportService.findAllEarningDetailReportByEarningReportSetId(1L);
		verify(abstractTaxReportDAO).findAllEarningDetailReportByEarningReportSetId(1L);
	}

	@Test
	public void getTaxFormPDFName(){
		assertNotNull(taxForm1099.getTaxFormPDFName());
		assertEquals(taxForm1099.getTaxFormPDFName(), "f1099_" + taxYear + ".pdf");
	}

	@Test
	public void getTaxFormPDFNameNullResponse(){
		when(taxForm1099.getTaxFormPDFName()).thenCallRealMethod();
		when(taxForm1099.getTaxForm1099Set()).thenReturn(null);

		assertEquals(taxForm1099.getTaxFormPDFName(), null);
	}
}

