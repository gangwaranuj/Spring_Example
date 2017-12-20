package com.workmarket.reporting.service;

import com.google.common.collect.Lists;
import com.workmarket.common.template.pdf.PDFTemplateFactory;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.reporting.model.EvidenceReport;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.template.TemplateService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.List;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EvidenceReportServiceTest {

	@Mock UserGroupService userGroupService;
	@Mock ScreeningService screeningService;
	@Mock TemplateService templateService;
	@Mock PDFTemplateFactory PDFTemplateFactory;
	@InjectMocks EvidenceReportServiceImpl evidenceReportService;

	Company company;
	User user;
	EvidenceReport evidenceReport;
	List<EvidenceReport> evidenceReports;
	UserUserGroupAssociation userUserGroupAssociation;

	@Before
	@SuppressWarnings("unchecked")
	public void setup() {
		company = mock(Company.class);
		when(company.getEffectiveName()).thenReturn("acme inc");

		user = mock(User.class);
		when(user.getFirstName()).thenReturn("test");
		when(user.getLastName()).thenReturn("me");
		when(user.getEmail()).thenReturn("me@me.com");
		when(user.getCompany()).thenReturn(company);

		evidenceReport = mock(EvidenceReport.class);
		when(evidenceReport.getResponseDate()).thenReturn(Calendar.getInstance());
		when(evidenceReport.getRequestDate()).thenReturn(Calendar.getInstance());
		when(evidenceReport.getCompanyName()).thenReturn("acme inc");
		when(evidenceReport.getFirstName()).thenReturn("test");
		when(evidenceReport.getLastName()).thenReturn("me");

		evidenceReports = Lists.newArrayList();

		when(screeningService.
				findBulkMostRecentEvidenceReport(anyListOf(Long.class),
						anyString())).thenReturn(evidenceReports);

		userUserGroupAssociation = mock(UserUserGroupAssociation.class);
		when(userUserGroupAssociation.getUser()).thenReturn(user);
		when(userGroupService.getAllActiveGroupMemberIds(anyLong())).thenReturn(Lists.newArrayList(1L));
	}

	@Test
	public void test_fetchEvidenceReportsByGroupId_findAllActiveAssociations(){
		List<EvidenceReport> backgroundChecks = evidenceReportService.fetchEvidenceReportByGroupId(1L,Screening.BACKGROUND_CHECK_TYPE);
		verify(userGroupService).getAllActiveGroupMemberIds(1L);
	}

	@Test
	public void test_fetchEvidenceReportsByGroupId_nullActiveMembers_findMostRecentEvidenceReportNotCalled(){
		when(userGroupService.findAllActiveAssociations(anyLong())).thenReturn(null);
		evidenceReportService.fetchEvidenceReportByGroupId(1L,Screening.BACKGROUND_CHECK_TYPE);
		verify(screeningService, never()).findMostRecentBackgroundCheck(anyLong());
	}

	@Test
	public void test_fetchEvidenceReportsByGroupId_nullMember_findMostRecentBackgroundCheckNotCalled(){
		when(userGroupService.getAllActiveGroupMemberIds(anyLong())).thenReturn(null);
		evidenceReportService.fetchEvidenceReportByGroupId(1L,Screening.BACKGROUND_CHECK_TYPE);
		verify(screeningService, never()).findBulkMostRecentEvidenceReport(anyListOf(Long.class),anyString());
	}


	@Test
	public void test_fetchEvidenceReportsByGroupId_singleActiveMembersWithBackgroundCheck(){
		evidenceReportService.fetchEvidenceReportByGroupId(1L,Screening.BACKGROUND_CHECK_TYPE);
		verify(screeningService,times(1)).findBulkMostRecentEvidenceReport(anyListOf(Long.class),anyString());
	}

	@Test
	public void test_fetchEvidenceReportsByGroupId_singleActiveMembersWithNullBackgroundCheck(){
		when(screeningService.
				findBulkMostRecentEvidenceReport(anyListOf(Long.class),anyString())).thenReturn(null);
		evidenceReportService.fetchEvidenceReportByGroupId(1L,Screening.BACKGROUND_CHECK_TYPE);
		verify(screeningService,times(1)).findBulkMostRecentEvidenceReport(anyListOf(Long.class),anyString());
	}

	@Test
	public void test_fetchEvidenceReportsByGroupId_emptyActiveMembers_findMostRecentBackgroundCheckNotCalled(){
		when(userGroupService.getAllActiveGroupMemberIds(anyLong())).thenReturn(null);
		evidenceReportService.fetchEvidenceReportByGroupId(1L,Screening.BACKGROUND_CHECK_TYPE);
		verify(screeningService, never()).findBulkMostRecentEvidenceReport(anyListOf(Long.class),anyString());
	}
}

