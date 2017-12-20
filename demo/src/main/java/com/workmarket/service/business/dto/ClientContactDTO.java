package com.workmarket.service.business.dto;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.dto.AddressDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class ClientContactDTO extends AddressDTO {

	private Long contactId;
	private Long clientCompanyId;
	private Long clientLocationId;

    @NotNull
    @Size(min = Constants.FIRST_NAME_MIN_LENGTH, max = Constants.FIRST_NAME_MAX_LENGTH)
	private String firstName;
    @NotNull
    @Size(min = Constants.LAST_NAME_MIN_LENGTH, max = Constants.LAST_NAME_MAX_LENGTH)
	private String lastName;
	private String jobTitle;
    @NotNull
    @Size(min = Constants.EMAIL_MIN_LENGTH, max = Constants.EMAIL_MAX_LENGTH)
	private String email;
    @Size(min = Constants.PHONE_NUMBER_MIN_LENGTH, max = Constants.PHONE_NUMBER_MAX_LENGTH)
	private String workPhone;
	private String workPhoneExtension;
    @Size(min = Constants.PHONE_NUMBER_MIN_LENGTH, max = Constants.PHONE_NUMBER_MAX_LENGTH)
	private String mobilePhone;
    @NotNull
	private boolean primaryContact = false;
    private boolean manager = false;

	@Deprecated
	private String clientCompanyName;
	@Deprecated
	private String clientLocationName; // See client_contact_location_association
	@Deprecated
	private String clientLocationNumber; // See client_contact_location_association

	private List<EmailAddressDTO> emails = Lists.newArrayList();
	private List<PhoneNumberDTO> phoneNumbers = Lists.newArrayList();
	private List<WebsiteDTO> websites = Lists.newArrayList();

	public Long getContactId() {
		return contactId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public String getEmail() {
		return email;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public String getWorkPhoneExtension() {
		return workPhoneExtension;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public void setWorkPhoneExtension(String workPhoneExtension) {
		this.workPhoneExtension = workPhoneExtension;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public void setPrimaryContact(boolean primaryContact) {
		this.primaryContact = primaryContact;
	}

	public boolean isPrimaryContact() {
		return primaryContact;
	}

    public Long getClientCompanyId() {
		return clientCompanyId;
	}

    public void setClientCompanyId(Long clientCompanyId) {
		this.clientCompanyId = clientCompanyId;
	}

	public Long getClientLocationId() {
		return clientLocationId;
	}

	public void setClientLocationId(Long clientLocationId) {
		this.clientLocationId = clientLocationId;
	}

	public boolean isManager() {
		return manager;
	}

	public void setManager(boolean manager) {
		this.manager = manager;
	}

	public String getClientCompanyName() {
		return clientCompanyName;
	}

	public void setClientCompanyName(String clientCompanyName) {
		this.clientCompanyName = clientCompanyName;
	}

	public String getClientLocationName() {
		return clientLocationName;
	}

	public void setClientLocationName(String clientLocationName) {
		this.clientLocationName = clientLocationName;
	}

	public List<EmailAddressDTO> getEmails() {
		return emails;
	}

	public void setEmails(List<EmailAddressDTO> emails) {
		this.emails = emails;
	}

	public List<PhoneNumberDTO> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<PhoneNumberDTO> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public List<WebsiteDTO> getWebsites() {
		return websites;
	}

	public void setWebsites(List<WebsiteDTO> websites) {
		this.websites = websites;
	}

	public String getClientLocationNumber() {
		return clientLocationNumber;
	}

	public void setClientLocationNumber(String clientLocationNumber) {
		this.clientLocationNumber = clientLocationNumber;
	}


	@Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientContactDTO that = (ClientContactDTO) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (jobTitle != null ? !jobTitle.equals(that.jobTitle) : that.jobTitle != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (mobilePhone != null ? !mobilePhone.equals(that.mobilePhone) : that.mobilePhone != null) return false;
        if (primaryContact != that.primaryContact) return false;
        if (workPhone != null ? !workPhone.equals(that.workPhone) : that.workPhone != null) return false;
		if (workPhoneExtension != null ? !workPhoneExtension.equals(that.workPhoneExtension) : that.workPhoneExtension != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (jobTitle != null ? jobTitle.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (workPhone != null ? workPhone.hashCode() : 0);
		result = 31 * result + (workPhoneExtension != null ? workPhoneExtension.hashCode() : 0);
        result = 31 * result + (mobilePhone != null ? mobilePhone.hashCode() : 0);
        result = 31 * result + new Boolean(primaryContact).hashCode();
        result = 31 * result + super.hashCode();
        return result;
    }

}
