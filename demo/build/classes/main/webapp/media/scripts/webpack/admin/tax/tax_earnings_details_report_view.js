'use strict';

import confirmActionTemplate from '../../templates/modals/confirmAction.hbs';
import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import EarningsDetailReportView from './tax_earnings_report_view';
import EarningsReportDataView from './tax_earnings_detail_report_data_view';
import wmNotify from '../../funcs/wmNotify';
import wmModal from '../../funcs/wmModal';
import ajaxSendInit from '../../funcs/ajaxSendInit';

export default Backbone.View.extend({
	el: '#earnings_detail',
	itemTemplate: $('#earnings_detail_report_row_template').html(),

	events: {
		'click #generate_csv' : 'generateCSV',
		'click .delete'       : 'deleteCSV',
		'click #publish'      : 'publish'
	},

	initialize: function () {
		ajaxSendInit();
		_.bindAll(this, 'canPublish', 'addItem');
		var self = this;
		$.getJSON('/admin/accounting/earnings_detail.json', function (json) {
			if (json.successful && !_.isNull(json.data)) {
				var reports = [];
				var reportsPublished = [];

				_.each(json.data.earningDetailReports, function (rpt) {
					if (rpt.status.published) {
						reportsPublished.push(rpt);
					} else {
						reports.push(rpt);
					}
				});
				self.formView = new EarningsDetailReportView({
					earningDetailReportSets: reports,
					canPublish: self.options.canPublish
				});
				self.formPublished = new EarningsReportDataView({
					earningReportDetailSets: reportsPublished
				});
			}
			self.render(reports);
		});
	},

	render: function (earningDetailReportSets) {
		this.$('.report_table').empty();
		_.each(earningDetailReportSets, this.addItem);

		if (this.canPublish()) {
			this.$('#publish').removeClass('disabled');
		}

		return this;
	},

	canPublish: function () {

		return this.options.canPublish;
	},

	addItem: function (rpt) {
		var item = $.tmpl(this.itemTemplate, rpt);
		this.$('.report_table').append(item);
	},

	generateCSV: function (e) {
		e.preventDefault();

		var self = this;
		$.post('/admin/accounting/earnings_detail/generate_csv',
			function (response) {
				if (response.successful) {
					self.addItem(response.data);
					wmNotify({
						message: 'Your request is being processed. You will receive an email when the report is ready.'
					});

				} else {
					wmNotify({
						type: 'danger',
						message: 'An error occurred while generating the report. Please try again.'
					});
				}
			}
		);
	},

	deleteCSV: function (e) {
		e.preventDefault();

		var self = this;
		$.post('/admin/accounting/earnings_detail/delete/' + $(e.currentTarget).attr('data-id'),
			function (response) {
				if (response.successful) {
					self.$(e.currentTarget).closest('tr').remove();
				}
			}
		);

	},

	publish: function () {
		var self = this;
		var selectedItem = this.$(':radio:checked');
		if (!this.canPublish() || selectedItem.size() === 0) {
			wmNotify({
				type: 'danger',
				message: 'You must select one CSV to publish'
			});
			return;
		}

		this.confirmModal = wmModal({
			autorun: true,
			title: 'Confirm',
			destroyOnClose: true,
			content: confirmActionTemplate({
				message: 'Are you sure you want to publish the selected CSV?'
			})
		});

		$('.cta-confirm-yes').on('click', _.bind(function () {
			$.post('/admin/accounting/earnings_detail/publish/' + selectedItem.val(),
				function (response) {
					if (!_.isNull(response) && response.successful) {
						self.trigger('published', {
							id: selectedItem.val()
						});
						//move element to publish table
						$('#earnings_detail_published .report_table').append($.tmpl($('#earnings_detail_report_published_row_template').html(), {
							taxYear: selectedItem.closest('tr').find('td:nth-child(2)').text(),
							createdOn: selectedItem.closest('tr').find('td:nth-child(3)').text(),
							id: selectedItem.val()
						}));
						//remove from arrays reports and reportsPublished
						self.$('.report_table tr').remove();
						//disable publish button
						self.$('#publish').attr('disabled', 'disabled');
					} else {
						wmNotify({
							type: 'danger',
							message: 'There was an error publishing your CSV'
						});
					}
				});
			this.confirmModal.hide();
		}, this));
	}
});
