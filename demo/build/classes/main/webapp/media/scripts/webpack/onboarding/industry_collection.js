'use strict';

import _ from 'underscore';
import OnboardCollection from './onboard_collection';
import IndustryModel from './industry_model';

export default OnboardCollection.extend({
	model: IndustryModel,

	isValid: function () {
		return _.some(this.models, function (model) { return model.get('checked'); }, this);
	}
});
