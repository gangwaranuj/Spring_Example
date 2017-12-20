'use strict';

import _ from 'underscore';
import $ from 'jquery';
import wmNotify from '../../../funcs/wmNotify';
import regula from '../../../dependencies/regula.min';
import Backbone from 'backbone';
import wmModal from '../../../funcs/wmModal';

export default Backbone.View.extend({
	el: '#renew_subscription_modal',
	tierTemplate: $('#renewal_tier_template').html(),

	initialize (options) {
		// Set last tier's maximum to Infinity
		_.last(options.tiers)[1] = Infinity;

		// Fill payment tiers
		_.each(options.tiers, function (tier, idx) {
			this.$('#renew_pricing_tiers').append($.tmpl(this.tierTemplate, {
				idx: idx,
				min: tier[0],
				max: tier[1],
				payAmount: tier[2],
				vorAmount: tier[3]
			}));
		}, this);

		this.expandedWidth = (options.isVOR ? (this.normalWidth + 100) : this.normalWidth);

		this.clearErrors();
	},

	render: function () {
		this.$('.messages').empty();
		wmModal({
			autorun: true,
			content: this.$el.html(),
			title: this.$el.attr('title') || '',
			customHandlers: [
				{
					event: 'change',
					selector: '[name="modifyPricing"]',
					callback: (e) => { this.showPricing(e) }
				},
				{
					event: 'change',
					selector: '[name="numberOfMonths"]',
					callback: () => { this.recalculateEndDate() }
				}
			]
		});
		$('.wm-modal--slide').css('max-width', '60em');

		return this;
	},

	recalculateEndDate () {
		var newEndDate = new Date(this.options.endDate);

		if (this.validTerm()) {
			newEndDate.setMonth(newEndDate.getMonth() + this.getRenewalTerm());
			$('.renewal_end_date').text(newEndDate.format('m/d/yyyy'));
			$('[name="numberOfPeriods"]').val(this.getRenewalTerm() / this.options.termInMonths);

			$('[name="numberOfMonths"]')
				.removeClass('fieldError')
				.siblings('.inlineError').hide();
		} else {
			$('.renewal_end_date').empty();
			$('[name="numberOfMonths"]')
				.addClass('fieldError')
				.siblings('.inlineError').show();
		}
	},

	showPricing (event) {
		$('#renew_pricing_tiers').toggleClass('dn', event.currentTarget.value === 'no');

		var width = $('#renew_pricing_tiers').hasClass('dn') ? this.normalWidth : this.expandedWidth;
	},

	submit () {
		return this.validate();
	},

	validate () {
		var isValid = true;

		// Unbind previous elements and clear errors
		regula.unbind();
		this.clearErrors();

		// Validate form
		var formElements;
		if ($('#renew_pricing_tiers').hasClass('dn')) {
			// Validate only renewal term (no pricing modifications)
			formElements = this.$('[name="numberOfMonths"]').toArray();
		} else {
			// Validate all inputs
			formElements = this.$(':text').toArray();
		}
		regula.bind({elements: formElements});

		var validationErrors = regula.validate({elements: formElements});
		isValid = this.validTerm() && _.isEmpty(validationErrors);

		if (!isValid) {
			_.each(validationErrors, function (error) {
				var elem = $(_.first(error.failingElements));

				// For input-prepend's add the prepended element
				if (elem.parent().is('.input-prepend')) {
					elem = elem.add(elem.prev());
				}
				elem.addClass('fieldError');
			});

			this.showErrors();
		}

		return isValid;
	},

	getRenewalTerm () {
		return parseInt($('[name="numberOfMonths"]').val(), 10);
	},

	// Check that renewal term is multiple of subscription period in months
	validTerm () {
		var newTerm = this.getRenewalTerm();

		return (!_.isNaN(newTerm) && (newTerm > 0) && (newTerm % this.options.termInMonths === 0));
	},

	clearErrors () {
		$('[name="numberOfMonths"]').siblings('.inlineError').hide();
		$('.fieldError').removeClass('fieldError');
		$('.messages').empty();
	},

	showErrors () {
		var messages = [];

		if ($('[name="numberOfMonths"]').hasClass('fieldError') || !this.validTerm()) {
			messages.push('Renewal term must be multiple of ' + this.options.termInMonths);
		}

		if ($('.input-prepend .fieldError').size()) {
			messages.push('Each amount field must be between 0 and 100000000');
		}

		_.each(messages, function (theMessage) {
			wmNotify({
				message: theMessage,
				type: 'danger'
			});
		});
	}

});
