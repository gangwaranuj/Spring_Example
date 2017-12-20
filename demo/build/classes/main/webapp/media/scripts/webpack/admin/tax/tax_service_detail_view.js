'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import TaxServiceDetailReportView from './tax_service_detail_report_view';
import TaxServiceDetailReportDataView from './tax_service_detail_report_data_view';

export default Backbone.View.extend({
	initialize: function () {
		// Fetch tax form reports
		var self = this;
		$.getJSON('/admin/accounting/tax_service_detail.json', function (json) {
			if (json.successful && !_.isNull(json.data)) {
				var reports = [];
				var reportsPublished = [];

				_.each(json.data.taxServiceDetailReports, function(rpt) {
					if (rpt.status.published) {
						reportsPublished.push(rpt);
					} else {
						reports.push(rpt);
					}
				});
				self.formView = new TaxServiceDetailReportView({
					taxServiceDetailReportSets: reports,
					canPublish: self.options.canPublish
				});
				self.formPublished = new TaxServiceDetailReportDataView({
					taxServiceReportDetailSets: reportsPublished
				});
			}
		});
	},

	render: function () {
		return this;
	}
});
