package com.workmarket.dao.insurance;

import com.google.common.collect.Maps;
import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.insurance.Insurance;
import com.workmarket.domains.model.insurance.UserInsuranceAssociation;
import com.workmarket.domains.model.insurance.UserInsuranceAssociationPagination;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class UserInsuranceAssociationDAOImpl extends PaginationAbstractDAO<UserInsuranceAssociation> implements UserInsuranceAssociationDAO {
	protected Class<UserInsuranceAssociation> getEntityClass() {
		return UserInsuranceAssociation.class;
	}
	
	public UserInsuranceAssociation findById(Long id) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFetchMode("user", FetchMode.JOIN)
				.setFetchMode("insurance", FetchMode.JOIN)
				.setFetchMode("insurance.industry", FetchMode.JOIN)
				.setFetchMode("assets", FetchMode.JOIN)
				.add(Restrictions.eq("id", id));
		
		return (UserInsuranceAssociation) criteria.uniqueResult();

	}
	
	public UserInsuranceAssociationPagination findByUser(final Long userId, UserInsuranceAssociationPagination pagination) {
		Map<String, Object> params = Maps.newHashMapWithExpectedSize(1);
		params.put(UserInsuranceAssociationPagination.FILTER_KEYS.USER_ID.toString(), userId);
		return (UserInsuranceAssociationPagination)super.paginationQuery(pagination, params);
	}
	
	public UserInsuranceAssociationPagination findVerifiedByUser(final Long userId, UserInsuranceAssociationPagination pagination) {
		Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
		params.put(UserInsuranceAssociationPagination.FILTER_KEYS.USER_ID.toString(), userId);
		params.put(UserInsuranceAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString(), VerificationStatus.VERIFIED);
		return (UserInsuranceAssociationPagination)super.paginationQuery(pagination, params);
	}
	
	public UserInsuranceAssociationPagination findUnverifiedByUser(final Long userId, UserInsuranceAssociationPagination pagination) {
		Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
		params.put(UserInsuranceAssociationPagination.FILTER_KEYS.USER_ID.toString(), userId);
		params.put(UserInsuranceAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString(), VerificationStatus.PENDING);	
		return (UserInsuranceAssociationPagination)super.paginationQuery(pagination, params);
	}

	public UserInsuranceAssociationPagination findByUserAndInsurance(final Long userId, final Long insuranceId, UserInsuranceAssociationPagination pagination) {
		Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
		params.put(UserInsuranceAssociationPagination.FILTER_KEYS.USER_ID.toString(), userId);
		params.put(UserInsuranceAssociationPagination.FILTER_KEYS.INSURANCE_ID.toString(), insuranceId);
		return (UserInsuranceAssociationPagination)super.paginationQuery(pagination, params);
	}
	
	public UserInsuranceAssociationPagination findAllUserInsuranceAssociations(UserInsuranceAssociationPagination pagination) {
		Map<String, Object> empty = Collections.emptyMap();
		return (UserInsuranceAssociationPagination)super.paginationQuery(pagination, empty);
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserInsuranceAssociationPagination findAllAssociationsByUserIdInList(Long userId, List<Long> insuranceIds, UserInsuranceAssociationPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.setProjection(Projections.rowCount());
		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());
		criteria.setFetchMode("user", FetchMode.JOIN);
		criteria.setFetchMode("insurance", FetchMode.JOIN);
		criteria.setFetchMode("assets", FetchMode.JOIN);

		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.eq("deleted", false));

		count.add(Restrictions.eq("user.id", userId));
		count.add(Restrictions.eq("deleted", false));

		if (CollectionUtils.isNotEmpty(insuranceIds)) {
			criteria.add(Restrictions.in("insurance.id", insuranceIds));
			count.add(Restrictions.in("insurance.id", insuranceIds));
		}

		pagination.setResults(criteria.list());
		if (count.list().size() > 0) {
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		} else {
			pagination.setRowCount(0);
		}
		return pagination;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Insurance> findAllInsuranceByUserIdInList(long userId, List<Long> ids) {
		if (ids.isEmpty()) {
			return new ArrayList<>();
		}

		Criteria criteria = getFactory()
			.getCurrentSession()
			.createCriteria(getEntityClass())
			.add(Restrictions.eq("user.id", userId))
			.add(Restrictions.eq("deleted", false))
			.add(Restrictions.ne("verificationStatus", VerificationStatus.FAILED))
			.add(Restrictions.in("insurance.id", ids))
			.setProjection(Projections.property("insurance"));

		return criteria.list();
	}
	
	public void applySorts(Pagination<UserInsuranceAssociation> pagination, Criteria query, Criteria count) {
		String sort = "id"; 		
		if (pagination.getSortColumn() != null) {
			if (pagination.getSortColumn().equals(UserInsuranceAssociationPagination.SORTS.CREATED_DATE.toString())) {
				sort = "createdOn";
			} else if (pagination.getSortColumn().equals(UserInsuranceAssociationPagination.SORTS.USER_FIRST_NAME.toString())) {
				sort = "user.firstName";
			} else if (pagination.getSortColumn().equals(UserInsuranceAssociationPagination.SORTS.USER_LAST_NAME.toString())) {
				sort = "user.lastName";			
			} else if (pagination.getSortColumn().equals(UserInsuranceAssociationPagination.SORTS.VERIFICATION_STATUS.toString())) {
				sort = "verificationStatus";
			} else if (pagination.getSortColumn().equals(UserInsuranceAssociationPagination.SORTS.PROVIDER.toString())) {
				sort = "provider";
			} else if (pagination.getSortColumn().equals(UserInsuranceAssociationPagination.SORTS.ISSUE_DATE.toString())) {
				sort = "issueDate";
			} else if (pagination.getSortColumn().equals(UserInsuranceAssociationPagination.SORTS.EXPIRATION_DATE.toString())) {
				sort = "expirationDate";
			} else if (pagination.getSortColumn().equals(UserInsuranceAssociationPagination.SORTS.LAST_ACTIVITY_DATE.toString())) {
				sort = "lastActivityOn";
			}
		} 
		
		if (pagination.getSortDirection() != null)
			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				query.addOrder(Order.desc(sort));
			} else {
				query.addOrder(Order.asc(sort));
			}
		else 
			query.addOrder(Order.desc(sort));
	}
	
	public void applyFilters(Pagination<UserInsuranceAssociation> pagination, Criteria criteria, Criteria count) {
		if (pagination.getFilters() == null) return;
		
		if (pagination.getFilters().get(UserInsuranceAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString()) != null) {
			String status = pagination.getFilters().get(UserInsuranceAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString());		
			
			if (VerificationStatus.valueOf(status).equals(VerificationStatus.UNVERIFIED)) {
				criteria.add(Restrictions.in("verificationStatus", VerificationStatus.UNVERIFIED_STATUSES));
	        	count.add(Restrictions.in("verificationStatus", VerificationStatus.UNVERIFIED_STATUSES));
			}
			else {
				criteria.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
				count.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
			}				
		}
		
		if (pagination.getFilters().get(UserInsuranceAssociationPagination.FILTER_KEYS.USER_NAME.toString()) != null) {
			String name = pagination.getFilters().get(UserInsuranceAssociationPagination.FILTER_KEYS.USER_NAME.toString());					
			criteria.add(Restrictions.or(Restrictions.ilike("user.firstName", name, MatchMode.ANYWHERE), Restrictions.ilike("user.lastName", name, MatchMode.ANYWHERE)));
			count.add(Restrictions.or(Restrictions.ilike("user.firstName", name, MatchMode.ANYWHERE), Restrictions.ilike("user.lastName", name, MatchMode.ANYWHERE)));
		}
	}
	
	public void buildWhereClause(Criteria query, Criteria count, Map<String,Object> params) {
		
		query
			.createAlias("user", "user", Criteria.INNER_JOIN)
			.setFetchMode("insurance", FetchMode.JOIN)
			.setFetchMode("insurance.industry", FetchMode.JOIN)
			.createAlias("insurance.industry", "industry")
			.setFetchMode("assets", FetchMode.JOIN);
		
		count
			.createAlias("user", "user", Criteria.INNER_JOIN)
			.setFetchMode("insurance", FetchMode.JOIN);
		
		query.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("deleted", false));
		
		if (params.containsKey(UserInsuranceAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString())) {
			query.add(Restrictions.eq("verificationStatus", params.get(UserInsuranceAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString())));
			count.add(Restrictions.eq("verificationStatus", params.get(UserInsuranceAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString())));
		}
		
		if (params.containsKey(UserInsuranceAssociationPagination.FILTER_KEYS.USER_ID.toString())) {
			query.add(Restrictions.eq("user.id", params.get(UserInsuranceAssociationPagination.FILTER_KEYS.USER_ID.toString())));
			count.add(Restrictions.eq("user.id", params.get(UserInsuranceAssociationPagination.FILTER_KEYS.USER_ID.toString())));
		}
		
		if (params.containsKey(UserInsuranceAssociationPagination.FILTER_KEYS.INSURANCE_ID.toString())) {
			query.add(Restrictions.eq("insurance.id", params.get(UserInsuranceAssociationPagination.FILTER_KEYS.INSURANCE_ID.toString())));
			count.add(Restrictions.eq("insurance.id", params.get(UserInsuranceAssociationPagination.FILTER_KEYS.INSURANCE_ID.toString())));
		}
	}
}