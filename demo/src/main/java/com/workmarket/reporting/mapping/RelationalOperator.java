package com.workmarket.reporting.mapping;

public enum RelationalOperator {

	EQUAL_TO("equal_to"),
	NOT_EQUAL_TO("not_equal_to"),
	GREATER_THAN("greater_than"),
	GREATER_THAN_EQUAL_TO("greater_than_equal_to"),
	LESS_THAN("less_than"),
	LESS_THAN_EQUAL_TO("less_than_equal_to"),
	PLEASE_SELECT("please_select");

	/*
	 * Instance variables
	 */
	private final String operator;

	RelationalOperator(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

	/**
	 * @param operator
	 * @param defaultRelationalOperator
	 * @return
	 * @throws Exception
	 */
	public static RelationalOperator getRelationalOperator(String operator, RelationalOperator defaultRelationalOperator)throws Exception{
		if(operator == null)
			return defaultRelationalOperator;
		
		RelationalOperator relationalOperators[] = RelationalOperator.values();
		for(int i = 0; i < relationalOperators.length; i++){//relationalOperators isn't null, no need to check.
			if(relationalOperators[i].getOperator().equals(operator))
				return relationalOperators[i];
		}

		throw new Exception("The operator '" + operator + "' doesn't return an appropriate RelationalOperator enum.");
	}
}