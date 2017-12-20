package com.workmarket.domains.work.dao;

import com.workmarket.domains.work.model.WorkResourceFeedbackRow;

import java.util.List;

public interface WorkResourceDecorator {
	<T extends WorkResourceFeedbackRow> List<T> addWorkResourceLabels(Long userId, Long viewingUserId, Long viewingCompanyId, List<T> rows);
	<T extends WorkResourceFeedbackRow> List<T> addRating(Long userId, Long viewingCompanyId, List<T> rows);
}
