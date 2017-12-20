package com.workmarket.service.business.upload.parser;

import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.service.business.CRMService;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.Name;
import com.workmarket.thrift.core.Phone;
import com.workmarket.thrift.core.Profile;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import com.workmarket.thrift.work.exception.WorkRowParseErrorType;
import com.workmarket.utility.SerializationUtilities;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LocationContactParserImpl implements LocationContactParser {

	@Autowired private CRMService CRMService;

	@Override
	public void build(WorkUploaderBuildResponse response, WorkUploaderBuildData buildData) {
		Map<String,String> types = buildData.getTypes();

		if (WorkUploadColumn.containsAny(types,
				WorkUploadColumn.CONTACT_FIRST_NAME,
				WorkUploadColumn.CONTACT_LAST_NAME,
				WorkUploadColumn.CONTACT_EMAIL,
				WorkUploadColumn.CONTACT_PHONE) &&
				response.getWork().isSetLocation()) {

			User user = new User();
			String firstName = WorkUploadColumn.get(types, WorkUploadColumn.CONTACT_FIRST_NAME, " ");
			String lastName = WorkUploadColumn.get(types, WorkUploadColumn.CONTACT_LAST_NAME, " ");
			String phoneNumber = WorkUploadColumn.get(types, WorkUploadColumn.CONTACT_PHONE, " ");
			String extension = WorkUploadColumn.get(types, WorkUploadColumn.CONTACT_PHONE_EXTENSION, " ");

			if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName) && StringUtils.isNotBlank(phoneNumber)) {
				if (response.getWork().getLocation().isSetId()) {
					ClientContact contact = CRMService.findClientContactByClientLocationNamePhone(
						response.getWork().getLocation().getId(), firstName, lastName, phoneNumber, extension);
					if (contact != null) {
						user.setId(contact.getId());
					}
				} else {
					if (response.getWork().isSetCompany()) {
						ClientContact contact = CRMService.findClientContactByCompanyIdNamePhone(
							response.getWork().getCompany().getId(), firstName, lastName, phoneNumber, extension);
						if (contact != null) {
							user.setId(contact.getId());
						}
					}
				}
			}
			else if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
				if (response.getWork().getLocation().isSetId()) {
					ClientContact contact = CRMService.findClientContactByClientLocationAndName(
							response.getWork().getLocation().getId(), firstName, lastName);
					if (contact != null) {
						user.setId(contact.getId());
					}
				} else {
					if (response.getWork().isSetCompany()) {
						ClientContact contact = CRMService.findClientContactByCompanyIdAndName(
								response.getWork().getCompany().getId(), firstName, lastName);
						if (contact != null) {
							user.setId(contact.getId());
						}
					}
				}
			}

			user.setName(new Name()
				.setFirstName(firstName)
				.setLastName(lastName));

			Profile profile = new Profile();
			if (WorkUploadColumn.containsAny(types, WorkUploadColumn.CONTACT_PHONE)) {
				String val = WorkUploadColumn.get(types, WorkUploadColumn.CONTACT_PHONE);
				String phone = val.replaceAll("[^0-9]+", "");
				if (phone.length() == 10) {
					Phone p = new Phone()
						.setPhone(phone)
						.setExtension(WorkUploadColumn.get(types, WorkUploadColumn.CONTACT_PHONE_EXTENSION))
						.setType(ContactContextType.WORK.name());
					profile.addToPhoneNumbers(p);
				} else {
					WorkRowParseError error = new WorkRowParseError();
					error.setColumn(WorkUploadColumn.CONTACT_PHONE);
					error.setData(val);
					error.setErrorType(WorkRowParseErrorType.INVALID_DATA);
					error.setMessage(val + " is not a valid phone number. Phone numbers must contain 10 digits.");
					response.addToRowParseErrors(error);
				}
			}

			user.setProfile(profile);
			user.setEmail(WorkUploadColumn.get(types, WorkUploadColumn.CONTACT_EMAIL));
			response.getWork().setLocationContact(user);

			if (!user.isSetId()) {
				WorkUploadLocationContact contact = null;
				if (response.getWork().isSetClientCompany()) {
					contact = new WorkUploadLocationContact(
							response.getWork().getClientCompany().getId(),
							(Location) SerializationUtilities.clone(response.getWork().getLocation()),
							user, true);
				} else {
					contact = new WorkUploadLocationContact(null, null, user, true);
				}
				response.addNewContact(contact, buildData.getLineNumber());
			}
		}
	}
}
