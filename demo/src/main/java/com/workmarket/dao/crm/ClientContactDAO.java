package com.workmarket.dao.crm;

import java.util.List;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientContactPagination;

public interface ClientContactDAO extends DAOInterface<ClientContact> {

	ClientContact findContactById(Long id);

	ClientContact findClientCompanyByIdAndCompany(Long contactId, Long companyId);

	ClientContact findClientContactByClientLocationAndName(Long clientLocationId, String firstName, String lastName);

	ClientContact findClientContactByCompanyIdNamePhone(Long companyId, String firstName, String lastName, String phone, String extension);

	List<ClientContact> findClientContactsByCompany(Long companyId);

	ClientContactPagination findClientContacts(ClientContactPagination pagination);

	List<ClientContact> findIndividualClientContactsByClientCompanyId(Long clientCompanyId);

	ClientContact findClientContactByClientLocationNamePhone(Long clientLocationId, String firstName, String lastName, String phone, String extension);

	ClientContact findClientContactByCompanyIdAndName(Long companyId, String firstName, String lastName);

}
