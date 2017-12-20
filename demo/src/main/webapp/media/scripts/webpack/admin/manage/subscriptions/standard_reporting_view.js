'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import 'datatables.net';

export default Backbone.View.extend({
	el: '#standard_subscription_report_table',

	initialize () {
		this.options.standardReportingTable = this.buildDataTable();
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
			'sAjaxSource': '/admin/manage/subscriptions/reporting/standard/list',
			'sPaginationType': 'full_numbers',
			'iDisplayLength': 50,
			'aoColumnDefs': [
				{'aTargets': [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13], 'bSortable': false},
				{'aTargets': [0], 'mRender': this.cellRenderer('#company-name-tmpl')},
				{'aTargets': [4, 12, 13], 'mRender': this.cellRenderer('#currency-tmpl')},
				{'aTargets': [7], 'mRender': this.cellRenderer('#auto-renewall-tmpl')},
				{'aTargets': [10], 'mRender': this.cellRenderer('#current-tier-range-tmpl')}
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
