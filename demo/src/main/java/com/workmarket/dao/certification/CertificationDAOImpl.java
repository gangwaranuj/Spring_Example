package com.workmarket.dao.certification;


import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.certification.Certification;
import com.workmarket.domains.model.certification.CertificationPagination;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static java.util.Collections.emptyMap;

@Repository
public class CertificationDAOImpl extends AbstractDAO<Certification> implements CertificationDAO {

	protected Class<Certification> getEntityClass() {
        return Certification.class;
    }

    @Override
    public Certification findCertificationById(Long certificationId) {
        Assert.notNull(certificationId);
        return  (Certification)getFactory().getCurrentSession().get(Certification.class, certificationId);
    }

    @SuppressWarnings("unchecked")
	@Override
    public CertificationPagination findAllCertifications(CertificationPagination pagination) 
    {    	       
        Assert.notNull(pagination);

        Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
        Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
        count.setProjection(Projections.rowCount());
        criteria.setFirstResult(pagination.getStartRow());
        criteria.setMaxResults(pagination.getResultsLimit());

        criteria.add(Restrictions.eq("deleted", false))
        	.createAlias("certificationVendor", "vendor", Criteria.INNER_JOIN);
        count.add(Restrictions.eq("deleted", false))
        	.createAlias("certificationVendor", "vendor", Criteria.INNER_JOIN);
       
      
        if (pagination.getFilters() != null) {
			if (pagination.getFilters().get(CertificationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString()) != null) {
				String status = pagination.getFilters().get(CertificationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString());		
				
				if (VerificationStatus.valueOf(status).equals(VerificationStatus.UNVERIFIED)) {
					criteria.add(Restrictions.in("verificationStatus", VerificationStatus.UNVERIFIED_STATUSES));
		        	count.add(Restrictions.in("verificationStatus", VerificationStatus.UNVERIFIED_STATUSES));
				}
				else {
					criteria.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
					count.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
				}				
			}	
			if (pagination.getFilters().get(CertificationPagination.FILTER_KEYS.VENDOR_ID.toString()) != null) {
				Long id = Long.parseLong(pagination.getFilters().get(CertificationPagination.FILTER_KEYS.VENDOR_ID.toString()));				
				criteria.add(Restrictions.eq("vendor.id", id));
				count.add(Restrictions.eq("vendor.id", id));
			}	
		}
        
        String sort = "name";
		if (pagination.getSortColumn() != null) {
			if (pagination.getSortColumn().equals(CertificationPagination.SORTS.CREATED_DATE.toString())) {
				sort = "createdOn";
			} else if (pagination.getSortColumn().equals(CertificationPagination.SORTS.VERIFICATION_STATUS.toString())) {
				sort = "verificationStatus";
			} else if (pagination.getSortColumn().equals(CertificationPagination.SORTS.VENDOR_NAME.toString())) {
				sort = "vendor.name";
			} else if (pagination.getSortColumn().equals(CertificationPagination.SORTS.CERTIFICATION_NAME.toString())) {
				sort = "name";
			} else if (pagination.getSortColumn().equals(CertificationPagination.SORTS.LAST_ACTIVITY_DATE.toString())) {
				sort = "lastActivityOn";
			}
		} 
		
		if (pagination.getSortDirection() != null)
			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
	            criteria.addOrder(Order.desc(sort));
	        } else {
	        	criteria.addOrder(Order.asc(sort));
	        }
		else 
			criteria.addOrder(Order.desc(sort));
		
        pagination.setResults(criteria.list());
        
        if(count.list().size()> 0)
            pagination.setRowCount(((Long) count.list().get(0)).intValue());
        else
            pagination.setRowCount(0);
        
        return pagination;
    }
       
    @SuppressWarnings("unchecked")
    @Override
	public Certification findCertificationByName(String name) {
        Assert.hasText(name);

		return (Certification)DataAccessUtils.singleResult(getFactory().getCurrentSession()
				.getNamedQuery("certification.findCertificationByName")
				.setParameter("name", name).list());
    }
    
    @Override
	public Certification findCertificationByNameAndVendorId(String name, Long certificationVendorId) {
        Assert.hasText(name);
        Assert.notNull(certificationVendorId);
        Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.add(Restrictions.eq("certificationVendor.id", certificationVendorId))
				.add(Restrictions.ilike("name", name, MatchMode.EXACT))
				.setMaxResults(1);
		
        return (Certification) criteria.uniqueResult(); 
    }
    

    @SuppressWarnings("unchecked")
	@Override
    public List<Certification> findAll()
    {
        return getFactory().getCurrentSession().getNamedQuery("certification.findAll").list();
    }

	@Override
	public Map<Long, String> findAllCertificationNamesToHydrateSearchData(
			Set<Long> certificationIdsInResponse) {
		if (certificationIdsInResponse == null || certificationIdsInResponse.size() == 0) {
			return emptyMap();
		}
		
		 Query q = getFactory().getCurrentSession().createQuery(
				 "select c.id, c.name from certification c where c.deleted = false and c.id in (:certificationIds)");
		 q.setParameterList("certificationIds", certificationIdsInResponse);
		 @SuppressWarnings("unchecked") 
		 List<Object> results = q.list();
		 Map<Long, String> returnVal = newHashMapWithExpectedSize(results.size());
		 for (Object result : results) {
			 Object[] row = (Object[])result;
			 returnVal.put((Long)row[0], (String)row[1]);
		 }
		 return returnVal;
		
		
	}
}
