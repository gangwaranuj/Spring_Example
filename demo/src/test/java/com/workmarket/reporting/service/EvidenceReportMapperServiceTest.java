package com.workmarket.reporting.service;

import com.workmarket.domains.model.screening.Screening;
import com.workmarket.reporting.model.EvidenceReport;
import com.workmarket.reporting.model.EvidenceReportRow;
import com.workmarket.utility.CollectionUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EvidenceReportMapperServiceTest {

	@InjectMocks EvidenceReportMapperServiceImpl evidenceReportMapperService;

	EvidenceReport evidenceReport;
	ArrayList<EvidenceReport> evidenceReports;
	Iterator<EvidenceReport> mockIter;

	@Before
	public void setup() {
		evidenceReport = mock(EvidenceReport.class);
		when(evidenceReport.getResponseDate()).thenReturn(Calendar.getInstance());
		when(evidenceReport.getRequestDate()).thenReturn(Calendar.getInstance());
		when(evidenceReport.getCompanyName()).thenReturn("acme inc");
		when(evidenceReport.getFirstName()).thenReturn("test");
		when(evidenceReport.getLastName()).thenReturn("me");

		evidenceReports = mock(ArrayList.class);
		mockIter = mock(Iterator.class);
		when(evidenceReports.iterator()).thenReturn(mockIter);
		when(mockIter.hasNext()).thenReturn(true,false);
		when(mockIter.next()).thenReturn(evidenceReport, null);
	}

	@Test
	public void mapEvidenceReportToDataTable_twoIterations() {
		when(mockIter.hasNext()).thenReturn(true, true, false);
		when(mockIter.next()).thenReturn(evidenceReport, evidenceReport, null);
		List<EvidenceReportRow> evidenceReportRows = evidenceReportMapperService.mapEvidenceReportToDataTable(
			1L, evidenceReports, Screening.BACKGROUND_CHECK_TYPE
		);
		assertEquals(evidenceReportRows.size(), 2);
	}

	@Test
	public void mapEvidenceReportToDataTable_emptyEvidenceReportList() {
		when(mockIter.hasNext()).thenReturn(false);
		when(mockIter.next()).thenReturn(null);
		List<EvidenceReportRow> evidenceReportRows = evidenceReportMapperService.mapEvidenceReportToDataTable(
			1L, evidenceReports, Screening.BACKGROUND_CHECK_TYPE
		);
		assertEquals(evidenceReportRows.size(), 0);
	}

	@Test
	public void mapEvidenceReportToDataTable_nullDate() {
		when(evidenceReport.getResponseDate()).thenReturn(null);
		List<EvidenceReportRow> evidenceReportRows = evidenceReportMapperService.mapEvidenceReportToDataTable(
			1L, evidenceReports, Screening.BACKGROUND_CHECK_TYPE
		);
		assertTrue(CollectionUtilities.contains(evidenceReportRows.get(0).getRow(), "n/a"));
	}

	@Test
	public void mapEvidenceReportToDataTable_nullFirstName() {
		when(evidenceReport.getFirstName()).thenReturn(null);
		List<EvidenceReportRow> evidenceReportRows = evidenceReportMapperService.mapEvidenceReportToDataTable(
			1L, evidenceReports, Screening.BACKGROUND_CHECK_TYPE
		);
		assertTrue(CollectionUtilities.contains(evidenceReportRows.get(0).getRow(), evidenceReport.getLastName()));
	}

	@Test
	public void mapEvidenceReportToDataTable_nullLastName() {
		when(evidenceReport.getLastName()).thenReturn(null);
		List<EvidenceReportRow> evidenceReportRows = evidenceReportMapperService.mapEvidenceReportToDataTable(
			1L, evidenceReports, Screening.BACKGROUND_CHECK_TYPE
		);
		assertTrue(CollectionUtilities.contains(evidenceReportRows.get(0).getRow(), evidenceReport.getFirstName()));
	}

	@Test
	public void mapEvidenceReportToCSV_twoIterations() {
		when(mockIter.hasNext()).thenReturn(true, true, false);
		when(mockIter.next()).thenReturn(evidenceReport,evidenceReport, null);
		List<String[]> evidenceReportRows = evidenceReportMapperService.mapEvidenceReportToCSV(evidenceReports);
		assertEquals(evidenceReportRows.size(), 3);
	}

	@Test
	public void mapEvidenceReportToCSV_emptyBackgroundCheckList() {
		when(mockIter.hasNext()).thenReturn(false);
		when(mockIter.next()).thenReturn(null);
		List<String[]> evidenceReportRows = evidenceReportMapperService.mapEvidenceReportToCSV(evidenceReports);
		assertEquals(evidenceReportRows.size(), 1);
	}

	@Test
	public void mapEvidenceReportToCSV_nullDate() {
		when(evidenceReport.getResponseDate()).thenReturn(null);
		List<String[]> evidenceReportRows = evidenceReportMapperService.mapEvidenceReportToCSV(evidenceReports);
		assertTrue(CollectionUtilities.contains(Arrays.asList(evidenceReportRows.get(1)), "n/a"));
	}

	@Test
	public void mapEvidenceReportToCSV_nullFirstName() {
		when(evidenceReport.getFirstName()).thenReturn(null);
		List<String[]> evidenceReportRows = evidenceReportMapperService.mapEvidenceReportToCSV(evidenceReports);
		assertFalse(CollectionUtilities.contains(Arrays.asList(evidenceReportRows.get(1)), evidenceReport.getFirstName()));
	}

	@Test
	public void mapEvidenceReportToCSV_nullLastName() {
		when(evidenceReport.getLastName()).thenReturn(null);
		List<String[]> evidenceReportRows = evidenceReportMapperService.mapEvidenceReportToCSV(evidenceReports);
		assertFalse(CollectionUtilities.contains(Arrays.asList(evidenceReportRows.get(1)), evidenceReport.getLastName()));
	}
}
