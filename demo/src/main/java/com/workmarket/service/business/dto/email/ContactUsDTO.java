package com.workmarket.service.business.dto.email;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ContactUsDTO extends PublicEmailDTO {

	@Pattern(regexp = "^$|(^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$)?")
	protected String phone;

	@Size(max=128)
	protected String title;

	@Size(max=128)
	protected String company;


	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@Override
	public String getFormattedText() {
		return String.format("Phone: %s<br/>Company: %s<br/>Message: %s<br/>", phone, company, text);
	}
}
