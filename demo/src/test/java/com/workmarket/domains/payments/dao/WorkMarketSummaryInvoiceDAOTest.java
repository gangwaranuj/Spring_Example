package com.workmarket.domains.payments.dao;

import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.invoice.WorkMarketSummaryInvoice;
import com.workmarket.domains.model.invoice.WorkMarketSummaryInvoicePagination;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: micah
 * Date: 3/10/15
 * Time: 2:28 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkMarketSummaryInvoiceDAOTest {
	@InjectMocks WorkMarketSummaryInvoiceDAOImpl wmSummaryInvoiceDAO;

	@Mock Pagination<WorkMarketSummaryInvoice> pagination;
	@Mock Criteria query;
	@Mock Criteria count;

	@Test
	public void applySorts_NullSortColumn_Desc() {
		when(pagination.getSortColumn()).thenReturn(null);

		wmSummaryInvoiceDAO.applySorts(pagination, query, count);

		ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

		verify(query).addOrder(captor.capture());

		assertThat(captor.getValue().toString(), is("id desc"));
	}

	@Test
	public void applySorts_INVOICE_TYPE_Desc() {
		when(pagination.getSortColumn()).thenReturn("INVOICE_TYPE");
		when(pagination.getSortDirection()).thenReturn(Pagination.SORT_DIRECTION.DESC);

		wmSummaryInvoiceDAO.applySorts(pagination, query, count);

		ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
		verify(query).addOrder(captor.capture());

		assertThat(captor.getValue().toString(), is("type desc"));
	}

	@Test
	public void applySorts_INVOICE_TYPE_Asc() {
		when(pagination.getSortColumn()).thenReturn("INVOICE_TYPE");
		when(pagination.getSortDirection()).thenReturn(Pagination.SORT_DIRECTION.ASC);

		wmSummaryInvoiceDAO.applySorts(pagination, query, count);

		ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
		verify(query).addOrder(captor.capture());

		assertThat(captor.getValue().toString(), is("type asc"));
	}

	@Test
	public void applyFilters_NullFilters_NoLogic() {
		when(pagination.getFilters()).thenReturn(null);

		wmSummaryInvoiceDAO.applyFilters(pagination, query, count);

		verify(pagination, times(0)).hasFilter(any(WorkMarketSummaryInvoicePagination.FILTER_KEYS.class));
	}

	@Test
	public void applyFilters_No_CREATED_DATE_FROM() {
		when(pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.CREATED_DATE_FROM)).thenReturn(false);

		wmSummaryInvoiceDAO.applyFilters(pagination, query, count);

		verify(pagination, times(0)).getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.CREATED_DATE_FROM);
	}

	@Test
	public void applyFilters_No_DUE_DATE_FROM() {
		when(pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.DUE_DATE_FROM)).thenReturn(false);

		wmSummaryInvoiceDAO.applyFilters(pagination, query, count);

		verify(pagination, times(0)).getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.DUE_DATE_FROM);
	}

	@Test
	public void applyFilters_No_DUE_DATE_TO() {
		when(pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.DUE_DATE_TO)).thenReturn(false);

		wmSummaryInvoiceDAO.applyFilters(pagination, query, count);

		verify(pagination, times(0)).getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.DUE_DATE_TO);
	}

	@Test
	public void applyFilters_No_INVOICE_STATUS() {
		when(pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.INVOICE_STATUS)).thenReturn(false);

		wmSummaryInvoiceDAO.applyFilters(pagination, query, count);

		verify(pagination, times(0)).getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.INVOICE_STATUS);
	}

	@Test
	public void applyFilters_No_COMPANY_NAME() {
		when(pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.COMPANY_NAME)).thenReturn(false);
		wmSummaryInvoiceDAO.applyFilters(pagination, query, count);
		verify(pagination, times(0)).getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.COMPANY_NAME);
	}

	@Test
	public void applyFilters_WITH_COMPANY_NAME() {
		final String COLUMN = "company.effectiveName";
		final String VALUE = "CoName";

		when(pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.COMPANY_NAME)).thenReturn(true);
		when(pagination.getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.COMPANY_NAME)).thenReturn(VALUE);
		wmSummaryInvoiceDAO.applyFilters(pagination, query, count);

		ArgumentCaptor<Criterion> captor = ArgumentCaptor.forClass(Criterion.class);
		verify(query).add(captor.capture());

		Criterion actual = captor.getValue();
		Criterion expected = Restrictions.ilike(COLUMN, StringUtilities.processForLike(VALUE));
		assertThat(actual.toString(), is(expected.toString()));
	}

	@Test
	public void applyFilters_No_INVOICE_NUMBER() {
		when(pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.INVOICE_NUMBER)).thenReturn(false);
		wmSummaryInvoiceDAO.applyFilters(pagination, query, count);
		verify(pagination, times(0)).getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.INVOICE_NUMBER);
	}

	@Test
	public void applyFilters_WITH_INVOICE_NAME() {

		final String COLUMN = "invoiceNumber";
		final String VALUE = "";

		when(pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.INVOICE_NUMBER)).thenReturn(true);
		when(pagination.getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.INVOICE_NUMBER)).thenReturn(VALUE);
		wmSummaryInvoiceDAO.applyFilters(pagination, query, count);

		ArgumentCaptor<Criterion> captor = ArgumentCaptor.forClass(Criterion.class);
		verify(query).add(captor.capture());

		Criterion actual = captor.getValue();
		Criterion expected = Restrictions.ilike(COLUMN, StringUtilities.processForLike(VALUE));
		assertThat(actual.toString(), is(expected.toString()));
	}

	private void testDateFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS filterKey, String dateStr, String field, String op) {
		when(pagination.hasFilter(filterKey)).thenReturn(true);
		when(pagination.getFilter(filterKey)).thenReturn(dateStr);

		wmSummaryInvoiceDAO.applyFilters(pagination, query, count);

		ArgumentCaptor<SimpleExpression> captor = ArgumentCaptor.forClass(SimpleExpression.class);
		verify(query).add(captor.capture());

		SimpleExpression actual = captor.getValue();
		SimpleExpression expected = ("ge".equals(op)) ?
			Restrictions.ge(field, DateUtilities.getCalendarFromISO8601(dateStr)) :
			Restrictions.le(field, DateUtilities.getCalendarFromISO8601(dateStr));

		assertThat(actual.toString(), is(expected.toString()));
	}

	@Test
	public void applyFilters_CREATED_DATE_FROM() {
		testDateFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.CREATED_DATE_FROM, "2015-01-01", "createdOn", "ge");
	}

	@Test
	public void applyFilters_DUE_DATE_FROM() {
		testDateFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.DUE_DATE_FROM, "2015-01-01", "dueDate", "ge");
	}

	@Test
	public void applyFilters_DUE_DATE_TO() {
		testDateFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.DUE_DATE_TO, "2015-01-01", "dueDate", "le");
	}

	@Test
	public void applyFilters() {
		when(pagination.hasFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.INVOICE_STATUS)).thenReturn(true);
		when(pagination.getFilter(WorkMarketSummaryInvoicePagination.FILTER_KEYS.INVOICE_STATUS)).thenReturn("pending");

		wmSummaryInvoiceDAO.applyFilters(pagination, query, count);

		ArgumentCaptor<SimpleExpression> captor = ArgumentCaptor.forClass(SimpleExpression.class);
		verify(query).add(captor.capture());

		assertThat(captor.getValue().toString(), is("invoiceStatusType.code=pending"));
	}
}
