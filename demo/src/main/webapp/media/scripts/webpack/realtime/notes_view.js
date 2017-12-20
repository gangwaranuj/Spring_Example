'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';

export default Backbone.View.extend({
	events: {
		'click .add_note_action' : 'toggleNote',
		'click form .cancel'     : 'toggleNote',
		'submit form'            : 'submitNote'
	},

	initialize: function () {
		this.render();
	},


	render: function () {
		var noteContainerVisible = this.$('.add_note_container').is(':visible');
		this.$('.form-actions .toggle-actions').toggle(!noteContainerVisible);
		this.$('.form-actions .submit-actions').toggle(noteContainerVisible);

		return this;
	},

	toggleNote: function (e) {
		var self = this;

		e.preventDefault();
		this.$('.add_note_container').slideToggle('fast', function () {
			self.render();
		});
	},

	submitNote: function (e) {
		e.preventDefault();

		this.$('form').ajaxSubmit({
			context: this,
			dataType: 'json',
			success: function (data) {
				if (data.successful) {
					window.location.reload();
				} else {
					_.each(data.errors, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}
		});
	}
});

