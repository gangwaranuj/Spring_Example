package com.workmarket.thrift.work;

import com.workmarket.thrift.EnumValue;

public enum LogEntryType implements EnumValue {
	WORK(0),
	WORK_CREATED(1),
	WORK_UPDATED(2),
	WORK_QUESTION_ASKED(3),
	WORK_QUESTION_ANSWERED(4),
	WORK_STATUS_CHANGE(5),
	WORK_RESOURCE_STATUS_CHANGE(6),
	WORK_NOTE_CREATED(7),
	WORK_PROPERTY(8),
	WORK_NEGOTIATION_REQUESTED(9),
	WORK_NEGOTIATION_STATUS_CHANGE(10),
	WORK_RESCHEDULE_REQUESTED(11),
	WORK_RESCHEDULE_STATUS_CHANGE(12),
	WORK_SUB_STATUS_CHANGE(13),
	WORK_NEGOTIATION_EXPIRED(14),
	WORK_RESCHEDULE_AUTO_APPROVED(15),
	WORK_CLOSEOUT_ATTACHMENT_ADD(16);

	private final int value;

	private LogEntryType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static LogEntryType findByValue(int value) {
		switch (value) {
			case 0:
				return WORK;
			case 1:
				return WORK_CREATED;
			case 2:
				return WORK_UPDATED;
			case 3:
				return WORK_QUESTION_ASKED;
			case 4:
				return WORK_QUESTION_ANSWERED;
			case 5:
				return WORK_STATUS_CHANGE;
			case 6:
				return WORK_RESOURCE_STATUS_CHANGE;
			case 7:
				return WORK_NOTE_CREATED;
			case 8:
				return WORK_PROPERTY;
			case 9:
				return WORK_NEGOTIATION_REQUESTED;
			case 10:
				return WORK_NEGOTIATION_STATUS_CHANGE;
			case 11:
				return WORK_RESCHEDULE_REQUESTED;
			case 12:
				return WORK_RESCHEDULE_STATUS_CHANGE;
			case 13:
				return WORK_SUB_STATUS_CHANGE;
			case 14:
				return WORK_NEGOTIATION_EXPIRED;
			case 15:
				return WORK_RESCHEDULE_AUTO_APPROVED;
			case 16:
				return WORK_CLOSEOUT_ATTACHMENT_ADD;
			default:
				return null;
		}
	}
}
