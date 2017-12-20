'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import Template from './templates/show_recurrence.hbs';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click  #work_report_entity_bucket_next' : 'workReportEntityBucketNext',
		'click  #work_report_save'               : 'workReportSaveShow'
	},

	initialize: function (options) {
		this.options = options || {};
	},

	render: function() {
		// Force assignment reports for right now - is this needed?
		$('#work_report_types').val('0');
	},

	workReportEntityBucketNext: function (e) {
		e.preventDefault();
		//check to see if a date range is checked
		let selectedRanges = $(".toggle_field:checked", $(".bucket:first"));
		if (selectedRanges.length === 0) {
			wmNotify({
				message: 'You must select one or more date ranges.',
				type: 'danger'
			});
		} else {
			let invalideDateRanges = $('.date-filter option[value="17"]:selected', selectedRanges.parent().parent().parent());
			let showError = invalideDateRanges.length > 0;
			if (!showError) {
				//lastly check if its 'Date Range' and if so, check if there is a range
				invalideDateRanges = $('.date-filter option[value="4"]:selected', selectedRanges.parent().parent().parent());
				$.each(invalideDateRanges, (index, dateRange) => {
					$.each($('.date', $(dateRange).parent().parent()), (index, datePicker) => {
						if ($(datePicker).datepicker('getDate') === null) {
							showError = true;
							return false;
						}
					});
					if (showError) {
						return false;
					}
				});
			}
			
			if (showError) {
				wmNotify({
					message: 'Invalid Date Range.',
					type: 'danger'
				});
			} else {
				this.options.router.navigate('step2', true);
			}
		}
	},

	workReportSaveShow: function() {
		$('.report_name').val(this.options.name);
		this.saveModal = wmModal({
			autorun: true,
			title: 'Save Report',
			destroyOnClose: true,
			content: Template,
			controls: [
				{
					text: 'Cancel',
					close: true,
					classList: ''
				},
				{
					text: 'Save Report',
					primary: true
				}
			]
		});

		$('.wm-modal .-active').find('.-primary').on('click', (event) => this.workReportSaveSubmit(event));
	},

	workReportSaveSubmit: function (e) {
		e.preventDefault();

		var reportName = $('#report_name').val();
		if (!reportName) {
			wmNotify({
				message: 'You must first name this report.',
				type: 'danger'
			});
		} else {
			//enable disabled select fields to prevent server exploding
			$('.date-filter').prop('disabled', false);
			this.options.router.workReportSave(reportName);
			this.options.name = reportName;
			this.saveModal.destroy();
		}
	}
});
