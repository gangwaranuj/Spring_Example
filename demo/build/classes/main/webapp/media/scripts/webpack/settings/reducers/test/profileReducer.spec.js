/* eslint-disable jest/no-disabled-tests */
import { Map } from 'immutable';
import profile, { initialProfileState } from '../profileReducer';
import * as types from '../../constants/actionTypes';

describe('Settings Profile Card Reducer :: ', () => {
	describe('Initial State', () => {
		xit('should return an Immutable.js Map', () => {
			expect(initialProfileState).toBeInstanceOf(Map);
		});
	});

	describe('Reducer', () => {
		const getState = () => {
			return Map(initialProfileState);
		};

		xit('should return initial state', () => {
			expect(profile(undefined, {})).toEqual(getState());
		});

		xit('should handle CHANGE_FIELD action', () => {
			const websiteName = 'http://cinco.mil';
			const action = {
				type: types.CHANGE_FIELD,
				name: 'website',
				error: '',
				value: websiteName
			};
			let state = getState();
			state = state.setIn(['website', 'value'], websiteName);
			expect(profile(undefined, action)).toEqual(state);
		});

		xit('should handle CHANGE_LOCATION_FIELD action', () => {
			const address1 = '240 W 37th Street';
			const action = {
				type: types.CHANGE_LOCATION_FIELD,
				name: 'address1',
				error: '',
				value: address1
			};
			let state = getState();
			state = state
				.setIn(['location', 'addressLine1', 'value'], address1)
				.setIn(['location', 'addressLine1', 'error'], '');
			expect(profile(undefined, action)).toEqual(state);
		});
	});
});
