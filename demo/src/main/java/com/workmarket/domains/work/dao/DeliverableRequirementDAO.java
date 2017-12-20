package com.workmarket.domains.work.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.DeliverableRequirement;

import java.util.List;

/**
 * Created by rahul on 2/5/14
 */
public interface DeliverableRequirementDAO extends DAOInterface<DeliverableRequirement> {

	public DeliverableRequirement findDeletedDeliverableRequirementByGroupIdAndType(Long deliverableRequirementGroupId, String type);

	public List<DeliverableRequirement> findAllDeliverableRequirementsByGroupId(Long deliverableRequirementGroupId);

	public List<Long> findAllDeliverableRequirementIdsByGroupId(Long deliverableRequirementGroupId);

	public List<Long> findDeliverableRequirementsWithMissingPositionOnAssets();
}

