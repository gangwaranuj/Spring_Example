'use strict';

import _ from 'underscore';
import AbstractView from './abstract_requirement_form_view';
import Template from './templates/company-work-requirement-form.hbs';

export default AbstractView.extend({
	formTemplate: Template,


	render: function() {
		this.$el.html(this.template({}));

		this.$el.find('[data-placeholder="requirable"]').html(this.selectTemplate({
			label: 'Select a company your resource must have done work for',
			lowerLabel: 'Select a company your resource must have done work for'.toLowerCase(),
			selectionType: 'requirables',
			collection: this.collection
		}));

		this.$el.find('[data-placeholder="requirable"]').after(this.formTemplate({}));

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
		var parentRequirement = AbstractView.prototype.getRequirement.apply(this); // call the parent method
		var minimumWorkCount = this.$('#minimum-work-count').val();

		return _.extend(parentRequirement, {
			name: 'Minimum ' + minimumWorkCount + " assignments with this company",
			minimumWorkCount: minimumWorkCount
		});
	}
});
