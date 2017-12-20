'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	sync: Backbone.syncWithJSON,

	initialize: function (models, options) {
		this.options = options;
	},

	url: function () {
		return '/assignments/bundle_overview/' + this.options.parentId;
	},

	parse: function (res) {
		this.overview = res.data.overview;
		return res.data.assignments;
	}

});
