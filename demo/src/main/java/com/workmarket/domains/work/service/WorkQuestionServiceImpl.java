package com.workmarket.domains.work.service;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.changelog.work.WorkQuestionAnsweredChangeLog;
import com.workmarket.domains.model.changelog.work.WorkQuestionAskedChangeLog;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.dao.WorkQuestionAnswerPairDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.utility.CollectionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.List;

/**
 * Created by nick on 2013-02-25 8:31 AM
 */
@Service
public class WorkQuestionServiceImpl implements WorkQuestionService {

	@Autowired private AuthenticationService authenticationService;
	@Autowired private WorkAuditService workAuditService;
	@Autowired private WorkActionRequestFactory workActionRequestFactory;
	@Autowired private WorkService workService;
	@Autowired private UserService userService;

	@Autowired private UserNotificationService userNotificationService;
	@Autowired private WorkChangeLogService workChangeLogService;
	@Autowired private WorkQuestionAnswerPairDAO workQuestionAnswerPairDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private UserDAO userDAO;

	@Override
	public WorkQuestionAnswerPair saveQuestion(Long workId, Long userId, String question) {
		return saveQuestion(workId, userId, question, null);
	}

	@Override
	public WorkQuestionAnswerPair saveQuestion(Long workId, Long userId, String question, Long onBehalfOfUserId) {
		Assert.notNull(workId);
		Assert.notNull(userId);
		Assert.notNull(question);

		Work work = workDAO.get(workId);
		User user = userDAO.get(userId);

		User currentUser = authenticationService.getCurrentUser();
		if (currentUser == null) {
			currentUser = work.getBuyer();
		}

		WorkQuestionAnswerPair qa = new WorkQuestionAnswerPair();
		qa.setWorkId(workId);
		qa.setQuestion(question);
		qa.setQuestionerId(user.getId());
		qa.setCreatedOn(Calendar.getInstance());

		workQuestionAnswerPairDAO.saveOrUpdate(qa);
		workChangeLogService.saveWorkChangeLog(new WorkQuestionAskedChangeLog(workId, userId, onBehalfOfUserId, authenticationService.getMasqueradeUserId()));
		userNotificationService.onQuestionCreated(qa, workId);
		workAuditService.auditAndReindexWork(workActionRequestFactory.create(work, currentUser.getId(), onBehalfOfUserId, authenticationService.getMasqueradeUserId(), WorkAuditType.QUESTION));

		return qa;
	}

	@Override
	public WorkQuestionAnswerPair saveAnswerToQuestion(Long questionAnswerPairId, Long userId, String answer, Long workId) {
		return saveAnswerToQuestion(questionAnswerPairId, userId, null, answer, workId);
	}

	@Override
	public WorkQuestionAnswerPair saveAnswerToQuestion(Long questionAnswerPairId, Long userId, Long onBehalfOfUserId, String answer, Long workId) {
		Assert.notNull(questionAnswerPairId);
		Assert.notNull(userId);
		Assert.notNull(answer);
		Assert.notNull(workId);

		WorkQuestionAnswerPair qa = workQuestionAnswerPairDAO.get(questionAnswerPairId);
		qa.setAnswer(answer);
		qa.setAnswererId(userId);
		qa.setAnsweredOn(Calendar.getInstance());

		workChangeLogService.saveWorkChangeLog(new WorkQuestionAnsweredChangeLog(workId, authenticationService.getCurrentUser().getId(),
				authenticationService.getMasqueradeUserId(), onBehalfOfUserId));

		userNotificationService.onQuestionAnswered(qa, workId);
		workAuditService.auditAndReindexWork(workActionRequestFactory.create(workService.findWork(workId), authenticationService.getCurrentUser().getId(), onBehalfOfUserId, authenticationService.getMasqueradeUserId(), WorkAuditType.ANSWER_QUESTION));

		return qa;
	}

	@Override
	public List<WorkQuestionAnswerPair> findQuestionAnswerPairs(Long workId) {
		Assert.notNull(workId);
		return workQuestionAnswerPairDAO.findByWork(workId);
	}

	@Override
	public List<WorkQuestionAnswerPair> findQuestionAnswerPairs(Long workId, Long userId, Long companyId) {
		Assert.notNull(workId);
		Assert.notNull(userId);

		final List<WorkContext> context = workService.getWorkContext(workId, userId);
		final List<WorkQuestionAnswerPair> questions = findQuestionAnswerPairs(workId);
		final List<WorkQuestionAnswerPair> results = Lists.newArrayListWithExpectedSize(questions.size());

		boolean isMyQuestion, isMyCompany;

		for (WorkQuestionAnswerPair question : questions) {
			// If the user is a worker, they can only see their questions.
			isMyQuestion = question.getQuestionerId().equals(userId) || userService.getUser(question.getQuestionerId()).getCompany().getId().equals(companyId);
			// If the user is part of the company, they can see all questions.
			isMyCompany = CollectionUtilities.containsAny(context, WorkContext.COMPANY_OWNED, WorkContext.OWNER);

			if (isMyQuestion || isMyCompany) {
				results.add(question);
			}
		}

		return results;
	}

	@Override
	public Optional<WorkQuestionAnswerPair> findQuestionAnswerPairById(Long id) {
		Assert.notNull(id);
		return Optional.fromNullable(workQuestionAnswerPairDAO.get(id));
	}

	@Override
	public Long findWorkIdByQuestionId(Long id) {
		Assert.notNull(id);
		return workQuestionAnswerPairDAO.findWorkIdByQuestionId(id);
	}

}
