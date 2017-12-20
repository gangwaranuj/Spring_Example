'use strict';

import Template from './templates/ontime-requirement-form-tmpl.hbs';
import $ from 'jquery';
import AbstractView from './abstract_requirement_form_view';

export default AbstractView.extend({
	formTemplate: Template,

	render: function() {
		this.$el.html(this.template({}));

		this.$el.find('[data-placeholder="requirable"]').html(this.formTemplate({}));

		this.$el.find('[data-placeholder="add-button"]').html(this.buttonTemplate({
			label: this.formLabel,
			enabled: true
		}));

		this.$el.find('[data-placeholder="mandatory"]').html(this.mandatoryTemplate({
			enabled: true,
			isMandatoryRequirement: this.options.isMandatoryRequirement
		}));

		return this;
	},

	getRequirement: function() {
		var minimumPercentage = this.$('#minimum-percentage').val();
		var mandatory = this.$('[data-placeholder="mandatory"] input').prop("checked");

		return {
			$type: this.formType,
			name: minimumPercentage + '% within last 3 months',
			minimumPercentage: minimumPercentage,
			mandatory: mandatory
		};
	}
});
