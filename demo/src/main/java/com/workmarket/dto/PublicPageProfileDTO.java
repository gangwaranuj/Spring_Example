package com.workmarket.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by rahul on 12/15/13
 */
public class PublicPageProfileDTO {
	@NotNull
	private String firstName;
	@NotNull
	private String lastName;
	@NotNull
	private String userNumber;
	@NotNull
	private String gender;
	@NotNull
	private String profileImageUri;
	@NotNull
	private String shortDescription;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getProfileImageUri() {
		return profileImageUri;
	}

	public void setProfileImageUri(String profileImageUri) {
		this.profileImageUri = profileImageUri;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String toString() {
		return "PublicPageProfileDTO [userNumber=" + userNumber + ", firstName=" + firstName + ", lastName=" + lastName + ", gender="
				+ gender + ", profileImageUri=" + profileImageUri + ", shortDescription=" + shortDescription + "]";
	}
}
