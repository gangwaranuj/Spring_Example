package com.workmarket.dao.crm;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientContactPagination;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.ClientContactDTO;
import org.junit.Assert;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: alexsilva Date: 5/6/14 Time: 9:59 AM
 */

@RunWith(SpringJUnit4ClassRunner.class)
@NotThreadSafe
public class ClientContactDAOImplIT extends BaseServiceIT {

	@Test
	public void findClientContacts_withPagination_emptyResults() throws Exception {
		User user = newFirstEmployee();
		ClientContactPagination pagination = new ClientContactPagination();
		ClientContactPagination results = crmService.findAllClientContactsByUser(user.getId(), pagination);

		Assert.assertNotNull(results);
		Assert.assertNotNull(results.getResults());
		Assert.assertEquals(results.getResults().size(), 0);
	}

	@Test
	public void findClientContacts_withPagination_withClient_oneResult() throws Exception {
		User user = newFirstEmployee();

		ClientContactDTO clientContactDTO = createClientContactDTO();
		ClientCompany clientCompany = newClientCompany(user.getId());
		clientContactDTO.setClientCompanyId(clientCompany.getId());

		crmService.saveOrUpdateClientContact(user.getCompany().getId(), clientContactDTO, null);

		ClientContactPagination pagination = new ClientContactPagination();
		ClientContactPagination results = crmService.findAllClientContactsByUser(user.getId(), pagination);

		Assert.assertNotNull(results);
		Assert.assertNotNull(results.getResults());
		Assert.assertEquals(results.getResults().size(), 1);
	}

	@Test
	public void findClientContacts_withPagination_noClient_oneResult() throws Exception {
		User user = newFirstEmployee();

		crmService.saveOrUpdateClientContact(user.getCompany().getId(), createClientContactDTO(), null);

		ClientContactPagination pagination = new ClientContactPagination();
		ClientContactPagination results = crmService.findAllClientContactsByUser(user.getId(), pagination);

		Assert.assertNotNull(results);
		Assert.assertNotNull(results.getResults());
		Assert.assertEquals(results.getResults().size(), 1);
	}

	@Test
	public void findClientContacts_withPagination_deletedClient_emptyResults() throws Exception {
		User user = newFirstEmployee();

		ClientCompany clientCompany = newClientCompany(user.getId());
		clientCompany.setDeleted(true);
		crmService.saveOrUpdateClientCompany(clientCompany);

		ClientContactDTO clientContactDTO = createClientContactDTO();
		clientContactDTO.setClientCompanyId(clientCompany.getId());

		crmService.saveOrUpdateClientContact(user.getCompany().getId(), clientContactDTO, null);

		ClientContactPagination pagination = new ClientContactPagination();
		ClientContactPagination results = crmService.findAllClientContactsByUser(user.getId(), pagination);

		Assert.assertNotNull(results);
		Assert.assertNotNull(results.getResults());
		Assert.assertEquals(results.getResults().size(), 0);
	}

	private ClientContactDTO createClientContactDTO() {
		ClientContactDTO clientContactDTO = new ClientContactDTO();
		clientContactDTO.setFirstName("First");
		clientContactDTO.setLastName("Last");
		clientContactDTO.setJobTitle("Web Developer");
		clientContactDTO.setManager(true);
		return clientContactDTO;
	}

}
