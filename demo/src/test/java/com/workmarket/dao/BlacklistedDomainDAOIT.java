package com.workmarket.dao;

import com.workmarket.domains.model.BlacklistedDomain;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.utility.RandomUtilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public class BlacklistedDomainDAOIT extends BaseServiceIT {

	@Autowired BlacklistedDomainDAO blacklistedDomainDAO;

	@Test @Transactional
	public void save_blacklistedDomain_matchesEmail() {
		String domain = String.format("%s.com", RandomUtilities.generateAlphaString(10));
		blacklistedDomainDAO.saveOrUpdate(new BlacklistedDomain(domain));

		String email = String.format("%s@%s", RandomUtilities.generateAlphaString(10), domain);
		BlacklistedDomain loadedDomain = blacklistedDomainDAO.findBy("domain", domain);

		assertTrue(loadedDomain.isEmailMatch(email));
	}
}
