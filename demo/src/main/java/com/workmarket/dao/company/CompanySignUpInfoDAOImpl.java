package com.workmarket.dao.company;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.CompanySignUpInfo;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * User: iloveopt
 * Date: 8/28/14
 */
@Repository
public class CompanySignUpInfoDAOImpl extends AbstractDAO<CompanySignUpInfo> implements CompanySignUpInfoDAO {

	private static final Log logger = LogFactory.getLog(CompanySignUpInfo.class);

	@Autowired @Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<CompanySignUpInfo> getEntityClass() {
		return CompanySignUpInfo.class;
	}

	@Override
	public String getCompanySignUpPricingPlan(long companyId) {
		SQLBuilder builder = new SQLBuilder()
				.addColumn("pricing_plan")
				.addTable("company_sign_up_info")
				.addWhereClause("company_id = :companyId")
				.addParam("companyId", companyId);

		return jdbcTemplate.queryForObject(builder.build(), builder.getParams(), String.class);
	}
}
