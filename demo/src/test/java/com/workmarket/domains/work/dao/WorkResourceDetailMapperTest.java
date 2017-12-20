package com.workmarket.domains.work.dao;

import com.workmarket.BaseUnitTest;
import com.workmarket.service.business.dto.WorkResourceDetail;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class WorkResourceDetailMapperTest extends BaseUnitTest {

	@InjectMocks WorkResourceDAOImpl.WorkResourceDetailMapper mapper = spy(new WorkResourceDAOImpl.WorkResourceDetailMapper());

	WorkResourceDetail workResourceDetail = mock(WorkResourceDetail.class);
	ResultSet resultSet = mock(ResultSet.class);
	WorkResourceDetailPagination pagination = mock(WorkResourceDetailPagination.class);

	@Before
	public void setup() throws SQLException {
		when(pagination.isIncludeApplyNegotiation()).thenReturn(false);

		when(mapper.getWorkResourceDetail()).thenReturn(workResourceDetail);
		when(mapper.getPagination()).thenReturn(pagination);
		when(mapper.getRlat()).thenReturn(ANY_DOUBLE);
		when(mapper.getRlng()).thenReturn(ANY_DOUBLE);

		when(resultSet.getLong(any(String.class))).thenReturn(ANY_LONG);
		when(resultSet.getString(any(String.class))).thenReturn(EMPTY_TOKEN);
		when(resultSet.getDouble(any(String.class))).thenReturn(ANY_DOUBLE);
		when(resultSet.getBoolean(any(String.class))).thenReturn(false);
		when(resultSet.getTimestamp(any(String.class))).thenReturn(new Timestamp(ANY_LONG));
		when(resultSet.getString("negotiation_status")).thenReturn(null);
		when(resultSet.getString("postal_code")).thenReturn(ANY_STRING);
		when(resultSet.getString("work_postal_code")).thenReturn(ANY_STRING);
		when(resultSet.getDouble("distance")).thenReturn(0d);
	}

	@Test
	public void workResourceDetailMapper_mapRow_doesNotSetMapperDataIfPostalCodesAreEqual() throws SQLException {
		mapper.mapRow(resultSet, 0);

		verify(mapper, never()).addDistance(anyDouble());
	}

	@Test
	public void workResourceDetailMapper_mapRow_setCalculatedGeoDataIfPostalCodesAreNotEqualAndDistanceIsZero() throws SQLException {
		when(resultSet.getString("work_postal_code")).thenReturn(ANY_STRING_2);

		mapper.mapRow(resultSet, 0);

		verify(mapper).addDistance(anyDouble());
	}
}
