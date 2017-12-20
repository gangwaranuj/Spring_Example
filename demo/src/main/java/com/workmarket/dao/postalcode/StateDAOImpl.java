package com.workmarket.dao.postalcode;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.postalcode.State;

import javax.annotation.Resource;

@Repository
public class StateDAOImpl extends AbstractDAO<State> implements StateDAO {

	private static final Log logger = LogFactory.getLog(StateDAO.class);

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<State> getEntityClass() {
		return State.class;
	}

	@Override
	@SuppressWarnings({"unchecked", "JpaQueryApiInspection"})
	public List<State> findStates() {
		Query query = getFactory().getCurrentSession().getNamedQuery("state.findAll");
		return query.list();
	}

	@Override
	public State findStateByShortName(String shortName) {
		Query query = getFactory().getCurrentSession().getNamedQuery("state.findByShortName");
		query.setString("shortName", shortName);
		return (State)query.uniqueResult();
	}

	@Override
	public State findStateWithPostalCodeAndShortName(String postalCode, String shortName) {
		SQLBuilder sqlBuilder = new SQLBuilder();
		sqlBuilder.addTable("postal_code p")
				.addJoin("INNER JOIN state s ON p.country_id = s.country_id")
				.addWhereClause("p.postal_code = :postalCode")
				.addWhereClause("s.short_name = :shortName")
				.addColumn("s.*")
				.addParam("postalCode", postalCode)
				.addParam("shortName", shortName);

		try {
			return jdbcTemplate.queryForObject(sqlBuilder.build(), sqlBuilder.getParams(), new StateRowMapper());
		} catch (EmptyResultDataAccessException ex) {
			logger.error("Unsupported postal code: " + postalCode + " and/or state code: " + shortName, ex);
			return null;
		}
	}

	@Override
	public State findStateWithCountryAndStateCode(String country, String state) {
		Query query = getFactory().getCurrentSession().getNamedQuery("state.findByCodeAndCountry");
		query.setString("country", country);
		query.setString("shortName", state);
		return (State)query.uniqueResult();
	}

	@Override
	public State findStateWithCountryAndStateName(String country, String state) {
		Query query = getFactory().getCurrentSession().getNamedQuery("state.findByNameAndCountry");
		query.setString("country", country);
		query.setString("name", state);
		return (State)query.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<State> findStates(String countryId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("state.findByCountry");
		query.setString("code", countryId);
		return query.list();
	}

	private class StateRowMapper implements RowMapper<State> {
		public State mapRow(ResultSet rs, int rowNum) throws SQLException {

			State state = new State();

			state.setId(rs.getLong("id"));
			state.setShortName(rs.getString("short_name"));
			state.setName(rs.getString("name"));
			state.setCountry(Country.newInstance(rs.getString("country_id")));

			return state;
		}
	}

}
