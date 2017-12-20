'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import NegotiateScheduleView from './negotiation_schedule_view';
import wmNotify from '../funcs/wmNotify';
import 'jquery-form/jquery.form';

export default Backbone.View.extend({
	events: {
		'change [name=label_id]' : 'render'
	},

	initialize: function () {
		$.unescapeHTML = function (html) {
			return $('<div/>').html(html).text();
		};

		$.unescapeAndParseJSON = function (json) {
			return $.parseJSON($.unescapeHTML(json));
		};

		new NegotiateScheduleView({
			el: this.el,
			millisOffset: this.options.millisOffset
		});

		this.$el.ajaxForm({
			dataType: 'json',
			context: this,
			success: function(data) {
				if (data.successful) {
					window.location.reload();
				} else {
					_.each(data.messages, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}
		});
	},

	render: function () {
		var labels_json = $.unescapeAndParseJSON($('#json_labels').html());
		var key = this.$('[name=label_id]').val();
		var label = labels_json[key] || {};

		this.$('label[for="label_note"]').toggleClass('required', label.is_note_required);

		if (label.is_include_instructions && label.instructions) {
			this.$('#label_note_instructions').show().html(label.instructions);
		} else {
			this.$('#label_note_instructions').hide().empty();
		}
		if (label.is_schedule_required) {
			this.$('#label_reschedule').show();
		} else {
			this.$('#label_reschedule').hide();
		}
	}
});
