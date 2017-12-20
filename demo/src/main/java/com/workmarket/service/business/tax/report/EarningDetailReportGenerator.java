package com.workmarket.service.business.tax.report;

import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.EarningDetailReport;
import com.workmarket.domains.model.tax.EarningDetailReportSet;
import com.workmarket.domains.model.tax.TaxReportDetailRow;
import com.workmarket.domains.model.tax.TaxReportRow;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
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
 * User: iloveopt
 * Date: 1/8/14
 */

@Component
@Scope(value = "prototype")
public class EarningDetailReportGenerator extends TaxReportGeneratorImpl<EarningDetailReportSet> implements TaxReportGenerator<EarningDetailReportSet> {

	private static final Log logger = LogFactory.getLog(EarningDetailReportGenerator.class);

	@SuppressWarnings("unchecked") @Override
	List<TaxReportDetailRow> getAllCompaniesWithEarningsInPeriod() {
		Assert.notNull(taxReportSet);
		Assert.notNull(taxReportDateRange);
		return taxReportService.getAllCompaniesWithEarningsDetailInPeriod(taxReportDateRange, null, AccountServiceType.NON_VOR_SERVICE_TYPES);
	}

	@Override
	<E extends AbstractTaxEntity> void saveTaxReport(long companyId, DateRange dateRange, TaxReportRow taxReport, E taxEntity) {
		Assert.notNull(taxReportSet);
		Assert.notNull(taxReport);
		Assert.notNull(dateRange);
		Assert.notNull(taxEntity);
		TaxReportDetailRow taxReportDetail = (TaxReportDetailRow)taxReport;
		BigDecimal expenses = workNegotiationService.findTotalAdditionalExpensesPaidToCompanyByBuyer(companyId, taxReportDetail.getBuyerCompanyId(), dateRange, AccountServiceType.NON_VOR_SERVICE_TYPES);
		BigDecimal earnings = taxReport.getItemizedTotal();

		if (NumberUtilities.isPositive(earnings)) {
			EarningDetailReport earningDetailReport = new EarningDetailReport();
			copyTaxEntityPropertiesToTaxReport(earningDetailReport, taxEntity);
			earningDetailReport.setEarnings(earnings);
			earningDetailReport.setBuyerCompanyId(taxReportDetail.getBuyerCompanyId());
			earningDetailReport.setExpenses(expenses.negate());
			earningDetailReport.setMarketingPayments(taxReport.getMarketingPayments());
			earningDetailReport.setPaymentReversals(taxReport.getPaymentReversals());
			earningDetailReport.setWorkPayments(taxReport.getWorkPayments());
			earningDetailReport.setTaxYear(taxReportSet.getTaxYear().toString());
			earningDetailReport.setEarningDetailReportSet(taxReportSet);

			if(StringUtils.isNotBlank(earningDetailReport.getLastName())) {
				taxReportService.saveTaxReport(earningDetailReport);
			} else {
				logger.warn("skipping over Earnings Detail Report for company with id: " + companyId);
			}

		}
	}

	@Override
	@Async
	void processCompaniesWithFirstTimeTaxEntity() {
		Assert.notNull(taxReportDateRange);

		//Companies with first tax entity in period
		for(Long resourceCompanyId: companiesWithFirstTaxEntityInPeriod) {
			DateRange earningsDateRange = new DateRange(DateUtilities.WM_EPOCH_CALENDAR, taxReportDateRange.getThrough());
			Set<Long> buyerCompanies = taxReportService.findAllCompaniesWithNonVorEarningsDetailsForCompany(earningsDateRange, resourceCompanyId, AccountServiceType.NON_VOR_SERVICE_TYPES);

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
						TaxReportDetailRow earnings = taxReportService.getEarningInPeriodByCompanyAndBuyerCompany(earningsDateRange.getFrom(), earningsDateRange.getThrough(), resourceCompanyId, buyerCompany, AccountServiceType.NON_VOR_SERVICE_TYPES);
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
					TaxReportDetailRow earnings = taxReportService.getEarningInPeriodByCompanyAndBuyerCompany(startDate, endDate, resourceCompanyId, buyerCompanyId, AccountServiceType.NON_VOR_SERVICE_TYPES);
					saveTaxReport(resourceCompanyId, new DateRange(startDate, endDate), earnings, entry.getKey());
					startDate = DateUtilities.cloneCalendar(taxEntityDateRange.getThrough());
				}
			}
		}
	}

	@Override
	TaxReportDetailRow getEarningsInPeriodByCompany(Calendar fromDate, Calendar toDate, long companyId) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	AbstractTaxEntity findTaxEntityForTaxReport(long companyId) {
		Assert.notNull(taxReportDateRange);
		Assert.notNull(taxReportDateRange.getThrough());
		AbstractTaxEntity taxEntity = taxService.findActiveTaxEntityByCompanyForEarningReport(companyId, taxReportDateRange.getThrough());
		if (taxEntity != null && !taxEntity.getStatus().isRejected()) {
			return taxEntity;
		}
		return null;
	}

	@Override
	<E extends AbstractTaxEntity> List<E> getApprovedTaxEntitiesByCompanyId(long companyId) {
		return taxService.findAllApprovedTaxEntitiesByCompanyId(companyId);
	}
}
