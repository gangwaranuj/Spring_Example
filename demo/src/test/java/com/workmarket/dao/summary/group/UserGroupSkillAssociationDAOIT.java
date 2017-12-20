package com.workmarket.dao.summary.group;

import com.google.common.collect.Lists;
import com.workmarket.dao.skill.UserGroupSkillAssociationDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.User;

import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.skill.UserGroupSkillAssociation;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.SkillDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UserGroupSkillAssociationDAOIT extends BaseServiceIT {

	@Autowired UserGroupSkillAssociationDAO userGroupSkillAssociationDAO;

	@Test
	@Transactional
	public void addUserGroupSkill() throws Exception {
		User user = newFirstEmployee();
		Skill skill = newSkill();
		UserGroup userGroup = newCompanyUserGroup(user.getCompany().getId());
		userGroupSkillAssociationDAO.addUserGroupSkill(skill, userGroup);

		List<Skill> skills = userGroupSkillAssociationDAO.findUserGroupSkills(userGroup);
		assertEquals(1, skills.size());
	}

	@Test
	@Transactional
	public void removeUserGroupSkill() throws Exception {
		User user = newFirstEmployee();
		Skill skill = newSkill();
		UserGroup userGroup = newCompanyUserGroup(user.getCompany().getId());
		userGroupSkillAssociationDAO.addUserGroupSkill(skill, userGroup);

		List<Skill> skills = userGroupSkillAssociationDAO.findUserGroupSkills(userGroup);
		assertEquals(1, skills.size());

		userGroupSkillAssociationDAO.removeUserGroupSkill(skill, userGroup);
		skills = userGroupSkillAssociationDAO.findUserGroupSkills(userGroup);
		assertEquals(0, skills.size());
	}

	@Test
	@Transactional
	public void findUserGroupSkillAssociations() throws Exception {
		User user = newFirstEmployee();
		Skill skill = newSkill();
		UserGroup userGroup = newCompanyUserGroup(user.getCompany().getId());
		userGroupSkillAssociationDAO.addUserGroupSkill(skill, userGroup);

		List<UserGroupSkillAssociation> userGroupSkillAssociations = userGroupSkillAssociationDAO.findUserGroupSkillAssociations(userGroup);
		assertEquals(1, userGroupSkillAssociations.size());
	}

	@Test
	@Transactional
	public void addUserGroupRemoveAddSkill() throws Exception {
		User user = newFirstEmployee();
		Skill skill = newSkill();
		UserGroup userGroup = newCompanyUserGroup(user.getCompany().getId());
		userGroupSkillAssociationDAO.addUserGroupSkill(skill, userGroup);

		List<Skill> skills = userGroupSkillAssociationDAO.findUserGroupSkills(userGroup);
		assertEquals(1, skills.size());

		userGroupSkillAssociationDAO.removeUserGroupSkill(skill, userGroup);
		skills = userGroupSkillAssociationDAO.findUserGroupSkills(userGroup);
		assertEquals(0, skills.size());

		userGroupSkillAssociationDAO.addUserGroupSkill(skill, userGroup);
		skills = userGroupSkillAssociationDAO.findUserGroupSkills(userGroup.getId());
		assertEquals(1, skills.size());
	}

	@Test
	@Transactional
	public void setUserGroupSkills() throws Exception {
		User user = newFirstEmployee();
		Skill skill = newSkill();
		Skill skill2 = newSkill();
		UserGroup userGroup = newCompanyUserGroup(user.getCompany().getId());

		userGroupSkillAssociationDAO.setUserGroupSkillAssociation(userGroup, Lists.newArrayList(skill.getId(), skill2.getId()));
		List<Skill> skills = userGroupSkillAssociationDAO.findUserGroupSkills(userGroup);
		assertEquals(2, skills.size());

		userGroupSkillAssociationDAO.setUserGroupSkillAssociation(userGroup, Lists.newArrayList(skill.getId()));
		skills = userGroupSkillAssociationDAO.findUserGroupSkills(userGroup);
		assertEquals(1, skills.size());

		userGroupSkillAssociationDAO.setUserGroupSkillAssociation(userGroup, new ArrayList<Long>());
		skills = userGroupSkillAssociationDAO.findUserGroupSkills(userGroup);
		assertEquals(0, skills.size());
	}
}
