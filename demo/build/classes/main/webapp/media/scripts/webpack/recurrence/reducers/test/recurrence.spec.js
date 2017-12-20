import moment from 'moment';
import recurrence from '../index';
import {
  UPDATE_RECURRENCE_TOGGLE,
  UPDATE_FREQUENCY_TYPE,
  UPDATE_REPETITION_COUNT,
  UPDATE_START_DATE,
  UPDATE_END_DATE,
  UPDATE_DAYS_OF_WEEK_SELECTED,
  UPDATE_ENDING_TYPE
} from '../../constants';
import initialState from '../initialState';

describe('<WMRecurrence />', () => {
	let state = recurrence(initialState, {
		type: null,
		value: null
	});

	describe('Initialization', () => {
		it('should initialize equal to initial state', () => {
			expect(state).toEqual(initialState);
		});
	});

	describe('Reduction/Validation', () => {
		beforeEach(() => {
			state = recurrence(initialState, {
				type: null,
				value: null
			});
		});

		it('should update recurrence properly', () => {
			state = recurrence(state, {
				type: UPDATE_RECURRENCE_TOGGLE,
				value: 'Recur'
			});
			expect(state.get('type')).toEqual('Recur');
		});

		it('should update the type of frequency properly', () => {
			state = recurrence(state, {
				type: UPDATE_FREQUENCY_TYPE,
				value: 'Weekly'
			});
			expect(state.get('frequency')).toEqual('Weekly');
			expect(state.get('repetitions')).toEqual('1');
			expect(state.get('frequencyModifier')).toEqual('1');
		});

		it('should update start date properly', () => {
			const newStartDate = moment().add(31, 'day');
			state = recurrence(state, {
				type: UPDATE_START_DATE,
				value: newStartDate
			});
			expect(moment(state.get('startDate')).format('MMMM Do YYYY')).toEqual((moment(newStartDate).format('MMMM Do YYYY')));
			expect(moment(state.get('endDate')).format('MMMM Do YYYY')).toEqual((moment(newStartDate).add(1, 'day').format('MMMM Do YYYY')));
			expect(state.get('repetitions')).toEqual('1');
		});

		it('should update repetitions properly', () => {
			state = recurrence(state, {
				type: UPDATE_START_DATE,
				value: moment()
			});
			state = recurrence(state, {
				type: UPDATE_REPETITION_COUNT,
				value: '10'
			});
			expect(state.get('repetitions')).toEqual('10');
			expect(moment(state.get('endDate')).format('MMMM Do YYYY')).toEqual((moment(state.get('startDate')).add(10, 'day').format('MMMM Do YYYY')));
		});

		// it('should update end date properly', () => {
		// 	const newEndDate = moment().add(20, 'day');
		// 	state = recurrence(state, {
		// 		type: UPDATE_START_DATE,
		// 		value: moment()
		// 	});
		// 	state = recurrence(state, {
		// 		type: UPDATE_END_DATE,
		// 		value: newEndDate
		// 	});
		// 	expect(state.get('endDate').format('MMMM Do YYYY')).toEqual(newEndDate.format('MMMM Do YYYY'));
		// 	expect(state.get('repetitions')).toEqual('20');
		// });

		it('should update selected days of week properly', () => {
			let payload = 1;
			let initialDay = state.getIn(['days', payload]);
			if (initialDay === true) {
				initialDay = false;
				payload = 2;
			}
			state = recurrence(state, {
				type: UPDATE_DAYS_OF_WEEK_SELECTED,
				value: payload
			});
			expect(state.getIn(['days', payload])).not.toEqual(initialDay);
		});

		it('should update ending type properly', () => {
			state = recurrence(state, {
				type: UPDATE_ENDING_TYPE,
				value: 'Instances'
			});
			expect(state.get('endType')).toEqual('Instances');
		});
	});
});
