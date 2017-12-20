package com.workmarket.service.business;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.ImmutableList;
import com.workmarket.data.dataimport.adapter.ContactManagerImportRowLimitExceededException;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientCompanyPagination;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientContactPagination;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.crm.ClientLocationPagination;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.service.business.dto.ClientCompanyDTO;
import com.workmarket.service.business.dto.ClientContactDTO;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.service.business.dto.WebsiteDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.web.forms.ClientCompanyForm;
import com.workmarket.web.models.MessageBundle;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface CRMService {

	/**
	 * Creates a new Client Company or updates an existing one.
	 *
	 * @return the recently saved {@link com.workmarket.domains.model.crm.ClientCompany ClientCompany}
	 * @
	 */
	ClientCompany saveOrUpdateClientCompany(Long userId, ClientCompanyDTO clientCompanyDTO, MessageBundle messages) ;

	ClientCompany saveOrUpdateClientCompany(ClientCompany clientCompany);

	/**
	 * Creates a new Client Location or updates an existing one.
	 *
	 * @return the recently saved {@link com.workmarket.domains.model.crm.ClientLocation ClientLocation}
	 * @
	 */
	ClientLocation saveOrUpdateClientLocation(Long clientCompanyId, LocationDTO locationDTO, MessageBundle bundle) ;

	ClientContact saveOrUpdateClientContact(Company company, ClientContactDTO clientContactDTO);

	/**
	 * Creates a new Client Contact or updates an existing one.
	 *
	 * @return the recently saved {@link com.workmarket.domains.model.crm.ClientContact ClientContact}
	 * @
	 */
	ClientContact saveOrUpdateClientContact(Long companyId, ClientContactDTO clientContactDTO, MessageBundle bundle) ;

	/**
	 * Finds a Client Company.
	 *
	 * @param id - The id of the Client Company
	 * @return a {@link com.workmarket.domains.model.crm.ClientCompany ClientCompany} if found.
	 * @
	 */
	ClientCompany findClientCompanyById(Long id);

	ClientCompany findClientCompanyByIdAndCompany(Long clientCompanyId, Long companyId);

	/**
	 * Finds a Client Company. Not guaranteed to return unique results.
	 *
	 * @param clientCompanyNumber - The number of the Client Company (customer_id)
	 * @param companyId - The company associated with the client
	 * @
	 */
	List<ClientCompany> findClientCompanyByNumberAndCompany(String clientCompanyNumber, Long companyId);

	/**
	 * Finds a Client Location.
	 *
	 * @param id - The id of the Client Location
	 * @return a {@link com.workmarket.domains.model.crm.ClientLocation ClientLocation} if found.
	 * @
	 */
	ClientLocation findClientLocationById(Long id);

	/**
	 * Finds a Client Contact.
	 *
	 * @param id - The id of the Client Contact
	 * @return a {@link com.workmarket.domains.model.crm.ClientContact ClientContact} if found.
	 * @
	 */
	ClientContact findClientContactById(Long id);

	ClientContact findClientContactByIdAndCompany(Long companyId, Long contactId);

	ClientCompany findClientCompanyByName(long companyId, String name);

	/**
	 * Finds all the client companies for a particular user's company.
	 */
	ClientCompanyPagination findAllClientCompanyByUser(Long userId, ClientCompanyPagination pagination) ;

	/**
	 * Finds all the client companies for a particular user's company.
	 */
	List<ClientCompany> findAllClientCompanyByUser(Long userId) ;

	/**
	 * Finds all the client companies for a particular company.
	 */
	List<ClientCompany> findAllClientCompanyByCompany(Long companyId);

	/**
	 * Finds all the client companies for a particular company and only returns specified fields.
	 */
	List<Map<String, Object>> findAllClientCompaniesByCompany(Long companyId, String... fields);

	/**
	 * Finds all the client companies for a particular company, return id, name, and associated location count
	 */
	List<Map<String, Object>> findAllClientCompaniesByCompanyWithLocationCount(Long companyId);


	ClientLocationPagination findAllLocations(Long companyId, ClientLocationPagination pagination);

	List<ClientLocation> findLocationsByNumberAndClientCompany(String locationNumber, Long clientCompanyId);

	List<ClientLocation> findLocationByNumberAndCompany(String locationNumber, Long companyId);

	/**
	 * Finds all the Locations for a particular Client Company.
	 */
	ClientLocationPagination findAllLocationsByClientCompany(Long companyId, Long clientCompanyId, ClientLocationPagination pagination);

	/**
	 * Finds all the location for a particular company, client company combo and returns all results
	 */
	List<ClientLocation> findAllLocationsByClientCompany(Long companyId, Long clientCompanyId);

	List<ClientLocation> findAllLocationsByClientContact(Long clientContactId);

	String findFirstLocationNameByClientContact(Long clientContactId);

	String findFirstContactNameByClientLocation(Long clientLocationId);

	int getContactCountByClientLocation(Long clientLocationId);

	int getLocationCountByClientContact(Long clientContactId);

	/**
	 * Finds all the client contacts for a particular user with pagination
	 */
	ClientContactPagination findAllClientContactsByUser(Long userId, ClientContactPagination pagination) ;

	/**
	 * Finds all the client contacts for a particular user, based on the company_id column
	 */
	List<ClientContact> findAllClientContactsByCompany(Long companyId);

	/**
	 * Finds all the Contacts for a particular Location NO pagination, based on the client_contact_location_association table
	 */
	List<ClientContact> findAllClientContactsByLocation(Long clientLocationId, boolean withAssociations);

	/**
	 * Finds all client contacts with No Location by client company.
	 */
	List<ClientContact> findIndividualClientContactsByClientCompanyId(Long clientCompanyId);

	ClientContact findClientContactByClientLocationAndName(Long clientLocationId, String firstName, String lastName);

	ClientContact findClientContactByClientLocationNamePhone(Long clientLocationId, String firstName, String lastName, String phone, String extension);

	ClientContact findClientContactByCompanyIdNamePhone(Long companyId, String firstName, String lastName, String phone, String extension);

	/**
	 * Returns all the Dress Code Policies
	 */
	List<DressCode> findAllDressCodes();

	void updateClientCompanyForClientLocation(Long clientLocationId, Long clientCompanyId);
	void updateClientCompanyForClientContact(Long clientContactId, Long clientCompanyId);

	/* DIRECTORY */
	void addWebsiteToClientCompany(Long clientCompanyId, WebsiteDTO websiteDTO) ;
	void addPhoneToClientCompany(Long clientCompanyId, PhoneNumberDTO phoneDTO) ;

	void removeContactAssociationFromClientContact(Long clientLocationId);
	void removeWebsiteAssociationsFromClientCompany(Long clientCompanyId);
	void removePhoneAssociationsFromClientCompany(Long clientCompanyId);

	void addEmailToClientContact(Long clientContactId, EmailAddressDTO emailDTO);
	void addEmailsToClientContact(Long clientContactId, List<EmailAddressDTO> emailDTO);
	void addPhoneToClientContact(Long clientContactId,  PhoneNumberDTO phoneDTO);
	void addPhonesToClientContact(Long clientContactId, List<PhoneNumberDTO> phoneNumberDTOs);
	void addLocationToClientContact(Long clientContactId, Long clientLocationId);

	void removeLocationAssociationsFromClientContact(Long clientContactId);
	void removeEmailAssociationsFromClientContact(Long clientContactId);
	void removePhoneAssociationsFromClientContact(Long clientContactId, ContactContextType type);

	void removePhoneFromClientContact(Long clientContactId, Long phoneId) ;

	void addPhoneToClientLocation(Long clientLocationId, PhoneNumberDTO phoneDTO) ;

	List<UserAvailability> findClientLocationWeeklyWorkingHours(Long clientLocationId) ;

	/**
	 * Deletes a client company if no locations nor contacts are attached to it.
	 */
	void deleteClientCompanyByIdAndCompany(Long clientCompanyId, Long companyId, MessageBundle bundle);

	/**
	 * Deletes a client location.
	 */
	ClientLocation deleteClientLocationByIdAndCompany(Long locationId, Long companyId);

	void deleteClientContactByIdAndCompany(Long companyId, Long contactId);

	void updateClientContactProperties(Long clientContactId, Map<String, String> properties) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException ;

	ClientContact findClientContactByCompanyIdAndName(Long companyId, String firstName, String lastName);

	ClientLocation findClientLocationByIdAndCompany(Long locationId, Long companyId);

	void bulkUploadFileRead(Long companyId, String importType, String fileUUID, MessageBundle messages) throws IOException, HostServiceException, ContactManagerImportRowLimitExceededException;

	void bulkUploadFileRead(Long companyId, String importType, CSVReader reader, MessageBundle messages) throws IOException, HostServiceException, ContactManagerImportRowLimitExceededException;

	ImmutableList<ClientCompanyForm> mapClientCompanyToForm(List<ClientCompany> companies);

	ClientCompanyForm mapClientCompanyToForm(ClientCompany company);

	ImmutableList<Map> getProjectedClientCompanies(String[] fields) throws Exception;

	ImmutableList<Map> getProjectedClientLocations(Long clientId, String locationName, String[] fields) throws Exception;

	ImmutableList<Map> getProjectedClientContacts(Long clientId, String contactName, String[] fields) throws Exception;
}
