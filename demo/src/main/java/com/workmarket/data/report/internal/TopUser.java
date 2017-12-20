package com.workmarket.data.report.internal;

/**
 * Created with IntelliJ IDEA.
 * User: rocio
 * Date: 6/26/12
 * Time: 6:26 PM
 */
public class TopUser {

	private Long userId;
	private String userNumber;
	private String firstName;
	private String lastName;
	private String email;
	private Integer sentAssignments;
	private Integer activeAssignments = 0;
	private Integer closedAssignments = 0;
	private Double rating;
	private Double throughput;

	public Double getThroughput() {	return throughput; }

	public void setThroughput(Double throughput) { this.throughput = throughput; }

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getSentAssignments() {
		return sentAssignments;
	}

	public void setSentAssignments(Integer sentAssignments) {
		this.sentAssignments = sentAssignments;
	}

	public Integer getActiveAssignments() {
		return activeAssignments;
	}

	public void setActiveAssignments(Integer activeAssignments) {
		this.activeAssignments = activeAssignments;
	}

	public Integer getClosedAssignments() {
		return closedAssignments;
	}

	public void setClosedAssignments(Integer closedAssignments) {
		this.closedAssignments = closedAssignments;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "TopUser{" +
				"userId=" + userId +
				", userNumber='" + userNumber + '\'' +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", email='" + email + '\'' +
				", sentAssignments=" + sentAssignments +
				", activeAssignments=" + activeAssignments +
				", closedAssignments=" + closedAssignments +
				", rating=" + rating +
				'}';
	}
}
