'use strict';

import Application from '../core';
import Backbone from 'backbone';
import $ from 'jquery';
import Payments from './main';
import formatCurrency from '../funcs/formatCurrency';
import '../config/datepicker';

$.unescapeHTML = function (html) {
	return $('<div/>').html(html).text();
};

$.unescapeAndParseJSON = function (json) {
	return $.parseJSON($.unescapeHTML(json));
};

const Router = Backbone.Router.extend({
	routes: {
		'payments(/)'                : 'index',
		'payments/ledger(/)'         : 'ledger',
		'payments/offline_ledger(/)' : 'offlineLedger',
		'payments/invoices/*path'    : 'invoices',
		'funds(/)'                   : 'accountsList',
		'funds/withdraw(/)'          : 'withdraw',
		'funds/add(/)'               : 'addFunds',
		'funds/accounts(/)'          : 'accountsList',
		'funds/accounts/new(/)'      : 'newAccount',
		'funds/accounts/gcc(/)'      : 'gcc',
		'funds/addaccount(/)'        : 'newAccount',
		'funds/allocate-budget'      : 'allocateBudget',
		'funds/invoice'              : 'generateInvoice'
	},

	initialize() {
		Backbone.history.start({ pushState: true });
	},

	ledger() {
		return Payments.LedgerPage();
	},
	offlineLedger() {
		return Payments.OfflineLedgerPage();
	},
	invoices() {
		new Payments.InvoicesView({
			statements_details: $.unescapeAndParseJSON($('#json_statements_details').html()),
			show_current_invoice_view: Application.Features.showCurrentInvoiceView,
			show_all_statements_view: Application.Features.showAllStatementsView,
			show_statements_id_view: Application.Features.showStatementsIdView,
			invoice_paid_status: Application.Features.invoicePaidStatus,
			current_view: Application.Features.currentView,
			mmwAutoPayEnabled: Application.Features.mmwAutoPayEnabled
		});
	},

	withdraw() {
		new Payments.WithdrawView({
			modal: false
		});

		var calc_factory = {
			ACH: function (amount) {
				return {
					value: Math.max(amount, 0),
					description: 'Free'
				};
			},
			PPA: function (amount, cost) {
				if (cost.fixed_amount) {
					return {
						value: Math.max(amount - cost.fixed_amount, 0),
						description: formatCurrency(cost.fixed_amount)
					};
				} else {
					return {
						value: Math.max(amount - Math.min(cost.percentage_based_amount_limit, cost.percentage_rate * amount), 0),
						description: (cost.percentage_rate * 100) + '% up to ' + formatCurrency(cost.percentage_based_amount_limit)
					};
				}
			},
			GCC: function (amount) {
				return {
					value: Math.max(amount, 0),
					description: 'Free'
				};
			}
		};

		$('[name=account]').on('change', function () {
			var $withDrawButton = $('#withdrawBtn');
			$withDrawButton.attr('disabled', 'disabled');
			$('.config').hide();
			var type = $(this).find(':selected').data('type');
			if (type) {
				var config = $('.config[rel~=' + type.toLowerCase() + ']').show();
				var country = $(this).find(':selected').data('country');

				$('[name=amount]').on('keyup',function () {
					var grossAmount = $(this).val();
					var adjusted = calc_factory[type](grossAmount, Application.Features.paypalFees[Application.Features.paypalCountryCodes[country]]);

					config.find('.amount-outlet').text(formatCurrency(adjusted.value));
					config.find('.fee-calc-outlet').text(adjusted.description);

				}).trigger('keyup');
				$withDrawButton.removeAttr('disabled');
				$('#canada-funds-message').toggle(country === 'CAN');
			}
		}).trigger('change');
	},

	addFunds() {
		new Payments.AddFundsView();

		$('#amount').keyup(calc_cc_amount);
		calc_cc_amount();


		$('select[name=card_type]').change(change_fee);
		change_fee();


		$('#amount_ach').keyup(calc_ach_amount);
		calc_ach_amount();


		$('select[name=account]').change(change_account);
		change_account();

		var $addressCheckbox = $('input.address_type');

		$addressCheckbox.click(function() {
			$addressCheckbox.filter(':checked').not(this).removeAttr('checked');
		});

		$('#use_company_address').click(function() {
			toggle_address(Application.Features.companyAddress);
		});

		$('#use_profile_address').click(function() {
			toggle_address(Application.Features.profileAddress);
		});

		function toggle_address(address_type) {
			$('#address1').val(address_type.address1);
			$('#address2').val(address_type.address2);
			$('#city').val(address_type.city);
			$('#state').val(address_type.state);
			$('#postal_code').val(address_type.postal_code);
		}

		function validate_form() {
			var empty = false;

			if ($('select[name=card_type]').val() === '') {
				empty = true;
			}

			if (get_dropdown_counts() > 0) {
				for(var i = 1; i <= get_dropdown_counts(); i++) {
					if($('#add_funds_amount_' + i).val() === '' || $('#add_funds_project_' + i).val() === '') {
						empty = true;
					}
				}
			}

			if (empty) {
				$('#cc-step1-next').attr('disabled', 'disabled');
			} else {
				$('#cc-step1-next').removeAttr('disabled', '');
			}
		}

		function calc_cc_amount() {
			var total = 0;
			if(get_dropdown_counts() > 0) {
				for(var i = 1; i <= get_dropdown_counts(); i++) {
					if ($('#add_funds_amount_' + i).val() && !isNaN($('#add_funds_amount_' + i).val())) {
						total += parseFloat($('#add_funds_amount_' + i).val());
					}
				}
				$('#amount').val(formatCurrency(total));
				$('#calc_total').html(formatCurrency(total + total * $('#percentage').val()));
				validate_form();
			} else {
				// No allocate to project
				if ($('#amount').val() && !isNaN($('#amount').val())) {
					$('#calc_total').html(formatCurrency(parseFloat($('#amount').val()) + (parseFloat($('#amount').val()) * $('#percentage').val())));
					if ($('select[name=card_type]').val() !== '') {
						$('#cc-step1-next').removeAttr('disabled','');
					}
				} else {
					$('#calc_total').html('$0');
					$('#cc-step1-next').attr('disabled','disabled');
				}
			}
		}

		function change_fee() {
			$('#cc-step1-next').attr('disabled','disabled');
			if ($('option:selected',this).val()) {
				if ($('option:selected',this).val() === 'amex') {
					$('#cc_fee').html('3.42%');
					$('#percentage').val('0.0342');
				} else {
					$('#cc_fee').html('3.18%');
					$('#percentage').val('0.0318');
				}
				if ($('#amount').val() && !isNaN($('#amount').val())) {
					$('#cc-step1-next').removeAttr('disabled','');
				}
			} else {
				$('#cc_fee').html('Select a card type to see the fee');
				$('#percentage').val('0.0318');
			}
			calc_cc_amount();
		}

		var dropdown_id = 0;
		function get_dropdown_counts() {
			return dropdown_id;
		}

		function validate_ach_form() {
			var empty = false;

			if ($('select[name=account]').val() === '') {
				empty = true;
			}

			if(get_ach_dropdown_counts() > 0) {
				for(var i = 1; i <= get_ach_dropdown_counts(); i++) {
					if($('#add_ach_funds_amount_' + i).val() === '' || $('#add_ach_funds_project_' + i).val() === '') {
						empty = true;
					}
				}
			}

			if (empty) {
				$('#add_funds_ach').attr('disabled', 'disabled');
			} else {
				$('#add_funds_ach').removeAttr('disabled', '');
			}
		}

		function calc_ach_amount() {
			var total = 0;
			if(get_ach_dropdown_counts() > 0) {
				for(var i = 1; i <= get_ach_dropdown_counts(); i++) {
					if ($('#add_ach_funds_amount_' + i).val() && !isNaN($('#add_ach_funds_amount_' + i).val())) {
						total += parseFloat($('#add_ach_funds_amount_' + i).val());
					}
				}
				$('#amount_ach').val(total);
				$('#add_funds_ach').attr('disabled','disabled');
				$('#calc_ach_total').html(formatCurrency(total));
				validate_ach_form();
			} else {
				if ($('#amount_ach').val() && !isNaN($('#amount_ach').val()) && $('#amount_ach').val() > 0) {
					if ($('select[name=account]').val()) {
						total = parseFloat($('#amount_ach').val());
						$('#add_funds_ach').removeAttr('disabled','');
						$('#calc_ach_total').html(formatCurrency(total));
					}
				} else {
					$('#add_funds_ach').attr('disabled','disabled');
					$('#calc_ach_total').html(formatCurrency(total));

				}
			}
		}

		function change_account() {
			$('#add_funds_ach').attr('disabled','disabled');
			if ($('option:selected',this).val()) {
				if ($('#amount_ach').val() && !isNaN($('#amount_ach').val())) {
					$('#add_funds_ach').removeAttr('disabled','');
				}
			}
			calc_ach_amount();
		}

		var ach_dropdown_id = 0;
		function get_ach_dropdown_counts() {
			return ach_dropdown_id;
		}
	},

	newAccount() {
		new Payments.NewAccountPage({
			isBuyer: Application.Features.isBuyer,
			country: Application.Features.country
		});
	},

	index() {
		new Payments.IndexPage({
			isBuyer: Application.Features.isBuyer
		});
	},

	gcc() {
		Payments.GccPage();
	},

	accountsList() {
		Payments.AccountsListPage();
	},

	allocateBudget() {
		Payments.AllocateBudgetPage();
	},

	generateInvoice() {
		Payments.GenerateInvoicePage();
	}
});

Application.init(config.payments, Router);

