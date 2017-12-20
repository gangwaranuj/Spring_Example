'use strict';

import $ from 'jquery';
import _ from 'underscore';
import AbstractView from './abstract_requirement_form_view';
import IndustriesCollection from './industries_collection';
import wmNotify from '../funcs/wmNotify';
import wmMaskInput from '../funcs/wmMaskInput';
import Template from './templates/insurance-requirement-form.hbs';

export default AbstractView.extend({
	formTemplate: Template,

	events: function () {
		// inherits parent events
		return _.extend({}, AbstractView.prototype.events,{
			'change select[data-selections="industries"]': 'refreshRequirables'
		});
	},

	render: function() {
		this.$el.html(this.template({}));
		this.industriesCollection = new IndustriesCollection();
		this.industriesCollection.fetch().then(_.bind(function() {
			this.$el.find('[data-placeholder="partitioner"]').append(this.selectTemplate({
				label: 'Industries',
				lowerLabel: 'Industries'.toLowerCase(),
				selectionType: 'industries',
				collection: this.industriesCollection
			}));
		}, this));

		this.$el.find('[data-placeholder="requirable"]').html(this.selectTemplate({
			label: this.formLabel,
			lowerLabel: this.formLabel.toLowerCase(),
			selectionType: 'requirables',
			collection: this.collection
		}));

		this.$el.find('[data-placeholder="expiry"]').after(this.formTemplate({}));
		wmMaskInput({ root: this.$el, selector: '#minimum-coverage' }, '000,000,000', { reverse: true });

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
		this.collection.industryId = $(e.currentTarget).val();

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
				wmNotify({
					message: 'Error loading.',
					type: 'danger'
				});
			}
		});
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
		if (this.partitionName) {
			name = this.partitionName + ' - ' + name;
		}

		var $minimumCoverage = this.$('#minimum-coverage');
		var minimumCoverage = $minimumCoverage.cleanVal() || 0;
		if (minimumCoverage) {
			name += ' ($' + $minimumCoverage.val() + ')';
		}

		return {
			$type: this.formType,
			requirable: {
				name: name,
				id: requirableId
			},
			minimumCoverage: minimumCoverage,
			mandatory: mandatory,
			notifyOnExpiry: false,
			removeMembershipOnExpiry: false
		};
	}
});
