'use strict';

import _ from 'underscore';
import OnboardModel from './onboard_model';

export default OnboardModel.extend({
	defaults: {
		checked: false
	},

	validate: function (attributes) {
		var errors = _.reduce(attributes, function (memo, value, key) {
			var isEmpty = _.isString(value) && _.isEmpty(value),
				error = { name: key };

			if (isEmpty) {
				error.message = 'Industry ' + key + ' is a required field.';
			} else if (key === 'name' && !_.isString(value)) {
				error.message = 'Industry name needs to be a string.';
			} else if (key === 'id' && !_.isNumber(value)) {
				error.message = 'Industry id needs to be a number.';
			} else if (key === 'id' && value < 0) {
				error.message = 'Industry id cannot be negative.';
			} else if (key === 'checked' && !_.isBoolean(value)) {
				error.message = 'Industry checked needs to be a boolean.';
			}

			if (_.has(error, 'message')) {
				memo.push(error);
			}

			return memo;
		}, [], this);

		if (!_.isEmpty(errors)) {
			return errors;
		}
	}
});
