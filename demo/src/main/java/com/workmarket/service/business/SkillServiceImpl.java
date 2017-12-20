package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.industry.IndustryDAO;
import com.workmarket.dao.skill.SkillDAO;
import com.workmarket.dao.skill.UserSkillAssociationDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.skill.SkillPagination;
import com.workmarket.domains.model.skill.UserSkillAssociation;
import com.workmarket.domains.model.skill.UserSkillAssociationPagination;
import com.workmarket.service.business.dto.SkillDTO;
import com.workmarket.utility.BeanUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

@Service
public class SkillServiceImpl implements SkillService {

	@Autowired private IndustryDAO industryDAO;
	@Autowired private SkillDAO skillDAO;
	@Autowired private UserSkillAssociationDAO userSkillAssociationDAO;
	@Autowired private UserSkillAssociationDAO companySkillAssociationDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private UserIndexer userSearchIndexerHelper;

	private static final int SKILL_POPULARITY_THRESHOLD = 9;

	@Override
	public Skill saveOrUpdateSkill(SkillDTO skillDTO) {
		Assert.notNull(skillDTO);

		Skill skill;
		Industry industry = (skillDTO.getIndustryId() != null ? industryDAO.get(skillDTO.getIndustryId()) : Industry.NONE);

		if (skillDTO.getSkillId() == null) {
			//First look for the skill by name, if exists, we don't need to store anything.
			skill = skillDAO.findSkillByNameAndIndustryId(skillDTO.getName(), industry.getId());

			if (skill == null) {
				skill = BeanUtilities.newBean(Skill.class, skillDTO);
			} else {
				if (skill.getDeleted()) {
					skill.setDeleted(false);
					skillDAO.saveOrUpdate(skill);
				}
				return skill;
			}
		} else {
			skill = findSkillById(skillDTO.getSkillId());
			BeanUtilities.copyProperties(skill, skillDTO);
		}

		skill.setIndustry(industry);
		skill.setDeleted(false);
		skillDAO.saveOrUpdate(skill);

		return skill;
	}

	@Override
	public Skill saveOrUpdateSkill(Skill skill) {
		Assert.notNull(skill);
		skillDAO.saveOrUpdate(skill);
		return skill;
	}

	@Override
	public Skill findSkillById(Long skillId) {
		return skillDAO.findSkillById(skillId);
	}

	@Override
	public Skill findSkillByNameAndIndustry(String name, Long industryId) {
		Assert.hasText(name);
		Assert.notNull(industryId);
		return skillDAO.findBy(
				"name", name,
				"industry.id", industryId
		);
	}

	@Override
	public SkillPagination findAllSkills(SkillPagination pagination) {
		return skillDAO.findAllSkills(pagination);
	}

	@Override
	public SkillPagination findAllSkills(SkillPagination pagination, boolean findByPrefix) {
		return skillDAO.findAllSkills(pagination, findByPrefix);
	}

	@Override
	public SkillPagination findAllSkillsByUser(Long userId, SkillPagination pagination) {
		return userSkillAssociationDAO.findAllSkillsByUser(userId, pagination);
	}

	@Override
	public void setSkillsOfUser(Integer[] skillIds, Long userId) throws Exception {
		Assert.noNullElements(skillIds);
		Assert.notNull(userId);

		UserSkillAssociationPagination pagination = userSkillAssociationDAO.findAllAssociationsByUser(userId, new UserSkillAssociationPagination(true));

		List<Integer> newSkillIds = Lists.newArrayList(Arrays.asList(skillIds));

		for(UserSkillAssociation association : pagination.getResults()) {
			if(newSkillIds.contains(association.getSkill().getId().intValue())) {
				association.setDeleted(false);
				newSkillIds.remove(Integer.valueOf(association.getSkill().getId().intValue()));
			} else {
				association.setDeleted(true);
			}
		}

		for(Integer newSkillId : newSkillIds)
			addSkillToUser(newSkillId, userId);
	}

	@Override
	public void setSkillsOfUser(List<Integer> skillIds, Long userId) throws Exception {
		setSkillsOfUser(skillIds.toArray(new Integer[skillIds.size()]), userId);
	}

	public void addSkillToUser(Integer skillId, Long userId) throws Exception {
		userSkillAssociationDAO.addSkillToUser(skillDAO.findSkillById(skillId.longValue()), userDAO.get(userId));
		userSearchIndexerHelper.reindexById(userId);
	}

	@Override
	public void removeSkillFromUser(Integer skillId, Long userId) throws Exception {
		userSkillAssociationDAO.removeSkillFromUser(skillDAO.findSkillById(skillId.longValue()), userDAO.get(userId));
		userSearchIndexerHelper.reindexById(userId);
	}

	@Override
	public void removeSkillsFromUser(Long userId) {
		List<UserSkillAssociation> userSkillAssociations = userSkillAssociationDAO.findAssociationsByUser(userId);

		for (UserSkillAssociation userSkillAssociation : userSkillAssociations) {
			userSkillAssociation.setDeleted(true);
		}

		userSearchIndexerHelper.reindexById(userId);
	}

	@Override
	public SkillPagination findAllSkillsByIndustry(Integer industryId, SkillPagination pagination) {
		return skillDAO.findAllSkillsByIndustry(industryId, pagination);
	}

	@Override
	public UserSkillAssociationPagination findAllAssociationsByUser(Long userId, UserSkillAssociationPagination pagination) {
		return userSkillAssociationDAO.findAllAssociationsByUser(userId, pagination);
	}

	@Override
	public void setSkillLevelsForUser(Integer[] skillIds, Integer[] skillLevels, Long userId) {
		Assert.noNullElements(skillIds);
		Assert.noNullElements(skillLevels);
		Assert.notNull(userId);


		for(int i = 0; i < skillIds.length; i++) {
			UserSkillAssociation association = userSkillAssociationDAO.findAssociationsBySkillAndUser(skillIds[i].longValue(), userId);

			Assert.notNull(association);

			association.setSkillLevel(skillLevels[i].longValue());
		}
	}

	@Override
	public UserSkillAssociation findAssociationsBySkillAndUser(Integer skillId, Long userId) {
		return userSkillAssociationDAO.findAssociationsBySkillAndUser(skillId.longValue(), userId);
	}

	@Override
	public SkillPagination findAllActiveSkillsByUser(Long userId, SkillPagination pagination) {
		return userSkillAssociationDAO.findAllActiveSkillsByUser(userId, pagination);
	}

	@Override
	public void declineSkill(Long skillId) {
		Assert.notNull(skillId);

		Skill skill = findSkillById(skillId);

		Assert.notNull(skill);

		skill.setDeleted(Boolean.TRUE);
	}

	@Override
	public void approveSkill(Long skillId) {
		Assert.notNull(skillId);

		Skill skill = findSkillById(skillId);

		Assert.notNull(skill);

		skill.setApprovalStatus(ApprovalStatus.APPROVED);
	}

	@Override
	public void mergeSkills(Long fromSkillId, Long toSkillId) {
		Assert.notNull(fromSkillId);
		Assert.notNull(toSkillId);
		Assert.notNull(skillDAO.get(toSkillId));

		Skill fromSkill = skillDAO.get(fromSkillId);
		Assert.notNull(fromSkill);

		userSkillAssociationDAO.mergeSkills(fromSkillId, toSkillId);

		fromSkill.setDeleted(true);
	}

	@Override
	public int getSkillPopularityThreshold() {
		return SKILL_POPULARITY_THRESHOLD;
	}

	@Override
	public List<Skill> findSkillsByIds(Long[] skillIds) {
		Assert.notNull(skillIds);
		return skillDAO.findSkillsbyIds(skillIds);
	}

}