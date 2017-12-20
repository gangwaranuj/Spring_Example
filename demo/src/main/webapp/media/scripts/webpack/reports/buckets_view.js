'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmSelect from '../funcs/wmSelect';
import 'jquery-ui';

export default Backbone.View.extend({
	el: '#reporting_work_display',

	events: {
		'click .toggle_selectall'   : 'toggleSelectAll',
		'click .toggle_field'       : 'toggleField',
		'click .bucket h4'          : 'toggleExpandCollapse',
		'keyup input[type="text"]'  : 'activateFilter',
		'change input[type="text"]' : 'activateFilter',
		'change select'             : 'activateFilter',
		'change .date-filter'       : 'toggleSelectDateFields'
	},

	initialize: function (options) {
		this.options = options || {};
	},

	render: function (workReportEntityBuckets) {
		$('#dynamic_messages').hide();
		$('#work_report_filter_main').empty();
		$('#work_report_filter_form').hide();

		$('#outer-container .container-fluid').removeClass('container-fluid').addClass('container').css({'padding-left': '0'});
		if (workReportEntityBuckets) {
			if (workReportEntityBuckets.model != null) {
				$('#work_report_entity_buckets_main').empty();

				$.each(workReportEntityBuckets.model.models, function (index, entityBucket) {
					if (entityBucket.get('displayName') != null) {
						var bucket = $('#work_report_entity_bucket-tmpl')
							.tmpl(_.extend(entityBucket.toJSON(), {mode: 'add'}))
							.appendTo('#work_report_entity_buckets_main');
						if (index === 0) {
							bucket.find('.expand_collapse').trigger('click');
						}
					}
				});
			}

			this.customFieldsSelect = wmSelect({
				sortField: 'text',
				selector: '.custom-fields-multi-select'
			})[0].selectize;

			this.customFieldsSelect.on('change', _.bind(function (currentSelectedValues) {
				$('input[name="workCustomFields_60"]')[0].checked = currentSelectedValues;
			}, this));

			wmSelect({
				selector: '[name*="multiselect"]',
				root: '#work_report_entity_buckets_main'
			});

			$('input[name="0_63_workResourceName"]').autocomplete({
				minLength: 0,
				source: function (request, response) {
					$.getJSON('/reports/custom/suggest_users.json', {
						term: extractLast(request.term)
					}, response);
				},
				search: function () {
					// custom minLength
					var term = extractLast(this.value);
					return term.length >= 2;
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
			if (this.hasQueryString('isCopy')) {
				$('.report-type').html('Copy of Report');
				$('.copy-report').html('Copy Report');
			}
			$('#work_report_entity_bucket_form').show();
		} else {
			$('#work_report_entity_bucket_form').hide();
		}
		// Attach date pickers to fields that don't yet have one attached.
		$('.date', this.el).not('.hasDatePicker').datepicker({dateFormat: 'mm/dd/yy', minDate: "-1Y"});
	},

	hasQueryString: function (field) {
		var url = window.location.href;
		if (url.indexOf('?' + field + '=') !== -1) {
			return true;
		} else if (url.indexOf('&' + field + '=') !== -1) {
			return true;
		}
		return false
	},

	toggleSelectAll: function (event) {
		var el = $(event.target);

		if (el.is(':checked')) {
			el.closest('.section-wrapper').find('.toggle_field').prop('checked', true).trigger('change');
		} else {
			el.closest('.section-wrapper').find('.toggle_field').prop('checked', false).trigger('change');
		}
	},

	toggleSelectDateFields: function (e) {
		var el = $(e.currentTarget);
		// toggle the date range selection on if FilteringTypeThrift.WORK_DATE_RANGE is selected
		el.next('.date-section').toggle(el.val() === '4');
	},

	toggleField: function (e) {
		var el = $(e.target);
		var wrapper = el.closest('.section-wrapper');

		if (el.is(':checked') && $('.toggle_field:checked', wrapper).length === $('.toggle_field', wrapper).length) {
			wrapper.find('.toggle_selectall').prop('checked', true);
		} else {
			wrapper.find('.toggle_selectall').prop('checked', false);
		}
	},

	toggleExpandCollapse: function (e) {
		var el = $(e.target);
		var bucket = el.closest('.bucket');
		var fields = bucket.find('.section-wrapper');

		if (fields.is(':visible')) {
			fields.slideUp('fast');
			el.closest('h2').find('span').removeClass('ui-icon-circle-minus').addClass('ui-icon-circle-plus');
		} else {
			fields.slideDown('fast');
			el.closest('h2').find('span').addClass('ui-icon-circle-minus').removeClass('ui-icon-circle-plus');
		}
	},

	activateFilter: function (e) {
		var el = $(e.target);
		var lineitem = el.closest('.lineitem');

		lineitem.find('.toggle_field').prop('checked', true);
		// If they are changing a text input, set the date filter type to date range.
		if (el[0].nodeName === 'INPUT' && el.hasClass('date')) {
			lineitem.find('select').val(this.options.filterTypes.dateRange);
		}
	}

});

function split(val) {
	return val.split( /,\s*/ );
}

function extractLast(term) {
	return split(term).pop();
}

