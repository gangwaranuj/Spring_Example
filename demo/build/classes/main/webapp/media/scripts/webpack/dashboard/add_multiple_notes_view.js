'use strict';

import Backbone from 'backbone';
import $ from 'jquery';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';
import AddMultipleNoteModel from './add_multiple_notes_model';

export default Backbone.View.extend({
	el: '#add-note-form',
	events: {
		'click #submit_add_notes' : 'save'
	},

	initialize: function (options) {
		$('#assignment_ids').val(options.selectedWorkNumbers);
	},

	save: function (event) {
		event.preventDefault();

		Backbone.emulateJSON = true;
		var model = new AddMultipleNoteModel();
		model.save({
			assignment_ids: $('#assignment_ids').val(),
			content: $('#multiple_content').val(),
			is_private: $('input[name=is_private_multiple]:checked').val()

		},{
			success: _.bind(function (model, response) {
				var message = model.get('messages');
				if (response.successful) {
					wmNotify({ message: message});
					this.options.modal.destroy();
					Backbone.Events.trigger('getDashboardData');
				} else {
					wmNotify({
						message: message,
						type: 'danger'
					});
				}
			}, this),
			error: _.bind(function (model) {
				// something went horribly wrong. Reload page to force display
				// of error message to the template. And to reload JS.
				var message = model.get('messages');
				wmNotify({
					message: message,
					type: 'danger'
				});
				this.options.modal.destroy();
			}, this)
		});
	}
});

