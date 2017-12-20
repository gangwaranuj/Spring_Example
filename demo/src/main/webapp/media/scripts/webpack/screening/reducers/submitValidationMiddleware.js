import { getFields } from '../fields';
import { formSubmitDisabledToggle } from '../actions';
import * as types from '../constants/actionTypes';

const submitValidationMiddleware = store => next => action => {
	const result = next(action);

	if (action.type === types.FORM_SUBMIT_DISABLED_TOGGLE) return result;

	const state = store.getState();
	const country = state.get('country').get('value');
	const paymentType = state.get('paymentType').get('value');
	const fieldsToCheck = getFields(country, paymentType);

	const isFormValid = Object.keys(fieldsToCheck).every(key => {
		if (!fieldsToCheck[key].required) return true;

		const error = state.get(key).get('error');
		const dirty = state.get(key).get('dirty');

		return error === '' && dirty;
	});

	store.dispatch(formSubmitDisabledToggle(isFormValid));

	return result;
};

export default submitValidationMiddleware;
