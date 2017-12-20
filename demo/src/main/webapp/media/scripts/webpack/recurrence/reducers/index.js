import initialState from './initialState';
import * as types from '../constants';
import {
	updateRecurrenceHelper,
	updateFrequencyHelper,
	updateRepetitionHelper,
	updateStartDateHelper,
	updateEndDateHelper,
	updateFrequencyModifierHelper,
	updateDaysOfWeekHelper,
	updateEndTypeHelper
} from './helpers';

export default (state = initialState, action) => {
	switch (action.type) {
	case types.UPDATE_RECURRENCE_TOGGLE:
		return updateRecurrenceHelper(state, action);
	case types.UPDATE_FREQUENCY_TYPE:
		return updateFrequencyHelper(state, action);
	case types.UPDATE_REPETITION_COUNT:
		return updateRepetitionHelper(state, action);
	case types.UPDATE_START_DATE:
		return updateStartDateHelper(state, action);
	case types.UPDATE_END_DATE:
		return updateEndDateHelper(state, action);
	case types.UPDATE_DAYS_OF_WEEK_SELECTED:
		return updateDaysOfWeekHelper(state, action);
	case types.UPDATE_ENDING_TYPE:
		return updateEndTypeHelper(state, action);
	case types.UPDATE_FORM_VALIDITY:
		return state.set('validity', action.value);
	case types.UPDATE_FREQUENCY_MODIFIER:
		return updateFrequencyModifierHelper(state, action);
	default:
		return state;
	}
};

