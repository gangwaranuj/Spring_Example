import $ from 'jquery';
import Backbone from 'backbone';
import WebHookHeaderView from './integrations-webhook-header_view';
import WebHookModel from './webhook-model';
import getCSRFToken from '../funcs/getCSRFToken';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import ConfirmActionTemplate from '../funcs/templates/confirmAction.hbs';
import '../dependencies/jquery.serializeObject';
import '../dependencies/jquery.tmpl';

export default Backbone.View.extend({
	template: $('#web_hook_template'),
	alertTemplate: $('#web_hook_alert_template'),

	events: {
		'click .web-hook-add-header': 'createWebHookHeader',
		'click .web-hook-delete': 'removeWebHook',
		'click .web-hook-save': 'saveWebHook',
		'click .web-hook-enable': 'enableWebHook',
		'click .web-hook-disable': 'disableWebHook'
	},

	initialize (options) {
		this.webHookData = options;

		this.render();
	},

	render () {
		this.$el.html(this.template.tmpl(this.webHookData));

		// set method type
		if (this.webHookData.methodType) {
			this.$el.find(`.web-hook-method-type option[value="${this.webHookData.methodType}"]`).attr('selected', 'selected');
		}

		// set content type
		if (this.webHookData.contentType) {
			this.$el.find(`.web-hook-content-type option[value="${this.webHookData.contentType}"]`).attr('selected', 'selected');
		}

		// add headers
		if (this.webHookData.headers) {
			this.webHookData.headers.forEach((hook) => {
				this.addWebHookHeader(hook);
			});
		}

		// bind events
		this.delegateEvents(this.events);
	},

	setWebHookEventView (webHookEventView) {
		this.webHookEventView = webHookEventView;
	},

	createWebHookHeader () {
		this.addWebHookHeader({});
	},

	addWebHookHeader (webHookHeaderData) {
		const webHookHeaderList = this.$el.find('.web-hook-headers');
		const webHookHeaderView = new WebHookHeaderView(webHookHeaderData);

		webHookHeaderList.append(webHookHeaderView.el);
	},

	removeWebHook () {
		const id = this.$el.find('[name="id"]').val();

		this.confirmModal = wmModal({
			autorun: true,
			title: 'Confirm',
			destroyOnClose: true,
			content: ConfirmActionTemplate({
				message: 'Are you sure you want to delete this webhook?'
			})
		});

		$('.cta-confirm-yes').on('click', () => {
			if (id) {
				$.ajax({
					context: this,
					type: 'POST',
					url: `/mmw/integration/delete_web_hook/${id}`,
					success: () => {
						this.remove();
						// notify webhook event view so it can be hidden if there are no webhooks in it
						this.webHookEventView.onWebHookRemoved();
						this.confirmModal.hide();
					},
					error: () => {
						const message = 'Could not remove webhook, please try again.';
						wmNotify({ message, type: 'danger' });
						this.confirmModal.hide();
					}
				});
			} else {
				this.remove();

				// notify webhook event view so it can be hidden if there are no webhooks in it
				this.webHookEventView.onWebHookRemoved();
				this.confirmModal.hide();
			}
		});
	},

	isURL (string) {
		// eslint-disable-next-line max-len
		const urlRegex = /^(?:(?:https?|ftp):\/\/)(?:\S+(?::\S*)?@)?(?:(?!10(?:\.\d{1,3}){3})(?!127(?:\.\d{1,3}){3})(?!169\.254(?:\.\d{1,3}){2})(?!192\.168(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]+-?)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]+-?)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/[^\s]*)?$/i;
		return urlRegex.test(string);
	},

	saveWebHook () {
		const webHookForm = this.$el.find('form');
		const postData = webHookForm.serializeObject();

		if (!this.isURL(postData.url)) {
			wmNotify({
				message: 'The URL provided is not valid.',
				type: 'danger'
			});
			return;
		}

		if (webHookForm.find("input[name='suppressApiEvents']").length === 1) {
			postData.suppressApiEvents = $(webHookForm.find("input[name='suppressApiEvents']")[0]).prop('checked');
		}

		// build headers
		postData.headers = [];
		this.$el.find('.web-hook-headers').children().each(function eachFunc () {
			postData.headers.push({
				id: $(this).find('[rel="webHookHeader.id"]').val(),
				name: $(this).find('[rel="webHookHeader.name"]').val(),
				value: $(this).find('[rel="webHookHeader.value"]').val()
			});
		});

		const model = new WebHookModel();

		model.save(postData, {
			type: 'POST',
			beforeSend (jqXHR) {
				jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
			},
			success: (cbModel, { successful, data, messages }) => {
				if (successful) {
					this.webHookData = data.web_hook;
					this.render();
					messages.forEach(message => wmNotify({ message }));
				} else {
					messages.forEach(message => wmNotify({ message, type: 'danger' }));
				}
			},
			error () {
				wmNotify({
					message: 'Could not save webhook, please try again.',
					type: 'danger'
				});
			}
		});
	},

	enableWebHook () {
		$.ajax({
			context: this,
			type: 'POST',
			url: `/mmw/integration/enable_web_hook/${this.$el.find('[name="id"]').val()}`,
			beforeSend (jqXHR) {
				jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
			},
			success () {
				this.showDisable();
			},
			error () {
				wmNotify({
					message: 'Could not enable webhook, please try again.',
					type: 'danger'
				});
			}
		});
	},

	disableWebHook () {
		$.ajax({
			context: this,
			type: 'POST',
			url: `/mmw/integration/disable_web_hook/${this.$el.find('[name="id"]').val()}`,
			beforeSend (jqXHR) {
				jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
			},
			success () {
				this.showEnable();
			},
			error () {
				wmNotify({
					message: 'Could not disable webhook, please try again.',
					type: 'danger'
				});
			}
		});
	},

	showEnable () {
		this.$el.find('.web-hook-enable').show();
		this.$el.find('.web-hook-disable').hide();
	},

	showDisable () {
		this.$el.find('.web-hook-enable').hide();
		this.$el.find('.web-hook-disable').show();
	}
});
