package com.workmarket.domains.groups.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;

import com.workmarket.service.business.dto.GroupMembershipDTO;

public class GroupMembershipRowMapper implements RowMapper<GroupMembershipDTO> {
	private static final Log logger = LogFactory.getLog(GroupMembershipRowMapper.class);

	@Override
	public GroupMembershipDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		try {
			GroupMembershipDTO dto = new GroupMembershipDTO();
			dto.setUserId(rs.getLong("userId"));
			dto.setUserNumber(rs.getString("userNumber"));
			dto.setFirstName(rs.getString("firstName"));
			dto.setLastName(rs.getString("lastName"));
			dto.setStarRating(rs.getInt("starRating"));
			dto.setCompanyName(rs.getString("companyName"));
			dto.setLaneType(rs.getLong("laneType"));
			dto.setCity(rs.getString("city"));
			dto.setPostalCode(rs.getString("postalCode"));
			dto.setCountry(rs.getString("country"));
			dto.setState(rs.getString("state"));
			dto.setVerificationStatus(rs.getInt("verificationStatus"));
			dto.setApprovalStatus(rs.getInt("approvalStatus"));
			dto.setLatitude(rs.getBigDecimal("latitude"));
			dto.setLongitude(rs.getBigDecimal("longitude"));
			dto.setDerivedStatus(rs.getString("status"));

			return dto;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new SQLException(e);
		}
	}
}

