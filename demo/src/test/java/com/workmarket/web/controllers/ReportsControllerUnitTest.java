package com.workmarket.web.controllers;

import au.com.bytecode.opencsv.CSVReader;
import com.workmarket.data.report.work.WorkReportPagination;
import com.workmarket.domains.reports.controllers.ReportsController;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.service.web.cachebusting.CacheBusterServiceImpl;
import com.workmarket.web.views.CSVView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.io.StringReader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


/**
 * Created by nick on 6/28/13 11:57 AM
 */
public class ReportsControllerUnitTest extends BaseControllerUnitTest {

	protected static class ExportRequestBuilder {
		public static MockHttpServletRequestBuilder create() {
			return MockMvcRequestBuilders.get("/reports/export")
					.param("filters.status", "1")
					.param("filters.subStatus", "1")
					.param("filters.owner", "")
					.param("filters.client", "")
					.param("filters.project", "")
					.param("filters.resourceType", "")
					.param("filters.from_date", "01/01/2012")
					.param("filters.to_date", "12/31/2012")
					.param("filters.assignment_approved_date_from", "")
					.param("filters.assignment_approved_date_to", "")
					.param("filters.assignment_paid_date_from", "")
					.param("filters.assignment_paid_date_to", "")
					.param("filters.from_price", "")
					.param("filters.to_price", "");
		}
	}


	public static final ResultHandler VALID_CSV_FILE_HANDLER = new ResultHandler() {
		@Override
		public void handle(MvcResult mvcResult) throws Exception {
			ModelAndView mav = mvcResult.getModelAndView();

			View view = mav.getView();
			assertTrue(view.getClass().equals(CSVView.class));

			CSVView csvView = (CSVView) view;
			assertTrue(csvView.getFilename().equals(
				String.format(ReportsController.CSV_EXPORT_FILE_FORMAT, "20120101", "20121231")));

			MockHttpServletResponse response = mvcResult.getResponse();
			assertNotNull(response.getOutputStream());
		}
	};

	private static final ArgumentCaptor<WorkReportPagination> paginationCaptor = ArgumentCaptor.forClass(WorkReportPagination.class);
	private static final Answer<WorkReportPagination> paginationAnswer = new Answer<WorkReportPagination>() {
		@Override public WorkReportPagination answer(InvocationOnMock invocationOnMock) throws Throwable {
			return paginationCaptor.getValue();
		}
	};

	@InjectMocks ReportsController controller;
	@Mock WorkReportService reportService;
	@Mock View mockView;
	@Mock CacheBusterServiceImpl cacheBusterServiceImpl;
	MockMvc mockMvc;

	private static String CACHE_BUSTER_HASH = "hash";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		initController(controller);

		when(cacheBusterServiceImpl.getMediaPrefix()).thenReturn(CACHE_BUSTER_HASH);

		mockMvc = standaloneSetup(controller)
				.setSingleView(mockView)
				.build();
	}

	@Test
	public void export_BuyerWorkReport_HasValidCSVFile() {

		mockBuyerWorkReport();

		try {
			mockMvc.perform(ExportRequestBuilder.create()
					.param("buyerReport", "true")
					.param("budgetReport", "false"))
					.andExpect(status().isOk())
					.andDo(VALID_CSV_FILE_HANDLER);

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void exportBuyerWorkReport_HasValidCSVColumnHeaders() {

		mockBuyerWorkReport();

		try {
			mockMvc.perform(ExportRequestBuilder.create()
					.param("buyerReport", "true")
					.param("budgetReport", "false"))
					.andExpect(status().isOk())
					.andDo(new ResultHandler() {
						@Override
						public void handle(MvcResult mvcResult) throws Exception {
							String response = mvcResult.getResponse().getContentAsString();
							String[] headerRow = new CSVReader(new StringReader(response)).readAll().get(0);
							for (String s : headerRow) {
								assertTrue(
										ReportsController.REPORT_CSV_ASSIGNMENT_COLUMNS.contains(s)
												|| ReportsController.REPORT_CSV_ASSIGNMENT_DATA_COLUMNS.contains(s)
												|| ReportsController.REPORT_CSV_NON_BUYER_FEE_COLUMNS.contains(s)
								);
								assertFalse(ReportsController.REPORT_CSV_BUYER_FEE_COLUMNS.contains(s));
								assertFalse(ReportsController.REPORT_CSV_BUYER_AND_BUDGET_COLUMNS.contains(s));
							}
						}
					});
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void export_ResourceEarningsReport_HasValidCSVFile() {

		mockResourceEarningsReport();

		try {
			mockMvc.perform(ExportRequestBuilder.create()
					.param("buyerReport", "false")
					.param("budgetReport", "false"))
					.andExpect(status().isOk())
					.andDo(VALID_CSV_FILE_HANDLER);

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void exportResourceEarningsReportCSV_HasValidCSVColumnHeaders() {

		mockResourceEarningsReport();

		try {
			mockMvc.perform(ExportRequestBuilder.create()
					.param("buyerReport", "false")
					.param("budgetReport", "false"))
					.andExpect(status().isOk())
					.andDo(new ResultHandler() {
						@Override
						public void handle(MvcResult mvcResult) throws Exception {
							String response = mvcResult.getResponse().getContentAsString();
							String[] headerRow = new CSVReader(new StringReader(response)).readAll().get(0);
							for (String s : headerRow) {
								assertTrue(
										ReportsController.REPORT_CSV_ASSIGNMENT_COLUMNS.contains(s)
												|| ReportsController.REPORT_CSV_ASSIGNMENT_DATA_COLUMNS.contains(s)
												|| ReportsController.REPORT_CSV_BUYER_FEE_COLUMNS.contains(s)
								);
								assertFalse(ReportsController.REPORT_CSV_NON_BUYER_FEE_COLUMNS.contains(s));
								assertFalse(ReportsController.REPORT_CSV_BUYER_AND_BUDGET_COLUMNS.contains(s));
							}
						}
					});

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void export_BuyerBudgetReport_HasValidCSVFile() {

		mockBuyerBudgetReport();

		try {
			mockMvc.perform(ExportRequestBuilder.create()
					.param("buyerReport", "true")
					.param("budgetReport", "true"))
					.andExpect(status().isOk())
					.andDo(VALID_CSV_FILE_HANDLER);

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}


	@Test
	public void export_BuyerBudgetReport_HasValidCSVColumnHeaders() {

		mockBuyerBudgetReport();

		try {
			mockMvc.perform(ExportRequestBuilder.create()
					.param("buyerReport", "true")
					.param("budgetReport", "true"))
					.andExpect(status().isOk())
					.andDo(new ResultHandler() {
						@Override
						public void handle(MvcResult mvcResult) throws Exception {
							String response = mvcResult.getResponse().getContentAsString();
							String[] headerRow = new CSVReader(new StringReader(response)).readAll().get(0);
							for (String s : headerRow) {
								assertTrue(
										ReportsController.REPORT_CSV_ASSIGNMENT_COLUMNS.contains(s)
												|| ReportsController.REPORT_CSV_BUYER_AND_BUDGET_COLUMNS.contains(s)
												|| ReportsController.REPORT_CSV_ASSIGNMENT_DATA_COLUMNS.contains(s)
												|| ReportsController.REPORT_CSV_BUYER_FEE_COLUMNS.contains(s)
								);
								assertFalse(ReportsController.REPORT_CSV_BUYER_FEE_COLUMNS.contains(s));

							}
						}
					});

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}


	private void mockBuyerWorkReport() {
		when(reportService.generateWorkReportBuyer(any(Long.class), paginationCaptor.capture(), any(boolean.class)))
				.thenAnswer(paginationAnswer);
		Whitebox.setInternalState(controller, "reportService", reportService);
	}

	private void mockResourceEarningsReport() {
		when(reportService.generateEarningsReportResource(any(Long.class), paginationCaptor.capture(), any(boolean.class)))
				.thenAnswer(paginationAnswer);
		Whitebox.setInternalState(controller, "reportService", reportService);
	}

	private void mockBuyerBudgetReport() {
		when(reportService.generateBudgetReportBuyer(any(Long.class), paginationCaptor.capture(), any(boolean.class)))
				.thenAnswer(paginationAnswer);
		Whitebox.setInternalState(controller, "reportService", reportService);
	}
}
