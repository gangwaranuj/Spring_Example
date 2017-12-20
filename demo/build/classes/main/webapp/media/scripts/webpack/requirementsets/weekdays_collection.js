'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: '/weekdays',

	comparator: function (weekday) {
		return weekday.get('id');
	}
});
