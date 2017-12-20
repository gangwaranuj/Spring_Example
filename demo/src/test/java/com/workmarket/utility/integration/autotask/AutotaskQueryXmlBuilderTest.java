package com.workmarket.utility.integration.autotask;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.Assert;

/**
 * Created by nick on 2012-12-24 7:54 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class AutotaskQueryXmlBuilderTest {

	private static final String response1 =
			"<queryxml>\n" +
					"<entity>contact</entity>\n" +
					"<query>\n" +
					"<field>firstname\n" +
					"<expression op=\"Equals\">Joe</expression>\n" +
					"</field>\n" +
					"</query>\n" +
					"</queryxml>";

	private static final String response2 =
			"<queryxml>\n" +
					"<entity>contact</entity>\n" +
					"<query>\n" +
					"<field>firstname\n" +
					"<expression op=\"Equals\">Joe</expression>\n" +
					"</field>\n" +
					"<field>lastname\n" +
					"<expression op=\"Equals\">Smith</expression>\n" +
					"</field>\n" +
					"</query>\n" +
					"</queryxml>";

	private static final String response3 =
			"<queryxml>\n" +
					"<entity>contact</entity>\n" +
					"<query>\n" +
					"<condition>\n" +
					"<field>firstname\n" +
					"<expression op=\"Equals\">Joe</expression>\n" +
					"</field>\n" +
					"</condition>\n" +
					"<condition operator=\"OR\">\n" +
					"<field>lastname\n" +
					"<expression op=\"Equals\">Smith</expression>\n" +
					"</field>\n" +
					"</condition>\n" +
					"</query>\n" +
					"</queryxml>";

	private static final String response4 =
			"<queryxml>\n" +
				"<entity>contact</entity>\n" +
				"<query>\n" +
					"<field>firstname\n" +
						"<expression op=\"Equals\">Joe</expression>\n" +
					"</field>\n" +
					"<condition operator=\"OR\">\n" +
						"<condition>\n" +
							"<field>firstname\n" +
								"<expression op=\"Equals\">Larry</expression>\n" +
							"</field>\n" +
							"<field>lastname\n" +
								"<expression op=\"Equals\">Brown</expression>\n" +
							"</field>\n" +
						"</condition>\n" +
						"<condition operator=\"OR\">\n" +
							"<field>firstname\n" +
								"<expression op=\"Equals\">Mary</expression>\n" +
							"</field>\n" +
							"<field>lastname\n" +
								"<expression op=\"Equals\">Smith</expression>\n" +
							"</field>\n" +
						"</condition>\n" +
					"</condition>\n" +
					"<field>city\n" +
						"<expression op=\"notequal\">Albany</expression>\n" +
					"</field>\n" +
				"</query>\n" +
			"</queryxml>";

	@Test
	public void testBuild() throws Exception {
		// test cases from the AutoTask API doc
		Assert.assertEquals(response1, new AutotaskQueryXmlBuilder("contact")
				.addCondition("firstname", Condition.Operations.EQUALS, "Joe")
				.build());

		Assert.assertEquals(response2, new AutotaskQueryXmlBuilder("contact")
				.addCondition("firstname",Condition.Operations.EQUALS, "Joe")
				.addCondition("lastname", Condition.Operations.EQUALS, "Smith")
				.build());

		Assert.assertEquals(response3, new AutotaskQueryXmlBuilder("contact")
				.addConditionGroup(ConditionGroup.JoinOperator.OR,
						new Condition("firstname", Condition.Operations.EQUALS, "Joe"),
						new Condition("lastname", Condition.Operations.EQUALS, "Smith"))
				.build());

		/* TODO: this doesn't quite work yet
		Assert.assertEquals(response4, new AutotaskQueryXmlBuilder("contact")
				.addCondition("firstname", Condition.Operations.EQUALS, "Joe")
				.addConditionGroup(ConditionGroup.JoinOperator.OR,
						new Condition("firstname", Condition.Operations.EQUALS, "Larry"),
						new Condition("lastname", Condition.Operations.EQUALS, "Brown"))
				.addConditionGroup(ConditionGroup.JoinOperator.OR,
						new Condition("firstname", Condition.Operations.EQUALS, "Mary"),
						new Condition("lastname", Condition.Operations.EQUALS, "Smith"))
				.addCondition("city", Condition.Operations.NOT_EQUAL "Albany")
				.build());
				*/
	}
}
