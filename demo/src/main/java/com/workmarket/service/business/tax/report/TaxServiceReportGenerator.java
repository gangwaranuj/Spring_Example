package com.workmarket.service.business.tax.report;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.tax.*;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhe on 12/30/14.
 */
@Component
@Scope(value = "prototype")
public class TaxServiceReportGenerator extends TaxReportGeneratorImpl<TaxServiceReportSet> implements TaxReportGenerator<TaxServiceReportSet> {

	private static final Log logger = LogFactory.getLog(TaxServiceReportGenerator.class);

	@Override
	List<TaxReportDetailRow> getAllCompaniesWithEarningsInPeriod() {
		Assert.notNull(taxReportSet);
		Assert.notNull(taxReportDateRange);
		return taxReportService.getAllCompaniesWithEarningsDetailInPeriod(taxReportDateRange, Constants.TAX_FORM_1099_GENERATION_AMOUNT_THRESHOLD, AccountServiceType.TAX_SERVICE_1099_SERVICE_TYPE);
	}

	@Override
	@Async
	<E extends AbstractTaxEntity> void saveTaxReport(long companyId, DateRange dateRange, TaxReportRow taxReport, E taxEntity) {
		Assert.notNull(taxReportSet);
		Assert.notNull(taxReport);
		Assert.notNull(dateRange);
		Assert.notNull(taxEntity);
		TaxReportDetailRow taxReportDetail = (TaxReportDetailRow) taxReport;
		BigDecimal expenses = workNegotiationService.findTotalAdditionalExpensesPaidToCompanyByBuyer(companyId, taxReportDetail.getBuyerCompanyId(), dateRange, AccountServiceType.TAX_SERVICE_1099_SERVICE_TYPE);
		BigDecimal earnings = taxReport.getItemizedTotal();

		TaxServiceReport taxServiceReport = new TaxServiceReport();
		copyTaxEntityPropertiesToTaxReport(taxServiceReport, taxEntity);
		taxServiceReport.setEarnings(earnings);
		taxServiceReport.setBuyerCompanyId(taxReportDetail.getBuyerCompanyId());
		taxServiceReport.setExpenses(expenses.negate());
		taxServiceReport.setMarketingPayments(taxReport.getMarketingPayments());
		taxServiceReport.setPaymentReversals(taxReport.getPaymentReversals());
		taxServiceReport.setWorkPayments(taxReport.getWorkPayments());
		taxServiceReport.setTaxYear(taxReportSet.getTaxYear().toString());
		taxServiceReport.setTaxServiceReportSet(taxReportSet);

		if(StringUtils.isNotBlank(taxServiceReport.getLastName())) {
			taxReportService.saveTaxReport(taxServiceReport);
		} else {
			logger.warn("skipping over Tax Service Report for company with id: " + companyId);
		}

	}

	@Override
	@Async
	void processCompaniesWithFirstTimeTaxEntity() {
		Assert.notNull(taxReportDateRange);

		//Companies with first tax entity in period
		for(Long resourceCompanyId: companiesWithFirstTaxEntityInPeriod) {
			DateRange earningsDateRange = new DateRange(DateUtilities.WM_EPOCH_CALENDAR, taxReportDateRange.getThrough());
			Set<Long> buyerCompanies = taxReportService.findAllCompaniesWithNonVorEarningsDetailsForCompany(earningsDateRange, resourceCompanyId, AccountServiceType.TAX_SERVICE_1099_SERVICE_TYPE);

			if (companiesWithMultipleTaxEntities.contains(resourceCompanyId)) {

				Map<? extends AbstractTaxEntity, DateRange> taxEntityDateRangeMap = findAllApprovedTaxEntitiesDateRangesByCompany(resourceCompanyId);
				for (Long buyerCompany: buyerCompanies) {
					splitTaxReportByBuyerCompany(resourceCompanyId, buyerCompany, taxEntityDateRangeMap);
				}

			} else {
				AbstractTaxEntity activeTaxEntity = findTaxEntityForTaxReport(resourceCompanyId);
				if (activeTaxEntity != null) {
					//get the amount earned even before this tax year
					for (Long buyerCompany: buyerCompanies) {
						TaxReportDetailRow earnings = taxReportService.getEarningInPeriodByCompanyAndBuyerCompany(earningsDateRange.getFrom(), earningsDateRange.getThrough(), resourceCompanyId, buyerCompany, AccountServiceType.TAX_SERVICE_1099_SERVICE_TYPE);
						saveTaxReport(resourceCompanyId, earningsDateRange, earnings, activeTaxEntity);
					}
				}
			}
		}
	}

	@Override
	@Async
	void processCompaniesWithEarningsThisYear() {
		Assert.notNull(taxReportDateRange);

		for (TaxReportDetailRow reportRow : getAllCompaniesWithEarningsInPeriod()) {
			long resourceCompanyId = reportRow.getCompanyId();
			if (!companiesWithFirstTaxEntityInPeriod.contains(resourceCompanyId)) {
				if (companiesWithMultipleTaxEntities.contains(resourceCompanyId)) {

					Map<? extends AbstractTaxEntity, DateRange> taxEntityDateRangeMap = findAllApprovedTaxEntitiesDateRangesByCompany(resourceCompanyId);
					splitTaxReportByBuyerCompany(resourceCompanyId, reportRow.getBuyerCompanyId(), taxEntityDateRangeMap);

				} else {
					AbstractTaxEntity activeTaxEntity = findTaxEntityForTaxReport(resourceCompanyId);
					if (activeTaxEntity != null) {
						saveTaxReport(resourceCompanyId, taxReportDateRange, reportRow, activeTaxEntity);
					}
				}
			}
		}
	}

	@Async
	void splitTaxReportByBuyerCompany(long resourceCompanyId, long buyerCompanyId, Map<? extends AbstractTaxEntity, DateRange> taxEntityDateRangeMap) {
		if (MapUtils.isEmpty(taxEntityDateRangeMap)) {
			return;
		}
		Calendar startDate = DateUtilities.cloneCalendar(taxReportDateRange.getFrom());
		if (companiesWithFirstTaxEntityInPeriod.contains(resourceCompanyId)) {
			startDate = DateUtilities.WM_EPOCH_CALENDAR;
		}

		for (Map.Entry<? extends AbstractTaxEntity, DateRange> entry : taxEntityDateRangeMap.entrySet()) {
			DateRange taxEntityDateRange = entry.getValue();

			if (taxEntityDateRange.getFrom() != null) {
				if (taxEntityDateRange.getFrom().before(taxReportDateRange.getThrough()) && taxEntityDateRange.getThrough().after(startDate)) {
					Calendar endDate;
					if (taxEntityDateRange.getThrough().after(taxReportDateRange.getThrough())) {
						endDate = DateUtilities.cloneCalendar(taxReportDateRange.getThrough());
					} else {
						endDate = DateUtilities.cloneCalendar(taxEntityDateRange.getThrough());
					}
					TaxReportDetailRow earnings = taxReportService.getEarningInPeriodByCompanyAndBuyerCompany(startDate, endDate, resourceCompanyId, buyerCompanyId, AccountServiceType.TAX_SERVICE_1099_SERVICE_TYPE);
					saveTaxReport(resourceCompanyId, new DateRange(startDate, endDate), earnings, entry.getKey());
					startDate = DateUtilities.cloneCalendar(taxEntityDateRange.getThrough());
				}
			}
		}
	}

	@Override
	TaxReportRow getEarningsInPeriodByCompany(Calendar fromDate, Calendar toDate, long companyId) {
		return taxReportService.getEarningsInPeriodByCompany(fromDate, toDate, companyId, AccountServiceType.TAX_SERVICE_1099_SERVICE_TYPE);
	}

	@Override
	UsaTaxEntity findTaxEntityForTaxReport(long companyId) {
		logger.info("[tax] findTaxEntityForTaxReport " + companyId);
		Assert.notNull(taxReportDateRange);
		Assert.notNull(taxReportDateRange.getThrough());
		AbstractTaxEntity activeTaxEntity = taxService.findActiveTaxEntityByCompanyForEarningReport(companyId, taxReportDateRange.getThrough());
		if (activeTaxEntity != null && AbstractTaxEntity.COUNTRY_USA.equals(activeTaxEntity.getCountry()) && activeTaxEntity.getStatus().isApproved()) {
			logger.info("[tax] found tax entity " + activeTaxEntity.getId());
			return (UsaTaxEntity) activeTaxEntity;
		}
		return null;
	}

	@Override
	List<UsaTaxEntity> getApprovedTaxEntitiesByCompanyId(long companyId) {
		return taxService.findAllUsaApprovedTaxEntitiesByCompanyId(companyId);
	}
}
