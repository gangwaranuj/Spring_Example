package com.workmarket.dao.skill;

import com.google.common.collect.Lists;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.skill.UserGroupSkillAssociation;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

@Repository
public class UserGroupSkillAssociationDAOImpl extends AbstractDAO<UserGroupSkillAssociation> implements
		UserGroupSkillAssociationDAO {

	protected Class<UserGroupSkillAssociation> getEntityClass() {
		return UserGroupSkillAssociation.class;
	}

	@Override
	public void addUserGroupSkill(Skill skill, UserGroup userGroup) {
		Assert.notNull(skill);
		Assert.notNull(userGroup);

		UserGroupSkillAssociation userGroupSkill = findUserGroupSkill(userGroup, skill);
		if(userGroupSkill == null) {
			userGroupSkill= new UserGroupSkillAssociation(userGroup, skill);
			saveOrUpdate(userGroupSkill);
		} else {
			userGroupSkill.setDeleted(false);
		}
	}

	@Override
	public void removeUserGroupSkill(Skill skill, UserGroup userGroup) {
		Assert.notNull(skill);
		Assert.notNull(userGroup);

		UserGroupSkillAssociation association = findUserGroupSkill(userGroup, skill);
		Assert.notNull(association);

		association.setDeleted(true);
	}

	private UserGroupSkillAssociation findUserGroupSkill(UserGroup userGroup, Skill skill) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("skill", skill))
			.add(Restrictions.eq("userGroup", userGroup));
		return (UserGroupSkillAssociation)criteria.uniqueResult();
	}

	@Override
	public List<Skill> findUserGroupSkills(UserGroup userGroup) {
		List<Skill> skills  = Lists.newArrayList();
		for(UserGroupSkillAssociation skillAssociation : findUserGroupSkillAssociations(userGroup)) {
			skills.add(skillAssociation.getSkill());
		}
		return skills;
	}

	@Override
	public List<UserGroupSkillAssociation> findUserGroupSkillAssociations(UserGroup userGroup) {
		Criteria criteria = getFactory()
			.getCurrentSession().createCriteria(getEntityClass())
			.createAlias("userGroup", "userGroup")
			.createAlias("skill", "skill")

			.add(Restrictions.eq("userGroup", userGroup))
			.add(Restrictions.eq("deleted", Boolean.FALSE))
			.add(Restrictions.eq("skill.deleted", Boolean.FALSE));

		return criteria.list();
	}

	@Override
	public List<Skill> findUserGroupSkills(Long userGroupId) {
		List<Skill> skills  = Lists.newArrayList();
		for(UserGroupSkillAssociation skillAssociation : findUserGroupSkillAssociations(userGroupId)) {
			skills.add(skillAssociation.getSkill());
		}
		return skills;
	}

	@Override
	public List<UserGroupSkillAssociation> findUserGroupSkillAssociations(Long userGroupId) {
		Criteria criteria = getFactory()
				.getCurrentSession().createCriteria(getEntityClass())
				.createAlias("userGroup", "userGroup")
				.createAlias("skill", "skill")

				.add(Restrictions.eq("userGroup.id", userGroupId))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("skill.deleted", Boolean.FALSE));

		return criteria.list();
	}

	@Override
	public void setUserGroupSkillAssociation(UserGroup userGroup, List<Long> skillIds) {
		Assert.notNull(skillIds);
		Assert.notNull(userGroup);

		List<UserGroupSkillAssociation> userGroupSkillAssociations = findUserGroupSkillAssociations(userGroup);
		for (UserGroupSkillAssociation userGroupSkillAssociation : userGroupSkillAssociations) {
			Boolean delete = true;
			for (Long skillId : skillIds) {
				if (userGroupSkillAssociation.getUserGroupSkill().getSkillId().equals(skillId)) {
					delete = false;
					skillIds.remove(skillId);
					break;
				}
			}

			userGroupSkillAssociation.setDeleted(delete);
		}

		//add the new skills now
		userGroupSkillAssociations = Lists.newArrayList();
		for (Long skillId : skillIds) {
			userGroupSkillAssociations.add(new UserGroupSkillAssociation(userGroup.getId(), skillId));
		}

		if (userGroupSkillAssociations.size() > 0) {
			saveAll(userGroupSkillAssociations);
		}
	}
}
