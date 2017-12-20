package com.workmarket.dao.random;

import com.workmarket.configuration.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class WorkRandomIdentifierDAOImpl extends AbstractRandomIdentifierDAOImpl implements WorkRandomIdentifierDAO {

	@Autowired
	public WorkRandomIdentifierDAOImpl(@Qualifier("simpleJdbcTemplate") final JdbcTemplate jdbcTemplate) {
		super("work", "work_number", Constants.WORK_NUMBER_IDENTIFIER_LENGTH, jdbcTemplate);
	}

}
