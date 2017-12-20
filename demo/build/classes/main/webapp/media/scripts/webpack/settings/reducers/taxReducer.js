import { Map } from 'immutable';
import * as types from '../constants/actionTypes';
import taxFields from '../fields/taxFields';

const taxState = {};
Object.keys(taxFields).forEach((name) => {
	taxState[name] = Map({
		value: taxFields[name].defaultValue,
		error: ''
	});
});
const initialTaxState = Map(taxState);

export { initialTaxState };

const tax = (state = initialTaxState, { type, name, value, error }) => {
	switch (type) {
	case types.CHANGE_TAX_FIELD:
		return state
			.setIn([name, 'value'], value)
			.setIn([name, 'error'], error)
			.setIn([name, 'dirty'], true);
	case types.BLUR_TAX_FIELD:
		return state
			.setIn([name, 'value'], value)
			.setIn([name, 'error'], error)
			.setIn([name, 'blurred'], true);
	default:
		return state;
	}
};

export default tax;
