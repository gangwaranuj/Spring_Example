package com.workmarket.dao.search;

import com.google.common.collect.ImmutableMap;
import com.workmarket.dao.search.user.EntityAssociationDAOImpl;
import com.workmarket.data.solr.model.SolrInsuranceCoverageData;
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
import java.util.Map;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * User: alexsilva Date: 12/26/13 Time: 12:53 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class EntityAssociationDAOImplTest {
	private String fakeSql = "SQL";
	private MapSqlParameterSource fakeMapSource = new MapSqlParameterSource();

	@Mock Map<Long, List<SolrInsuranceCoverageData>> coverageData;
	@Mock NamedParameterJdbcTemplate jdbcTemplate;
	@InjectMocks EntityAssociationDAOImpl entityAssociationDAO = spy(new EntityAssociationDAOImpl());

	@Before
	public void setup() throws Exception {
		when(jdbcTemplate.queryForList(fakeSql, fakeMapSource)).thenReturn( buildFakeInsuranceCoverageResults() );
		when(coverageData.get(anyLong())).thenReturn(new ArrayList<SolrInsuranceCoverageData>());
	}

	@Test
	public void generateInsuranceCoverageMap_coverageAmountNull_thenDontAddData() {
		doReturn(null).when(entityAssociationDAO).cleanRawSQLInsuranceFigure(anyString());

		entityAssociationDAO.generateInsuranceCoverageMap(fakeSql, fakeMapSource);
		verify(coverageData, never()).get(anyLong());
	}

	public List<Map<String, Object>> buildFakeInsuranceCoverageResults() {
		Map<String, Object> map = ImmutableMap.<String, Object>of("user_id", 1, "coverage", "1.00");
		List<Map<String, Object>> list = new ArrayList<>();
		list.add(map);
		return list;
	}
}
