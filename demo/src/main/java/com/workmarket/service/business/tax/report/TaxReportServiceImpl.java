package com.workmarket.service.business.tax.report;

import com.google.common.base.Optional;
import com.workmarket.common.template.pdf.PDFTemplate;
import com.workmarket.common.template.pdf.PDFTemplateFactory;
import com.workmarket.dao.UserDAO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.tax.AbstractTaxReportDAO;
import com.workmarket.dao.tax.EarningReportDownloadAuditDAO;
import com.workmarket.dao.tax.TaxForm1099DownloadAuditDAO;
import com.workmarket.dao.tax.TaxReportSetDAO;
import com.workmarket.dao.tax.TaxServiceReportDownloadAuditDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.AbstractTaxReportSet;
import com.workmarket.domains.model.tax.EarningDetailReport;
import com.workmarket.domains.model.tax.EarningDetailReportRow;
import com.workmarket.domains.model.tax.EarningDetailReportSet;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.domains.model.tax.EarningReportDownloadAudit;
import com.workmarket.domains.model.tax.EarningReportSet;
import com.workmarket.domains.model.tax.TaxForm1099;
import com.workmarket.domains.model.tax.TaxForm1099DownloadAudit;
import com.workmarket.domains.model.tax.TaxForm1099Set;
import com.workmarket.domains.model.tax.TaxReportDetailRow;
import com.workmarket.domains.model.tax.TaxReportRow;
import com.workmarket.domains.model.tax.TaxReportSetStatusType;
import com.workmarket.domains.model.tax.TaxServiceReport;
import com.workmarket.domains.model.tax.TaxServiceReportDownloadAudit;
import com.workmarket.domains.model.tax.TaxServiceReportSet;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.template.TemplateService;
import com.workmarket.service.exception.IllegalEntityAccessException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.vault.services.VaultServerService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

@Service
public class TaxReportServiceImpl implements TaxReportService {

	private static final Log logger = LogFactory.getLog(TaxReportServiceImpl.class);

	@Autowired private UserDAO userDAO;
	@Autowired private AbstractTaxReportDAO abstractTaxReportDAO;
	@Autowired private TaxForm1099DownloadAuditDAO taxForm1099AuditDAO;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private EventFactory eventFactory;
	@Autowired private EventRouter eventRouter;
	@Autowired private PDFTemplateFactory PDFTemplateFactory;
	@Autowired private TemplateService templateService;
	@Autowired private EarningReportDownloadAuditDAO earningReportAuditDAO;
	@Autowired private TaxForm1099DownloadAuditDAO taxForm1099DownloadAuditDAO;
	@Autowired private TaxServiceReportDownloadAuditDAO taxServiceReportDownloadAuditDAO;
	@Autowired private SecurityContextFacade securityContextFacade;
	@Qualifier("taxForm1099ReportSetDAOImpl") @Autowired private TaxReportSetDAO taxForm1099ReportSetDAO;
	@Qualifier("earningReportSetDAOImpl") @Autowired private TaxReportSetDAO earningReportSetDAO;
	@Qualifier("earningDetailReportSetDAOImpl") @Autowired private TaxReportSetDAO earningDetailReportSetDAO;
	@Qualifier("taxServiceReportSetDAOImpl") @Autowired private TaxReportSetDAO taxServiceReportSetDAO;
	@Autowired protected VaultServerService vaultServerService;
	@Autowired protected VaultHelper vaultHelper;
	@Autowired FeatureEvaluator featureEvaluator;
	@Autowired private UserRoleService userRoleService;

	@Override
	 public List<TaxForm1099> findAvailable1099s(long userId) {
		 Assert.notNull(userId);
		 User user = userDAO.get(userId);
		 return abstractTaxReportDAO.findAvailable1099s(user.getCompany().getId());
	 }

	 @Override
	 public List<EarningReport> findAvailableEarningReports(long userId) throws IllegalEntityAccessException {
		 Assert.notNull(userId);
		 User user = userDAO.get(userId);
		 if (isAdminControllerOrInternal(user)) {
			 return abstractTaxReportDAO.findAvailableEarningReports(user.getCompany().getId());
		 }
		 throw new IllegalEntityAccessException("User Id: " + userId + " Can not access Earning report");
	 }

	private boolean isAdminControllerOrInternal(final User user) {
		return userRoleService.isInternalUser(user) || (userRoleService.hasAnyAclRole(user, AclRole.ACL_ADMIN, AclRole.ACL_CONTROLLER));
	}

	@Override
	public List<EarningDetailReport> findAvailableEarningDetailReportsByUserIdAndReportSetId(long userId, long earningDetailResultSetId) throws IllegalEntityAccessException {
		User user = userDAO.get(userId);
		Assert.notNull(user);
		if (isAdminControllerOrInternal(user)) {
			return abstractTaxReportDAO.findAvailableEarningDetailReportsByCompanyAndReportSetId(userId,
				user.getCompany().getId(), earningDetailResultSetId);
		}
		throw new IllegalEntityAccessException("User Id: " + userId + " Can not access Earning report");

	}


	@Override
	public List<EarningDetailReportSet> findAvailableEarningDetailReportSets(long userId) throws IllegalEntityAccessException {
		User user = userDAO.get(userId);
		Assert.notNull(user);
		if (isAdminControllerOrInternal(user)) {
			return abstractTaxReportDAO.findAvailableEarningDetailReportSets(user.getCompany().getId());
		}
		throw new IllegalEntityAccessException("User Id: " + userId + " Can not access Earning report");
	}

	@Override
	public List<TaxServiceReport> findAvailableTaxServiceDetailReports(long userId) throws IllegalEntityAccessException {
		User user = userDAO.get(userId);
		Assert.notNull(user);
		if (isAdminControllerOrInternal(user)) {
			return abstractTaxReportDAO.findAvailableTaxServiceDetailReportsByTaxEntityCompany(user.getCompany().getId());
		}
		throw new IllegalEntityAccessException("User Id: " + userId + " Can not access tax service report");
	}

	@Override
	public boolean doesUserHaveRESTaxDetailReport(long userId) throws IllegalEntityAccessException {
		User user = userDAO.get(userId);
		Assert.notNull(user);
		if (isAdminControllerOrInternal(user)) {
			return abstractTaxReportDAO.doesUserHaveRESTaxDetailReport(user.getCompany().getId());
		}
		throw new IllegalEntityAccessException("User Id: " + userId + " Can not access tax service report");
	}

	@Override
	 public Optional<TaxForm1099> get1099Form(long id) throws IllegalEntityAccessException {
		 TaxForm1099 the1099 = abstractTaxReportDAO.get(TaxForm1099.class, id);
		 return get1099Form(the1099);
	 }

	 private Optional<TaxForm1099> get1099Form(TaxForm1099 form1099) throws IllegalEntityAccessException {

		 if (form1099 == null) {
			 return Optional.absent();
		 }

		 User user = authenticationService.getCurrentUser();
		 if (!userRoleService.isInternalUser(user) && !user.getCompany().getId().equals(form1099.getCompanyId())) {
			 throw new IllegalEntityAccessException(
					 String.format("Could not access 1099 - Invalid company [taxForm1099.id=%d, companyId=%d]", form1099.getId(), user.getCompany().getId()));
		 }

		 TaxForm1099DownloadAudit audit = new TaxForm1099DownloadAudit();
		 audit.setUserId(user.getId());
		 audit.setDownloadedOn(Calendar.getInstance());
		 audit.setTaxForm1099Id(form1099.getId());

		 taxForm1099AuditDAO.saveOrUpdate(audit);

		 return Optional.fromNullable(form1099);
	 }
		
	
	 private Optional<EarningReport> getEarningReport(EarningReport earningReport) throws IllegalEntityAccessException {

		 if (earningReport == null) {
			 return Optional.absent();
		 }

		 User user = authenticationService.getCurrentUser();
		 if (!userRoleService.isInternalUser(user) && !user.getCompany().getId().equals(earningReport.getCompanyId())) {
			 throw new IllegalEntityAccessException(
					 String.format("Could not access Earning report - Invalid company [earningReport.id=%d, companyId=%d]", earningReport.getId(), user.getCompany().getId()));
		 }

		 // udpate the task number masking if we are masquerading as another user
		 updateTaxNumber(earningReport);

		 EarningReportDownloadAudit audit = new EarningReportDownloadAudit();
		 audit.setUserId(user.getId());
		 audit.setDownloadedOn(Calendar.getInstance());
		 audit.setEarningReportId(earningReport.getId());

		 earningReportAuditDAO.saveOrUpdate(audit);

		 return Optional.fromNullable(earningReport);
	 }

	 @Override
	 public Optional<EarningReport> getEarningReport(long id) throws IllegalEntityAccessException {
		 EarningReport earningReport = abstractTaxReportDAO.get(EarningReport.class, id);
		 return getEarningReport(earningReport);
	 }

	@Override
	public Optional<TaxServiceReport> getTaxServiceDetailReport(long id) throws IllegalEntityAccessException {
		TaxServiceReport taxServiceReport = abstractTaxReportDAO.get(TaxServiceReport.class, id);
		return getTaxServiceDetailReport(taxServiceReport);
	}

	private Optional<TaxServiceReport> getTaxServiceDetailReport(TaxServiceReport report) throws IllegalEntityAccessException {
		if (report == null) {
			return Optional.absent();
		}

		User user = authenticationService.getCurrentUser();
		if (!userRoleService.isInternalUser(user) && !user.getCompany().getId().equals(report.getCompanyId())) {
			throw new IllegalEntityAccessException(
					String.format("Could not access tax service report - Invalid company [TaxServiceDetailReport.id=%d, companyId=%d]", report.getId(), user.getCompany().getId()));
		}

		// now update the task number masking in the case we are masquerading
		updateTaxNumber(report);

		TaxServiceReportDownloadAudit audit = new TaxServiceReportDownloadAudit();
		audit.setUserId(user.getId());
		audit.setDownloadedOn(Calendar.getInstance());
		audit.setTaxServiceReportId(report.getId());
		taxServiceReportDownloadAuditDAO.saveOrUpdate(audit);

		return Optional.fromNullable(report);
	}

	/**
	 * The following updates the tax number stored on the tax report object itself - this is done to handle
	 * situations involving masquerading. It is a little odd but the report has a formatted masked version of the
	 * tax number while the tax entity has an unformatted clear text one, neither is exactly what we want!
	 * So here we kind of "correct" the tax number on the tax report doc masking when masquerading and then
	 * formatting for display. This way the velocity templates used to generate the docs can just use
	 * the tax number from the report rather than the formattedTaxNumber from the taxEntity (which does not
	 * allow us to mask).
	 *
	 * @param report
	 */
	private void updateTaxNumber(AbstractTaxReport report) {
		boolean masquerading = securityContextFacade.getCurrentUser().isMasquerading();

		if (report.getTaxEntity() != null && report.getTaxEntity().getTaxNumberSanitized() != null) {
			AbstractTaxEntity entity = report.getTaxEntity();
			String taxNumber = vaultHelper.get(entity, "taxNumber", entity.getTaxNumberSanitized()).getValue();

			if (report.getBusinessFlag()) {
				if (StringUtils.equals(report.getCountry(), Country.CANADA)) {
					if (masquerading) {
						report.setTaxNumber(StringUtilities.formatSecureCanadaBn(taxNumber));
					}
					else {
						report.setTaxNumber(StringUtilities.formatCanadaBn(taxNumber));
					}
				} else {
					if (masquerading) {
						report.setTaxNumber(StringUtilities.formatSecureEin(taxNumber));
					}
					else {
						report.setTaxNumber(StringUtilities.formatEin(taxNumber));
					}
				}
			} else {
				if (StringUtils.equals(report.getCountry(), Country.CANADA)) {
					if (masquerading) {
						report.setTaxNumber(StringUtilities.formatSecureCanadaSin(taxNumber));
					}
					else {
						report.setTaxNumber(StringUtilities.formatCanadaSin(taxNumber));
					}
				} else {
					if (masquerading) {
						report.setTaxNumber(StringUtilities.formatSecureSsn(taxNumber));
					}
					else {
						report.setTaxNumber(StringUtilities.formatSsn(taxNumber));
					}
				}

			}
		}
	}

	@Override
	 public TaxForm1099Set generateTaxForm1099Report(Integer year) {
		 Assert.notNull(year);
		 Assert.isTrue(year > 2011);
		 Assert.isTrue(isTax1099ReportYearAvailableToGenerate(year));

		 TaxForm1099Set taxForm1099Set = new TaxForm1099Set();
		 taxForm1099Set.setTaxYear(year);
		 taxForm1099ReportSetDAO.saveOrUpdate(taxForm1099Set);
		 eventRouter.sendEvent(eventFactory.buildTaxReportGenerationEvent(authenticationService.getCurrentUser(), taxForm1099Set));
		 return taxForm1099Set;
	 }

	 @Override
	 public EarningReportSet generateEarningsReport(Integer year) {
		 Assert.notNull(year);
		 Assert.isTrue(year > 2011);
		 Assert.isTrue(isEarningReportYearAvailableToGenerate(year));

		 EarningReportSet earningReportSet = new EarningReportSet();
		 earningReportSet.setTaxYear(year);
		 earningReportSetDAO.saveOrUpdate(earningReportSet);
		 eventRouter.sendEvent(eventFactory.buildTaxReportGenerationEvent(authenticationService.getCurrentUser(), earningReportSet));
		 return earningReportSet;
	 }

	 @Override
	 public EarningDetailReportSet generateEarningsDetailReport(Integer year) {
		 Assert.notNull(year);
		 Assert.isTrue(year > 2011);
		 Assert.isTrue(isEarningDetailReportYearAvailableToGenerate(year));

		 EarningDetailReportSet earningDetailReportSet = new EarningDetailReportSet();
		 earningDetailReportSet.setTaxYear(year);
		 earningDetailReportSetDAO.saveOrUpdate(earningDetailReportSet);
		 eventRouter.sendEvent(eventFactory.buildTaxReportGenerationEvent(authenticationService.getCurrentUser(), earningDetailReportSet));
		 return earningDetailReportSet;
	 }

	@Override
	public TaxServiceReportSet generateTaxServiceDetailReport(Integer year) {
		Assert.notNull(year);
		Assert.isTrue(year > 2011);
		Assert.isTrue(isTaxServiceDetailReportYearAvailableToGenerate(year));

		TaxServiceReportSet taxServiceReportSet = new TaxServiceReportSet();
		taxServiceReportSet.setTaxYear(year);
		taxServiceReportSetDAO.saveOrUpdate(taxServiceReportSet);
		eventRouter.sendEvent(eventFactory.buildTaxReportGenerationEvent(authenticationService.getCurrentUser(), taxServiceReportSet));
		return taxServiceReportSet;

	}

	@Override
	 public TaxForm1099Set findTaxForm1099SetById(long id) {
		 return (TaxForm1099Set) taxForm1099ReportSetDAO.get(id);
	 }

	 @Override
	 public TaxForm1099Set deleteTaxForm1099Report(long tax1099SetId) {
		 TaxForm1099Set taxForm1099Set = (TaxForm1099Set) taxForm1099ReportSetDAO.get(tax1099SetId);
		 if (taxForm1099Set != null && !taxForm1099Set.isPublished()) {
			 taxForm1099Set.setDeleted(true);
		 }
		 return taxForm1099Set;
	 }

	 @Override
	 public EarningReportSet deleteEarningReport(long earningReportSetId) {
		 EarningReportSet earningReportSet = (EarningReportSet) earningReportSetDAO.get(earningReportSetId);
		 if (earningReportSet != null && !earningReportSet.isPublished()) {
			 earningReportSet.setDeleted(true);
		 }
		 return earningReportSet;
	 }

	 @Override
	 public EarningDetailReportSet deleteEarningDetailReport(long earningDetailReportSetId) {
		 EarningDetailReportSet earningDetailReportSet = (EarningDetailReportSet) earningDetailReportSetDAO.get(earningDetailReportSetId);
		 if (earningDetailReportSet != null && !earningDetailReportSet.isPublished()) {
			 earningDetailReportSet.setDeleted(true);
		 }
		 return earningDetailReportSet;
	 }

	@Override
	public TaxServiceReportSet deleteTaxServiceDetailReport(long taxServiceReportSetId) {
		TaxServiceReportSet taxServiceReportSet = (TaxServiceReportSet) taxServiceReportSetDAO.get(taxServiceReportSetId);
		if (taxServiceReportSet != null && !taxServiceReportSet.isPublished()) {
			taxServiceReportSet.setDeleted(true);
		}
		return taxServiceReportSet;
	}

	@Override
	public TaxForm1099Set findLatestPublishedTaxForm1099Report() {
		return (TaxForm1099Set)taxForm1099ReportSetDAO.findLatestPublishedTaxReport();
	}

	@Override
	public EarningReportSet findLatestPublishedEarningReport() {
		return (EarningReportSet)earningReportSetDAO.findLatestPublishedTaxReport();
	}

	@Override
	public EarningDetailReportSet findLatestPublishedEarningDetailReport() {
		return (EarningDetailReportSet)earningDetailReportSetDAO.findLatestPublishedTaxReport();
	}

	@Override
	public TaxServiceReportSet findLatestPublishedTaxServiceReport() {
		return (TaxServiceReportSet)taxServiceReportSetDAO.findLatestPublishedTaxReport();
	}

	@Override
	public TaxForm1099Set findPublishedTaxForm1099ReportForYear(Integer year) {
		return (TaxForm1099Set)taxForm1099ReportSetDAO.findPublishedTaxReportForYear(year);
	}

	@Override
	public TaxForm1099Set publishTaxForm1099Report(long tax1099SetId) {
		TaxForm1099Set taxForm1099Set = (TaxForm1099Set) taxForm1099ReportSetDAO.get(tax1099SetId);
		Assert.notNull(taxForm1099Set);

		final int taxYear = taxForm1099Set.getTaxYear();
		Assert.isTrue(canPublishTax1099ReportForYear(taxYear), "Not allowed to publish Tax 1099 Report for " + taxYear + " tax year.");

		if (taxForm1099Set != null) {
			if (!taxForm1099Set.isPublished() && isTax1099ReportYearAvailableToGenerate(taxForm1099Set.getTaxYear())) {
				taxForm1099Set.setTaxReportSetStatusType(new TaxReportSetStatusType(TaxReportSetStatusType.PUBLISHED));
				taxForm1099Set.setPublishedOn(Calendar.getInstance());
				taxForm1099Set.setPublishedBy(authenticationService.getCurrentUser());
				// Delete all the others
				for (TaxForm1099Set deletableSet : (List<TaxForm1099Set>) taxForm1099ReportSetDAO.findAllTaxReportSetsByYear(taxYear)) {
					if (!deletableSet.getId().equals(tax1099SetId) && !deletableSet.getDeleted()) {
						deletableSet.setDeleted(true);
					}
				}
				eventRouter.sendEvent(eventFactory.buildTaxReportPublishedEvent(taxForm1099Set));
			}
			if (taxForm1099Set.isPublished()) {
				eventRouter.sendEvent(eventFactory.buildTaxReportPublishedEvent(taxForm1099Set));
			}
		}

		return taxForm1099Set;
	}

	@Override
	 public EarningReportSet publishEarningReport(long earningReportSetId) {
		 EarningReportSet earningReportSet = (EarningReportSet) earningReportSetDAO.get(earningReportSetId);
		 Assert.notNull(earningReportSet);

		 final int taxYear = earningReportSet.getTaxYear();
		 Assert.isTrue(canPublishEarningReportForYear(taxYear), "Not allowed to publish Earning Report for " + taxYear + " tax year.");

		 if (!earningReportSet.isPublished()) {
			 earningReportSet.setTaxReportSetStatusType(new TaxReportSetStatusType(TaxReportSetStatusType.PUBLISHED));
			 earningReportSet.setPublishedOn(Calendar.getInstance());
			 earningReportSet.setPublishedBy(authenticationService.getCurrentUser());
			 // Delete all the others
			 for (EarningReportSet deletableSet : (List<EarningReportSet>) earningReportSetDAO.findAllTaxReportSetsByYear(taxYear)) {
				 if (!deletableSet.getId().equals(earningReportSetId) && !deletableSet.getDeleted()) {
					 deletableSet.setDeleted(true);
				 }
			 }
			 eventRouter.sendEvent(eventFactory.buildTaxReportPublishedEvent(earningReportSet));
		 }
		 return earningReportSet;
	 }

	 @Override
	 public EarningDetailReportSet publishEarningDetailReport(long earningDetailReportSetId) {
		 EarningDetailReportSet earningDetailReportSet = (EarningDetailReportSet) earningDetailReportSetDAO.get(earningDetailReportSetId);
		 Assert.notNull(earningDetailReportSet);

		 final int taxYear = earningDetailReportSet.getTaxYear();
		 Assert.isTrue(canPublishEarningDetailReportForYear(taxYear), "Not allowed to publish Earning Detail Report for " + taxYear + " tax year.");

		 if (!earningDetailReportSet.isPublished()) {
			 earningDetailReportSet.setTaxReportSetStatusType(new TaxReportSetStatusType(TaxReportSetStatusType.PUBLISHED));
			 earningDetailReportSet.setPublishedOn(Calendar.getInstance());
			 earningDetailReportSet.setPublishedBy(authenticationService.getCurrentUser());
			 // Delete all the others
			 for (EarningDetailReportSet deletableSet : (List<EarningDetailReportSet>) earningDetailReportSetDAO.findAllTaxReportSetsByYear(taxYear)) {
				 if (!deletableSet.getId().equals(earningDetailReportSetId) && !deletableSet.getDeleted()) {
					 deletableSet.setDeleted(true);
				 }
			 }
		 }
		 return earningDetailReportSet;
	 }

	@Override
	public TaxServiceReportSet publishTaxServiceDetailReport(long taxServiceReportSetId) {
		TaxServiceReportSet taxServiceReportSet = (TaxServiceReportSet) taxServiceReportSetDAO.get(taxServiceReportSetId);
		Assert.notNull(taxServiceReportSet);

		final int taxYear = taxServiceReportSet.getTaxYear();
		Assert.isTrue(canPublishTaxServiceDetailReportForYear(taxYear), "Not allowed to publish Tax Service Detail Report for " + taxYear + " tax year.");

		if (!taxServiceReportSet.isPublished()) {
			taxServiceReportSet.setTaxReportSetStatusType(new TaxReportSetStatusType(TaxReportSetStatusType.PUBLISHED));
			taxServiceReportSet.setPublishedOn(Calendar.getInstance());
			taxServiceReportSet.setPublishedBy(authenticationService.getCurrentUser());
			// Delete all the others
			for (TaxServiceReportSet deletableSet : (List<TaxServiceReportSet>) taxServiceReportSetDAO.findAllTaxReportSetsByYear(taxYear)) {
				if (!deletableSet.getId().equals(taxServiceReportSetId) && !deletableSet.getDeleted()) {
					deletableSet.setDeleted(true);
				}
			}
		}
		return taxServiceReportSet;
	}

	 @Override
	 public List<TaxForm1099Set> findAllTaxForm1099Reports() {
		 return taxForm1099ReportSetDAO.findAllTaxReportSets();
	 }

	 @Override
	 public TaxForm1099Set findTaxForm1099Set(final long setId) {
		 return (TaxForm1099Set) taxForm1099ReportSetDAO.get(setId);
	 }

	 @Override
	 public List<EarningReportSet> findAllEarningReportReports() {
		 return earningReportSetDAO.findAllTaxReportSets();
	 }

	 @Override
	 public EarningReportSet findEarningReportSet(final long setId) {
		 return (EarningReportSet) earningReportSetDAO.get(setId);
	 }

	 @Override
	 public List<EarningDetailReportSet> findAllEarningDetailReports() {
		 return earningDetailReportSetDAO.findAllTaxReportSets();
	 }

	 @Override
	 public EarningDetailReportSet findEarningDetailReportSet(final long setId) {
		 return (EarningDetailReportSet) earningDetailReportSetDAO.get(setId);
	 }
	
	 @Override
	 public List<TaxServiceReportSet> findAllTaxServiceDetailReports() {
		 return taxServiceReportSetDAO.findAllTaxReportSets();
	 }

	 @Override
	 public TaxServiceReportSet findTaxServiceDetailReportSet(final long setId) {
		 return (TaxServiceReportSet) taxServiceReportSetDAO.get(setId);
	 }

	 @Override
	 public Optional<EarningReport> findEarningReportForUser(long userId, Integer year) throws IllegalEntityAccessException {
		 User user = userDAO.findUserById(userId);
		 if (user != null) {
			 EarningReport earningReport = abstractTaxReportDAO.findEarningReportForUser(user.getCompany().getId(), year);
			 return getEarningReport(earningReport);
		 }
		 return Optional.absent();
	 }

	 @Override
	 public boolean isTax1099ReportYearAvailableToGenerate(int taxYear) {
		 return abstractTaxReportDAO.isTax1099ReportYearAvailable(taxYear);
	 }

	@Override
	public boolean canPublishTax1099ReportForYear(final int taxYear) {
		return hasCalendarTaxYearBeenCompleted(taxYear) && isTax1099ReportYearAvailableToGenerate(taxYear);
	}

	 @Override
	 public boolean isEarningReportYearAvailableToGenerate(int taxYear) {
		 return abstractTaxReportDAO.isEarningReportYearAvailable(taxYear);
	 }

	@Override
	public boolean canPublishEarningReportForYear(final int taxYear) {
		return hasCalendarTaxYearBeenCompleted(taxYear) && isEarningReportYearAvailableToGenerate(taxYear);
	}

	 @Override
	 public boolean isEarningDetailReportYearAvailableToGenerate(int taxYear) {
		 return abstractTaxReportDAO.isEarningDetailReportYearAvailable(taxYear);
	 }

	@Override
	public boolean canPublishEarningDetailReportForYear(final int taxYear) {
		return hasCalendarTaxYearBeenCompleted(taxYear) && isEarningDetailReportYearAvailableToGenerate(taxYear);
	}

	@Override
	public boolean isTaxServiceDetailReportYearAvailableToGenerate(int taxYear) {
		return abstractTaxReportDAO.isTaxServiceDetailReportYearAvailable(taxYear);
	 }

	 @Override
	 public boolean canPublishTaxServiceDetailReportForYear(final int taxYear) {
		return hasCalendarTaxYearBeenCompleted(taxYear) && isTaxServiceDetailReportYearAvailableToGenerate(taxYear);
	 }

	 @Override
	 public List<TaxForm1099> findAllTaxForm1099ByTaxForm1099SetId(long id) {
		 return abstractTaxReportDAO.findAllTaxForm1099ByTaxForm1099SetId(id);
	 }

	@Override
	public List<TaxForm1099> findAllTaxForm1099ForReportGeneration(long id) {
		return abstractTaxReportDAO.findAllTaxForm1099NoHibernate(id);
	}

	@Override
	public List<TaxForm1099> findAllUndownloadedTaxForm1099ByTaxForm1099SetId(long id) {
		return taxForm1099DownloadAuditDAO.findAllUndownloadedTaxForm1099ByTaxForm1099SetId(id);
	}

	@Override
	 public List<EarningReport> findAllEarningReportByEarningReportSetId(long id) {
		 return abstractTaxReportDAO.findAllEarningReportByEarningReportSetId(id);
	 }

	@Override
	public List<EarningReport> findAllEarningReportForReportGeneration(long id) {
		return abstractTaxReportDAO.findAllEarningReportsNoHibernate(id);
	}

	 @Override
	 public List<EarningDetailReport> findAllEarningDetailReportByEarningReportSetId(long id) {
		 return abstractTaxReportDAO.findAllEarningDetailReportByEarningReportSetId(id);
	 }

	@Override
	public List<EarningDetailReport> findAllEarningDetailReportForReportGeneration(long id) {
		return abstractTaxReportDAO.findAllEarningDetailReportsNoHibernate(id);
	}

	@Override
	public List<TaxServiceReport> findAllTaxServiceReportByReportSetId(long id) {
		return abstractTaxReportDAO.findAllTaxServiceReportByReportSetId(id);
	 }

	@Override
	public List<TaxServiceReport> findAllTaxServiceReportForReportGeneration(long id) {
		return abstractTaxReportDAO.findAllTaxServiceReportsNoHibernate(id);
	}

	@Override
	public List<? extends AbstractTaxReport> getAllReports(final Calendar fromCreatedOn) {
		return abstractTaxReportDAO.getAllReports(fromCreatedOn);
	}

	@Override
	 public String getEarningReportPdfView(long earningReportId) throws IllegalEntityAccessException {
		 Optional<EarningReport> earningReportOpt = getEarningReport(earningReportId);
		 if (earningReportOpt.isPresent()) {
			 PDFTemplate pdfTemplate = PDFTemplateFactory.earningReportPDFTemplate(earningReportOpt.get());
			 if (pdfTemplate != null) {
				 return templateService.renderPDFTemplate(pdfTemplate);
			 }
		 }
		 return StringUtils.EMPTY;
	 }

	 @Override
	 public <T extends AbstractTaxReport> T saveTaxReport(T taxReport) {
		 Assert.notNull(taxReport);
		 abstractTaxReportDAO.saveOrUpdate(taxReport);
		 return taxReport;
	 }

	 @Override
	 public <T extends AbstractTaxReportSet> T saveTaxReportSet(T taxReportSet) {
		 Assert.notNull(taxReportSet);
		 if (taxReportSet instanceof EarningReportSet) {
			 earningReportSetDAO.saveOrUpdate(taxReportSet);
		 } else if (taxReportSet instanceof EarningDetailReportSet) {
			 earningDetailReportSetDAO.saveOrUpdate(taxReportSet);
		 } else if (taxReportSet instanceof TaxForm1099Set){
			 taxForm1099ReportSetDAO.saveOrUpdate(taxReportSet);
		 } else if (taxReportSet instanceof TaxServiceReportSet) {
			 taxServiceReportSetDAO.saveOrUpdate(taxReportSet);
		 }
		 return taxReportSet;
	 }

	 @Override
	 public List<TaxReportRow> getAllCompaniesWithEarningsInPeriod(DateRange dateRange, BigDecimal amountThreshold, List<String> accountServiceType) {
		 Assert.notNull(dateRange);
		 return abstractTaxReportDAO.getAllCompaniesWithEarningsInPeriod(dateRange, amountThreshold, accountServiceType);
	 }

	 @Override
	 public List<TaxReportDetailRow> getAllCompaniesWithEarningsDetailInPeriod(DateRange dateRange, BigDecimal amountThreshold, List<String> accountServiceType) {
		 Assert.notNull(dateRange);
		 return abstractTaxReportDAO.getAllCompaniesWithEarningsInPeriodByBuyerCompany(dateRange, amountThreshold, accountServiceType);
	 }


	 @Override
	 public TaxReportRow getEarningsInPeriodByCompany(Calendar fromDate, Calendar toDate, long companyId, List<String> accountServiceType) {
		 return abstractTaxReportDAO.getEarningsInPeriodByCompany(fromDate, toDate, companyId, accountServiceType);
	 }

	 @Override
	 public List<EarningDetailReportRow> getEarningDetailReportForUserInYear(Long userId, int year) {
		 Assert.notNull(userId);
		 User user = userDAO.findUserById(userId);
		 Assert.notNull(user);
		 return abstractTaxReportDAO.getEarningDetailReportForUserInYear(user.getCompany().getId(), year);
	 }

	@Override
	public List<TaxReportDetailRow> getTaxDetailReportForUserInYear(Long userId, int year) {
		Assert.notNull(userId);
		User user = userDAO.findUserById(userId);
		Assert.notNull(user);
		return abstractTaxReportDAO.getTaxDetailReportForUserInYear(user.getCompany().getId(), year);
	}

	@Override
	public TaxReportDetailRow getEarningInPeriodByCompanyAndBuyerCompany(Calendar startDate, Calendar endDate, long resourceCompanyId, long buyerCompanyId, List<String> accountServiceType) {
		return abstractTaxReportDAO.getEarningsInPeriodByCompanyAndBuyerCompany(new DateRange(startDate, endDate), resourceCompanyId, buyerCompanyId, accountServiceType);
	}

	@Override
	public Set<Long> findAllCompaniesWithNonVorEarningsDetailsForCompany(DateRange dateRange, long resourceCompanyId, List<String> accountServiceType) {
		return abstractTaxReportDAO.findAllCompaniesWithNonVorEarningsDetailsForCompany(dateRange, resourceCompanyId, accountServiceType);
	}

	@Override
	public Optional<TaxServiceReport> getRESTaxServiceReportForYear(int year, Long companyId) {
		return abstractTaxReportDAO.getRESTaxServiceReportForYear(year, companyId);
	}

	private boolean hasCalendarTaxYearBeenCompleted(final long taxYear) {
		return DateUtilities.getCalendarNow().get(Calendar.YEAR) > taxYear;
	}
}
