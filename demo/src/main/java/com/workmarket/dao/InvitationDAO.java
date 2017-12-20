package com.workmarket.dao;

import java.util.Calendar;
import java.util.List;

import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.InvitationPagination;
import com.workmarket.domains.model.InvitationStatusType;

public interface InvitationDAO extends PaginatableDAOInterface<Invitation>{
	
	public List<Invitation> findInvitations(Long inviterPK);

	public List<Invitation> findInvitationsByStatus(String emailAddress, InvitationStatusType statusType);

	Invitation findInvitationByRecruitingCampaign(Long recruitingCampaignId, String emailAddress);

	public Invitation findInvitationByCompany(Long companyId, String emailAddress);
	
	public InvitationPagination findInvitations(Long inviterPk, InvitationPagination pagination);
	
	public InvitationPagination findInvitationsByCompany(Long companyId, InvitationPagination pagination);
	
	public Integer countInvitationsByCompanyAndStatus(Long companyId, InvitationStatusType statusType) ;

    public Invitation findInvitationById(Long invitationId);   
    
    public Integer countInvitationsByCompanyStatusAndDate(Long companyId, String invitationStatusType, Calendar date);	
    	
}
