'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import View from './details_view';
import wmSelect from '../funcs/wmSelect';
import wmNotify from '../funcs/wmNotify';
import 'datatables.net';
import 'jquery-ui';

export default Backbone.Router.extend({
	routes: {
		'reports'                     : 'index',
		'reports/assignment_feedback' : 'assignmentFeedback',
		'reports/transactions'        : 'transactions',
		'reports/buyer'               : 'buyer',
		'reports/budget'              : 'budget',
		'reports/resource'            : 'resource'
	},

	index: function () {
		new View();
	},

	assignmentFeedback: function () {
		$('.datepicker').datepicker();

		var cellRenderer = function (template) {
			return function (row) {
				return $(template).tmpl({
					data: row.aData[row.iDataColumn],
					meta: meta[row.iDataRow]
				}).html();
			};
		};

		var meta;

		$('#report_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': false,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'iDisplayLength': 25,
			'aoColumnDefs': [
				{'fnRender': cellRenderer('#cell-title-tmpl'), 'aTargets': [1]},
				{'fnRender': cellRenderer('#rating-cell-tmpl'), 'aTargets': [6]},
				{'bSortable': false, 'aTargets': [7, 8]}
			],
			'sAjaxSource': '/reports/assignment_feedback.json',
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				$.each($('#report_filters').serializeArray(), function (i, item) {
					if (item.value !== 0) {
						aoData.push(item);
					}
				});
				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});

		$('#apply-filter-outlet').on('click', function () {
			$('#report_filters').trigger('submit');
		});

		$('#export-outlet').on('click', function () {
			var listFilters = $('#report_filters').serialize();
			$(this).attr('href', $(this).attr('href') + '?' + listFilters);
			return true;
		});

		$('#cta-toggle-filters').on('click', function () {
			if ($('#advanced-filters').is(':visible')) {
				$('#advanced-filters').hide();
				$('#cta-toggle-filters').html('Show Filters');
			} else {
				$('#advanced-filters').show();
				$('#cta-toggle-filters').html('Hide Filters');
			}
		});

		$(document).on('click', '.flag-rating', function (event) {
			$.post('/ratings/flag/' + $(event.currentTarget).data('id'))
				.done(function (response) {
					wmNotify({
						message: response.message
					});
				})
				.fail(function (response) {
					wmNotify({
						message: response.message,
						type: 'danger'
					});
				});
		});
	},

	transactions: function () {
		wmSelect();

		$('.datepicker').datepicker();

		var cellRenderer = function (template) {
			return function (row) {
				return $(template).tmpl({
					data: row.aData[row.iDataColumn],
					meta: meta[row.iDataRow]
				}).html();
			};
		};

		var meta;

		$('#report_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bAutoWidth' : false,
			'bLengthChange': true,
			'bFilter': false,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'iDisplayLength': 25,
			'aoColumnDefs': [
				{'fnRender': cellRenderer('#cell-title-tmpl'), 'aTargets': [6]},
				{'bSortable': false, 'aTargets': [5,6,7,8,9,10,11]}
			],
			'sAjaxSource': '/reports/transactions.json',
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				$.each($('#report_filters').serializeArray(), function (i, item) {
					if (item.value !== 0) {
						aoData.push(item);
					}
				});
				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});

		$('#apply-filter-outlet').on('click', function () {
			$('#report_filters').trigger('submit');
		});

		$('#export-outlet').on('click', function () {
			$(this).attr('href', $(this).attr('href') + '?' + $('#report_filters').serialize());
			return true;
		});

		$('#cta-toggle-filters').on('click', function () {
			if ($('#advanced-filters').is(':visible')) {
				$('#advanced-filters').hide();
				$('#cta-toggle-filters').html('Show Filters');
			} else {
				$('#advanced-filters').show();
				$('#cta-toggle-filters').html('Hide Filters');
			}
		});
	},

	budget: function () {
		this.standard('budget');
	},

	buyer: function () {
		this.standard('buyer');
	},

	resource: function () {
		this.standard('resource');
	},

	standard: function (reportType) {
		wmSelect();

		$('.datepicker').datepicker();

		$('#report_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bAutoWidth' : false,
			'bLengthChange': true,
			'bFilter': false,
			'bStateSave': true,
			'bProcessing': true,
			'bServerSide': true,
			'iDisplayLength': 25,
			'aaSorting': [[12, 'desc']],
			'aoColumnDefs': [
				{'sClass': 'titlecase', 'aTargets': [6]},
				{'bSortable': true, 'aTargets': [0,1,2,3,4,5,8,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29]}
			],
			'sAjaxSource': '/reports/' + reportType,
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				$.each($('#report_filters').serializeArray(), function (i, item) {
					// Don't include empty values.
					if (item.value !== 0) {
						aoData.push(item);
					}
				});

				$.post(sSource, aoData, function (json) {
					var meta = json.aMeta;
					for (var i = 0, size = json.aaData.length; i < size; i++) {
						json.aaData[i][0]  = '<a href="/assignments/details/'+meta[i].work_number+'" target="_blank">' + json.aaData[i][0] + '<\/a>';
						json.aaData[i][1]  = '<a href="/assignments/details/'+meta[i].work_number+'" target="_blank">' + json.aaData[i][1] + '<\/a>';
						json.aaData[i][21] = '<div class="tar">' + json.aaData[i][21] + '<\/div>';
						json.aaData[i][22] = '<div class="tar">' + json.aaData[i][22] + '<\/div>';
						json.aaData[i][23] = '<div class="tar">' + json.aaData[i][23] + '<\/div>';
					}
					fnCallback(json);
				}, 'json');
			}
		});

		$('#apply-filter-outlet').on('click', function () {
			$.each(['from', 'to'], function (i, item) {
				$('#' + item + '-date-outlet').html($.datepicker.formatDate('MM d, yy', $('#' + item + '_date_filter').datepicker('getDate')));
			});

			$('#report_filters').trigger('submit');
		});

		$('#export-outlet').on('click', function () {
			$(this).attr('href', $(this).attr('href') + '?' + $('#report_filters').serialize());
			return true;
		});

		$('#cta-toggle-filters').on('click', function () {
			if ($('#advanced-filters').is(':visible')) {
				$('#advanced-filters').hide();
				$('#cta-toggle-filters').html('Show Filters');
			} else {
				$('#advanced-filters').show();
				$('#cta-toggle-filters').html('Hide Filters');
			}
		});
	}
});
