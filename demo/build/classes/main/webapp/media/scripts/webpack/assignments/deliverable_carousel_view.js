/*global,__insp*/
'use strict';

import DeliverableModalTemplate from './templates/details/deliverableModalTemplate.hbs';
import MetaInformationTemplate from './templates/details/meta_information_on_current.hbs';
import PdfTemplate from './templates/details/pdf_viewer.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import DeliverableAssetView from './deliverable_asset_view';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';

export default Backbone.View.extend({
	className: 'deliverable-modal',
	events: {
		'click .modal-header .close'       : 'closeModal',
		'click .thumbnail-carousel-'       : 'showAssetFromThumbnail',
		'click .reject-button'             : 'rejectAsset',
		'click .reject-asset-post'         : 'submitRejection',
		'click .cancel-rejection'          : 'cancelRejectionView',
		'click .switch-to-reject'          : 'switchToRejectionView',
		'click .remove'                    : 'showDeleteAssetConfirm',
		'click .cancel-deletion-of-asset'  : 'hideDeleteAssetConfirm',
		'click .delete-file'               : 'deleteAsset',
		'keydown'                          : 'keydownEventHandler',
		'click .update-deliverable-button' : 'updateAsset'
	},

	initialize: function (options) {
		// State
		this.model = options.model;
		this.ASSETS_PER_PAGE = 6;
		this.currentPage = this.getCurrentPage();
		this.rejectedFiles = [];
		this.status = options.status;
	},

	render: function () {
		// Init view
		this.$el.append(DeliverableModalTemplate({
			model: this.model,
			activeOrComplete: this.status === 'active' || this.status === 'complete',
			canDelete: this.options.isWorker || this.options.isAdmin || this.options.isOwner,
			isAdminOrOwner: this.options.isAdmin || this.options.isOwner
		}));

		// These lines allow us to capture keydown events
		this.$el.attr('tabindex', '-1');
		this.$el.focus();

		// Cache subviews
		this.$carousel = this.$('.modal-thumbnail-carousel');
		this.$projector = this.$('.deliverables-big-preview');
		this.$deletionConfirmationDialogue = this.$('.confirmation-to-delete');
		this.$modalBody = this.$('.modal-body');
		this.$spinner = this.$projector.find('.spinner-zone');
		this.$primaryImageView = this.$projector.find('.asset-preview.primary');
		this.$secondaryImageView = this.$projector.find('.asset-preview.secondary');
		this.$primaryButtonBox = this.$primaryImageView.find('.modal-button-box');
		this.$secondaryButtonBox = this.$secondaryImageView.find('.modal-button-box');

		// Init uploader
		this.$('.update-deliverable-form').fileupload({
			dataType: 'json',
			paramName: 'qqfile',
			fileInput: this.$('.update-deliverable-input'),
			url: '/assignments/add_deliverable',
			dropZone: null,
			pasteZone: null,

			add: (event, data) => {
				const [{ name, size }] = data.files;
				const validation = this.model.validateUpload({ name, size });

				// ToDo Micah: Temporary hack to capture weird undefined file events
				if (!name || name === 'undefined') {
					return false;
				}

				if (validation.errors.length > 0) {
					this.rejectedFiles.push(validation);
				}

				// Show errors
				if (this.rejectedFiles.length > 0) {
					this.showErrors(this.rejectedFiles[0].errors);
				} else {
					// Add progress bar and submit request
					this.$spinner.addClass('show');
					data.submit();
				}
			},

			done: (event, { result }) => {
				if (result.successful) {
					this.model.getSelectedAsset().pushAssetToHistory(result);
				} else {
					this.showErrors(result.errors);
				}
				this.postUploadCleanup();
			}
		}).on('fileuploadsubmit', (event, data) => {
			data.formData = {
				CSRFToken: getCSRFToken(),
				work_id: this.model.get('workNumber'),
				deliverable_requirement_id: this.model.get('id'),
				mime_type: this.model.get('mime_type'),
				position: this.model.getPositionOfSelectedAsset()
			};
		});

		this.addListeners();
		this.renderBigPreview();
		this.renderNavigationArrows();
		this.renderFooterCarousel();
		this.renderSelectedThumbnail();
		this.renderCurrentThumbnailPage();

		return this;
	},

	postUploadCleanup: function () {
		this.rejectedFiles = [];
		this.$spinner.removeClass('show');
	},

	addListeners: function () {
		$(document).on('click', { context: this }, this.clickOffModal);
		this.model.deliverableAssets.on('change', this.renderBigPreview, this);
		this.model.deliverableAssets.on('add',this.renderFooterCarousel, this);
		this.model.deliverableAssets.on('add remove', this.renderNavigationArrows, this);

		this.model.deliverableAssets.on('remove', function () {
			if (this.model.deliverableAssets.isEmpty()) {
				this.closeModal();
			}
		}, this);

		this.model.bind('setPositionOfSelectedAsset', function () {
			this.renderSelectedThumbnail();
			this.renderBigPreview();

			if (this.currentPage !== this.getCurrentPage()) {
				this.renderCurrentThumbnailPage();
				this.currentPage = this.getCurrentPage();
			}
		}, this);
	},

	renderBigPreview: function () {
		this.cancelRejectionView();

		// Reset view
		this.$('.preview-not-available').hide();
		this.$('.asset-preview').css('background-image', '');
		this.$projector.removeClass('-split');
		this.$deletionConfirmationDialogue.removeClass('-is-rejected-or-worker-view');
		this.$('.deliverable-state-ribbon').removeClass('rejectedState updatedState');
		this.$('.modal-button-box').removeClass('-should-update');
		this.$secondaryImageView.hide();
		this.$('.rejection-notes').hide();
		this.$('.deliverables-currently-previewed-information').remove();
		this.$('.pdf-previewer').remove();

		var theModelInQuestion = this.model.getSelectedAsset();
		var templateMetaInfo = {
			time: theModelInQuestion.get('uploadDate'),
			submittedOnTime: new Date(theModelInQuestion.get('uploadDate')).toDateString(),
			uploadedBy: theModelInQuestion.get('uploadedBy'),
			imageName: theModelInQuestion.get('name')
		};

		// If asset is an updated asset
		if (theModelInQuestion.get('rejectedAsset')) {
			this.renderUpdatedStateBigView(theModelInQuestion.get('rejectedAsset'), theModelInQuestion, templateMetaInfo);

		// If asset is a rejected asset
		} else if (theModelInQuestion.get('rejectedBy')) {
			this.renderRejectedStateBigView(theModelInQuestion, templateMetaInfo);

		// Otherwise, show default view
		} else {
			this.renderImageView(this.$primaryImageView, theModelInQuestion);

			// Render asset info
			templateMetaInfo.isRejected = false;
			this.$projector.append(MetaInformationTemplate(templateMetaInfo));
		}

		// If asset is a rejected asset OR if current user is a worker
		// Change the default text and available actions in deletion confirm modal

		if (theModelInQuestion.get('rejectedBy') || this.options.isWorker) {
			this.$deletionConfirmationDialogue.addClass('-is-rejected-or-worker-view');
		}

		return this;
	},

	renderImageView: function (imageView, model) {
		imageView.find('.download-button').attr('href', '/asset/download/' + model.get('uuid'));

		if (model.get('mimeType') === 'application/pdf') {
			imageView.prepend(PdfTemplate({ asset: model }));
		} else if (['image/jpeg', 'image/pjpeg', 'image/jpg', 'image/tiff', 'image/png'].includes(model.get('mimeType'))) {
			imageView.css('background-image', `url(${model.get('uriBig')})`).data('uri', model.get('uriBig'));
		} else {
			imageView.find('.preview-not-available').show();
		}
		return imageView;
	},

	renderUpdatedStateBigView: function (rejectedAsset, updatedAsset, templateMetaInfo) {
		this.$projector.addClass('-split');

		// Render rejected asset
		this.renderImageView(this.$primaryImageView, rejectedAsset)
			.find('.deliverable-state-ribbon')
			.addClass('rejectedState');

		// Render updated asset
		this.renderImageView(this.$secondaryImageView, updatedAsset)
			.show()
			.find('.deliverable-state-ribbon')
			.addClass('updatedState')
			.removeClass('rejectedState');

		// Render updated asset info
		templateMetaInfo.isRejected = false;
		this.$secondaryImageView.prepend(MetaInformationTemplate(templateMetaInfo));
	},

	renderRejectedStateBigView: function (assetModel, templateMetaInfo) {
		this.$projector.addClass('-split');
		this.$('.modal-button-box').addClass('-should-update');

		this.renderImageView(this.$primaryImageView, assetModel)
			.find('.deliverable-state-ribbon')
			.addClass('rejectedState')
			.removeClass('updatedState');

		// Render rejection notes
		templateMetaInfo.isRejected = true;
		templateMetaInfo.rejectedBy = assetModel.get('rejectedBy');
		templateMetaInfo.rejectionReason = assetModel.get('rejectionReason');
		templateMetaInfo.rejectedOn = new Date(assetModel.get('rejectedOn')).toDateString();
		$('.rejection-notes')
			.addClass('-split')
			.show()
			.append(MetaInformationTemplate(templateMetaInfo));
	},

	renderNavigationArrows: function () {
		var arrows = _.template($('#carouselArrowsTemplate').html());

		if (this.model.deliverableAssets.length > 1) {
			if (this.$projector.find('.wm-icon-right-arrow').length === 0) {
				this.$projector.append($(arrows()).clone());
				var leftArrow = this.$projector.find('.wm-icon-left-arrow');
				var rightArrow = this.$projector.find('.wm-icon-right-arrow');
				$(rightArrow).on('click', this.toggleRight.bind(this));
				$(leftArrow).on('click', this.toggleLeft.bind(this));
			}
		} else {
			this.$projector.find('.icon-right-arrow').unbind().remove();
		}

		if (this.model.deliverableAssets.length > 5) {
			if (this.$modalBody.find('.icon-right-arrow').length === 0) {
				this.$modalBody.find('.paging-buttons').append($(arrows()).clone());
				var footerArrows = this.$modalBody.find('.icon-right-arrow');
				$(footerArrows[0]).on('click', this.pageRight.bind(this));
				$(footerArrows[1]).on('click', this.pageLeft.bind(this));
			}
		} else {
			this.$modalBody.find('.icon-right-arrow').unbind().remove();
		}
	},

	renderFooterCarousel: function () {
		_.each(this.$carousel.children(), (thumbnail) => $(thumbnail).unbind().remove());
		this.model.deliverableAssets.each((asset) => this.addDeliverableAsset(asset));
	},

	renderSelectedThumbnail: function () {
		this.$carousel.find('li').removeClass('isSelected');
		$(this.getSelectedThumbnail()).addClass('isSelected');
	},

	renderCurrentThumbnailPage: function () {
		var start = this.getCurrentPage() * this.ASSETS_PER_PAGE,
			end = start + this.ASSETS_PER_PAGE;

		this.$carousel.find('li').each((index, thumbnail) => $(thumbnail).toggle(index >= start && index < end));
	},

	showAssetFromThumbnail: function (e) {
		var clickedPosition = $(e.target).closest('.thumbnail-carousel-').data('position');
		if (typeof clickedPosition !== 'undefined') {
			this.model.setPositionOfSelectedAsset(clickedPosition);
		}
	},

	toggleRight: function () {
		this.model.incrementPositionOfSelectedAsset(1);
	},

	toggleLeft: function () {
		this.model.incrementPositionOfSelectedAsset(-1);
	},

	pageRight: function () {
		this.pageBy(1);
	},

	pageLeft: function () {
		this.pageBy(-1);
	},

	pageBy: function (amount) {
		this.model.setPositionOfSelectedAsset(
			this.getPositionAtIndex(
				this.getFirstIndexOnNextPage(amount)));
	},

	keydownEventHandler: function (e) {
		if (e.which === 37) {
			this.toggleLeft();
		} // left
		if (e.which === 39) {
			this.toggleRight();
		} // right
	},

	getFirstIndexOnNextPage: function (pageAmount) {
		return Math.abs(((this.getCurrentPage() + pageAmount) % this.getPageMax()) * this.ASSETS_PER_PAGE);
	},

	getPositionAtIndex: function (index) {
		return this.model.deliverableAssets.getPositionAtIndex(index);
	},

	getCurrentPage: function () {
		return Math.floor(Math.abs(this.model.getIndexOfSelectedAsset() / this.ASSETS_PER_PAGE));
	},

	getPageMax: function () {
		return Math.ceil(this.model.deliverableAssets.length / this.ASSETS_PER_PAGE);
	},

	getSelectedThumbnail: function () {
		return _.find(this.$carousel.find('li'), (el) => {
			return $(el).data('position') === this.model.getPositionOfSelectedAsset();
		});
	},

	showModal: function () {
		this.$el.removeClass('hide').focus();
	},

	closeModal: function () {
		this.$el.addClass('hide');
	},

	clickOffModal: function ({ data: { context }, target }) {
		if ($(target).is(context.el)) {
			context.closeModal();
		}
	},

	addDeliverableAsset: function (deliverableAsset) {
		var thumbnailInFooterCarousel = new DeliverableAssetView({
			auth: this.options,
			model: deliverableAsset,
			millisOffset: this.options.millisOffset,
			className: 'thumbnail-carousel- deliverable'
		});

		this.$carousel.append(thumbnailInFooterCarousel.render().el);
	},

	rejectAsset: function () {
		if (this.model.getSelectedAsset().get('rejectedAsset')) {
			this.$secondaryButtonBox.addClass('-is-being-rejected');
		} else {
			this.$primaryButtonBox.addClass('-is-being-rejected');
		}
		$('.rejectDeliverableModalReason').focus();
	},

	submitRejection: function () {
		var theModelInQuestion = this.model.getSelectedAsset();

		var assetId = theModelInQuestion.get('id');

		$.ajax({
			url: '/assignments/reject_deliverable',
			type: 'POST',
			context: this,
			data: {
				work_number: this.model.get('workNumber'),
				asset_id: assetId,
				rejection_reason: this.getRejectionReason()
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
					theModelInQuestion.set({
						rejectedOn : data.rejectedOn,
						rejectedBy : data.rejectedBy,
						rejectionReason : data.rejectionReason,
						rejectedAsset : void 0
					});
					$('.rejectDeliverableModalReason').val('');
					if (response.redirect) {
						redirectWithFlash(data.redirect, 'success', response.messages[0]);
					}
				} else {
					this.showErrors(['You must provide a reason for rejecting this deliverable.']);
				}
			}
		});
	},

	getRejectionReason: function () {
		return $('.rejectDeliverableModalReason:visible').val();
	},

	cancelRejectionView: function () {
		$('.-is-being-rejected').removeClass('-is-being-rejected');
	},

	switchToRejectionView: function () {
		this.hideDeleteAssetConfirm();
		this.rejectAsset();
	},

	showDeleteAssetConfirm: function () {
		this.$projector.addClass('grayed-out');
		this.$('.confirmation-to-delete').show();
		this.$('.modal-button-box').hide();
	},

	hideDeleteAssetConfirm: function () {
		this.$projector.removeClass('grayed-out');
		this.$('.confirmation-to-delete').hide();
		this.$('.modal-button-box').show();
	},

	deleteAsset: function () {
		var theModelInQuestion = this.model.getSelectedAsset();

		if (typeof __insp !== 'undefined') {
			__insp.push(['tagSession', 'Deleted Deliverable']);
		}

		$.ajax({
			url: '/assignments/remove_attachment',
			data: {
				work_id: theModelInQuestion.get('workNumber'),
				asset_id: theModelInQuestion.get('id')
			},
			type: 'POST',
			dataType: 'json',
			context: this
		}).done(function (response) {
			if (response.successful) {
				theModelInQuestion.popAssetFromHistory({
					beforeDestroy: this.model.incrementPositionOfSelectedAsset.bind(this.model, 1)
				});
			}
			this.hideDeleteAssetConfirm();
		});
	},

	updateAsset: function (e) {
		if (!$(e.target).is($('.update-deliverable-input'))) {
			this.$('.update-deliverable-input').click();
		}
	},

	showErrors: function (errors) {
		errors.forEach((message) => {
			wmNotify({
				message,
				type: 'danger',
				element: '#rejectDeliverableForm',
				placement: { align: 'left', from: 'top' },
				offset: 100
			});
		});
	}
});
