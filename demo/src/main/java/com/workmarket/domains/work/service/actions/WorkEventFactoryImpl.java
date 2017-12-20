package com.workmarket.domains.work.service.actions;

import com.google.common.base.Optional;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.service.actions.handlers.*;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

@Service
public class WorkEventFactoryImpl implements WorkEventFactory {

	@Autowired AddNotesWorkEventHandler addNotesWorkEventHandler;
	@Autowired RemoveAttachmentsEventHandler removeAttachmentsEventHandler;
	@Autowired GetAttachmentsEventHandler getAttachmentsEventHandler;
	@Autowired AddAttachmentsEventHandler addAttachmentsEventHandler;
	@Autowired ApproveForPaymentEventHandler approveForPaymentEventHandler;
	@Autowired RescheduleEventHandler rescheduleEventHandler;
	@Autowired BulkLabelRemovalEventHandler bulkLabelRemovalEventHandler;
	@Autowired BulkCancelWorksEventHandler bulkCancelWorksEventHandler;
	@Autowired DoNothingTestHandler doNothingTestHandler;
	@Autowired WorkListFetcherService workListFetcherService;
	@Autowired BulkEditClientProjectEventHandler bulkEditClientProjectEventHandler;

	@Override
	public final AddNotesWorkEvent createAddNotesWorkAction(
			List<String> workNumbers,
			User user,
			String actionName,
			String messageKey,
			String content,
			boolean isPrivate) {
		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(true);
		return (AddNotesWorkEvent) new AddNotesWorkEvent.Builder(workNumbers, user, actionName, messageKey, content, isPrivate)
				.work(workListFetcherService.fetchValidatedWork(user, workNumbers, response, messageKey))
				.workEventHandler(addNotesWorkEventHandler)
				.queue()
				.response(response)
				.build();
	}

	@Override
	public final RemoveAttachmentsEvent createRemoveAttachmentsEvent(
			List<String> workNumbers,
			User user,
			String actionName,
			String messageKey,
			String assetId) {
		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(true);
		return (RemoveAttachmentsEvent) new RemoveAttachmentsEvent.Builder(workNumbers, user, actionName, messageKey, assetId)
				.work(workListFetcherService.fetchValidatedWork(user, workNumbers, response, messageKey))
				.workEventHandler(removeAttachmentsEventHandler)
				.response(response)
				.queue()
				.build();
	}

	@Override
	public final GetAttachmentsEvent createGetAttachmentsEvent(
			List<String> workNumbers,
			User user,
			String actionName,
			String messageKey) {
		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(true);
		return (GetAttachmentsEvent) new GetAttachmentsEvent.Builder(workNumbers, user, actionName, messageKey)
				.work(workListFetcherService.fetchValidatedWork(user, workNumbers, response, messageKey))
				.workEventHandler(getAttachmentsEventHandler)
				.response(response)
				.build();
	}

	@Override
	public final AddAttachmentsWorkEvent createAddAttachmentsEvent(List<String> workNumbers,
	                                                               User user,
	                                                               String actionName,
	                                                               String messageKey,
	                                                               String associationType,
	                                                               String mimeType,
	                                                               String filename,
	                                                               String description,
	                                                               long contentLength,
	                                                               String absoluteFilePath) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(true);
		return (AddAttachmentsWorkEvent) new AddAttachmentsWorkEvent.Builder(workNumbers, user, actionName, messageKey, associationType, mimeType, filename, description, contentLength, absoluteFilePath)
				.workEventHandler(addAttachmentsEventHandler)
				.response(response)
				.build();
	}

	@Override
	public final AddAttachmentsWorkEvent createAddAttachmentsEventQueue(
			List<String> workNumbers, User user, String actionName, String messageKey, String associationType,
			String mimeType, String filename, String description, long contentLength, String absoluteFilePath
	) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(true);
		return (AddAttachmentsWorkEvent) new AddAttachmentsWorkEvent.Builder(
			workNumbers, user, actionName, messageKey, associationType, mimeType, filename, description, contentLength, absoluteFilePath
		)
		.workEventHandler(addAttachmentsEventHandler)
		.response(response)
		// TODO - Micah - Add back in when async file upload is resolved
		//.queue()
		.build();
	}

	@Override
	public final ApproveForPaymentWorkEvent createApproveForPaymentEvent(List<String> workNumbers,
	                                                                     User user,
	                                                                     String actionName,
	                                                                     String messageKey) {
		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(true);
		return (ApproveForPaymentWorkEvent) new ApproveForPaymentWorkEvent.Builder(workNumbers, user, actionName, messageKey)
				.work(workListFetcherService.fetchValidatedWork(user, workNumbers, response, messageKey))
				.workEventHandler(approveForPaymentEventHandler)
				.response(response)
				.queue()
				.build();
	}

	@Override
	public final RescheduleEvent createRescheduleEvent(
			final List<String> workNumbers,
			final List<Work> works,
			final User user,
			final String note,
			final String actionName,
			final String messageKey,
			final Optional<String> startDateTime,
			final Optional<String> startDateTimeFormat,
			final Optional<String> endDateTime,
			final Optional<String> endDateTimeFormat) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(true);

		return (RescheduleEvent) new RescheduleEvent.Builder(workNumbers, user, note, actionName, messageKey)
				.setStartDateTime(startDateTime)
				.setStartDateTimeFormat(startDateTimeFormat)
				.setEndDateTime(endDateTime)
				.setEndDateTimeFormat(endDateTimeFormat)
				.work(works)
				.workEventHandler(rescheduleEventHandler)
				.response(response)
				.queue()
				.build();
	}

	@Override
	public BulkLabelRemovalEvent createBulkLabelRemovalEvent(Set<Long> workIds, User user, String note, List<Long> labelIds, String actionName, String messageKey) {
		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(true);

		return (BulkLabelRemovalEvent) new BulkLabelRemovalEvent.Builder(workIds, user, note, labelIds, actionName, messageKey)
				.workEventHandler(bulkLabelRemovalEventHandler)
				.response(response)
				.queue()
				.build();


	}

	@Override
	public BulkCancelWorksEvent createBulkCancelWorksEvent(Set<Long> workIds, User user, String actionName, String messageKey, String note, Double price, String cancellationReasonTypeCode) {
		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(true);

		return (BulkCancelWorksEvent) new BulkCancelWorksEvent.Builder(workIds, user, actionName, messageKey, note, price, cancellationReasonTypeCode)
				.workEventHandler(bulkCancelWorksEventHandler)
				.response(response)
				.queue()
				.build();
	}

	@Override
	public BulkEditClientProjectEvent createBulkEditClientProjectEvent(List<Work> works, List<String> workNumbers, User user, String actionName, String messageKey, ClientCompany company, Project project) {
		AjaxResponseBuilder response = AjaxResponseBuilder.success();

		return (BulkEditClientProjectEvent)new BulkEditClientProjectEvent.Builder(workNumbers, user, actionName, messageKey, company, project)
				.work(works)
				.workEventHandler(bulkEditClientProjectEventHandler)
				.response(response)
				.queue()
				.build();
	}

	@Override
	public final DoNothingEvent createDoNothingEvent(
			List<String> workNumbers,
			User user,
			String actionName,
			String messageKey) {
		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(true);
		return (DoNothingEvent) new DoNothingEvent.Builder(workNumbers, user, actionName, messageKey)
				.work(workListFetcherService.fetchValidatedWork(user, workNumbers, response, messageKey))
				.workEventHandler(doNothingTestHandler)
				.response(response)
				.build();
	}


}
