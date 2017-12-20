'use strict';

import _ from 'underscore';
import OnboardModel from './onboard_model';
import splitCamelCase from '../funcs/wmSplitCamelCase';

export default OnboardModel.extend({
	defaults: {
		maxTravelDistance: 150,
		numberOfNearbyAssignments: 0
	},
	step: 2,
	requiredFields: ['address'],
	locationFields: ['address1', 'city', 'stateShortName', 'countryIso', 'postalCode'],
	requiredLocationFields: ['address1', 'countryIso'],

	initialize: function (attrs) {
		this.id = attrs.id;
	},

	fields: function () {
		return _.union(_.keys(this.defaults), this.locationFields, this.requiredFields);
	},

	parse: function (response) {
		// Generate our formatted address for the Google autocomplete
		var addressComponents = _.omit(_.pick(response, this.locationFields), _.isEmpty);
		response.address = _.values(addressComponents).join(', ');

		return _.omit(response, _.isNull);
	},

	validate: function (attributes) {
		var errors = _.reduce(this.requiredLocationFields, function (memo, field) {
			var name = splitCamelCase(field);

			// Let's clean up the error messages to be a bit more user friendly
			name = name === 'Address1' ? 'Street Address' : name;
			name = name === 'Country Iso' ? 'Country Code' : name;
			name = name === 'State Short Name' ? 'State Abbreviation' : name;

			if (_.isUndefined(attributes[field]) || _.isEmpty(attributes[field])) {
				memo.push({
					name: 'address',
					message: 'Cannot find a valid ' + name + '.'
				});
			}

			return memo;
		}, [], this);

		errors = _.reduce(this.requiredFields, function (memo, field) {
			var name = splitCamelCase(field);

			if (_.isEmpty(attributes[field])) {
				memo.push({
					name: field,
					message: name + ' is a required field.'
				});
			}

			return memo;
		}, errors, this);

		if (!_.isEmpty(errors)) {
			return errors;
		}
	}
});
