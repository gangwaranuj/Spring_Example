package com.workmarket.domains.model.linkedin;

import com.google.code.linkedinapi.schema.Location;
import com.google.code.linkedinapi.schema.PhoneType;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity(name = "linkedInPerson")
@Table(name = "linkedin_person")
@AuditChanges
public class LinkedInPerson extends LinkedInEntity {
	private static final long serialVersionUID = 5430080757801510116L;

	private User user;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String headline;
	private LinkedInLocation location;
	private String industry;
	private Long numRecommenders;
	private Long numConnections;
	private Long numConnectionsCapped;
	private String summary;
	private String publicProfileUrl;

	private String interests;
	private String associations;
	private String honors;
	private String specialties;
	private String pictureUrl;
	private String mainAddress;

	private Set<LinkedInEducation> linkedInEducation = Sets.newLinkedHashSet();
	private Set<LinkedInPosition> linkedInPositions = Sets.newLinkedHashSet();
	private Set<LinkedInPhoneNumber> linkedInPhoneNumbers = Sets.newLinkedHashSet();

	@OneToOne(optional = true, cascade = {}, fetch = FetchType.LAZY, orphanRemoval = false)
	@JoinColumn(name = "user_id", nullable = true)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "first_name", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "last_name", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "email_address", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Column(name = "headline", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	@Embedded
	@AttributeOverrides({
				@AttributeOverride(name = "country_code", column = @Column(name = "location_country_code"))
		})
		public LinkedInLocation getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location == null ? null : new LinkedInLocation(location);
	}

	public void setLocation(LinkedInLocation location) {
		this.location = location;
	}

	@Column(name = "industry", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	@Column(name = "num_recommenders")
	public Long getNumRecommenders() {
		return numRecommenders;
	}

	public void setNumRecommenders(Long numRecommenders) {
		this.numRecommenders = numRecommenders;
	}

	@Column(name = "num_connections")
	public Long getNumConnections() {
		return numConnections;
	}

	public void setNumConnections(Long numConnections) {
		this.numConnections = numConnections;
	}

	@Column(name = "num_connections_capped")
	public Long getNumConnectionsCapped() {
		return numConnectionsCapped;
	}

	public void setNumConnectionsCapped(Long numConnectionsCapped) {
		this.numConnectionsCapped = numConnectionsCapped;
	}

	@Column(name = "summary", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Column(name = "public_profile_url", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getPublicProfileUrl() {
		return publicProfileUrl;
	}

	public void setPublicProfileUrl(String publicProfileUrl) {
		this.publicProfileUrl = publicProfileUrl;
	}

	@Column(name = "interests", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	@Column(name = "associations", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getAssociations() {
		return associations;
	}

	public void setAssociations(String associations) {
		this.associations = associations;
	}

	@Column(name = "honors", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getHonors() {
		return honors;
	}

	public void setHonors(String honors) {
		this.honors = honors;
	}

	@Column(name = "specialties", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getSpecialties() {
		return specialties;
	}

	public void setSpecialties(String specialties) {
		this.specialties = specialties;
	}

	@Column(name = "picture_url", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	@Column(name = "main_address", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getMainAddress() {
		return mainAddress;
	}

	public void setMainAddress(String mainAddress) {
		this.mainAddress = mainAddress;
	}

	@OneToMany(mappedBy = "linkedInPerson", cascade = {}, fetch = FetchType.LAZY)
	@org.hibernate.annotations.OrderBy(clause = "COALESCE(end_year,3000) DESC,COALESCE(end_month,13) DESC")
	public Set<LinkedInEducation> getLinkedInEducation() {
		return linkedInEducation;
	}

	public void setLinkedInEducation(Set<LinkedInEducation> linkedInEducation) {
		this.linkedInEducation = linkedInEducation;
	}

	@OneToMany(mappedBy = "linkedInPerson", cascade = {}, fetch = FetchType.LAZY)
	@org.hibernate.annotations.OrderBy(clause = "COALESCE(end_year,3000) DESC,COALESCE(end_month,13) DESC")
	public Set<LinkedInPosition> getLinkedInPositions() {
		return linkedInPositions;
	}

	public void setLinkedInPositions(Set<LinkedInPosition> linkedInPositions) {
		this.linkedInPositions = linkedInPositions;
	}

	@OneToMany(mappedBy = "linkedInPerson", cascade = {}, fetch = FetchType.LAZY)
	public Set<LinkedInPhoneNumber> getLinkedInPhoneNumbers() {
		return linkedInPhoneNumbers;
	}

	public void setLinkedInPhoneNumbers(Set<LinkedInPhoneNumber> linkedInPhoneNumbers) {
		this.linkedInPhoneNumbers = linkedInPhoneNumbers;
	}

	@Transient
	public String getPostalCode() {
		String ret = null;
		String mainAddress = (this.mainAddress == null) ? "" : this.mainAddress;
		String pattern = ".*(\\d{5})$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(mainAddress.trim());

		if (m.find()) {
			ret = m.group(1);
		}

		return ret;
	}

	@Transient
	public String getMobileOrOtherPhoneNumber() {
		String ret = null;

		// favor mobile number
		Set<LinkedInPhoneNumber> lips = getLinkedInPhoneNumbers();
		if (lips == null) return null;
		for (LinkedInPhoneNumber lip : lips) {
			ret = lip.getPhoneNumber();
			if (lip.getPhoneType().equals(PhoneType.MOBILE)) break;
		}
		return ret;
	}
}
