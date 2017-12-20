package com.workmarket.dao;

import com.workmarket.dao.decisionflow.WorkToDecisionFlowAssociationDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.decisionflow.WorkToDecisionFlowAssociation;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkToDecisionFlowAssociationDAOIT extends BaseServiceIT {
	@Autowired WorkToDecisionFlowAssociationDAO dao;

	Work work;
	User user;
	String uuid1;
	@Before
	public void setUp() throws Exception {
		user = newInternalUser();
		work = newWork(user.getId());
		uuid1 = UUID.randomUUID().toString();
	}

	@Test
	@Transactional
	public void crud_test() throws Exception {
		dao.addDecisionFlowAssociation(work, uuid1);
		WorkToDecisionFlowAssociation association = dao.findDecisionFlowAssociation(work.getId());
		assertEquals(association.getDecisionFlowUuid(), uuid1);

		String uuid2 = dao.findDecisionFlowUuid(work.getId());
		assertEquals(uuid1, uuid2);
	}

}
