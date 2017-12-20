package com.workmarket.api.v2.utils.enterprise.decision;

import com.workmarket.api.v2.model.enterprise.decisionflow.DecisionDTO;
import com.workmarket.business.decision.gen.Messages.Decider;

import java.util.ArrayList;
import java.util.List;

public class DecisionConverter {

	private DecisionConverter() { }

	public static List<DecisionDTO> convert(final List<Decider> deciders) {
		final List<DecisionDTO> decisionDTOs = new ArrayList<>();
		for (final Decider decision : deciders) {
			decisionDTOs.add(convert(decision));
		}
		return decisionDTOs;
	}

	private static DecisionDTO convert(final Decider decider) {
		return new DecisionDTO.Builder()
				.withDecider(DeciderConverter.convert(decider))
				.build();
	}
}
