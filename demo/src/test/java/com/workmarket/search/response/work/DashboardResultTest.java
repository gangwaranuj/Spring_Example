package com.workmarket.search.response.work;

import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DashboardResultTest {

	private static final long CURRENT_COMPANY_ID = 1L;
	private static final long OTHER_COMPANY_ID = 2L;
	DashboardResultFlags dashboardResultFlags;

	@Before
	public void setup()  {
		dashboardResultFlags = mock(DashboardResultFlags.class);
	}

	@Test
	public void getFormattedWorkStatusType_statusSent_selectedStatusAvailable_displayAvailable() {
		DashboardResult dashboardResult = buildDashboardResult(WorkStatusType.SENT, CURRENT_COMPANY_ID, dashboardResultFlags);
		String selectedStatus = WorkStatusType.AVAILABLE.toString();

		String result = dashboardResult.getFormattedWorkStatusType(WorkSearchRequestUserType.CLIENT, CURRENT_COMPANY_ID, selectedStatus);

		assertEquals("Available", result);
	}

	@Test
	public void getFormattedWorkStatusType_statusSent_displaySent() {
		DashboardResult dashboardResult = buildDashboardResult(WorkStatusType.SENT, CURRENT_COMPANY_ID, dashboardResultFlags);
		String selectedStatus = WorkStatusType.SENT.toString();

		String result = dashboardResult.getFormattedWorkStatusType(WorkSearchRequestUserType.CLIENT, CURRENT_COMPANY_ID, selectedStatus);

		assertEquals("Sent", result);
	}

	@Test
	public void getFormattedWorkStatusType_statusSent_otherCompany_displayAvailable() {
		DashboardResult dashboardResult = buildDashboardResult(WorkStatusType.SENT, OTHER_COMPANY_ID);

		String result = dashboardResult.getFormattedWorkStatusType(WorkSearchRequestUserType.CLIENT, CURRENT_COMPANY_ID, "");

		assertEquals("Available", result);
	}

	@Test
	public void getFormattedWorkStatusType_statusComplete_resource_displayComplete() {
		DashboardResult dashboardResult = buildDashboardResult(WorkStatusType.COMPLETE, CURRENT_COMPANY_ID, dashboardResultFlags);

		String result = dashboardResult.getFormattedWorkStatusType(WorkSearchRequestUserType.RESOURCE, CURRENT_COMPANY_ID, "");

		assertEquals("Complete", result);
	}

	@Test
	public void getFormattedWorkStatusType_statusComplete_otherCompany_displayComplete() {
		DashboardResult dashboardResult = buildDashboardResult(WorkStatusType.COMPLETE, OTHER_COMPANY_ID, dashboardResultFlags);

		String result = dashboardResult.getFormattedWorkStatusType(WorkSearchRequestUserType.CLIENT, CURRENT_COMPANY_ID, "");

		assertEquals("Complete", result);
	}

	@Test
	public void getFormattedWorkStatusType_statusActive_isConfirmed_displayConfirmed() {
		DashboardResult dashboardResult = buildDashboardResult(WorkStatusType.ACTIVE, CURRENT_COMPANY_ID, dashboardResultFlags);
		when(dashboardResultFlags.isConfirmed()).thenReturn(true);

		String result = dashboardResult.getFormattedWorkStatusType(WorkSearchRequestUserType.CLIENT, CURRENT_COMPANY_ID, "");

		assertEquals("Confirmed", result);
	}

	@Test
	public void getFormattedWorkStatusType_statusActive_needsResourceConfirmation_displayUnconfirmed() {
		DashboardResult dashboardResult = buildDashboardResult(WorkStatusType.ACTIVE, CURRENT_COMPANY_ID, dashboardResultFlags);
		when(dashboardResultFlags.isConfirmed()).thenReturn(false);
		when(dashboardResultFlags.isResourceConfirmationRequired()).thenReturn(true);

		String result = dashboardResult.getFormattedWorkStatusType(WorkSearchRequestUserType.CLIENT, CURRENT_COMPANY_ID, "");

		assertEquals("Unconfirmed", result);
	}

	@Test
	public void getFormattedWorkStatusType_statusActive_displayAssigned() {
		DashboardResult dashboardResult = buildDashboardResult(WorkStatusType.ACTIVE, CURRENT_COMPANY_ID, dashboardResultFlags);
		when(dashboardResultFlags.isConfirmed()).thenReturn(false);
		when(dashboardResultFlags.isResourceConfirmationRequired()).thenReturn(false);

		String result = dashboardResult.getFormattedWorkStatusType(WorkSearchRequestUserType.CLIENT, CURRENT_COMPANY_ID, "");

		assertEquals("Assigned", result);
	}

	@Test
	public void getFormattedWorkStatusType_statusComplete_buyer_displayPendingApproval() {
		DashboardResult dashboardResult = buildDashboardResult(WorkStatusType.COMPLETE, CURRENT_COMPANY_ID, dashboardResultFlags);

		String result = dashboardResult.getFormattedWorkStatusType(WorkSearchRequestUserType.CLIENT, CURRENT_COMPANY_ID, "");

		assertEquals("Pending Approval", result);
	}

	@Test
	public void getFormattedWorkStatusType_InProgress() {
		DashboardResult dashboardResult = buildDashboardResult(WorkStatusType.INPROGRESS, CURRENT_COMPANY_ID, dashboardResultFlags);

		String result = dashboardResult.getFormattedWorkStatusType(WorkSearchRequestUserType.CLIENT, CURRENT_COMPANY_ID, "");

		assertEquals("In Progress", result);
	}

	@Test
	public void getFormattedWorkStatusType_statusPaymentPending_buyer_displayInvoiced() {
		DashboardResult dashboardResult = buildDashboardResult(WorkStatusType.PAYMENT_PENDING, CURRENT_COMPANY_ID, dashboardResultFlags);

		String result = dashboardResult.getFormattedWorkStatusType(WorkSearchRequestUserType.CLIENT, CURRENT_COMPANY_ID, "");

		assertEquals("Invoiced", result);
	}

	@Test
	public void getFormattedWorkStatusType_otherStatus_displayCapitalized() {
		DashboardResult dashboardResult = buildDashboardResult(WorkStatusType.ABANDONED, CURRENT_COMPANY_ID, dashboardResultFlags);

		String result = dashboardResult.getFormattedWorkStatusType(WorkSearchRequestUserType.CLIENT, CURRENT_COMPANY_ID, "");

		assertEquals("Abandoned", result);
	}

	private DashboardResult buildDashboardResult(String workStatusTypeCode, long ownerCompanyId, DashboardResultFlags dashboardResultFlags) {
		DashboardResult dashboardResult = buildDashboardResult(workStatusTypeCode, ownerCompanyId);
		dashboardResult.setResultFlags(dashboardResultFlags);
		return dashboardResult;
	}

	private DashboardResult buildDashboardResult(String workStatusTypeCode, long ownerCompanyId) {
		DashboardResult dashboardResult = new DashboardResult();
		dashboardResult.setWorkStatusTypeCode(workStatusTypeCode);
		dashboardResult.setOwnerCompanyId(ownerCompanyId);
		return dashboardResult;
	}
}
