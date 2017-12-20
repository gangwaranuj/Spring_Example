'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import regula from '../../dependencies/regula.min';
import '../../dependencies/autoNumeric';
import '../../dependencies/jquery.tmpl';
import '../../config/datepicker';
import 'jquery-ui';

export default Backbone.View.extend({
	el: '.content',

	events: {
		'click #add_more_items'               : 'addLineItem',
		'click .remove'                       : 'removeLineItem',
		'change .invoice_line_item_type'      : 'setDefaultDescription',
		'change .currency'                    : 'updateInvoiceTotal',
		'submit #adhoc_invoice_form'          : 'submitFor',
		'change #subscriptionInvoiceTypeCode' : 'toggleAllInvoiceLineItemsCallback'
	},

	initialize () {
		this.initCompanyDropdown();
		this.addLineItem();
		this.initializeDatePicker();
		this.setupCustomValidators();
		this.setFieldMasks();
		this.updateInvoiceTotal();
		this.initPaymentPeriods();
	},

	initCompanyDropdown () {

		function renderCompanyId () {
			if ($('#companyId').val()) {
				$('#selected_company').text('(Company ID: ' + $('#companyId').val() + ')');
				$('#selected_company').show();
			}
		}

		$('#companyName').autocomplete({
			minLength: 0,
			source: '/admin/manage/profiles/suggest_company',
			focus: function (event, ui) {
				$('#companyName').val(ui.item.value);
				return false;
			},
			select: (event, ui) => {
				$('#companyId').val(ui.item.id);

				let subscriptionInfo = $('.subscriptionInfo');
				subscriptionInfo.find('.companyId').text(ui.item.id);
				subscriptionInfo.find('.companyName').text(ui.item.value);
				subscriptionInfo.show();

				$.get('/admin/accounting/subscriptionDetails/' + ui.item.id, response => {
					let subscriptionDetails = response.data.subscriptionDetails;
					let subscriptionInvoiceTypeCodeSelect = $('[name="subscriptionInvoiceTypeCode"]');
					let noActiveSubscription = Object.keys(subscriptionDetails).length === 0;
					let noPlanSubscriptionInvoiceTypeCodes = this.options.noPlanSubscriptionInvoiceTypeCodes;

					if (noActiveSubscription) {
						let firstNoPlanSubscriptionInvoiceTypeCode = subscriptionInvoiceTypeCodeSelect.find('option').filter(function () {
							let isNoPlanSubscriptionInvoiceTypeCode = noPlanSubscriptionInvoiceTypeCodes.includes($(this).val());
							if (isNoPlanSubscriptionInvoiceTypeCode || $(this).val() === 'N/A') {
								$(this).prop('disabled', false);
							} else {
								$(this).prop('disabled', true);
							}
							return isNoPlanSubscriptionInvoiceTypeCode;
						}).first().prop('selected', true).val();

						this.toggleAllInvoiceLineItems(firstNoPlanSubscriptionInvoiceTypeCode);

						this.setSubscriptionDetails('N/A', 'N/A', 'N/A', 'N/A', 'N/A', 'N/A');
						
					} else {

						let firstSubscriptionInvoiceTypeCode = subscriptionInvoiceTypeCodeSelect.find('option').filter(function () {
							let isNoPlanSubscriptionInvoiceTypeCode = noPlanSubscriptionInvoiceTypeCodes.includes($(this).val());
							if (isNoPlanSubscriptionInvoiceTypeCode && $(this).val() !== 'N/A') {
								$(this).prop('disabled', true);
							} else {
								$(this).prop('disabled', false);
							}
							return !isNoPlanSubscriptionInvoiceTypeCode;
						}).first().prop('selected', true).val();

						this.toggleAllInvoiceLineItems(firstSubscriptionInvoiceTypeCode);

						let nextNotInvoicedPaymentPeriod = new Date(subscriptionDetails.nextNotInvoicedPaymentPeriod);
						let nextNotInvoicedPaymentPeriodLastDay = new Date(nextNotInvoicedPaymentPeriod.getFullYear(), nextNotInvoicedPaymentPeriod.getMonth() + 1, 0);
						this.setSubscriptionDetails(
							subscriptionDetails.period,
							subscriptionDetails.effectiveDate, 
							subscriptionDetails.endDate,
							$.datepicker.formatDate('mm/dd/yy', nextNotInvoicedPaymentPeriodLastDay),
							'$' + subscriptionDetails.currentTierAmount.toFixed(2),
							subscriptionDetails.currentTierVORAmount ? '$' + subscriptionDetails.currentTierVORAmount.toFixed(2) : 'N/A'
						);
					}

				});

				return false;
			},
			search: function () {
				$('#companyId').val('');

				$('#selected_company').text('');
				$('#selected_company').hide();
			}
		});

		renderCompanyId();
	},

	setSubscriptionDetails(period, effectiveDate, endDate, servicePeriod, tier, vorTier) {
		let subscriptionInfo = $('.subscriptionInfo');

		subscriptionInfo.find('.period').text(period);
		subscriptionInfo.find('.effectiveDate').text(effectiveDate);
		subscriptionInfo.find('.endDate').text(endDate);
		subscriptionInfo.find('.servicePeriod').text(servicePeriod);
		subscriptionInfo.find('.tier').text(tier);
		subscriptionInfo.find('.vorTier').text(vorTier);
	},

	setFieldMasks () {
		var $currency = $('.currency');
		$currency.autoNumeric('destroy');
		$currency.autoNumeric('init', {
			aSep: '',
			aDec: '.'
		});
	},

	initializeDatePicker () {
		$('#invoiceDueDate').datepicker({
			dateFormat:'mm/dd/yy',
			minDate: '+1d'
		});
		// 1 day from today
	},

	setupCustomValidators () {
		// Validator for amount fields
		regula.custom({
			name: 'Amount',
			defaultMessage: 'Amount must be greater than 0',
			validator: function () {
				var value = parseFloat(this.value);
				return !_.isNaN(value) && (value > 0);
			}
		});
	},

	addLineItem () {
		$('#invoiceLineItems').append( $.tmpl($('#invoice_line_item_template').html(), {idx: $('.line_item').size()}) );
		let lastInvoiceLineItemSelect = $('.invoice_line_item_type:last');
		lastInvoiceLineItemSelect.focus();
		this.setFieldMasks();
		this.toggleInvoiceLineItems(lastInvoiceLineItemSelect, $('#subscriptionInvoiceTypeCode option:selected').val());
	},

	removeLineItem (e) {
		this.$(e.currentTarget).closest('.line_item').remove();

		// Reassign item indexes
		this.$('.line_item').each( function (idx, div) {
			$(':input', this).each(function (i, elem) {
				$(elem).attr('name', elem.name.replace(/\[.+\]/, '["+idx+"]'));
			});
		});

		this.updateInvoiceTotal();
	},

	// Upon changing line item's type, description will default to the new selected type
	setDefaultDescription (e) {
		var defaultValue = $(':selected', e.currentTarget).text();

		$(e.currentTarget).parents('.line_item')
			.find('input[name$=description]')
			.val(defaultValue);
	},

	// Update the invoice total amount
	updateInvoiceTotal () {
		var amountValues = _.pluck($('.currency').toArray(), 'value');

		var total = _.reduce(amountValues, function (sum, val) {
			let amount = parseFloat(val);

			return sum + (_.isNaN(amount) ? 0.0 : amount);
		}, 0);

		if (total > 0) {
			this.$('.submit').removeAttr('disabled');
		} else {
			this.$('.submit').attr('disabled', 'disabled');
		}

		this.$('#invoice_total span').text(total.toFixed(2));
	},

	validate () {
		// Clear previous errors
		this.$('.error').removeClass('error');
		this.$('.inlineError').remove();

		// Unbind previous elements
		regula.unbind();

		// Bind form elements
		var elements = $('#companyName')
			.add('[name="dueDate"]')
			.add($('.line_item :text'));

		regula.bind({elements: elements.toArray()});

		var validationErrors = regula.validate();
		var isValid = _.isEmpty(validationErrors);

		// If form is not valid, show errors
		if (!isValid) {
			_.each(validationErrors, (e) => {
				let elem = _.first(e.failingElements);
				this.showFieldError('[name="' + _.first(e.failingElements).name +'"]', e.message, true);
			});
		}

		return isValid;
	},

	showFieldError (selector, msg) {
		var errMsg = $('<span>')
						.addClass('inlineError')
						.text(msg);

		$(selector).closest('.field')
			.addClass('error')
			.append(errMsg);
	},

	submitForm () {
		return this.validate();
	},

	toggleInvoiceLineItems: function ($lineItemSelect, subscriptionInvoiceTypeCode) {
		let nonSubscriptionInvoiceLineItemTypes = this.options.nonSubscriptionInvoiceLineItemTypes;

		// (1) If invoice is non-subscription, disable all subscription invoice line items
		// otherwise, disable all non-subscription invoice line items
		$lineItemSelect.find('option').each(function() {
			if (subscriptionInvoiceTypeCode === "N/A") {
				$(this).prop('disabled', !_.contains(nonSubscriptionInvoiceLineItemTypes, $(this).val()));
			} else {
				$(this).prop('disabled', _.contains(nonSubscriptionInvoiceLineItemTypes, $(this).val()));
			}
		});

		// (2) If invoice is non-subscription, make sure all dropdowns have a non-subscription line-item selected
		// otherwise, set make sure a subscription line-item is selected

		$lineItemSelect.find('option').filter(function () {
			if (subscriptionInvoiceTypeCode === 'N/A') {
				return nonSubscriptionInvoiceLineItemTypes.includes($(this).val());
			} else {
				return !nonSubscriptionInvoiceLineItemTypes.includes($(this).val());
			}
		}).first().prop('selected', true);

		// (3) Make sure invoice line item description field is in sync with selection
		let selectedOption = $lineItemSelect.find('option:selected');
		$lineItemSelect.closest('.line_item').find('.description input').val(selectedOption.text());
	},

	toggleAllInvoiceLineItems: function (subscriptionInvoiceTypeCode) {
		if (subscriptionInvoiceTypeCode === 'N/A' || this.lastSubscriptionInvoiceTypeCode === 'N/A') {
			$('.invoice_line_item_type').each((i, el) => {
				this.toggleInvoiceLineItems($(el), subscriptionInvoiceTypeCode)
			});

			$('#paymentPeriod').closest('.control-group').css('visibility', subscriptionInvoiceTypeCode === "N/A" ? 'hidden' : 'visible');
		}

		this.lastSubscriptionInvoiceTypeCode = subscriptionInvoiceTypeCode;
	},

	toggleAllInvoiceLineItemsCallback: function (e) {
		this.toggleAllInvoiceLineItems(e.currentTarget.value);
	},

	initPaymentPeriods: function () {
		let now = new Date();
		let firstDayOfOneYearAgo = new Date(now.getFullYear(), now.getMonth() - 12, 0);

		let twoYearsOfMonths = new Array(18).fill().reduce(function(arrayOfMonths, currentValue, currentIndex) {
			let previousMonth = arrayOfMonths[currentIndex];
			let currentMonth = new Date(previousMonth.getFullYear(), previousMonth.getMonth() + 2, 0);
			return arrayOfMonths.concat(currentMonth);
		}, [firstDayOfOneYearAgo]);

		let isCurrentMonth = (month) => {
			if (now.getMonth() === month.getMonth() && now.getFullYear() === month.getFullYear()) {
				return 'selected';
			} else {
				return '';
			}
		};
		$('#paymentPeriod').html( 
			twoYearsOfMonths.map(function(month) {
				let formattedDate = $.datepicker.formatDate('mm/dd/yy', month);
				return '<option value="' + formattedDate + '" '  + isCurrentMonth(month) + ' >' + formattedDate + '</option>';
			})
		);
	}

});
