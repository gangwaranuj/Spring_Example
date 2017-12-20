'use strict';

import Template from './templates/deliverable-ontime-requirement-form.hbs';
import $ from 'jquery';
import AbstractView from './abstract_requirement_form_view';

export default AbstractView.extend({
	formTemplate: Template,

	render: function () {
		var $el = this.$el;

		$el.html(this.template({}));

		$el.find('[data-placeholder="requirable"]').html(this.formTemplate({}));

		$el.find('[data-placeholder="add-button"]').html(this.buttonTemplate({
			label: this.formLabel,
			enabled: true
		}));

		$el.find('[data-placeholder="mandatory"]').html(this.mandatoryTemplate({
			enabled: true,
			isMandatoryRequirement: this.options.isMandatoryRequirement
		}));

		return this;
	},

	getRequirement: function () {
		var minimumPercentage = this.$('#minimum-deliverable-percentage').val();
		var mandatory = this.$('[data-placeholder="mandatory"] input').prop("checked");

		return {
			$type: this.formType,
			name: minimumPercentage + '% within last 3 months',
			minimumPercentage: minimumPercentage,
			mandatory: mandatory
		};
	}
});
