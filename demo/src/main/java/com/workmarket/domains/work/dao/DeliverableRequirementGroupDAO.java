package com.workmarket.domains.work.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.DeliverableRequirementGroup;

/**
 * Created by rahul on 3/12/14
 */
public interface DeliverableRequirementGroupDAO extends DAOInterface<DeliverableRequirementGroup> {

	DeliverableRequirementGroup findDeliverableGroupByWorkNumber(String workNumber);
}
