package com.workmarket.dao.tax;

import com.google.common.base.Optional;
import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.EarningDetailReport;
import com.workmarket.domains.model.tax.EarningDetailReportRow;
import com.workmarket.domains.model.tax.EarningDetailReportSet;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.domains.model.tax.TaxForm1099;
import com.workmarket.domains.model.tax.TaxReportDetailRow;
import com.workmarket.domains.model.tax.TaxReportRow;
import com.workmarket.domains.model.tax.TaxServiceReport;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface AbstractTaxReportDAO extends DAOInterface<AbstractTaxReport> {

	List<TaxForm1099> findAvailable1099s(Long companyId);
	
	boolean isTax1099ReportYearAvailable(int taxYear);

	boolean isEarningReportYearAvailable(int taxYear);

	boolean isEarningDetailReportYearAvailable(int taxYear);

	boolean isTaxServiceDetailReportYearAvailable(int taxYear);

	<T extends AbstractTaxReport> T get(Class clazz, long id);


	List<TaxReportDetailRow> getAllCompaniesWithEarningsInPeriodByBuyerCompany(DateRange dateRange, BigDecimal amountThreshold, List<String> accountServiceType);

	TaxReportDetailRow getEarningsInPeriodByCompanyAndBuyerCompany(DateRange dateRange, long resourceCompanyId, long buyerCompanyId, List<String> accountServiceType);

	Set<Long> findAllCompaniesWithNonVorEarningsDetailsForCompany(DateRange dateRange, long resourceCompanyId, List<String> accountServiceType);

	List<TaxReportRow> getAllCompaniesWithEarningsInPeriod(DateRange dateRange, BigDecimal amountThreshold, List<String> accountServiceType);

	TaxReportRow getEarningsInPeriodByCompany(Calendar fromDate, Calendar toDate, long companyId, List<String> accountServiceType);

	/*
		Find methods
	 */
	EarningReport findEarningReportForUser(long companyId, Integer year);

	List<TaxForm1099> findAllTaxForm1099ByTaxForm1099SetId(long taxForm1099SetId);
	
	List<EarningReport> findAllEarningReportByEarningReportSetId(long taxForm1099SetId);

	List<EarningDetailReport> findAllEarningDetailReportByEarningReportSetId(long earningDetailReportSetId);

	List<TaxServiceReport> findAllTaxServiceReportByReportSetId(long taxServiceReportSetId);

	List<EarningReport> findAvailableEarningReports(Long companyId);

	List<EarningDetailReport> findAvailableEarningDetailReportsByCompanyAndReportSetId(Long userId, Long companyId, Long earningDetailResultSetId);

	List<EarningDetailReportSet> findAvailableEarningDetailReportSets(Long companyId);

	List<TaxServiceReport> findAvailableTaxServiceDetailReports(Long companyId);

	List<TaxServiceReport> findAvailableTaxServiceDetailReportsByTaxEntityCompany(Long companyId);

	boolean doesUserHaveRESTaxDetailReport(Long companyId);

	Optional<TaxServiceReport> getRESTaxServiceReportForYear(int year, Long companyId);

	List<EarningDetailReportRow> getEarningDetailReportForUserInYear(Long companyId, int year);

	List<TaxReportDetailRow> getTaxDetailReportForUserInYear(Long companyId, int year);

	List<TaxForm1099> findAllTaxForm1099NoHibernate(final long taxForm1099SetId);

	List<EarningReport> findAllEarningReportsNoHibernate(final long earningReportSetId);

	List<EarningDetailReport> findAllEarningDetailReportsNoHibernate(final long earningDetailReportSetId);

	List<TaxServiceReport> findAllTaxServiceReportsNoHibernate(final long taxServiceReportSetId);

	List<? extends AbstractTaxReport> getAllReports(Calendar fromCreateDate);
}