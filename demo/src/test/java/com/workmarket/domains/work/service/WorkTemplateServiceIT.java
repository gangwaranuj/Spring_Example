package com.workmarket.domains.work.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.work.model.WorkTemplatePagination;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.WorkTemplateDTO;
import com.workmarket.test.IntegrationTest;

import org.apache.commons.collections.MapUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkTemplateServiceIT extends BaseServiceIT {

	@Autowired private WorkTemplateService workTemplateService;

	@Test
	public void test_saveOrUpdateWorkTemplate_empty() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		WorkTemplateDTO workTemplateDTO = newWorkTemplateBlankDTO();

		WorkTemplate template = workTemplateService.saveOrUpdateWorkTemplate(employee.getId(), workTemplateDTO);
	}

	@Test
	public void test_findWorkTemplatesById() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		WorkTemplateDTO workDTO = newWorkTemplateBlankDTO();

		WorkTemplate template = workTemplateService.saveOrUpdateWorkTemplate(employee.getId(), workDTO);

		template = workTemplateService.findWorkTemplateById(template.getId());

		Assert.assertNotNull(template);
	}

	@Test
	public void test_findAllActiveWorkTemplates() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		WorkTemplateDTO workDTO = newWorkTemplateBlankDTO();

		workTemplateService.saveOrUpdateWorkTemplate(employee.getId(), workDTO);

		WorkTemplatePagination pagination = new WorkTemplatePagination();
		pagination.setReturnAllRows();

		pagination = workTemplateService.findAllActiveWorkTemplates(employee.getCompany().getId(), pagination);

		Assert.assertEquals(1, pagination.getRowCount().intValue());
	}

	@Test
	public void test_findAllInActiveWorkTemplates() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		WorkTemplateDTO workDTO = newWorkTemplateBlankDTO();

		WorkTemplate template = workTemplateService.saveOrUpdateWorkTemplate(employee.getId(), workDTO);
		workTemplateService.toggleWorkTemplateActiveStatusById(template.getId());


		WorkTemplatePagination pagination = new WorkTemplatePagination();
		pagination.setReturnAllRows();

		pagination = workTemplateService.findAllTemplatesByStatusCode(employee.getCompany().getId(), pagination,
				WorkStatusType.DEACTIVATED);

		Assert.assertEquals(1, pagination.getRowCount().intValue());
	}

	@Test
	public void test_findAllActiveWorkTemplates_list() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		WorkTemplateDTO workDTO = newWorkTemplateBlankDTO();

		WorkTemplate template = workTemplateService.saveOrUpdateWorkTemplate(employee.getId(), workDTO);

		List<WorkTemplate> list = workTemplateService.findAllActiveWorkTemplates(employee.getCompany().getId());

		Assert.assertEquals(1, list.size());
	}

	@Test
	public void test_deleteWorkTemplates() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		WorkTemplateDTO workDTO = newWorkTemplateBlankDTO();

		WorkTemplate template = workTemplateService.saveOrUpdateWorkTemplate(employee.getId(), workDTO);

		WorkTemplatePagination pagination = new WorkTemplatePagination();
		pagination.setReturnAllRows();

		pagination = workTemplateService.findAllActiveWorkTemplates(employee.getCompany().getId(), pagination);

		Assert.assertEquals(Integer.valueOf(1), pagination.getRowCount());

		workTemplateService.deleteWorkTemplate(template.getId());

		pagination = workTemplateService.findAllActiveWorkTemplates(employee.getCompany().getId(), pagination);

		Assert.assertEquals(Integer.valueOf(0), pagination.getRowCount());
	}

	@Test
	public void test_deleteWorkTemplates1() throws Exception {
		List<WorkTemplate> wt = workTemplateService.findAllActiveWorkTemplates(1000L);
	}

	@Test
	public void test_findWorkTemplateByName() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		WorkTemplateDTO workTemplateDTO = newWorkTemplateBlankDTO();
		workTemplateDTO.setTemplateName("unique name");

		WorkTemplate template = workTemplateService.saveOrUpdateWorkTemplate(employee.getId(), workTemplateDTO);

		WorkTemplate find = workTemplateService.findWorkTemplateByName(employee.getCompany().getId(), "Unique Name");
		Assert.assertNotNull(find);

		Assert.assertEquals(template, find);
	}

	@Test
	public void testFindTemplateMap() throws Exception {
		Map<Long, String> map = workTemplateService.findAllActiveWorkTemplatesIdNameMap(COMPANY_ID);
		Assert.assertNotNull(map);
		Assert.assertTrue(map.size() > 0);
	}

	public WorkTemplateService getWorkTemplateService() {
		return workTemplateService;
	}

	public void setWorkTemplateService(WorkTemplateService workTemplateService) {
		this.workTemplateService = workTemplateService;
	}

	@Test
	public void testFindAllActiveWorkTemplatesWorkNumberNameMap() throws Exception {
		Map<String, Map<String, Object>> map =
				workTemplateService.findAllActiveWorkTemplatesWorkNumberNameMap(COMPANY_ID, null);
		Assert.assertNotNull(map);
		Assert.assertTrue(map.size() > 0);
		for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
			Assert.assertNotNull(entry.getKey());
			Assert.assertNotNull(entry.getValue().get("template_name"));
			Assert.assertTrue(entry.getValue().containsKey("client_id"));
		}
	}

	@Test
	public void filterTemplatesByClientId() throws Exception {
		// create first template
		User user = newFirstEmployeeWithCashBalance();
		ClientCompany clientCompany = newClientCompany(user.getId());
		WorkTemplateDTO workDTO = newWorkTemplateBlankDTO();
		workDTO.setClientCompanyId(clientCompany.getId());
		WorkTemplate template = workTemplateService.saveOrUpdateWorkTemplate(user.getId(), workDTO);

		// create second template with different client company ID
		final User user2 = newFirstEmployeeWithCashBalance();
		final ClientCompany clientCompany2 = newClientCompany(user2.getId());
		final WorkTemplateDTO workDTO2 = newWorkTemplateBlankDTO();
		workDTO2.setClientCompanyId(clientCompany2.getId());
		workTemplateService.saveOrUpdateWorkTemplate(user2.getId(), workDTO2);

		Map<String, Map<String, Object>> map =
			workTemplateService.findAllActiveWorkTemplatesWorkNumberNameMap(user.getCompany().getId(), null);
		Assert.assertTrue(MapUtils.isNotEmpty(map));
		Assert.assertEquals(1, map.size());
		for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
			Assert.assertEquals(clientCompany.getId(), entry.getValue().get("client_id"));
		}
	}

	@Test
	public void filterTemplatesByClientIdEmpty() throws Exception {
		User user = newFirstEmployeeWithCashBalance();
		Map<String, Map<String, Object>> map =
				workTemplateService.findAllActiveWorkTemplatesWorkNumberNameMap(user.getCompany().getId(), null);

		Assert.assertTrue(MapUtils.isEmpty(map));
	}
}
