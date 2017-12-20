package com.workmarket.domains.model.compliance;

import com.workmarket.domains.compliance.model.ComplianceRuleType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class ComplianceRuleTypeTest {
	@Test
	public void equals_equalsContract() {
		EqualsVerifier
		.forClass(ComplianceRuleType.class)
		.usingGetClass()
		.verify();
	}
}
