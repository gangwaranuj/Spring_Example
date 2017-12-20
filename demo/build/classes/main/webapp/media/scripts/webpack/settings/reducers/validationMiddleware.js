/* eslint-disable no-param-reassign */
import validate from 'validate.js';
import fields from '../fields';

const validationMap = {
	CHANGE_FUNDS_FIELD: 'funds',
	BLUR_FUNDS_FIELD: 'funds',
	CHANGE_TAX_FIELD: 'tax',
	BLUR_TAX_FIELD: 'tax'
};

const validationMiddleware = store => next => (action) => {
	if (
		action.name
		&& action.type !== 'CHANGE_LOCATION_FIELD'
		&& action.type !== 'CHANGE_CREDIT_CARD_FIELD'
		&& action.type !== 'CHANGE_ADD_EMPLOYEE_FIELD'
		&& action.type !== 'CHANGE_ASSIGNMENT_PREFERENCES_FIELD'
		&& action.type !== 'CHANGE_FIRST_ASSIGNMENT_FIELD'
		&& action.type !== 'CHANGE_FIRST_ASSIGNMENT_TEMPLATE_FIELD'
		&& action.type !== 'CHANGE_FIELD'
	) {
		const field = store.getState()[validationMap[action.type]].get(action.name);

		if ((field.get('blurred') || (/BLUR/).test(action.type)) && field.get('dirty')) {
			const validationResult = validate(
				{ [action.name]: action.value },
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
