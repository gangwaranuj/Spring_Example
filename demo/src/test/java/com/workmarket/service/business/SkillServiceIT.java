package com.workmarket.service.business;

import com.workmarket.dao.skill.SkillDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.skill.SkillPagination;
import com.workmarket.domains.model.skill.UserSkillAssociation;
import com.workmarket.service.business.dto.SkillDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class SkillServiceIT extends BaseServiceIT {

	@Autowired private SkillService skillService;

	@Autowired private SkillDAO skillDAO;

	private Skill newSkill;
	private User newContractor;
	private String skillName;
	private String skillDescription;

	@Before
	public void before() throws Exception {

		skillName = "skill" + RandomUtilities.nextLong();
		skillDescription = "description" + RandomUtilities.nextLong();
		SkillDTO dto = new SkillDTO();
		dto.setName(skillName);
		dto.setDescription(skillDescription);
		dto.setIndustryId(INDUSTRY_ID_1000);

		newSkill = skillService.saveOrUpdateSkill(dto);
		Assert.assertNotNull(newSkill);
		Assert.assertNotNull(newSkill.getId());

		newContractor = newContractorIndependentlane4Ready();
	}

	@Test
	@Transactional
	public void shouldUndeleteADuplicateSkill() {
		newSkill.setDeleted(true);
		skillService.saveOrUpdateSkill(newSkill);
		final SkillDTO dto = new SkillDTO();
		dto.setName(skillName);
		dto.setDescription(skillDescription);
		dto.setIndustryId(INDUSTRY_ID_1000);

		final Skill skill = skillService.saveOrUpdateSkill(dto);

		assertFalse(skill.getDeleted());
		assertEquals(newSkill.getId(), skill.getId());
		assertEquals(newSkill.getName(), skill.getName());
		assertEquals(newSkill.getDescription(), skill.getDescription());
	}

	@Test
	@Transactional
	public void test_findSkillById() throws Exception {
		Skill skill = skillService.findSkillById(newSkill.getId());
		Assert.assertNotNull(skill);
		assertEquals(newSkill.getName(), skill.getName());
		Assert.assertNotNull(skill.getIndustry().getName());
	}

	@Test
	@Transactional
	public void test_findAllApprovedSkills() throws Exception {
		skillService.addSkillToUser(newSkill.getId().intValue(),newContractor.getId());
		SkillPagination pagination = new SkillPagination();

		pagination.setResultsLimit(10);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);

		pagination.setReturnAllRows();
		pagination = skillService.findAllSkills(pagination);

		Assert.assertTrue(pagination.getRowCount() > 0);

		for (Skill skill : pagination.getResults()) {
			Assert.assertNotNull(skill.getName());
			Assert.assertNotNull(skill.getIndustry().getName());
		}
	}

	@Test
	@Transactional
	public void test_findAllSkillsByUser() throws Exception {
		skillService.addSkillToUser(newSkill.getId().intValue(),newContractor.getId());
		SkillPagination pagination = new SkillPagination();
		pagination.setReturnAllRows();
		pagination = skillService.findAllSkillsByUser(newContractor.getId(), pagination);

		for (Skill skill : pagination.getResults()) {
			Assert.assertNotNull(skill.getName());
			Assert.assertNotNull(skill.getIndustry().getName());
		}
	}

	@Test
	@Transactional
	public void test_addSkillToUser() throws Exception {
		skillService.addSkillToUser(newSkill.getId().intValue(), newContractor.getId());

		SkillPagination pagination = skillService.findAllSkillsByUser(newContractor.getId(), new SkillPagination(true));

		assertEquals(1, pagination.getResults().size());
		Assert.assertTrue(pagination.getResults().contains(skillService.findSkillById(newSkill.getId())));
	}

	@Test
	@Transactional
	public void test_removeSkillFromUser() throws Exception {
		skillService.addSkillToUser(newSkill.getId().intValue(), newContractor.getId());

		SkillPagination pagination = skillService.findAllSkillsByUser(newContractor.getId(), new SkillPagination(true));

		assertEquals(1, pagination.getResults().size());
		Assert.assertTrue(pagination.getResults().contains(skillService.findSkillById(newSkill.getId())));

		skillService.removeSkillFromUser(newSkill.getId().intValue(), newContractor.getId());

		pagination = skillService.findAllSkillsByUser(newContractor.getId(), new SkillPagination(true));

		assertEquals(0, pagination.getResults().size());
	}

	@Test
	@Transactional
	public void test_setSkillsOfUser() throws Exception {
		skillService.setSkillsOfUser(new Integer[]{newSkill.getId().intValue()}, newContractor.getId());

		SkillPagination pagination = skillService.findAllSkillsByUser(newContractor.getId(), new SkillPagination(true));

		assertEquals(1, pagination.getResults().size());
		Assert.assertTrue(pagination.getResults().contains(skillService.findSkillById(newSkill.getId())));

	}

	@Test
	@Transactional
	public void test_findAllSkills() throws Exception {
		SkillPagination pagination = new SkillPagination(true);
		pagination.setResultsLimit(10);
		pagination.setSortColumn(SkillPagination.SORTS.CREATED_ON);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);

		SkillPagination skills = skillService.findAllSkills(pagination);

		Assert.assertTrue(skills.getResults().size() > 0);
		Assert.assertTrue(skills.getResults().contains(skillService.findSkillById(newSkill.getId())));
	}

	@Test
	@Transactional
	public void test_findAllSkillsByIndustry() throws Exception {
		SkillPagination pagination = skillService.findAllSkillsByIndustry(INDUSTRY_1000_ID.intValue(), new SkillPagination(true));

		Assert.assertTrue(pagination.getResults().size() > 0);
	}

	@Test
	@Transactional
	public void test_setSkillLevels() throws Exception {
		skillService.setSkillsOfUser(new Integer[]{newSkill.getId().intValue()}, newContractor.getId());

		skillService.setSkillLevelsForUser(
				new Integer[]{newSkill.getId().intValue()},
				new Integer[]{13},
				newContractor.getId()
		);

		UserSkillAssociation association = skillService.findAssociationsBySkillAndUser(
				newSkill.getId().intValue(), newContractor.getId());

		assertEquals(Long.valueOf(13), association.getSkillLevel());

	}

	@Test
	@Transactional
	public void test_saveOrUpdateSkill() throws Exception {
		SkillDTO dto = new SkillDTO();
		dto.setName("AJAX");
		dto.setDescription("description" + RandomUtilities.nextLong());

		Skill skill = skillService.saveOrUpdateSkill(dto);
		Assert.assertNotNull(skill);
	}

	@Test
	@Transactional
	public void test_findSkillByName() throws Exception {
		SkillDTO dto = new SkillDTO();
		dto.setName("skillnametest1" + RandomUtilities.nextLong());
		dto.setDescription("description" + RandomUtilities.nextLong());
		dto.setIndustryId(INDUSTRY_ID_1000);

		newSkill = skillService.saveOrUpdateSkill(dto);


		Skill skill = skillDAO.findSkillByNameAndIndustryId(newSkill.getName(), INDUSTRY_ID_1000);
		Assert.assertNotNull(skill);
		Assert.assertTrue(skill.getName().equals(newSkill.getName()));
	}

	@Test
	@Transactional
	public void test_declineSkill() throws Exception {
		Skill skill = newSkill();

		skillService.setSkillsOfUser(new Integer[]{skill.getId().intValue()}, newContractor.getId());

		SkillPagination pagination = new SkillPagination(true);

		pagination = skillService.findAllActiveSkillsByUser(newContractor.getId(), pagination);

		assertEquals(1, pagination.getResults().size());

		skillService.declineSkill(skill.getId());

		pagination = skillService.findAllActiveSkillsByUser(newContractor.getId(), pagination);

		assertEquals(0, pagination.getResults().size());
	}

	@Test
	@Transactional
	public void test_sortByName() throws Exception {
		SkillPagination pagination = new SkillPagination(true);

		pagination.addFilter(SkillPagination.FILTER_KEYS.NAME, newSkill.getName());

		pagination = skillService.findAllSkills(pagination);
		assertEquals(newSkill.getName(), pagination.getResults().get(0).getName());
	}

	@Test
	@Transactional
	public void test_sortByIndustryName() throws Exception {
		SkillPagination pagination = new SkillPagination(true);
		pagination.addFilter(SkillPagination.FILTER_KEYS.NAME, newSkill.getName());
		pagination.setSortColumn(SkillPagination.SORTS.INDUSTRY_NAME.toString());
		pagination.setSortDirection(Pagination.SORT_DIRECTION.ASC);

		pagination = skillService.findAllSkills(pagination);
		assertEquals(newSkill.getName(), pagination.getResults().get(0).getName());
	}

	@Test
	@Transactional
	public void test_sortByCreatedOn() throws Exception {
		SkillPagination pagination = new SkillPagination(true);
		pagination.setResultsLimit(100);
		pagination.setSortColumn(SkillPagination.SORTS.CREATED_ON.toString());
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);

		pagination = skillService.findAllSkills(pagination);

		List<String> skillNames = extract(pagination.getResults(), on(Skill.class).getName());
		Assert.assertTrue(skillNames.contains(newSkill.getName()));
	}
}
