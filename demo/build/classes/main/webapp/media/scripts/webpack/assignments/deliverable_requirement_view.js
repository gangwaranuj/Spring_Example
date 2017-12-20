'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import 'underscore.inflection';
import DeliverableCarouselView from './deliverable_carousel_view';
import DeliverableAssetView from './deliverable_asset_view';
import DeliverableAssetModel from './deliverable_asset_model';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import RequiremnetHeaderTemplate from './templates/details/deliverableRequirementHeader.hbs';
import UnorderedListTemplate from './templates/details/deliverableUnorderedList.hbs';
import UploaderContainerTemplate from './templates/details/deliverableUploaderContainer.hbs';
import PlaceholderTemplate from './templates/details/deliverablePlaceholder.hbs';
import UploadProgressBarTemplate from './templates/details/uploadProgressBar.hbs';
import DropzoneTemplate from './templates/details/deliverableDropzone.hbs';
import PendingUploadRequiredTemplate from './templates/details/pendingUploadRequiredDeliverable.hbs';

/*
 *  Deliverable requirement displays the metadata for that requirement and any associated
 *  deliverable assets.
 */
export default Backbone.View.extend({
	tagName: 'li',
	className: 'upload-assets dropzone',
	attributes : function () {
		return {
			id : 'upload-assets-' + this.model.cid,
			'data-type' : this.model.attributes.type
		};
	},

	initialize: function (options) {
		this.model = options.model;
		this.status = options.status;

		// Templates
		this.headerTemplate = RequiremnetHeaderTemplate;
		this.deliverableAssetListTemplate = UnorderedListTemplate;
		this.uploaderContainerTemplate = UploaderContainerTemplate;
		this.placeholderTemplate = PlaceholderTemplate;
		this.uploadInprogressTemplate = UploadProgressBarTemplate;
		this.dropzoneTemplate = DropzoneTemplate;
		this.pendingUploadTemplate = PendingUploadRequiredTemplate;

		// State
		this.deliverableRequirementName = this.model.get('DELIVERABLE_TYPES')[this.options.model.get('type')].description;
		this.deliverableRequirementIdSuffix = this.deliverableRequirementName.replace(/\s+/g, '-');
		this.headerId = 'deliverableHeader-' + this.deliverableRequirementIdSuffix;
		this.uploadContainerId = 'attachment-uploader-' + this.deliverableRequirementIdSuffix;

		// Attach listeners
		this.model.deliverableAssets.on('remove', function (asset) {
			if (this.model.needPlaceholder()) {
				this.addPlaceholder(asset.get('position'));
			}
		}, this);

		this.model.deliverableAssets.on('change remove add', function () {
			this.updateDeliverableAssetCount(true);
		}, this);
	},

	render: function () {
		$(this.el).empty();

		var numberOfRequiredUploads = this.model.get('numberOfFiles');
		var numberOfRequirementFulfillingUploads = Math.min(this.model.getNumberOfNonRejectedUploads(), numberOfRequiredUploads);
		var className = numberOfRequirementFulfillingUploads === numberOfRequiredUploads ? 'text-success' : 'text-error';

		// Add header (Deliverable Type, Submitted Count, Required Count, etc)
		var generatedHeader = this.headerTemplate({
			name: this.deliverableRequirementName,
			id: this.headerId,
			className: className,
			instructions: this.model.get('instructions')
		});
		$(generatedHeader).prependTo($(this.el));

		// Add file dropzone
		var generatedDropzone = this.dropzoneTemplate();
		this.$uploadZone = $(generatedDropzone).appendTo($(this.el));

		// Generate uploaded deliverables container
		var generatedDeliverableAssetList = this.deliverableAssetListTemplate({ cssClass: 'deliverable-asset-list deliverable-details-thumbnails' });
		$(generatedDeliverableAssetList).appendTo($(this.el));
		this.$assetList = this.$('ul.deliverable-asset-list');

		// Add uploaded deliverables
		_.each(this.model.deliverableAssets.models, function (asset) {
			this.addDeliverableAsset(asset);
		}, this);

		// Add placeholders, if necessary
		if (this.model.needPlaceholder()) {
			var nextAssetPosition = this.model.getNextPosition();
			_.times(this.model.getPlaceholdersNeeded(), function () {
				this.addPlaceholder(nextAssetPosition);
				nextAssetPosition++;
			}, this);
		}

		// Generate file uploader container
		var generatedUploadContainer = this.uploaderContainerTemplate({
			uploadAllowed: this.options.isWorker || this.options.isAdmin || this.options.isOwner,
			containerId: this.uploadContainerId,
			deliverableRequirementId: this.model.get('id'),
			workNumber: this.model.get('workNumber')
		});
		$(generatedUploadContainer).appendTo($(this.el));
		this.updateDeliverableAssetCount(true);

		this.$('#' + this.uploadContainerId).find('form').fileupload({
			dataType: 'json',
			paramName: 'qqfile',
			fileInput: this.$('#' + this.uploadContainerId).find('input'),
			url: '/assignments/add_deliverable',
			dropZone: null,
			pasteZone: null,

			start: _.bind(function () {
				this.disableUploader();
			}, this),

			add: _.bind(function (e, data) {

				// Wish this block could go in the 'start' callback, but we don't get access to the 'data' object there
				if (this.isFirstRequest) {

					this.setUploadPositions(data.originalFiles);

					// Validate upload
					_.each(data.originalFiles, function (file) {
						var validation = this.model.validateUpload({name: file.name, size: file.size});
						if (validation.errors.length > 0) {
							this.rejectedFiles.push(validation);
						}
					}, this);

					// Show errors
					if (this.rejectedFiles.length > 0) {
						this.showErrors(_.flatten(_.pluck(this.rejectedFiles, 'errors')));
					}

					this.uploadFilesRemaining = data.originalFiles.length - this.rejectedFiles.length;

					// Init upload progress bar, if necessary
					if (this.uploadFilesRemaining > 0 && this.$('.upload-progress').size() === 0) {
						var generatedPendingUpload = this.uploadInprogressTemplate({
							number: this.uploadFilesRemaining,
							object: _.pluralize('file', this.uploadFilesRemaining)
						});
						$(generatedPendingUpload).appendTo(this.$('.upload-bottom-bar'));
					}

					this.isFirstRequest = false;
				}

				// If file passed validation, submit request
				if (this.fileIsNotRejected(data.files[0].name)) {
					data.submit();
				}

				// If on last request AND no requests passed validation (i.e. no requests were made), do upload cleanup here
				// Need to do cleanup here in this case since we'll never hit the done callback
				if (this.isLastUpload(data) && data.originalFiles.length === this.rejectedFiles.length) {
					this.resetUploadVars();
				}
			}, this),

			progressall: _.bind(function (e, data) {
				this.drawProgressBar(data);
			}, this),

			done: _.bind(function (e, response) {
				if (response.result.successful) {
					this.uploadAssetCallback(response.result);
				} else {
					this.showErrors(response.result.errors);
				}

				// Update file upload counter
				--this.uploadFilesRemaining;
				this.$('.upload-progress').find('.number').text(this.uploadFilesRemaining);
				this.$('.upload-progress').find('.object').text( _.pluralize('file', this.uploadFilesRemaining));
			}, this),

			// Callback to do cleanup for when all uploads are finished
			stop: _.bind(function () {
				this.resetUploadVars();
				this.$assetList.find('li.temp').remove();
			}, this)

		}).on('fileuploadsubmit', _.bind(function (e, data) {

			// Add form data, if file passed validation
			if (this.fileIsNotRejected(data.files[0].name)) {
				data.formData = {
					CSRFToken: getCSRFToken(),
					work_id: this.model.get('workNumber'),
					deliverable_requirement_id: this.model.get('id'),
					mime_type: this.model.get('mime_type'),
					position: this.getNextUploadPosition()
				};
			}
		}, this));

		// Upload related state
		this.resetUploadVars();

		return this;
	},

	setUploadPositions: function (numberOfUploads) {
		this.uploadPositions = _.map(this.$assetList.children('.still-needed-placeholder'), function (el) {
			return $(el).data('position');
		});
		var extraPositionsNeeded = Math.max(numberOfUploads.length - this.uploadPositions.length, 0),
			nextPosition = this.$assetList.children().last().data('position') + 1;
		_.times(extraPositionsNeeded, function () {
			this.uploadPositions.push(nextPosition);
			$(this.pendingUploadTemplate({ position : nextPosition })).appendTo(this.$assetList);
			nextPosition++;
		}, this);
	},

	getNextUploadPosition: function () {
		var nextUploadPosition = _.first(this.uploadPositions);
		this.uploadPositions = _.rest(this.uploadPositions);
		return nextUploadPosition;
	},

	disableUploader: function () {
		this.$uploadZone.css('display', 'none');
		this.$('#' + this.uploadContainerId).find('input').prop('disabled', true);
	},

	enableUploader: function () {
		this.$uploadZone.css('display', '');
		this.$('#' + this.uploadContainerId).find('input').prop('disabled', false);
	},

	isLastUpload: function (uploadData) {
		return uploadData.originalFiles.indexOf(uploadData.files[0]) === uploadData.originalFiles.length - 1;
	},

	fileIsNotRejected: function (name) {
		return _.isUndefined(_.find(this.rejectedFiles, function (file) {
			return file.name === name;
		}));
	},

	resetUploadVars: function () {
		this.$('.upload-progress').remove();
		this.uploadFilesRemaining = 0;
		this.rejectedFiles = [];
		this.uploadPositions = [];
		this.isFirstRequest = true;
		this.enableUploader();
	},

	addDeliverableAsset: function (deliverableAsset) {
		/* The deliverableAsset data being passed in via the uploader contains a format like:
		 *
		 *   position: Int,
		 *   deliverableRequirementId: Int,
		 *   deliverableRequirements: Int,
		 *   description: String,
		 *   id: Int,
		 *   mimeType: String,
		 *   name: String,
		 *   type: String,
		 *   uploadDate: Int,
		 *   uploadedBy: String,
		 *   uri: String, => on immediate upload this doesn't exist
		 *   uuid: String, => we concatenate this to create a uri on upload so we can thumbnail
		 *
		 *   The position acts like an asset grouping tag.
		 *
		 * */

		// add some data to it's model for the template
		var deliverableAssetView = new DeliverableAssetView({
			auth: this.options,
			model: deliverableAsset,
			millisOffset: this.options.millisOffset
		});

		deliverableAssetView.bind('openCarousel', this.openCarousel, this);

		// render the asset as a thumbnail
		var $deliverableTarget = this.$assetList.find('li[data-position="' + deliverableAssetView.model.get('position') + '"]');
		if ($deliverableTarget.length) {
			$deliverableTarget.replaceWith(deliverableAssetView.render().el);
		} else {
			$(deliverableAssetView.render().el).appendTo(this.$assetList);
		}
	},

	// Utility to ensure placeholders are added at the same position as a deleted asset
	addPlaceholderToView: function ($generatedPlaceholder, position) {
		var lower = this.$assetList.children().first();
		var upper = lower;

		if (position < lower.data('position')) {
			// add to front of list
			this.$assetList.prepend($generatedPlaceholder);
			return;
		}
		upper = upper.next();
		while (!(position > lower.data('position') && position < upper.data('position'))) {
			lower = lower.next();
			upper = upper.next();
			if (upper.length === 0) {
				break;
			}
		}
		if (upper.length === 0) {
			// add to end of list
			this.$assetList.append($generatedPlaceholder);
			return;
		}
		// add just before 'upper' node
		upper.before($generatedPlaceholder);
	},

	addPlaceholder: function (position) {
		// clean slate
		var placeholderModel = {};

		// give it a type so the placeholder icon is correct
		placeholderModel.type = this.model.get('type');
		placeholderModel.position = position;
		placeholderModel.deliverableRequirementId = this.model.get('id');

		// then bind it's model with the template
		var $generatedPlaceholder = $(this.placeholderTemplate(placeholderModel));

		// and finally append it at the right position
		this.addPlaceholderToView($generatedPlaceholder, position);

		var $fileForm = $generatedPlaceholder.find('.deliverable-form');
		$fileForm.fileupload({
			dataType: 'json',
			url: '/assignments/add_deliverable',
			fileInput: $fileForm.find('input[type=file]'),
			singleFileUploads: true,
			paramName: 'qqfile',
			dropZone: null,
			pasteZone: null,
			formData: {
				CSRFToken: getCSRFToken(),
				work_id: this.model.get('workNumber'),
				deliverable_requirement_id: this.model.get('id'),
				position
			},

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
				}

				// Add progress bar and submit request, if file passed validation
				if (this.fileIsNotRejected(name)) {
					let generatedPendingUpload = this.uploadInprogressTemplate({ number: 1, object: 'file'});
					$(generatedPendingUpload).appendTo(this.$('.upload-bottom-bar'));
					data.submit();
				}
			},

			progress: (event, data) => this.drawProgressBar(data),

			done: (event, { result }) => {
				if (result.successful) {
					this.uploadAssetCallback(result);
				} else {
					this.showErrors(result.errors);
				}

				this.resetUploadVars();
			}
		});
	},

	updateDeliverableAssetCount: function (tellSubscribers) {
		if (tellSubscribers) {
			this.trigger('deliverableRequirementStateChange');
			this.populateHeader();
			this.populateFooter();
		}
	},

	populateHeader: function () {
		var missingCount = this.model.get('numberOfFiles') - this.model.getNumberOfNonRejectedUploads();

		$(this.el).children('.orange-requirement-square').text(this.model.get('numberOfFiles'));

		// if there are uploads and there are still some left before all requirements are fulfilled
		if (this.model.get('numberOfFiles') > this.model.getNumberOfNonRejectedUploads()) {
			// then let the user know how many are still needed
			$(this.el).children('.still-needed-reqs').text(missingCount + ' ' + _.pluralize('file',missingCount) + ' missing');
			$(this.el).children('.all-requirements-fulfilled').removeClass('show');
		} else {
			// otherwise the message should be cleared
			$(this.el).children('.still-needed-reqs').text('');
			$(this.el).children('.all-requirements-fulfilled').addClass('show');
		}
	},

	populateFooter: function () {
		var $downloadAllButton = $(this.$assetList).next('.upload-bottom-bar').children('.download-all-button');

		//if there are any uploads at all, show the download all button
		if (this.model.getNumberOfNonRejectedUploads() > 0) {
			$downloadAllButton.show();
		} else {
			$downloadAllButton.hide();
		}
	},

	openCarousel: function (position) {
		this.model.setPositionOfSelectedAsset(position);
		if (!this.modal) {
			this.modal = new DeliverableCarouselView({
				model : this.model,
				status: this.status,
				isWorker: this.options.isWorker,
				isAdmin: this.options.isAdmin,
				isOwner: this.options.isOwner
			});
			this.modal.$el.appendTo(this.$el);
			this.modal.render();
		} else {
			this.modal.showModal();
		}

	},

	uploadAssetCallback: function (asset) {
		var deliverableAssetModel = new DeliverableAssetModel(asset);
		this.model.deliverableAssets.add(deliverableAssetModel);
		this.addDeliverableAsset(deliverableAssetModel);
	},

	drawProgressBar: function(data) {
		var progress = parseInt(data.loaded / data.total * 100, 10) + '%';
		this.$('.bar').css('width', progress).html('Uploading ...');
	},

	showErrors: function (errors) {
		_.each(errors, function (error) {
			wmNotify({
				message: error,
				type: 'danger',
				element: $('#' + this.el.id),
				placement: { align: 'left', from: 'top' },
				z_index: 9997
			});
		}, this);
	}
});
