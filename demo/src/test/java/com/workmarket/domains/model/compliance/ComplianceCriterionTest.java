package com.workmarket.domains.model.compliance;

import com.workmarket.domains.compliance.model.WorkComplianceCriterion;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ComplianceCriterionTest {
	WorkComplianceCriterion workComplianceCriterion1;
	WorkComplianceCriterion workComplianceCriterion2;
	User user1;
	User user2;
	Work work1;
	Work work2;
	DateRange schedule1;
	DateRange schedule2;
	Calendar from;
	Calendar through;

	@Before
	public void setup() {
		user1 = new User();
		user2 = new User();
		work1 = new Work();
		work2 = new Work();
		from = Calendar.getInstance();
		from.add(Calendar.DATE, 1);
		through = Calendar.getInstance();
		through.add(Calendar.DATE, 3);
		schedule1 = new DateRange(from, through);
		schedule2 = new DateRange(from, through);

		user1.setId(2000L);
		user2.setId(2000L);
		work1.setId(2000L);
		work2.setId(2000L);

		workComplianceCriterion1 = new WorkComplianceCriterion(user1, work1, schedule1);
		workComplianceCriterion2 = new WorkComplianceCriterion(user2, work2, schedule2);
	}

	@Test
	public void equals_isEqual() {
		assertEquals(true, workComplianceCriterion1.equals(workComplianceCriterion2));
	}

	@Test
	public void hashcode_isEqual() {
		assertEquals(workComplianceCriterion1.hashCode(), workComplianceCriterion2.hashCode());
	}

	@Test
	public void equals_workNotEqual() {
		work2.setId(2001L);
		assertEquals(false, workComplianceCriterion1.equals(workComplianceCriterion2));
	}

	@Test
	public void equals_userNotEqual() {
		user2.setId(2001L);
		assertEquals(false, workComplianceCriterion1.equals(workComplianceCriterion2));
	}

	@Test
	public void equals_scheduleNotEqual() {
		schedule2.setFrom(Calendar.getInstance());
		from.add(Calendar.DATE, 2);
		assertEquals(false, workComplianceCriterion1.equals(workComplianceCriterion2));
	}
}
