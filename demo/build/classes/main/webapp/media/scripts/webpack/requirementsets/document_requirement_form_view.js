'use strict';

import $ from 'jquery';
import _ from 'underscore';
import AbstractView from './abstract_requirement_form_view';
import DocumentsCollection from './documents_collection';

export default AbstractView.extend({
	collection: DocumentsCollection,
	formTemplate: _.template($('#document-expires-form-tmpl').html()),

	render: function() {
		this.$el.html(this.template({}));

		this.$el.find('[data-placeholder="requirable"]').html(this.selectTemplate({
			label: this.formLabel + 's',
			lowerLabel: (this.formLabel + 's').toLowerCase(),
			selectionType: 'requirables',
			collection: this.collection
		}));

		this.$el.find('[data-placeholder="requirable"]').after(this.formTemplate({}));

		this.$el.find('[data-placeholder="add-button"]').html(this.buttonTemplate({
			label: this.formLabel,
			enabled: false
		}));

		this.$el.find('[data-placeholder="mandatory"]').html(this.mandatoryTemplate({
			enabled: true,
			isMandatoryRequirement: this.options.isMandatoryRequirement
		}));

		return this;
	},

	getRequirement: function() {
		var requirableId = parseInt(this.$('[data-selections="requirables"]').val(), 10);

		if (isNaN(requirableId)) {
			requirableId = this.$('[data-selections="requirables"]').val();
		}

		var mandatory = this.$('[data-placeholder="mandatory"] input').prop('checked');
		var requirable = this.collection && this.collection.findWhere({id: requirableId});
		var requirementType = this.requirementTypes.findWhere({name: this.formType});
		var name = (requirable && requirable.get('name')) || requirementType.get('defaultRequirableName');
		if (this.partitionName) { name = this.partitionName + " - " + name; }

		var requiresExpirationDate = this.$('#requires-expiration-date').prop('checked');

		return {
			$type: this.formType,
			requirable: {
				name:  name,
				id: requirableId
			},
			mandatory: mandatory,
			requiresExpirationDate: requiresExpirationDate
		};
	}
});
