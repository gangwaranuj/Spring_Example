package com.workmarket.api.v2.employer.assignments.services;

import com.google.common.collect.ImmutableList;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.WorkTemplateDTO;
import com.workmarket.test.IntegrationTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class GetTemplatesUseCaseIT extends BaseServiceIT {
  @Autowired
  private GetTemplatesUseCase getTemplatesUseCase;

  @Test
  public void filterTemplatesByClientId() throws Exception {
    // create first template for user
    final User user = newFirstEmployeeWithCashBalance();
    final ClientCompany clientCompany = newClientCompany(user.getId());
    final WorkTemplateDTO workDTO = newWorkTemplateBlankDTO();
    workDTO.setClientCompanyId(clientCompany.getId());
    final WorkTemplate template = workTemplateService.saveOrUpdateWorkTemplate(user.getId(), workDTO);

    // create second template for user2 with different client company
    final User user2 = newFirstEmployeeWithCashBalance();
    final ClientCompany clientCompany2 = newClientCompany(user2.getId());
    final WorkTemplateDTO workDTO2 = newWorkTemplateBlankDTO();
    workDTO2.setClientCompanyId(clientCompany2.getId());
    workTemplateService.saveOrUpdateWorkTemplate(user2.getId(), workDTO2);

    getTemplatesUseCase.findAllActiveWorkTemplates(user.getCompany().getId()); // template for first user
    final ImmutableList<Map> mapList = getTemplatesUseCase.andReturn();
    assertEquals(1, mapList.size());
    final Map<String, String> map = getTemplatesUseCase.andReturn().get(0);
    assertEquals(template.getTemplateName(), map.get("name"));
    assertEquals(clientCompany.getId().toString(), map.get("clientId"));
  }

  @Test
  public void filterTemplatesByClientIdEmpty() throws Exception {
    final User userWithoutTemplates = newFirstEmployeeWithCashBalance();

    getTemplatesUseCase.findAllActiveWorkTemplates(userWithoutTemplates.getCompany().getId());
    final ImmutableList<Map> mapList = getTemplatesUseCase.andReturn();

    assertEquals(0, mapList.size());
  }
}