import { Map } from 'immutable';
import tax, { initialTaxState } from '../taxReducer';
import taxFields from '../../fields/taxFields';
import * as types from '../../constants/actionTypes';

describe('Settings Tax Card Reducer :: ', () => {
	describe('Initial State', () => {
		it('should return an Immutable.js Map', () => {
			expect(initialTaxState).toBeInstanceOf(Map);
		});

		Object.keys(taxFields).forEach((name) => {
			it(`should have the '${name}' field`, () => {
				expect(initialTaxState.get(name)).toBeDefined();
			});
		});

		Object.keys(taxFields).forEach((name) => {
			it(`should have a 'value' property for the '${name}' field`, () => {
				if (taxFields[name].defaultValue !== null) {
					expect(initialTaxState.getIn([name, 'value'])).toEqual(taxFields[name].defaultValue);
				} else {
					expect(initialTaxState.getIn([name, 'value'])).toBeNull();
				}
			});
		});

		Object.keys(taxFields).forEach((name) => {
			it(`should have an empty \`error\` property for the \`${name}\` field`, () => {
				expect(initialTaxState.get(name).get('error')).toEqual('');
			});
		});
	});

	describe('Reducer', () => {
		const getState = () => {
			return Map(initialTaxState);
		};

		it('should return initial state', () => {
			expect(tax(undefined, {})).toEqual(getState());
		});

		it('should handle CHANGE_TAX_FIELD action', () => {
			const taxNumber = '34565';
			const action = {
				type: types.CHANGE_TAX_FIELD,
				name: 'taxNumber',
				error: '',
				value: taxNumber
			};
			let state = getState();
			state = state
				.setIn(['taxNumber', 'value'], taxNumber)
				.setIn(['taxNumber', 'dirty'], true);
			expect(tax(undefined, action)).toEqual(state);
		});

		it('should handle BLUR_TAX_FIELD action', () => {
			const taxNumber = '34565';
			const action = {
				type: types.BLUR_TAX_FIELD,
				name: 'taxNumber',
				error: '',
				value: taxNumber
			};
			let state = getState();
			state = state
				.setIn(['taxNumber', 'value'], taxNumber)
				.setIn(['taxNumber', 'blurred'], true);
			expect(tax(undefined, action)).toEqual(state);
		});
	});
});
