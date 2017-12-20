import { Map } from 'immutable';
import { initialState } from '../';
import { initialFundsState } from '../fundsReducer';
import { initialTaxState } from '../taxReducer';
import * as types from '../../constants/actionTypes';
import validationMiddleware from '../validationMiddleware';
import filterMiddleware from '../filterMiddleware';
import fundsFields from '../../fields/fundsFields';
import taxFields from '../../fields/taxFields';

const createAction = (name, type, value = '', error = '') => ({
	changeAction: {
		type,
		name,
		value,
		error
	}
});

const populateState = (state, name, value) => state.setIn([name, 'value'], value)
	.setIn([name, 'blurred'], true)
	.setIn([name, 'dirty'], true);

describe('Onboarding :: Validation Middleware', () => {
	describe('Structure ::', () => {
		let state;
		let getState;
		let store;
		let action;
		let next;

		beforeEach(() => {
			state = initialState;
			getState = jest.fn(() => state);
			store = { getState };
			action = {
				type: types.CHANGE_FUNDS_FIELD,
				name: 'routingNumber',
				value: '123456789'
			};
			next = jest.fn(() => (action));
		});

		it('should return a function', () => {
			expect(typeof validationMiddleware(store)).toBe('function');
		});

		it('should call the store\'s `getState` method', () => {
			validationMiddleware(store)(next)(action);
			expect(store.getState).toHaveBeenCalled();
		});

		it('should call the `next` method', () => {
			validationMiddleware(store)(next)(action);
			expect(next).toHaveBeenCalled();
		});

		it('should call the `next` method with `action`', () => {
			validationMiddleware(store)(next)(action);
			expect(next).toHaveBeenCalledWith(action);
		});

		it('should call the `next` and return `action`', () => {
			validationMiddleware(store)(next)(action);
			expect(next.mock.calls[0][0]).toEqual(action);
		});

		it('should return the result of the `next` call', () => {
			const result = validationMiddleware(store)(next)(action);
			expect(result).toEqual(action);
		});
	});

	describe('Validation ::', () => {
		let state;
		let getState;
		let store;

		// no-op
		const next = freeMaxB => freeMaxB;

		Object.keys(fundsFields).forEach((key) => {
			if (key === 'type' || key === 'paymentMethod' || key === 'country') {
				return;
			}

			it(`should add an error property to the 'action' object if '${key}' is invalid`, () => {
				state = Map({});
				state.funds = populateState(initialFundsState, key, 'placeholder');
				getState = jest.fn(() => state);
				store = { getState };
				const { changeAction } = createAction(key, types.CHANGE_FUNDS_FIELD, '');

				const result = validationMiddleware(store)(next)(changeAction);
				expect(result.error).toEqual(fundsFields[key].constraints.presence.message);
			});
		});

		Object.keys(taxFields).forEach((key) => {
			if (taxFields[key].constraints === undefined || !taxFields[key].constraints.presence) {
				return;
			}

			it(`should add an error property to the 'action' object if '${key}' is invalid`, () => {
				state = Map({});
				state.tax = populateState(initialTaxState, key, 'placeholder');
				getState = jest.fn(() => state);
				store = { getState };
				const { changeAction } = createAction(key, types.CHANGE_TAX_FIELD, '');

				const result = validationMiddleware(store)(next)(changeAction);
				expect(result.error).toEqual(taxFields[key].constraints.presence.message);
			});
		});

		it('should fail if routing number is not 9 digits', () => {
			const routingNumber = '12345678';
			const { changeAction } = createAction('routingNumber', types.CHANGE_FUNDS_FIELD, routingNumber);
			state.funds = populateState(initialFundsState, 'routingNumber', '');
			getState = jest.fn(() => state);
			store = { getState };

			const result = validationMiddleware(store)(next)(changeAction);
			expect(result.error).toEqual(fundsFields.routingNumber.constraints.length.wrongLength);
		});

		describe('for account number', () => {
			it('should fail if account number is less than 4 digits', () => {
				const accountNumber = '123';
				const { changeAction } = createAction('accountNumber', types.CHANGE_FUNDS_FIELD, accountNumber);
				state.funds = populateState(initialFundsState, 'accountNumber', '');
				getState = jest.fn(() => state);
				store = { getState };

				const result = validationMiddleware(store)(next)(changeAction);
				expect(result.error).toEqual(fundsFields.accountNumber.constraints.length.wrongLength);
			});

			it('should fail if account number is greater than 17 digits', () => {
				const accountNumber = '1234567890123456789';
				const { changeAction } = createAction('accountNumber', types.CHANGE_FUNDS_FIELD, accountNumber);
				state.funds = populateState(initialFundsState, 'accountNumber', '');
				getState = jest.fn(() => state);
				store = { getState };

				const result = validationMiddleware(store)(next)(changeAction);
				expect(result.error).toEqual(fundsFields.accountNumber.constraints.length.wrongLength);
			});
		});

		describe('for confirm account number', () => {
			it('should fail if confirm account number is less than 4 digits', () => {
				const accountNumberConfirm = '123';
				const { changeAction } = createAction('accountNumberConfirm', types.CHANGE_FUNDS_FIELD, accountNumberConfirm);
				state.funds = populateState(initialFundsState, 'accountNumberConfirm', '');
				getState = jest.fn(() => state);
				store = { getState };

				const result = validationMiddleware(store)(next)(changeAction);
				expect(result.error)
					.toEqual(fundsFields.accountNumberConfirm.constraints.length.wrongLength);
			});

			it('should fail if confirm account number is greater than 17 digits', () => {
				const accountNumberConfirm = '1234567890123456789';
				const { changeAction } = createAction('accountNumberConfirm', types.CHANGE_FUNDS_FIELD, accountNumberConfirm);
				state.funds = populateState(initialFundsState, 'accountNumberConfirm', '');
				getState = jest.fn(() => state);
				store = { getState };

				const result = validationMiddleware(store)(next)(changeAction);
				expect(result.error)
					.toEqual(fundsFields.accountNumberConfirm.constraints.length.wrongLength);
			});
		});

		it('should not modify the `action` object if field is valid', () => {
			const type = 'RIP Gene Wilder';
			state.funds = populateState(initialFundsState, 'type', type);
			getState = jest.fn(() => state);
			store = { getState };
			const { changeAction } = createAction('type', types.CHANGE_FUNDS_FIELD, type);

			const result = validationMiddleware(store)(next)(changeAction);
			expect(result).toEqual(changeAction);
		});
	});

	describe('Filter ::', () => {
		let state;
		let getState;
		let store;

		// no-op
		const next = freeMaxB => freeMaxB;

		describe('Tax Numbers ::', () => {
			it('should filter US tax numbers to match EID format', () => {
				state = Map({});
				const taxNumber = '1234';
				const { changeAction } = createAction('taxNumber', types.CHANGE_TAX_FIELD, taxNumber);
				state.tax = populateState(initialTaxState, 'taxNumber', '');
				getState = jest.fn(() => state);
				store = { getState };

				const result = filterMiddleware(store)(next)(changeAction);
				expect(result.value).toEqual('12-34');
			});
			it('should filter Canadian tax numbers to match Business Number format', () => {
				state = Map({});
				const taxNumber = '123456789RN2345';
				const { changeAction } = createAction('taxNumber', types.CHANGE_TAX_FIELD, taxNumber);
				state.tax = populateState(initialTaxState, 'taxNumber', '');
				state.tax = state.tax.setIn(['taxCountry', 'value'], 'canada');
				getState = jest.fn(() => state);
				store = { getState };

				const result = filterMiddleware(store)(next)(changeAction);
				expect(result.value).toEqual('123456789-RN-2345');
			});
		});
	});
});
