package com.workmarket.dao.random;

import com.workmarket.configuration.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CompanyRandomIdentifierDAOImpl extends AbstractRandomIdentifierDAOImpl implements CompanyRandomIdentifierDAO {

	@Autowired
	public CompanyRandomIdentifierDAOImpl(@Qualifier("simpleJdbcTemplate") final JdbcTemplate jdbcTemplate) {
		super("company", "company_number", Constants.COMPANY_NUMBER_IDENTIFIER_LENGTH, jdbcTemplate);
	}

}
