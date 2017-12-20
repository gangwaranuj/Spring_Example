import $ from 'jquery';
import Backbone from 'backbone';
import WebhookEventView from './integrations-webhook-event_view';
import WebHookView from './integrations-webhook_view';

export default Backbone.View.extend({
	el: '#web_hooks_container',
	webHookEventList: $('#web_hook_events'),
	webHookEventViews: [],

	events: {
		'click #web_hook_add': 'createWebHook'
	},

	initialize (options) {
		if (options.webHookClientId) {
			this.loadWebHooks(options.webHookClientId);
		}
	},

	loadWebHooks (webHookClientId) {
		$.ajax({
			context: this,
			type: 'GET',
			url: `/mmw/integration/load_web_hooks/${webHookClientId}`,
			success: (data) => {
				if (data instanceof Array) {
					data.forEach((hook) => {
						if (hook.integrationEventTypeCode) {
							this.addWebHook(hook.integrationEventTypeCode, hook);
						}
					});
				}
			}
		});
	},

	createWebHook () {
		const integrationEventTypeCode = $('#web_hook_event_type').val();
		let callOrder = 0;

		// find callOrder
		if (this.webHookEventViews[integrationEventTypeCode]) {
			callOrder = this.webHookEventViews[integrationEventTypeCode]
				.getWebHookList()
				.children()
				.length;
		}

		this.addWebHook(integrationEventTypeCode, {
			integrationEventTypeCode,
			callOrder,
			id: null
		});

		// scroll to it
		$('html,body').animate({ scrollTop: $(`[rel="${integrationEventTypeCode}"]`).offset().top }, 'slow');
	},

	addWebHook (integrationEventTypeCode, webHookData) {
		// create container if it doesn't exist
		if (!this.webHookEventViews[integrationEventTypeCode]) {
			this.webHookEventViews[integrationEventTypeCode] = new WebhookEventView({
				integrationEventTypeCode,
				eventTypeName: $(`#web_hook_event_type option[value="${integrationEventTypeCode}"]`).text()
			});

			this.webHookEventList.append(this.webHookEventViews[integrationEventTypeCode].el);
		}

		// show it if it was hidden
		const webHookEventView = this.webHookEventViews[integrationEventTypeCode];
		$(webHookEventView.el).show();

		// create webhook
		const webHookView = new WebHookView(webHookData);

		webHookView.setWebHookEventView(webHookEventView);

		webHookEventView.addWebHook(webHookView);
	}
});
