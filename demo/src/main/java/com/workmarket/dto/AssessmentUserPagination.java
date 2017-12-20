package com.workmarket.dto;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.lane.LaneType;

public class AssessmentUserPagination extends AbstractPagination<AssessmentUser> implements Pagination<AssessmentUser> {

	public static final String PASSED = "Passed";
	public static final String FAILED = "Failed";
	public static final String INVITED = "Invited";
	public static final String ACCEPTED = "Accepted";
	public static final String NOT_INVITED = "Not Invited";
	public static final String COMPLETED = "Completed";
	public static final String GRADED = "graded";
	public static final String ALL = "All";
	public static final String LANE0 = LaneType.LANE_0.toString();
	public static final String LANE1 = LaneType.LANE_1.toString();
	public static final String LANE2 = LaneType.LANE_2.toString();
	public static final String LANE3 = LaneType.LANE_3.toString();

	public enum FILTER_KEYS {
		LANE_TYPE_ID("lane.lane_type_id"),
		STATUS("invitationStatus"),
		KEYWORD(""),
		USER_NUMBER("user.user_number"),
		ATTEMPT_STATUS("assessment_user_association.attempt_status_type_code");

		private String column;

		FILTER_KEYS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public enum SORTS {
		USER_ID("user.id"),
		USER_LAST_NAME("user.last_name"),
		LANE_TYPE("laneType"),
		STATUS("invitationStatus"),
		DATE_ADDED("dateAdded"),
		COMPANY("company.effective_name"),
		LOCATION("state.short_name"),
		ATTEMPT_STATUS("association.attempt_status_type_code"),
		SCORE("association.score"),
		DATE_COMPLETED("association.completed_on");

		private String column;

		SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public AssessmentUserPagination() {}
	public AssessmentUserPagination(boolean returnAllRows) {
		super(returnAllRows);
	}
}
