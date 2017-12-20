package com.workmarket.utility.integration.autotask;

public class Condition {

	public enum Operations {
		EQUALS("Equals"),
		NOT_EQUAL("NotEqual"),
		GREATER_THAN("GreaterThan"),
		LESS_THAN("LessThan"),
		GREATER_THAN_OR_EQUALS("GreaterThanOrEquals"),
		LESS_THAN_OR_EQUALS("LessThanOrEquals"),
		BEGINS_WITH("BeginsWith"),
		ENDS_WITH("EndsWith"),
		CONTAINS("Contains"),
		IS_NOT_NULL("IsNotNull"),
		IS_NULL("IsNull"),
		IS_THIS_DAY("IsThisDay"),
		LIKE("Like"),
		NOT_LIKE("NotLike"),
		SOUNDS_LIKE("SoundsLike");

		private String name;

		private Operations(String s) {
			name = s;
		}

		public boolean equalsName(String otherName) {
			return (otherName != null) && name.equals(otherName);
		}

		public String toString() {
			return name;
		}

	}

	private String field;
	private Operations operation;
	private String value;

	public Condition(String field, Operations operation, String value) {
		this.field = field;
		this.operation = operation;
		this.value = value;
	}

	public String getField() {
		return field;
	}

	public Operations getOperation() {
		return operation;
	}

	public String getValue() {
		return value;
	}

	public Condition setField(String field) {
		this.field = field;
		return this;
	}

	public Condition setOperation(Operations operation) {
		this.operation = operation;
		return this;
	}

	public Condition setValue(String value) {
		this.value = value;
		return this;
	}

	public String toString() {
		return String.format("<field>%s\n<expression op=\"%s\">%s</expression>\n</field>\n", field, operation, value);
	}
}
