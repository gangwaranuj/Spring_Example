'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import OnboardModel from './onboard_model';
import ImageModel from './image_model';
import splitCamelCase from '../funcs/wmSplitCamelCase';
import wmPatterns from '../funcs/wmPatterns';

export default OnboardModel.extend({
	defaults: {
		individual: true
	},
	externalFields: ['logo'],
	requiredFields: ['companyName','companyOverview','companyWebsite','companyYearFounded'],
	currentYear: (new Date()).getFullYear(),
	startingFoundedYear: 1900,

	fields: function () {
		return _.union(_.keys(this.defaults), this.requiredFields, this.externalFields);
	},

	initialize: function (attrs) {
		this.logo = new ImageModel();

		this.id = attrs.id;
	},

	parse: function (response) {
		// Grab all possible values for the number of employees
		if (response.logo) {
			if (response.logo.url) {
				this.logo.set(_.omit(response.logo, _.isNull));
			}
			delete response.logo;
		}

		return _.omit(response, _.isNull);
	},

	validate: function (attributes) {
		if (!attributes.individual) {
			var errors = _.reduce(this.requiredFields, function (memo, field) {
				// We're removing the "Company" word from the error label to make
				// it fit nicely within the form.
				var name = splitCamelCase(field).replace('Company ', '');

				if (_.isUndefined(attributes[field]) || _.isNull(attributes[field]) || (_.isString(attributes[field]) && _.isEmpty(attributes[field]))) {
					memo.push({
						name: field,
						message: name + ' is a required field.'
					});
				}

				return memo;
			}, [], this);

			if (!wmPatterns.url.test(attributes.companyWebsite)) {
				errors.push({
					name: 'companyWebsite',
					message: 'Website is not a valid URI.'
				});
			}

			if (_.has(attributes, 'companyOverview') && attributes.companyOverview && attributes.companyOverview.length > 1000) {
				errors.push({
					name: 'companyOverview',
					message: 'Company Overview must be 1000 characters or less.'
				});
			}

			if (!_.isEmpty(errors)) {
				return errors;
			}
		}
	},

	isValid: function () {
		var legalModelIsValid = Backbone.Model.prototype.isValid.call(this),
		legalSubModelsAreValid = _.every(this.externalFields, function (field) { return this[field].isValid(); }, this);

		return this.get('individual') || legalModelIsValid && legalSubModelsAreValid;
	}

});
