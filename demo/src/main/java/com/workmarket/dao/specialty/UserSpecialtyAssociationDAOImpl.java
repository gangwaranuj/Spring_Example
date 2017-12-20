package com.workmarket.dao.specialty;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.domains.model.specialty.SpecialtyPagination;
import com.workmarket.domains.model.specialty.UserSpecialtyAssociation;
import com.workmarket.domains.model.specialty.UserSpecialtyAssociationPagination;
import com.workmarket.utility.CollectionUtilities;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Repository
public class UserSpecialtyAssociationDAOImpl extends AbstractDAO<UserSpecialtyAssociation> implements
		UserSpecialtyAssociationDAO {

	@Autowired @Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate readOnlyJdbcTemplate;

	@Autowired @Resource(name = "jdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;


	protected Class<UserSpecialtyAssociation> getEntityClass() {
		return UserSpecialtyAssociation.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SpecialtyPagination findAllSpecialtiesByUser(Long userId, SpecialtyPagination pagination) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFirstResult(pagination.getStartRow())
			.setMaxResults(pagination.getResultsLimit())
			.createAlias("specialty", "s")
			.setFetchMode("s", FetchMode.JOIN)
			.createAlias("s.industry", "i")
			.setFetchMode("i", FetchMode.JOIN);
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.distinct(Projections.property("id")))
			.setProjection(Projections.rowCount())
			.createAlias("specialty", "s");

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("s.name"));
		} else {
			criteria.addOrder(Order.asc("s." + SpecialtyPagination.SORTS.valueOf(pagination.getSortColumn()).getColumnName()));
		}

		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("s.deleted", false));
		count.add(Restrictions.eq("user.id", userId));
		count.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("s.deleted", false));

		Set<Specialty> specialties  = Sets.newLinkedHashSet();

		for(UserSpecialtyAssociation association : (List<UserSpecialtyAssociation>)criteria.list())
			specialties.add(association.getSpecialty());
		
		pagination.setResults(Lists.newArrayList(specialties));
		if(count.list().size()> 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserSpecialtyAssociationPagination findAllAssociationsByUser(Long userId, UserSpecialtyAssociationPagination pagination ) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFirstResult(pagination.getStartRow())
			.setMaxResults(pagination.getResultsLimit())
			.createAlias("specialty", "s")
			.setFetchMode("s", FetchMode.JOIN);

		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.distinct(Projections.property("id")))
			.setProjection(Projections.rowCount())
			.createAlias("specialty", "s");

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("s.name"));
		} else {
			criteria.addOrder(Order.asc("s." + SpecialtyPagination.SORTS.valueOf(pagination.getSortColumn()).getColumnName()));
		}

		criteria.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("s.deleted", Boolean.FALSE));

		count.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("s.deleted", Boolean.FALSE));


		pagination.setResults(criteria.list());
		pagination.setRowCount(isNotEmpty(count.list()) ? ((Long)CollectionUtilities.first(count.list())).intValue() : 0);
		return pagination;
	}

	@Override
	public void addSpecialtyToUser(Specialty specialty, User user) {
		UserSpecialtyAssociation association = findAssociationsBySpecialtyAndUser(specialty.getId(), user.getId(), true);

		if(association == null) {
			association = new UserSpecialtyAssociation(user, specialty);
			saveOrUpdate(association);
		} else {
			association.setDeleted(false);
		}
	}

	@Override
	public void removeSpecialtyFromUser(Specialty specialty, User user) {
		UserSpecialtyAssociation association = findAssociationsBySpecialtyAndUser(specialty.getId(), user.getId(), true);

		association.setDeleted(true);
	}

	public UserSpecialtyAssociation findAssociationsBySpecialtyAndUser(Long specialtyId, Long userId) {
		return findAssociationsBySpecialtyAndUser(specialtyId, userId, false);
	}

	private UserSpecialtyAssociation findAssociationsBySpecialtyAndUser(Long specialtyId, Long userId, boolean includeDeleted) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("specialty", "s")
				.add(Restrictions.eq("s.id", specialtyId))
				.add(Restrictions.eq("user.id", userId));

		if (!includeDeleted) {
			criteria.setFetchMode("s", FetchMode.JOIN)
					.add(Restrictions.eq("deleted", Boolean.FALSE))
					.add(Restrictions.eq("s.deleted", Boolean.FALSE));
		}

		return (UserSpecialtyAssociation)criteria.uniqueResult();
	}

	@Override
	public List<UserSpecialtyAssociation> findAssociationsByUser(Long userId) {
		return (List<UserSpecialtyAssociation>)getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("specialty", "s")
				.setFetchMode("s", FetchMode.JOIN)
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("s.deleted", Boolean.FALSE))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.list();
	}

	@Override
	public SpecialtyPagination findAllActiveSpecialtiesByUser(Long userId, SpecialtyPagination pagination)
	{
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFirstResult(pagination.getStartRow())
			.setMaxResults(pagination.getResultsLimit())
			.createAlias("specialty", "s")
			.setFetchMode("s", FetchMode.JOIN)
			.createAlias("s.industry", "i")
			.setFetchMode("i", FetchMode.JOIN);
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.distinct(Projections.property("id")))
			.setProjection(Projections.rowCount())
			.createAlias("specialty", "s");

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("s.name"));
		} else {
			criteria.addOrder(Order.asc("s." + SpecialtyPagination.SORTS.valueOf(pagination.getSortColumn()).getColumnName()));
		}

		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("s.deleted", false));
		count.add(Restrictions.eq("user.id", userId));
		count.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("s.deleted", false));

		Set<Specialty> specialties  = Sets.newLinkedHashSet();

		for(UserSpecialtyAssociation association : (List<UserSpecialtyAssociation>)criteria.list())
			specialties.add(association.getSpecialty());

		pagination.setResults(Lists.newArrayList(specialties));
		if(count.list().size()> 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}

	public void mergeSpecialties(Long fromSpecialtyId, Long toSpecialtyId) {
		// find users that have both fromSpecialtyId and toSpecialtyId and we'll just delete the entry for fromSpecialtyId
		String sql = "UPDATE user_specialty_association AS from_usa " +
				"INNER JOIN user_specialty_association AS to_usa ON from_usa.user_id = to_usa.user_id " +
				"SET from_usa.deleted = 1 " +
				"WHERE from_usa.specialty_id = :fromSpecialtyId AND to_usa.specialty_id = :toSpecialtyId;";

		jdbcTemplate.update(sql, CollectionUtilities.newObjectMap(
			"fromSpecialtyId", fromSpecialtyId,
			"toSpecialtyId", toSpecialtyId
		));

		// map the rest to toSpecialtyId
		sql = "UPDATE user_specialty_association AS from_usa " +
				"SET from_usa.specialty_id = :toSpecialtyId " +
				"WHERE from_usa.specialty_id = :fromSpecialtyId AND from_usa.deleted = 0;";

		jdbcTemplate.update(sql, CollectionUtilities.newObjectMap(
			"fromSpecialtyId", fromSpecialtyId,
			"toSpecialtyId", toSpecialtyId
		));
	}
}
