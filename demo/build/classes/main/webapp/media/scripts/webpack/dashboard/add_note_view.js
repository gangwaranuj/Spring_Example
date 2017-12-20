'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import AddNoteModel from './add_note_model';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';

export default Backbone.View.extend({
	el: '#single-note-add',
	tagName: 'div',
	events: {
		'click #submit_add_note' : 'save',
		'click #add-note-close'  : 'close'
	},

	initialize: function (options) {},

	save: function (event) {
		event.preventDefault();

		var model = new AddNoteModel();
		Backbone.emulateJSON = true;
		model.save({
			assignment_id: this.options.assignmentId,
			content: this.$el.find('#note_content').val(),
			is_private: this.$el.find('input[name=is_private]:checked').val()
		},{
			success: _.bind(function (model, response) {
				var message = model.get('messages');
				if (response.successful) {
					wmNotify({
						message: message
					});
					this.options.modal.destroy();
					Backbone.Events.trigger('getDashboardData');

				}
			}, this),
			error: _.bind(function (model, error){
				var message = model.get('messages');
				this.redirect(window.location.pathname, message, 'error');
			}, this)
		});
	},

	redirect: function (url, msg, type) {
		if (msg) {
			var e = $("<form class='dn'></form>");
			e.attr({
				'action': '/message/create',
				'method': 'POST'
			});
			if (typeof msg === 'string') { msg = [msg]; }
			for (var i=0; i < msg.length; i++) {
				e.append(
					$("<input>").attr({
						'name': 'message[]',
						'value': msg[i]
					}));
			}
			e.append(
				$("<input>").attr({
					'name': 'type',
					'value': type
				}));
			e.append(
				$("<input>").attr({
					'name': 'url',
					'value': url
				}));
			e.append(
				$("<input>").attr({
					'name':'_tk',
					'value':getCSRFToken()
				}));
			$('body').append(e);
			e.submit();
		} else {
			window.location = url;
		}
	},

	close: function (event) {
		event.preventDefault();
		$('.wm-modal--close').trigger('click');
	}
});
