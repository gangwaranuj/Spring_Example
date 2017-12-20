'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import ApprovalQueueView from './approval_queue_view';
import wmNotify from '../../../funcs/wmNotify';
import ajaxSendInit from '../../../funcs/ajaxSendInit';
import getCSRFToken from '../../../funcs/getCSRFToken';

export default Backbone.View.extend({
	el: '.content',

	events: {
		'click #select_all'                  : 'selectAllSubscriptions',
		'click #subscriptions_queue_approve' : 'approveSubscriptions',
		'click #subscriptions_queue_reject'  : 'rejectSubscriptions'
	},

	initialize () {
		ajaxSendInit();
		this.approvalQueue = new ApprovalQueueView();
	},

	render () {
		return this;
	},

	selectAllSubscriptions (evt) {
		$('#approval_queue_table td :checkbox')
			.prop('checked', $(evt.currentTarget).is(':checked'));
	},

	getSelectedRows () {
		return $.map($('#approval_queue_table tbody :checked'), function (e) {
			return parseInt($(e).val(), 10);
		});
	},

	approveSubscriptions () {
		// Cleanup previous errors
		$('.alert-message.success, .alert-message.error').remove();

		var self = this;
		var data = $('#approval_queue_form').serialize();

		// Check that at least one row is selected
		if (this.getSelectedRows().length === 0) {
			return;
		}

		$.ajax({
			context: this,
			type: 'POST',
			url: '/admin/manage/subscriptions/approve',
			data: data,
			success: function (response) {
				if (response.successful) {
					wmNotify({
						message: response.messages[0]
					});

					// Remove affected rows
					$('#approval_queue_form tbody :checked').each(function (idx, elem) {
						self.approvalQueue.deleteRow($(elem).closest('tr'));
					});
				} else {
					this.redirect('/admin/manage/subscriptions/approval', response.messages, 'error');
				}
			},
			error: function () {
				console.log('error approving subscriptions');
			}
		});
	},

	redirect (url, msg, type) {
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


	rejectSubscriptions () {
		// Cleanup previous errors
		$('.alert-message.success, .alert-message.error').remove();

		var data = $('#approval_queue_form').serialize();

		// Check that at least one row is selected
		if (this.getSelectedRows().length === 0) {
			return;
		}

		$.ajax({
			context: this,
			type: 'POST',
			url: '/admin/manage/subscriptions/reject',
			data: data,
			success (response) {
				if (response.successful) {
					wmNotify({ message: response.messages[0] });

					// Remove affected rows
					$('#approval_queue_form tbody :checked').each((idx, elem) => {
						this.approvalQueue.deleteRow($(elem).closest('tr'));
					});
				} else {
					this.redirect('/admin/manage/subscriptions/approval', response.messages, 'error');
				}
			},
			error () {
				console.log('error rejecting subscriptions');
			}
		});
	}
});
