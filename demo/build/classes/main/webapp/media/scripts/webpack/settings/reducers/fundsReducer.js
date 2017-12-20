import { Map } from 'immutable';
import * as types from '../constants/actionTypes';
import fundsFields from '../fields/fundsFields';

const fundsState = {};

Object.keys(fundsFields).forEach((name) => {
	fundsState[name] = Map({
		value: fundsFields[name].defaultValue,
		error: '',
		dirty: false,
		blurred: false
	});
});
const initialFundsState = Map(fundsState);
export { initialFundsState };

const funds = (state = initialFundsState, { type, name, value, error }) => {
	switch (type) {
	case types.CHANGE_FUNDS_FIELD:
		return state
			.setIn([name, 'value'], value)
			.setIn([name, 'error'], error)
			.setIn([name, 'dirty'], true);
	case types.BLUR_FUNDS_FIELD:
		return state
			.setIn([name, 'value'], value)
			.setIn([name, 'error'], error)
			.setIn([name, 'blurred'], true);
	default:
		return state;
	}
};

export default funds;
