'use strict';

import _ from 'underscore';
import OnboardCollection from './onboard_collection';
import PhoneModel from './phone_model';

export default OnboardCollection.extend({
	model: PhoneModel,
	maxPhones: 3,
	initialize: function() {
		this.countryCodes = []
	},
	isLast: function(model) {
		return !_.isUndefined(model) && this.models.length > 0 && model === _.last(this.models);
	}, 
	getDefaultCountry: function() {
		return this.countryCodes.filter((country) => country.name === 'USA (+1)')[0];
	}
});
