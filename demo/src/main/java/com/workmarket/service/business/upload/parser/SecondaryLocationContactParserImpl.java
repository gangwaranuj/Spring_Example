package com.workmarket.service.business.upload.parser;

import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.service.business.CRMService;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.thrift.core.Name;
import com.workmarket.thrift.core.Phone;
import com.workmarket.thrift.core.Profile;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import com.workmarket.thrift.work.exception.WorkRowParseErrorType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SecondaryLocationContactParserImpl implements SecondaryLocationContactParser {

	@Autowired private CRMService CRMService;

	@Override
	public void build(WorkUploaderBuildResponse response, WorkUploaderBuildData buildData) {

		Map<String,String> types = buildData.getTypes();
		int lineNum = buildData.getLineNumber();

		if (WorkUploadColumn.containsAny(types,
				WorkUploadColumn.SECONDARY_CONTACT_FIRST_NAME,
				WorkUploadColumn.SECONDARY_CONTACT_LAST_NAME,
				WorkUploadColumn.SECONDARY_CONTACT_EMAIL,
				WorkUploadColumn.SECONDARY_CONTACT_PHONE) &&
				response.getWork().isSetLocation()) {

			User user = new User();
			String firstName = WorkUploadColumn.get(types, WorkUploadColumn.SECONDARY_CONTACT_FIRST_NAME, " ");
			String lastName = WorkUploadColumn.get(types, WorkUploadColumn.SECONDARY_CONTACT_LAST_NAME, " ");

			if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
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
			if (WorkUploadColumn.containsAny(types, WorkUploadColumn.SECONDARY_CONTACT_PHONE)) {
				String val = WorkUploadColumn.get(types, WorkUploadColumn.SECONDARY_CONTACT_PHONE);
				String phone = val.replaceAll("[^0-9]+", "");
				if (phone.length() == 10) {
					Phone p = new Phone()
						.setPhone(phone)
						.setExtension(WorkUploadColumn.get(types, WorkUploadColumn.SECONDARY_CONTACT_PHONE_EXTENSION))
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
			user.setEmail(WorkUploadColumn.get(types, WorkUploadColumn.SECONDARY_CONTACT_EMAIL));
			response.getWork().setSecondaryLocationContact(user);

			if (!user.isSetId()) {
				WorkUploadLocationContact contact = null;
				if (response.getWork().isSetClientCompany()) {
					contact = new WorkUploadLocationContact(
							response.getWork().getClientCompany().getId(),
							response.getWork().getLocation(),
							user, false);
				} else {
					contact = new WorkUploadLocationContact(null, response.getWork().getLocation(), user, false);
				}
				response.addNewContact(contact, lineNum);
			}
		}
	}
}
