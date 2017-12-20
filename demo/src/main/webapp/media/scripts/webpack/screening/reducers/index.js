import validate from 'validate.js';
import * as types from '../constants/actionTypes';
import fields from '../fields';
import { initialState } from '../configureStore/state';

export const fieldReducer = (state = initialState, { type, name, value, error }) => {
	let newState = state;
	switch (type) {
	case types.VALIDATE_ALL_FIELDS:
		Object.keys(fields).forEach(field => {
			const valueToBeValidated = state.get(field).get('value');
			const validationResult = validate(
				{ [field]: valueToBeValidated },
				{ [field]: fields[field].constraints },
				{ fullMessages: false }
			);
			const errorMessage = validationResult
				? validationResult[field][0]
				: '';

			newState = newState.set(
				field,
				state.get(field)
					.set('error', errorMessage)
			);
		});

		return newState.set('submissionAttempt', true);

	case types.BLUR_FIELD:
		return state.set(
			name,
			state.get(name)
				.set('error', error || '')
				.set('blurred', true)
		);

	case types.CHANGE_FIELD:
		return state.set(
			name,
			state.get(name)
				.set('value', value)
				.set('error', error || '')
				.set('dirty', true)
		);

	case types.CHECK_FIELD:
		return state
			.set('billingAddress1', state.get('address1'))
			.set('billingAddress2', state.get('address2'))
			.set('billingCity', state.get('city'))
			.set('billingState', state.get('state'))
			.set('billingProvince', state.get('province'))
			.set('billingPostalCode', state.get('postalCode'))
			.set('billingZip', state.get('zip'))
			.set('billingCountry', state.get('country'))
			.set('checked', !state.get('checked'));

	case types.FORM_SUBMIT_DISABLED_TOGGLE:
		return state.set('isFormValid', value);

	case types.FORM_SUBMIT_REQUEST:
		return state
			.set('isFormValid', true)
			.set('submitting', true)
			.set('internalError', false)
			.set('paymentError', false);

	case types.FORM_SUBMIT_ERROR:
		// TODO: Extract the differnet types of errors and set below
		return state
			.set('isFormValid', false)
			.set('submitting', false)
			.set('internalError', true)
			.set('paymentError', false);

	case types.FORM_SUBMIT_SUCCESS:
		return state
			.set('drugTestPending', true)
			.set('canOrder', false);

	default:
		return state;
	}
};

export default fieldReducer;
