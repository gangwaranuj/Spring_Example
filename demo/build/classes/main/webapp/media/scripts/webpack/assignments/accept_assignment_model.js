'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({

	initialize: function (options) {
		this.options = options;
	},

	url: function () {
		return '/assignments/accept_work_on_behalf/' + this.get('workId');
	}
});
