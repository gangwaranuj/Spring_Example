'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	initialize: function (options) {
		this.options = options;
	}
});
