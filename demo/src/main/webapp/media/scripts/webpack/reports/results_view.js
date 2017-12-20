'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import 'datatables.net';
import 'jquery-ui';
import wmModal from '../funcs/wmModal';

export default Backbone.View.extend({
	el: '#reporting_work_display',

	events: {
		'click #work_report_export' : 'downloadReport'
	},

	initialize: function (options) {
		this.options = options || {};
		this.filenameHash = '';
		$('#toggle-filters').click(this.toggleFiltersPane);
	},

	render: function () {
		// Scroll back to top of page.
		window.scrollTo(0, 0);
		$('#dynamic_messages').hide();
		var cellRenderer = function (template) {
			return function (row) {
				return $(template).tmpl({
					data: row.aData[row.iDataColumn],
					meta: meta[row.iDataRow]
				}).html();
			};
		};

		$('#results_spinner').show();
		$.ajax({
			dataType: 'json',
			type: 'GET',
			url: '/reports/custom/report_data.json?report_id=' + this.options.savedReportKey,
			success: function (data) {
				$('#results_spinner').hide();
				$('#report_display').dataTable({
					'aaData': data.aaData,
					'aoColumnDefs': data.aoColumnDefs,
					'sPaginationType': 'full_numbers',
					'bLengthChange': true,
					'bFilter': false,
					'bStateSave': false,
					'bProcessing': true,
					'bServerSide': false,
					'iDisplayLength': 50,
					'bDestroy': true,
					'bAutoWidth': false
				});

				//adding the toggle icon to column headers
				$('th').prepend('<i class="reports_icon icon-sort icon-small">');
			},
			error: function () {
				$('#results_spinner').hide();
			}
		});
		$('#outer-container .container').removeClass('container').addClass('container-fluid').attr('name', 'reporting-container');
		$('[name$="reporting-container"]').css({'padding-left': '40px'});
		$('.date', this.el).not('.hasDatePicker').datepicker({dateFormat: 'mm/dd/yy', minDate: "-1Y"});
	},

	checkIterator: function (index) {
		return (index % 2 === 0);
	},

	downloadReport: function () {
		$.ajax({
			url: '/reports/custom/export_saved_to_csv?report_id=' + this.options.savedReportKey,
			success: function (response) {
				wmModal({
					autorun: true,
					title: 'Email Report',
					destroyOnClose: true,
					content: response,
					controls: [
						{
							text: 'Close',
							close: true,
							classList: ''
						}
					]
				});
			}
		});
	},

	toggleFiltersPane: function () {
		var el = $('#fixed_filters_wrap');
		if (el.is(':visible')) {
			el.hide();
			$('#toggle-filters').html('Show filters');
		}
		else {
			el.show();
			$('#toggle-filters').html('Hide filters');
		}
	}

});
