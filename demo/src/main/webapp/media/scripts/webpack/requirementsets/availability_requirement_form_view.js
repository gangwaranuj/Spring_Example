'use strict';

import $ from 'jquery';
import _ from 'underscore';
import AbstractView from './abstract_requirement_form_view';
import Template from './templates/availability-requirement-form.hbs';
import '../dependencies/jquery.calendrical';

export default AbstractView.extend({
	formTemplate: Template,

	events: function () {
		// inherits parent events
		return _.extend({}, AbstractView.prototype.events, {
			'change select[data-selections="weekdays"]': 'resetForm'
		});
	},

	render: function () {
		this.$el.html(this.template({}));

		this.$el.find('[data-placeholder="partitioner"]').html(this.selectTemplate({
			label: 'Weekdays',
			lowerLabel: 'weekdays',
			selectionType: 'weekdays',
			collection: this.collection
		}));

		this.$el.find('[data-placeholder="requirable"]').html(this.formTemplate({}));

		this.$el.find('#from-time, #to-time').calendricalTime();

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

	resetForm: function (e) {
		$(e.currentTarget).find('option.prompt').remove();
		this.$('[data-action="add"]').prop('disabled', false);

		this.$el.find('[data-placeholder="requirable"]').html(this.formTemplate({}));

		this.$el.find('#from-time')
			.calendricalTime({
				startDate:new Date(),
				endDate:new Date(),
				defaultTime:'09:00',
				usa : true
			});

		this.$el.find('#to-time')
			.calendricalTime({
				startDate:new Date(),
				endDate:new Date(),
				defaultTime:'09:00',
				usa : true
			});
	},

	getRequirement: function () {
		var dayOfWeek = parseInt(this.$('[data-selections="weekdays"]').val(), 10);
		var requirable = this.collection && this.collection.findWhere({id: dayOfWeek});
		var fromTime = this.$('#from-time').val();
		var toTime = this.$('#to-time').val();
		var mandatory = this.$('[data-placeholder="mandatory"] input').prop("checked");

		var name = requirable.get('name') + " - " + fromTime + " to " + toTime;

		return {
			$type: this.formType,
			requirable: {
				name:  name,
				id: dayOfWeek
			},
			fromTime: fromTime,
			toTime: toTime,
			mandatory: mandatory
		};
	}
});
