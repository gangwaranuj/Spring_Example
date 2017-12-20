package com.workmarket.service.business.tax.report;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxForm1099;
import com.workmarket.domains.model.tax.TaxForm1099Set;
import com.workmarket.domains.model.tax.TaxReportRow;
import com.workmarket.domains.model.tax.UsaTaxEntity;
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
public class TaxReport1099Generator extends TaxReportGeneratorImpl<TaxForm1099Set> implements TaxReportGenerator<TaxForm1099Set> {

	private static final Log logger = LogFactory.getLog(TaxReport1099Generator.class);

	@Override
	List<TaxReportRow> getAllCompaniesWithEarningsInPeriod() {
		Assert.notNull(taxReportSet);
		Assert.notNull(taxReportDateRange);
		return taxReportService.getAllCompaniesWithEarningsInPeriod(taxReportDateRange, Constants.TAX_FORM_1099_GENERATION_AMOUNT_THRESHOLD, AccountServiceType.VOR_SERVICE_TYPES);
	}

	@Override
	@Async
	<E extends AbstractTaxEntity> void saveTaxReport(long companyId, DateRange dateRange, TaxReportRow taxReport, E taxEntity) {
		Assert.notNull(taxReportSet);
		Assert.notNull(taxReport);
		Assert.notNull(dateRange);
		Assert.notNull(taxEntity);
		Map<String, BigDecimal> expensesMap = workNegotiationService.findTotalAdditionalExpensesPaidToCompany(companyId, dateRange);
		BigDecimal earnings = taxReport.getEarnings();
		BigDecimal expenses = (BigDecimal)MapUtils.getObject(expensesMap, AccountServiceType.VENDOR_OF_RECORD, BigDecimal.ZERO);

		earnings = earnings.subtract(expenses);
		logger.info("[1099] earnings " + earnings.toString() + " for companyId " + companyId);
		if (earnings.compareTo(Constants.TAX_FORM_1099_GENERATION_AMOUNT_THRESHOLD) >= 0) {
			TaxForm1099 taxForm1099 = new TaxForm1099();
			copyTaxEntityPropertiesToTaxReport(taxForm1099, taxEntity);
			taxForm1099.setAmount(earnings);
			taxForm1099.setTaxYear(taxReportSet.getTaxYear().toString());
			taxForm1099.setTaxForm1099Set(taxReportSet);

			if(StringUtils.isNotBlank(taxForm1099.getLastName())) {
				taxReportService.saveTaxReport(taxForm1099);
			} else {
				logger.warn("skipping over 1099 Tax Report for company with id: " + companyId);
			}


		}
	}

	@Override
	TaxReportRow getEarningsInPeriodByCompany(Calendar fromDate, Calendar toDate, long companyId) {
		return taxReportService.getEarningsInPeriodByCompany(fromDate, toDate, companyId, AccountServiceType.VOR_SERVICE_TYPES);
	}

	@Override
	UsaTaxEntity findTaxEntityForTaxReport(long companyId) {
		logger.info("[1099] findTaxEntityForTaxReport " + companyId);
		Assert.notNull(taxReportDateRange);
		Assert.notNull(taxReportDateRange.getThrough());
		AbstractTaxEntity activeTaxEntity = taxService.findActiveTaxEntityByCompanyForEarningReport(companyId, taxReportDateRange.getThrough());
		if (activeTaxEntity != null && AbstractTaxEntity.COUNTRY_USA.equals(activeTaxEntity.getCountry()) && activeTaxEntity.getStatus().isApproved()) {
			logger.info("[1099] found tax entity " + activeTaxEntity.getId());
			return (UsaTaxEntity) activeTaxEntity;
		}
		return null;
	}

	@Override
	/**
	 * 	NOTE: This assumes that the effective date is populated for all the multiple tax entities for the same company.
	 * 	UPDATE tax_entity SET effective_date = active_date WHERE effective_date is null AND country = 'usa' AND status = 'approved';
	 */
	List<UsaTaxEntity> getApprovedTaxEntitiesByCompanyId(long companyId) {
		return taxService.findAllUsaApprovedTaxEntitiesByCompanyId(companyId);
	}
}
