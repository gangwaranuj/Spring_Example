'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import StrideUserModel from './stride_user_model';
import wmModal from '../funcs/wmModal';
import strideTemplate from './templates/stride.hbs';
import permissionModal from './templates/stride-permission-modal.hbs';

export default Backbone.View.extend({
	el: '#worker-services-hub-content',
	template: strideTemplate,
	events: {
		'click .stride-start': 'handleStrideClick',
		'click .stride-guide': 'handleStrideClick'
	},
	model: new StrideUserModel(),
	spinner: '.stride-loading',

	initialize(options) {
		this.render();
	},

	render() {
		this.$el.html(this.template());
		setTimeout(() => this.$('.worker-services').addClass('animate-in'));
		this.permissionModal();
		this.contextualButton();
		analytics.page({
			path: '/workerservices/stride'
		});
	},

	contextualButton() {
		this.model.checkActiveUser()
			.then(result => {
				if (result.error) {
					this.showStrideDownMessage();
				} else {
					if (result.signedUp) {
						this.$('.stride-start').text('Manage Your Plan');
					}
					this.$('.stride-start').addClass('button-animate-in');
				}
			});
	},

	permissionModal() {
		this.modal = wmModal({
			title: 'Permission to Share Information',
			content: permissionModal(),
			controls: [
				{
					text: 'Cancel',
					close: true
				},
				{
					text: 'Yes, proceed to Stride Health',
					classList: 'stride-accept',
					primary: true
				}
			]
		});
	},

	toggleModal() {
		this.modal.toggle();
		analytics.track('Worker Services', {
			location: 'permission_modal',
			category: 'stride',
			label: 'open'
		});
		$('.stride-accept').on('click', () => {
			analytics.track('Worker Services', {
				location: 'permission_modal',
				category: 'stride',
				label: 'accept'
			});
			this.passNewUserToStride();
		});
		$('[data-modal-close]').on('click', () => {
			analytics.track('Worker Services', {
				location: 'permission_modal',
				category: 'stride',
				label: 'cancel'
			});
			this.$(this.spinner).hide();
			this.$('.stride-start').removeAttr('disabled');
		});
	},

	handleStrideClick(event) {
		this.destination = 'onboarding';
		if (event.target.className === 'stride-guide') {
			this.destination = 'guide';
			analytics.track('Worker Services', {
				location: 'landing',
				category: 'stride',
				label: 'guide'
			});
		} else {
			analytics.track('Worker Services', {
				location: 'landing',
				category: 'stride',
				label: 'start'
			});
		}
		this.$(this.spinner).show();
		this.$('.stride-start').attr('disabled', 'disabled');
		this.model.checkActiveUser()
			.then(result => {
				if (result.error) {
					this.showStrideDownMessage();
				} else {
					if (result.userNotRegistered) {
						this.toggleModal();
						this.$(this.spinner).hide();
					} else {
						this.passExistingUserToStride();
					}
				}
			});
	},

	passNewUserToStride() {
		$('.stride-accept').html('Sending you to Stride Health...').attr('disabled', 'disabled');
		var createUser = this.model.provisionUser(),
			dismissPromo,
			getUserUrl;

		createUser.done(function (result) {
			if (result.error) {
				this.showStrideDownMessage();
			} else {
				dismissPromo = this.model.dismissPromo();
				dismissPromo.done(function () {
					if (this.destination === 'guide') {
						getUserUrl = this.model.getStrideGuideUrl();
					} else {
						getUserUrl = this.model.getStrideUserUrl();
					}
					getUserUrl.done(function (result) {
						if (result.error) {
							this.showStrideDownMessage();
						} else {
							window.location.href = result.url;
						}
					}.bind(this));
				}.bind(this));
			}
		}.bind(this));
	},

	passExistingUserToStride() {
		var getUserUrl;

		if (this.destination === 'guide') {
			getUserUrl = this.model.getStrideGuideUrl();
		} else {
			getUserUrl = this.model.getStrideUserUrl();
		}

		getUserUrl.done(result => {
			if (result.error) {
				this.showStrideDownMessage();
			} else {
				window.location.href = result.url;
			}
		});
	},

	showStrideDownMessage() {
		this.errorModal = wmModal({
			title: "Couldn't contact Stride Health",
			content: "<div class=\"stride-error\"><p>We were unable to contact our partners at Stride Health to forward your request. Please try again later.</p></div>",
			controls: [
				{
					text: 'Close',
					close: true
				}
			]
		});
		this.$(this.spinner).hide();
		this.errorModal.toggle();
	}
});
