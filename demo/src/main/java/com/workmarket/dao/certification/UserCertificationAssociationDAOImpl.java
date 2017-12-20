package com.workmarket.dao.certification;


import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.certification.Certification;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.model.certification.UserCertificationAssociationPagination;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class UserCertificationAssociationDAOImpl extends AbstractDAO<UserCertificationAssociation> implements UserCertificationAssociationDAO {

	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<UserCertificationAssociation> getEntityClass() {
		return UserCertificationAssociation.class;
	}

	@Override
	public UserCertificationAssociation findAssociationById(Long id) {
		Assert.notNull(id);

		return (UserCertificationAssociation) getFactory().getCurrentSession()
				.get(UserCertificationAssociation.class, id);
	}

	@Override
	public UserCertificationAssociation findAssociationByCertificationIdAndUserId(Long certificationId, Long userId) {
		Assert.notNull(certificationId);
		Assert.notNull(userId);

		Query query = getFactory().getCurrentSession().getNamedQuery("userCertificationAssociation.findAssociationByCertificationIdAndUserId")
				.setParameter("certificationId", certificationId)
				.setParameter("userId", userId);

		UserCertificationAssociation association = (UserCertificationAssociation) query.uniqueResult();
		if (association != null) {
			Hibernate.initialize(association.getAssets());
		}

		return association;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserCertificationAssociationPagination findAllUserCertifications(UserCertificationAssociationPagination pagination) {
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.setProjection(Projections.rowCount());

		criteria.createAlias("certification", "c", Criteria.INNER_JOIN)
				.setFetchMode("c.certificationVendor", FetchMode.JOIN)
				.createAlias("c.certificationVendor", "vendor")
				.createAlias("user", "user", Criteria.INNER_JOIN);

		String includeAssets = pagination.getFilters().get(UserCertificationAssociationPagination.FILTER_KEYS.WITH_ASSETS.toString());
		if (includeAssets == null || UserCertificationAssociationPagination.ASSETS.valueOf(includeAssets).equals(UserCertificationAssociationPagination.ASSETS.YES)) {
			criteria.setFetchMode("assets", FetchMode.JOIN);
		}

		criteria.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit());

		count.createAlias("certification", "c", Criteria.INNER_JOIN)
				.setFetchMode("c.certificationVendor", FetchMode.JOIN)
				.createAlias("c.certificationVendor", "vendor")
				.createAlias("user", "user", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("deleted", false));

		if (pagination.getFilters() != null) {
			if (pagination.getFilters().get(UserCertificationAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString()) != null) {
				String status = pagination.getFilters().get(UserCertificationAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString());

				if (VerificationStatus.valueOf(status).equals(VerificationStatus.UNVERIFIED)) {
					criteria.add(Restrictions.in("verificationStatus", VerificationStatus.UNVERIFIED_STATUSES));
					count.add(Restrictions.in("verificationStatus", VerificationStatus.UNVERIFIED_STATUSES));
				} else {
					criteria.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
					count.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
				}
			}
			if (pagination.getFilters().get(UserCertificationAssociationPagination.FILTER_KEYS.USER_NAME.toString()) != null) {
				String name = pagination.getFilters().get(UserCertificationAssociationPagination.FILTER_KEYS.USER_NAME.toString());

				criteria.add(Restrictions.or(Restrictions.ilike("user.firstName", name, MatchMode.ANYWHERE), Restrictions.ilike("user.lastName", name, MatchMode.ANYWHERE)));
				count.add(Restrictions.or(Restrictions.ilike("user.firstName", name, MatchMode.ANYWHERE), Restrictions.ilike("user.lastName", name, MatchMode.ANYWHERE)));
			}
			if (pagination.getFilters().get(UserCertificationAssociationPagination.FILTER_KEYS.USER_ID.toString()) != null) {
				Long id = Long.parseLong(pagination.getFilters().get(UserCertificationAssociationPagination.FILTER_KEYS.USER_ID.toString()));
				criteria.add(Restrictions.eq("user.id", id));
				count.add(Restrictions.eq("user.id", id));
			}
		}

		String sort = "id";
		if (pagination.getSortColumn() != null) {
			if (pagination.getSortColumn().equals(UserCertificationAssociationPagination.SORTS.CREATED_DATE.toString())) {
				sort = "createdOn";
			} else if (pagination.getSortColumn().equals(UserCertificationAssociationPagination.SORTS.USER_FIRST_NAME.toString())) {
				sort = "user.firstName";
			} else if (pagination.getSortColumn().equals(UserCertificationAssociationPagination.SORTS.USER_LAST_NAME.toString())) {
				sort = "user.lastName";
			} else if (pagination.getSortColumn().equals(UserCertificationAssociationPagination.SORTS.VENDOR_NAME.toString())) {
				sort = "vendor.name";
			} else if (pagination.getSortColumn().equals(UserCertificationAssociationPagination.SORTS.VERIFICATION_STATUS.toString())) {
				sort = "verificationStatus";
			} else if (pagination.getSortColumn().equals(UserCertificationAssociationPagination.SORTS.EXPIRATION_DATE.toString())) {
				sort = "expirationDate";
			} else if (pagination.getSortColumn().equals(UserCertificationAssociationPagination.SORTS.ISSUE_DATE.toString())) {
				sort = "issueDate";
			} else if (pagination.getSortColumn().equals(UserCertificationAssociationPagination.SORTS.USER_ID.toString())) {
				sort = "user.id";
			} else if (pagination.getSortColumn().equals(UserCertificationAssociationPagination.SORTS.LAST_ACTIVITY_DATE.toString())) {
				sort = "lastActivityOn";
			}
		}

		if (pagination.getSortDirection() != null)
			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				criteria.addOrder(Order.desc(sort));
			} else {
				criteria.addOrder(Order.asc(sort));
			}
		else {
			criteria.addOrder(Order.desc(sort));
		}

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		count.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<UserCertificationAssociation> associationList = criteria.list();
		pagination.setResults(associationList);

		if (count.list().size() > 0) {
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		} else {
			pagination.setRowCount(0);
		}

		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserCertificationAssociationPagination findAllUserCertificationsByUserIds(Set<Long> userIds, UserCertificationAssociationPagination pagination) {
		Assert.notEmpty(userIds);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.setProjection(Projections.rowCount());

		criteria.createAlias("certification", "c", Criteria.INNER_JOIN)
				.setFetchMode("c.certificationVendor", FetchMode.JOIN)
				.createAlias("c.certificationVendor", "vendor")
				.createAlias("user", "user", Criteria.INNER_JOIN);

		String includeAssets = pagination.getFilters().get(UserCertificationAssociationPagination.FILTER_KEYS.WITH_ASSETS.toString());
		if (includeAssets == null || UserCertificationAssociationPagination.ASSETS.valueOf(includeAssets).equals(UserCertificationAssociationPagination.ASSETS.YES)) {
			criteria.setFetchMode("assets", FetchMode.JOIN);
		}

		criteria.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit());

		count.createAlias("certification", "c", Criteria.INNER_JOIN)
				.setFetchMode("c.certificationVendor", FetchMode.JOIN)
				.createAlias("c.certificationVendor", "vendor")
				.createAlias("user", "user", Criteria.INNER_JOIN);

		criteria.add(Restrictions.in("user.id", userIds));
		criteria.add(Restrictions.eq("deleted", false));

		count.add(Restrictions.in("user.id", userIds));
		count.add(Restrictions.eq("deleted", false));

		if (pagination.getFilters() != null) {
			if (pagination.getFilters().get(UserCertificationAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString()) != null) {
				String status = pagination.getFilters().get(UserCertificationAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString());

				if (VerificationStatus.valueOf(status).equals(VerificationStatus.UNVERIFIED)) {
					criteria.add(Restrictions.in("verificationStatus", VerificationStatus.UNVERIFIED_STATUSES));
					count.add(Restrictions.in("verificationStatus", VerificationStatus.UNVERIFIED_STATUSES));
				} else {
					criteria.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
					count.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
				}
			}
		}

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		count.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<UserCertificationAssociation> associationList = criteria.list();
		pagination.setResults(associationList);

		if (count.list().size() > 0) {
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		} else {
			pagination.setRowCount(0);
		}

		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Certification> findAllCertificationsByUserIdInList(long userId, List<Long> ids) {
		if (ids.isEmpty()) {
			return new ArrayList<>();
		}

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.ne("verificationStatus", VerificationStatus.FAILED))
				.add(Restrictions.in("certification.id", ids))
				.setProjection(Projections.property("certification"));

		return criteria.list();
	}

	@Override
	public List<Map<String, String>> findAllCertificationFacetsByTypeAheadFilter(String typeAheadFilter) {
		String sql =
			"select c.id, c.name, count(uca.certification_id) as count from certification c " +
				"join user_certification_association uca on uca.certification_id = c.id " +
				"where uca.deleted = false and c.deleted = false " +
				"and c.name like :typeAheadFilter " +
				"GROUP BY c.id " +
				"ORDER BY count(uca.certification_id) desc";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("typeAheadFilter", "%" + typeAheadFilter + "%");
		List<Map<String, Object>> transactionMap = jdbcTemplate.queryForList(sql, params);

		List<Map<String,String>> results = Lists.newArrayList();
		for (Map<String, Object> row : transactionMap) {
			Map<String, String>  result = Maps.newHashMap();
			result.put("id", row.get("id").toString());
			result.put("name", row.get("name").toString());
			result.put("count", row.get("count").toString());
			results.add(result);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserCertificationAssociationPagination findAllAssociationsByUserIdInList(
			Long userId,
			List<Long> certificationIds,
			UserCertificationAssociationPagination pagination) {

		Assert.notNull(userId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit())
				.setFetchMode("user", FetchMode.JOIN)
				.setFetchMode("certification", FetchMode.JOIN)
				.setFetchMode("assets", FetchMode.JOIN);

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("id"));
		}

		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.eq("deleted", false));

		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setProjection(Projections.rowCount())
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("deleted", false));

		if (CollectionUtils.isNotEmpty(certificationIds)) {
			criteria.add(Restrictions.in("certification.id", certificationIds));
			count.add(Restrictions.in("certification.id", certificationIds));
		}

		pagination.setResults(criteria.list());
		List<Long> countList = count.list();
		pagination.setRowCount(countList.isEmpty() ? 0 : CollectionUtilities.first(countList).intValue());

		return pagination;
	}
}
