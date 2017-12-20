'use strict';

import $ from 'jquery';
import AbstractFormView from './abstract_compliance_rule_form_view';
import wmMaskInput from '../funcs/wmMaskInput';

export default AbstractFormView.extend({

	getViewLabel: function () {
		throw 'getViewLabel():NoImplementationException';
	},

	getMaxValue: function () {
		throw 'getMaxValue():NoImplementationException';
	},

	getInterval: function () {
		throw 'getInterval():NoImplementationException';
	},

	getIntervalText: function () {
		throw 'getIntervalText():NoImplementationException';
	},

	render: function () {
		this.$el.html(this.template({}));

		this.$el.find('[data-placeholder="complianceable"]').html(this.formTemplate({}));

		this.$el.find('[data-placeholder="add-button"]').html(this.buttonTemplate({
			label: this.formLabel,
			enabled: false
		}));

		wmMaskInput({ root: this.$el, selector: '#maximum-assignments' }, 'Z9999', {
			translation: {
				'Z': { pattern: /[1-9]/, optional: false }
			}
		});

		this.$('#maximum-assignments').on('keyup', function () {
			var disabled = true;
			if ($(this).val() && $('#interval option:enabled').length) { disabled = false; }
			$('button[data-action="add"]').attr('disabled', disabled);
		});

		return this;
	},

	resetForm: function () {
		// disable options already in compliance rules
		this.complianceRules.each(function(complianceRule) {
			this.$('#interval option[value="' + complianceRule.get('periodType') + '"]').attr('disabled', true);
		}, this);
		// select the first non-disabled option
		this.$('#interval option:enabled:first').prop('selected', true);
		// clear out the input field
		this.$('#maximum-assignments').val('');
		$('button[data-action="add"]').attr('disabled', true);
	},

	cartTrashHandler: function (complianceRule) {
		this.$('#interval option[value="' + complianceRule.get('periodType') + '"]').attr('disabled', false);
		this.resetForm();
	},

	getComplianceRule: function () {
		return {
			$type: this.formType,
			viewLabel: this.getViewLabel(),
			maxAssignments: this.getMaxValue(),
			periodType: this.getInterval(),
			periodValue: this.getMaxValue()
		};
	}
});
