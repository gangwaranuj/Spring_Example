'use strict';

import _ from 'underscore';
import OnboardModel from './onboard_model';
import PhonesCollection from './phones_collection';
import ImageModel from './image_model';
import LegalModel from './legal_model';
import splitCamelCase from '../funcs/wmSplitCamelCase';
import wmPatterns from '../funcs/wmPatterns';

export default OnboardModel.extend({
	step: 1,
	requiredFields: ['email', 'firstName', 'lastName'],
	externalFields: ['phones','avatar', 'legal'],

	fields: function () {
		return _.union(this.requiredFields, this.externalFields, ['gender', 'countryCodes']);
	},

	initialize: function (attrs) {
		this.phones = new PhonesCollection();
		this.avatar = new ImageModel();
		this.legal  = new LegalModel({ id: this.id });
		this.countryCodes = [];

		this.id = attrs.id;
	},

	parse: function (response) {
		// The response object has all 3 phone types set, even if they're empty.
		// We'll fetch the type from each one to populate the phone type dropdown
		this.phones.phoneTypes = _.pluck(response.phones, 'type');

		// We want to remove any empty phone numbers.
		response.phones = _.reject(response.phones, function (phone) { return _.isEmpty(phone.number) || _.isNull(phone.number); });
		// Remove any phone number formatting
		_.each(response.phones, function (phone) { phone.number = phone.number.replace(/[\s\(\)\-]*/g, ''); });
		// Remove any null values
		_.each(response.phones, function (phone) {
			_.each(phone, function (value, key) {
				if (_.isNull(value)) {
					delete phone[key];
				}
			});
		});

		// Handle the empty edge case... we always want to have at least one
		// phone number row for the user.
		if (_.isEmpty(response.phones)) { response.phones = {}; }

		// Populate our phones collection
		this.phones.reset(response.phones, { validate: false });
		delete response.phones;

		if (response.avatar) {
			if (response.avatar.url) {
				this.avatar.set(response.avatar);
			}
			delete response.avatar;
		}

		if (response.countryCodes) {
			this.countryCodes = response.countryCodes;
			this.phones.countryCodes = response.countryCodes;
			delete response.countryCodes;
		}

		return _.omit(response, _.isNull);
	},

	validate: function (attributes) {
		var errors = _.reduce(this.requiredFields, function (memo, field) {
			if (!_.has(attributes, field) || _.isEmpty(attributes[field])) {
				memo.push({
					name: field,
					message: splitCamelCase(field) + ' is a required field.'
				});
			}

			return memo;
		}, []);

		if (_.has(attributes, 'firstName') && attributes.firstName.length > 50) {
			errors.push({
				name: 'firstName',
				message: 'First Name must be 50 characters or less.'
			});
		}

		if (_.has(attributes, 'lastName') && attributes.lastName.length > 50) {
			errors.push({
				name: 'lastName',
				message: 'Last Name must be 50 characters or less.'
			});
		}

		if (!wmPatterns.email.test(attributes.email)) {
			errors.push({
				name: 'email',
				message: 'Please enter a valid email address'
			});
		}

		if (!_.isEmpty(errors)) {
			return errors;
		}
	},

	isValid: function () {
		var profileInfoModelIsValid = Backbone.Model.prototype.isValid.call(this),
			profileInfoSubModelsAreValid = _.every(this.externalFields, function (field) { return this[field].isValid(); }, this);

		return profileInfoModelIsValid && profileInfoSubModelsAreValid;
	},

	/* The default `update` function on OnboardModel saves all the attributes,
	 * however on this slide we only want to save the `individual` attribute
	 * if its set to true.
	 */
	update: function () {
		if (this.legal.get('individual')) {
			_.each(_.keys(this.legal.omit(['individual','id'])), this.legal.unset, this.legal);
		}

		var externalObjects = _.map(this.legal.externalFields, function (field) { return this[field]; }, this.legal),
			externalJSON = _.map(externalObjects, function (object) { return object.toJSON(); }),
			attributes = _.defaults(_.object(this.legal.externalFields, externalJSON), this.legal.toJSON());

		this.set(attributes);

		return OnboardModel.prototype.update.call(this);
	}
});
