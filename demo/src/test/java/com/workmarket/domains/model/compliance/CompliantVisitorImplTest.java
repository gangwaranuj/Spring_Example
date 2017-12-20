package com.workmarket.domains.model.compliance;

import com.google.api.client.util.Sets;
import com.workmarket.domains.compliance.model.AssignmentCountComplianceRule;
import com.workmarket.domains.compliance.model.PeriodicComplianceRule;
import com.workmarket.domains.compliance.model.WorkBundleComplianceCriterion;
import com.workmarket.domains.compliance.model.WorkComplianceCriterion;
import com.workmarket.domains.compliance.service.ComplianceService;
import com.workmarket.domains.compliance.service.CompliantVisitorImpl;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Set;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompliantVisitorImplTest {
	@Mock private ComplianceService complianceService;
	@Mock private WorkService workService;
	@Mock private WorkBundleService workBundleService;
	@Mock private MessageBundleHelper messageHelper;
	@Mock private DateUtilities dateUtilities;
	@InjectMocks CompliantVisitorImpl visitor = spy(new CompliantVisitorImpl());

	WorkComplianceCriterion workComplianceCriterion;
	WorkBundleComplianceCriterion workBundleComplianceCriterion;
	User user;
	Work work1, work2, work3;
	DateRange schedule1, schedule2, schedule3;
	WorkBundle bundle;
	Company company;
	AssignmentCountComplianceRule assignmentCountComplianceRule;
	Set<Work> bundledWork;
	Calendar now, lastDay;

	@Before
	public void setUp() throws SearchException {
		bundledWork = Sets.newHashSet();
		now = DateUtilities.getCalendarNow();
		lastDay = DateUtilities.addHours(DateUtilities.getMidnight(DateUtilities.getCalendarWithLastDayOfWeek(now)), 2);

		schedule1 = mock(DateRange.class);
			when(schedule1.getFrom()).thenReturn(now);
		schedule2 = mock(DateRange.class);
			when(schedule2.getFrom()).thenReturn(now);
		schedule3 = mock(DateRange.class);
			when(schedule3.getFrom()).thenReturn(now);
		user = mock(User.class);
			when(user.getId()).thenReturn(1L);
			when(user.getFullName()).thenReturn("John Doe");
		company = mock(Company.class);
			when(company.getId()).thenReturn(1L);
		work1 = mock(Work.class);
			when(work1.getCompany()).thenReturn(company);
			when(work1.getSchedule()).thenReturn(schedule1);
			when(work1.getScheduleFrom()).thenReturn(now);
		work2 = mock(Work.class);
			when(work2.getCompany()).thenReturn(company);
			when(work2.getSchedule()).thenReturn(schedule2);
			when(work2.getScheduleFrom()).thenReturn(now);
		work3 = mock(Work.class);
			when(work3.getCompany()).thenReturn(company);
			when(work3.getSchedule()).thenReturn(schedule3);
			when(work3.getScheduleFrom()).thenReturn(now);
		bundle = mock(WorkBundle.class);
			when(bundle.getCompany()).thenReturn(company);
			when(bundle.isWorkBundle()).thenReturn(true);
			when(bundle.getBundle()).thenReturn(bundledWork);
		workComplianceCriterion = mock(WorkComplianceCriterion.class);
			when(workComplianceCriterion.getUser()).thenReturn(user);
			when(workComplianceCriterion.getWork()).thenReturn(work1);
			when(workComplianceCriterion.getSchedule()).thenReturn(schedule1);
			when(workComplianceCriterion.isMet()).thenReturn(true);
		workBundleComplianceCriterion = mock(WorkBundleComplianceCriterion.class);
			when(workBundleComplianceCriterion.getUser()).thenReturn(user);
			when(workBundleComplianceCriterion.getWork()).thenReturn(bundle);
			when(workBundleComplianceCriterion.getSchedule()).thenReturn(schedule1);
			when(workBundleComplianceCriterion.isMet()).thenReturn(true);
		assignmentCountComplianceRule = mock(AssignmentCountComplianceRule.class);
			when(assignmentCountComplianceRule.getPeriodType()).thenReturn(PeriodicComplianceRule.PeriodType.WEEK);
			when(assignmentCountComplianceRule.getMaxAssignments()).thenReturn(2L);
		when(workService.countWorkByCompanyUserRangeAndStatus(anyLong(), anyLong(), anyList(), (Calendar) anyObject(), (Calendar) anyObject(), anyList())).thenReturn(1);
		when(workBundleService.findById(anyLong(), anyBoolean())).thenReturn(bundle);

		bundledWork.add(work1);
		bundledWork.add(work2);
		bundledWork.add(work3);
	}

	@Test
	public void visitBundle_withAssignmentCountComplianceRule_bundleVisitCalled() {
		when(workBundleComplianceCriterion.getWork()).thenReturn(bundle);
		visitor.visit(workBundleComplianceCriterion, assignmentCountComplianceRule);
		verify(visitor, times(1)).visit(workBundleComplianceCriterion, assignmentCountComplianceRule);
	}

	@Test
	public void visitBundle_withAssignmentCountComplianceRule_setCriterionMet() {
		when(assignmentCountComplianceRule.getMaxAssignments()).thenReturn(4L);
		when(workBundleComplianceCriterion.getWork()).thenReturn(bundle);
		visitor.visit(workBundleComplianceCriterion, assignmentCountComplianceRule);
		verify(workBundleComplianceCriterion).setMet(true);
	}

	@Test
	public void visitBundle_withAssignmentCountComplianceRule_notSetCriterionMet() {
		when(workBundleComplianceCriterion.getWork()).thenReturn(bundle);
		visitor.visit(workBundleComplianceCriterion, assignmentCountComplianceRule);
		verify(workBundleComplianceCriterion).setMet(false);
	}

	@Test
	public void visitBundle_withAssignmentCountComplianceRule_EndOfPeriod_notSetCriterionMet() {
		when(work1.getScheduleFrom()).thenReturn(lastDay);
		when(work2.getScheduleFrom()).thenReturn(lastDay);
		when(work3.getScheduleFrom()).thenReturn(lastDay);
		when(workBundleComplianceCriterion.getWork()).thenReturn(bundle);
		visitor.visit(workBundleComplianceCriterion, assignmentCountComplianceRule);
		verify(workBundleComplianceCriterion).setMet(false);
	}

	@Test
	public void visit_withAssignmentCountComplianceRule_setCriterionMet() {
		visitor.visit(workComplianceCriterion, assignmentCountComplianceRule);
		verify(workComplianceCriterion).setMet(true);
	}

	@Test
	public void visit_withAssignmentCountComplianceRule_notSetCriterionMet() {
		when(workService.countWorkByCompanyUserRangeAndStatus(anyLong(), anyLong(), anyList(), (Calendar) anyObject(), (Calendar) anyObject(), anyList())).thenReturn(2);
		visitor.visit(workComplianceCriterion, assignmentCountComplianceRule);
		verify(workComplianceCriterion).setMet(false);
	}
}
