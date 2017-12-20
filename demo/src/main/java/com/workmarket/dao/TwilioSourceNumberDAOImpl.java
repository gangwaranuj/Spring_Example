package com.workmarket.dao;

import com.workmarket.domains.model.TwilioSourceNumber;
import com.workmarket.utility.sql.SQLBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TwilioSourceNumberDAOImpl extends AbstractDAO<TwilioSourceNumber> implements TwilioSourceNumberDAO {
	protected Class<TwilioSourceNumber> getEntityClass() {
		return TwilioSourceNumber.class;
	}

	@Autowired @Qualifier("readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate readOnlyJdbcTemplate;

	public List<String> getAllSourceNumbers() {

		SQLBuilder query = new SQLBuilder();
		query
			.addColumns("tsn.source_number")
			.addTable("twilio_source_number tsn");

		return readOnlyJdbcTemplate.queryForList(query.build(), query.getParams(), String.class);
	}
}
