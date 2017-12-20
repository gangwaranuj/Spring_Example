'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import 'jquery-form/jquery.form';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click #addfunds-next'      : 'handleNext',
		'change [name=addfundtype]' : 'onSelect',
		'click #cc-step1-next'      : 'handleNextCC',
		'click #btn-addcheck-back'  : 'handleBackAddCheck',
		'click #btn-addcc-back2'    : 'handleBack2AddCC',
		'click #btn-addcc-back1'    : 'handleBack1AddCC',
		'click #btn-addach-back'    : 'handleBackAddACH'
	},

	initialize: function (options) {
		options = options || {};
		this.render();
	},

	render: function () {
		this.setCreditCardFormHandler();
		this.setACHFormHandler();
		this.setAddFundsWireFormHandler();
		$('#cancel-btn').addClass('dn');
	},

	onSelect: function () {
		$('#addfunds-next').prop('disabled', false);
	},

	setCreditCardFormHandler: function () {
		var self = this;
		$('#addCreditCardForm').ajaxForm({
			dataType: 'json',
			success: function (data) {
				if (data.successful) {
					self.redirect('/payments', data.messages, "success");
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



	setACHFormHandler: function () {
		var self = this;
		$('#addACHForm').ajaxForm({
			dataType: 'json',
			success: function (data) {
				if (data.successful) {
					self.redirect('/payments', data.messages, "success");
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

	setAddFundsWireFormHandler: function () {
		var self = this;
		$('#addFundsWireForm').ajaxForm({
			dataType: 'json',
			success: function (data) {
				if (data.successful) {
					self.redirect('/payments', data.messages, "success");
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

	handleNext: function () {
		if ($('#add_funds_wizard').is(':visible')) {
			$("#add_funds_wizard").addClass("dn");

			if ($('#add_funds_wizard #creditcard').is(':checked')) {
				$("#addcc_view").removeClass("dn");
				$("#cc_step1").removeClass("dn");
			}

			if ($('#add_funds_wizard #bankaccount').is(':checked')) {
				$("#addach_view").removeClass("dn");
			}

			if ($('#add_funds_wizard #wiretransfer').is(':checked')) {
				$("#addwire_view").removeClass("dn");
			}

			if ($('#add_funds_wizard #mailacheck').is(':checked')) {
				$("#addcheck_view").removeClass("dn");
			}
		}
	},

	handleNextCC: function () {
		$("#cc_step1").addClass("dn");
		$("#cc_step2").removeClass("dn");
	},

	handleBackAddCheck: function () {
		$("#addcheck_view").addClass("dn");
		$("#addwire_view").addClass("dn");
		$("#add_funds_wizard").removeClass("dn");
	},

	handleBack2AddCC: function () {
		$("#cc_step2").addClass("dn");
		$("#cc_step1").removeClass("dn");
	},

	handleBack1AddCC: function () {
		$("#addcc_view").addClass("dn");
		$("#cc_step1").addClass("dn");
		$("#add_funds_wizard").removeClass("dn");
	},

	handleBackAddACH: function () {
		$("#addach_view").addClass("dn");
		$("#add_funds_wizard").removeClass("dn");
	}
})
