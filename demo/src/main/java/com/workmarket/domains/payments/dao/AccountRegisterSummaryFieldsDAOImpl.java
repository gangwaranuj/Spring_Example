package com.workmarket.domains.payments.dao;

import com.google.common.base.Optional;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.utility.sql.SQLBuilder;
import com.workmarket.utility.sql.SQLOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * Created by nick on 2013-06-01 5:12 PM
 */
@Repository
public class AccountRegisterSummaryFieldsDAOImpl extends AbstractDAO<AccountRegisterSummaryFields> implements AccountRegisterSummaryFieldsDAO {

	@Autowired @Resource(name = "jdbcTemplate") // uses master to avoid risk of replication lag
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<?> getEntityClass() {
		return AccountRegisterSummaryFields.class;
	}

	@Override
	public Optional<AccountRegisterSummaryFields> findAccountRegisterSummaryByCompanyId(Long companyId) {
		SQLBuilder sqlBuilder = new SQLBuilder()
				.addTable("account_register")
				.addColumn("available_cash")
				.addColumn("deposited_cash")
				.addColumn("withdrawable_cash")
				.addColumn("pending_earned_cash")
				.addColumn("accounts_payable_balance")
				.addColumn("accounts_receivable_balance")
				.addColumn("pending_commitments")
				.addColumn("actual_cash")
				.addColumn("general_cash")
				.addColumn("project_cash")
				.addColumn("assignment_throughput")
				.addColumn("assignment_throughput_software AS assignment_software_throughput") // name mismatch
				.addColumn("assignment_throughput_vor AS assignment_vor_throughput")
				.addWhereClause("company_id", SQLOperator.EQUALS, "companyId", companyId)
				.addParam("companyId", companyId);

		try {
			return Optional.fromNullable(
				jdbcTemplate.queryForObject(
						sqlBuilder.build(),
						sqlBuilder.getParams(),
						new BeanPropertyRowMapper<>(AccountRegisterSummaryFields.class))
			);
		} catch (EmptyResultDataAccessException e) {
			return Optional.absent();
		}
	}

}
