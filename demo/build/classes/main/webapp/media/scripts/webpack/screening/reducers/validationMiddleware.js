import validate from 'validate.js';
import fields from '../fields';
import * as types from '../constants/actionTypes';
import { canadianSIN, americanSSN } from '../fields/customValidators';

validate.validators.canadianSIN = canadianSIN;
validate.validators.americanSSN = americanSSN;

const validationMiddleware = store => next => action => {
	if (action.name) {
		const field = store.getState().get(action.name);

		if (
			(field.get('blurred') && field.get('dirty'))
			|| (action.type === types.BLUR_FIELD && field.get('dirty'))
		) {
			let valueToBeValidated;

			if ('value' in action) {
				valueToBeValidated = action.value
			} else {
				valueToBeValidated = field.get('value');
			}

			const validationResult = validate(
				{ [action.name]: valueToBeValidated },
				{ [action.name]: fields[action.name].constraints },
				{ fullMessages: false }
			);

			if (validationResult) {
				action.error = validationResult[action.name][0];
			} else {
				action.error = '';
			}
		}
	}

	return next(action);
};

export default validationMiddleware;
