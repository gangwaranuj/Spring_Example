import moment from 'moment';
import {
	formatDailyString,
	formatWeekString,
	formatMonthlyString
} from './visuals';

export const convertTypeForMoment = (frequency) => {
	switch (frequency) {
	case 'Daily':
		return 'day';
	case 'Weekly':
		return 'week';
	case 'Monthly':
		return 'month';
	default:
		return 'Daily';
	}
};

export const numOfAssignments = (state, desiredEndDate) => {
	if (desiredEndDate) {
		return Math.floor(desiredEndDate.diff(state.get('startDate'), convertTypeForMoment(state.get('frequency'))) / state.get('frequencyModifier'));
	}
	return Math.floor(state.get('endDate').diff(state.get('startDate'), convertTypeForMoment(state.get('frequency'))) / state.get('frequencyModifier'));
};

export const aboveMin = (state, desiredEndDate) => numOfAssignments(state, desiredEndDate) >= state.get('minRepeat');

export const belowMax = (state, desiredEndDate) => {
	if (desiredEndDate) {
		return desiredEndDate.diff(state.get('startDate'), 'day') < state.get('maxRepeat');
	}
	return state.get('endDate').diff(state.get('startDate'), 'day') < state.get('maxRepeat');
};

export const generateRepetitions = (state, endDate) => {
	return String(numOfAssignments(state, endDate));
};

export const generateEndDate = (state, desiredEndDate) => {
	const clonedStartDate = moment(state.get('startDate'));
	if (aboveMin(state, desiredEndDate)) {
		if (belowMax(state, desiredEndDate)) {
			return desiredEndDate;
		}
		return clonedStartDate.add(1, 'year');
	}
	return clonedStartDate.add((state.get('minRepeat') * state.get('frequencyModifier')), convertTypeForMoment(state.get('frequency')));
};

export const dynamicallyRestrictPopulate = (sD, mR, f, fMod) => {
	return moment(sD).add(Math.floor(mR / fMod), 'day').diff(sD, convertTypeForMoment(f)) + 1;
};

export const createValidation = ({
	startDate,
	endDate,
	frequency,
	days,
	endType,
	frequencyModifier
}) => {
	if (startDate === null) startDate = moment().set('hour', 12); //eslint-disable-line
	let failed;
	let validateObj = {
		valid: false,
		reason: ''
	};

	if (startDate.format('MMMM Do YYYY') === endDate.format('MMMM Do YYYY')) {
		validateObj.reason = 'Cannot make a recurring event that starts and ends on the same day. Are you sure that you dont want a single event?';
		failed = true;
	} else if (startDate.isAfter(endDate)) {
		validateObj.reason = 'Starting date cannot come after the ending date of your event.';
		failed = true;
	}
	if (!failed) {
    // It passed the easy edge cases so lets set it to true.
		validateObj.valid = true;
		switch (frequency) {
		// used to be latter half of ternary - switching to endDate temporarily.
		case 'Daily':
			validateObj.reason = formatDailyString(startDate, endDate, frequencyModifier);
			break;
		case 'Weekly':
			validateObj = formatWeekString(
				days,
				endType,
				endDate,
				frequency,
				frequencyModifier,
				startDate
			);
			break;
		case 'Monthly':
			validateObj.reason = formatMonthlyString(
				endDate,
				startDate,
				frequency,
				frequencyModifier,
			);
			break;
		default:
			validateObj.reason = 'No proper frequency detected...';
		}
		return validateObj;
	}
	return validateObj;
};
