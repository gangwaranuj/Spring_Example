package com.workmarket.domains.work.service.actions;

import com.google.common.base.Optional;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.project.Project;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

public interface WorkEventFactory {
	AddNotesWorkEvent createAddNotesWorkAction(
			List<String> workNumbers,
			User user,
			String actionName,
			String messageKey,
			String content,
			boolean isPrivate);

	RemoveAttachmentsEvent createRemoveAttachmentsEvent(
			List<String> workNumbers,
			User user,
			String actionName,
			String messageKey,
			String assetId);

	GetAttachmentsEvent createGetAttachmentsEvent(
			List<String> workNumbers,
			User user,
			String actionName,
			String messageKey);

	AddAttachmentsWorkEvent createAddAttachmentsEvent(
			List<String> workNumbers,
			User user,
			String actionName,
			String messageKey,
			String associationType,
			String mimeType,
			String filename,
			String description,
			long contentLength,
			String absoluteFilePath);

	AddAttachmentsWorkEvent createAddAttachmentsEventQueue(
			List<String> workNumbers,
			User user,
			String actionName,
			String messageKey,
			String associationType,
			String mimeType,
			String filename,
			String description,
			long contentLength,
			String absoluteFilePath);

	ApproveForPaymentWorkEvent createApproveForPaymentEvent(
			List<String> workNumbers,
			User user,
			String actionName,
			String messageKey);

	RescheduleEvent createRescheduleEvent(
			List<String> workNumbers,
			List<Work> works,
			User user,
			String note,
			String actionName,
			String messageKey,
			Optional<String> startDateTime,
			Optional<String> startDateTimeFormat,
			Optional<String> endDateTime,
			Optional<String> endDateTimeFormat);

	BulkLabelRemovalEvent createBulkLabelRemovalEvent(
			Set<Long> workIds,
			User user,
			String note,
			List<Long> labelIds,
			String actionName,
			String messageKey);

	BulkCancelWorksEvent createBulkCancelWorksEvent(
			Set<Long> workIds,
			User user,
			String actionName,
			String messageKey,
			String note,
			Double price,
			String cancellationReasonTypeCode);

	BulkEditClientProjectEvent createBulkEditClientProjectEvent(
			List<Work> works,
			List<String> workNumbers,
			User user,
			String actionName,
			String messageKey,
			ClientCompany company,
			Project project);

	DoNothingEvent createDoNothingEvent(
			List<String> workNumbers,
			User user,
			String actionName,
			String messageKey);

}
