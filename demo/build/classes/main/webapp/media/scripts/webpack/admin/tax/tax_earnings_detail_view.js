'use strict';

import $ from 'jquery';
import _ from 'backbone';
import Backbone from 'backbone';
import EarningsDetailReportView from './tax_earnings_report_view';
import EarningsReportDataView from './tax_earnings_report_data_view';

export default Backbone.View.extend({
	initialize () {
		// Fetch tax form reports
		$.getJSON('/admin/accounting/earnings_detail.json', (json) => {
			if (json.successful && !_.isNull(json.data)) {
				var reports = [],
					reportsPublished = [];

				_.each(json.data.earningDetailReports, function (rpt) {
					if (rpt.status.published) {
						reportsPublished.push(rpt);
					} else {
						reports.push(rpt);
					}
				});
				this.formView = new EarningsDetailReportView({
					earningDetailReportSets: reports,
					canPublish: this.options.canPublish
				});
				this.formPublished = new EarningsReportDataView({
					earningReportDetailSets: reportsPublished
				});
			}
		});
	},

	render () {
		return this;
	}
});
