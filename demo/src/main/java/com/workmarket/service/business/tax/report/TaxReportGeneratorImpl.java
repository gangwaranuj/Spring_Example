package com.workmarket.service.business.tax.report;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.AbstractTaxReportSet;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.domains.model.tax.TaxReportRow;
import com.workmarket.domains.model.tax.TaxReportSetStatusType;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.vault.services.VaultServerService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

/**
 * Author: rocio
 */
@Component
@Scope(value = "prototype")
public abstract class TaxReportGeneratorImpl<T extends AbstractTaxReportSet> implements TaxReportGenerator<T> {

	private static final Log logger = LogFactory.getLog(TaxReportGeneratorImpl.class);

	@Autowired protected TaxService taxService;
	@Autowired protected TaxReportService taxReportService;
	@Autowired protected WorkNegotiationService workNegotiationService;
	@Autowired protected VaultServerService vaultServerService;
	@Autowired protected VaultHelper vaultHelper;
	@Autowired FeatureEvaluator featureEvaluator;
	@Autowired SecurityContextFacade securityContextFacade;

	T taxReportSet;
	DateRange taxReportDateRange;
	final Set<Long> companiesWithFirstTaxEntityInPeriod = Sets.newHashSet();
	final Set<Long> companiesWithMultipleTaxEntities = Sets.newHashSet();


	abstract <E extends AbstractTaxEntity> void saveTaxReport(long companyId, DateRange dateRange, TaxReportRow taxReport, E taxEntity);

	abstract <E extends TaxReportRow> List<E> getAllCompaniesWithEarningsInPeriod();

	abstract TaxReportRow getEarningsInPeriodByCompany(Calendar fromDate, Calendar toDate, long companyId);

	abstract <E extends AbstractTaxEntity> E findTaxEntityForTaxReport(long companyId);

	abstract <E extends AbstractTaxEntity> List<E> getApprovedTaxEntitiesByCompanyId(long companyId);

	public void setTaxReportSet(T taxReportSet) {
		this.taxReportSet = taxReportSet;
		if (taxReportSet != null && taxReportSet.getTaxYear() != null) {
			taxReportDateRange = getDateRangeForTaxReportYear(taxReportSet.getTaxYear());
			companiesWithFirstTaxEntityInPeriod.addAll(taxService.getAllCompaniesWithFirstTaxEntityInPeriod(taxReportDateRange, taxReportSet.getTaxYear() - 1));
			companiesWithMultipleTaxEntities.addAll(taxService.findAllCompaniesWithMultipleApprovedTaxEntities(taxReportDateRange));
		}
	}

	@Override
	public T generateTaxReport() {
		Assert.isTrue(taxReportSet.getTaxReportSetStatusType().isNew(), "Tax Report can't be processed");
		taxReportSet.setTaxReportSetStatusType(new TaxReportSetStatusType(TaxReportSetStatusType.PROCESSING));
		taxReportService.saveTaxReportSet(taxReportSet);

		processCompaniesWithFirstTimeTaxEntity();
		processCompaniesWithEarningsThisYear();

		taxReportSet.setTaxReportSetStatusType(new TaxReportSetStatusType(TaxReportSetStatusType.ISSUED));
		taxReportService.saveTaxReportSet(taxReportSet);
		return taxReportSet;
	}

	@Async
	void processCompaniesWithFirstTimeTaxEntity() {
		Assert.notNull(taxReportDateRange);
		logger.info("processCompaniesWithFirstTimeTaxEntity");

		//Companies with first tax entity in period
		for(Long companyId: companiesWithFirstTaxEntityInPeriod) {
			if (companiesWithMultipleTaxEntities.contains(companyId)) {
				splitTaxReport(companyId, findAllApprovedTaxEntitiesDateRangesByCompany(companyId));
			} else {
				AbstractTaxEntity activeTaxEntity = findTaxEntityForTaxReport(companyId);
				if (activeTaxEntity != null) {
					//get the amount earned even before this tax year
					DateRange earningsDateRange = new DateRange(DateUtilities.WM_EPOCH_CALENDAR, taxReportDateRange.getThrough());
					TaxReportRow earnings = getEarningsInPeriodByCompany(earningsDateRange.getFrom(), earningsDateRange.getThrough(), companyId);
					saveTaxReport(companyId, earningsDateRange, earnings, activeTaxEntity);
				}
			}
		}
	}

	@Async
	void processCompaniesWithEarningsThisYear() {
		Assert.notNull(taxReportDateRange);
		logger.info("processCompaniesWithEarningsThisYear");

		for (TaxReportRow earnings : getAllCompaniesWithEarningsInPeriod()) {
			long companyId = earnings.getCompanyId();
			if (!companiesWithFirstTaxEntityInPeriod.contains(companyId)) {
				if (companiesWithMultipleTaxEntities.contains(companyId)) {
					splitTaxReport(companyId, findAllApprovedTaxEntitiesDateRangesByCompany(companyId));
				} else {
					AbstractTaxEntity activeTaxEntity = findTaxEntityForTaxReport(companyId);
					if (activeTaxEntity != null) {
						saveTaxReport(companyId, taxReportDateRange, earnings, activeTaxEntity);
					}
				}
			}
		}
	}

	@Async
	void splitTaxReport(long companyId, Map<? extends AbstractTaxEntity, DateRange> taxEntityDateRangeMap) {
		if (MapUtils.isEmpty(taxEntityDateRangeMap)) {
			return;
		}
		Calendar startDate = DateUtilities.cloneCalendar(taxReportDateRange.getFrom());
		if (companiesWithFirstTaxEntityInPeriod.contains(companyId)) {
			startDate = DateUtilities.WM_EPOCH_CALENDAR;
		}

		for (Map.Entry<? extends AbstractTaxEntity, DateRange> entry : taxEntityDateRangeMap.entrySet()) {
			DateRange taxEntityDateRange = entry.getValue();

			if (taxEntityDateRange.getFrom() != null) {
				/**
				 *	We don't want tax entities with effective date after the end of the tax year. Example: Jan 2014 for tax year 2013.
				 *  We also don't want tax entities with effective date before the start date.
				 */
				if (taxEntityDateRange.getFrom().before(taxReportDateRange.getThrough()) && taxEntityDateRange.getThrough().after(startDate)) {
					//Cap the endDate to the end fo the current fiscal year that is being processed
					Calendar endDate;
					if (taxEntityDateRange.getThrough().after(taxReportDateRange.getThrough())) {
						endDate = DateUtilities.cloneCalendar(taxReportDateRange.getThrough());
					} else {
						endDate = DateUtilities.cloneCalendar(taxEntityDateRange.getThrough());
					}

					TaxReportRow earnings = getEarningsInPeriodByCompany(startDate, endDate, companyId);
					if (earnings.getEarnings().compareTo(BigDecimal.ZERO) > 0 || earnings.getExpenses().compareTo(BigDecimal.ZERO) > 0) {
						saveTaxReport(companyId, new DateRange(startDate, endDate), earnings, entry.getKey());
					}
					startDate = DateUtilities.cloneCalendar(taxEntityDateRange.getThrough());
				}
			}
		}
	}

	<T extends AbstractTaxReport> T copyTaxEntityPropertiesToTaxReport(T taxReportEntry, AbstractTaxEntity taxEntity) {
		Assert.notNull(taxReportEntry);
		Assert.notNull(taxEntity);
		taxReportEntry.setFirstName(StringUtilities.fullName(taxEntity.getFirstName(), taxEntity.getMiddleName()));
		taxReportEntry.setLastName(StringUtils.isNotBlank(taxEntity.getLastName()) ? taxEntity.getLastName() : taxEntity.getTaxName());
		taxReportEntry.setAddress(taxEntity.getAddress());
		taxReportEntry.setCity(taxEntity.getCity());
		taxReportEntry.setState(taxEntity.getState());
		taxReportEntry.setBusinessFlag(taxEntity.getBusinessFlag());
		taxReportEntry.setCompanyId(taxEntity.getCompany().getId());
		taxReportEntry.setCountry(taxEntity.getCountry());
		taxReportEntry.setPostalCode(taxEntity.getPostalCode());
		taxReportEntry.setTaxEntityType(taxEntity.getTaxEntityType());
		taxReportEntry.setTaxEntity(taxEntity);
		taxReportEntry.setEffectiveDate(taxEntity.getEffectiveDate());
		taxReportEntry.setTaxNumber(vaultHelper.get(taxEntity, "taxNumber", taxEntity.getTaxNumberSanitized()).getValue());

		if (taxReportEntry instanceof EarningReport) {
			((EarningReport)taxReportEntry).setBusinessName(taxEntity.getBusinessName());
			((EarningReport)taxReportEntry).setBusinessNameFlag(taxEntity.isBusinessNameFlag());
		}
		return taxReportEntry;
	}

	DateRange getDateRangeForTaxReportYear(int year) {
		TimeZone serverTimeZone = TimeZone.getTimeZone(Constants.DEFAULT_TIMEZONE);
		Calendar nowInYear = DateUtilities.getCalendarNow(Constants.DEFAULT_TIMEZONE);
		nowInYear.set(Calendar.YEAR, year);
		Calendar fromDate = DateUtilities.getCalendarWithFirstDayOfYear(nowInYear, serverTimeZone);
		Calendar toDate = DateUtilities.getCalendarWithLastDayOfYear(nowInYear, serverTimeZone);
		return new DateRange(fromDate, toDate);
	}

	@SuppressWarnings("unchecked")
	<E extends AbstractTaxEntity> Map<E, DateRange> findAllApprovedTaxEntitiesDateRangesByCompany(long companyId) {
		final Map<E, DateRange> taxEntityDateRangeMap = Maps.newLinkedHashMap();
		List<E> approvedTaxEntities = getApprovedTaxEntitiesByCompanyId(companyId);

		Calendar startDate = null;
		Calendar endDate = null;
		AbstractTaxEntity taxEntity = null;

		do {
			if (isNotEmpty(approvedTaxEntities)) {
				taxEntity = approvedTaxEntities.get(0);
				approvedTaxEntities.remove(0);
				startDate = DateUtilities.cloneCalendar(firstNonNull(taxEntity.getEffectiveDate(), taxEntity.getActiveDate()));
			}
		} while (startDate == null && isNotEmpty(approvedTaxEntities));

		if (startDate == null) {
			return taxEntityDateRangeMap;
		}

		for (E entity : approvedTaxEntities) {
			endDate = DateUtilities.cloneCalendar(firstNonNull(entity.getEffectiveDate(), entity.getActiveDate()));
			if (startDate != null && endDate != null) {
				taxEntityDateRangeMap.put((E)taxEntity, new DateRange(startDate, endDate));
				startDate = DateUtilities.cloneCalendar(endDate);
				taxEntity = entity;
				endDate = null;
			}
		}
		if (startDate != null) {
			taxEntityDateRangeMap.put((E)taxEntity, new DateRange(startDate, firstNonNull(endDate, taxReportDateRange.getThrough())));
		}
		return taxEntityDateRangeMap;
	}
}
