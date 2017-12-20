'use strict';

import Template from './templates/travel-distance-requirement-form.hbs';
import $ from 'jquery';
import _ from 'underscore';
import AbstractView from './abstract_requirement_form_view';

export default AbstractView.extend({
	formTemplate: Template,
	events: function events() {
		return _.extend({}, AbstractView.prototype.events, {
			'change #distance' : 'distanceChanged',
			'change #addressTyper' : 'addressChanged'
		});
	},
	render: function() {
		this.$el.html(this.template({}));

		this.$el.find('[data-placeholder="requirable"]').html(this.formTemplate({}));

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

	addressChanged: function (e) {
		e.preventDefault();
		this.$('[data-action="add"]').prop('disabled', e.target.value === '');
	},

	distanceChanged: function (e) {
		e.preventDefault();
		this.$('[data-action="add"]').prop('disabled', ($('#longitude').val() === '' || $('#distance').val() === '') );
	},

	getRequirement: function() {
		var distance = this.$('#distance').val();
		var address  = this.$('#addressTyper').val();
		var longitude  = this.$('#longitude').val();
		var latitude  = this.$('#latitude').val();
		var mandatory = this.$('[data-placeholder="mandatory"] input').prop("checked");

		return {
			$type: this.formType,
			name: 'Within ' + distance + " miles of " + address,
			distance: distance,
			address: address,
			longitude: longitude,
			latitude: latitude,
			mandatory: mandatory
		};
	}
});
