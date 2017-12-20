'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: '/lms/manage/tests.json',

	comparator: function (test) {
		return test.get('name');
	},

	parse: function (response) {
		return response;
	}
});
