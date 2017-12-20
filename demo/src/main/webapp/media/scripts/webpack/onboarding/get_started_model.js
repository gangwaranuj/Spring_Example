'use strict';

import OnboardModel from './onboard_model';

export default OnboardModel.extend({
	step: 4,

	defaults: {
		isLastStep: true
	},

	initialize: function (attrs) {
		this.id = attrs.id;
	}
});
