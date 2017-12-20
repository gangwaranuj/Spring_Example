import * as types from '../constants/actionTypes';

export const toggleScheduleRange = (value) => {
	return {
		type: types.TOGGLE_SCHEDULE_RANGE,
		value
	};
};

export const updateScheduleFrom = (value) => {
	return {
		type: types.UPDATE_SCHEDULE_FROM,
		value
	};
};

export const updateScheduleThrough = (value) => {
	return {
		type: types.UPDATE_SCHEDULE_THROUGH,
		value
	};
};

export const toggleConfirmationRequired = () => {
	return {
		type: types.TOGGLE_CONFIRMATION_REQUIRED
	};
};

export const updateConfirmationLeadTime = (value) => {
	return {
		type: types.UPDATE_CONFIRMATION_LEAD_TIME,
		value
	};
};

export const toggleCheckinRequired = () => {
	return {
		type: types.TOGGLE_CHECKIN_REQUIRED
	};
};

export const toggleCheckinCallRequired = () => {
	return {
		type: types.TOGGLE_CHECKIN_CALL_REQUIRED
	};
};

export const updateCheckinContactName = (value) => {
	return {
		type: types.UPDATE_CHECKIN_CONTACT_NAME,
		value
	};
};

export const updateCheckinContactPhone = (value) => {
	return {
		type: types.UPDATE_CHECKIN_CONTACT_PHONE,
		value
	};
};

export const toggleCheckoutNoteDisplayed = () => {
	return {
		type: types.TOGGLE_CHECKOUT_NOTE_DISPLAYED
	};
};

export const updateCheckoutNote = (value) => {
	return {
		type: types.UPDATE_CHECKOUT_NOTE,
		value
	};
};
