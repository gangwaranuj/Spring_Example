package com.workmarket.dao.insurance;

import com.google.common.collect.ImmutableMap;
import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.insurance.Insurance;
import com.workmarket.domains.model.insurance.InsurancePagination;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static java.util.Collections.emptyMap;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Repository
public class InsuranceDAOImpl extends PaginationAbstractDAO<Insurance> implements InsuranceDAO {
	protected Class<Insurance> getEntityClass() {
		return Insurance.class;
	}
	
	public InsurancePagination findByIndustry(final long industryId, InsurancePagination pagination) {
		return (InsurancePagination)super.paginationQuery(pagination, ImmutableMap.<String, Object>of("industryId", industryId));
	}

	@Override
	public InsurancePagination findAllInsurances(InsurancePagination pagination) {
		return (InsurancePagination)super.paginationQuery(pagination, new HashMap<String,Object>());
	}

	public void applySorts(Pagination<Insurance> pagination, Criteria query, Criteria count) {
				
		String sort = "name"; 		
		if (pagination.getSortColumn() != null) {
			if (pagination.getSortColumn().equals(InsurancePagination.SORTS.CREATED_DATE.toString())) {
				sort = "createdOn";
			} else if (pagination.getSortColumn().equals(InsurancePagination.SORTS.VERIFICATION_STATUS.toString())) {
				sort = "verificationStatus";
			} else if (pagination.getSortColumn().equals(InsurancePagination.SORTS.INSURANCE_NAME.toString())) {
				sort = "name";
			} else if (pagination.getSortColumn().equals(InsurancePagination.SORTS.INDUSTRY.toString())) {
				sort = "industry.name";
			} else if (pagination.getSortColumn().equals(InsurancePagination.SORTS.LAST_ACTIVITY_DATE.toString())) {
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
	
	public void applyFilters(Pagination<Insurance> pagination, Criteria criteria, Criteria count) {
		if (pagination.getFilters() == null) return;
				
		if (pagination.getFilters().get(InsurancePagination.FILTER_KEYS.VERIFICATION_STATUS.toString()) != null) {
			String status = pagination.getFilters().get(InsurancePagination.FILTER_KEYS.VERIFICATION_STATUS.toString());		
			
			if (VerificationStatus.valueOf(status).equals(VerificationStatus.UNVERIFIED)) {
				criteria.add(Restrictions.in("verificationStatus", VerificationStatus.UNVERIFIED_STATUSES));
	        	count.add(Restrictions.in("verificationStatus", VerificationStatus.UNVERIFIED_STATUSES));
			}
			else {
				criteria.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
				count.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
			}				
		}
	}
	
	public void buildWhereClause(Criteria query, Criteria count, Map<String,Object> params) {
		
		query.createAlias("industry", "industry", Criteria.INNER_JOIN);
		count.createAlias("industry", "industry", Criteria.INNER_JOIN);
		
		if (params.containsKey("industryId")) {
			// Industry.NONE is treated as globally available insurance. Always include it.
			query.add(
				Restrictions.or(
					Restrictions.eq("industry.id", params.get("industryId")),
					Restrictions.eq("industry.id", Industry.NONE.getId())
				)
			);
			count.add(
				Restrictions.or(
					Restrictions.eq("industry.id", params.get("industryId")),
					Restrictions.eq("industry.id", Industry.NONE.getId())
				)
			);
		}
		
		if (params.containsKey(InsurancePagination.FILTER_KEYS.VERIFICATION_STATUS.toString())) {
			query.add(Restrictions.eq("verificationStatus", params.get(InsurancePagination.FILTER_KEYS.VERIFICATION_STATUS.toString())));
			count.add(Restrictions.eq("verificationStatus", params.get(InsurancePagination.FILTER_KEYS.VERIFICATION_STATUS.toString())));
		}
	}

	
	@SuppressWarnings("unchecked")
	public Map<Long, String> findAllInsuranceNamesByInsuranceId(Set<Long> insuranceIds) {
		if (isEmpty(insuranceIds)) {
			return emptyMap();
		}
		Query q =  getFactory().getCurrentSession().createQuery("select i.id, i.name from insurance i where i.id in (:insuranceIds)");
		q.setParameterList("insuranceIds", insuranceIds);
		List<Object> results = q.list();
		Map<Long, String> returnVal = newHashMapWithExpectedSize(results.size());
		for (Object result : results) {
			Object[] row = (Object[]) result;
			returnVal.put((Long) row[0], (String) row[1]);
		}
		return returnVal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, String> findAllInsuranceNamesAndId() {
		
		Query q =  getFactory().getCurrentSession().createQuery("select i.id, i.name from insurance i");
		List<Object> results = q.list();
		Map<Long, String> returnVal = newHashMapWithExpectedSize(results.size());
		for (Object result : results) {
			Object[] row = (Object[]) result;
			returnVal.put((Long) row[0], (String) row[1]);
		}
		return returnVal;
	}
		
}