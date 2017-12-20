package com.workmarket.domains.work.service;

import com.sun.istack.Nullable;
import com.workmarket.common.service.wrapper.response.Response;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkResourcePagination;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.audit.ViewType;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.resource.LiteResource;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkDue;
import com.workmarket.domains.work.model.WorkPagination;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;
import com.workmarket.domains.work.model.WorkSchedule;
import com.workmarket.domains.work.model.WorkUniqueId;
import com.workmarket.domains.work.model.WorkWorkResourceAccountRegister;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.StopPaymentDTO;
import com.workmarket.service.business.dto.UnassignDTO;
import com.workmarket.service.business.dto.WorkAggregatesDTO;
import com.workmarket.service.business.dto.BuyerIdentityDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.business.wrapper.CloseWorkResponse;
import com.workmarket.service.exception.IllegalWorkAccessException;
import com.workmarket.service.exception.account.AccountRegisterConcurrentException;
import com.workmarket.service.exception.account.DuplicateWorkNumberException;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.thrift.work.AcceptWorkOfferRequest;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.thrift.work.TimeTrackingResponse;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkActionResponse;
import net.fortuna.ical4j.model.ValidationException;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WorkService {

	void saveOrUpdateWork(Work work);

	Long findWorkId(String workNumber);

	Long findBuyerCompanyId(long workId);

	// Work Lifecycle: Buyer actions

	List<Long> findWorkIdsByBuyerAndStatus(Long buyerId, String... workStatusType);

	List<Long> findAllWorkIdsByUUIDs(final List<String> workUUIDs);

	int countWorkByCompanyUserRangeAndStatus(Long companyId, Long userId, List<Long> excludeIds, Calendar fromDate, Calendar toDate, List<String> statuses);

	/**
	 * Void when no resources have yet to accept the assignment.
	 *
	 *  @param user
	 *  @param deleteAssignments
	 *  @param voidAssignments
	 *
	 */
	void deleteDraftAndSent(Long user, List<Long> deleteAssignments, List<Long> voidAssignments);

	/**
	 * Void when no resources have yet to accept the assignment.
	 *
	 * @param workId
	 * @return Constraint violations
	 * @throws AccountRegisterConcurrentException
	 *
	 */
	List<ConstraintViolation> voidWork(Long workId, String message) throws AccountRegisterConcurrentException;


	/**
	 * Void or cancel work, depending on the work's current status.
	 *
	 * @param cancelWorkDTO
	 * @return Constraint violations
	 * @throws AccountRegisterConcurrentException
	 *
	 */
	List<ConstraintViolation> cancelWork(CancelWorkDTO cancelWorkDTO) throws AccountRegisterConcurrentException;

	void transitionWorkToCanceledState(
		Long workId,
		CancelWorkDTO cancelWorkDTO,
		WorkActionRequest workRequest,
		final WorkStatusType newWorkStatus
	);

	/**
	 * Send assignment back to active status for eventual unassignment
	 *
	 * @param workId
	 */
	void handleIncompleteWork(Long workId);

	/**
	 * Mark an assignment as incompleted, effectively returning it to the work
	 * resource.
	 *
	 * @param workId
	 * @param message Message that goes back to the assigned work resource
	 */
	void incompleteWork(Long workId, String message);

	/**
	 * Stop Payment and mark the assignment as incompleted, return it back to the work
	 * resource too
	 *
	 * @param workId
	 * @param dto
	 */
	List<ConstraintViolation> stopWorkPayment(Long workId, StopPaymentDTO dto);

	/**
	 * Close an assignments. Charges the buyer and pays the resource or
	 * generates the required payment terms transactions.
	 *
	 * @param workId
	 * @return Constraint violations
	 */
	CloseWorkResponse closeWork(Long workId);

	CloseWorkResponse closeWork(Long workId, CloseWorkDTO dto);

	// Work Lifecycle: Work resource actions

	WorkActionResponse acceptWorkOnBehalf(AcceptWorkOfferRequest request) throws WorkActionException;
	/**
	 * Action for a work resource to accept an assignment invitation.
	 *
	 * @param userId Work resource's user ID
	 * @param workId Work to be accepted
	 * @throws IllegalWorkAccessException
	 */

	AcceptWorkResponse acceptWork(Long userId, Long workId);

	AcceptWorkResponse acceptWork(User user, Work work);

	/**
	 * Action for a work resource to decline an assignment invitation.
	 *
	 * @param userId Work resource's user ID
	 * @param workId Work to be declined
	 */
	void declineWork(Long userId, Long workId);

	void declineWork(Long userId, Long workId, Long onBehalfOfUserId);

	/**
	 * Action for a work resource to reverse a declined assignment
	 *
	 * @param workResource
	 */
	void undeclineWork(WorkResource workResource);

	/**
	 * Deletes an assignment with Draft status.
	 *
	 * @param userId
	 * @param workId
	 * @
	 */
	boolean deleteDraft(Long userId, Long workId);

	/**
	 * Action for a work resource who has accepted an assignment to
	 * cancel/abandon.
	 *
	 * @param userId Work resource's user ID
	 * @param workId Work to be cancelled
	 * @return Constraint violations
	 */
	List<ConstraintViolation> abandonWork(Long userId, Long workId, String message);

	/**
	 * Assumes the action is buyer initiated.
	 *	 */
	void unassignWork(UnassignDTO unassignDTO);

	/**
	 *  Unassigns worker from an active assignment without cancelling said assignment.  This version includes
	 *  unassign-related validation from WorkValidationService, whereas unassignWork does not.
	 *
	 * @param unassignDTO Parameters around how to do the unassign
	 * @return Constraint violations
	 */
	List<ConstraintViolation> unassignWorker(UnassignDTO unassignDTO);

	/**
	 * Action to unassign the active work resource from an assignment. This is
	 * used to effectively cancel the active resource's involvement.
	 *
	 * @param workId         Work to be cancelled
	 * @param buyerInitiated Boolean indicating whether the unassignment was initiated by
	 *                       the buyer or resource
	 * @return Constraint violations
	 */
	void removeWorkerFromWork(Long workId, Boolean buyerInitiated);

	/**
	 * Action for a work resource to mark an assignment as finished. The DTO
	 * provides an interface for setting resolution status, hours/units worked,
	 * and other aspects affecting the ultimate cost of the assignment.
	 *
	 * @param workId
	 * @param dto
	 * @return Constraint violations
	 */
	List<ConstraintViolation> completeWork(Long workId, CompleteWorkDTO dto);

	List<ConstraintViolation> completeWork(Long workId, Long onBehalfOf, CompleteWorkDTO dto);

	// Work CRUD

	Work saveOrUpdateWork(Long userId, WorkDTO workDTO);

	<T extends AbstractWork> T findWork(Long workId);

	/**
	 * Loads Work with custom fields and, if bundle, the bundle children
	 *
	 * @param workId
	 * @param <T>
	 * @return
	 */
	<T extends AbstractWork> T findWorkForInvitation(Long workId);

	<T extends AbstractWork> T findWork(Long workId, boolean loadEverything);

	<T extends AbstractWork> T findWorkByWorkNumber(String workNumber);

	<T extends AbstractWork> T findWorkByWorkNumber(String workNumber, boolean loadEverything);

	List<AbstractWork> findWorkByWorkNumbers(List<String> workNumbers);

	WorkPagination findWorkByWorkResource(Long userId, WorkPagination pagination);

	WorkPagination findWorkByBuyerAndWorkResource(Long buyerId, Long resourceUserId, WorkPagination pagination);

	WorkAggregatesDTO countWorkByCompany(Long companyId);

	/**
	 * Reprices an assignment with the condition that the assignment price doesn't go lower than the original price when the assignment is ACTIVE
	 *
	 * @param workId
	 * @param workDTO
	 * @return List<ConstraintViolation>
	 */
	List<ConstraintViolation> repriceWork(Long workId, WorkDTO workDTO);

	/**
	 * Reprices an assignment after a negotiation, bypassing the condition that the assignment price shouldn't go lower
	 * than the original price when the assignment is ACTIVE if the Negotiation is an Apply negotiation.
	 *
	 * @param workId
	 * @param workDTO
	 * @return
	 */
	List<ConstraintViolation> repriceWork(Long workId, WorkDTO workDTO, AbstractWorkNegotiation abstractWorkNegotiation);

	/**
	 * Check if an "active" assignment is "in progress".
	 */
	Boolean isWorkInProgress(Long workId);

	Boolean isWorkShownInFeed(Long workId);

	/**
	 * Checks if client Company has active assignments
	 */
	boolean doesClientCompanyHaveActiveAssignments(Long clientCompanyId);

	// Work Resources

	/**
	 * Find list of workers associated with work.
	 *
	 * @param workId
	 * @param pagination
	 * @return Work resource pagination
	 */
	WorkResourcePagination findWorkResources(Long workId, WorkResourcePagination pagination);

	/**
	 * Find user associated with work. Returns null if the user is not found.
	 *
	 * @param userId
	 * @param workId
	 * @return Work resource
	 */
	WorkResource findWorkResource(Long userId, Long workId);

	/**
	 * Find user assigned to work who accepted and is active. An active resource
	 * is the user who accepted the work and is responsible for completing the
	 * assignment.
	 *
	 * @param workId
	 * @return Work resource
	 */
	WorkResource findActiveWorkResource(Long workId);

	Long findActiveWorkerId(Long workId);

	/**
	 * Check if the user is an associated resource for the assignment.
	 *
	 * @param userId
	 * @param workId
	 * @return Boolean
	 */
	boolean isUserWorkResourceForWork(Long userId, Long workId);

	/**
	 * Check if the user is the "active" (i.e. accepted) resource for the
	 * assignment.
	 *
	 * @param userId
	 * @param workId
	 * @return Boolean
	 */
	boolean isUserActiveResourceForWork(Long userId, Long workId);

	/**
	 * Check if the user is the "active" resource for any assignments that also
	 * are associated with this assessment.
	 *
	 * @param userId
	 * @param assessmentId
	 * @return
	 */
	boolean isUserActiveResourceForWorkWithAssessment(Long userId, Long assessmentId);

	/**
	 * If the work creator requested confirmation from resource, a message is
	 * dispatched to the resource who accepted it. This method confirms that the
	 * message still needs to be sent to the resource, since there are multiple
	 * variables that could change from the time the resource accepted the work.
	 *
	 * @param userId The work resource
	 * @param workId
	 * @return TRUE if is still valid, FALSE otherwise
	 * @
	 */
	boolean isWorkResourceConfirmationValid(Long userId, Long workId);

	WorkResource confirmWorkResource(Long userId, Long workId);

	/**
	 * Creates or modifies a check-in record.
	 *
	 * @param timeTrackingRequest
	 * @return
	 */
	TimeTrackingResponse checkInActiveResource(TimeTrackingRequest timeTrackingRequest);

	TimeTrackingResponse checkOutActiveResource(TimeTrackingRequest timeTrackingRequest);

	@Nullable
	void deleteCheckInResource(Long workId, Long timeTrackingId);

	@Nullable
	void deleteCheckOutResource(Long workId, Long timeTrackingId);

	WorkResourceTimeTracking findLatestTimeTrackRecordByWorkResource(Long workResourceId);

	List<WorkResourceTimeTracking> findTimeTrackingByWorkResource(Long workResourceId);

	/**
	 * If the work creator requested that the resource should checkin when
	 * arriving to the location, a message is dispatched to the resource who
	 * accepted it 10 Minutes before the work's schedule datetime. This method
	 * confirms that the message still needs to be sent, since there are
	 * multiple variables that could change from the time the resource accepted
	 * the work.
	 *
	 * @param userId The work resource
	 * @param workId
	 * @return TRUE if is still valid, FALSE otherwise
	 * @
	 */
	boolean isWorkResourceCheckInValid(Long userId, Long workId);

	/**
	 * Returns TRUE if the active resource, on an in-progress assignment, has checked in but has not yet
	 * checked out.  So they're potentially still onsite, for example.
	 *
	 * @param workId
	 * @return TRUE if resource is checked in, but has not yet checked out
	 * @
	 */
	boolean isActiveResourceCurrentlyCheckedIn(Long workId);

	/**
	 * Returns TRUE if the active resource has checked in but has not yet
	 * checked out.  So they're potentially still onsite, for example.
	 *
	 * @param work
	 * @return TRUE if resource is checked in, but has not yet checked out
	 * @
	 */
	boolean isResourceCurrentlyCheckedIn(Work work);

	Response resendInvitationsAsync(Long workId, List<Long> resourcesIds);
	Response resendInvitationsAsync(String workNumber);

	void resendInvitations(Long workId, List<Long> resourcesIds);

	/**
	 * Send reminder to the active resource of an assignment
	 * instructing them to complete the assignment.
	 *
	 * @param workId
	 * @param message
	 */
	void remindResourceToComplete(Long workId, String message);

	void addFirstToAcceptGroupsForWork(Collection<Long> groupIds, Long workId);

	void addGroupsForWork(Collection<Long> groupIds, Long workId);

	void clearGroupsForWork(Long workId);
	/**
	 * Creates a Calendar object based on the work information
	 *
	 * @param userId
	 * @param workId
	 * @return String containing the absolute file path of the created calendar.
	 * @throws ValidationException
	 * @throws IOException
	 * @
	 */
	String createCalendar(Long userId, Long workId) throws IOException, ValidationException;

	// Projects

	List<Work> findAllWorkByProject(Long projectId);

	List<Work> findAllWorkByProjectByStatus(Long projectId, String... status);

	/**
	 * Overrides the state of work object with data in DTO
	 *
	 * @param userId
	 * @param workDTO
	 * @param work
	 * @param <T>
	 * @return
	 * @
	 */
	<T extends AbstractWork> T buildWork(Long userId, WorkDTO workDTO, T work, boolean initialize);

	/**
	 * Finds all work resources
	 *
	 * @param companyId
	 * @param contractorUserId
	 * @param statuses
	 * @return
	 */
	boolean doesWorkerHaveWorkWithCompany(Long companyId, Long contractorUserId, List<String> statuses);

	void updateWorkProperties(Long workId, Map<String, String> properties) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException;

	void updateMyPaidResourcesGroup(Long workId, Long workResourceId, User actor);

	WorkResource findWorkResourceById(Long workResourceId);

	/**
	 * Returns the context representing how a user is related to an assignment.
	 *
	 * @param workId
	 * @param userId
	 * @return {@link com.workmarket.service.infra.security.WorkContext
	 *         WorkContext}
	 * @
	 */
	List<WorkContext> getWorkContext(long workId, long userId);

	List<WorkContext> getWorkContext(AbstractWork work,User user);

	/**
	 * Check whether user is authorized to administer the assignment.
	 *
	 * @param workId
	 * @param userId
	 * @return
	 * @
	 */
	boolean isAuthorizedToAdminister(Long workId, Long userId);

	boolean isAuthorizedToAdminister(String workNumber, Long userId);

	boolean isAuthorizedToAdminister(Long userId, Long userCompanyId, String workNumber);

	boolean isAuthorizedToAccept(String workNumber, Long userId);

	boolean isAuthorizedToAcceptNegotiation(Long workId, Long userId, AbstractWorkNegotiation negotiation);

	TimeZone findAssignmentTimeZone(Long workId);

	boolean isAutomaticAppointmentChange(Long workId, DateRange appointment);

	DateRange getAppointmentTime(Long workId);

	DateRange getAppointmentTime(AbstractWork work);

	void setAppointmentTime(Long workId, Calendar apptTime, String message);

	void setAppointmentTime(Long workId, DateRange appointment, String message);

	Work updateWorkSchedule(Work work,DateRange appointment,String message);

	/**
	 * Returns the assignment scheduled through time if there's a window, otherwise the scheduled from
	 *
	 * @param work
	 * @return Calendar
	 */
	Calendar calculateMaxAppointmentDate(AbstractWork work);

	Calendar calculateMaxAppointmentDateLatenessThreshold(AbstractWork work);

	Calendar calculateRequiredCheckinDate(AbstractWork work);

	Calendar calculateRequiredCheckinReminderDate(AbstractWork work);
	Calendar calculateRequiredCheckinReminderDate(Long workId);

	Calendar calculateRequiredConfirmationNotificationDate(AbstractWork work);
	Calendar calculateRequiredConfirmationNotificationDate(Long workId);

	Calendar calculateRequiredConfirmationDate(AbstractWork work);

	boolean isConfirmableNow(AbstractWork work);

	Long getBuyerIdByWorkNumber(String workNumber) throws DuplicateWorkNumberException;

	List<BuyerIdentityDTO> findBuyerIdentitiesByWorkIds(Collection<Long> workIds);

	Long getBuyerIdByWorkId(Long workId) throws DuplicateWorkNumberException;

	/**
	 * Returns true if the work has the specified status
	 *
	 * @param workNumber The work number
	 * @param status     The work status
	 * @return true if the work has the specified status
	 */
	boolean isWorkStatusForWorkByWorkNumber(String workNumber, String status);

	/**
	 * Returns true if the user has been invited to apply for the assignment,
	 * if it belongs to an employee of it's company or if the user is the assignment's creator.
	 *
	 * @param workNumber
	 * @param userId
	 */
	boolean isWorkStatusAccessibleForUser(String workNumber, Long userId);

	void markWorkViewed(Long workId, Long workResourceUserId, ViewType workViewType);

	List<Long> findWorkerIdsForWork(Long workId);

	double calculateDistanceToWork(Long userId, AbstractWork work);

	boolean isWorkPendingFulfillment(Long workId);


	// Count Work By Company, By Status

	int countWorkByCompanyByStatus(Long companyId, List<String> statuses);

	Integer countAllAssignmentsPaymentPendingByCompany(Long companyId);

	Integer countAllDueWorkByCompany(long companyId);

	int reassignWorkOwnership(Long fromId, Long toId);

	void addRequirementSetsToWork(AbstractWork work, List<Long> requirementSetIds);

	List<Work> findAllWorkWhereWorkStatusTypeNotInAndWorkSubStatusTypeIn(long companyId, long subStatusId, String[] statusCodes);

	List<Work> findAllWorkWhereTemplatesNotInAndWorkSubStatusTypeIn(long companyId, long subStatusId, Long[] templateIds);

	void validateResourceCheckIn(Long workId);

	/**
	 * Returns a list of user ids for all users that have declined this work.
	 *
	 * @param workId
	 */
	List<Long> findDeclinedResourceIds(Long workId);

	Work findWorkByInvoice(Long invoice);

	Map<String, Object> findActiveWorkerTimeWorked(Long workId);

	Map<String, Object> findActiveWorkerTimeWorked(Long workId, Long activeWorkerId);

	List<Integer> findAssignmentsMissingResourceNoShow();

	List<String> findAssignmentsWithDeliverablesDue();

	List<Work> findAssignmentsRequiringDeliverableDueReminder();

	void cleanUpDeliverablesForReassignmentOrCancellation(WorkResource workResource);

	void cleanUpDeliverablesForReschedule(WorkResource workResource);

	List<Long> findOpenWorkIdsBetweenUserAndCompany(Long userId, Long companyId);

	Integer countAllDueWorkByCompany(Calendar dueDateFrom, Calendar dueDateThrough, Long companyId);

	Work updateClientAndProject(ClientCompany client, Project project, long workId);

	List<LiteResource> findLiteResourceByWorkNumber(String workNumber);

	List<Long> findWorkIdsByInvoiceId(Long... invoiceIds);

	List<Integer> getAutoPayWorkIds(Date dueOn, List<String> workStatusTypes);

	List<WorkWorkResourceAccountRegister> findWorkAndWorkResourceForPayment(List<Long> assignmentIds);

	Set<WorkDue> findAllAssignmentsPastDue(Calendar dueDate);

	Set<WorkDue> findAllDueAssignmentsByDueDate(Calendar dueDateFrom, Calendar dueDateThrough);

	boolean isWorkNotifyAllowed(Long workId);

	boolean isWorkNotifyAvailable(Long workId);

	void workNotifyResourcesForWork(Long workId) throws OperationNotSupportedException;

	/**
	 * Revert assignment pricing to the state when the assignment was created
	 * */
	void rollbackToOriginalPricePrice(Long workId);

	/**
	 * Get assignment's original PricingStrategy
	 * */
	PricingStrategy getOriginalWorkPricingStategy(Long workId);

	/**
	 * Get assignment's current PricingStrategy
	 */
	PricingStrategy getCurrentWorkPricingStategy(Long workId);

	WorkUniqueId findUniqueIdByCompanyVersionIdValue(Long companyId, int version, String idValue);

	boolean isOfflinePayment(AbstractWork work);

	void setOfflinePayment(AbstractWork work, boolean offline);

	String getRecurrenceUUID(Long workId);

	void saveWorkRecurrence(Long workId, Long recurringWorkId, String recurrenceUUID);

	Map<String, Object> getAssignmentDataOne(final Map<String, Object> params);


	List<WorkSchedule> augmentWorkSchedules(List<WorkSchedule> workSchedules);

	WorkSchedule augmentWorkSchedule(WorkSchedule schedule);
}
