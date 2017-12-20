package com.workmarket.domains.work.dao;

import com.workmarket.dao.rating.RatingDAO;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.work.model.WorkResourceFeedbackRow;
import com.workmarket.domains.work.model.WorkResourceLabel;
import com.workmarket.utility.CollectionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class WorkResourceDecoratorImpl implements WorkResourceDecorator {

	@Autowired private WorkResourceLabelDAO labelDAO;
	@Autowired private RatingDAO ratingDAO;

	@Override
	@SuppressWarnings("unchecked")
	public <T extends WorkResourceFeedbackRow> List<T> addWorkResourceLabels(Long userId, Long viewingUserId, Long viewingCompanyId, List<T> rows) {
		Set<Long> workIds = CollectionUtilities.newSetPropertyProjection(rows, "workId");

		Map<Long,List<WorkResourceLabel>> labelLookup = labelDAO.findConfirmedForUserByCompanyInWork(userId, viewingUserId, viewingCompanyId, workIds);
		for (WorkResourceFeedbackRow r : rows) {
			Long key = r.getWorkId();
			if (labelLookup.containsKey(key)) {
				r.setResourceLabels(labelLookup.get(key));
			}
		}

		return rows;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends WorkResourceFeedbackRow> List<T> addRating(Long userId, Long viewingCompanyId, List<T> rows) {
		Set<Long> workIds = CollectionUtilities.newSetPropertyProjection(rows, "workId");

		Map<Long,Rating> ratingLookup = ratingDAO.findLatestForUserVisibleToCompanyInWork(userId, viewingCompanyId, workIds);
		for (WorkResourceFeedbackRow r : rows) {
			Long key = r.getWorkId();
			if (ratingLookup.containsKey(key)) {
				Rating rating = ratingLookup.get(key);
				r.setRatingValue(rating.getValue());
				r.setRatingReview(rating.getReview());
				r.setQualityValue(rating.getQuality());
				r.setProfessionalismValue(rating.getProfessionalism());
				r.setCommunicationValue(rating.getCommunication());
			}
		}

		return rows;
	}
}
