package com.workmarket.utility.integration.autotask;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by nick on 2012-12-26 3:16 PM
 */
public class ConditionGroup {



	public enum JoinOperator {OR, AND;}
	private List<Condition> conditions = Lists.newArrayList();

	private JoinOperator joinOperator = JoinOperator.AND;
	public ConditionGroup() {
	}

	public ConditionGroup(JoinOperator operator) {
		joinOperator = operator;
	}

	public ConditionGroup(Condition condition) {
		conditions.add(condition);
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public ConditionGroup setConditions(List<Condition> conditions) {
		this.conditions = conditions;
		return this;
	}

	public boolean hasMultipleConditions() {
		return conditions.size() > 1;
	}

	public ConditionGroup addCondition(Condition condition) {
		conditions.add(condition);
		return this;
	}

	public JoinOperator getJoinOperator() {
		return joinOperator;
	}

	public ConditionGroup setJoinOperator(JoinOperator joinOperator) {
		this.joinOperator = joinOperator;
		return this;
	}

	public String conditionListToString(List<Condition> conditions, boolean showOperator) {
		boolean nested = conditions.size() > 1 && showOperator;
		StringBuilder builder = new StringBuilder();

		if (nested) {
			builder.append("<condition");
			if (JoinOperator.OR.equals(getJoinOperator()))
				builder.append(" operator=\"OR\"");
			builder.append(">");
		}
		for (Condition c : conditions)
			builder.append(c.toString());
		if (nested) builder.append("</condition>");

		return builder.toString();
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		boolean flat = conditions.size() == 1;
		int i = 0;
		for (Condition c : conditions) {

			if (!flat) {
				builder.append("<condition");
				if (JoinOperator.OR.equals(getJoinOperator()) && (i > 0))  // omit operator on first condition
					builder.append(" operator=\"OR\"");
				builder.append(">\n");
			}
			builder.append(c.toString());
			if (!flat)
				builder.append("</condition>\n");
			i++;
		}

		return builder.toString();
	}
}
