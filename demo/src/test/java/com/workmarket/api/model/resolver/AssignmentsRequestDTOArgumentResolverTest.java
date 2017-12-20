package com.workmarket.api.model.resolver;

import com.workmarket.api.v2.worker.model.AssignmentsRequestDTO;
import com.workmarket.domains.model.WorkStatusType;

import org.apache.struts.mock.MockHttpServletRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AssignmentsRequestDTOArgumentResolverTest {
  AssignmentsRequestDTOArgumentResolver resolver = new AssignmentsRequestDTOArgumentResolver();

  @Test
  public void shouldReturnDto() {
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.addParameter("pageSize", "5");
    r.addParameter("page", "2");
    r.addParameter("sort", "firstName,lastName");
    r.addParameter("fields", "lastName,firstName");
    r.addParameter("status", WorkStatusType.SENT);

    final AssignmentsRequestDTO dto = resolver.evaluate(r);

    assertEquals(Integer.valueOf("5"), dto.getPageSize());
    assertEquals(Integer.valueOf("2"), dto.getPage());
    assertEquals("firstName,lastName", dto.getSort());
    assertEquals("lastName,firstName", dto.getFields());
    assertEquals(WorkStatusType.SENT, dto.getStatus().getCode());
  }

  @Test
  public void shouldReturnDefaults() {
    MockHttpServletRequest r = new MockHttpServletRequest();

    final AssignmentsRequestDTO dto = resolver.evaluate(r);

    assertEquals(WorkStatusType.AVAILABLE, dto.getStatus().getCode());
    assertEquals("", dto.getFields());
    assertEquals(Integer.valueOf(1), dto.getPage());
    assertEquals(Integer.valueOf(25), dto.getPageSize());
    assertEquals(null, dto.getSort());
  }

  @Test
  public void shouldSetPageSizeBound() {
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.addParameter("pageSize", "100");

    final AssignmentsRequestDTO dto = resolver.evaluate(r);

    assertEquals(Integer.valueOf(25), dto.getPageSize());
  }
}