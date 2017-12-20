'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	initialize: function (options) {
		this.options = options;
	},

	url: function () {
		return '/reports/custom/report_filters.json?report_id=' + this.options.savedReportKey;
	}
});
