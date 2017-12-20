package com.workmarket.dao.skill;

import com.workmarket.service.business.dto.SkillDTO;
import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.skill.UserGroupSkillAssociation;

import java.util.List;

public interface UserGroupSkillAssociationDAO extends DAOInterface<UserGroupSkillAssociation> {
	void addUserGroupSkill(Skill skill, UserGroup userGroup);

	void removeUserGroupSkill(Skill skill, UserGroup userGroup);

	List<Skill> findUserGroupSkills(UserGroup userGroup);

	List<UserGroupSkillAssociation> findUserGroupSkillAssociations(UserGroup userGroup);

	List<Skill> findUserGroupSkills(Long userGroupId);

	List<UserGroupSkillAssociation> findUserGroupSkillAssociations(Long userGroupId);

	void setUserGroupSkillAssociation(UserGroup userGroup, List<Long> skillIds);
}
