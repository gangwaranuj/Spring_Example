package com.workmarket.domains.groups.dao;

import com.workmarket.domains.groups.model.UserGroupLastRoutedDTO;
import com.workmarket.domains.groups.model.UserGroupLastRoutedRowMapper;
import com.workmarket.domains.groups.model.UserGroupThroughputDTO;
import com.workmarket.domains.groups.model.UserGroupThroughputRowMapper;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserGroupDAOImplTest {

	@Mock NamedParameterJdbcTemplate jdbcTemplate;
	@Mock NamedParameterJdbcTemplate readOnlyJdbcTemplate;
	@Mock SessionFactory sessionFactory;
	@InjectMocks UserGroupDAOImpl dao;

	List<UserGroupLastRoutedDTO> lastRoutedDTOs;
	List<UserGroupThroughputDTO> groupSumDTOs;

	private void setupFindAllWorkPriceSumsSinceLastUpdate() {
		when(jdbcTemplate.query(any(String.class),
				any(MapSqlParameterSource.class), any(UserGroupThroughputRowMapper.class))).thenReturn(groupSumDTOs);
	}

	private void  setupFindAllWithNewLastRoutedSinceLastUpdate() {
		when(jdbcTemplate.query(any(String.class),
				any(MapSqlParameterSource.class), any(UserGroupLastRoutedRowMapper.class))).thenReturn(lastRoutedDTOs);
	}

	@Before
	public void setup() {
		lastRoutedDTOs = new ArrayList<>();
		groupSumDTOs = new ArrayList<>();
	}

	@Test
	public void findAllWorkPriceSumsSinceLastUpdate_dao_callsCorrectMapper() {
		setupFindAllWorkPriceSumsSinceLastUpdate();

		dao.calculateThroughputSinceLastUpdate();

		verify(readOnlyJdbcTemplate).query(any(String.class), any(MapSqlParameterSource.class), any(UserGroupThroughputRowMapper.class));
	}

	@Test
	public void findAllWorkPriceSumsSinceLastUpdate_dao_returnsGroupSumDTOs() {
		setupFindAllWorkPriceSumsSinceLastUpdate();

		List<UserGroupThroughputDTO> result = dao.calculateThroughputSinceLastUpdate();

		assertEquals(result, groupSumDTOs);
	}

	@Test
	public void findAllWithNewLastRoutedSinceLastUpdate_dao_returnsLastRoutedDTOs() {
		setupFindAllWithNewLastRoutedSinceLastUpdate();

		List<UserGroupLastRoutedDTO> result = dao.findAllWithNewLastRoutedSinceLastUpdate();

		assertEquals(result, lastRoutedDTOs);
	}

	@Test
	public void findAllWithNewLastRoutedSinceLastUpdate_dao_callsCorrectMapper() {
		setupFindAllWithNewLastRoutedSinceLastUpdate();

		dao.findAllWithNewLastRoutedSinceLastUpdate();

		verify(readOnlyJdbcTemplate).query(any(String.class), any(MapSqlParameterSource.class), any(UserGroupLastRoutedRowMapper.class));
	}

}
