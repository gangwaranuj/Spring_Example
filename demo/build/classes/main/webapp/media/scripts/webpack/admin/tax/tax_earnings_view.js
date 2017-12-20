'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import EarningsReport from './tax_earnings_report_view';
import EarningsReportData from './tax_earnings_report_data_view';

export default Backbone.View.extend({

	initialize: function (options) {
		// Fetch tax form reports
		var self = this;
		$.getJSON('/admin/accounting/earnings.json', function (json) {
			if (json.successful && !_.isNull(json.data)) {
				var reports = [];
				var reportsPublished = [];

				_.each(json.data.earningReports, function(rpt) {
					if (rpt.status.published) {
						reportsPublished.push(rpt);
					} else {
						reports.push(rpt);
					}
				});
				self.formView = new EarningsReport({earningReportSets: reports, canPublish: self.options.canPublish});
				self.formPublished = new EarningsReportData({earningReportSets: reportsPublished});
			}
		});
	},

	render: function () {
		return this;
	}
});
