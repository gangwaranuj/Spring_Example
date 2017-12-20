package com.workmarket.domains.model.realtime;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.workmarket.thrift.services.realtime.RealtimeDropDownOption;

public class RealtimeDropdownMapper implements RowMapper<RealtimeDropDownOption> {

	@Override
	public RealtimeDropDownOption mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		RealtimeDropDownOption option = new RealtimeDropDownOption();
		option.setId(rs.getString("id"));
		option.setName(rs.getString("name"));
		return option;
	}

}
