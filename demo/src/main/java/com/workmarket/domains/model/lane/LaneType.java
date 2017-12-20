package com.workmarket.domains.model.lane;

public enum LaneType {
	LANE_0("lane0work", "Internal User"), // 0 sentient
	LANE_1("lane1work", "Employee"),      // 1
	LANE_2("lane2work", "Contractor"),    // 2
	LANE_3("lane3work", "Third Party"),   // 3

	//Virtual Lane types
	LANE_4("lane4work", "Everyone Else"),
	LANE_23("lane3work", "Resources");

	private final String column;
	private final String description;

	private LaneType(String column, String description) {
		this.column = column;
		this.description = description;
	}

	public String getColumn() {
		return column;
	}

	public String getDescription() {
		return description;
	}

	public static LaneType valueOf(int idx) {
		return LaneType.values()[idx];
	}

	public boolean isLane0() {
		return this.equals(LANE_0);
	}

	public boolean isLane1() {
		return this.equals(LANE_1);
	}

	public boolean isLane2() {
		return this.equals(LANE_2);
	}

	public boolean isLane3() {
		return this.equals(LANE_3);
	}

	public boolean isLane4() {
		return this.equals(LANE_4);
	}

	public boolean isEmployeeLane() {
		return isLane0() || isLane1();
	}

	public int getValue() {
		switch (this) {
			case LANE_1:
				return 1;
			case LANE_2:
				return 2;
			case LANE_3:
				return 3;
			case LANE_4:
				return 4;
			case LANE_23:
				return 23;
			default:
				return 0;
		}
	}

	public static LaneType findByValue(int value) {
		switch (value) {
			case 0:
				return LANE_0;
			case 1:
				return LANE_1;
			case 2:
				return LANE_2;
			case 3:
				return LANE_3;
			case 4:
				return LANE_4;
			case 23:
				return LANE_23;
			default:
				return null;
		}
	}
}
