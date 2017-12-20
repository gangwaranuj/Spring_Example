import fetch from 'isomorphic-fetch';
import * as types from '../constants/actionTypes';
import Application from '../../core';
import { getFieldsForSubmission } from '../fields';

const makeActionCreator = (type, ...argNames) => (...args) => {
	const action = { type };

	argNames.forEach((arg, index) => {
		action[argNames[index]] = args[index];
	});

	return action;
};

export const blurField = makeActionCreator(types.BLUR_FIELD, 'name');
export const changeField = makeActionCreator(types.CHANGE_FIELD, 'name', 'value');
export const checkField = makeActionCreator(types.CHECK_FIELD);
export const formSubmitDisabledToggle = makeActionCreator(types.FORM_SUBMIT_DISABLED_TOGGLE, 'value');
export const formSubmitError = makeActionCreator(types.FORM_SUBMIT_ERROR, 'error');
const formSubmitRequest = { type: types.FORM_SUBMIT_REQUEST };
const formSubmitSuccess = { type: types.FORM_SUBMIT_SUCCESS };

export const preparePayload = (form) => {
	const country = form.get('country').get('value');
	const paymentType = form.get('paymentType').get('value');
	const fields = getFieldsForSubmission(country, paymentType);
	const payload = {
		screening: {},
		payment: {}
	};

	Object.keys(fields.screening).forEach(key => {
		const name = fields.screening[key].submitAs || key;
		payload.screening[name] = form.get(key).get('value');
	});

	Object.keys(fields.payment).forEach(key => {
		const name = fields.payment[key].submitAs || key;
		payload.payment[name] = form.get(key).get('value');
	});

	return payload;
};

export const submitFormToServer = (data, url) => {
	return fetch(url, {
		method: 'POST',
		credentials: 'same-origin',
		body: JSON.stringify(data),
		headers: new Headers({
			'Content-Type': 'application/json',
			'X-CSRF-Token': Application.CSRFToken,
			'Data-Type': 'json'
		})
	});
};

export const submitForm = ({ form, url }) => (dispatch) => {
	dispatch(formSubmitRequest);

	const payload = preparePayload(form);

	return submitFormToServer(payload, url)
		.then(response => {
			if (response.ok) {
				return response.json().then(parsed => {
					if (parsed.meta.code === 200) {
						dispatch(formSubmitSuccess);
					} else {
						dispatch(formSubmitError(parsed));
					}
				});
			} else {
				return response.json()
					.then(parsed => dispatch(formSubmitError(parsed)));
			}
		})
		.catch(error => dispatch(formSubmitError(error)));
};
