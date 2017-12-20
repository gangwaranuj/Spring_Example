'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import 'datatables.net';

export default Backbone.View.extend({
	el: '#usage_subscription_report_table',

	initialize () {
		this.options.usageReportingTable = this.buildDataTable();
	},

	cellRenderer (template) {
		return  (data, type, val, metaData) => {
			return $(template).tmpl({
				data,
				meta: this.options.meta[metaData.row]
			}).html();
		};
	},

	buildDataTable () {
		return $(this.el).dataTable({
			'bSort': true,
			'bFilter': false,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'bLengthChange': true,
			'sAjaxSource': '/admin/manage/subscriptions/reporting/usage/list',
			'sPaginationType': 'full_numbers',
			'iDisplayLength': 50,
			'aoColumnDefs': [
				{'aTargets': [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10], 'bSortable': false},
				{'aTargets': [0], 'mRender': this.cellRenderer('#company-name-tmpl')},
				{'aTargets': [2, 3, 5, 7, 8, 9, 10], 'mRender': this.cellRenderer('#currency-tmpl')},
				{'aTargets': [4, 6], 'mRender': this.cellRenderer('#percent-tmpl')}
			],

			'fnServerData': (sSource, aoData, fnCallback) => {
				$.getJSON(sSource, aoData, (json) => {
					this.options.meta = json.aMeta;
					fnCallback(json);
				});
			}
		});
	}
});
