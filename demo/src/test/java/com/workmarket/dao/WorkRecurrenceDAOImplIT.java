package com.workmarket.dao;

	import com.workmarket.domains.model.User;
	import com.workmarket.domains.model.skill.Skill;
	import com.workmarket.domains.work.dao.WorkRecurrenceAssociationDAO;
	import com.workmarket.domains.work.model.AbstractWork;
	import com.workmarket.domains.work.model.WorkRecurrenceAssociation;
	import com.workmarket.service.business.BaseServiceIT;
	import com.workmarket.test.IntegrationTest;
	import org.junit.Before;
	import org.junit.Test;
	import org.junit.experimental.categories.Category;
	import org.junit.runner.RunWith;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
	import org.springframework.transaction.annotation.Transactional;

	import java.util.List;

	import static org.junit.Assert.assertEquals;
	import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkRecurrenceDAOImplIT extends BaseServiceIT {

	@Autowired WorkRecurrenceAssociationDAO workRecurrenceAssociationDAO;

	private User employee;

	@Before
	public void initEmployee() throws Exception {
		employee = newFirstEmployee();
	}

	@Test
	@Transactional
	public void addWorkRecurrence() throws Exception {
		AbstractWork recurringWork = newWork(employee.getId());
		AbstractWork work = newWork(employee.getId());
		String recurrenceUUID = "asdfghjkl";
		workRecurrenceAssociationDAO.addWorkRecurrence(work, recurringWork, recurrenceUUID);

		WorkRecurrenceAssociation workRecurrenceAssociation = workRecurrenceAssociationDAO.findWorkRecurrenceAssociation(work.getId());
		assertNotNull(workRecurrenceAssociation);
		assertEquals(recurrenceUUID, workRecurrenceAssociation.getWorkRecurrence().getRecurrenceUUID());
		assertEquals(work.getId(), workRecurrenceAssociation.getWorkRecurrence().getWorkId());
		assertEquals(recurringWork.getId(), workRecurrenceAssociation.getRecurringWorkId());
	}
}
