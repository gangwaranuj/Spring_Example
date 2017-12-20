'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import OnboardModel from './onboard_model';

export default Backbone.Collection.extend({
	model: OnboardModel,

	isValid: function () {
		return this.isOptional || _.some(this.models, function (model) { return model.isValid(); }, this);
	}
});
