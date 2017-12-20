package com.workmarket.api.v2.employer.clientcontacts.controllers;

import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.mapType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ClientContactsControllerIT extends ApiV2BaseIT{

	private static final String ENDPOINT = "/employer/v2/client_contacts";

	private ClientCompany clientCompany;
	private ClientContact clientContact;

	@Before
	public void setUp() throws Exception {
		login();
		clientCompany = newClientCompany(user.getId());
		clientContact = newClientContactForCompany(user.getCompany().getId());

		PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO();
		phoneNumberDTO.setPhone("999-999-9999");
		phoneNumberDTO.setExtension("1234");
		phoneNumberDTO.setContactContextType(ContactContextType.WORK);
		crmService.addPhoneToClientContact(clientContact.getId(), phoneNumberDTO);

		EmailAddressDTO emailAddressDTO = new EmailAddressDTO();
		emailAddressDTO.setEmail("abc@domain.com");
		emailAddressDTO.setContactContextType(ContactContextType.WORK);
		crmService.addEmailToClientContact(clientContact.getId(), emailAddressDTO);
		crmService.updateClientCompanyForClientContact(clientContact.getId(), clientCompany.getId());

		clientContact = crmService.findClientContactById(clientContact.getId());
	}

	@Test
	public void getClientContactsByClientCompanyByName_Success() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("clientId", String.valueOf(clientCompany.getId()))
				.param("contactName", clientContact.getFullName())
				.param("fields", "id", "name", "number", "email")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);
		assertThat(result, hasEntry("id", String.valueOf(clientContact.getId())));
		assertThat(result, hasEntry("name", String.valueOf(clientContact.getFullName())));
		assertThat(result, hasEntry("number", String.valueOf(clientContact.getMostRecentWorkPhone().getPhone())));
		assertThat(result, hasEntry("email", String.valueOf(clientContact.getMostRecentEmail().getEmail())));
	}

	@Test
	public void getClientContactsByNameWith_Success() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("contactName", clientContact.getFullName())
				.param("fields", "id", "name", "number", "email")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);
		assertThat(result, hasEntry("id", String.valueOf(clientContact.getId())));
		assertThat(result, hasEntry("name", String.valueOf(clientContact.getFullName())));
		assertThat(result, hasEntry("number", String.valueOf(clientContact.getMostRecentWorkPhone().getPhone())));
		assertThat(result, hasEntry("email", String.valueOf(clientContact.getMostRecentEmail().getEmail())));
	}
}
