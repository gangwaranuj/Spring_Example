'use strict';

import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.Model.extend({
	defaults: {
		name: '',
		locationNumber: '',
		address1: '',
		address2: '',
		city: '',
		state: '',
		postalCode: '',
		country: ''
	},

	parse: function (rawLocationData) {
		if (!_.has(rawLocationData, 'postalCode')) {
			var message = 'Location is not provided.';
			if (rawLocationData.isSuppliedByWorker) {
				message = 'Worker is supplying parts.';
			} else if (rawLocationData.distType === 'WORKER' && !rawLocationData.isReturn) {
				message = 'Location will be known once assignment is assigned to a worker.';
			}
			return _.extend(rawLocationData, { message: message });
		} else {
			return rawLocationData;
		}
	},

	format: function (localJSON) {
		if (!_.isEmpty(localJSON.message)) { return localJSON.message; }

		// filter out the properties the users shouldn't see
		var filteredJSON = _.pick(localJSON, _.keys(this.defaults));

		var formatted = _.map(filteredJSON, function (value, key, location) {
			if (!_.contains(['city', 'state', 'country'], key) && !_.isEmpty(value)) {
				return _.isEqual('locationNumber', key) ? value = '(' + location.locationNumber + ')<br/>' : value += '<br/>';
			} else if (_.contains(['city'], key)) {
				return value += ', ';
			} else {
				return value += ' ';
			}
		});
		_.isEmpty(formatted.locationNumber) ? formatted.name += '<br/>' : formatted.name += ' ';
		return _.reduce(formatted, function (memo, value) {
			return memo + value;
		});
	}
});
