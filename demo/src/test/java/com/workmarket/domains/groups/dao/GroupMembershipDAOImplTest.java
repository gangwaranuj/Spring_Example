package com.workmarket.domains.groups.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * User: alexsilva Date: 1/6/14 Time: 5:23 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupMembershipDAOImplTest {

	@Mock NamedParameterJdbcTemplate jdbcTemplate;
	@InjectMocks GroupMembershipDAOImpl dao;

	List<Map<Long, String>> memberStatus;
	Set<Long> userIds;

	@Before
	public void setup() {
		memberStatus = new ArrayList<>();
		userIds = new HashSet<>();
	}

	private void setupGroupMemberStatusResults() {
		Map<Long, String> res1 = new HashMap<>();
		res1.put(1L, "MEMBER");
		Map<Long, String> res2 = new HashMap<>();
		res2.put(2L, "DECLINED");

		memberStatus.add(res1);
		memberStatus.add(res2);
		when(jdbcTemplate.query(any(String.class), any(RowMapper.class))).thenReturn(memberStatus);

		userIds.add(1L);
	}

	@Test
	public void findGroupMemberStatus_flattenCorrectly() {
		setupGroupMemberStatusResults();
		Map<Long, String> res = dao.getDerivedStatusesByGroupIdAndUserIds(1L, userIds);
		Assert.notEmpty(res);
	}

}
