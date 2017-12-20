'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';
import '../funcs/wmAssignmentPricing';

export default Backbone.View.extend({
	el: '#bulk-add-label',

	events: {
		'change [name=bulk_edit_label]'          : 'pickedLabel',
		'change input[name="reschedule_option"]' : 'toggleRangeFields',
		'click #add_mult_label'                  : 'save'
	},

	initialize: function (options) {
		this.$el.wmAssignmentScheduling({ 'modal': true });
	},

	toggleRangeFields: function () {
		this.$('.to-date').toggle(this.$('input[name="reschedule_option"]:checked').val() === 'window');
	},

	save: function () {
		$.post('/assignments/add_label_reschedule_multiple', {
				workNumbers: this.options.selectedWorkNumbers,
				label_id: $('#bulk_edit_label').val(),
				note: $('#label_note').val(),
				reschedule_option: $('input[name="reschedule_option"]:checked').val(),
				from: $('input[name="from"]').val(),
				fromtime: $('input[name="fromtime"]').val(),
				to: $('input[name="to"]').val(),
				totime: $('input[name="totime"]').val()
			}, _.bind(function (response) {
				if (response.successful) {
					wmNotify({
						message: response.messages[0]
					});
					this.options.modal.destroy();
					Backbone.Events.trigger('getDashboardData');
				} else {
					wmNotify({
						message: response.messages[0],
						type: 'danger'
					});
				}
			}, this), 'json'
		);

		return false;
	},

	pickedLabel: function () {
		$.unescapeHTML = function (html) {
			return $('<div/>').html(html).text();
		};

		$.unescapeAndParseJSON = function (json) {
			return $.parseJSON($.unescapeHTML(json));
		};

		var labels_json = $.unescapeAndParseJSON($('#json_labels').html());
		var key = this.$('[name=bulk_edit_label]').val();
		var label = labels_json[key] || {};
		this.$('label[for="bulk_edit_label"]').toggleClass('required', label.is_note_required);

		if (label.is_include_instructions && label.instructions) {
			this.$('#label_note_instructions').show().html(label.instructions);
		} else {
			this.$('#label_note_instructions').hide().empty();
		}
		if (label.is_schedule_required) {
			this.$('#add_note_container_schedule').show();
			this.$('#label_reschedule').show();
			this.$('#new_time').attr('checked', 'checked');
			this.$('.to-date').hide();
		} else {
			this.$('#add_note_container_schedule').hide();
			this.$('#label_reschedule').hide();
			this.$('#new_time').attr('checked', 'checked');
			this.$('.to-date').hide();
		}
	}

});
