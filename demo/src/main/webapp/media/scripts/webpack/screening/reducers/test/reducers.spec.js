import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import _ from 'underscore';
import { Map } from 'immutable';
import fieldReducer from '../index';
import { initialState } from '../../configureStore/state';
import * as types from '../../constants/actionTypes';
import fields, { getFields } from '../../fields';
import validationMiddleware from '../validationMiddleware';
import filterMiddleware from '../filterMiddleware';
import submitValidationMiddleware from '../submitValidationMiddleware';

const createAction = (name, value = '', error = '') => ({
	changeAction: {
		type: types.CHANGE_FIELD,
		name,
		value,
		error
	}
});

const populateState = (state, name, value) => state.set(
	name,
	state.get(name)
		.set('value', value)
		.set('blurred', true)
		.set('dirty', true)
	);

const createStateFrom = (state, data) => {
	Object.keys(data).forEach(name => {
		state = populateState(state, name, data[name])
	});

	return state;
};

const mockStore = configureMockStore([thunk]);

describe('Screening Order Form Reducer', () => {
	let state;

	beforeEach(() => {
		state = Map(initialState)
	});

	it('should return the initial state', () => {
		expect(fieldReducer(undefined, {})).toEqual(state);
	});

	it('should handle `CHANGE_FIELD` action', () => {
		const firstName = 'Vinny';
		const action = {
			type: types.CHANGE_FIELD,
			name: 'firstName',
			error: '',
			value: firstName
		};
		state = state.set(
			'firstName',
			state.get('firstName')
				.set('value', firstName)
				.set('dirty', true)
		);

		expect(fieldReducer(undefined, action)).toEqual(state);
	});

	it('should handle `BLUR_FIELD` action', () => {
		const action = {
			type: types.BLUR_FIELD,
			name: 'firstName'
		};
		state = state.set(
			'firstName',
			state.get('firstName')
				.set('blurred', true)
		);

		expect(fieldReducer(undefined, action)).toEqual(state);
	});
});

describe('Middleware: `validationMiddleware`', () => {
	let state;
	let getState;
	let store;
	let action;
	let next;

	describe('Structure', () => {
		beforeEach(() => {
			state = Map(initialState);
			getState = jest.fn(() => state);
			store = { getState };
			action = {
				type: types.CHANGE_FIELD,
				name: 'firstName',
				value: 'Queezus'
			};
			next = jest.fn(() => action);
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

	describe('Validating', () => {
		const firstName = 'Queezus';
		const next = _.identity;

		Object.keys(fields).forEach(key => {
			if (!fields[key].required) {
				return;
			}

			it(`should add an error property to the \`action\` object if \`${key}\` is invalid`, () => {
				state = populateState(initialState, key, 'placeholder');
				getState = jest.fn(() => state);
				store = { getState };
				const { changeAction } = createAction(key, '');

				const result = validationMiddleware(store)(next)(changeAction);
				expect(result.error).toEqual(fields[key].constraints.presence.message);
			});
		});

		describe('SSN', () => {
			it('should fail for non-digits in `SSN`', () => {
				const SSN = '123xxx789';
				const { changeAction } = createAction('SSN', SSN);
				state = populateState(state, 'SSN', '');
				getState = jest.fn(() => state);
				store = { getState };

				const result = validationMiddleware(store)(next)(changeAction);
				expect(result.error).toEqual(fields.SSN.constraints.format.message);
			});

			it('should only accept `SSN` of 9 digits', () => {
				const SSN = '1234567890987654321';
				const { changeAction } = createAction('SSN', SSN);
				state = populateState(state, 'SSN', '');
				getState = jest.fn(() => state);
				store = { getState };

				const result = validationMiddleware(store)(next)(changeAction);
				expect(result.error).toEqual(fields.SSN.constraints.length.wrongLength);
			});
		});

		describe('Security Code', () => {
			it('should only accept `cardSecurityCode` a minimum of 3 digits', () => {
				const cardSecurityCode = '12';
				const { changeAction } = createAction('cardSecurityCode', cardSecurityCode);
				state = populateState(state, 'cardSecurityCode', '');
				getState = jest.fn(() => state);
				store = { getState };

				const result = validationMiddleware(store)(next)(changeAction);
				expect(result.error).toEqual(fields.cardSecurityCode.constraints.length.wrongLength);
			});

			it('should fail for non-digits in `cardSecurityCode`', () => {
				const cardSecurityCode = '12x';
				const { changeAction } = createAction('cardSecurityCode', cardSecurityCode);
				state = populateState(state, 'cardSecurityCode', '');
				getState = jest.fn(() => state);;
				store = { getState };

				const result = validationMiddleware(store)(next)(changeAction);
				expect(result.error).toEqual(fields.cardSecurityCode.constraints.format.message);
			});
		});

		describe('Credit Card', () => {
			xit('should only accept `cardNumber` a minimum of 15 digits', () => {
				const cardNumber = '424242424242424';
				const { changeAction } = createAction('cardNumber', cardNumber);
				state = populateState(state, 'cardNumber', '');
				getState = jest.fn(() => state);
				store = { getState };

				const result = validationMiddleware(store)(next)(changeAction);
				expect(result.error).toEqual(fields.cardNumber.constraints.length.wrongLength);
			});

			xit('should only accept `cardNumber` a maximum of 16 digits', () => {
				const cardNumber = '4242424242424242';
				const { changeAction } = createAction('cardNumber', cardNumber);
				state = populateState(state, 'cardNumber', '');
				getState = jest.fn(() => state);
				store = { getState };

				const result = validationMiddleware(store)(next)(changeAction);
				expect(result.error).toEqual(fields.cardNumber.constraints.length.wrongLength);
			});

			xit('should fail for non-digits in `cardNumber`', () => {
				const cardNumber = '4242xxxx42424242';
				const { changeAction } = createAction('cardNumber', cardNumber);
				state = populateState(state, 'cardNumber', '');
				getState = jest.fn(() => state);
				store = { getState };

				const result = validationMiddleware(store)(next)(changeAction);
				expect(result.error).toEqual(fields.cardNumber.constraints.format.message);
			});
		});

		it('should validate `zip` codes based on #####-####', () => {
			const zip = '1001d443fdss';
			const { changeAction } = createAction('zip', zip);
			state = populateState(state, 'zip', '');
			getState = jest.fn(() => state);
			store = { getState };

			const result = validationMiddleware(store)(next)(changeAction);
			expect(result.error).toEqual(fields.zip.constraints.format.message);
		});

		it('should not modify the `action` object if field is valid', () => {
			state = populateState(initialState, 'firstName', firstName);
			getState = jest.fn(() => state);;
			store = { getState };
			const { changeAction } = createAction('firstName', firstName);

			const result = validationMiddleware(store)(next)(action);
			expect(result).toEqual(action);
		});
	});
});

describe('Middleware: `filterMiddleware`', () => {
	let state;
	let getState;
	let store;
	let action;
	let next;

	describe('Structure', () => {
		beforeEach(() => {
			state = Map(initialState);
			getState = jest.fn(() => state);
			store = { getState };
			action = {
				type: types.CHANGE_FIELD,
				name: 'firstName',
				value: 'Queezus'
			};
			next = jest.fn(() => action);
		});

		it('should return a function', () => {
			expect(typeof filterMiddleware(store)).toBe('function');
		});

		it('should call the `next` method', () => {
			filterMiddleware(store)(next)(action);
			expect(next).toHaveBeenCalled();
		});

		it('should call the `next` method with `action`', () => {
			filterMiddleware(store)(next)(action);
			expect(next).toHaveBeenCalledWith(action);
		});

		it('should call the `next` and return `action`', () => {
			filterMiddleware(store)(next)(action);
			expect(next.mock.calls[0][0]).toEqual(action);
		});

		it('should return the result of the `next` call', () => {
			const result = filterMiddleware(store)(next)(action);
			expect(result).toEqual(action);
		});
	});

	describe('Filtering', () => {
		const SSN = '123-45-6789';
		const filteredSSN = '123456789'
		const next = _.identity;

		it('should return a filtered value for `SSN`', () => {
			const { changeAction } = createAction('SSN', SSN);
			state = populateState(state, 'SSN', '');
			getState = jest.fn(() => state);;
			store = { getState };

			const result = filterMiddleware(store)(next)(changeAction);
			expect(result.value).toEqual(filteredSSN);
		});
	});
});

describe('Middleware: `submitValidationMiddleware`', () => {
	let state;
	let getState;
	let dispatch;
	let store;
	let action;
	let next;

	beforeEach(() => {
		state = Map(initialState)
	});

	describe('Structure', () => {
		beforeEach(() => {
			state = Map(initialState);
			getState = jest.fn(() => state);
			dispatch = jest.fn(() => {});
			store = { getState, dispatch };
			action = {
				type: types.CHANGE_FIELD,
				name: 'firstName',
				value: 'Queezus'
			};
			next = jest.fn(() => action);
		});

		it('should return a function', () => {
			expect(typeof submitValidationMiddleware(store)).toBe('function');
		});

		it('should call the `next` method', () => {
			submitValidationMiddleware(store)(next)(action);
			expect(next).toHaveBeenCalled();
		});

		it('should call the `next` method with `action`', () => {
			submitValidationMiddleware(store)(next)(action);
			expect(next).toHaveBeenCalledWith(action);
		});

		it('should call the `next` and return `action`', () => {
			submitValidationMiddleware(store)(next)(action);
			expect(next.mock.calls[0][0]).toEqual(action)
		});

		it('should return the result of the `next` call', () => {
			const result = submitValidationMiddleware(store)(next)(action);
			expect(result).toEqual(action);
		});
	});

	describe('Validating', () => {
		next = _.identity;
		const SSN = '1234567890987654321';
		const { changeAction } = createAction('SSN', SSN);

		beforeEach(() => {
			store = mockStore(state);
			const result = submitValidationMiddleware(store)(next)(changeAction);
		});

		it('should dispatch `FORM_SUBMIT_DISABLED_TOGGLE`', () => {
			expect(store.getActions()[0].type).toEqual(types.FORM_SUBMIT_DISABLED_TOGGLE)
		});

		it('should dispatch `false` if fields are not valid', () => {
			expect(store.getActions()[0].value).toBeFalsy();
		});

		it('should dispatch `true` if fields are not valid', () => {
			state.map((field, name) => {
				if (
					Map.isMap(state.get(name))
					&& state.get(name).has('dirty')
				) {
					state = state.setIn([name, 'dirty'], true);
				}
			});

			store = mockStore(state);
			const result = submitValidationMiddleware(store)(next)(changeAction);

			expect(store.getActions()[0].value).toBeTruthy();
		});
	});
});
