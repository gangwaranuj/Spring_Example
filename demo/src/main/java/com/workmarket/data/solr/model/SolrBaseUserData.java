package com.workmarket.data.solr.model;

import com.workmarket.utility.StringUtilities;

import java.io.Serializable;

public class SolrBaseUserData implements Serializable {

	private static final long serialVersionUID = 7155497341688484422L;

	private String userNumber;
	private String firstName;
	private String lastName;
	private String email;
	private String overview;
	private int workCompletedCount;
	private double satisfactionRate;
	private double onTimePercentage = 0d;
	private double deliverableOnTimePercentage;
	private boolean sharedWorkerRole;
	private boolean emailConfirmed;
	private int lane3ApprovalStatus;
	private String userStatusType;
	private String workPhone;
	private String mobilePhone;
	private String cbsaName;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEmailConfirmed() {
		return emailConfirmed;
	}

	public void setEmailConfirmed(boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public int getLane3ApprovalStatus() {
		return lane3ApprovalStatus;
	}

	public void setLane3ApprovalStatus(int lane3ApprovalStatus) {
		this.lane3ApprovalStatus = lane3ApprovalStatus;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		return StringUtilities.fullName(firstName, lastName);
	}

	public double getSatisfactionRate() {
		return satisfactionRate;
	}

	public void setSatisfactionRate(double satisfactionRate) {
		this.satisfactionRate = satisfactionRate;
	}

	public double getOnTimePercentage() {
		return onTimePercentage;
	}

	public void setOnTimePercentage(double onTimePercentage) {
		this.onTimePercentage = onTimePercentage;
	}

	public double getDeliverableOnTimePercentage() {
		return deliverableOnTimePercentage;
	}

	public void setDeliverableOnTimePercentage(double deliverableOnTimePercentage) {
		this.deliverableOnTimePercentage = deliverableOnTimePercentage;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public boolean isSharedWorkerRole() {
		return sharedWorkerRole;
	}

	public void setSharedWorkerRole(boolean sharedWorkerRole) {
		this.sharedWorkerRole = sharedWorkerRole;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public String getUserStatusType() {
		return userStatusType;
	}

	public void setUserStatusType(String userStatusType) {
		this.userStatusType = userStatusType;
	}

	public int getWorkCompletedCount() {
		return workCompletedCount;
	}

	public void setWorkCompletedCount(int workCompletedCount) {
		this.workCompletedCount = workCompletedCount;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getCbsaName() {
		return cbsaName;
	}

	public void setCbsaName(String cbsaName) {
		this.cbsaName = cbsaName;
	}
}
