package com.workmarket.domains.compliance.service;

import com.google.common.collect.Sets;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.compliance.model.AbstractComplianceRule;
import com.workmarket.domains.compliance.model.BaseComplianceCriterion;
import com.workmarket.domains.compliance.model.Compliance;
import com.workmarket.domains.compliance.model.ComplianceRuleSet;
import com.workmarket.domains.compliance.model.WorkBundleComplianceCriterion;
import com.workmarket.domains.compliance.model.WorkComplianceCriterion;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Set;

@Service
public class ComplianceServiceImpl implements ComplianceService {
	private static final Log logger = LogFactory.getLog(ComplianceServiceImpl.class);

	@Autowired private CompliantVisitor visitor;
	@Autowired private ComplianceRuleSetsService service;
	@Autowired private WorkService workService;
	@Autowired private UserService userService;
	@Autowired private FeatureEvaluator featureEvaluator;

	@Override
	public Compliance getComplianceFor(String workerNumber, String workNumber) {
		Assert.notNull(workerNumber);
		Assert.notNull(workNumber);

		AbstractWork work = workService.findWorkByWorkNumber(workNumber);
		User user = userService.findUserByUserNumber(workerNumber);

		return getComplianceFor(user, work);
	}

	@Override
	public Compliance getComplianceFor(Long userId, Long workId) {
		Assert.notNull(userId);
		Assert.notNull(workId);

		AbstractWork work = workService.findWork(workId);
		User user = userService.findUserById(userId);

		return getComplianceFor(user, work);
	}

	@Override
	public Compliance getComplianceFor(Long workId, DateRange dateRange) {
		Assert.notNull(workId);
		Assert.notNull(dateRange);

		AbstractWork work = workService.findWork(workId);
		User user = userService.findUserById(workService.findActiveWorkerId(workId));

		return getComplianceFor(user, work, dateRange);
	}

	@Override
	public Compliance getComplianceFor(User user, AbstractWork work) {
		Assert.notNull(user);
		Assert.notNull(work);

		return getComplianceFor(user, work, work.getSchedule());
	}

	@Override
	public Compliance getComplianceFor(User user, AbstractWork work, DateRange schedule) {
		Assert.notNull(user);
		Assert.notNull(work);
		Assert.notNull(schedule);

		Set<BaseComplianceCriterion> complianceCriteria = Sets.newTreeSet();
		boolean compliant = true;

		Collection<ComplianceRuleSet> complianceRuleSets = service.findAll(work.getCompany().getId());

		for (ComplianceRuleSet complianceRuleSet : complianceRuleSets) {
			for (AbstractComplianceRule complianceRule : complianceRuleSet.getComplianceRules()) {
				BaseComplianceCriterion complianceCriterion;
				if (work.isWorkBundle()) {
					complianceCriterion = new WorkBundleComplianceCriterion(user, (WorkBundle)work, schedule);
					complianceRule.accept(visitor, (WorkBundleComplianceCriterion)complianceCriterion);
				} else {
					complianceCriterion = new WorkComplianceCriterion(user, (Work)work, schedule);
					complianceRule.accept(visitor, (WorkComplianceCriterion)complianceCriterion);
				}

				// when this becomes false, it stays false
				if (compliant) {
					compliant = complianceCriterion.isMet();
				}

				complianceCriteria.add(complianceCriterion);
			}

		}
		return new Compliance(complianceCriteria, compliant);
	}
}
