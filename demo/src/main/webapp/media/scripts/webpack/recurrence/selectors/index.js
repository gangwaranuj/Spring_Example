import { createSelector } from 'reselect';
import moment from 'moment';
import { convertTypeForMoment } from '../utils';

const getStartDate = recurrence => moment(recurrence.get('startDate'));
const getMinRepeat = recurrence => recurrence.get('minRepeat');
const getFrequencyModififer = recurrence => recurrence.get('frequencyModifier');
const getFrequency = recurrence => recurrence.get('frequency');

export const getMinDate = createSelector(
	[getStartDate, getMinRepeat, getFrequencyModififer, getFrequency],
  (startDate, minRepeat, frequencyModifier, frequency) => {
	return startDate.add(minRepeat * frequencyModifier, convertTypeForMoment(frequency)).toDate();
});

export const getMaxDate = createSelector(
	[getStartDate],
  (startDate) => {
	return startDate.add(1, 'year').toDate();
});
