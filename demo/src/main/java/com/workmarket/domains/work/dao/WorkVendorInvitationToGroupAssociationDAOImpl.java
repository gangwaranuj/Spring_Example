package com.workmarket.domains.work.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.WorkVendorInvitationToGroupAssociation;
import org.springframework.stereotype.Repository;

/**
 * WorkVendorInvitationToGroupAssociationDAO Impl.
 */
@Repository
public class WorkVendorInvitationToGroupAssociationDAOImpl
	extends AbstractDAO<WorkVendorInvitationToGroupAssociation>
	implements WorkVendorInvitationToGroupAssociationDAO
{
	@Override protected Class<WorkVendorInvitationToGroupAssociation> getEntityClass() {
		return WorkVendorInvitationToGroupAssociation.class;
	}
}
