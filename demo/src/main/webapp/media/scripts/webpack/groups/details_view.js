/* eslint-disable object-shorthand, func-names */
/* global confirm */

import $ from 'jquery';
import Backbone from 'backbone';
import MessagesView from './messages_view';
import 'jquery-form/jquery.form';
import Application from '../core';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import wmTabs from '../funcs/wmTabs';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click .show-apply-confirmation': 'showApplyConfirmation',
		'click #submit_application_link': '_submitApplication',
		'click #block-client': 'blockClientAction',
		'click #unblock-client': 'unblockClientAction',
		'click .toggle-manage': 'toggleManage',
		'click .report-concern': 'reportConcern',
		'click #agreement_modal_anchor': 'displayAgreementModal',
		'click .trigger-apply': 'triggerApply',
		'click .esig-hellosign-init': 'initEsigHelloSign'
	},

	initialize: function () {
		this.reportConcernModal = wmModal({
			title: 'Report a Concern',
			content: `<p>Thank you for choosing to report a concern, we love to hear from you. Please briefly describe your concern below &mdash; we&rsquo;ll review and take action immediately.</p>
				<textarea name="content" id="concernContent" class="span8" rows="5"></textarea>`,
			controls: [
				{
					text: 'Submit',
					close: true,
					primary: true
				}
			],
			customHandlers: [
				{
					event: 'click',
					selector: '.wm-modal--control.-primary',
					callback: () => {
						const { id } = this.model;
						const type = 'group';
						const content = document.getElementById('concernContent').value;
						$.ajax({
							url: '/quickforms/concern',
							type: 'POST',
							data: { id, type, content },
							dataType: 'json',
							headers: {
								'X-CSRF-Token': Application.CSRFToken
							},
							success: ({ messages }) => {
								const [message] = messages;
								wmNotify({ message });
							}
						});
					}
				}
			]
		});

		if (this.model.requires_agreements) {
			this.totalAgreed = 0;
		}

		this.messagesView = new MessagesView({ model: this.model });
		// Add client id to block-client form
		if (!this.options.isGroupCompanyViewer) {
			const $blockClientForm = $('#block_client_form');

			// Get form action url
			const action = $blockClientForm.attr('action');

			// Replace id in form with passed id
			$blockClientForm
				.attr('action', action.replace(/\/block_client.*$/, '/block_client/' + this.options.clientIdToBlock));
		}

		this.render();

		const membersParam = document.URL.match(/members=(.*)/);
		if (membersParam && membersParam[1]) {
			this.toggleManage();
		}
	},

	render: function () {
		this.messagesView.render();
		if (this.options.companyBlocked) {
			this.showDisabledButtonTooltip();
		}

		// If there's no fulfilled/missing requirements, add text.
		const fulfilledRequirements = $('#fulfilled-requirements').find('.requirements.unstyled');
		if (fulfilledRequirements.children().length < 1) {
			fulfilledRequirements.html('<li class="default">You have no fulfilled requirements</li>');
		}

		const missingRequirements = $('#missing-requirements').find('.requirements.unstyled');
		if (missingRequirements.children().length < 1) {
			missingRequirements.html('<li class="default">You have no missing requirements</li>');
		}

		wmTabs();

		return this;
	},

	initEsigHelloSign: function (event) {
		event.preventDefault();

		if (window.HelloSign) {
			if ($(event.target).attr('disabled') === 'disabled') {
				return;
			}

			$(event.target).attr('disabled', 'disabled');

			$.ajax({
				url: '/v2/esignature/get_signable',
				type: 'GET',
				data: {
					companyUuid: event.target.dataset.companyuuid,
					templateUuid: event.target.dataset.templateuuid
				},
				dataType: 'json',
				headers: {
					'X-CSRF-Token': Application.CSRFToken
				}
			})
			.done((response) => {
				const {
					clientId,
					signingUrl
				} = response.results[0];

				window.HelloSign.init(clientId);
				window.HelloSign.open({
					url: signingUrl,
					allowCancel: true,
					messageListener: function (eventData) {
						if (eventData.event === window.HelloSign.EVENT_SIGNED) {
							window.location.reload();
						}
					}
				});
			})
			.always(() => $(event.target).removeAttr('disabled'));
		}
	},

	reportConcern: function () {
		this.reportConcernModal.show();
	},

	showApplyConfirmation: function (e) {
		const confirmed = confirm('Are you sure you want to apply to this talent pool before meeting all the requirements? People who meet all the requirements are more likely to be accepted as members.');
		if (confirmed) {
			return true;
		}
		e.stopImmediatePropagation();
		return false;
	},

	_submitApplication: function () {
		if (!$('#submit_application_link').attr('disabled')) {
			this.submitApplication();
		}
	},

	submitApplication: function () {
		$('#group_apply_form').trigger('submit');
	},

	triggerApply: function (event) {
		// redirect to this page after applying to the talent pool.
		$('#redirect_to').val(event.target.dataset.url);
		this.submitApplication();
	},

	displayAgreementModal: function (event) {
		event.preventDefault();
		const dataUrl = event.target.dataset.url;
		const id = parseInt(dataUrl.substring(dataUrl.lastIndexOf('/') + 1), 10);
		wmModal({
			autorun: true,
			title: 'Accept or Decline Agreement',
			destroyOnClose: true,
			content: $('#confirm_agreements').html(),
			controls: [
				{
					text: 'Decline',
					classList: '-decline-agreement',
					close: true
				},
				{
					text: 'Accept',
					primary: true
				}
			],
			customHandlers: [
				{
					event: 'click',
					selector: '.wm-modal--control.-primary',
					callback: () => {
						$.ajax({
							url: '/agreements/accept/contract',
							type: 'POST',
							data: { contractId: id },
							dataType: 'json',
							headers: {
								'X-CSRF-Token': Application.CSRFToken
							}
						})
						.always(() => window.location.reload());
					}
				},
				{
					event: 'click',
					selector: '.wm-modal--control.-decline-agreement',
					callback: () => {
						this.totalAgreed = 0;
					}
				}
			]
		});
	},

	blockClientAction: function () {
		wmModal({
			title: 'Block Company',
			content: 'Are you sure you would like to block this company?',
			autorun: true,
			controls: [
				{
					text: 'Close',
					close: true
				},
				{
					text: 'Block',
					primary: true,
					close: true
				}
			],
			customHandlers: [
				{
					event: 'click',
					selector: '.wm-modal--control.-primary',
					callback: () => $.post(`/user/block_client/${this.options.clientIdToBlock}`, ({ messages, data }) => {
						const [message] = messages;
						if (data && data.status === 'OK') {
							wmNotify({ message });
							this.showDisabledButtonTooltip();
						} else {
							wmNotify({ message, type: 'danger' });
						}
					})
				}
			]
		});
	},

	// Unblock client
	unblockClientAction: function () {
		$.post(`/user/unblock_client/${this.options.clientIdToBlock}`, (response) => {
			const [message] = response.messages;
			if (response.data && response.data.status === 'OK') {
				wmNotify({ message });
				this.hideDisabledButtonTooltip();
			} else {
				wmNotify({ message, type: 'danger' });
			}
		});
	},

	// Display a tooltip on test-action button (used when company is blocked)
	showDisabledButtonTooltip: function () {
		document.getElementById('block-client').classList.add('dn');
		document.getElementById('unblock-client').classList.remove('dn');

		$('#submit_application_link')
			.attr('disabled', 'true')
			.removeAttr('href')
			.wrap(`<span id="blocked-client-tooltip" class="tooltipped tooltipped-n" aria-label="${this.options.blockedClientTooltip}">`);
	},

	// Hide a tooltip on test-action button (used when company is blocked)
	hideDisabledButtonTooltip: function () {
		document.getElementById('block-client').classList.remove('dn');
		document.getElementById('unblock-client').classList.add('dn');

		$('#submit_application_link')
			.removeAttr('disabled')
			.unwrap();
	},

	toggleManage: function () {
		this.$('.groups-toggle').toggle();
	}
});
