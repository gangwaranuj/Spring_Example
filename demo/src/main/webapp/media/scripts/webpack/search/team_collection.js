'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	initialize: function (options) {
		this.companyId = options.companyId;
		this.companyNumber = options.companyNumber;
	},

	url: function () {
		return `/search/retrieve?company=${this.companyId}&userTypes=WORKER`;
	},

	parse: function (data) {
		this.resultsCount = data.results_count;
		return data.results;
	}
});
