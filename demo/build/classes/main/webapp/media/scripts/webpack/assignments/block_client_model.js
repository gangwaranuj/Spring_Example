'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	initialize: function (options) {
		this.options = options;
	},

	url: function () {
		return '/assignments/block_client/' + this.get('workId');
	}
});