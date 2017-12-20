'use strict';

import $ from 'jquery';
import AbstractView from './abstract_requirement_form_view';
import RatingsCollection from './ratings_collection';

export default AbstractView.extend({
	collection: RatingsCollection,
	render: function() {
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
	},

	getRequirement: function() {
		var ratingValue = parseInt(this.$('[data-selections="requirables"]').val(), 10);
		var requirable = this.collection && this.collection.findWhere({id: ratingValue});
		var name = (requirable && requirable.get('name'));
		var mandatory = this.$('[data-placeholder="mandatory"] input').prop('checked');

		return {
			$type: this.formType,
			name:  name,
			value: ratingValue,
			mandatory: mandatory
		};
	}
});
