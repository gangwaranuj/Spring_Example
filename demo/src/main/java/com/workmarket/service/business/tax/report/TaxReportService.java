package com.workmarket.service.business.tax.report;

import com.google.common.base.Optional;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.tax.*;
import com.workmarket.service.exception.IllegalEntityAccessException;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;


public interface TaxReportService {

	/**
	 * For a particular user, finds the available 1099 forms to download.
	 * @param userId
	 * @return
	 */
	List<TaxForm1099> findAvailable1099s(long userId);

	Optional<TaxForm1099> get1099Form(long id) throws IllegalEntityAccessException;

	Optional<EarningReport> getEarningReport(long id) throws IllegalEntityAccessException;

	Optional<TaxServiceReport> getTaxServiceDetailReport(long id) throws IllegalEntityAccessException;

	/*
		TAX REPORT GENERATION PROCESSES (they only send an event to the queue)
	 */
	TaxForm1099Set generateTaxForm1099Report(Integer year);

	EarningReportSet generateEarningsReport(Integer year);

	EarningDetailReportSet generateEarningsDetailReport(Integer year);

	TaxServiceReportSet generateTaxServiceDetailReport(Integer year);

	/*
    	SOFT DELETION METHODS
	 */
	TaxForm1099Set deleteTaxForm1099Report(long tax1099SetId);

	EarningReportSet deleteEarningReport(long earningReportSetId);

	EarningDetailReportSet deleteEarningDetailReport(long earningDetailReportSetId);

	TaxServiceReportSet deleteTaxServiceDetailReport(long taxServiceReportSetId);

	/*
		PUBLISH METHODS (When Liz clicks publish)
	 */
	TaxForm1099Set publishTaxForm1099Report(long tax1099SetId);

	TaxForm1099Set findPublishedTaxForm1099ReportForYear(Integer year);

	EarningReportSet publishEarningReport(long earningReportSetId);

	EarningDetailReportSet publishEarningDetailReport(long earningDetailReportSetId);

	TaxServiceReportSet publishTaxServiceDetailReport(long taxServiceReportSetId);

	List<TaxForm1099Set> findAllTaxForm1099Reports();

	TaxForm1099Set findTaxForm1099Set(final long setId);

	List<EarningReportSet> findAllEarningReportReports();

	EarningReportSet findEarningReportSet(final long setId);

	List<EarningDetailReportSet> findAllEarningDetailReports();

	EarningDetailReportSet findEarningDetailReportSet(final long setId);

	List<TaxServiceReportSet> findAllTaxServiceDetailReports();

	TaxServiceReportSet findTaxServiceDetailReportSet(final long setId);

	Optional<EarningReport> findEarningReportForUser(long userId, Integer year) throws IllegalEntityAccessException;

	List<TaxForm1099> findAllTaxForm1099ByTaxForm1099SetId(long id);

	List<TaxForm1099> findAllTaxForm1099ForReportGeneration(final long id);

	List<TaxForm1099> findAllUndownloadedTaxForm1099ByTaxForm1099SetId(long id);

	List<EarningReport> findAllEarningReportByEarningReportSetId(long id);

	List<EarningReport> findAllEarningReportForReportGeneration(final long id);

	List<EarningDetailReport> findAllEarningDetailReportByEarningReportSetId(long id);

	List<EarningDetailReport> findAllEarningDetailReportForReportGeneration(final long id);

	List<TaxServiceReport> findAllTaxServiceReportByReportSetId(long id);

	List<TaxServiceReport> findAllTaxServiceReportForReportGeneration(final long id);

	List<? extends AbstractTaxReport> getAllReports(Calendar fromCreatedOn);

	TaxForm1099Set findTaxForm1099SetById(long id);

	/*
		VALIDATION METHODS
	 */
	/**
	 * Validates that only one 1099 report per year can be issue
	 * @param taxYear
	 * @return
	 */
	boolean isTax1099ReportYearAvailableToGenerate(int taxYear);

	boolean canPublishTax1099ReportForYear(int taxYear);

	boolean isEarningReportYearAvailableToGenerate(int taxYear);

	boolean canPublishEarningReportForYear(int taxYear);

	boolean isEarningDetailReportYearAvailableToGenerate(int taxYear);

	boolean canPublishEarningDetailReportForYear(int taxYear);

	boolean isTaxServiceDetailReportYearAvailableToGenerate(int taxYear);

	boolean canPublishTaxServiceDetailReportForYear(int taxYear);

	/**
	 * Finds all the earning reports for a user.
	 *
	 * @param userId
	 * @return
	 * @throws IllegalEntityAccessException
	 */
	List<EarningReport> findAvailableEarningReports(long userId) throws IllegalEntityAccessException;

	List<EarningDetailReport> findAvailableEarningDetailReportsByUserIdAndReportSetId(long userId, long earningDetailResultSetId) throws IllegalEntityAccessException;

	List<EarningDetailReportSet> findAvailableEarningDetailReportSets(long userId) throws IllegalEntityAccessException;

	List<TaxServiceReport> findAvailableTaxServiceDetailReports(long userId) throws IllegalEntityAccessException;

	boolean doesUserHaveRESTaxDetailReport(long userId) throws IllegalEntityAccessException;

	String getEarningReportPdfView(long earningReportId) throws IllegalEntityAccessException;

	<T extends AbstractTaxReport> T saveTaxReport(T taxReport);

	<T extends AbstractTaxReportSet> T saveTaxReportSet(T taxReportSet);

	List<TaxReportRow> getAllCompaniesWithEarningsInPeriod(DateRange dateRange, BigDecimal amountThreshold, List<String> accountServiceType);

	List<TaxReportDetailRow> getAllCompaniesWithEarningsDetailInPeriod(DateRange dateRange, BigDecimal amountThreshold, List<String> accountServiceType);

	TaxReportRow getEarningsInPeriodByCompany(Calendar fromDate, Calendar toDate, long companyId, List<String> accountServiceType);

	List<EarningDetailReportRow> getEarningDetailReportForUserInYear(Long userId, int year);

	List<TaxReportDetailRow> getTaxDetailReportForUserInYear(Long userId, int year);

	TaxReportDetailRow getEarningInPeriodByCompanyAndBuyerCompany(Calendar startDate, Calendar endDate, long resourceCompanyId, long buyerCompanyId, List<String> accountServiceType);

	Set<Long> findAllCompaniesWithNonVorEarningsDetailsForCompany(DateRange dateRange, long resourceCompanyId, List<String> accountServiceType);

	Optional<TaxServiceReport> getRESTaxServiceReportForYear(int year, Long companyId);

	TaxForm1099Set findLatestPublishedTaxForm1099Report();

	EarningReportSet findLatestPublishedEarningReport();

	EarningDetailReportSet findLatestPublishedEarningDetailReport();

	TaxServiceReportSet findLatestPublishedTaxServiceReport();
}
