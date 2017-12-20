'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	initialize: function (models, options) {
		Backbone.Collection.prototype.initialize.call(this, models);
		this.options = options;
	},
	url: function () {
		return '/assignments/' + this.options.id + '/followers';
	},
	parse: function (response) {
		return response.results;
	}
});
