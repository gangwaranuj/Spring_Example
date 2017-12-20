package com.workmarket.dao.integration.autotask;

import com.google.common.base.Optional;
import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.integration.autotask.AutotaskTicketWorkAssociation;

/**
 * Created by nick on 2012-12-24 5:23 PM
 */
public interface AutotaskTicketWorkAssociationDAO extends DAOInterface<AutotaskTicketWorkAssociation> {

	public Optional<AutotaskTicketWorkAssociation> findAutotaskTicketWorkAssociationByWorkId(Long workId);

	public Optional<AutotaskTicketWorkAssociation> findAutotaskTicketWorkAssociationByTicketIdAndWorkId(Long ticketId, Long workId);
}
