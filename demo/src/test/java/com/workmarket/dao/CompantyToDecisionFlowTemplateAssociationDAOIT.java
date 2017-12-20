package com.workmarket.dao;

import com.workmarket.dao.decisionflow.CompanyToDecisionFlowTemplateAssociationDAO;
import com.workmarket.dao.decisionflow.WorkToDecisionFlowAssociationDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.decisionflow.CompanyToDecisionFlowTemplateAssociation;
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

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class CompantyToDecisionFlowTemplateAssociationDAOIT extends BaseServiceIT {
	@Autowired CompanyToDecisionFlowTemplateAssociationDAO dao;

	Company company;
	String uuid1;
	String uuid2;
	@Before
	public void setUp() throws Exception {
		company = newCompany();
		uuid1 = UUID.randomUUID().toString();
		uuid2 = UUID.randomUUID().toString();
	}

	@Test
	@Transactional
	public void crud_test() throws Exception {
		dao.addDecisionFlowTemplateAssociation(company, uuid1);
		dao.addDecisionFlowTemplateAssociation(company, uuid2);
		List<CompanyToDecisionFlowTemplateAssociation> associations1 =
			dao.findDecisionFlowTemplateAssociations(company.getId());
		assertEquals(2, associations1.size());

		List<String> uuids1 = dao.findDecisionFlowTemplateUuids(company.getId());
		assertTrue(uuids1.contains(uuid1) && uuids1.contains(uuid2));

		dao.delete(associations1.get(0));
		List<CompanyToDecisionFlowTemplateAssociation> associations2 =
			dao.findDecisionFlowTemplateAssociations(company.getId());
		assertEquals(1, associations2.size());

		CompanyToDecisionFlowTemplateAssociation association = dao.findDecisionFlowTemplateAssociation(
				company.getId(),
				associations1.get(1).getDecisionFlowTemplateUuid()
			);
		assertNotNull(association);

		association = dao.findDecisionFlowTemplateAssociation(
			company.getId(),
			associations1.get(0).getDecisionFlowTemplateUuid()
		);
		assertNull(association);

		List<String> uuids2 = dao.findDecisionFlowTemplateUuids(company.getId());
		assertFalse(uuids2.contains(associations1.get(0).getDecisionFlowTemplateUuid()));
		assertTrue(uuids2.contains(associations1.get(1).getDecisionFlowTemplateUuid()));
	}
}
