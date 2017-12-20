package com.workmarket.dao.tax;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.pricing.AccountServiceType;

@Repository
public class TaxUpdateDAOImpl implements TaxUpdateDAO {
	private static final String WORK_BACKUP_SQL =
			"INSERT INTO tax_update_work_status_backup "
			+ "(work_id, original_account_service_type_code)"
			+ "SELECT distinct work.id, work.account_service_type_code "
			+ "FROM work "
			+ "STRAIGHT_JOIN register_transaction ON work.id = register_transaction.work_id AND "
			+ 	"work.account_service_type_code <> :newServiceType AND "
			+ 	"work.company_id = :companyId AND "
			+ 	"register_transaction.pending_flag = 'N' AND "
			+ 	"register_transaction.register_transaction_type_code IN ('payment', 'wrkpayment') AND "
			+ 	"register_transaction.transaction_date > :startDate AND "
			+ 	"register_transaction.transaction_date <= :endDate "
			+ "STRAIGHT_JOIN work_resource_transaction ON register_transaction.id = work_resource_transaction.id "
			+ "STRAIGHT_JOIN work_resource ON "
			+ 	"(work.id = work_resource.work_id AND work_resource.assigned_to_work = true AND "
			+ 	"work_resource.work_resource_status_type_code != 'cancelled') "
			+ "JOIN user worker ON worker.id = work_resource.user_id "
			+ "STRAIGHT_JOIN tax_entity ON tax_entity.company_id = worker.company_id AND "
			+ 	"tax_entity.country = 'usa' AND tax_entity.active_flag = 'Y' "
			+ "WHERE NOT EXISTS (SELECT bak.work_id FROM tax_update_work_status_backup bak WHERE bak.work_id = work.id)";

	private static final String WORK_BACKUP_SQL_NONE =
			"INSERT INTO tax_update_work_status_backup "
			+ "(work_id, original_account_service_type_code)"
			+ "SELECT distinct work.id, work.account_service_type_code "
			+ "FROM work "
			+ "STRAIGHT_JOIN register_transaction ON work.id = register_transaction.work_id AND "
			+ 	"work.account_service_type_code <> :newServiceType AND "
			+ 	"work.company_id = :companyId AND "
			+ 	"register_transaction.pending_flag = 'N' AND "
			+ 	"register_transaction.register_transaction_type_code IN ('payment', 'wrkpayment') AND "
			+ 	"register_transaction.transaction_date > :startDate AND "
			+ 	"register_transaction.transaction_date <= :endDate "
			+ "STRAIGHT_JOIN work_resource_transaction ON register_transaction.id = work_resource_transaction.id "
			+ "WHERE NOT EXISTS (SELECT bak.work_id FROM tax_update_work_status_backup bak WHERE bak.work_id = work.id)";

	private static final String TRANSACTION_BACKUP_SQL =
			"INSERT INTO tax_update_transaction_status_backup "
			+ "(transaction_id, original_account_service_type_code)"
			+ "SELECT distinct work_resource_transaction.id, work_resource_transaction.account_service_type_code "
			+ "FROM work "
			+ "STRAIGHT_JOIN register_transaction ON work.id = register_transaction.work_id AND "
			+ 	"work.company_id = :companyId AND "
			+ 	"register_transaction.pending_flag = 'N' AND "
			+ 	"register_transaction.register_transaction_type_code IN ('payment', 'wrkpayment') AND "
			+ 	"register_transaction.transaction_date > :startDate AND "
			+ 	"register_transaction.transaction_date <= :endDate "
			+ "STRAIGHT_JOIN work_resource_transaction ON register_transaction.id = work_resource_transaction.id AND "
			+ 	"work_resource_transaction.account_service_type_code <> :newServiceType "
			+ "STRAIGHT_JOIN work_resource ON "
			+ 	"(work.id = work_resource.work_id AND work_resource.assigned_to_work = true AND "
			+ 	"work_resource.work_resource_status_type_code != 'cancelled') "
			+ "JOIN user worker ON worker.id = work_resource.user_id "
			+ "STRAIGHT_JOIN tax_entity ON tax_entity.company_id = worker.company_id AND tax_entity.country = 'usa' AND "
			+ 	"tax_entity.active_flag = 'Y' "
			+ "WHERE NOT EXISTS (SELECT bak.transaction_id FROM tax_update_transaction_status_backup bak WHERE bak.transaction_id = work_resource_transaction.id)";

	private static final String TRANSACTION_BACKUP_SQL_NONE =
			"INSERT INTO tax_update_transaction_status_backup "
			+ "(transaction_id, original_account_service_type_code)"
			+ "SELECT distinct work_resource_transaction.id, work_resource_transaction.account_service_type_code "
			+ "FROM work "
			+ "STRAIGHT_JOIN register_transaction ON work.id = register_transaction.work_id AND "
			+ 	"work.company_id = :companyId AND "
			+ 	"register_transaction.pending_flag = 'N' AND "
			+ 	"register_transaction.register_transaction_type_code IN ('payment', 'wrkpayment') AND "
			+ 	"register_transaction.transaction_date > :startDate AND "
			+ 	"register_transaction.transaction_date <= :endDate "
			+ "STRAIGHT_JOIN work_resource_transaction ON register_transaction.id = work_resource_transaction.id AND "
			+ 	"work_resource_transaction.account_service_type_code <> :newServiceType "
			+ "WHERE NOT EXISTS (SELECT bak.transaction_id FROM tax_update_transaction_status_backup bak WHERE bak.transaction_id = work_resource_transaction.id)";

	private static final String UPDATE_SQL =
			"UPDATE work "
			+ "JOIN register_transaction ON work.id = register_transaction.work_id AND "
			+ 	"register_transaction.pending_flag = 'N' AND "
			+ 	"register_transaction.register_transaction_type_code IN ('payment', 'wrkpayment') AND "
			+ 	"register_transaction.transaction_date > :startDate AND "
			+ 	"register_transaction.transaction_date <= :endDate "
			+ "JOIN work_resource_transaction ON register_transaction.id = work_resource_transaction.id "
			+ "STRAIGHT_JOIN work_resource ON "
			+ 	"(work.id = work_resource.work_id AND work_resource.assigned_to_work = true AND "
			+ 	"work_resource.work_resource_status_type_code != 'cancelled') "
			+ "JOIN user worker ON worker.id = work_resource.user_id "
			+ "STRAIGHT_JOIN tax_entity ON tax_entity.company_id = worker.company_id AND tax_entity.active_flag = 'Y' "
			+ "LEFT OUTER JOIN tax_update_work_status_backup p1 ON p1.work_id = work.id "
			+ "SET work.account_service_type_code = "
			+ 	"CASE "
			+ 		"WHEN tax_entity.country = 'USA' THEN :newServiceType "
			+ 		"ELSE p1.original_account_service_type_code "
			+ 	"END, "
			+ "work_resource_transaction.account_service_type_code = "
			+ 	"CASE "
			+ 		"WHEN tax_entity.country = 'USA' THEN :newServiceType "
			+ 		"ELSE p1.original_account_service_type_code "
			+ 	"END "
			+ "WHERE "
			+ 	"work.company_id = :companyId AND "
			+ 	"(tax_entity.country = 'USA' OR p1.original_account_service_type_code IS NOT NULL)";

	private static final String UPDATE_SQL_NONE =
			"UPDATE work "
			+ "JOIN register_transaction ON work.id = register_transaction.work_id AND "
			+ 	"register_transaction.pending_flag = 'N' AND "
			+ 	"register_transaction.register_transaction_type_code IN ('payment', 'wrkpayment') AND "
			+ 	"register_transaction.transaction_date > :startDate AND "
			+ 	"register_transaction.transaction_date <= :endDate "
			+ "JOIN work_resource_transaction ON register_transaction.id = work_resource_transaction.id AND "
			+ "SET work.account_service_type_code = 'none', work_resource_transaction.account_service_type_code = 'none' "
			+ "WHERE "
			+ 	"work.company_id = :companyId";

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public void backup(Company company, AccountServiceType serviceType, Calendar startDate, Calendar endDate) {
		Assert.notNull(company);
		Assert.notNull(serviceType);
		Assert.notNull(startDate);
		Assert.notNull(endDate);

		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("companyId", company.getId());
		params.addValue("newServiceType", serviceType.getCode());
		params.addValue("startDate", startDate);
		params.addValue("endDate", endDate);

		if (serviceType.getCode() != AccountServiceType.NONE) {
			jdbcTemplate.update(WORK_BACKUP_SQL, params);
			jdbcTemplate.update(TRANSACTION_BACKUP_SQL, params);
		}
		else {
			jdbcTemplate.update(WORK_BACKUP_SQL_NONE, params);
			jdbcTemplate.update(TRANSACTION_BACKUP_SQL_NONE, params);
		}
	}

	@Override
	public void upgrade(Company company, AccountServiceType serviceType, Calendar startDate, Calendar endDate) {
		Assert.notNull(company);
		Assert.notNull(serviceType);
		Assert.notNull(startDate);
		Assert.notNull(endDate);

		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("companyId", company.getId());
		params.addValue("newServiceType", serviceType.getCode());
		params.addValue("startDate", startDate);
		params.addValue("endDate", endDate);

		if (serviceType.getCode() != AccountServiceType.NONE) {
			jdbcTemplate.update(UPDATE_SQL, params);
		}
		else {
			jdbcTemplate.update(UPDATE_SQL_NONE, params);
		}
	}
}
