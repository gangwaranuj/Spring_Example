import moment from 'moment';
import { List } from 'immutable';
import {
  convertTypeForMoment,
  aboveMin,
  generateRepetitions,
  generateEndDate
} from '../utils';

/*
  This is not a pretty file, whatever developer has ended up here. I apologize.
  Dates suck.

  There is one formula used a lot here I'd love to break down for you.
  sD = startDate;
  R = repetitions;
  fMod = frequencyModifier (i.e. every week, every other week, ...);
  convertToAnnoyingString = A function that takes a string, and makes it moment(tm) friendly.
  f = frequency (i.e. 'Daily', 'Weekly', 'Monthly');
  eD = endDate;

  sD + ((R*fMod), convertToAnnoyingString(f) = eD;

  This formula figures out the new end date after any of the variables in it change. I also
  have some helper functions that make sure that this is within the maximum possible allowed
  date range - and if not makes it the max possible endDate.
*/

// DAYS OF WEEK
const validateDaysOfWeekHelper = (state, action) => {
	const clearedState = state.set('errors', List([]));
	const errorOne = 'There must always be atleast one day of the week selected.';
  // Find how many 'true' booleans there are in the Days array -
  // this is the amount of days per week.
	const daysSelected = clearedState.get('days').filter(day => day).length;
	if (daysSelected === 1 && clearedState.getIn(['days', action.value])) {
		return clearedState.set('errors', clearedState.get('errors').push(errorOne));
	}
	return clearedState;
};

export const updateDaysOfWeekHelper = (state, action) => {
	const potentialState = validateDaysOfWeekHelper(state, action);
	return potentialState.get('errors').size > 0
  ? potentialState
  : state.setIn(['days', action.value], !state.getIn(['days', action.value])).set('errors', List([]));
};

// ACTIVATION OF MODULE
export const updateRecurrenceHelper = (state, action) =>
	state
    .set('type', action.value)
    .set('errors', List([]));

// FREQUENCY
const validateFrequencyHelper = (state, action) => {
	const modifiedState = state
  .set('frequency', action.value)
  .set('frequencyModifier', '1')
  .set('errors', List([]));

	const desiredEndDate = moment(modifiedState.get('startDate'))
    .add(1, convertTypeForMoment(action.value));
	const actualEndDate = generateEndDate(modifiedState, desiredEndDate);

	return modifiedState
  .set('endDate', actualEndDate);
};

export const updateFrequencyHelper = (state, action) =>
	action.value === 'Monthly'
	? validateFrequencyHelper(state, action)
		.set('repetitions', '1')
		.set('endType', 'Instances')
    : validateFrequencyHelper(state, action).set('repetitions', '1');

// REPETITIONS
const validateRepetitionHelper = (state, action) => {
	const modifiedState = state
  .set('repetitions', action.value)
  .set('errors', List([]));

	const desiredEndDate = moment(modifiedState.get('startDate')).add((Number(action.value) * state.get('frequencyModifier')), convertTypeForMoment(state.get('frequency')));
	const actualEndDate = generateEndDate(modifiedState, desiredEndDate);

	return { modifiedState, actualEndDate };
};

export const updateRepetitionHelper = (state, action) => {
	const {
    modifiedState,
    actualEndDate
  } = validateRepetitionHelper(state, action);

	return modifiedState
  .set('endDate', actualEndDate)
  .set('repetitions', generateRepetitions(modifiedState, actualEndDate));
};

// START DATE
const initializeStartDate = (state, action) => {
	const daysOfWeek = List([false, false, false, false, false, false, false])
		.set(action.value.day(), true);

	return state
  .set('startDate', action.value)
  .set('days', daysOfWeek)
  .set('errors', List([]));
};

const validStartDate = (state) => {
	return state
  // Relocate End Date to the same distance away because start date
  // comes from a different module in assignment creation.
  .set('endDate', moment(state.get('startDate'))
    .add(state.get('repetitions') * state.get('frequencyModifier'), convertTypeForMoment(state.get('frequency')),
    ),
  )
  .set('errors', List([]));
};

const invalidStartDate = (state) => {
	return state
  .set('endDate', moment(state.get('startDate'))
    .add(state.get('minRepeat') * state
      .get('frequencyModifier'), convertTypeForMoment(state.get('frequency')),
    ),
  );
};

const shouldHideStartBasedErrors = (state, error) =>
	state.get('type') === 'Recur'
		? state.set('repetitions', generateRepetitions(state, state.get('endDate'))).set('errors', state.get('errors').push(error))
    : state.set('repetitions', generateRepetitions(state, state.get('endDate')));

export const updateStartDateHelper = (state, action) => {
	const errorOne = 'The end date has been moved later, because your selected date range didn\'t have a single recurring assignment taking place!';

	let modifiedState = initializeStartDate(state, action);

	if (aboveMin(modifiedState, false)) {
		return validStartDate(modifiedState);
	}
	modifiedState = invalidStartDate(modifiedState);

	return shouldHideStartBasedErrors(modifiedState, errorOne);
};

// END DATE
const initEndDate = (state, action) => {
  // The setting of hours is to deal with .diff rounding on day diffs based on hours.
	const actualEndDate = generateEndDate(state, moment(action.value)).set('hour', 11);

	const modifiedState = state
  .set('endDate', actualEndDate)
  .set('errors', List([]));

	return {
		actualEndDate,
		modifiedState
	};
};

export const updateEndDateHelper = (state, action) => {
	const {
    modifiedState,
    actualEndDate
  } = initEndDate(state, action);

	if (aboveMin(state, action.value)) {
		return modifiedState
    .set('repetitions', generateRepetitions(state, moment(actualEndDate).add(1, 'day')));
	}
	return modifiedState
    .set('repetitions', generateRepetitions(state, moment(actualEndDate)));
};

// FREQUENCY MODIFIER
const initFreqMod = (state, action) =>
	state
    .set('frequencyModifier', action.value)
    .set('errors', List([]));

const calculateFreqModEndDate = (state, action) => moment(state.get('startDate')).add(state.get('minRepeat') * action.value, convertTypeForMoment(state.get('frequency')));

export const updateFrequencyModifierHelper = (state, action) => {
	const errorOne = 'Your new \'repeats every\' setting didn\'t have a single assignment recurring before the end date, so we\'ve moved the end date to a later date to ensure an assignment happens.';
	const errorTwo = 'We\'ve moved the end date to have the same amount of assignments created as before using your new settings.';
	let desiredEndDate;
	let actualEndDate;
	let modifiedState = initFreqMod(state, action);

  // With both Daily and Weekly, you can set endDates that frequency modifier may fit in even
  // after change, so it needs to be handled differently than monthly where thats not an option.
	if ((state.get('frequency') === 'Daily' || state.get('frequency') === 'Weekly') && state.get('endType') === 'Date') {
		if (aboveMin(modifiedState, moment(state.get('endDate')))) {
			actualEndDate = moment(state.get('endDate'));
		} else {
			actualEndDate = calculateFreqModEndDate(modifiedState, action);
      // Due to hours playing a part in how diffs are calculated in moment - this is a sanity check
      // To make sure that the end date has in fact moved.
			if (actualEndDate.format('MMMM Do YYYY') !== state.get('endDate').format('MMMM Do YYYY')) {
				modifiedState = modifiedState.set('errors', modifiedState.get('errors').push(errorOne));
			}
		}
	} else {
		desiredEndDate = calculateFreqModEndDate(modifiedState, action);
		actualEndDate = generateEndDate(modifiedState, desiredEndDate);
		modifiedState = modifiedState.set('errors', state.get('errors').push(errorTwo));
	}

	return modifiedState
  .set('endDate', actualEndDate)
  .set('repetitions', generateRepetitions(modifiedState, actualEndDate));
};

// END TYPE

const calculateEndTypeEndDate = state => moment(state.get('startDate')).add((state.get('repetitions') * state.get('frequencyModifier')), convertTypeForMoment(state.get('frequency')));

export const updateEndTypeHelper = (state, action) => {
	const desiredEndDate = calculateEndTypeEndDate(state);

	if (action.value === 'Instances') {
		return state
    .set('endType', action.value)
    .set('endDate', generateEndDate(state, desiredEndDate))
    .set('errors', List([]));
	}
	return state
    .set('endType', action.value)
    .set('errors', List([]));
};
