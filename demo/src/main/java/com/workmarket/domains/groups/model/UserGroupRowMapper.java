package com.workmarket.domains.groups.model;

import com.workmarket.dto.UserGroupDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserGroupRowMapper implements RowMapper<UserGroupDTO> {
	@Override
	public UserGroupDTO mapRow(ResultSet resultSet, int i) throws SQLException {
		UserGroupDTO userGroupDTO = new UserGroupDTO();
		userGroupDTO.setUserGroupId(resultSet.getLong("user_group_id"));
		userGroupDTO.setSearchable(resultSet.getBoolean("searchable"));
		userGroupDTO.setOpenMembership(resultSet.getBoolean("open_membership"));
		return userGroupDTO;
	}
}
