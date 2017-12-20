package com.workmarket.domains.groups.model;

import com.workmarket.utility.DateUtilities;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: ianha
 * Date: 12/1/13
 * Time: 10:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserGroupLastRoutedRowMapper implements RowMapper<UserGroupLastRoutedDTO> {

	@Override
	public UserGroupLastRoutedDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserGroupLastRoutedDTO userGroupDTO = new UserGroupLastRoutedDTO();

		userGroupDTO.setUserGroupId(rs.getLong("user_group_id"));
		userGroupDTO.setLastRoutedOn(DateUtilities.getCalendarFromDate(rs.getDate("routed_on")));

		return userGroupDTO;
	}
}
