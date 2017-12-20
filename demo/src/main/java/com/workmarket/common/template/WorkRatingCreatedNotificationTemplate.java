package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;
import com.workmarket.web.helpers.RatingStarsHelper;

public class WorkRatingCreatedNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = -595417338567435402L;
	private Rating rating;
	private String companyName;

	public WorkRatingCreatedNotificationTemplate(Long toId, Rating rating) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType((rating.getWork() == null) ? NotificationType.RESOURCE_WORK_RATED : NotificationType.WORK_RATED), ReplyToType.TRANSACTIONAL, rating.getWork());
		this.rating = rating;
		this.companyName = rating.getRatingCompany().getEffectiveName();
	}

	public Rating getRating() {
		return rating;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getRatingDescription() {
		return RatingStarsHelper.getLevels(rating.getValue());
	}

	public String getQualityDescription() {
		return RatingStarsHelper.getLevels(rating.getQuality());
	}

	public String getProfessionalismDescription() {
		return RatingStarsHelper.getLevels(rating.getProfessionalism());
	}

	public String getCommunicationDescription() {
		return RatingStarsHelper.getLevels(rating.getProfessionalism());
	}

}
