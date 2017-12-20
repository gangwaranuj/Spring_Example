package com.workmarket.service.business;

import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.service.business.dto.SpecialtyDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class SpecialtyServiceIT extends BaseServiceIT {
  @Autowired
  SpecialtyService specialtyService;

  private Specialty specialty;
  private String specialtyName;
  private String specialtyDescription;

  @Before
  public void before() throws Exception {
    specialtyName = "specialty" + RandomUtilities.nextLong();
    specialtyDescription = "description" + RandomUtilities.nextLong();
    final SpecialtyDTO dto = new SpecialtyDTO();
    dto.setName(specialtyName);
    dto.setDescription(specialtyDescription);
    dto.setIndustryId(INDUSTRY_ID_1000);
    specialty = specialtyService.saveOrUpdateSpecialty(dto);
  }

  @Test
  @Transactional
  public void shouldUndeleteADuplicateSkill() {
    specialty.setDeleted(true);
    specialtyService.saveOrUpdateSpecialty(specialty);
    final SpecialtyDTO dto = new SpecialtyDTO();
    dto.setName(specialtyName);
    dto.setDescription(specialtyDescription);
    dto.setIndustryId(INDUSTRY_ID_1000);

    final Specialty specialty2 = specialtyService.saveOrUpdateSpecialty(dto);

    assertFalse(specialty.getDeleted());
    assertEquals(specialty.getId(), specialty2.getId());
    assertEquals(specialty.getName(), specialty2.getName());
    assertEquals(specialty.getDescription(), specialty2.getDescription());
  }

}