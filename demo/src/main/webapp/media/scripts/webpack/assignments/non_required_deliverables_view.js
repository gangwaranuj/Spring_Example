'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import NonRequiredDeliverableAssetView from './non_required_deliverable_asset_view';
import wmNotify from '../funcs/wmNotify';
import '../funcs/autoresizeTextarea';
import DeliverableUnorderedList from './templates/details/deliverableUnorderedList.hbs';
import NonRequiredDeliverableUploader from './templates/details/nonRequiredDeliverableUploader.hbs';
import NonRequiredDeliverableUploadingPending from './templates/details/nonRequiredDeliverableUploadPending.hbs';
import '../dependencies/jquery.fileupload';

/*
 *  Non-required deliverables view
 *  Enabled only in two scenarios:
 *   1) Custom Closeout enabled, no requirements set
 *   2) Custom Closeout disabled
 */
export default Backbone.View.extend({
	el: '#deliverables',
	className: 'documents-two',
	events: {
		'click .uploader-button': 'showFileChooser'
	},

	initialize: function (options) {
		this.model = options.model;
		this.workNumber = options.workNumber;
		this.options = options;

		// Templates
		this.deliverableRequirementListTemplate = DeliverableUnorderedList;
		this.uploaderTemplate = NonRequiredDeliverableUploader;
		this.uploadInprogressTemplate = NonRequiredDeliverableUploadingPending

		// Document state
		this.$deliverablesContainer = this.$('.documents');
		this.$downloadAllButton = this.$('.deliverables-download-all-icon');
	},

	render: function () {
		// Rendering is handled by the JSP on Sent and Draft status ... move rendering to js exclusively? ... use a template and load ...
		if (this.options.status === 'sent' || this.options.status === 'draft') {
			return this;
		}

		var generatedList = this.deliverableRequirementListTemplate({ cssClass: 'non-required-deliverables' });
		$(generatedList).appendTo(this.$deliverablesContainer);

		this.$deliverableRequirementList = $('ul.non-required-deliverables', this.$deliverablesContainer);

		_.each(this.model, this.addDeliverableAsset, this);

		this.toggleDownloadAllButton();

		var generatedUploader = this.uploaderTemplate();
		$(generatedUploader).appendTo(this.$deliverablesContainer);
		this.$fileForm = $('.deliverable-form', this.$deliverablesContainer);
		this.$fileUploaderInput = $('.uploader-input', this.$deliverablesContainer);

		this.$fileForm.fileupload({
			dataType: 'json',
			url: '/assignments/add_deliverable',
			fileInput: this.$fileForm.find('input[type=file]'),
			paramName: 'qqfile',
			dropZone: null,
			pasteZone: null,
			formData: {
				work_id: this.workNumber
			},

			add: (event, data) => {
				// jQuery uploader replaces the <input> element after each upload
				// This causes this.$fileUploaderInput to be invalid because it references an element that no longer exists
				// This line re-assigns this.$fileUploaderInput with the new element
				// Without this line, you'll notice that the uploading works the first time and fails on subsequent attempts
				this.$fileUploaderInput = $('.uploader-input', event.target);

				// ToDo Micah: Temporary hack to capture weird undefined file events
				var [{ name }] = data.files;
				if (!name || name === 'undefined') {
					return false;
				}

				let generatedPendingUpload = this.uploadInprogressTemplate({ fileName: name });
				data.context = $(generatedPendingUpload).appendTo(this.$deliverableRequirementList);
				data.submit();
			},

			progress: (event, { loaded, total, context }) => {
				const progress = `${parseInt(loaded / total * 100, 10)}%`;
				const $progressBar = $('.bar', context);
				$progressBar.css('width', progress).html('Uploading ...');
			},

			done: (event, { result, context }) => {
				const { successful, file_name, largeUuid, errors } = result;

				if (successful) {
					result.workNumber = this.workNumber;
					result.name = file_name;
					if (!_.isEmpty(largeUuid)) {
						result.transformLargeUuid = largeUuid;
						result.uri = `/asset/${largeUuid}`;
					}
					this.addDeliverableAsset(result, context);
					this.toggleDownloadAllButton();
				} else {
					this.showErrors(errors);
					context.remove();
				}
			}
		});

		return this;
	},

	addDeliverableAsset: function (deliverableAsset, placeholder) {
		deliverableAsset.workNumber = this.workNumber;

		var nonRequiredDeliverableAsset = new NonRequiredDeliverableAssetView({
			model: deliverableAsset,
			isAdmin: this.options.isAdmin,
			isActiveWorker: this.options.isActiveWorker
		});

		nonRequiredDeliverableAsset.bind('assetRemoved', _.bind(this.toggleDownloadAllButton, this));

		var renderedNonRequiredDeliverableAsset = nonRequiredDeliverableAsset.render().el;

		if (_.isUndefined(placeholder) || !(placeholder instanceof $)) {
			this.$deliverableRequirementList
				.append(renderedNonRequiredDeliverableAsset)
				.find('.asset-description')
				.last()
				.autoresizeTextarea();
		} else {
			placeholder.replaceWith(renderedNonRequiredDeliverableAsset);
		}
	},

	showFileChooser: function() {
		this.$fileUploaderInput.click();
	},

	toggleDownloadAllButton: function () {
		var numberOfUploads = this.$deliverableRequirementList.children().length;

		if (numberOfUploads) {
			this.$downloadAllButton.removeClass('-hidden');
		} else {
			this.$downloadAllButton.addClass('-hidden');
		}
	},

	showErrors: function (errors) {
		_.each(errors, function (error) {
			wmNotify({
				message: error,
				type: 'danger'
			});
		}, this);
	}

});
