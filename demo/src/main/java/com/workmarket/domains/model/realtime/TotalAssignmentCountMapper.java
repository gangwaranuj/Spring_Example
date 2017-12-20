package com.workmarket.domains.model.realtime;

import com.workmarket.domains.model.realtime.TotalAssignmentCountMapper.AssignmentCount;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TotalAssignmentCountMapper implements
		RowMapper<AssignmentCount> {

	@Override
	public AssignmentCount mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		return new AssignmentCount(rs.getString("work_status"), rs.getInt("assignment_count"));
	}

	public static class AssignmentCount extends MutablePair<String, Integer> {
		public AssignmentCount(String first, Integer second) {
			super(first, second);
		}
	}

}
