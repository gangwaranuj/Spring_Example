import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import { Map } from 'immutable';
import * as actions from '../index';
import * as types from '../../constants/actionTypes';
import { initialState } from '../../configureStore/state';

const mockStore = configureMockStore([thunk]);
const baseUrl = 'http://localhost:8080/worker/v2/services';
const drugEndPoint = '/drug-test';
const backgroundEndPoint = '/background-check';
const successResponse = {
	meta: {
		code: 200
	}
};
const errorResponse = {
	meta: {
		message: 'We are unable to process your request, please refers to results for more details.',
		code: 422
	},
	results: [
		'Address is a required field.'
	]
};
const stateValues = {
	firstName: 'Test',
	lastName: 'User',
	birthMonth: 1,
	birthDay: 1,
	birthYear: 1900,
	SSN: '111-11-1111',
	address1: '1010 Digital Lane',
	address2: '',
	city: 'New York',
	state: 'NY',
	zip: '10001',
	country: 'USA',
	email: 'test.user@email.com',
	paymentType: 'cc',
	cardType: 'mastercard',
	firstNameOnCard: 'Test',
	lastNameOnCard: 'User',
	cardNumber: '4111111111111111',
	cardExpirationMonth: '01',
	cardExpirationYear: '2020',
	cardSecurityCode: '000',
	billingCountry: 'USA',
	billingAddress1: '1010 Digital Lane',
	billingAddress2: '',
	billingCity: 'New York',
	billingState: 'NY',
	billingZip: '10001'
};

const screeningInfo = {
	firstName: 'Test',
	lastName: 'User',
	birthMonth: 1,
	birthDay: 1,
	birthYear: 1900,
	workIdentificationNumber: '111-11-1111',
	address1: '1010 Digital Lane',
	address2: '',
	city: 'New York',
	state: 'NY',
	postalCode: '10001',
	country: 'USA',
	email: 'test.user@email.com'
};
const payByAccountRequest = {
	screening: screeningInfo,
	payment: {
		paymentType: 'account',
	}
};
const payByCreditCard = {
	screening: screeningInfo,
	payment: {
		paymentType: 'cc',
		cardType: 'mastercard',
		firstNameOnCard: 'Test',
		lastNameOnCard: 'User',
		cardNumber: '4111111111111111',
		cardExpirationMonth: '01',
		cardExpirationYear: '2020',
		cardSecurityCode: '000',
		country: 'USA',
		address1: '1010 Digital Lane',
		address2: '',
		city: 'New York',
		state: 'NY',
		postalCode: '10001'
	}
};
const createServer = (payload = {}) =>
	nock(baseUrl)
		.post(drugEndPoint, payload)
		.reply(200, successResponse);
const createErrorServer = (payload = {}) =>
	nock(baseUrl)
		.post(drugEndPoint, payload)
		.reply(422, errorResponse);

describe('Screening Order Form Actions', () => {
	it('should create a change field action', () => {
		const firstName = 'Vinny';
		const expectedAction = {
			type: types.CHANGE_FIELD,
			name: 'firstName',
			value: firstName
		};

		expect(actions.changeField('firstName', firstName)).toEqual(expectedAction);
	});

	it('should create a blur field action', () => {
		const expectedAction = {
			type: types.BLUR_FIELD,
			name: 'firstName'
		};

		expect(actions.blurField('firstName')).toEqual(expectedAction);
	});

	it('should create a form submit error action', () => {
		const someError = {
			code: 666,
			message: 'What is Aleppo?'
		};
		const expectedAction = {
			type: types.FORM_SUBMIT_ERROR,
			error: someError
		};

		expect(actions.formSubmitError(someError)).toEqual(expectedAction);
	});

	describe('#submitFormToServer', () => {
		let server;

		beforeEach(() => {
			server = createServer();
		});

		afterEach(() => {
			nock.cleanAll();
		});

		it('should be fulfilled', () => {
			try {
				expect(actions.submitFormToServer({}, baseUrl + drugEndPoint)).resolves.toBe('');
			} catch (error) {
				throw error;
			}
		});
	});

	describe('#submitForm', () => {
		let state;
		let server;
		let store;
		let payload;

		beforeEach(() => {
			state = Map(initialState);
			Object.keys(stateValues).forEach(key => {
				state = state.setIn([key, 'value'], stateValues[key]);
			});
			payload = {
				form: state,
				url: baseUrl + drugEndPoint
			};
		});

		afterEach(() => {
			nock.cleanAll();
		});

		it('should dispatch a request action', () => {
			const expectedActions = [
				{
					type: types.FORM_SUBMIT_REQUEST
				}
			];

			store = mockStore(state);
			server = createServer(payByCreditCard);

			return store.dispatch(actions.submitForm(payload))
				.then(() => {
					try {
						const actions = store.getActions().slice(0, 1);
						expect(actions).toEqual(expectedActions);
					} catch (error) {
						throw error;
					}
				});
		});

		it('should dispatch a success action when successful', () => {
			const expectedActions = [
				{
					type: types.FORM_SUBMIT_REQUEST
				},
				{
					type: types.FORM_SUBMIT_SUCCESS
				}
			];

			store = mockStore(state);
			server = createServer(payByCreditCard);

			return store.dispatch(actions.submitForm(payload))
				.then(() => {
					try {
						expect(store.getActions()).toEqual(expectedActions);
					} catch (error) {
						throw error;
					}
				});
		});

		it('should dispatch an error action on error', () => {
			const expectedActions = [
				{
					type: types.FORM_SUBMIT_REQUEST
				},
				{
					type: types.FORM_SUBMIT_ERROR,
					error: errorResponse
				}
			];

			store = mockStore({});
			server = createErrorServer(payByCreditCard);

			return store.dispatch(actions.submitForm(payload))
				.then(() => {
					try {
						expect(store.getActions()).toEqual(expectedActions);
					} catch (error) {
						throw error;
					}
				});
		});
	});
});
