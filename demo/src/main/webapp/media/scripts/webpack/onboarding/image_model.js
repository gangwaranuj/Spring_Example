'use strict';

import OnboardModel from './onboard_model';

export default OnboardModel.extend({
	defaults: {
		image: null,
		filename: null,
		coordinates: null,
		url: null
	}
});
