package com.workmarket.domains.model.reporting;

import com.workmarket.utility.DateUtilities;

import java.util.Calendar;

/**
 * User: iloveopt
 * Date: 11/14/13
 */
public class RatingReport {

	private Integer value;
	private String review;
	private String title;
	private String workNumber;
	private String ratingUserFirstName;
	private String ratingUserLastName;
	private String ratedUserFirstName;
	private String ratedUserLastName;
	private Calendar ratingDate;
	private Calendar dueOn;
	private Calendar paidOn;
	private boolean flaggedForReview;

	public String getRatingUserLastName() {
		return ratingUserLastName;
	}

	public void setRatingUserLastName(String ratingUserLastName) {
		this.ratingUserLastName = ratingUserLastName;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}

	public String getRatingUserFirstName() {
		return ratingUserFirstName;
	}

	public void setRatingUserFirstName(String ratingUserFirstName) {
		this.ratingUserFirstName = ratingUserFirstName;
	}

	public String getRatedUserFirstName() {
		return ratedUserFirstName;
	}

	public void setRatedUserFirstName(String ratedUserFirstName) {
		this.ratedUserFirstName = ratedUserFirstName;
	}

	public String getRatedUserLastName() {
		return ratedUserLastName;
	}

	public void setRatedUserLastName(String ratedUserLastName) {
		this.ratedUserLastName = ratedUserLastName;
	}

	public Calendar getRatingDate() {
		return ratingDate;
	}

	public void setRatingDate(Calendar ratingDate) {
		this.ratingDate = ratingDate;
	}

	public Calendar getDueOn() {
		return dueOn;
	}

	public void setDueOn(Calendar dueOn) {
		this.dueOn = dueOn;
	}

	public Calendar getPaidOn() {
		return paidOn;
	}

	public void setPaidOn(Calendar paidOn) {
		this.paidOn = paidOn;
	}

	public boolean isFlaggedForReview() {
		return flaggedForReview;
	}

	public void setFlaggedForReview(boolean flaggedForReview) {
		this.flaggedForReview = flaggedForReview;
	}


	public String getPaymentTimeliness() {
		String paymentTimeliness = "Not Applicable";
		if(getDueOn() !=null && getPaidOn() != null) {
			Calendar dueOn = getDueOn();
			Calendar paidOn = getPaidOn() == null ? Calendar.getInstance() : getPaidOn();
			int daysBetweenDueAndPaid = DateUtilities.getDaysBetween(dueOn, paidOn, false);
			paymentTimeliness = String.valueOf(daysBetweenDueAndPaid);
		}
		return paymentTimeliness;
	}

}
