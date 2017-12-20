package com.workmarket.service.decisionflow;

import com.google.common.base.Optional;
import com.workmarket.business.decision.gen.Messages.DecisionFlowTemplateResponse;
import com.workmarket.business.decision.gen.Messages.CreateDecisionFlowTemplateReq;
import com.workmarket.business.decision.gen.Messages.CreateDecisionFlowTemplateResp;
import com.workmarket.business.decision.gen.Messages.Decision;
import com.workmarket.business.decision.gen.Messages.GetDoableDecisionsRequest;
import com.workmarket.business.decision.gen.Messages.QueryDecisionsRequest;
import com.workmarket.business.decision.gen.Messages.StartDecisionFlowResponse;
import com.workmarket.business.decision.gen.Messages.Status;
import com.workmarket.core.flow.gen.Messages.FlowResp;
import com.workmarket.domains.model.Company;

import java.util.List;

public interface DecisionFlowService {

	DecisionFlowTemplateResponse getDecisionFlowTemplate(String uuid);

	CreateDecisionFlowTemplateResp createDecisionFlowTemplate(CreateDecisionFlowTemplateReq request, Company company);

	CreateDecisionFlowTemplateResp updateDecisionFlow(CreateDecisionFlowTemplateReq request, Company company, String uuid);

	List<String> getDecisionFlowTemplateUuids(long companyId);

	Optional<String> getDecisionFlowUuid(long workId);

	List<Decision> getDoableDecisions(final GetDoableDecisionsRequest doableDecisionsRequest);

	List<Decision> queryDecisions(final QueryDecisionsRequest queryDecisionsRequest);

	Status approve(final String decisionUuid, final String deciderUuid);

	StartDecisionFlowResponse start(final String templateUuid);

	Status activate(final String uuid);

	Status deactivate(String uuid);
}
