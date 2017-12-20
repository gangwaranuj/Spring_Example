package com.workmarket.domains.work.dao;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.model.WorkVendorInvitation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WorkVendorInvitationDAO extends DeletableDAOInterface<WorkVendorInvitation> {

	boolean hasInvitedAtLeastOneVendor(Long workId);

	List<Long> getDeclinedVendorIdsByWork(Long workId);

	List<String> getDeclinedVendorNumbersByWork(Long workId);

	List<Long> getAssignToFirstToAcceptVendorIdsByWork(Long workId);

	List<Long> getNotDeclinedVendorIdsByWork(Long workId);

	List<String> getNotDeclinedVendorNumbersByWork(Long workId);

	List<Long> getVendorIdsByWork(Long workId);

	List<String> getVendorNumbersByWork(Long workId);

	List<Long> getVendorInvitedByGroupIds(Long workId, Long companyId);

	Map<Long, Set<Long>> getVendorInvitationGroupAssociationsByWorkId(Long workId);

	List<WorkVendorInvitation> getVendorInvitationsByWork(Long workId);
}
