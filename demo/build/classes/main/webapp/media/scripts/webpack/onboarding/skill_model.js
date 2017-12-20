'use strict';

import _ from 'underscore';
import OnboardModel from './onboard_model';

export default OnboardModel.extend({
	defaults: {
		id: 0,
		name: "",
		recommended: false,
		type: "SKILL"
	},

	validate: function (attributes) {
		return true;
	}
});
