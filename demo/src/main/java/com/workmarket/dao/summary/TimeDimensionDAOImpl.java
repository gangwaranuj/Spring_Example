package com.workmarket.dao.summary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.summary.TimeDimension;
import com.workmarket.utility.DateUtilities;

@Repository
public class TimeDimensionDAOImpl extends AbstractDAO<TimeDimension> implements TimeDimensionDAO {
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	/**
	 *   `day_of_month` int(11) unsigned NOT NULL,
	 *     `month_of_year` int(11) unsigned NOT NULL,
	 *     `year` int(11) unsigned NOT NULL,
	 *     `day_of_year` int(11) unsigned NOT NULL,
	 *     `hour_of_day` int(11) unsigned NOT NULL,
	 *     `week_of_year` int(11) unsigned NOT NULL,
	 *     `quarter_of_year` int(11) unsigned NOT NULL,
	 *     
	 */
	@Override
	public Long findTimeDimensionId(Integer month, Integer year, Integer day, Integer hour){
		
		String sql = "select id from time_dimension where year = :year and month_of_year = :month and day_of_year = :day and hour_of_day = :hour";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("month", month);
		params.addValue("year", year);
		params.addValue("day", day);
		params.addValue("hour", hour);
		
		return jdbcTemplate.queryForObject(sql, params, Long.class);
	}

	@Override
	public TimeDimension findTimeDimension(Integer month, Integer year, Integer day, Integer hour){
		return get(findTimeDimensionId(month, year, day, hour));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Calendar> findMonthsBetweenDates(Calendar fromDate, Calendar toDate) {
		String query = "Select date from time_dimension where date BETWEEN :fromDate AND :toDate GROUP BY year, month_of_year";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fromDate", fromDate);
		params.addValue("toDate", toDate);

		List results = this.jdbcTemplate.query(query, params, new RowMapper() {
			public Calendar mapRow(ResultSet rs, int rowNum) throws SQLException {
				Calendar row = DateUtilities.getCalendarFromDate(rs.getTimestamp("date"));
				return row;
			}
		});
		return results;
	}

	@Override
	public String getDateInStringById(long dateId) {
		String sql = "SELECT date FROM time_dimension where id = :dateId ";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("dateId", dateId);
		return jdbcTemplate.queryForObject(sql, params, String.class);
	}

	@Override
	protected Class<TimeDimension> getEntityClass() {
		return TimeDimension.class;
	}

}
