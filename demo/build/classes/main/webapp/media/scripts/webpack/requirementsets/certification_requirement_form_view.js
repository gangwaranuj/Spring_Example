'use strict';

import $ from 'jquery';
import _ from 'underscore';
import AbstractView from './abstract_requirement_form_view';
import IndustriesCollection from './industries_collection';
import ProvidersCollection from './providers_collection';

export default AbstractView.extend({
	events: function(){
		// inherits parent events
		return _.extend({}, AbstractView.prototype.events,{
			'change select[data-selections="industries"]': 'refreshProviders',
			'change select[data-selections="providers"]': 'refreshRequirables'
		});
	},

	render: function() {
		this.$el.html(this.template({}));
		this.industriesCollection = new IndustriesCollection();
		this.industriesCollection.fetch().then(_.bind(function() {
			this.$el.find('[data-placeholder="partitioner"]').append(this.selectTemplate({
				label: 'Industries',
				lowerLabel: 'industries',
				selectionType: 'industries',
				collection: this.industriesCollection
			}));
		}, this));

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

	refreshProviders: function (e) {
		var self = this;
		e.preventDefault();
		this.providersCollection = new ProvidersCollection();
		this.providersCollection.industryId = $(e.currentTarget).val();
		this.providersCollection.fetch({
			success: function (collection, response, options) {
				self.$el.find('[data-selections="providers"]').parents('.control-group:first').remove();
				self.$el.find('[data-placeholder="partitioner"]').append(self.selectTemplate({
					label: 'Providers',
					lowerLabel: 'providers',
					selectionType: 'providers',
					collection: collection
				}));
			},
			error: function () {
				alert('error loading.');
			}
		});
	},

	refreshRequirables: function (e) {
		var self = this;
		e.preventDefault();

		this.collection.industryId = this.providersCollection.industryId;
		this.collection.providerId = $(e.currentTarget).val();
		this.partitionName = $(e.currentTarget).find('option:selected').text();

		this.collection.fetch({
			success: function () {
				self.disableRequirables();
				self.$el.find('[data-selections="requirables"]').parents('.control-group:first').remove();
				self.$el.find('[data-placeholder="requirable"]').html(self.selectTemplate({
					label: self.formLabel,
					lowerLabel: self.formLabel.toLowerCase(),
					selectionType: 'requirables',
					collection: self.collection
				}));
			},
			error: function () {
				alert('error loading.');
			}
		});
	}
});

