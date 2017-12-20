'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: function () {
		var base = '/assignments/fetch_status';
		if (this.isNew()) {
			return base;
		} else {
			return base + (base.charAt(base.length - 1) === '/' ? '' : '/') + this.id;
		}
	}
});

