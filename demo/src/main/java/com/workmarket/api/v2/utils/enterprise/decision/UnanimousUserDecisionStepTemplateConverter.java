package com.workmarket.api.v2.utils.enterprise.decision;

import com.workmarket.api.v2.model.enterprise.decisionflow.DecisionDTO;
import com.workmarket.api.v2.model.enterprise.decisionflow.DecisionStepDTO;
import com.workmarket.api.v2.model.enterprise.decisionflow.QuorumType;
import com.workmarket.business.decision.gen.Messages.Decider;
import com.workmarket.business.decision.gen.Messages.DecisionStepTemplateNode;

import java.util.List;

public class UnanimousUserDecisionStepTemplateConverter {

	private UnanimousUserDecisionStepTemplateConverter() { }

	public static DecisionStepDTO convert(final DecisionStepTemplateNode decisionStepTemplateNode) {
		final List<Decider> deciders =
				decisionStepTemplateNode.getDecisionStepTemplate().getUnanimousUserTemplate().getDeciderList();
		final List<DecisionDTO> decisions = DecisionConverter.convert(deciders);
		return new DecisionStepDTO.Builder()
				.withName(decisionStepTemplateNode.getName())
				.withQuorumType(QuorumType.UNANIMOUS)
				.withDecisions(decisions)
				.build();
	}
}
