import { Map } from 'immutable';

import fields, { getFieldsForSubmission } from '../../fields';
import { initialState } from '../../configureStore/state';
import { preparePayload } from '../index';

describe('#preparePayload', () => {
	const placeholderValue = 'cc';
	let state;

	beforeEach(() => {
		state = Map(initialState);
		state.forEach((field, name) => {
			if (Map.isMap(field) && field.has('value') ) {
				state = state.setIn([name, 'value'], placeholderValue);
			}
		});
	});

	it('should return an object', () => {
		expect(typeof preparePayload(state)).toBe('object');
	});

	it('should return an object with properties relevant to the country of origin', () => {
		const country = state.get('country').get('value');
		const paymentType = state.get('paymentType').get('value');
		const formFields = getFieldsForSubmission(country, paymentType);
		const screeningFieldNames = Object.keys(formFields.screening)
			.map(name => formFields.screening[name].submitAs || name);
		const paymentFieldNames = Object.keys(formFields.payment)
			.map(name => formFields.payment[name].submitAs || name);
		const payload = preparePayload(state);

		expect(Object.keys(payload)).toEqual(['screening', 'payment']);
		expect(Object.keys(payload.screening)).toEqual(screeningFieldNames);
		expect(Object.keys(payload.payment)).toEqual(paymentFieldNames);
	});

	it('should preserve the field values after processing', () => {
		const payload = preparePayload(state);

		Object.keys(payload.screening).forEach(key => {
			expect(payload.screening[key]).toEqual(placeholderValue);
		});
		Object.keys(payload.payment).forEach(key => {
			expect(payload.payment[key]).toEqual(placeholderValue);
		});
	});
});
