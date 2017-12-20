package com.workmarket.service.business.tax;

import com.google.common.base.Optional;

import com.workmarket.api.v2.model.TaxInfoApiDTO;
import com.workmarket.api.v2.model.TaxInfoSaveApiDTO;
import com.workmarket.api.v2.model.W9PdfPreviewApiDTO;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.service.business.dto.TaxEntityDTO;

import groovy.lang.Tuple2;

import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

public interface TaxService {
	BigDecimal calculateTaxAmount(long workId);

	AbstractTaxEntity findTaxEntityByIdAndCompany(long taxEntityId, long companyId);

	AbstractTaxEntity findActiveTaxEntity(long userId);

	boolean hasTaxEntityPendingApproval(long userId, String taxNumber);

	AbstractTaxEntity findActiveTaxEntityByCompany(long companyId);

	String findTaxEntityCountryId(AbstractTaxEntity entity);

	String getCountryIdFromTaxEntityCountry(String taxCountry);

	AbstractTaxEntity findActiveTaxEntityByCompanyForEarningReport(long companyId, Calendar activeDateDeadline);

	List<? extends AbstractTaxEntity> findAllTaxEntities(long userId);

	AbstractTaxEntity saveTaxEntity(long userId, TaxEntityDTO taxEntityDTO) throws RuntimeException;

	<T extends AbstractTaxEntity> T saveTaxEntityForCompany(long companyId, TaxEntityDTO taxEntityDTO) throws RuntimeException;

	List<? extends AbstractTaxEntity> findTaxEntitiesById(List<Long> ids);

	List<? extends AbstractTaxEntity> findTaxEntitiesByTinAndCountry(String tin, String country);

	Set<Long> findAllCompaniesWithMultipleApprovedTaxEntities(DateRange dateRange);

	List<UsaTaxEntity> findAllUsaApprovedTaxEntitiesByCompanyId(long companyId);

	<T extends AbstractTaxEntity> List<T> findAllApprovedTaxEntitiesByCompanyId(long companyId);

	Set<Long> getAllCompaniesWithFirstTaxEntityInPeriod(DateRange dateRange, int taxReportYearToExclude);

	void saveTaxEntity(AbstractTaxEntity abstractTaxEntity);

	List<Long> getAllIds();

	List<Long> getAllActivatedAccountIds();

	boolean areIrsMatchFieldsDifferent(TaxEntityDTO dto, AbstractTaxEntity entity);

	List<Long> getAllAccountIdsFromId(long fromId);

	List<? extends AbstractTaxEntity> findAllAccountsFromId(long fromId);

	List<? extends AbstractTaxEntity> findAllTaxEntitiesFromModifiedDate(Calendar fromModifiedDate);

	<T extends AbstractTaxReport> Map<Long, String> getTaxIdToTaxNumberMapFromVault(
			List<? extends AbstractTaxReport> taxReports);

	String getFormattedTaxNumber(AbstractTaxEntity taxEntity, String taxNumber);

	Tuple2<File, String> buildPdfForTaxForm(AbstractTaxEntity taxEntity) throws IOException, com.lowagie.text.DocumentException;

	Tuple2<File, String> buildPdfForTaxForm(W9PdfPreviewApiDTO dto) throws IOException, com.lowagie.text.DocumentException;

	Optional<TaxInfoApiDTO> getTaxInfoForUser(long userId);

	Optional<TaxInfoApiDTO> convert(AbstractTaxEntity entity);

	TaxEntityDTO convert(TaxInfoSaveApiDTO dto);
}
