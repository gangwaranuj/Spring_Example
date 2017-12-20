'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import 'datatables.net';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'change #assessments_filter_form input' : 'filterChange'
	},

	initialize: function () {
		var meta;
		const cellRenderer = (template) => {
			return  (data, type, val, metaData) => {
				return $(template).tmpl({
					data,
					meta: meta[metaData.row]
				}).html();
			};
		};

		this.datatableObj = $('#surveys_worker_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': false,
			'bFilter': false,
			'bRetrieve': true,
			'iDisplayLength': 50,
			'aoColumnDefs': [
				{'mRender': cellRenderer('#cell-name-tmpl'), 'aTargets': [0]},
				{'mRender': cellRenderer('#cell-status-tmpl'), 'aTargets': [2]},
				{'bSortable': false, 'aTargets': [2]}
			],
			'bProcessing': true,
			'bServerSide': true,
			'sAjaxSource': '/lms/view/surveys.json',
			'fnServerData': function (sSource, aoData, fnCallback) {
				$.each($('#assessments_filter_form').serializeArray(), function (i, item) {
					aoData.push(item);
				});

				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});
	},

	filterChange: function () {
		this.datatableObj.fnDraw();
	}
});
