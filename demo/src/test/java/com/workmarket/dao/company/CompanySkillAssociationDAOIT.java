package com.workmarket.dao.company;

import com.workmarket.dao.skill.CompanySkillAssociationDAO;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.skill.CompanySkillAssociation;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class CompanySkillAssociationDAOIT extends BaseServiceIT {

	@Autowired CompanySkillAssociationDAO companySkillAssociationDAO;

	@Test
	@Transactional
	public void addCompanySkill() throws Exception {
		User user = newFirstEmployee();
		Skill skill = newSkill();
		companySkillAssociationDAO.addCompanySkill(skill, user.getCompany());

		List<Skill> skills = companySkillAssociationDAO.findCompanySkills(user.getCompany());
		assertEquals(1, skills.size());
	}

	@Test
	@Transactional
	public void removeCompanySkill() throws Exception {
		User user = newFirstEmployee();
		Skill skill = newSkill();
		companySkillAssociationDAO.addCompanySkill(skill, user.getCompany());

		List<Skill> skills = companySkillAssociationDAO.findCompanySkills(user.getCompany());
		assertEquals(1, skills.size());

		companySkillAssociationDAO.removeCompanySkill(skill, user.getCompany());

		skills = companySkillAssociationDAO.findCompanySkills(user.getCompany());
		assertEquals(0, skills.size());
	}

	@Test
	@Transactional
	public void findCompanySkillAssociations() throws Exception {
		User user = newFirstEmployee();
		Skill skill = newSkill();
		companySkillAssociationDAO.addCompanySkill(skill, user.getCompany());

		List<CompanySkillAssociation> companySkillAssociations = companySkillAssociationDAO.findCompanySkillAssociations(user.getCompany());
		assertEquals(1, companySkillAssociations.size());
	}

	@Test
	@Transactional
	public void addCompanyRemoveAddSkill() throws Exception {
		User user = newFirstEmployee();
		Skill skill = newSkill();
		companySkillAssociationDAO.addCompanySkill(skill, user.getCompany());

		List<Skill> skills = companySkillAssociationDAO.findCompanySkills(user.getCompany());
		assertEquals(1, skills.size());

		companySkillAssociationDAO.removeCompanySkill(skill, user.getCompany());

		skills = companySkillAssociationDAO.findCompanySkills(user.getCompany());
		assertEquals(0, skills.size());

		companySkillAssociationDAO.addCompanySkill(skill, user.getCompany());
		skills = companySkillAssociationDAO.findCompanySkills(user.getCompany());
		assertEquals(1, skills.size());
	}
}
