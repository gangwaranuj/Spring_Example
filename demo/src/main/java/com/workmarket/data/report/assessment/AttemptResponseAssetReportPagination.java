package com.workmarket.data.report.assessment;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.utility.sql.SQLOperator;

public class AttemptResponseAssetReportPagination extends AbstractPagination<AttemptResponseAssetReportRow> implements Pagination<AttemptResponseAssetReportRow> {
	public enum FILTER_KEYS {
		ASSET_ID("asset.id", SQLOperator.EQUALS),
		ASSET_UUID("asset.uuid", SQLOperator.EQUALS),
		ASSESSMENT_ID("aua.assessment_id", SQLOperator.EQUALS),
		ATTEMPT_ID("attempt.id", SQLOperator.EQUALS),
		ITEM_ID("item.id", SQLOperator.EQUALS),
		WORK_ID("work.id", SQLOperator.EQUALS),
		CREATED_ON_FROM("asset.created_on", SQLOperator.GREATER_THAN_OR_EQUAL),
		CREATED_ON_THROUGH("asset.created_on", SQLOperator.LESS_THAN_OR_EQUAL),
		WORK_PROJECT_ID("project.id", SQLOperator.EQUALS),
		WORK_CLIENT_ID("client.id", SQLOperator.EQUALS),
		RESOURCE_USER_NUMBER("user.user_number", SQLOperator.EQUALS);
		
		private String column;
		private String operator;

		FILTER_KEYS(String column, String operator) {
			this.column = column;
			this.operator = operator;
		}

		public String getColumn() {
			return column;
		}
		
		public String getOperator() {
			return operator;
		}
	}
	
	public enum SORTS {
		CREATED_ON("asset.created_on"),
		WORK_NUMBER("work.work_number"),
		CREATOR_LAST_NAME("user.last_name");
		
		private String column;

		SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}
	
	private Long totalFileByteSize = 0L;

	public Long getTotalFileByteSize() {
		return totalFileByteSize;
	}
	public void setTotalFileByteSize(Long totalFileByteSize) {
		this.totalFileByteSize = totalFileByteSize;
	}
}