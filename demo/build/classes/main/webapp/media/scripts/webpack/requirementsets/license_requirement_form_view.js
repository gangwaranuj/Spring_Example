'use strict';

import $ from 'jquery';
import _ from 'underscore';
import AbstractView from './abstract_requirement_form_view';
import StatesCollection from './states_collection'; 

export default AbstractView.extend({
	statesCollection: StatesCollection,

	events: function(){
		// inherits parent events
		return _.extend({}, AbstractView.prototype.events, {
			'change select[data-selections="states"]': 'refreshRequirables'
		});
	},

	render: function() {
		this.$el.html(this.template({}));

		this.statesCollection = new StatesCollection();
		this.statesCollection.fetch().then(_.bind(function() {
			this.$el.find('[data-placeholder="partitioner"]').append(this.selectTemplate({
				label: 'States',
				lowerLabel: 'states',
				selectionType: 'states',
				collection: this.statesCollection
			}));
		}, this));

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
	},

	refreshRequirables: function(e) {
		var self = this;
		e.preventDefault();
		var state = $(e.currentTarget);
		this.collection.stateId = this.partitionName = state.val();

		this.collection.fetch({
			success: function () {
				self.disableRequirables();
				self.$el.find('[data-placeholder="requirable"]').html(self.selectTemplate({
					label: self.formLabel,
					lowerLabel: self.formLabel.toLowerCase(),
					selectionType: 'requirables',
					collection: self.collection
				}));
			},
			error: function () {
				alert("error loading.");
			}
		});
	}
});
