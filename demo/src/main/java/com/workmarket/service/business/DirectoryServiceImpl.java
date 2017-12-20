package com.workmarket.service.business;

import com.workmarket.dao.DressCodeDAO;
import com.workmarket.dao.LocationDAO;
import com.workmarket.dao.LocationTypeDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.crm.ClientCompanyDAO;
import com.workmarket.dao.crm.ClientContactDAO;
import com.workmarket.dao.directory.EmailDAO;
import com.workmarket.dao.directory.PhoneDAO;
import com.workmarket.dao.directory.WebsiteDAO;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
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
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.StringUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class DirectoryServiceImpl implements DirectoryService {

	@Autowired private EmailDAO emailDAO;
	@Autowired private WebsiteDAO websiteDAO;
	@Autowired private PhoneDAO phoneDAO;
	@Autowired private LocationDAO locationDAO;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private AddressService addressService;
	@Autowired private LocationTypeDAO locationTypeDAO;
	@Autowired private DressCodeDAO dressCodeDAO;
	@Autowired private ClientContactDAO clientContactDAO;
	@Autowired private ClientCompanyDAO clientCompanyDAO;

	@Override
	public Email saveOrUpdateEmailAddress(EmailAddressDTO dto) {
		Assert.notNull(dto);
		Assert.hasText(dto.getEmail(), "Email can't be empty");

		Email email;
		if (dto.getEntityId() != null) {
			email = emailDAO.findById(dto.getEntityId());
			Assert.notNull(email, "Unable to find email");
			BeanUtilities.copyProperties(email, dto);
		} else {
			email = new Email(dto.getEmail(), dto.getContactContextType());
		}

		emailDAO.saveOrUpdate(email);
		return email;
	}

	@Override
	public Website saveOrUpdateWebsite(WebsiteDTO dto) {
		Assert.notNull(dto);
		Assert.hasText(dto.getWebsite(), "Website can't be empty");

		Website website;
		if (dto.getEntityId() != null) {
			website = websiteDAO.findById(dto.getEntityId());
			Assert.notNull(website, "Unable to find website");
			BeanUtilities.copyProperties(website, dto);
		} else {
			website = new Website(dto.getWebsite(), dto.getContactContextType());
		}

		websiteDAO.saveOrUpdate(website);
		return website;
	}

	@Override
	public Phone saveOrUpdatePhoneNumber(PhoneNumberDTO dto) {
		Assert.notNull(dto);
		Assert.hasText(dto.getPhone(), "Phone number can't be empty");

		Phone phone;
		if (dto.getEntityId() != null) {
			phone = phoneDAO.findById(dto.getEntityId());
			Assert.notNull(phone, "Unable to find phone");
			BeanUtilities.copyProperties(phone, dto);
		} else {
			phone = new Phone(dto.getPhone(), dto.getExtension(), dto.getContactContextType());
		}

		phoneDAO.saveOrUpdate(phone);
		return phone;
	}

	@Override
	public Location saveOrUpdateLocation(LocationDTO locationDTO) {
		Assert.notNull(locationDTO);
		Assert.notNull(locationDTO.getAddressTypeCode());
		Assert.notNull(locationDTO.getCompanyId());

		Location location = (locationDTO.getId() == null) ?
			new Location() :
			locationDAO.findLocationById(locationDTO.getId());

		BeanUtilities.copyProperties(location, locationDTO);
		location.setName(StringUtilities.defaultString(locationDTO.getName(), ""));

		// Set address
		if (location.getAddress() != null) {
			locationDTO.setAddressId(location.getAddress().getId());
		}
		Address address = addressService.saveOrUpdate(locationDTO);
		location.setAddress(address);

		// Set company
		Company company = companyDAO.findById(locationDTO.getCompanyId());
		location.setCompany(company);

		locationDAO.saveOrUpdate(location);

		return location;
	}

	@Override
	public Location findLocationById(Long locationId) {
		Assert.notNull(locationId);
		return locationDAO.findLocationById(locationId);
	}

	@Override
	public ClientLocation findClientLocationById(Long locationId) {
		Assert.notNull(locationId);
		return locationDAO.findLocationById(ClientLocation.class, locationId);
	}

	@Override
	public ClientContact findClientContactById(Long id) {
		return clientContactDAO.get(id);
	}

	@Override
	public ClientCompany findClientCompanyById(Long id) {
		return clientCompanyDAO.get(id);
	}

	@Override
	public void deleteLocation(Location location) {
		if (location != null) {
			Assert.notNull(location.getAddress());

			location.getAddress().setDeleted(true);
			location.setDeleted(true);
			addressService.saveOrUpdate(location.getAddress());
			locationDAO.saveOrUpdate(location);
		}
	}
}
