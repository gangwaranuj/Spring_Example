package com.workmarket.domains.work.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.DeliverableRequirementGroup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Created by rahul on 3/12/14
 */
@Repository
public class DeliverableRequirementGroupDAOImpl extends AbstractDAO<DeliverableRequirementGroup> implements DeliverableRequirementGroupDAO {
	private static final Log logger = LogFactory.getLog(DeliverableRequirementGroupDAOImpl.class);

	protected Class<DeliverableRequirementGroup> getEntityClass() {
		return DeliverableRequirementGroup.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DeliverableRequirementGroup findDeliverableGroupByWorkNumber(String workNumber) {
		Assert.notNull(workNumber);

		Query query = getFactory().getCurrentSession()
				.getNamedQuery("deliverableRequirementGroup.findByWorkNumber")
				.setParameter("workNumber", workNumber);

		return (DeliverableRequirementGroup) query.uniqueResult();
	}
}