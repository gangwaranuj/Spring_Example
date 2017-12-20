package com.workmarket.service.business;

import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.skill.SkillPagination;
import com.workmarket.domains.model.skill.UserSkillAssociation;
import com.workmarket.domains.model.skill.UserSkillAssociationPagination;
import com.workmarket.service.business.dto.SkillDTO;

import java.util.List;

public interface SkillService {
	Skill findSkillById(Long skillId);
	Skill findSkillByNameAndIndustry(String name, Long industryId);
	Skill saveOrUpdateSkill(SkillDTO skillDTO);
	Skill saveOrUpdateSkill(Skill skill);

	void declineSkill(Long skillId);
	void approveSkill(Long skillId);

	void mergeSkills(Long fromSkillId, Long toSkillId);

	SkillPagination findAllSkills(SkillPagination pagination);
	SkillPagination findAllSkills(SkillPagination pagination, boolean findByPrefix);
	SkillPagination findAllSkillsByIndustry(Integer industryId, SkillPagination pagination);
	SkillPagination findAllSkillsByUser(Long userId, SkillPagination pagination);
	SkillPagination findAllActiveSkillsByUser(Long userId, SkillPagination pagination);

	void setSkillsOfUser(Integer[] skillIds, Long userId) throws Exception;
	void setSkillsOfUser(List<Integer> skillIds, Long userId) throws Exception;
	void addSkillToUser(Integer skillId, Long userId) throws Exception;
	void removeSkillFromUser(Integer skillId, Long userId) throws Exception;
	void removeSkillsFromUser(Long userId);

	void setSkillLevelsForUser(Integer[] skillId, Integer[] skillLevels, Long userId);

	UserSkillAssociationPagination findAllAssociationsByUser(Long id, UserSkillAssociationPagination userSkillAssociationPagination);
	UserSkillAssociation findAssociationsBySkillAndUser(Integer skillId, Long userId);

	int getSkillPopularityThreshold();

	List<Skill> findSkillsByIds(Long[] skillIds);
}
