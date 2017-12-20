package com.workmarket.service.business;

import com.google.common.collect.ImmutableList;
import com.workmarket.data.report.assessment.AttemptReportPagination;
import com.workmarket.data.report.assessment.AttemptResponseAssetReportPagination;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AbstractItem;
import com.workmarket.domains.model.assessment.AssessmentPagination;
import com.workmarket.domains.model.assessment.AssessmentStatistics;
import com.workmarket.domains.model.assessment.AssessmentUserAssociation;
import com.workmarket.domains.model.assessment.AssessmentUserAssociationPagination;
import com.workmarket.domains.model.assessment.SurveyAssessment;
import com.workmarket.dto.AssessmentUserPagination;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.assessment.AttemptResponse;
import com.workmarket.domains.model.assessment.Choice;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination;
import com.workmarket.domains.model.assessment.WorkAssessmentAssociation;
import com.workmarket.domains.model.notification.AssessmentNotificationPreference;
import com.workmarket.dto.AggregatesDTO;
import com.workmarket.service.business.dto.AssessmentChoiceDTO;
import com.workmarket.service.business.dto.AssessmentDTO;
import com.workmarket.service.business.dto.AssessmentItemDTO;
import com.workmarket.service.business.dto.AttemptResponseDTO;
import com.workmarket.service.business.dto.NotificationPreferenceDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.assessment.AssessmentAttemptLimitExceededException;
import com.workmarket.service.exception.assessment.AssessmentAttemptTimedOutException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.security.RequestContext;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AssessmentService {

	/**
	 * Find an assessment by its identifier
	 *
	 * @param assessmentId
	 */
	AbstractAssessment findAssessment(Long assessmentId);

	Integer countAssessmentsByCompany(long companyId);

	Integer countAssessmentsByCompanyCreatedSince(long companyId, Calendar dateFrom);

	Map<Long, String> findActiveSurveysByCompany(Long companyId);

	AssessmentPagination findAssessmentsByCompany(Long companyId, AssessmentPagination pagination);

	/**
	 * Create or update an assessment
	 *
	 * @param userId -
	 * @param assessmentDTO -
	 * @return Assessment
	 */
	AbstractAssessment saveOrUpdateAssessment(Long userId, AssessmentDTO assessmentDTO);

	AbstractAssessment saveOrUpdateAssessment(AbstractAssessment assessment);

	/**
	 * Update the status of an assessment.
	 *
	 * @param assessmentId
	 * @param statusTypeCode
	 */
	void updateAssessmentStatus(Long assessmentId, String statusTypeCode);

	/**
	 * Set the skills for an assessment.
	 *
	 * @param assessmentId
	 * @param skillIds
	 */
	void setSkillsForAssessment(Long assessmentId, Long[] skillIds);

	/**
	 * Set the notifications that should be sent out for an assessment.
	 *
	 * @param assessmentId
	 * @param preferences
	 * @return
	 */
	List<AssessmentNotificationPreference> setNotificationPreferencesForAssessment(Long assessmentId, NotificationPreferenceDTO[] preferences);

	/**
	 * Set the recipients that should receive notifications for an assessment.
	 *
	 * @param assessmentId
	 * @param userIds
	 */
	void setNotificationRecipientsForAssessment(Long assessmentId, Long[] userIds);


	// Item CRUD

	/**
	 * Create, update and reorder a list of assessment questions
	 *
	 * @param assessmentId
	 * @param itemDTOs
	 */
	List<AbstractItem> saveOrUpdateItemsInAssessment(Long assessmentId, AssessmentItemDTO[] itemDTOs);

	/**
	 * Add a new item or edit an existing one. If changing an item's type, note that we effectively
	 * delete the original and swap in the new item in the original's position. Thus it's an "update"
	 * but the returned item will be a new entity with a new identifier.
	 *
	 * @param assessmentId
	 * @param itemDTO
	 * @return
	 */
	AbstractItem addOrUpdateItemInAssessment(Long assessmentId, AssessmentItemDTO itemDTO);

	/**
	 * Remove an item from an assessment.
	 *
	 * @param assessmentId
	 * @param itemId
	 */
	void removeItemFromAssessment(Long assessmentId, Long itemId);

	/**
	 * Reorder the items in an assessment.
	 *
	 * @param assessmentId
	 * @param itemIds
	 * @return
	 */
	List<AbstractItem> reorderItemsInAssessment(Long assessmentId, Long[] itemIds);

	/**
	 * Create, update and reorder a list of answers to an assessment question
	 *
	 * @param itemId
	 * @param choiceDTOs
	 */
	List<Choice> saveOrUpdateChoicesInItem(Long itemId, AssessmentChoiceDTO[] choiceDTOs);

	/**
	 * Add a new choice or edit an existing one.
	 *
	 * @param itemId
	 * @param choiceDTO
	 * @return
	 */
	Choice addOrUpdateChoiceInItem(Long itemId, AssessmentChoiceDTO choiceDTO);

	/**
	 * Reorder the choices in an item.
	 *
	 * @param itemId
	 * @param choiceIds
	 * @return
	 */
	List<Choice> reorderChoicesInItem(Long itemId, Long[] choiceIds);


	AssessmentUserAssociation findAssessmentUserAssociationByUserAndAssessment(Long userId, Long assessmentId);

	AssessmentUserAssociationPagination findAssessmentUserAssociationsByUser(Long userId, AssessmentUserAssociationPagination pagination);

	AssessmentUserAssociationPagination findAssessmentUserAssociationsByUsers(Set<Long> userIds, AssessmentUserAssociationPagination pagination);

	List<String> getActiveAssessmentForGroup(Long companyId, Long assessmentId);

	List<String> getActiveAssessmentForAssignment(Long companyId, Long assessmentId);

	List<String> getActiveAssessmentForReqSet(Long companyId, Long assessmentId);

	boolean hasUserPassedAssessment(Long userId, Long assessmentId);

	/**
	 * Find all of a user's attempts of an assessment.
	 *
	 * @param assessmentId
	 * @param userId
	 * @return
	 */
	List<Attempt> findAttemptsForAssessmentByUser(Long assessmentId, Long userId);

	/**
	 * Find the user's latest attempt at an assessment.
	 *
	 * @param assessmentId
	 * @param userId
	 * @return
	 */
	Attempt findLatestAttemptForAssessmentByUser(Long assessmentId, Long userId);

	/**
	 * Find the user's latest attempt at an assessment as it relates to a particular assignment.
	 *
	 * @param assessmentId
	 * @param userId
	 * @param workId
	 * @return
	 */
	Attempt findLatestAttemptForAssessmentByUserScopedToWork(Long assessmentId, Long userId, Long workId);

	/**
	 * Is a new attempt allowed for an assessment by the user?
	 * This is conditional on a few things:
	 * <ol>
	 * <li>Has the user attempted the assessment yet?
	 * <li>Does the user have an in-progress attempt for the assessment?
	 * <li>Has the user passed the assessment?
	 * <li>If the user has failed the assessment, are they eligible to take it again?
	 * </ol>
	 *
	 * @param assessmentId
	 * @param userId
	 * @return
	 */
	boolean isAttemptAllowedForAssessmentByUser(Long assessmentId, Long userId);

	/**
	 * Is a new attempt allowed for an assessment by the user as it relates to a particular assignment?
	 * When related to an assignment, the conditions change a bit?
	 * <ol>
	 * <li>Is the user eligible? I.e. are they a (active) resource, buyer, etc.
	 * <li>Has the user attempted the assessment for this assignment yet?
	 * <li>Does the user have an in-progress attempt for the assessment for this assignment?
	 * </ol>
	 *
	 * @param assessmentId
	 * @param userId
	 * @param workId
	 * @return
	 */
	boolean isAttemptAllowedForAssessmentByUserScopedToWork(Long assessmentId, Long userId, Long workId);

	/**
	 * Starts/saves a new attempt for the assessment by the user.
	 *
	 * @param userId
	 * @param assessmentId
	 * @return
	 * @throws AssessmentAttemptLimitExceededException
	 *
	 */
	Attempt saveAttemptForAssessment(Long userId, Long assessmentId) throws AssessmentAttemptLimitExceededException;

	/**
	 * Starts/saves a new attempt for the assessment by the user.
	 *
	 * @param userId
	 * @param assessmentId
	 * @param workId
	 * @param behalfOfId
	 * @return
	 * @throws AssessmentAttemptLimitExceededException
	 *
	 */
	Attempt saveAttemptForAssessmentScopedToWork(Long userId, Long assessmentId, Long workId, Long behalfOfId) throws AssessmentAttemptLimitExceededException;

	/**
	 * Mark the attempt as complete, grading if possible, or marking as grade pending.
	 *
	 * @param attemptId
	 * @return
	 */
	Attempt completeAttemptForAssessment(Long attemptId);

	/**
	 * Marks the latest attempt of an assessment by the user as complete.
	 *
	 * @param userId
	 * @param assessmentId
	 * @return
	 */
	Attempt completeAttemptForAssessment(Long userId, Long assessmentId);

	/**
	 * Marks the latest attempt of an assessment by the user for an assignment as complete.
	 *
	 * @param userId
	 * @param assessmentId
	 * @return
	 */
	Attempt completeAttemptForAssessmentScopedToWork(Long userId, Long assessmentId, Long workId);

	/**
	 * Submit responses for a single assessment item in an assessment attempt.
	 *
	 * @param attemptId
	 * @param itemId
	 * @param responses
	 * @return
	 * @throws AssessmentAttemptTimedOutException
	 *
	 * @throws IOException
	 * @throws AssetTransformationException
	 * @throws HostServiceException
	 */
	List<AttemptResponse> submitResponsesForItem(Long attemptId, Long itemId, AttemptResponseDTO[] responses) throws AssessmentAttemptTimedOutException, HostServiceException, AssetTransformationException, IOException;

	/**
	 * Submit responses for a single assessment item in the latest assessment attempt.
	 *
	 * @param userId
	 * @param assessmentId
	 * @param itemId
	 * @param responses
	 * @return
	 * @throws IOException
	 * @throws AssetTransformationException
	 * @throws HostServiceException
	 * @throws AssessmentAttemptTimedOutException
	 *
	 */
	List<AttemptResponse> submitResponsesForItemInAssessment(Long userId, Long assessmentId, Long itemId, AttemptResponseDTO[] responses) throws AssessmentAttemptTimedOutException, HostServiceException, AssetTransformationException, IOException;

	/**
	 * Submit responses for a single assessment item in the latest assessment attempt attached to an assignment.
	 *
	 * @param userId
	 * @param assessmentId
	 * @param itemId
	 * @param responses
	 * @param workId
	 * @return
	 * @throws IOException
	 * @throws AssetTransformationException
	 * @throws HostServiceException
	 * @throws AssessmentAttemptTimedOutException
	 *
	 */
	List<AttemptResponse> submitResponsesForItemInAssessmentScopedToWork(Long userId, Long assessmentId, Long itemId, AttemptResponseDTO[] responses, Long workId) throws AssessmentAttemptTimedOutException, HostServiceException, AssetTransformationException, IOException;

	/**
	 * Get general statistics for an assessment: # passed, # failed, # invited, avg. score, etc.
	 *
	 * @param assessmentId
	 * @return
	 */
	AssessmentStatistics getAssessmentStatistics(Long assessmentId);

	/**
	 * Generate an exportable report of all attempts and responses for the assessment
	 *
	 * @param assessmentId
	 * @param pagination
	 * @return
	 */
	AttemptReportPagination generateReportForAssessment(Long assessmentId, AttemptReportPagination pagination);

	/**
	 * Find all assets that were submitted as responses to an assessment item.
	 * Provides additional data that includes meta data about the assignment the attempt was in response to.
	 *
	 * @param pagination
	 * @return
	 */
	AttemptResponseAssetReportPagination findAssessmentAttemptResponseAssets(AttemptResponseAssetReportPagination pagination);


	// Grading API

	/**
	 * Grade the responses to an assessment item.
	 *
	 * @param attemptId
	 * @param itemId
	 * @param passed
	 */
	void gradeResponsesForItemInAttempt(Long attemptId, Long itemId, boolean passed);

	/**
	 * Grade a user's assessment attempt. Method to only be called by an authorized user.
	 *
	 * @param userId
	 * @param assessmentId
	 * @return
	 */
	Attempt gradeAttemptForAssessment(Long userId, Long assessmentId);

	/**
	 * Get the current user's authorization context for the requested entity.
	 *
	 * @param assessmentId
	 * @return
	 */
	List<RequestContext> getRequestContext(Long assessmentId);

	/**
	 * Returns all the users in the Assessment's company worker pool and the status of each user in the context of the Assessment.
	 *
	 * @param assessmentId
	 * @param pagination
	 * @return AssessmentUserPagination
	 */
	AssessmentUserPagination findAllAssessmentUsers(Long assessmentId, AssessmentUserPagination pagination);

	/**
	 * Returns the latest attempts for all users who have started or completed the assessment.
	 *
	 * @param assessmentId
	 * @param pagination
	 * @return
	 */
	AssessmentUserPagination findLatestAssessmentUserAttempts(Long assessmentId, AssessmentUserPagination pagination);

	/**
	 * Returns all the users in the Assessment's company worker pool and the status of each user in the context of the Assessment.
	 * e.g. How many users have taken the assessment, how many invitations, etc.
	 *
	 * @param assessmentId
	 * @param pagination
	 * @return AssessmentUserPagination
	 */
	AggregatesDTO countAssessmentUsers(Long assessmentId, AssessmentUserPagination pagination);

	/**
	 * Returns assessments based on several pagination filters
	 *
	 * @param userId
	 * @param pagination
	 * @return
	 */
	ManagedAssessmentPagination findAssessmentsForUser(Long userId, ManagedAssessmentPagination pagination);

	ManagedAssessmentPagination findRecommendedAssessmentsForUser(Long userId);

	int reassignAssessmentsOwnership(Long fromId, Long toId);

	List<AbstractAssessment> findAllTestsByCompanyId(Long companyId);

	void addUsersToTest(Long userId, String userNumber, Long testId, Set<String> resourceUserNumbers);

	List<WorkAssessmentAssociation> findAllWorkAssessmentAssociationByWork(long workId);

	List<Attempt> findLatestAttemptByUserAndWork(long userId, long workId);

	void setAssessmentsForWork(List<AssessmentDTO> assessments, Long workId);

	void addAssessmentToWork(Long assessmentId, Boolean required, Long workId);

	ImmutableList<Map> getProjectedSurveys(String[] fields) throws Exception;
	List<SurveyAssessment> getSurveys() throws Exception;

	boolean isUserAllowedToTakeAssessment(Long assessmentId, Long userId);

	Long getTimeUntilAttemptExpires(final Long assessmentId, final Long userId);

	boolean isAttemptFinished(Long assessmentId, Long userId);
}
