package com.workmarket.service.business.dto;

import com.workmarket.service.business.feed.FeedItem;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.DateUtilities;
import com.workmarket.xml.CDATAAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@XmlRootElement(name = "job")
@XmlType(propOrder = {"title", "date", "referenceNumber", "url", "company", "city", "state", "country", "description",
		"salary", "education", "jobType", "category", "experience"})
public class PublicWorkDTO {
	private String title;
	private Date date;
	private String referenceNumber;
	private String company;
	private String city;
	private String state;
	private String country;
	private String description;
	private double salary;
	private String education;
	private String jobType;
	private String category;
	private String experience;

	public PublicWorkDTO(FeedItem item) {
		setTitle(item.getPublicTitle());
		setDescription(item.getDescription());
		setCity(item.getCity());
		setDate(item.getCreatedDate());
		setReferenceNumber(item.getWorkNumber());
		setSalary(item.getSpendLimit());
		setState(item.getState());
		setCompany(item.getCompanyName());
		setCountry(item.isOffSite() ? "USA" : item.getCountry() );
	}

	public PublicWorkDTO(){

	}

	@XmlElement
	@XmlJavaTypeAdapter(CDATAAdapter.class)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@XmlElement
	@XmlJavaTypeAdapter(CDATAAdapter.class)
	public String getDate() {
		return DateUtilities.format("EEE, d MMM y h:mmaa", date);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@XmlElement(name = "referencenumber")
	@XmlJavaTypeAdapter(CDATAAdapter.class)
	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	@XmlElement
	@XmlJavaTypeAdapter(CDATAAdapter.class)
	public String getUrl() {
		return Constants.PROD_PUBLIC_BASE_URL+"/work/"+referenceNumber;
	}


	@XmlElement
	@XmlJavaTypeAdapter(CDATAAdapter.class)
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@XmlElement
	@XmlJavaTypeAdapter(CDATAAdapter.class)

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@XmlElement
	@XmlJavaTypeAdapter(CDATAAdapter.class)
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@XmlElement
	@XmlJavaTypeAdapter(CDATAAdapter.class)
	public String getCountry() {
		return country;
	}

	public void setCountry(String county) {
		this.country = county;
	}

	@XmlElement
	@XmlJavaTypeAdapter(CDATAAdapter.class)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement
	@XmlJavaTypeAdapter(CDATAAdapter.class)
	public String getSalary() {
		return String.format("$%.2f%n", salary).trim();
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	@XmlElement
	@XmlJavaTypeAdapter(CDATAAdapter.class)
	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	@XmlElement
	@XmlJavaTypeAdapter(CDATAAdapter.class)
	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	@XmlElement
	@XmlJavaTypeAdapter(CDATAAdapter.class)
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@XmlElement
	@XmlJavaTypeAdapter(CDATAAdapter.class)
	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

}
