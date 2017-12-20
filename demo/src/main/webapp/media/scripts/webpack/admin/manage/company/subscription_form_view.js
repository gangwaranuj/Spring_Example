'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import ConfirmSubscriptionTemplate from './templates/confirm_subscription.hbs';
import CancelSubscriptionView from './cancel_subscription_view';
import RenewSubscriptionView from './renew_subscription_view';
import confirmActionTemplate from '../../../funcs/templates/confirmAction.hbs';
import moment from 'moment';
import wmNotify from '../../../funcs/wmNotify';
import wmModal from '../../../funcs/wmModal';
import ajaxSendInit from '../../../funcs/ajaxSendInit';
import '../../../dependencies/autoNumeric';
import regula from '../../../dependencies/regula.min';
import '../../../funcs/dateFormat';
import '../../../config/datepicker';

export default Backbone.View.extend({
	el: '#subscription_details',

	events: {
		'change #effective_date_year'                                          : 'recalculateEndDate',
		'change #effective_date_month'                                         : 'recalculateEndDate',
		'change #subscription_period'                                          : 'recalculateEndDate',
		'change #number_of_months'                                             : 'recalculateEndDate',
		'click #subscription_tier_form .add-tier-btn'                          : 'addSubscriptionTier',
		'click .subscription-tier .remove'                                     : 'removeSubscriptionTier',
		'change #subscription_form [name="hasDiscountOptions"]'                : 'discountOptionsOption',
		'change #subscription_form [name="hasAddOns"]'                         : 'addonsOption',
		'click #AddOnsOptions .add-addons-btn'                                 : 'addAddOnItem',
		'click .serviceTypeConfig .add-btn'                                    : 'addAccountServiceTypeConfiguration',
		'click #service_type_configurations .remove'                           : 'removeAccountServiceTypeConfiguration',
		'click #AddOnsOptions .remove'                                         : 'removeAddOnItem',
		'change .subscription-tier-upper'                                      : 'recalculateSubscriptionTiers',
		'change #service_type_configurations [name$="accountServiceTypeCode"]' : 'checkServiceTypeVOR',
		'click #submit_subscription'                                           : 'submitSubscriptionForm',
		'click #save_subscription_form'                                        : 'saveSubscriptionForm',
		'click #edit_subscription'                                             : 'enableEditableFields',
		'click #renew_subscription'                                            : 'showRenewSubscription',
		'click #cancel_subscription'                                           : 'showCancelSubscription',
		'click #issue_future_invoice'                                          : 'showIssueFutureInvoice',
		'change #subscription-type'                                            : 'toggleBlockTierPercentageInputCallback'
	},

	initialize: function () {
		ajaxSendInit();
		this.MAX_TIERS = 10;
		this.currentTier = 0;
		this.setupCustomValidators();
		this.accountServiceTypeIndex = 0;
		this.addOnsIndex = 0;
		this.isBeingEdited = false;

		this.recalculateEndDate();

		// Only instantiate cancellation dialog for active subscriptions
		if (this.options.subscriptionStatus === 'active' || this.options.subscriptionStatus === 'effective') {
			this.subscriptionCancellationDialog = new CancelSubscriptionView();
		}

		// Recreate existing tiers
		if (!_.isEmpty(this.options.subscriptionTiers)) {
			_.each(this.options.subscriptionTiers, function (tier) {
				this.addSubscriptionTier(tier);
			}, this);

			// Clear last tier's upper bound
			$('.subscription-tier:last .subscription-tier-upper').val('');
		} else {
			// If we don't have any tiers, create an empty one
			this.addSubscriptionTier();
		}

		// Fill subscription add-ons (if any)
		if (!_.isEmpty(this.options.subscriptionAddOns)) {
			_.each(this.options.subscriptionAddOns, function (addOn) {
				this.addAddOnItem(addOn);
			}, this);
		} else {
			// If we don't have any add-on, create an empty one
			this.addAddOnItem();
		}

		// Fill subscription account configuration types
		if (this.options.hasServiceType && !_.isEmpty(this.options.subscriptionServiceConfigs)) {
			_.each(this.options.subscriptionServiceConfigs, function (serviceConfig) {
				this.addAccountServiceTypeConfiguration(serviceConfig);
			}, this);
		} else {
			this.addAccountServiceTypeConfiguration();
		}

		// Initialize date-picker fields
		var datePickerOptions = {dateFormat: 'mm/dd/yy'};

		if (!_.isEmpty(this.options.nextPossibleUpdateDate)) {
			datePickerOptions.minDate = new Date(this.options.nextPossibleUpdateDate);
		}
		$('#paymentTierEffectiveDate').datepicker(datePickerOptions);
		$('#addOnsEffectiveDate').datepicker(datePickerOptions);
		$('#signedDate').datepicker({dateFormat: 'mm/dd/yy'});

		this.render();

		// Only instantiate renewal dialog if a renewal request can be submitted
		if (this.options.subscriptionCanRenew) {
			this.subscriptionRenewDialog = new RenewSubscriptionView({
				termInMonths: parseInt(this.$('#subscription_period').val(), 10),
				endDate: new Date(this.$('#termination_date').text()),
				tiers: this.options.subscriptionTiers,
				isVOR: this.options.isVendorOfRecord
			});
		}
	},

	render: function () {
		this.initializeYearPicker();
		this.initializeFieldMasks();

		if (!this.hasVendorOfRecord()) {
			this.hideVendorOfRecordColumn();
		}

		if (!this.hasDiscountOptions()) {
			this.hideDiscountOptions();
		}

		if (!this.hasAddons()) {
			this.hideAddonsOptions();
		}

		// If subscription is approved, disable edition of fields for normal view
		if (_.contains(['active', 'effective', 'cancellation_pending', 'cancellation_approved'], this.options.subscriptionStatus)) {
			this.disableFieldsForActiveSubscription();
			$('#save_subscription_form').remove();
		}

		// Check if we can edit the subscription
		if (_.isEmpty(this.options.nextPossibleUpdateDate)) {
			$('#edit_subscription').parent('li').remove();
		}

		return this;
	},

	// Fill-in year selector for Effective Date
	initializeYearPicker: function () {
		var selectedYear = parseInt($('#effective_date_year :selected').val(), 10);
		$('#effective_date_year').empty();

		var currentYear = new Date().getFullYear();

		if (selectedYear < currentYear) {
			currentYear = selectedYear;
		}
		for (var year = currentYear; year <= (currentYear + 5); year++) {
			$('#effective_date_year').append($('<option>').val(year).text(year));
		}

		$('#effective_date_year').val(selectedYear);
	},

	initializeFieldMasks: function () {
		var elems = [
			'#number_of_months',
			'#paymentTermsDays'
		];
		this.setFieldMask(elems, {vMax: 999, mDec: 0});

		var currencyFields = [
			'#setUpFee',
			'#discountPerPeriod',
			'#subscription_tier_form input:text',
			'[name$=costPerPeriod]'
		];
		this.setAmountMask(currencyFields);
	},

	setFieldMask: function (selector, opts) {
		if (!_.isArray(selector)) {
			selector = [selector];
		}
		_.each(selector, function (sel) {
			try {
				$(sel).autoNumeric('destroy', opts);
			} catch (err) {
			}

			$(sel).autoNumeric('init', opts);
		});
	},

	setAmountMask: function (selector) {
		this.setFieldMask(selector, {
			vMax: 99999999,
			mDec: 2,
			aSep: ''
		});
	},

	recalculateEndDate: function () {
		var period = parseInt($('#subscription_period').val(), 10);
		var nMonths = parseInt($('#number_of_months').val(), 10);

		if (_.isNaN(period) || _.isNaN(nMonths) || period === 0) {
			return false;
		}

		if (nMonths === 0 || nMonths % period !== 0) {
			$('#termination_date').empty();
			this.showFieldError('#number_of_months',
				'Your subscription payment period is ' + $('#subscription_period :selected').text() + ', the term must be a multiple of ' + period);

			return false;
		} else {
			this.hideFieldError('#number_of_months');
		}

		// Calculate termination date
		var effectiveDate = this.getEffectiveDate();
		if (_.isNull(effectiveDate)) {
			return false;
		}

		var terminationDate = new Date(effectiveDate);
		terminationDate.setMonth(effectiveDate.getMonth() + nMonths, 0);  // 0 = previous month's last day

		$('#termination_date').text(terminationDate.format('m/d/yyyy'));
		return true;
	},

	hasVendorOfRecord: function () {
		return this.options.isVendorOfRecord || $('#service_type_configurations [value="vor"]:selected').size() > 0;
	},

	hideVendorOfRecordColumn: function () {
		$('#subscription_tier_form .vendor-of-record').hide();
		$('#subscription_tier_form').removeClass('subscription_tier_width');
	},

	showVendorOfRecordColumn: function () {
		$('#subscription_tier_form .vendor-of-record').show();
		$('#subscription_tier_form').addClass('subscription_tier_width');
	},

	addSubscriptionTier: function (tier) {
		if (this.currentTier < this.MAX_TIERS) {
			// Enable upper bound for previous tiers
			$('.subscription-tier-upper:lt(' + this.currentTier + ')').removeAttr('readonly');

			++this.currentTier;

			// Render a new tier
			$('#subscription_tier_form').append($.tmpl($('#subscription_tier_template').html(),
				{
					nTier: this.currentTier,		// Number of Tier
					idx: (this.currentTier - 1)	// Tier index in the array
				}
			));

			// Set field masks for the new tier and the previous tier's upper bound
			this.setAmountMask('.subscription-tier:last input:text');
			this.setAmountMask($('.subscription-tier-upper').eq(-2));

			// Show/hide action buttons
			$('.subscription-tier').not(':last')
				.find('.add-tier-btn')
				.addClass('dn')
				.end()
				.find('.remove')
				.removeClass('dn');

			if (this.hasVendorOfRecord()) {
				this.showVendorOfRecordColumn();
			} else {
				this.hideVendorOfRecordColumn();
			}

			// Pre-fill tier fields (if we receive a "tier" parameter)
			if (!_.isUndefined(tier) && !_.isNull(tier)) {
				$('.subscription-tier:last input[type="text"]').each(function (idx, field) {
					$(field).val(tier[idx]);
				});
			}
		}

		this.recalculateSubscriptionTiers();
	},

	// Remove a tier from the pricing ranges
	removeSubscriptionTier: function (evt) {
		$(evt.currentTarget).closest('.subscription-tier').remove();

		this.reassignTierNumbers();
		this.recalculateSubscriptionTiers();
	},

	// Reassign tier numbers
	reassignTierNumbers: function () {
		$('.subscription-tier').find('label:first').each(function (idx, label) {
			$(label).text('Tier ' + (idx + 1));

			$(label).closest('.subscription-tier').find(':text').each(function (n, elem) {
				$(elem).attr('name', elem.name.replace(/\[.+\]/, '[' + idx + ']'));
			});
		});

		this.currentTier = $('.subscription-tier').length;
	},

	// Clean subscription tiers that are not used
	cleanupSubscriptionTiers: function () {
		var firstNonNumericUpperBoundIndex = -1;

		// Set the last tier as the first whose upper bound is empty or NaN
		$('#subscription_tier_form .subscription-tier-upper').each(function (idx, elem) {
			var value = $(elem).val();

			if (!_.isNull(value)) {
				value = $.trim(value);
			}

			if (_.isEmpty(value) || _.isNaN(parseInt(value, 10))) {
				firstNonNumericUpperBoundIndex = idx;
				$(elem).val('');

				return false; // stop processing
			}
		});

		// If we don't have any empty or NaN upper-bound, set the last visible tier's upper-bound to blank
		if (firstNonNumericUpperBoundIndex < 0) {
			$('.subscription-tier:last .subscription-tier-upper').val('').attr('readonly', 'readonly');
		} else {
			// Otherwise, we remove the remaining "not used" tiers
			this.currentTier = firstNonNumericUpperBoundIndex + 1;	// we always have at least 1 tier
			$('.subscription-tier-upper:eq(' + firstNonNumericUpperBoundIndex + ')').attr('readonly', 'readonly');
			$('.subscription-tier:gt(' + firstNonNumericUpperBoundIndex + ')').remove();
		}

		// Make sure Vendor of Record amounts are set zero if not used
		if (!this.hasVendorOfRecord()) {
			$('.vendor-of-record input').val(0);
		}

		// Reassign tier numbers
		this.reassignTierNumbers();

		// Only show add button for last tier
		$('.subscription-tier:last')
			.find('.remove')
			.addClass('dn')
			.end()
			.find('.add-tier-btn')
			.removeClass('dn');
	},

	// Discount options radio button change
	discountOptionsOption: function (evt) {
		var elem = $(evt.currentTarget);
		if (elem.val() === 'yes') {
			this.showDiscountOptions();
		} else {
			this.hideDiscountOptions();
		}
	},

	hasDiscountOptions: function () {
		return ($('[name="hasDiscountOptions"]:checked').val() === 'yes');
	},

	hideDiscountOptions: function () {
		$('#discountOptions').hide();
	},

	showDiscountOptions: function () {
		$('#discountOptions').show();
	},

	// Add-ons radio button change
	addonsOption: function (evt) {
		var elem = $(evt.currentTarget);

		if (elem.val() === 'yes') {
			this.showAddonsOptions();
		} else {
			this.hideAddonsOptions();
		}
	},

	hasAddons: function () {
		return ($('[name="hasAddOns"]:checked').val() === 'yes');
	},

	hideAddonsOptions: function () {
		$('#AddOnsOptions').hide();
	},

	showAddonsOptions: function () {
		$('#AddOnsOptions').show();
	},

	addAddOnItem: function (item) {
		var newAddOn = $.tmpl($('#add_ons_template').html(), {idx: this.addOnsIndex});

		if (!_.isUndefined(item)) {
			$(newAddOn)
				.find('option[value="' + item.type + '"]')
				.attr('selected', 'selected')
				.end()
				.find('[name$=costPerPeriod]')
				.val(item.cost);
		}

		$('#AddOnsOptions').append(newAddOn);
		++this.addOnsIndex;

		// Show/hide action buttons
		$('.addOnItem').not(':last')
			.find('.add-addons-btn')
			.addClass('dn')
			.end()
			.find('.remove')
			.removeClass('dn');

		// Set field mask for the new add-on
		this.setAmountMask('.addOnItem:last input:text');
	},

	removeAddOnItem: function (event) {
		$(event.currentTarget).closest('.addOnItem').remove();
		--this.addOnsIndex;
	},

	// Remove add-ons that don't have both type and cost (empty)
	cleanupEmptyAddOnItems: function () {
		$('.addOnItem').each(function (idx, item) {
			var type = $('option:selected', item);
			var cost = $('input:text', item);

			if (type.index() === 0 && _.isEmpty(cost.val())) {
				$(item).remove();
			}
		});

		if ($('.addOnItem').length === 0) {
			// Keep at least one add-on
			this.addAddOnItem();
		} else {
			// Enable add button for last add-on
			var lastAddOn = $('.addOnItem:last');
			$('.add-addons-btn', lastAddOn).removeClass('dn');
			$('.remove', lastAddOn).addClass('dn');
		}
	},

	// Bind subscription form fields for validation (using regula.js)
	bindFieldsValidation: function () {
		var subscriptionFormElements = $('#subscription_form #effective_date_month')
			.add('#effective_date_year')
			.add('#subscription_period')
			.add('#number_of_months')
			.add('#paymentTermsDays')
			.add('.subscription-tier [name$=paymentAmount]')
			.add('#setUpFee');

		// Validate effective date for edit-mode
		this.subscriptionTiersEdited = false;
		this.subscriptionAddOnsEdited = false;
		this.subscriptionTypeOriginalValue = $('#subscription-type option:selected').val();
		this.blockTierPercentageOriginalValue = $('#block-tier-percentage').val();

		if (_.contains(['active', 'effective', 'cancellation_approved'], this.options.subscriptionStatus)) {
			// Payment tiers modification effective date
			if (this.changedTiers()) {
				subscriptionFormElements = subscriptionFormElements.add('#paymentTierEffectiveDate');
				this.subscriptionTiersEdited = true;
			} else {
				$('#paymentTierEffectiveDate').val('');
			}

			if (this.hasAddons() && this.changedAddons()) {
				// Add-ons modification effective date
				subscriptionFormElements = subscriptionFormElements.add('#addOnsEffectiveDate');
				this.subscriptionAddOnsEdited = true;
			} else {
				$('#addOnsEffectiveDate').val('');
			}
		}

		// Validate VOR amount
		if (this.hasVendorOfRecord()) {
			subscriptionFormElements = subscriptionFormElements.add('[name$=vendorOfRecordAmount]');
		}

		// Validate discount options
		if (this.hasDiscountOptions()) {
			subscriptionFormElements = subscriptionFormElements
				.add('[name=discountNumberOfPeriods]')
				.add('[name=discountPerPeriod]');
		}

		// Validate add-ons
		if (this.hasAddons()) {
			subscriptionFormElements = subscriptionFormElements
				.add('[name$=addOnTypeCode]')
				.add('[name$=costPerPeriod]');
		}

		// Unbind previous elements
		regula.unbind();

		// Bind form elements
		regula.bind({ elements: subscriptionFormElements.toArray() });
	},

	// Setup custom validator annotations for regula.js
	setupCustomValidators: function () {
		var self = this;

		// Validator to check that Term is multiple of period
		regula.custom({
			name: 'MultipleOfPeriod',
			defaultMessage: '{label} must be a positive multiple of Payment Period',
			validator: function () {
				var paymentPeriod = parseInt($('#subscription_period :selected').val(), 10);
				var termInMonths = parseInt($('#number_of_months').val(), 10);

				return !_.isNaN(paymentPeriod) && !_.isNaN(termInMonths) &&
					(paymentPeriod > 0) &&
					(termInMonths > 0) &&
					(termInMonths % paymentPeriod === 0);
			}
		});

		// Validator for payment amount range
		regula.custom({
			name: 'PaymentAmountRange',
			params: ['lower', 'upper'],
			defaultMessage: '{label} must be between {lower} and {upper}',
			validator: function (params) {
				var lower = parseFloat(params.lower);
				var upper = parseFloat(params.upper);

				return (!_.isNaN(lower) && !_.isNaN(upper)) &&
					(this.value >= lower) && (this.value <= upper);
			}
		});

		// Validator for edition effective date
		regula.custom({
			name: 'EditEffectiveDate',
			params: ['date'],
			defaultMessage: '{label} must be later than the latest invoiced payment period ({date})',
			validator: function () {
				var editEffectiveDate = moment(this.value);

				if (editEffectiveDate === null || !editEffectiveDate.isValid()) {
					return false;
				}

				if (!_.isEmpty(self.options.nextPossibleUpdateDate)) {
					var nextPossibleUpdate = moment(self.options.nextPossibleUpdateDate);

					return editEffectiveDate.diff(nextPossibleUpdate) >= 0;
				} else {
					return true;
				}
			}
		});

		// Validator for setup fee
		regula.compound({
			name: 'SetupFee',
			constraints: [
				{constraintType: regula.Constraint.NotEmpty},
				{constraintType: regula.Constraint.Real},
				{constraintType: regula.Constraint.Min, params: {min: 0, value: 0}}
			],
			defaultMessage: '{label} must be greater or equal to 0'
		});
	},

	validateSubscriptionForm: function () {
		var self = this;

		this.bindFieldsValidation();
		var validationErrors = regula.validate();

		// If there are validation errors, show error messages
		if (!_.isEmpty(validationErrors)) {
			_.each(validationErrors, function (error) {
				self.showFieldError('[name="' + _.first(error.failingElements).name + '"]', error.message, true);
			});

			// Validation failed
			return false;
		}

		// Do not allow effective date to be in the past
		if (!$('#effective_date_month').is(':disabled')) {
			var today = new Date();
			if (this.getEffectiveDate() <= today) {
				this.showFieldError('#effective_date_month', 'Effective date must be in the future');
				return false;
			}
		}

		// There can be only one service type configuration per country
		var selectedCountries = [];
		var duplicatedCountries = false;
		$('.serviceTypeConfig').find('select:first :selected').each(function (idx, option) {
			if (_.contains(selectedCountries, option.value)) {
				self.showFieldError($(option), 'There must be only one service type configuration per country');
				duplicatedCountries = true;
				return false;
			} else {
				selectedCountries.push(option.value);
			}
		});

		if (duplicatedCountries) {
			return false;
		}

		// Validation succeeded
		return true;
	},

	cleanupErrors: function () {
		// Cleanup previous errors
		$('.alert-message.success, .alert-message.error').remove();
		$('#dynamic_messages').empty();
		$('.inlineError').remove();
		$('.control-group').removeClass('error');
	},

	submitSubscriptionForm: function () {
		this.cleanupErrors();

		// Prepare subscription form for validation
		this.recalculateSubscriptionTiers();
		this.cleanupSubscriptionTiers();
		this.cleanupEmptyAddOnItems();

		// Perform subscription form validation
		if (!this.validateSubscriptionForm()) {
			wmNotify({
				type: 'danger',
				message: 'Subscription form contains errors. Check the highlighted fields.'
			});
			$('html, body').animate({scrollTop: 0}, 200);
			return false;
		}

		// If there aren't any modifications, don't submit the form
		if (this.isBeingEdited) {
			if (!(this.subscriptionTiersEdited || this.subscriptionAddOnsEdited || this.changedSubscriptionType(this.subscriptionTypeOriginalValue) || this.changedBlockTierPercentage(this.blockTierPercentageOriginalValue))) {
				alert('Neither payment tiers, professional services, subscription type, nor block tier percentage were modified. Cancelling submission.');

				return false;
			}
		}

		// When subscription is pending approval, confirm resubmission
		if (this.options.subscriptionStatus === 'pending_approval') {
			this.confirmModal = wmModal({
				autorun: true,
				title: 'Confirm',
				destroyOnClose: true,
				content: confirmActionTemplate({
					message: 'Are you sure you want to resubmit the subscription application?'
				})
			});

			$('.cta-confirm-yes').on('click', function () {
				$('#subscription_form :input').removeAttr('disabled');
				$('#subscription_form').submit();
			});

			return;
		}

		// Calculate the "Cost per period" as the lower tier's sum of payment-amount and VOR amount
		let costPerPeriod = parseFloat($('[name="pricingRanges[0].paymentAmount"]').val());
		if (this.hasVendorOfRecord()) {
			costPerPeriod += parseFloat($('[name="pricingRanges[0].vendorOfRecordAmount"]').val());
		}

		const effectiveDate = this.getEffectiveDate();
		const effectiveMonth = effectiveDate.getMonth();
		const periodInMonths = parseInt($('#subscription_period').val(), 10);
		const numberOfMonths = parseInt($('#number_of_months').val(), 10);
		const periodsAnnually = Math.min((numberOfMonths / periodInMonths), (12 / periodInMonths));
		const periods = [];

		for (var n = 0; n < periodsAnnually; ++n) {
			let startDate = new Date(effectiveDate);
			let endDate = new Date(effectiveDate);

			startDate.setMonth(effectiveMonth + (n * periodInMonths));
			endDate.setMonth(effectiveMonth + ((n + 1) * periodInMonths), 0);

			periods.push({
				startDate: startDate.format('m/d/yyyy'),
				endDate: endDate.format('m/d/yyyy')
			});
		}

		const confirmSubscriptionRequestData = {
			startDate: effectiveDate.format('m/d/yyyy'),
			endDate: $('#termination_date').text(),
			paymentPeriod: $('#subscription_period :selected').text(),
			costPerPeriod,
			vendorOfRecord: this.hasVendorOfRecord() ? 'Y' : 'N',
			periods
		};

		wmModal({
			title: 'Confirm Subscription Request',
			content: ConfirmSubscriptionTemplate(confirmSubscriptionRequestData),
			autorun: true,
			controls: [
				{
					text: 'Cancel',
					close: true
				},
				{
					text: 'Confirm & Submit',
					primary: true,
					close: true
				}
			],
			customHandlers: [
				{
					event: 'click',
					selector: '.wm-modal--control.-primary',
					callback: (event) => {
						event.preventDefault();
						$('#subscription_form :input').removeAttr('disabled');

						// When editing, prevent the submission of unmodified items
						if (this.isBeingEdited) {
							// If subscription tiers were not modified, don't send them
							// unless subscription type or block tier percentage was modified
							if (!this.subscriptionTiersEdited && !(this.changedSubscriptionType() || this.changedBlockTierPercentage())) {
								$('.subscription-tier input').attr('name', '');
							}

							// If add-ons were not modified, don't send them
							if (!this.subscriptionAddOnsEdited) {
								$('.addOnItem :input').attr('name', '');
							} else {
								// Only send new add-ons
								$('.addOnItem:lt(' + this.options.subscriptionAddOns.length + ') :input').attr('name', '');
							}
						}

						$('#subscription_form').submit();
					}
				}
			]
		});
	},

	// Disable edition of fields once the subscription was approved
	disableFieldsForActiveSubscription: function () {
		// Disabled elements
		var selectors = [
			'#effective_date_month',
			'#effective_date_year',
			'#subscription_period',
			'[name="vendorOfRecord"]',
			'[name="hasDiscountOptions"]',
			'#discountNumberOfPeriods',
			'[name="autoRenewal"]',
			'[name="hasAddOns"]',
			'.addOnItem select',
			'.serviceTypeConfig *',
			'#subscription_tier_form .add-tier-btn',
			'#submit_subscription',
			'#signedDate',
			'#subscription-type',
			'#block-tier-percentage'
		];
		$(selectors.join(', ')).attr('disabled', 'disabled');

		$('#signedDate').datepicker('disable');

		// Read-only elements
		selectors = [
			'#number_of_months',
			'#paymentTermsDays',
			'#setUpFee',
			'#cancellationOption',
			'#additionalNotes',
			'.subscription-tier input',
			'#discountPerPeriod',
			'.addOnItem input',
			'#signedDate'
		];
		$(selectors.join(', ')).attr('readonly', 'readonly');

		// Hidden elements
		$('.subscription-tier .remove').addClass('dn');
		$('#paymentTierEffectiveDate').closest('div').addClass('dn');
		$('#addOnsEffectiveDate').closest('div').addClass('dn');
		$('.serviceTypeConfig .add-btn, .serviceTypeConfig .remove').addClass('dn');
		$('.addOnItem .add-addons-btn, .addOnItem .remove').addClass('dn');

		// Avoid removal of existing add-ons
		$('.addOnItem .remove').remove();
	},

	// Enable fields that can be edited in an approved subscription
	enableEditableFields: function () {
		// Enable edition of tier fields except for lower-bound
		$('.subscription-tier').find('input:gt(0)').removeAttr('readonly');

		// Make last tier's upper bound read-only
		$('.subscription-tier:last .subscription-tier-upper').attr('readonly', 'readonly');

		// Display effective date field for payment tier and add-ons modifications
		$('#paymentTierEffectiveDate').closest('div').removeClass('dn');

		$('#addOnsEffectiveDate').closest('div').removeClass('dn');

		// Enable add-tier button
		$('#subscription_tier_form .add-tier-btn').removeAttr('disabled');

		// Show remove-tier action
		$('.subscription-tier').not(':last').find('.remove').removeClass('dn');

		// Enable add-addon button
		$('.addOnItem .add-addons-btn:last').removeClass('dn');

		// Enable submit button
		$('#submit_subscription').removeAttr('disabled');

		// Enable subscription type select
		let selectedSubscriptionType = $('#subscription-type').removeAttr('disabled').find('option:selected').val();

		// Enable block tier percentage inpout
		$('#block-tier-percentage').removeAttr('disabled');
		this.toggleBlockTierPercentageInput(selectedSubscriptionType);
		
		// Subscription is being edited
		this.isBeingEdited = true;
	},

	showRenewSubscription: function () {
		this.subscriptionRenewDialog.render();
	},

	showCancelSubscription: function () {
		this.subscriptionCancellationDialog.render();
	},

	recalculateSubscriptionTiers: function () {
		for (var i = 0; i < this.currentTier; ++i) {
			if (i === 0) {
				// Lower tier
				$('[name="pricingRanges[0].minimum"]').val(0);
			} else {
				// Set this tier's minimum to the maximum of the previous tier
				var previousTierMaximum = parseFloat($('[name="pricingRanges[' + (i - 1) + '].maximum"]').val()) || '';

				$('[name="pricingRanges[' + i + '].minimum"]').val(
					previousTierMaximum
				);
			}
		}
	},

	// Calculate the subscription's effective date
	getEffectiveDate: function () {
		var dateMonth = parseInt($('#effective_date_month').val(), 10);
		var dateYear = parseInt($('#effective_date_year').val(), 10);

		if (_.isNaN(dateMonth) || _.isNaN(dateYear)) {
			return null;
		}

		var effectiveDate = new Date();

		// effective date always starts 1st day of month
		effectiveDate.setMonth(dateMonth, 1);
		effectiveDate.setYear(dateYear);

		return effectiveDate;
	},

	// Save subscription but don't submit it for approval
	saveSubscriptionForm: function () {
		this.cleanupErrors();

		// Prepare subscription form for validation
		this.recalculateSubscriptionTiers();
		this.cleanupSubscriptionTiers();
		this.cleanupEmptyAddOnItems();

		// Perform subscription form validation
		if (!this.validateSubscriptionForm()) {
			wmNotify({
				type: 'danger',
				message: 'Subscription form contains errors. Check the highlighted fields.'
			});
			$('html, body').animate({scrollTop: 0}, 200);
			return false;
		}

		$('#save_subscription_form').attr('disabled', 'disabled');
		$.ajax({
			type: 'POST',
			url: '/admin/manage/company/save_subscription',
			data: $('#subscription_form').serialize(),
			success: function (response) {
				if (response.successful) {
					// Set subscription ID appropriately
					if (!_.isUndefined(response.data)) {
						$('[name=subscriptionConfigurationId]').val(response.data.subscription_id);
					}
					wmNotify({message: response.messages[0]});
				} else {
					_.each(response.messages, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
				$('#save_subscription_form').removeAttr('disabled');
			},
			error: function () {
				wmNotify({
					message: 'There was an error saving the subscription.',
					type: 'danger'
				});
				$('#save_subscription_form').removeAttr('disabled');
			}
		});
		$('html, body').animate({scrollTop: 0}, 200);
	},

	showFieldError: function (selector, msg, keepPrevious) {
		var errMsg = $('<span>')
			.addClass('inlineError')
			.text(msg);

		var div = $(selector).closest('.control-group');

		div.addClass('error');

		if (_.isUndefined(keepPrevious) || !keepPrevious) {
			div.find('.inlineError')
				.remove()
				.end();
		}

		div.append(errMsg);
	},

	hideFieldError: function (selector) {
		$(selector).each(function (idx, elem) {
			var e = $(elem);

			e.closest('.control-group')
				.removeClass('error')
				.children()
				.remove('.inlineError');
		});
	},

	showIssueFutureInvoice: function (event) {
		event.preventDefault();

		$.ajax({
			type: 'GET',
			url: $(event.currentTarget).attr('href'),
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Issue Future Invoice',
						destroyOnClose: true,
						content: response
					});
				}
			}

		});
	},

	removeAccountServiceTypeConfiguration: function (event) {
		$(event.currentTarget).closest('.serviceTypeConfig').remove();
		--this.accountServiceTypeIndex;
		this.checkServiceTypeVOR();
	},

	addAccountServiceTypeConfiguration: function (item) {
		var newConfigItem = $.tmpl($('#service_type_template').html(), {idx: this.accountServiceTypeIndex});

		if (!_.isUndefined(item)) {
			$(newConfigItem)
				.find('option[value="' + item.countryCode + '"]')
				.attr('selected', 'selected')
				.end()
				.find('option[value="' + item.accountServiceTypeCode + '"]')
				.attr('selected', 'selected');
		}

		$('#service_type_configurations').append(newConfigItem);
		++this.accountServiceTypeIndex;

		// Show/hide action buttons
		$('.serviceTypeConfig:not(:last)')
			.find('.add-btn')
			.addClass('dn')
			.end()
			.find('.remove')
			.removeClass('dn');
	},

	checkServiceTypeVOR: function () {
		if ($('#service_type_configurations [value="vor"]:selected').size() > 0) {
			this.showVendorOfRecordColumn();
		} else {
			this.hideVendorOfRecordColumn();
		}
	},

	// Returns true if payment tiers were edited (for active subscriptions)
	changedTiers: function () {
		var oldTiers = this.options.subscriptionTiers;
		var newTiers = [];

		// Set oldTiers last tier's upper bound to Infinity
		oldTiers[oldTiers.length - 1][1] = Infinity;

		// Obtain new tiers
		$('.subscription-tier').each(function (idx, div) {
			var tier = $('input[type="text"]', div).map(function (i, e) {
				var value = parseFloat(e.value);

				return (_.isNaN(value) ? Infinity : value);
			});

			newTiers.push(tier.toArray());
		});

		// Check if all tiers are equal
		if (oldTiers.length === newTiers.length) {
			var changed = false;

			$.each(oldTiers, function (idx, tier) {
				// If we don't have the same elements in the same order, the tier has changed.
				changed = !_.all(_.zip(tier, newTiers[idx]), function (pair) {
					return pair[0] === pair[1];
				});

				if (changed) {
					return false; //stop processing
				}
			});
			return changed;
		} else {
			return true;
		}
	},

	// Returns true if add-ons were edited (for active subscriptions)
	changedAddons: function () {
		var oldAddOns = this.options.subscriptionAddOns;

		return (oldAddOns.length !== $('.addOnItem').length);
	},

	changedSubscriptionType: function (subscriptionTypeOriginalValue) {
		return subscriptionTypeOriginalValue !== $('#subscription-type option:selected').val();
	},

	changedBlockTierPercentage: function (blockTierPercentageOriginalValue) {
		return blockTierPercentageOriginalValue !== $('#block-tier-percentage').val();
	},

	toggleBlockTierPercentageInput: function (subscriptionType) {
		$('#block-tier-percentage').prop('disabled', subscriptionType !== 'block');
	},

	toggleBlockTierPercentageInputCallback: function (e) {
		this.toggleBlockTierPercentageInput($(e.currentTarget).find('option:selected').val());
	}
});
