package com.workmarket.dao.linkedin;


import com.workmarket.dao.linkedin.LinkedInDAO.LinkedInRestriction;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.linkedin.LinkedInPerson;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class LinkedInDAOIT extends BaseServiceIT {
	@Autowired LinkedInDAO linkedInDAO;

	private void setLinkedInPersonProperties(LinkedInPerson linkedInPerson) {
		linkedInPerson.setLinkedInId("abcdefgh");
		linkedInPerson.setFirstName("Bob");
		linkedInPerson.setLastName("Smith");
		linkedInPerson.setEmailAddress("me@me.com");
	}

	@Test
	@Transactional
	public void test_saveLinkedInDataWithUser() throws Exception {
		LinkedInPerson linkedInPerson = new LinkedInPerson();
		setLinkedInPersonProperties(linkedInPerson);

		User u = newInternalUser();
		linkedInPerson.setUser(u);
		linkedInDAO.saveOrUpdateLinkedInPerson(linkedInPerson);

		LinkedInPerson lip = linkedInDAO.findBy("linkedInId", "abcdefgh");
		Assert.assertNotNull(lip);
		Assert.assertTrue(lip.getFirstName().equals("Bob"));
		Assert.assertNotNull(lip.getUser());
	}

	@Test
	@Transactional
	public void test_saveLinkedInDataWithoutUser() throws Exception {
		LinkedInPerson linkedInPerson = new LinkedInPerson();
		setLinkedInPersonProperties(linkedInPerson);
		linkedInDAO.saveOrUpdateLinkedInPerson(linkedInPerson);

		LinkedInPerson lip = linkedInDAO.findBy("linkedInId", "abcdefgh");
		Assert.assertNotNull(lip);
		Assert.assertTrue(lip.getFirstName().equals("Bob"));
	}

	@Test
	@Transactional
	public void test_findMostRecentByLinkedInId() throws Exception {
		LinkedInPerson linkedInPerson = new LinkedInPerson();
		setLinkedInPersonProperties(linkedInPerson);
		linkedInDAO.saveOrUpdate(linkedInPerson);
		Long firstId = linkedInPerson.getId();

		LinkedInPerson linkedInPerson2 = new LinkedInPerson();
		setLinkedInPersonProperties(linkedInPerson2);
		linkedInDAO.saveOrUpdate(linkedInPerson2);
		Long secondId = linkedInPerson2.getId();

		Assert.assertTrue(!firstId.equals(secondId));

		LinkedInPerson foundLinkedInPerson =
			linkedInDAO.findMostRecentLinkedInPersonByLinkedInId(
				linkedInPerson.getLinkedInId(), LinkedInRestriction.WITHOUT_USER
			);
		Assert.assertTrue(secondId.equals(foundLinkedInPerson.getId()));
	}

	@Test
	@Transactional
	public void test_LinkedInDAO_Restriction() throws Exception {
		// with user
		LinkedInPerson linkedInPerson2 = new LinkedInPerson();
		setLinkedInPersonProperties(linkedInPerson2);

		User u = newInternalUser();
		linkedInPerson2.setUser(u);
		linkedInDAO.saveOrUpdate(linkedInPerson2);
		Long idWithUser = linkedInPerson2.getId();

		// without user
		LinkedInPerson linkedInPerson = new LinkedInPerson();
		setLinkedInPersonProperties(linkedInPerson);
		linkedInDAO.saveOrUpdateLinkedInPerson(linkedInPerson);
		Long idWithoutUser = linkedInPerson.getId();

		Assert.assertTrue(
			linkedInDAO.findMostRecentLinkedInPersonByLinkedInId(
				linkedInPerson.getLinkedInId(), LinkedInRestriction.WITH_USER
			).getId().equals(idWithUser)
		);

		Assert.assertTrue(
				linkedInDAO.findMostRecentLinkedInPersonByLinkedInId(
						linkedInPerson.getLinkedInId(), LinkedInRestriction.WITHOUT_USER
				).getId().equals(idWithoutUser)
		);
	}
}
