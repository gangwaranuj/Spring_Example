package com.workmarket.dao.skill;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.skill.SkillPagination;
import com.workmarket.domains.model.skill.UserSkillAssociation;
import com.workmarket.domains.model.skill.UserSkillAssociationPagination;

import java.util.List;

public interface UserSkillAssociationDAO extends DAOInterface<UserSkillAssociation>{

	SkillPagination findAllSkillsByUser(Long userId, SkillPagination pagination);

	UserSkillAssociationPagination findAllAssociationsByUser(Long userId, UserSkillAssociationPagination pagination );

	void addSkillToUser(Skill skill, User user);

	void removeSkillFromUser(Skill skill, User user);

	UserSkillAssociation findAssociationsBySkillAndUser(Long skillId, Long userId);

	List<UserSkillAssociation> findAssociationsByUser(Long userId);

	SkillPagination findAllActiveSkillsByUser(Long userId, SkillPagination pagination);

	void mergeSkills(Long fromSkillId, Long toSkillId);
}
