'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import 'datatables.net';
import '../../../dependencies/jquery.tmpl';

export default Backbone.View.extend({
	el: '#approval_queue_table',

	initialize () {
		this.options.approvalTable = this.buildDataTable();
	},

	deleteRow (trElement) {
		this.options.approvalTable.fnDeleteRow(trElement);
	},

	buildDataTable () {
		const cellRenderer = (template) => {
			return  (data, type, val, metaData) => {
				return $(template).tmpl({
					data,
					meta: this.options.meta[metaData.row]
				}).html();
			};
		};

		return $(this.el).dataTable({
			'bSort': true,
			'bFilter': false,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'bLengthChange': true,
			'sAjaxSource': '/admin/manage/subscriptions/queue',
			'sPaginationType': 'full_numbers',
			'iDisplayLength': 25,

			'aoColumnDefs': [
				{'aTargets': [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16], 'bSortable': false},

				{
					'aTargets': [0],
					'bUseRendered': false,
					'mRender': cellRenderer('#subscription-checkbox-tmpl')
				},

				{'aTargets': [1], 'mRender': cellRenderer('#subscription-id-tmpl')},
				{'aTargets': [10, 11, 12, 13, 14, 15, 16], 'mRender': cellRenderer('#currency-tmpl')},
				{'aTargets': [7], 'sWidth': '60px'},
				{'aTargets': [14], 'sWidth': '60px'}
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
