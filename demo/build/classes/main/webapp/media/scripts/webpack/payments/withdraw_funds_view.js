'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import 'jquery-form/jquery.form';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click #withdrawBtn' : 'setWithdrawFundsForm'
	},

	initialize: function (options) {
		options = options || {};
		this.render();
	},

	render: function () {
		this.setWithdrawFundsForm();
		$('#closeBtn').addClass('dn');
	},

	setWithdrawFundsForm: function () {
		$('#withdrawFundsForm').ajaxForm({
			dataType: 'json',
			success: (response) => {
				if (response.successful) {
					if (response.data.rate_work) {
						this.redirect('/ratings/?withdrawal=true', response.messages, 'success');
					} else {
						this.redirect('/payments', response.messages, 'success');
					}
				} else {
					response.messages.forEach((theMessage) => {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}
		});
	},

	redirect: function (url, msg, type) {
		if (msg) {
			var e = $('<form class="dn"></form>');
			e.attr({
				'action': '/message/create',
				'method': 'POST'
			});
			if (typeof msg === 'string') { msg = [msg]; }
			for (var i=0; i < msg.length; i++) {
				e.append(
					$('<input>').attr({
						'name': 'message[]',
						'value': msg[i]
					}));
			}
			e.append(
				$('<input>').attr({
					'name': 'type',
					'value': type
				}));
			e.append(
				$('<input>').attr({
					'name': 'url',
					'value': url
				}));
			e.append(
				$('<input>').attr({
					'name':'_tk',
					'value':getCSRFToken()
				}));
			$('body').append(e);
			e.submit();
		} else {
			window.location = url;
		}
	}
});
