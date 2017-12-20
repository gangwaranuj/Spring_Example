package com.workmarket.domains.work.model.negotiation;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.workmarket.domains.model.LookupEntity;

@Entity(name="spendLimitNegotiationType")
@Table(name="spend_limit_negotiation_type")
public class SpendLimitNegotiationType extends LookupEntity {

	private static final long serialVersionUID = -5402256642017847252L;

	public static final String NEED_MORE_TIME = "time";
	public static final String NEED_MORE_EXPENSES = "expenses";
	public static final String NEED_MORE_TIME_AND_EXPENSES = "timeAndExpenses";
	public static final String BONUS = "bonus";

	public SpendLimitNegotiationType(){
		super();
	}

	public SpendLimitNegotiationType(String code){
		super(code);
	}
}
