'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	initialize: function (options) {
		this.options = options;
	},

	url: function () {
		var recurrenceUrl = '/reports/custom/recurrence';
		if (this.options.savedReportKey && this.options.savedReportKey !== '') {
			recurrenceUrl += '/' + this.options.savedReportKey;
		}
		return recurrenceUrl;
	}
});

