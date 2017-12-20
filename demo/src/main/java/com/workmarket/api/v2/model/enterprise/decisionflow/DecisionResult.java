package com.workmarket.api.v2.model.enterprise.decisionflow;

import io.swagger.annotations.ApiModel;

@ApiModel("DecisionResult")
public enum DecisionResult {
	OPEN,
	TRUE,
	FALSE,
	CANCELLED,
	SKIPPED
}
