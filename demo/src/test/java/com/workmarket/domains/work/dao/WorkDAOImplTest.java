package com.workmarket.domains.work.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkDAOImplTest {

  @Mock NamedParameterJdbcTemplate jdbcTemplate;
  @Mock NamedParameterJdbcTemplate readOnlyJdbcTemplate;
  @InjectMocks WorkDAOImpl workDAO;

  @Test
  public void getAssignmentDataOne_withData_assertOnShapeOfPayload() {
    final Map<String, Object> row1 = Maps.newHashMap();
    row1.put("work_id", 1L);
    row1.put("location_state", "A");
    row1.put("internal_owner_last_name", "B");
    row1.put("company_id", 2L);
    row1.put("hours_worked", 3);
    row1.put("per_hour_price_initial", 1D);
    row1.put("per_hour_price_additional", 2D);
    row1.put("final_cost", 3D);
    row1.put("close_date", new Date());
    row1.put("sent_date", new Date());
    row1.put("complete_date", new Date());

    final Map<String, Object> row2 = Maps.newHashMap();
    row2.put("work_id", 1L);
    row2.put("location_state", "A");
    row2.put("internal_owner_last_name", "B");
    row2.put("company_id", 2L);
    row2.put("hours_worked", 3);
    row2.put("per_hour_price_initial", 1D);
    row2.put("per_hour_price_additional", 2D);
    row2.put("final_cost", 3D);
    row2.put("close_date", new Date());
    row2.put("sent_date", new Date());
    row2.put("complete_date", new Date());

    final List<Map<String, Object>> rows = Lists.newArrayList(row1, row2);

    when(readOnlyJdbcTemplate.query(anyString(), any(SqlParameterSource.class), any(RowMapper.class))).thenReturn(rows);
    when(readOnlyJdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), eq(Integer.class))).thenReturn(rows.size());

    final Map<String, Object> data = workDAO.getAssignmentDataOne(Maps.<String, Object>newHashMap());

    verify(readOnlyJdbcTemplate).query(anyString(), any(SqlParameterSource.class), any(RowMapper.class));

    assertTrue(data.containsKey("results"));
    assertTrue(data.containsKey("totalRows"));

    final List<Map<String, Object>> results = (List<Map<String, Object>>) data.get("results");
    assertEquals(rows.get(0), results.get(0));
    assertEquals(rows.size(), results.size());
    assertEquals(1L, ((Long) rows.get(0).get("work_id")).longValue());

    final Integer totalRows = (Integer) data.get("totalRows");
    assertEquals(rows.size(), totalRows.intValue());
  }

  @Test
  public void getAssignmentDataOne_noData_returnEmptyMap() {
    when(readOnlyJdbcTemplate.query(anyString(), any(SqlParameterSource.class), any(RowMapper.class))).thenReturn(Lists.newArrayList());

    assertTrue(workDAO.getAssignmentDataOne(Maps.<String, Object>newHashMap()).isEmpty());
    verify(readOnlyJdbcTemplate).query(anyString(), any(SqlParameterSource.class), any(RowMapper.class));
  }

}
