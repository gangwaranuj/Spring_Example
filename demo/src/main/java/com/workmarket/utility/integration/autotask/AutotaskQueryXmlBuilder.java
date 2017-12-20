package com.workmarket.utility.integration.autotask;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nick on 2012-12-24 5:53 PM
 */
public class AutotaskQueryXmlBuilder {

	public static final String ENTITY_TICKET = "Ticket";

	private String entity;
	private List<ConditionGroup> conditionGroups = Lists.newArrayList();

	public AutotaskQueryXmlBuilder() {
	}

	public AutotaskQueryXmlBuilder(String entity) {
		this.entity = entity;
	}

	public AutotaskQueryXmlBuilder addCondition(String type, Condition.Operations operation, String value) {
		conditionGroups.add(new ConditionGroup(new Condition(type, operation, value)));
		return this;
	}

	public AutotaskQueryXmlBuilder addCondition(Condition condition) {
		conditionGroups.add(new ConditionGroup(condition));
		return this;
	}

	public AutotaskQueryXmlBuilder addConditionGroup(ConditionGroup cg) {
		conditionGroups.add(cg);
		return this;
	}

	public AutotaskQueryXmlBuilder addConditionGroup(ConditionGroup.JoinOperator op, List<Condition> conditions) {
		conditionGroups.add(new ConditionGroup()
				.setConditions(conditions)
				.setJoinOperator(op));
		return this;
	}

	public AutotaskQueryXmlBuilder addConditionGroup(ConditionGroup.JoinOperator op, Condition cond1, Condition... conds) {
		ArrayList<Condition> allConditions = Lists.newArrayList(cond1);
		allConditions.addAll(Arrays.asList(conds));
		conditionGroups.add(new ConditionGroup()
				.setJoinOperator(op)
				.setConditions(allConditions));
		return this;
	}

	public String build() {
		StringBuilder builder = new StringBuilder()
				.append("<queryxml>\n")
				.append(String.format("<entity>%s</entity>\n", entity))
				.append("<query>\n");

		int i = 0;

		for (ConditionGroup cg : conditionGroups) {

			for (Condition c : cg.getConditions()) {
				if (cg.hasMultipleConditions()) {
					builder.append("<condition");
					if (ConditionGroup.JoinOperator.OR.equals(cg.getJoinOperator()) && (i > 0))  // omit operator on first condition
						builder.append(" operator=\"OR\"");
					builder.append(">\n");
				}
				builder.append(c.toString());
				if (cg.hasMultipleConditions())
					builder.append("</condition>\n");
				i++;
			}
		}

		builder.append("</query>\n</queryxml>");
		return builder.toString();
	}
}

