import * as types from '../constants';

export const updateRecurrence = value => ({
	type: types.UPDATE_RECURRENCE_TOGGLE,
	value
});

export const updateFrequencyType = value => ({
	type: types.UPDATE_FREQUENCY_TYPE,
	value
});

export const updateRepetitions = value => ({
	type: types.UPDATE_REPETITION_COUNT,
	value
});

export const updateFrequencyModifier = value => ({
	type: types.UPDATE_FREQUENCY_MODIFIER,
	value
});

export const updateEndDate = value => ({
	type: types.UPDATE_END_DATE,
	value
});

export const updateStartDate = value => ({
	type: types.UPDATE_START_DATE,
	value
});

export const updateDaysOfWeek = value => ({
	type: types.UPDATE_DAYS_OF_WEEK_SELECTED,
	value
});

export const updateEndType = value => ({
	type: types.UPDATE_ENDING_TYPE,
	value
});

export const updateFormValidity = value => ({
	type: types.UPDATE_FORM_VALIDITY,
	value
});

export const updateErrors = value => ({
	type: types.UPDATE_ERRORS,
	value
});
