package com.workmarket.domains.groups.model;

import com.workmarket.utility.DateUtilities;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: ianha
 * Date: 11/25/13
 * Time: 3:50 PM
 */
public class UserGroupThroughputRowMapper implements RowMapper<UserGroupThroughputDTO> {
	@Override
	public UserGroupThroughputDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserGroupThroughputDTO userGroupThroughputDTO = new UserGroupThroughputDTO();
		userGroupThroughputDTO.setUserGroupId(rs.getLong("userGroupId"));
		userGroupThroughputDTO.setThroughput(rs.getBigDecimal("newThroughput"));
		userGroupThroughputDTO.setFromDate(DateUtilities.getCalendarFromDate(rs.getDate("fromDate")));

		return userGroupThroughputDTO;
	}
}
