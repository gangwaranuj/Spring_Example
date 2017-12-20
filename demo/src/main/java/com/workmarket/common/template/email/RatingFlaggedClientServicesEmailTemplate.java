package com.workmarket.common.template.email;

import com.workmarket.domains.model.rating.Rating;
import com.workmarket.utility.DateUtilities;

public class RatingFlaggedClientServicesEmailTemplate extends AbstractClientServicesEmailTemplate {

	private static final long serialVersionUID = 7517378763474309730L;
	private Rating rating;
	private String ratingCreatorFullName;
	private String ratingCreatorUserNumber;
	
	public RatingFlaggedClientServicesEmailTemplate(Long fromId, Rating rating, String ratingCreatorFullName, String ratingCreatorUserNumber) {
		super(fromId);
		this.rating = rating;
		this.ratingCreatorFullName = ratingCreatorFullName;
		this.ratingCreatorUserNumber = ratingCreatorUserNumber;
	}
	
	public String getDate() {
		return DateUtilities.formatDateForEmail(rating.getWork().getScheduleFrom(), this.getTimeZoneId());
	}
	
	public Rating getRating() {
		return rating;
	}

	public String getRatingCreatorFullName() { return ratingCreatorFullName; }

	public String getRatingCreatorUserNumber() { return ratingCreatorUserNumber; }
}
