'use strict';

import Application from '../core';
import ConfirmActionTemplate from '../funcs/templates/confirmAction.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import NotesView from './notes_view';
import PricingPage from '../assignments/pricing_page';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import '../dependencies/jquery.tmpl';
import 'jquery-form/jquery.form';
import '../funcs/wmAssignmentPricing';
import '../dependencies/jquery.bootstrap-tab';

export default Backbone.View.extend({
	tagName: 'tr',
	template: $('#tmpl-realtime_row').template(),

	events: {
		'click a.expand-assignment'  : 'toggleAssignment',
		'click a.resource-link'      : 'showActions',
		'click a.profile_link'       : 'showProfile',
		'click a.void_action'        : 'showVoidModal',
		'click a.reschedule_action'  : 'showRescheduleModal',
		'click a.work_dialer_action' : 'showWorkDialerModal',
		'click a.work_notify_action' : 'showWorkNotifyModal',
		'click a.reprice_action'     : 'showReprice',
		'click a.resend_action'      : 'showResend',
		'click a.notes_action'       : 'showNotesHistoryModal',
		'click a.history_action'     : 'showNotesHistoryModal',
		'click a.toggle_workingonit' : 'toggleWorkingOnIt'
	},

	initialize: function () {
		this.fadeInterval = 6000;
		this.expanded = false;
	},


	render: function () {
		var cells = $.tmpl(this.template, _.extend(this.model, {
			resource_iterator: function (index) {
				return (index % 3 === 0);
			},
			index: this.options.index
		}));

		this.$el.css('height', '68px').html(cells);

		// Add alternating row colors.
		if (this.options.index % 2) {
			this.$el.addClass('odd');
		} else {
			this.$el.addClass('even');
		}

		// Highlight "working on it".
		if (this.model.user_working_on_it) {
			this.$el.addClass('highlight_workingonit');
		}

		// Highlight "bad" assignments.
		if (this.model.percent_with_offers === 1 ||
			this.model.percent_resources_declined === 1 ||
			this.model.percent_time_to_work_elapsed >= 0.75 ||
			this.model.expires_in_3_hours) {
			this.$el.addClass('highlight_bad');
		}

		return this;
	},

	toggleAssignment: function () {
		if (this.expanded) {
			this.collapseAssignment();
		} else {
			this.expandAssignment();
		}
	},

	expandAssignment: function () {
		this.$('a.expand-assignment').html('[-]');
		this.$('.collapsed-content').hide();
		this.$('.expanded-content').show();
		this.expanded = true;
	},

	collapseAssignment: function () {
		this.$('a.expand-assignment').html('[+]');
		this.$('.collapsed-content').show();
		this.$('.expanded-content').hide();
		this.expanded = false;
	},

	highlight: function () {
		this.$('td').effect('highlight', {}, this.fadeInterval);
	},

	highlightQuestions: function () {
		this.$('span.questions').effect('highlight', {}, this.fadeInterval);
	},

	highlightOffers: function () {
		this.$('span.offers').effect('highlight', {}, this.fadeInterval);
	},

	highlightDeclines: function () {
		this.$('span.declines').effect('highlight', {}, this.fadeInterval);
	},

	highlightResource: function (id) {
		this.$('div.resource_' + id).effect('highlight', {}, this.fadeInterval);
	},

	showVoidModal: function (e) {
		e.preventDefault();

		var self = this;
		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					let modal = wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});
					$('#form_cancel_work').ajaxForm({
						dataType: 'json',
						success: function (data) {
							if (data.successful) {
								self.options.parent.reload();
								modal.destroy();
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
			}

		});
	},

	showRescheduleModal: function (e) {
		e.preventDefault();

		var self = this;
		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					let modal = wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});
					var $form = $('#form_reschedule_assignment');
					$form.wmAssignmentScheduling({
						'modal': true
					});
					$form.ajaxForm({
						dataType: 'json',
						success: function (data) {
							if (data.successful) {
								self.options.parent.reload();
								wmNotify({ message: 'Successfully created reschedule request.' });
								modal.destroy();
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
			}
		});
	},

	showWorkDialerModal: function (e) {
		e.preventDefault();

		var self = this;
		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					let modal = wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});
					$('#form_work_dialer').ajaxForm({
						dataType: 'json',
						success: function (data) {
							if (data.successful) {
								self.options.parent.reload();
								modal.destroy();
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
			}
		});
	},

	showWorkNotifyModal: function (e) {
		e.preventDefault();

		var self = this;
		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					let modal = wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});
					$('#form_work_notify').ajaxForm({
						dataType: 'json',
						success: function (data) {
							if (data.successful) {
								self.options.parent.reload();
								wmNotify({ message: 'Successfully notified resources.' });
								modal.destroy();
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
			}
		});
	},

	showReprice: function (e) {
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					let modal = wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});

					var $form = $('#form_price');
					$form.wmAssignmentPricing({ 'modal': true });

					new PricingPage({
						wmFee: parseFloat($('#work-fee').val()),
						pricingType: $('#pricing-type').val()
					});

					var formUrl = $form.attr('action');
					$form.attr('action', formUrl + '.json');
					$form.ajaxForm({
						context: this,
						dataType: 'json',
						success: function (response) {
							if (response.successful) {
								wmNotify({ message: 'The assignment pricing has been updated. All users will be resent assignment information.' });
								modal.destroy();
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
			}
		});
	},

	showResend: function (e) {
		var self = this;
		var link = e.currentTarget;

		this.confirmModal = wmModal({
			autorun: true,
			title: 'Confirm',
			destroyOnClose: true,
			content: ConfirmActionTemplate({
				message: 'Are you sure you want to resend this assignment to all resources?'
			})
		});

		$('.cta-confirm-yes').on('click', _.bind(function () {
			$.getJSON(link.href, _.bind(function (data) {
				if (data.successful === true) {
					self.options.parent.reload();
					wmNotify({ message: 'The assignment has been resent to all resources.' });
					this.confirmModal.hide();
				} else {
					_.each(data.errors, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}, this));
		}, this));

		e.preventDefault();
		return false;
	},

	showNotesHistoryModal: function (e) {
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});

					new NotesView({
						el: '#notes'
					});
				}
			}
		});
	},

	showActions: function (e) {
		e.preventDefault();
		var userNumber = $(e.target).data('usernumber').substring(1);
		var index = this.model.resources_index[userNumber];
		var worker = this.model.resources[index];

		Application.Events.trigger('realtime:assignments:showActions', {
			assignment: this.model,
			worker
		}, e);
	},

	showProfile: function (e) {
		e.preventDefault();
		var $profileBody = $('.profile-body');
		var $profilePopup = $('#user-profile-popup');

		$profileBody.empty();
		$profilePopup.modal('show');
		$profilePopup.find('.profile-spinner').show();
		$.get($(e.currentTarget).attr('href') + '&popup=1', function (result) {
			$profilePopup.find('.profile-spinner').hide();
			$profileBody.html(result);
		});
	},

	toggleWorkingOnIt: function (e) {
		e.preventDefault();

		var self = this;
		$.ajax({
			url: $(e.target).attr('href'),
			type: 'POST',
			dataType: 'json',
			success: function (data) {
				if (data.successful) {
					var html = $('#tmpl-working_on_it').tmpl(data);
					$(e.target).closest('td').html(html);

					if (data.user_working_on_it != null) {
						$(self.el).addClass('highlight_workingonit');
					} else {
						$(self.el).removeClass('highlight_workingonit');
					}
				} else {
					// An error occurred so refresh the list to get the most recent data.
					self.options.parent.reload();
				}
			}
		});
	}
});
