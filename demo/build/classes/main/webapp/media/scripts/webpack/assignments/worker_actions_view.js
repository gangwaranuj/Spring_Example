'use strict';

import Application from '../core';
import Template from '../assignments/templates/details/actionModalContent.hbs';
import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import wmNotify from '../funcs/wmNotify';
import wmTabs from '../funcs/wmTabs';
import 'jquery-form/jquery.form.js';
import '../funcs/wmAssignmentPricing';
import wmModal from '../funcs/wmModal';
import '../dependencies/jquery.calendrical';

export default Backbone.View.extend({
	template: Template,

	events: {
		'click .wm-tab'             : 'toggleActionTab',
		'click #date_negotiation'   : 'toggleDateNegotiation',
		'click #offer_expiration'   : 'toggleOfferExpiration'
	},

	initialize: function () {
		this.listenTo(Application.Events, 'realtime:assignments:showActions', this.render);
	},

	render: function (actionData, event) {
		event.preventDefault();

		$(this.el).html(this.template(actionData));
		this.delegateEvents();

		var title = 'Action on behalf of: <a href="/profile/' + actionData.worker.user_number + '" target="_blank">' + actionData.worker.name + '<\/a> ';
		if (actionData.worker.lane === 0) {
			title+= '<span class="label tooltipped tooltipped-e" aria-label="Employee">E<\/span>';
		} else if (actionData.worker.lane === 2) {
			title+= '<span class="label notice tooltipped tooltipped-e" aria-label="Contractor">C<\/span>';
		} else if (actionData.worker.lane === 3) {
			title+= '<span class="label warning tooltipped tooltipped-e" aria-label="Third Party">3<\/span>';
		} else if (actionData.worker.lane === 1) {
			title+= '<span class="label tooltipped tooltipped-e" aria-label="Employee">E<\/span>';
		}

		event.preventDefault();

		$.ajax({
			type: 'GET',
			url: event.currentTarget.href,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Action on behalf of:',
						destroyOnClose: true,
						content: this.el.innerHTML
					});
					$('.wm-modal--title').html(title);
					const modal = $('.wm-modal .-active');

					modal.find('#form_price').wmAssignmentPricing({ 'modal': true });
					modal.find('#form_price').wmAssignmentScheduling({ 'modal': true });
					modal.find('#expires_on').datepicker({ dateFormat: 'mm/dd/yy' });
					modal.find('#expires_on_time').calendricalTime({ startDate: this.$('#expires_on'), defaultTime: null });

					modal.find('.accept_work_form').ajaxForm({
						context: this,
						success: function (data) {
							if (data.successful) {
								if (this.options.successCallback) {
									this.options.successCallback();
								}
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

					modal.find('.decline_work_form').ajaxForm({
						context: this,
						success: function (data) {
							if (data.success) {
								if (this.options.successCallback) {
									this.options.successCallback();
								}
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

					modal.find('.counteroffer_work_form').ajaxForm({
						context: this,
						success: function (data) {
							if (data.success) {
								if (this.options.successCallback) {
									this.options.successCallback();
								}
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

					modal.find('.ask_question_form').ajaxForm({
						context: this,
						success: function (data) {
							var messageType = data.successful ? 'success' : 'danger';
							if (data.successful) {
								$('.wm-modal--close').trigger('click');
							}
							_.each(data.messages, function (theMessage) {
								wmNotify({
									message: theMessage,
									type: messageType
								});
							});
						}
					});

					modal.find('.add_note_form').ajaxForm({
						context: this,
						success: function (data) {
							var messageType = data.successful ? 'success' : 'danger';
							if (data.successful) {
								$('.wm-modal--close').trigger('click');
							}
							_.each(data.messages, function (theMessage) {
								wmNotify({
									message: theMessage,
									type: messageType
								});
							});
						}
					});
				}
			}

		});

		wmTabs();

		return this;
	},

	toggleActionTab: function (event) {
		$(event.target).blur();
	},

	toggleDateNegotiation: function () {
		if (this.$('#date_negotiation_config').is(':visible')) {
			this.$('#date_negotiation_config').hide();
		} else {
			this.$('#date_negotiation_config').show();
		}
	},

	toggleOfferExpiration: function () {
		if (this.$('#offer_expiration_config').is(':visible')) {
			this.$('#offer_expiration_config').hide();
		} else {
			this.$('#offer_expiration_config').show();
		}
	}
});
