package com.workmarket.dao.tax;


import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.UsaTaxEntity;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

public interface TaxEntityDAO extends DAOInterface<AbstractTaxEntity> {

	AbstractTaxEntity findTaxEntityByIdAndCompany(long taxEntityId, long companyId);

	List<? extends AbstractTaxEntity> findUnverifiedActiveTaxEntitiesByCountry(String country);

	List<? extends AbstractTaxEntity> findTaxEntitiesByTinAndCountry(String tin, String country);

	/**
	 * This returns all unverified tax entities in the system that meet the following criteria:
	 *  1. the company has an active tax entity AND an unverified tax entity
	 *  2. the unverified tax entity is newer than the active one
	 * @return
	 */
	List<AbstractTaxEntity> findAllUnverifiedTaxEntitiesWhereActiveOrRejectedExists();

	/**
	 * This returns both active and inactive entities
	 * @param companyId
	 * @return
	 */
	List<? extends AbstractTaxEntity> findAllTaxEntitiesByCompany(long companyId);

	List<? extends AbstractTaxEntity> findAllApprovedTaxEntitiesByCompanyId(long companyId);
	
	List<UsaTaxEntity> findAllUsaApprovedTaxEntitiesByCompanyId(long companyId);

	<T extends AbstractTaxEntity> T findActiveTaxEntityByCompany(long companyId);

	boolean hasTaxEntityPendingApproval(long companyId, String taxNumber);

	boolean hasTaxEntityPendingApproval(long companyId);

	Set<Long> findAllCompaniesWithMultipleApprovedTaxEntities(DateRange dateRange);

	Set<Long> getAllCompaniesWithFirstTaxEntityInPeriodAndNoTaxReportForYear(DateRange dateRange, int taxReportYearToExclude);
	List<Long> getAllActivatedAccountIds();

	List<Long> getAccountIdsFromId(long fromId);

	List<? extends AbstractTaxEntity> findAllTaxEntitiesFromModifiedDate(Calendar fromModifiedDate);

	List<? extends AbstractTaxEntity> findAllAccountsFromId(long fromId);
}
