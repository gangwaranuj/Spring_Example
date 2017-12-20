package com.workmarket.api.model.resolver;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.v2.worker.model.FeedRequestDTO;

import org.apache.struts.mock.MockHttpServletRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeedRequestDTOArgumentResolverTest {
  FeedRequestDTOArgumentResolver resolver = new FeedRequestDTOArgumentResolver();

  @Test
  public void shouldSupportParamterType() {
    final MethodParameter methodParameter = mock(MethodParameter.class);
    when(methodParameter.getParameterType()).thenReturn(null);
    assertFalse(resolver.supportsParameter(methodParameter));
  }

  @Test
  public void shouldReturnDto() {
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.addParameter("pageSize", "3");
    r.addParameter("page", "2");
    r.addParameter("industryId", "4");
    r.addParameter("keyword", "John Smith");
    r.addParameter("latitude", "-4.4");
    r.addParameter("longitude", "5.5");
    r.addParameter("radius", "0.5");
    r.addParameter("virtual", "false");
    r.addParameter("fields", "lastName,firstName");
    r.addParameter("when", "foo");
    r.addParameter("sortByDistance", "true");
    r.addParameter("filterOutApplied", "true");
    r.addParameter("sort", "firstName,lastName");
    r.addParameter("endDate", "123123123");
    r.addParameter("startDate", "345345345");
    r.addParameter("filter", "foo,bazz");

    final FeedRequestDTO dto = resolver.evaluate(r);

    assertEquals(Integer.valueOf(3), dto.getPageSize());
    assertEquals(Integer.valueOf(2), dto.getPage());
    assertEquals(Integer.valueOf(4), dto.getIndustryId());
    assertEquals("John Smith", dto.getKeyword());
    assertEquals(Double.valueOf("-4.4"), dto.getLatitude());
    assertEquals(Double.valueOf("5.5"), dto.getLongitude());
    assertEquals(Double.valueOf("0.5"), dto.getRadius());
    assertEquals(Boolean.FALSE, dto.getVirtual());
    assertEquals("lastName,firstName", dto.getFields());
    assertEquals("foo", dto.getWhen());
    assertEquals(Boolean.TRUE, dto.isSortByDistance());
    assertEquals(Boolean.TRUE, dto.isFilterOutApplied());
    assertEquals(dto.getSort(), ImmutableList.of("firstName", "lastName"));
    assertEquals(Long.valueOf("123123123"), dto.getEndDate());
    assertEquals(Long.valueOf("345345345"), dto.getStartDate());
    assertEquals(dto.getFilter(), ImmutableList.of("foo", "bazz"));
  }

  @Test
  public void shouldBoundPageSize() {
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.addParameter("pageSize", "43");

    final FeedRequestDTO dto = resolver.evaluate(r);

    assertEquals(Integer.valueOf(25), dto.getPageSize());
  }

  @Test
  public void shouldSetDefaults() {
    MockHttpServletRequest r = new MockHttpServletRequest();

    final FeedRequestDTO dto = resolver.evaluate(r);

    assertNull(dto.getIndustryId());
    assertFalse(dto.getVirtual());
    assertNull(dto.getKeyword());
    assertNull(dto.getLatitude());
    assertNull(dto.getLongitude());
    assertNull(dto.getRadius());
    assertNull(dto.getFields());
    assertNull(dto.getStartDate());
    assertNull(dto.getEndDate());
    assertFalse(dto.isSortByDistance());
    assertFalse(dto.isFilterOutApplied());
    assertEquals("all", dto.getWhen());
    assertEquals(0, dto.getSort().size());
    assertEquals(0, dto.getFilter().size());
    assertEquals(Integer.valueOf("1"), dto.getPage());
    assertEquals(Integer.valueOf("25"), dto.getPageSize());
  }
}