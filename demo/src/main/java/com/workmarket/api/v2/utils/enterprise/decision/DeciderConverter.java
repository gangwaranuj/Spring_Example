package com.workmarket.api.v2.utils.enterprise.decision;

import com.workmarket.api.v2.model.enterprise.decisionflow.DeciderDTO;
import com.workmarket.business.decision.gen.Messages.Decider;

public class DeciderConverter {

	private DeciderConverter() { }

	public static DeciderDTO convert(final Decider decider) {
		return new DeciderDTO.Builder()
				.withUuid(decider.getUuid())
				.build();
	}
}
