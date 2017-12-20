package com.workmarket.dao.skill;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.skill.SkillPagination;
import com.workmarket.domains.model.skill.UserSkillAssociation;
import com.workmarket.domains.model.skill.UserSkillAssociationPagination;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.HibernateUtilities;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Repository
public class UserSkillAssociationDAOImpl extends AbstractDAO<UserSkillAssociation> implements
		UserSkillAssociationDAO {

	@Autowired @Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate readOnlyJdbcTemplate;

	@Autowired @Resource(name = "jdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;


	protected Class<UserSkillAssociation> getEntityClass() {
		return UserSkillAssociation.class;
	}

	@Override
	public SkillPagination findAllSkillsByUser(Long userId, SkillPagination pagination) {
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFirstResult(pagination.getStartRow())
			.setMaxResults(pagination.getResultsLimit())
			.createAlias("skill", "s")
			.setFetchMode("s", FetchMode.JOIN)
			.createAlias("s.industry", "i")
			.setFetchMode("i", FetchMode.JOIN);
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.distinct(Projections.property("id")))
			.setProjection(Projections.rowCount())
			.createAlias("skill", "s");

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("s.name"));
		} else {
			criteria.addOrder(Order.asc("s." + SkillPagination.SORTS.valueOf(pagination.getSortColumn()).getColumnName()));
		}

		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("s.deleted", false));
		count.add(Restrictions.eq("user.id", userId));
		count.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("s.deleted", false));

		Set<Skill> skills  = Sets.newLinkedHashSet();

		for(UserSkillAssociation association : (List<UserSkillAssociation>)criteria.list())
			skills.add(association.getSkill());

		pagination.setResults(Lists.<Skill>newArrayList(skills));
		if(count.list().size()> 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}

	@Override
	public UserSkillAssociationPagination findAllAssociationsByUser(Long userId, UserSkillAssociationPagination pagination ) {
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());

		criteria.createAlias("skill", "s")
				.setFetchMode("s", FetchMode.JOIN)
				.createAlias("s.industry", "i")
				.setFetchMode("i", FetchMode.JOIN);

		count.createAlias("skill", "s");

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("s.name"));
		} else {
			criteria.addOrder(Order.asc("s." + SkillPagination.SORTS.valueOf(pagination.getSortColumn()).getColumnName()));
		}

		HibernateUtilities.addRestrictionsEq(criteria, "user.id", userId);
		HibernateUtilities.addRestrictionsEq(count, "user.id", userId);

		criteria.add(Restrictions.eq("deleted", Boolean.FALSE));
		count.add(Restrictions.eq("deleted", Boolean.FALSE));
		count.add(Restrictions.eq("s.deleted", Boolean.FALSE));

		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		pagination.setResults(criteria.list());
		pagination.setRowCount(HibernateUtilities.getRowCount(count));

		return pagination;
	}

	@Override
	public void addSkillToUser(Skill skill, User user) {
		Assert.notNull(skill);
		Assert.notNull(user);

		UserSkillAssociation association = findAssociationsBySkillAndUser(skill.getId(), user.getId(), true);

		if(association == null) {
			association = new UserSkillAssociation(user, skill);
			saveOrUpdate(association);
		} else {
			association.setDeleted(false);
		}
	}

	@Override
	public void removeSkillFromUser(Skill skill, User user) {
		Assert.notNull(skill);
		Assert.notNull(user);

		UserSkillAssociation association = findAssociationsBySkillAndUser(skill.getId(), user.getId(), true);

		Assert.notNull(association);

		association.setDeleted(true);
	}

	public UserSkillAssociation findAssociationsBySkillAndUser(Long skillId, Long userId) {
		return findAssociationsBySkillAndUser(skillId, userId, false);
	}

	private UserSkillAssociation findAssociationsBySkillAndUser(Long skillId, Long userId, boolean includeDeleted) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("skill", "s")
				.add(Restrictions.eq("s.id", skillId))
				.add(Restrictions.eq("user.id", userId));

		if (!includeDeleted) {
			criteria.setFetchMode("s", FetchMode.JOIN)
					.add(Restrictions.eq("deleted", Boolean.FALSE))
					.add(Restrictions.eq("s.deleted", Boolean.FALSE));
		}

		return (UserSkillAssociation)criteria.uniqueResult();
	}

	public List<UserSkillAssociation> findAssociationsByUser(Long userId) {
		return (List<UserSkillAssociation>)getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("skill", "s")
				.setFetchMode("s", FetchMode.JOIN)
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("s.deleted", Boolean.FALSE))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SkillPagination findAllActiveSkillsByUser(Long userId, SkillPagination pagination)
	{
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFirstResult(pagination.getStartRow())
			.setMaxResults(pagination.getResultsLimit())
			.createAlias("skill", "s")
			.setFetchMode("s", FetchMode.JOIN)
			.createAlias("s.industry", "i")
			.setFetchMode("i", FetchMode.JOIN);
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.distinct(Projections.property("id")))
			.setProjection(Projections.rowCount())
			.createAlias("skill", "s");

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("s.name"));
		} else {
			criteria.addOrder(Order.asc("s." + SkillPagination.SORTS.valueOf(pagination.getSortColumn()).getColumnName()));
		}

		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("s.deleted", false));
		count.add(Restrictions.eq("user.id", userId));
		count.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("s.deleted", false));

		Set<Skill> skills  = Sets.newLinkedHashSet();

		for(UserSkillAssociation association : (List<UserSkillAssociation>)criteria.list())
			skills.add(association.getSkill());

		pagination.setResults(Lists.<Skill>newArrayList(skills));
		if(count.list().size()> 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;	}

	public void mergeSkills(Long fromSkillId, Long toSkillId) {
		// find users that have both fromSkillId and toSkillId and we'll just delete the entry for fromSkillId
		String sql = "UPDATE user_skill_association AS from_usa " +
				"INNER JOIN user_skill_association AS to_usa ON from_usa.user_id = to_usa.user_id " +
				"SET from_usa.deleted = 1 " +
				"WHERE from_usa.skill_id = :fromSkillId AND to_usa.skill_id = :toSkillId;";

		jdbcTemplate.update(sql, CollectionUtilities.newObjectMap(
			"fromSkillId", fromSkillId,
			"toSkillId", toSkillId
		));

		// map the rest to toSkillId
		sql = "UPDATE user_skill_association AS from_usa " +
				"SET from_usa.skill_id = :toSkillId " +
				"WHERE from_usa.skill_id = :fromSkillId AND from_usa.deleted = 0;";

		jdbcTemplate.update(sql, CollectionUtilities.newObjectMap(
			"fromSkillId", fromSkillId,
			"toSkillId", toSkillId
		));
	}
}
