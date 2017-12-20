package com.workmarket.domains.model.realtime;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.workmarket.thrift.services.realtime.RealtimeUser;

public class RealtimeUserMapper implements RowMapper<IRealtimeUser> {

	@Override
	public IRealtimeUser mapRow(ResultSet rs, int rowNum) throws SQLException {
		RealtimeUser owner = new RealtimeUser();
		owner.setFirstName(rs.getString("u.first_name"));
		owner.setLastName(rs.getString("u.last_name"));
		owner.setUserId(rs.getLong("u.id"));
		owner.setUserNumber(rs.getString("u.user_number"));
		return new RealtimeOwnerDecorator(owner);
	}

}
