package com.workmarket.service.business;

import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.directory.Phone;
import com.workmarket.domains.model.directory.Website;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.service.business.dto.WebsiteDTO;

public interface DirectoryService {

	Email saveOrUpdateEmailAddress(EmailAddressDTO dto);

	Website saveOrUpdateWebsite(WebsiteDTO dto);

	Phone saveOrUpdatePhoneNumber(PhoneNumberDTO dto);

	Location saveOrUpdateLocation(LocationDTO locationDTO);

	Location findLocationById(Long locationId);

	ClientLocation findClientLocationById(Long locationId);

	ClientContact findClientContactById(Long id);

	ClientCompany findClientCompanyById(Long id);

	void deleteLocation(Location location);
}