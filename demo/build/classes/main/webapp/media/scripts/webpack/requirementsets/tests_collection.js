'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: '/tests',

	comparator: function (test) {
		return test.get('name');
	}
});
