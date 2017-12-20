package com.workmarket.dao.random;

import com.workmarket.configuration.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserRandomIdentifierDAOImpl extends AbstractRandomIdentifierDAOImpl implements UserRandomIdentifierDAO {

	@Autowired
	public UserRandomIdentifierDAOImpl(@Qualifier("simpleJdbcTemplate") final JdbcTemplate jdbcTemplate) {
		super("user", "user_number", Constants.USER_NUMBER_IDENTIFIER_LENGTH, jdbcTemplate);
	}

}
