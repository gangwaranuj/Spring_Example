package com.workmarket.api.v2.utils.enterprise.decision;

import com.google.common.collect.Lists;
import com.workmarket.api.v2.model.enterprise.decisionflow.DeciderDTO;
import com.workmarket.api.v2.model.enterprise.decisionflow.DecisionDTO;
import com.workmarket.api.v2.model.enterprise.decisionflow.DecisionFlowDTO;
import com.workmarket.api.v2.model.enterprise.decisionflow.DecisionStepDTO;
import com.workmarket.api.v2.model.enterprise.decisionflow.QuorumType;
import com.workmarket.business.decision.gen.Messages.Decider;
import com.workmarket.business.decision.gen.Messages.DeciderType;
import com.workmarket.business.decision.gen.Messages.DecisionFlowTemplate;
import com.workmarket.business.decision.gen.Messages.DecisionStepTemplate;
import com.workmarket.business.decision.gen.Messages.DecisionStepTemplateNode;
import com.workmarket.business.decision.gen.Messages.DecisionStepTemplateType;
import com.workmarket.business.decision.gen.Messages.UnanimousUserDecisionStepTemplate;

import java.util.List;

public class DecisionFlowConverter {

	public static DecisionFlowDTO asApiResponse(final DecisionFlowTemplate decisionFlowTemplate) {
		final List<DecisionStepDTO> templateNodes = convert(decisionFlowTemplate.getDecisionStepTemplateNodeList());

		return new DecisionFlowDTO.Builder()
				.withUuid(decisionFlowTemplate.getUuid())
				.withName(decisionFlowTemplate.getName())
				.withDescription(decisionFlowTemplate.getDescription())
				.withDecisionSteps(templateNodes)
				.build();
	}

	private static List<DecisionStepDTO> convert(final List<DecisionStepTemplateNode> steps) {
		final List<DecisionStepDTO> decisionStepDTOs = Lists.newArrayList();
		for (final DecisionStepTemplateNode step : steps) {
			decisionStepDTOs.add(DecisionStepConverter.convert(step));
		}
		return decisionStepDTOs;
	}

	public static List<DecisionStepTemplateNode> asDecisionStepTemplateNodes(List<DecisionStepDTO> dtos) {
		List<DecisionStepTemplateNode> decisionSteps = Lists.newArrayList();
		for (DecisionStepDTO dto : dtos) {
			decisionSteps.add(asDecisionStepTemplateNode(dto));
		}
		return decisionSteps;
	}

	private static DecisionStepTemplateNode asDecisionStepTemplateNode(DecisionStepDTO decisionStepDTO) {
		List<Decider> deciders = Lists.newArrayList();
		for (DecisionDTO decisionDTO : decisionStepDTO.getDecisions()) {
			deciders.add(asDeciderFromDTO(decisionDTO.getDecider()));
		}

		DecisionStepTemplateNode.Builder decisionStepTemplateNode = DecisionStepTemplateNode.newBuilder();
		DecisionStepTemplate.Builder decisionStepTemplate = DecisionStepTemplate.newBuilder();

		if (decisionStepDTO.getQuorumType() == QuorumType.UNANIMOUS) {
			UnanimousUserDecisionStepTemplate.Builder  unanimousUserTemplate =
					UnanimousUserDecisionStepTemplate.newBuilder()
							.addAllDecider(deciders);
			decisionStepTemplate
					.setType(DecisionStepTemplateType.UNANIMOUS_USER)
					.setUnanimousUserTemplate(unanimousUserTemplate);
		}

		return decisionStepTemplateNode
				.setSequence(decisionStepDTO.getSequence())
				.setName(decisionStepDTO.getName())
				.setDecisionStepTemplate(decisionStepTemplate)
				.build();
	}

	private static Decider asDeciderFromDTO(final DeciderDTO dto) {
		return Decider.newBuilder()
				.setUuid(dto.getUuid())
				.setDeciderType(DeciderType.INDIVIDUAL_DECIDER)
				.build();
	}
}
