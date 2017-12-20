'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	defaults: {
		companyLogo: null,
		companyName: '',
		testName: '',
		testDescription: '',
		testStatus: '',
		testDuration: 0
	}
});
