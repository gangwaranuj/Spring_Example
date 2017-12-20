package com.workmarket.service.business;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.tool.Tool;
import com.workmarket.domains.model.tool.ToolPagination;
import com.workmarket.domains.model.tool.UserToolAssociation;
import com.workmarket.service.business.dto.ToolDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ToolServiceIT extends BaseServiceIT {

	@Autowired private ToolService toolService;

	@Test
	public void test_findToolById() throws Exception {
		ToolDTO dto = newToolDTO();
		Tool tool = toolService.saveOrUpdateTool(dto);

		tool = toolService.findToolById(tool.getId());
		Assert.assertNotNull(tool);
		Assert.assertEquals(dto.getName(), tool.getName());
	}

	@Test
	public void test_findAllApprovedTools() throws Exception {
		ToolPagination pagination = new ToolPagination();
		pagination.setReturnAllRows();
		pagination = toolService.findAllTools(pagination);

		Assert.assertTrue(pagination.getRowCount() > 0);

		for (Tool tool : pagination.getResults()) {
			Assert.assertNotNull(tool.getName());
			Assert.assertNotNull(tool.getIndustry().getName());
		}
	}

	@Test
	public void test_findAllSpecialtiesByUser() throws Exception {

		User contractor = newContractorIndependent();

		ToolPagination pagination = new ToolPagination();
		pagination.setReturnAllRows();

		toolService.addToolToUser(newTool().getId().intValue(), contractor.getId());
		toolService.addToolToUser(newTool().getId().intValue(), contractor.getId());
		toolService.addToolToUser(newTool().getId().intValue(), contractor.getId());
		toolService.addToolToUser(newTool().getId().intValue(), contractor.getId());

		pagination = toolService.findAllToolsByUser(contractor.getId(), pagination);

		Assert.assertEquals(Integer.valueOf(4), pagination.getRowCount());

		for (Tool tool : pagination.getResults()) {
			Assert.assertNotNull(tool.getName());
			Assert.assertNotNull(tool.getIndustry().getName());
		}
	}

	@Test
	public void test_addToolToUser() throws Exception {
		Tool tool = newTool();
		User newContractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		toolService.addToolToUser(tool.getId().intValue(), newContractor.getId());
		ToolPagination pagination = toolService.findAllToolsByUser(newContractor.getId(), new ToolPagination(true));

		Assert.assertEquals(1, pagination.getResults().size());
		Assert.assertTrue(pagination.getResults().contains(toolService.findToolById(tool.getId())));
	}

	@Test
	public void test_removeToolFromUser() throws Exception {
		Tool tool = newTool();
		User newContractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		toolService.addToolToUser(tool.getId().intValue(), newContractor.getId());

		ToolPagination pagination = toolService.findAllToolsByUser(newContractor.getId(), new ToolPagination(true));

		Assert.assertEquals(1, pagination.getResults().size());
		Assert.assertTrue(pagination.getResults().contains(toolService.findToolById(tool.getId())));

		toolService.removeToolFromUser(tool.getId().intValue(), newContractor.getId());

		pagination = toolService.findAllToolsByUser(newContractor.getId(), new ToolPagination(true));

		Assert.assertEquals(0, pagination.getResults().size());
	}

	@Test
	public void test_setToolsOfUser() throws Exception {
		Tool tool1 = newTool();
		Tool tool2 = newTool();
		Tool tool3 = newTool();
		User newContractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		toolService.setToolsOfUser(new Integer[]{tool1.getId().intValue(), tool2.getId().intValue()}, newContractor.getId());

		ToolPagination pagination = toolService.findAllToolsByUser(newContractor.getId(), new ToolPagination(true));

		Assert.assertEquals(2, pagination.getResults().size());
		Assert.assertTrue(pagination.getResults().contains(toolService.findToolById(tool1.getId())));
		Assert.assertTrue(pagination.getResults().contains(toolService.findToolById(tool2.getId())));

		toolService.setToolsOfUser(new Integer[]{tool2.getId().intValue(), tool3.getId().intValue()}, newContractor.getId());

		pagination = toolService.findAllToolsByUser(newContractor.getId(), new ToolPagination(true));

		Assert.assertEquals(2, pagination.getResults().size());
		Assert.assertTrue(pagination.getResults().contains(toolService.findToolById(tool2.getId())));
		Assert.assertTrue(pagination.getResults().contains(toolService.findToolById(tool3.getId())));

		toolService.setToolsOfUser(new Integer[]{}, newContractor.getId());

		pagination = toolService.findAllToolsByUser(newContractor.getId(), new ToolPagination(true));

		Assert.assertEquals(0, pagination.getResults().size());
		Assert.assertFalse(pagination.getResults().contains(toolService.findToolById(tool1.getId())));
		Assert.assertFalse(pagination.getResults().contains(toolService.findToolById(tool2.getId())));
		Assert.assertFalse(pagination.getResults().contains(toolService.findToolById(tool3.getId())));

	}

	@Test
	public void test_findAllToolsByIndustry() throws Exception {
		ToolPagination pagination = toolService.findAllToolsByIndustry(INDUSTRY_1000_ID.intValue(), new ToolPagination(true));
		Assert.assertTrue(pagination.getResults().size() > 0);
	}

	@Test
	public void test_setProficiencyLevels() throws Exception {
		Tool tool1 = newTool();
		Tool tool2 = newTool();
		User newContractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		toolService.setToolsOfUser(new Integer[]{tool1.getId().intValue(), tool2.getId().intValue()}, newContractor.getId());

		toolService.setProficiencyLevelsForUser(
			new Integer[]{tool1.getId().intValue(), tool2.getId().intValue()},
			new Integer[]{21, 89},
			newContractor.getId()
		);

		UserToolAssociation association = toolService.findAssociationsByToolAndUser(
			tool1.getId().intValue(), newContractor.getId());

		Assert.assertTrue(association.getProficiencyLevel().equals(21));

		association = toolService.findAssociationsByToolAndUser(
			tool2.getId().intValue(), newContractor.getId());

		Assert.assertTrue(association.getProficiencyLevel().equals(89));
	}

	@Test
	public void test_saveOrUpdateTool() throws Exception {
		ToolDTO dto = newToolDTO();
		Tool tool = toolService.saveOrUpdateTool(dto);
		Assert.assertNotNull(tool);
		Assert.assertTrue(tool.getIndustry().getId().equals(INDUSTRY_1000_ID));
	}

	@Test
	public void test_findToolByName() throws Exception {
		Tool tool = newTool();
		Tool tool2 = toolService.findToolByNameAndIndustryId(tool.getName(), INDUSTRY_1000_ID);
		Assert.assertNotNull(tool2);
	}

	@Test
	public void test_declineTool() throws Exception {
		Tool tool1 = newTool();
		User newContractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		toolService.setToolsOfUser(new Integer[]{tool1.getId().intValue()}, newContractor.getId());

		ToolPagination pagination = new ToolPagination(true);

		pagination = toolService.findAllActiveToolsByUser(newContractor.getId(), pagination);

		Assert.assertEquals(1, pagination.getResults().size());

		toolService.declineTool(tool1.getId());

		pagination = toolService.findAllActiveToolsByUser(newContractor.getId(), pagination);

		Assert.assertEquals(0, pagination.getResults().size());
	}


}
