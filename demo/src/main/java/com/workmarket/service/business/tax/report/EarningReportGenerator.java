package com.workmarket.service.business.tax.report;

import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.domains.model.tax.EarningReportSet;
import com.workmarket.domains.model.tax.TaxReportRow;
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

/**
 * Author: rocio
 */
@Component
@Scope(value = "prototype")
public class EarningReportGenerator extends TaxReportGeneratorImpl<EarningReportSet> implements TaxReportGenerator<EarningReportSet> {

	private static final Log logger = LogFactory.getLog(EarningReportGenerator.class);

	@SuppressWarnings("unchecked")
	@Override List<TaxReportRow> getAllCompaniesWithEarningsInPeriod() {
		Assert.notNull(taxReportSet);
		Assert.notNull(taxReportDateRange);
		return taxReportService.getAllCompaniesWithEarningsInPeriod(taxReportDateRange, null, AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES);
	}

	@Override
	@Async <E extends AbstractTaxEntity> void saveTaxReport(long companyId, DateRange dateRange, TaxReportRow taxReport, E taxEntity) {
		Assert.notNull(taxReportSet);
		Assert.notNull(taxReport);
		Assert.notNull(dateRange);
		Assert.notNull(taxEntity);
		Map<String, BigDecimal> expensesMap = workNegotiationService.findTotalAdditionalExpensesPaidToCompany(companyId, dateRange);
		BigDecimal expenses = BigDecimal.ZERO;
		BigDecimal earnings = taxReport.getItemizedTotal();
		BigDecimal nonVorExpenses = BigDecimal.ZERO;

		for (Map.Entry<String, BigDecimal> entry : expensesMap.entrySet()) {
			expenses = expenses.add(entry.getValue());
			if (AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES.contains(entry.getKey())) {
				nonVorExpenses = nonVorExpenses.add(entry.getValue());
			}
		}

		logger.info("[EarningReport] earnings " + earnings.toString() + " for companyId " + companyId);
		if (NumberUtilities.isPositive(earnings)) {
			BigDecimal vorExpenses = (BigDecimal)MapUtils.getObject(expensesMap, AccountServiceType.VENDOR_OF_RECORD, BigDecimal.ZERO);
			EarningReport earningReport = new EarningReport();
			copyTaxEntityPropertiesToTaxReport(earningReport, taxEntity);
			earningReport.setEarnings(earnings);
			earningReport.setExpenses(expenses.negate());
			earningReport.setMarketingPayments(taxReport.getMarketingPayments());
			earningReport.setPaymentReversals(taxReport.getPaymentReversals());
			earningReport.setWorkPayments(taxReport.getWorkPayments());
			earningReport.setTaxYear(taxReportSet.getTaxYear().toString());
			earningReport.setEarningReportSet(taxReportSet);
			earningReport.setVorEarnings(taxReport.getVorEarnings());
			earningReport.setNonVorEarnings(taxReport.getNonVorEarnings());
			earningReport.setVorExpenses(vorExpenses.negate());
			earningReport.setNonVorExpenses(nonVorExpenses.negate());

			if(StringUtils.isNotBlank(earningReport.getLastName())) {
				taxReportService.saveTaxReport(earningReport);
			} else {
				logger.info("skipping over Earnings Report for company with id: " + companyId);
			}

		}
	}

	@Override
	TaxReportRow getEarningsInPeriodByCompany(Calendar fromDate, Calendar toDate, long companyId) {
		return taxReportService.getEarningsInPeriodByCompany(fromDate, toDate, companyId, AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES);
	}

	@SuppressWarnings("unchecked")
	@Override
	AbstractTaxEntity findTaxEntityForTaxReport(long companyId) {
		logger.info("[tax] findTaxEntityForEarningReport " + companyId);
		Assert.notNull(taxReportDateRange);
		Assert.notNull(taxReportDateRange.getThrough());
		AbstractTaxEntity taxEntity = taxService.findActiveTaxEntityByCompanyForEarningReport(companyId, taxReportDateRange.getThrough());
		if (taxEntity != null && !taxEntity.getStatus().isRejected()) {
			logger.info("[tax] found tax entity " + taxEntity.getId());
			return taxEntity;
		}
		return null;
	}

	@Override
	<E extends AbstractTaxEntity> List<E> getApprovedTaxEntitiesByCompanyId(long companyId) {
		return taxService.findAllApprovedTaxEntitiesByCompanyId(companyId);
	}
}
