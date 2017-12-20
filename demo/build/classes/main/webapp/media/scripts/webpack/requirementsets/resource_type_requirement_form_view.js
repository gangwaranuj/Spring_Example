'use strict';

import AbstractView from './abstract_requirement_form_view';

export default AbstractView.extend({
	render: function () {
		this.$el.html(this.template({}));

		this.$el.find('[data-placeholder="requirable"]').html(this.selectTemplate({
			label: this.formLabel + 's',
			lowerLabel: (this.formLabel + 's').toLowerCase(),
			selectionType: 'requirables',
			collection: this.collection
		}));

		this.$el.find('[data-placeholder="add-button"]').html(this.buttonTemplate({
			label: this.formLabel,
			enabled: false
		}));

		this.$el.find('[data-placeholder="mandatory"]').html(this.mandatoryTemplate({
			enabled: true,
			isMandatoryRequirement: this.options.isMandatoryRequirement
		}));

		return this;
	}
});
