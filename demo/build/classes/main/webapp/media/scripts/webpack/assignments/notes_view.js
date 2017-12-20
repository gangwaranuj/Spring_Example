'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';
import '../dependencies/jquery.tmpl';
// 'jquery-helpers'


export default Backbone.View.extend({
	el: '#notes',
	events: {
		'submit form'      : 'submitNote',
		'keydown textarea' : 'maybeSubmitNote'
	},
	template: $('#tmpl-note').template(),

	render: function() {
		return this;
	},

	maybeSubmitNote: function (e) {
		if (e.shiftKey && e.keyCode === 13) {
			this.$('form').submit();
			e.stopPropagation();
			return false;
		}
	},

	submitNote: function (e) {
		var self = this;

		e.preventDefault();

		this.$('form').ajaxSubmit({
			dataType: 'json',
			beforeSubmit: function() {
				self.$('.messages').hide();
			},
			success: function(response) {
				if (response.successful) {
					self.$('form').trigger('reset');
					self.$('.empty-notes-message').remove();
					self.$('table')
						.removeClass('dn')
						.prepend($.tmpl(self.template, response.data));
				} else {
					_.each(response.messages, function (theMessage) {
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
