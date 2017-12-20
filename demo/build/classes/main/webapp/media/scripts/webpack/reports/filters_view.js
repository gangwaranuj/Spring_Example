'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmSelect from '../funcs/wmSelect';

export default Backbone.View.extend({
	el: '#reporting_work_display',

	events: {
		'click .add_additional_filters' : 'addFilters',
		'click #work_report_regenerate' : 'workReportFiltersNext',
		'keyup input[type="text"]'      : 'syncFilterValue',
		'change input[type="text"]'     : 'syncFilterValue',
		'change select'                 : 'syncFilterValue',
		'click #work_report_export'     : 'downloadReport',
		'change .toggle_field'          : 'toggleFilter'
	},

	initialize: function (options) {
		this.options = options || {};
		this.filenameHash = '';
		$('#toggle-filters').on('click', this.toggleFiltersPane);
	},

	render: function (filteringEntityCompositeResponse) {
		// Scroll back to top of page.
		window.scrollTo(0, 0);

		$('#dynamic_messages').hide();

		this.filenameHash = filteringEntityCompositeResponse.get('reportUri');

		var workReportEntityBucketsCompositeResponse = filteringEntityCompositeResponse.get('workReportEntityBucketsCompositeResponse');
		if (workReportEntityBucketsCompositeResponse) {
			$('#work_report_entity_bucket_form').hide();
			$('#work_report_filter_main').empty();
			$('#work_report_display').empty();

			$('#outer-container .container').removeClass('container').addClass('container-fluid').attr('name', 'reporting-container');
			$('[name$="reporting-container"]').css({'padding-left': '40px', 'padding-top': '120px'});

			var workReportEntityBucketResponses = workReportEntityBucketsCompositeResponse.workReportEntityBucketResponses;
			_.each(workReportEntityBucketResponses, function (workReportEntityBucket) {
				if (workReportEntityBucket.displayName != null) {
					$('#work_report_entity_bucket-tmpl')
						.tmpl(_.extend(workReportEntityBucket, { mode: 'edit' }))
						.appendTo('#work_report_filter_main');
				}
			});

			$('#work_report_display-tmpl').tmpl({
				headers: filteringEntityCompositeResponse.get('reportHeader').reportFields,
				rows: filteringEntityCompositeResponse.get('reportRow'),
				iterator: this.checkIterator
			}).appendTo('#work_report_display');

			// Attach date pickers to fields that don't yet have one attached.
			$('.date', this.el).not('.hasDatePicker').datepicker({dateFormat: 'mm/dd/yy', minDate: "-1Y"});

			wmSelect({
				selector: '[name*="multiselect"]',
				root: '#fixed_filters'
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
			$('#work_report_filter_form').show();

			// Add total number of records.
			$('#assignments_paging_total').html(filteringEntityCompositeResponse.get('paginationThrift').total);

			// Fixed position of some important elements.
			this.initializeFixedPositioning();
		} else {
			this.options.router.navigate('step1', true);
		}
	},

	checkIterator: function (index) {
		return (index % 2 === 0);
	},

	workReportFiltersNext: function (e) {
		e.preventDefault();

		this.options.router.workReportFilters();
	},

	addFilters: function () {
		this.options.router.navigate('step1', true);
	},

	toggleFilter: function (event) {
		let el = $(event.target);
		let dateFilter = $('.date-filter', el.parent().parent().parent());
		dateFilter.prop('disabled', !el.prop('checked'))
			.prop("selectedIndex", el.prop('checked') ? dateFilter.prop("selectedIndex") : 0)
			.change()
			.focus();
	},

	syncFilterValue: function (event) {
		var el = $(event.target);
		var name = el.attr('name');

		if (el[0].nodeName === 'SELECT') {
			if (el[0].className === "span5 selectized") {
				$('#work_report_entity_bucket_form').find('select[name="' + name + '"]')[0].selectize.addItem(_.last(el.val()));
			} else {
				$('select[name="' + name + '"]').not(el).val(el.val());
			}
		} else if (el[0].nodeName === 'INPUT') {
			$('input[name="' + name + '"]').not(el).val(el.val());
		}
	},

	downloadReport: function () {
		this.options.router.downloadReport(this.filenameHash);
	},

	initializeFixedPositioning: function () {
		// Add conditional fixed positioning for browsers that can support it so as the user's scroll
		// vertically and horizontally they reports actions will stick with them.
		// Cache selector queries.
		var view = $(window),
			filters = $('#fixed_filters'),
			filtersWrap = $('#fixed_filters_wrap'),
			filtersWrapperOffset = filtersWrap.offset(),
			header = $('#report_display_header'),
			headerWrap = $('#report_display_header_wrap'),
			headerHeight = header.outerHeight(true),
			headerWrapperOffset = headerWrap.offset();

		// Filters box (fixed horizontally).
		view.bind('scroll resize', _.throttle(function () {
			if ($(this).scrollTop() > filtersWrapperOffset.top || $(this).scrollLeft() > filtersWrapperOffset.left) {
				if (!filters.hasClass('fixed-floating-box')) {
					filters.addClass('fixed-floating-box');
				}

				if (filters.css('left') !== filtersWrapperOffset.left) {
					if (view.scrollLeft() < filtersWrapperOffset.left) {
						filters.css('left', filtersWrapperOffset.left - view.scrollLeft());
					} else {
						filters.css('left', 0);
					}
				}

				if (filters.css('top') !== filtersWrapperOffset.top) {
					filters.css('top', filtersWrapperOffset.top - view.scrollTop());
				}
			} else {
				if (filters.hasClass('fixed-floating-box')) {
					filters.removeClass('fixed-floating-box');
					filtersWrap.css('margin-bottom', 0);
				}
			}
		}, 100));

		// Results table header (fixed vertically).
		view.bind('scroll resize', _.throttle(function () {
			if ($(this).scrollTop() > headerWrapperOffset.top || $(this).scrollLeft() > headerWrapperOffset.left) {
				if (!header.hasClass('fixed-results-header')) {
					header.addClass('fixed-results-header');
					headerWrap.css('height', headerHeight);
				}

				if (header.css('left') !== headerWrapperOffset.left) {
					header.css('left', headerWrapperOffset.left - view.scrollLeft());
				}

				if (header.css('top') !== headerWrapperOffset.top) {
					if (view.scrollTop() < headerWrapperOffset.top) {
						header.css('top', headerWrapperOffset.top - view.scrollTop());
					} else {
						header.css('top', 0);
					}
				}
			} else {
				if (header.hasClass('fixed-results-header')) {
					header.removeClass('fixed-results-header');
					headerWrap.css('margin-bottom', 0);
				}
			}
		}, 50));
	},

	toggleFiltersPane: function () {
		var el = $('#fixed_filters_wrap');
		if (el.is(':visible')) {
			el.hide();
			$('#toggle-filters').html('Show filters')
		}
		else {
			el.show();
			$('#toggle-filters').html('Hide filters');
		}
	}
});

function split (val) {
	return val.split( /,\s*/ );
}

function extractLast (term) {
	return split(term).pop();
}


