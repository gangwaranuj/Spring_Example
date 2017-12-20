package com.workmarket.service.business;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.dto.SuggestionDTO;
import com.workmarket.service.infra.business.SuggestionService;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class SuggestionServiceIT extends BaseServiceIT {
	@Autowired SuggestionService suggestionService;

	@Test
	public void test_suggestProject() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		ClientCompany clientCompany = newClientCompany(employee.getId());
		Project blue = newProject(employee.getId(), clientCompany.getId(), "Color: Blue");
		Project blueGreen = newProject(employee.getId(), clientCompany.getId(), "Color: Blue green");
		Project orange = newProject(employee.getId(), clientCompany.getId(), "Color: Orange");

		List<SuggestionDTO> suggestions = suggestionService.suggestProject("blue", employee.getId());
		Assert.assertTrue(suggestions.size() == 2);

		suggestions = suggestionService.suggestProject("or", employee.getId());
		Assert.assertTrue(suggestions.size() == 3);

		suggestions = suggestionService.suggestProject("ora", employee.getId());
		Assert.assertTrue(suggestions.size() == 1);
		Assert.assertTrue(suggestions.get(0).getId().equals(orange.getId()));
	}
}
