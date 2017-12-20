'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import Application from '../core';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import InvoiceBundleRowDetailTemplate from './templates/invoice-bundle-row-detail.hbs';
import InvoiceRowDetail from './templates/invoice-row-detail.hbs';
import ConfirmActionTemplate from '../funcs/templates/confirmAction.hbs';
import FastFundsErrorMessageTemplate from './templates/fast-funds-error-message.hbs';
import FastFundsSummaryTemplate from './templates/fast-funds-summary.hbs';
import FastFundsResponseTemplates from './templates/fast-funds-response.hbs';

import 'jquery-form/jquery.form';

export default Backbone.View.extend({
	tagName: 'tr',

	events: {
		'click'                         : 'toggleRow',
		'click button.get-paid-now'     : 'getPaidNow',
		'click a.email-invoice-outlet'  : 'email',
		'click a.print-invoice-outlet'  : 'print',
		'click a.pay-invoice-outlet'    : 'pay',
		'click .js-unlock-invoice'      : 'unlock',
		'click a.js-remove-from-bundle' : 'unbundleInvoice'
	},

	render: function () {
		if (this.model.get('invoiceType') === 'bundle') {
			this.$el.html(InvoiceBundleRowDetailTemplate({
				model: this.model.toJSON(),
				invoiceStatusTypeCodePaid: this.model.get('invoiceStatusTypeCode') === 'paid',
				invoiceStatusTypeCodePending: this.model.get('invoiceStatusTypeCode') === 'pending',
				invoiceStatusTypeCodeVoid: this.model.get('invoiceStatusTypeCode') === 'void',
				invoiceFulfillmentStatusIsPending: this.model.get('invoiceFulfillmentStatus') === 'pending',
				isReceivables: this.options.currentView === 'receivables',
				mmwAutoPayEnabled: this.options.mmwAutoPayEnabled
			}));
		} else {
			this.$el.html(InvoiceRowDetail({
				model: this.model.toJSON(),
				invoiceStatusTypeCodePaid: this.model.get('invoiceStatusTypeCode') === 'paid',
				invoiceStatusTypeCodeVoid: this.model.get('invoiceStatusTypeCode') === 'void',
				invoiceStatusTypeCodePending: this.model.get('invoiceStatusTypeCode') === 'pending',
				invoiceIsSubscriptionOrAdhoc: (this.model.get('invoiceType') === 'subscription' || this.model.get('invoiceType') === 'adHoc'),
				isInvoiceTypeSubscription: this.model.get('invoiceType') === 'subscription',
				isInvoiceTypeAdHoc: this.model.get('invoiceType') === 'adHoc',
				isReceivables: this.options.currentView === 'receivables',
				mmwAutoPayEnabled: this.options.mmwAutoPayEnabled,
				hasFeatureFastFunds: this.options.hasFeatureFastFunds,
				isFastFundsAvailable: this.options.isFastFundsAvailable,
				isFastFundsComplete: this.options.isFastFundsComplete,
				fastFundsFee: this.options.fastFundsFee
			}));
		}

		this.$el.addClass(this.model.get('invoiceType'));
		if (this.model.get('invoiceType') === 'invoice') {
			this.$el.addClass('invoice-row');
		}

		if (this.model.get('invoicePastDue') && this.model.get('invoiceType') !== 'statement') {
			this.$el.addClass('important');
		} else if (this.model.get('invoiceDueWithinWeek' && this.model.get('invoiceType') !== 'statement')) {
			this.$el.addClass('warning');
		}

		(function disableRemoveFromBundle(self) {
			var $removeFromBundleLinks = $('.js-remove-from-bundle'),
				isBundled = self.model.get('bundle'),
				hasOnlyOneInvoice = self.model.get('bundledInvoices').length === 1;

			if (isBundled && hasOnlyOneInvoice) {
				// Disable the link
				$removeFromBundleLinks.addClass('-disabled tooltipped tooltipped-n').on('click', false);
			}
		})(this);

		return this;
	},

	toggleRow: function (event) {
		if ($(event.target).is('a, input')) {
			return;
		}

		if (this.$el.hasClass('invoice')) {
			this.toggleInvoiceRow(event);
		} else if (this.$el.hasClass('bundle')) {
			this.toggleBundleRow(event);
		}
	},

	toggleBundleRow: function (event) {
		event.stopPropagation();

		if (this.model.get('bundledInvoices').length !== this.model.get('numberOfInvoices')) {
			$.getJSON('/payments/invoices/load_bundle/' + this.model.get('invoiceId'), function (response) {
				this.model.set({ bundledInvoices: response.data.bundledInvoices });
				this.render();
				this.toggleInvoiceRow(event);
			}.bind(this));
		} else {
			this.toggleInvoiceRow(event);
		}
	},

	toggleInvoiceRow: function () {
		this.$el.toggleClass('expanded');
	},

	//this is used
	emailAjaxForm: function (modal) {
		$('#form_email_invoices').ajaxForm({
			dataType: 'json',
			success: function (data) {
				if (data.successful) {
					this.options.list.refetch();
					modal.destroy();
					wmNotify({
						message: data.messages[0]
					});
				} else {
					wmNotify({
						message: data.messages[0],
						type: 'danger'
					});
				}
			}.bind(this)
		});
	},

	email: function (event) {
		event.preventDefault();
		var link = $(event.currentTarget).attr('href');
		if ($(event.target).data('confirmationMessage')) {
			this.confirmModal = wmModal({
				autorun: true,
				title: 'Confirm',
				destroyOnClose: true,
				content: ConfirmActionTemplate({
					message: $(event.target).data('confirmationMessage')
				})
			});

			$('.cta-confirm-yes').on('click', function () {
				this.showEnterEmailModal(link);
				this.confirmModal.destroy();
			}.bind(this));
		} else {
			this.showEnterEmailModal(link);
		}
	},

	showEnterEmailModal: function (link) {
		$.ajax({
			type: 'GET',
			url: link,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					var modal = wmModal({
						autorun: true,
						title: 'Email Invoice',
						destroyOnClose: true,
						content: response
					});
					this.emailAjaxForm(modal);
				}
			}.bind(this)
		});
	},

	print: function (event) {
		if ($(event.target).data('confirmationMessage')) {
			wmModal({
				autorun: true,
				title: 'Confirm',
				destroyOnClose: true,
				content: ConfirmActionTemplate({
					message: $(event.target).data('confirmationMessage')
				})
			});

			$('.cta-confirm-yes').on('click', function () {
				window.location = event.target.href;
			});
		} else {
			window.location = event.target.href;
		}

		event.preventDefault();
		return false;
	},

	pay: function (event) {
		event.preventDefault();

		$.ajax({
			type: 'GET',
			url: $(event.currentTarget).attr('href'),
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					var modal = wmModal({
						autorun: true,
						title: 'Pay Invoice',
						destroyOnClose: true,
						content: response,
						controls: [
							{
								text: 'Cancel',
								close: true,
								classList: ''
							},
							{
								text: 'Pay',
								primary: true,
								classList: 'bundle-pay-submit'
							}
						],
					});

					$('.bundle-pay-submit').on('click', function () {
						$('#form_pay_invoices').submit();
					});

					$('#form_pay_invoices').ajaxForm({
						context: this,
						dataType: 'json',
						success: function (data) {
							if (data.successful) {
								modal.hide();
								wmNotify({ message: data.messages[0] });
								this.options.list.refetch();
							} else {
								wmNotify({
									message: data.messages[0],
									type: 'danger'
								});
							}
						}
					});
				}
			}

		});
	},

	unlock: function (event) {
		event.preventDefault();

		$.post('/payments/invoices/unlock_invoice/' + $(event.target).data('invoice'))
			.done(function (response) {
				wmNotify({
					message: response.message
				});
				this.model.set({ editable: true });
				this.render();
			}.bind(this))
			.fail(function (response) {
				wmNotify({
					message: response.message,
					type: 'danger'
				});
			});
	},

	unbundleInvoice: function (event) {
		var $link = $(event.currentTarget);

		$.post('/payments/invoices/remove_from_bundle/' + $link.data('invoice-id') + '/' + $link.data('invoice-summary'))
			.done(function (data) {
				if (data.successful) {
					wmNotify({
						message: data.messages[0]
					});
					this.options.list.refetch();
				} else {
					wmNotify({
						message: data.messages[0],
						type: 'danger'
					});
				}
			}.bind(this));
	},

	getPaidNow: function getPaidNow (event) {
		event.preventDefault();
		event.stopPropagation();

		let self = this;

		const amountEarned = this.model.get('amountEarned');
		const assignmentData = {
			invoiceId: this.model.get('invoiceId'),
			workTitle: this.model.get('workTitle'),
			companyName: this.model.get('companyName'),
			amountEarned: amountEarned.toFixed(2),
			fastFundsFee: this.options.fastFundsFee.toFixed(2),
			fastFundsTotal: (amountEarned - this.options.fastFundsFee).toFixed(2)
		};

		analytics.track('Payments', Object.assign({ action: `Clicked "Get Paid Now" on Invoice` }, assignmentData));

		const getPaidNowModal = wmModal({
			showProgress: false,
			destroyOnClose: true,
			customHandlers: [
				{
					selector: '.fast-funds--accept-terms',
					event: 'click',
					callback: function (event) {

						let $acceptButton = $(event.target);
						let $cancelButton = $('.fast-funds--cancel');
						$acceptButton.prop('disabled', true);
						$cancelButton.prop('disabled', true);

						analytics.track('Payments', Object.assign({
							action: `Clicked "Accept & Get Paid Now" in FastFunds modal`,
						}, assignmentData));

						$.post(`/payments/invoices/fast_funds/confirmation/${assignmentData.invoiceId}`)
							.then(response => {
								if (response.successful) {
									$('#fast-funds-success').addClass('-active');
									Application.Events.trigger('invoices:fastFundsSuccess');

									$.get('/worker/v2/funds')
										.then(response => {
											let availableToWithdraw = `$${response.results[0].availableToWithdraw.toLocaleString()}`;
											$('#available-funds').text(availableToWithdraw);
											$('#withdrawLink .currency').text(availableToWithdraw);
										});
								} else {
									$('#fast-funds-error').addClass('-active');
									let errorTemplate = FastFundsErrorMessageTemplate;
									let errorMessageHtml = response.messages.reduce((previousMessage, errorMessage) => {
										return `${previousMessage}${errorTemplate({ errorMessage })}`;
									}, '');

									$acceptButton.prop('disabled', false);
									$cancelButton.prop('disabled', false);

									$('.fast-funds--error-messages').html(errorMessageHtml);
								}

								getPaidNowModal.slideForward();
							});
					}
				}
			],
			slides: [
				{
					title: 'FastFunds – Payment Breakdown',
					content: FastFundsSummaryTemplate(assignmentData),
					controls: [
						{
							text: 'Cancel',
							classList: 'fast-funds--cancel',
							close: true
						},
						{
							text: 'Accept & Get Paid Now',
							classList: 'fast-funds--accept-terms',
							primary: true,
							primaryIcon: 'wm-icon-checkmark',
							asynchronous: true
						}
					]
				},
				{
					title: 'FastFunds',
					content: FastFundsResponseTemplates(assignmentData),
					controls: [
						{
							text: 'Done',
							classList: 'fast-funds--done',
							primary: true,
							close: true
						}
					]
				}
			]
		});

		getPaidNowModal.show();
	}
});
