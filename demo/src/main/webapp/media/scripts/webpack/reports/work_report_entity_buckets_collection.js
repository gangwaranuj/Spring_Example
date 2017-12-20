'use strict';

import Backbone from 'backbone';
import Model from './entity_buckets_model';

export default Backbone.Collection.extend({
	model: Model,

	initialize: function(options) {
		this.options = options;
	},

	url: function () {
		var workReportEntityBuckets = '/reports/custom/get_work_report_entity_buckets';
		if (this.options.reportKey && this.options.reportKey !== '') {
			workReportEntityBuckets += '/' + this.options.reportKey;
		}
		return workReportEntityBuckets;
	}
});
