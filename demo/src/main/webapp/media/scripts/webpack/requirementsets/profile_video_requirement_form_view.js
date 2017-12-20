'use strict';

import AbstractView from './abstract_requirement_form_view';

export default AbstractView.extend({
	render: function() {
		this.$el.html(this.template({}));

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
		var requirementType = this.requirementTypes.findWhere({name: this.formType});
		var name = requirementType.get('defaultRequirableName');
		var mandatory = this.$('[data-placeholder="mandatory"] input').prop("checked");

		return {
			$type: this.formType,
			name:  name,
			mandatory: mandatory
		};
	}
});
