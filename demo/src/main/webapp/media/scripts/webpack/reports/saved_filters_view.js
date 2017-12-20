'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmSelect from '../funcs/wmSelect';
import 'jquery-ui';
import '../dependencies/jquery.serializeObject';

export default Backbone.View.extend({
	el: '#fixed_filters_wrap',

	events: {
		'click .add_additional_filters'  : 'addFilters',
		'click  #work_report_regenerate' : 'saveFilters',
		'change .date-filter'            : 'toggleSelectDateFields'
	},

	initialize: function (options) {
		this.options = options || {};

	},

	render: function (model) {
		$('#work_report_filter_main').empty();

		if (model) {
			$('#work_report_filter_main').show();
			var workReportEntityBucketResponses = model.get('work_report_entity_bucket_responses');
			_.each(workReportEntityBucketResponses, function (workReportEntityBucket) {
				if (workReportEntityBucket.display_name !== null) {
					$('#work_report_entity_bucket-tmpl')
						.tmpl(_.extend(workReportEntityBucket, { mode: 'edit' }))
						.appendTo('#work_report_filter_main');
				}
			});
		} else {
			$('#work_report_filter_main').hide();
		}

		$('.date', this.el).not('.hasDatePicker').datepicker({dateFormat: 'mm/dd/yy', minDate: "-1Y"});
		wmSelect({
			selector: '[name*="multiselect"]',
			root: this.el
		});
		$('input[name*="workResourceName"]').autocomplete({
			minLength: 0,
			source: function (request, response) {
				$.getJSON('/reports/custom/suggest_users.json', {
					term: extractLast(request.term)
				}, response);
			},
			search: function () {
				// custom minLength
				var term = extractLast(this.value);
				if (term.length < 2) {
					return false;
				}
			},
			focus: function () {
				// prevent value inserted on focus
				return false;
			},
			select: function (event, ui) {
				var terms = split(this.value);
				// remove the current input
				terms.pop();
				// add the selected item
				terms.push(ui.item.value);
				// add placeholder to get the comma-and-space at the end
				terms.push('');
				this.value = terms.join(', ');
				return false;
			}
		});
	},

	toggleSelectDateFields: function (e) {
		var el = $(e.currentTarget);
		// toggle the date range selection on if FilteringTypeThrift.WORK_DATE_RANGE is selected
		el.next('.date-section').toggle(el.val() === '4');
	},


	addFilters: function () {
		this.options.router.navigate('step1', true);
	},

	saveFilters: function () {
		var url = '/reports/custom/report_filters.json?report_id=' + encodeURIComponent(this.options.savedReportKey);
		var data = JSON.stringify($('#work_report_filter_form').serializeObject());
		$.ajax({
			context: this,
			url: url,
			type: 'POST',
			data: data,
			contentType: 'application/json; charset=utf-8',
			dataType: 'json',
			success: function () {
				this.options.router.workReportResults();
				this.options.router.workReportDisplayViewRender();
			}
		});
	}
});

function split(val) {
	return val.split( /,\s*/ );
}

function extractLast(term) {
	return split(term).pop();
}
