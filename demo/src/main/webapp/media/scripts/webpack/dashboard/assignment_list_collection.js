'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	initialize: function (options) {
		this.options = options;
	}
});

