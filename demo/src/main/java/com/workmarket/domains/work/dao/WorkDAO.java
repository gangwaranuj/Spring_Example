package com.workmarket.domains.work.dao;

import com.google.common.base.Optional;
import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.EntityIdPagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkDue;
import com.workmarket.domains.work.model.WorkPagination;
import com.workmarket.domains.work.model.WorkWorkResourceAccountRegister;
import com.workmarket.service.business.dto.BuyerIdentityDTO;
import com.workmarket.service.exception.account.DuplicateWorkNumberException;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WorkDAO extends PaginatableDAOInterface<Work> {

	WorkPagination findByWorkResource(Long userId, WorkPagination pagination) ;

	WorkPagination findWorkByBuyerAndWorkResource(Long buyerId, Long resourceUserId, WorkPagination pagination) ;

	List<Work> findAllWorkByProject(Long projectId);

	List<Work> findAllWorkByProjectByStatus(Long projectId, String... status);

	Work findWorkByWorkNumber(String workNumber);

	/**
	 * Returns true if the work has the specified status
	 *
	 * @param workNumber
	 *            The work number
	 * @param status
	 *            The work status
	 *
	 * @return true if the work has the specified status
	 *
	 */
	boolean isWorkStatusForWorkByWorkNumber(String workNumber, String status);

	List<Work> findWorkByWorkNumber(Collection<String> workNumbers);

	Integer countAllActiveWork();

	Work findWorkById(Long workId);

	List<Work> findWorksByIds(List<Long> workIds);

	boolean doesWorkerHaveWorkWithCompany(Long companyId, Long contractorUserId, List<String> statuses);

	/**
	 * Finds all work within a company whose status are not in statusCodes and whose sub-statuses include subStatusId
	 *
	 * @param companyId
	 *            The company that owns the works
	 * @param subStatusId
	 *            The sub-status you wish to filter in
	 * @param statusCodes
	 *            The statuses you wish to filter out
	 * @return A list of all work whose status codes are not in statusCodes and whose sub-statuses include subStatusId
	 *
	 */
	List<Work> findAllWorkWhereWorkStatusTypeNotInAndWorkSubStatusTypeIn(long companyId, long subStatusId, String[] statusCodes);

	/**
	 * Finds all work within a company whose templates are not in templateIds and whose sub-statuses include subStatusId
	 *
	 * @param companyId
	 *            The company that owns the works
	 * @param subStatusId
	 *            The sub-status you wish to filter in
	 * @param templateIds
	 *            The templates you wish to filter out
	 * @return A list of all work whose templates are not in templateIds and whose sub-statuses include subStatusId
	 *
	 */
	List<Work> findAllWorkWhereTemplatesNotInAndWorkSubStatusTypeIn(long companyId, long subStatusId, Long[] templateIds);

	WorkPagination findAllWorkPendingRatingByResource(final Long userId, WorkPagination pagination) ;

	Set<WorkDue> findAllDueAssignmentsByDueDate(Calendar dueDateFrom, Calendar dueDateThrough);

	Set<WorkDue> findAllAssignmentsPastDue(Calendar dueDate);

	int countWorkByCompanyUserRangeAndStatus(Long companyId, Long userId, List<Long> excludeIds, Calendar fromDate, Calendar toDate, List<String> statuses);

	int countWorkByCompanyByStatus(Long companyId, List<String> statuses);

	Integer countAllAssignmentsPaymentPendingByCompany(Long companyId);

	Integer countAllDueWorkByCompany(Calendar dueDateFrom, Calendar dueDateThrough, Long companyId);

	Integer countAllDueWorkByCompany(Long companyId);

	List<Integer> getAutoPayWorkIds(Date dueOn, List<String> workStatusTypes);

	Long getBuyerIdByWorkNumber(String workNumber) throws DuplicateWorkNumberException;

	List<BuyerIdentityDTO> findBuyerIdentitiesByWorkIds(Collection<Long> workIds);

	Long getBuyerIdByWorkId(Long workId) throws DuplicateWorkNumberException;

	Long findBuyerCompanyIdByWorkId(long workId);

	List<Long> findAllWorkIdsByUUIDs(final List<String> workUUIDs);

	List<Long> findWorkIdsByInvoiceId(Long... invoiceIds);

	List<Long> findOpenWorkIdsBetweenUserAndCompany(Long userId, Long companyId);

	List<WorkWorkResourceAccountRegister> findWorkAndWorkResourceForPayment(List<Long> assignmentIds);

	List<Integer> findAssignmentsMissingResourceNoShow();

	List<String> findAssignmentsWithDeliverablesDue();

	List<Work> findAssignmentsRequiringDeliverableDueReminder();

	boolean isWorkPendingFulfillment(Long workId);

	boolean doesClientCompanyHaveActiveAssignments(long clientCompanyId);

	Integer getMaxAssignmentId();

	List<Long> findWorkIdsForBuyer(Long buyerId, String ... workStatusType);

	int updateWorkBuyerUserId(final Long newBuyerUserId, final List<Long> workIds, final List<String> workStatusCodes);

	boolean hasWorkPendingRatingByResource(Long userId);

	public Optional<User> findSupportContactUserByWorkId(Long workId);

	Map<Long, Calendar> findLastModifiedDate(int limit);

	EntityIdPagination findAllWorkIdsByCompanyId(long companyId, EntityIdPagination pagination);

	EntityIdPagination findAllWorkIdsByLastModifiedDate(Calendar lastModifiedFrom, EntityIdPagination pagination);

	Map<String, Object> getAssignmentDataOne(final Map<String, Object> params);
}
