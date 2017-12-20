/*global __insp*/
'use strict';

import DeliverableAssetTemplate from './templates/details/deliverable_asset.hbs';
import RejectModalTemplate from './templates/details/shortcut_reject_deliverable_modal_body.hbs';
import RejectModalErrorTemplate from './templates/details/shortcut_reject_deliverable_modal.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import getCSRFToken from '../funcs/getCSRFToken';
import wmModal from '../funcs/wmModal';
import wmNotify from '../funcs/wmNotify';

/*
 *   Lowest-level view in the deliverables hierarchy.  This is an actual asset (file)
 *   associated with a deliverable requirement.
 */
export default Backbone.View.extend({
	tagName: 'li',
	className: 'deliverable',
	events: {
		'mouseenter'              : 'showOptions',
		'mouseleave'              : 'hideOptions',
		'click .remove'           : 'removeAsset',
		'click .rejection-button' : 'rejectAsset',
		'click'                   : 'openCarousel',
		'click .options'          : 'stopPropagation' // Adding this so the carousel doesn't accidentally open when clicking inside the options (download or delete) container
	},

	initialize: function (options) {
		this.$el = $(this.el);
		this.auth = options.auth;
		// Templates
		this.template = DeliverableAssetTemplate;
		this.rejectModalTemplate = RejectModalTemplate;
		this.rejectModalErrorTemplate = RejectModalErrorTemplate;

		// State
		this.rejectReasonMaxChar = this.options.rejectReasonMaxChar;
		this.isCarouselThumbnail = this.$el.hasClass('thumbnail-carousel-');

		this.$el.attr('data-position', this.model.get('position'));
		this.$el.attr('data-deliverable-requirement-id', this.model.get('deliverableRequirementId'));

		this.model.on('change', this.render, this);
		this.model.on('destroy', function () {
			this.unbind().remove();
		}, this);
	},

	render: function () {
		var filename = this.model.get('name') || this.model.get('file_name');
		filename = _.isUndefined(filename) ? '' : filename;
		var extension = filename.split('.').pop();
		if (extension === filename) {
			// If the file does not have an extension, use "MISC" as the extension
			extension = 'MISC';
		}
		this.model = _.extend(this.model, { extension: extension });

		this.$el.html(this.template({
			asset: this.model,
			activeOrComplete: this.auth.status === 'active' || this.auth.status === 'complete',
			isPermission: this.auth.isAdmin || this.auth.isWorker,
			isAdmin: this.auth.isAdmin,
			isImage: _.contains(['image/jpeg', 'image/pjpeg', 'image/jpg', 'image/tiff', 'image/png'], this.model.get('mimeType'))
		}));

		var $shortcutRejectButton = this.$('.rejection-button');
		var shortcutRejectButtonExists = $shortcutRejectButton.length;

		if (this.model.get('rejectedAsset')) {
			this.$el.removeClass('rejectedDeliverable');
			this.$el.addClass('updatedDeliverable');
			this.$('.rejectedState').hide();
			this.$('.updatedState').show();
			if (shortcutRejectButtonExists) {
				this.$('.remove').hide();
				$shortcutRejectButton.show();
			}
		} else if (this.model.get('rejectedOn')) {
			this.$el.removeClass('updatedDeliverable');
			this.$el.addClass('rejectedDeliverable');
			this.$('.updatedState').hide();
			this.$('.rejectedState').show();
			if (shortcutRejectButtonExists) {
				this.$('.remove').show();
				$shortcutRejectButton.hide();
			}
		} else if (shortcutRejectButtonExists) {
			this.$('.remove').hide();
			$shortcutRejectButton.show();
		}

		return this;
	},

	showOptions: function () {
		if (!this.isCarouselThumbnail) {
			this.$('.deliverable-asset-thumbnail').addClass('darken');
		}
	},

	hideOptions: function () {
		if (!this.isCarouselThumbnail) {
			this.$('.deliverable-asset-thumbnail').removeClass('darken');
		}
	},

	removeAsset: function () {
		var modal = wmModal({
			autorun: true,
			title: 'Remove File',
			content: 'Are you sure you want to delete this file?',
			destroyOnClose: true,
			controls: [
				{
					text: 'No',
					close: true
				},
				{
					text: 'Yes',
					primary: true
				}
			]
		});

		$('.wm-modal .-active').find('.-primary').on('click', () => {
			if (typeof __insp !== 'undefined') {
				__insp.push(['tagSession', 'Deleted Deliverable']);
			}
			$.ajax({
				url: '/assignments/remove_attachment',
				data: {
					work_id: this.model.get('workNumber'),
					asset_id: this.model.get('id')
				},
				type: 'POST',
				dataType: 'json',
				context: this
			}).done(function (response) {
				if (response.successful) {
					modal.destroy();
					this.model.popAssetFromHistory();
				}
			});
		});
	},

	rejectAsset: function () {
		var modal = wmModal({
			autorun: true,
			title: 'Reject Deliverable',
			content: this.rejectModalTemplate(),
			destroyOnClose: true,
			controls: [
				{
					text: 'Cancel',
					close: true
				},
				{
					text: 'Reject',
					primary: true
				}
			]
		});

		$('.wm-modal .-active').find('.-primary').on('click', () => {
			var rejectionReason = $('.shortcutRejectDeliverableModalReason').val();
			if (_.isEmpty(rejectionReason)) {
				wmNotify({
					message: 'You must provide a reason for rejecting this deliverable.',
					type: 'danger'
				});
				return false;
			}
			$.ajax({
				url: '/assignments/reject_deliverable',
				type: 'POST',
				context: this,
				data: {
					work_number: this.model.get('workNumber'),
					asset_id: this.model.get('id'),
					rejection_reason: rejectionReason
				},
				success: function (response) {
					function redirectWithFlash(url, type, msg) {
						var e = $('<form></form>');
						e.attr({
							'action':'/message/create',
							'method':'POST'
						});
						e.append(
							$('<input>').attr({
								'name': 'message[]',
								'value': msg
							}));
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
								'name': '_tk',
								'value': getCSRFToken()
							}));
						$('body').append(e);
						e.submit();
					}

					if (response.successful) {
						var data = response.data;
						this.model.set({
							rejectedOn : data.rejectedOn,
							rejectedBy : data.rejectedBy,
							rejectionReason : data.rejectionReason,
							rejectedAsset : void 0
						});
						modal.destroy();
						if (response.redirect) {
							redirectWithFlash(data.redirect, 'success', response.messages[0]);
						}
					} else {
						wmNotify({
							message: 'You must provide a reason for rejecting this deliverable.',
							type: 'danger'
						});
					}
				}
			});
		});
	},

	openCarousel: function () {
		this.trigger('openCarousel', this.model.get('position'));
	},

	stopPropagation: function (e) {
		e.stopPropagation();
	},

	showErrorsInRejectionModal: function (errors) {
		var $errorNotificationsContainer = $('.error-notifications');
		var $errorNotifications = $errorNotificationsContainer.find('.errors');

		_.each(errors, function (error) {
			$errorNotifications.append(this.rejectModalErrorTemplate({ error: error }));
		}, this);

		$errorNotificationsContainer.show();
	}
});
