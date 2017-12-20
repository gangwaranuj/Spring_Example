'use strict';

import _ from 'underscore';
import $ from 'jquery';
import Backbone from 'backbone';

export default Backbone.View.extend({
	el: '#transactional_form',

	events: {
		// Transactional actions
		'change .transactional-tier-upper' : 'recalculateTransactionalTiers',
		'click .add-tier-btn'              : 'addTransactionalTier'
	},

	initialize: function () {
		this.MAX_TIERS = 3;
		this.currentTier = $('.transactional-tier').length;

		// Check that we show at least one transactional tier
		if (this.currentTier === 0) {
			this.addTransactionalTier();
		}

		// Disable edition for last upper bound (always "infinity")
		$('.transactional-tier:last .transactional-tier-upper').val('').attr('readonly', 'readonly');

		_.bindAll(this, 'submitTransactional');
		$('#submit_transactional_form button').click(this.submitTransactional);
	},

	addTransactionalTier: function () {
		if (this.currentTier < this.MAX_TIERS) {
			// Enable upper bound for previous tiers
			$('.transactional-tier-upper:lt(' + this.currentTier + ')').removeAttr('readonly');

			++this.currentTier;

			// Render a new tier
			$('#transactional_form').append($.tmpl($('#transactional_tier_template').html(),
				{
					tierNum: this.currentTier,			// Number of Tier
					idx: (this.currentTier - 1)	// Tier index in the array
				}
			));

			// Hide the "Add Tier" button once we reached the maximum number of tiers
			if (this.currentTier === this.MAX_TIERS) {
				$('#transactional_form .add-tier-btn').hide();
			}
		}

		this.recalculateTransactionalTiers();
	},

	recalculateTransactionalTiers: function () {
		for (var i = 0; i < this.currentTier; ++i) {
			if (i === 0) {
				// Lower tier
				$('[name="workFeeBands[0].minimum"]').val(0);
			} else {
				// Set this tier's minimum to the maximum of the previous tier
				var previousTierMaximum = parseFloat($('[name="workFeeBands[' + (i - 1) + '].maximum"]').val()) || '';

				$('[name="workFeeBands[' + i + '].minimum"]').val(
					previousTierMaximum
				);
			}
		}
	},

	// Clean transactional tiers that are not used
	cleanupTransactionalTiers: function () {
		var firstNonNumericUpperBoundIndex = -1;

		// Set the last tier as the first whose upper bound is empty or NaN
		$('#transactional_form .transactional-tier-upper').each(function (idx, elem) {
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
			$('.transactional-tier:last .transactional-tier-upper').val('').attr('readonly', 'readonly');
		} else {
			// Otherwise, we remove the remaining "not used" tiers
			this.currentTier = firstNonNumericUpperBoundIndex + 1;	// we always have at least 1 tier
			$('.transactional-tier-upper:eq(' + firstNonNumericUpperBoundIndex + ')').attr('readonly', 'readonly');
			$('.transactional-tier:gt(' + firstNonNumericUpperBoundIndex + ')').remove();
		}

		if (this.currentTier < this.MAX_TIERS) {
			$('#transactional_form .add-tier-btn').show();
		}
	},

	submitTransactional: function () {
		// Cleanup previous errors
		$('.alert-message.success, .alert-message.error').remove();

		this.recalculateTransactionalTiers();
		this.cleanupTransactionalTiers();

		$(this.el).submit();
	}
});
