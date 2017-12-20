package com.workmarket.domains.work.dao;

import com.google.api.client.util.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkVendorInvitationDAOImplTest {

	private static final Long WORK_ID = 1L;
	private static final List<Long> VENDOR_IDS = Lists.newArrayList();

	@Mock NamedParameterJdbcTemplate jdbcTemplate;
	@InjectMocks WorkVendorInvitationDAOImpl workVendorAssociationDAO;

	@Before
	public void setup() {
		VENDOR_IDS.add(1L);
		VENDOR_IDS.add(2L);

		when(jdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), eq(Integer.class))).thenReturn(1);
		when(jdbcTemplate.queryForList(anyString(), any(SqlParameterSource.class), eq(Long.class))).thenReturn(VENDOR_IDS);
	}

	@Test
	public void hasInvitedAtLeastOneVendor_noOneInvited_returnsFalse() {
		when(jdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), eq(Integer.class))).thenReturn(0);

		assertFalse(workVendorAssociationDAO.hasInvitedAtLeastOneVendor(WORK_ID));
	}

	@Test
	public void hasInvitedAtLeastOneVendor_atLeastOneVendorInvited_returnsTrue() {
		assertTrue(workVendorAssociationDAO.hasInvitedAtLeastOneVendor(WORK_ID));
	}

	@Test
	public void getVendorIdsByWork_noAssociations_emptyList() {
		when(jdbcTemplate.queryForList(anyString(), any(SqlParameterSource.class), eq(Long.class))).thenReturn(Lists.<Long>newArrayList());

		assertTrue(CollectionUtils.isEmpty(workVendorAssociationDAO.getNotDeclinedVendorIdsByWork(WORK_ID)));
	}

	@Test
	public void getVendorIdsByWork_someAssociations_nonEmptyList() {
		assertTrue(CollectionUtils.isNotEmpty(workVendorAssociationDAO.getNotDeclinedVendorIdsByWork(WORK_ID)));
	}
}
