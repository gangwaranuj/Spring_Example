/*jshint camelcase:false*/
'use strict';

import UnorderedListTemplate from './templates/details/deliverableUnorderedList.hbs';
import NonRequiredDeliverableTemplate from './templates/details/nonRequiredDeliverableUploadPending.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import DocumentAssetView from './document_asset_view';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import '../funcs/autoresizeTextarea';
import '../dependencies/jquery.fileupload';
import moment from 'moment';

export default Backbone.View.extend({
	el: '#documents',
	className: 'documents-list',
	events: {
		'click .upload-documents-trigger' : 'showFileChooser'
	},

	initialize: function (options) {

		this.options = options;
		// Document State
		this.$messages = this.$('.message');
		this.$documentsContainer = this.$('.documents');
		this.$uploadDocumentsForm = this.$('.upload-documents form');
		this.$uploadDocumentInput = this.$uploadDocumentsForm.find('input');

		this.asset_type = 'attachment';

		// Templates
		this.documentsListTemplate = UnorderedListTemplate;
		this.uploadInprogressTemplate = NonRequiredDeliverableTemplate;
	},

	render: function () {

		var generatedList = this.documentsListTemplate({ cssClass: 'non-required-deliverables' });
		this.$documentsList = $(generatedList).appendTo(this.$documentsContainer);

		this.collection.forEach(this.addDocument, this);

		this.$uploadDocumentsForm.fileupload({
			dataType: 'json',
			url: '/assignments/add_document',
			fileInput: this.$uploadDocumentInput,
			paramName: 'qqfile',
			dropZone: null,
			pasteZone: null,
			formData: {
				CSRFToken: getCSRFToken(),
				work_id: this.options.workNumber,
				asset_type: this.asset_type
			},

			add: (event, data) => {
				// jQuery uploader replaces the <input> element after each upload
				// This causes this.$fileUploaderInput to be invalid because it references an element that no longer exists
				// This line re-assigns this.$fileUploaderInput with the new element
				// Without this line, you'll notice that the uploading works the first time and fails on subsequent attempts
				this.$uploadDocumentInput = $('input', event.target);

				// ToDo Micah: Temporary hack to capture weird undefined file events
				let fileName = data.files[0].name;
				if (!fileName || fileName === 'undefined') {
					return false;
				}
				let generatedPendingUpload = this.uploadInprogressTemplate({ fileName });
				data.context = $(generatedPendingUpload).appendTo(this.$documentsList);
				data.submit();
			},

			progress: (event, { loaded, total, context }) => {
				$('.bar', context)
					.css('width', `${parseInt(loaded / total * 100, 10)}%`)
					.html('Uploading ...');
			},

			done: (event, { result, context }) => {
				const { largeUuid, uuid, successful, file_name } = result;

				if (successful) {
					result.workNumber = this.workNumber;
					result.name = file_name;
					if (!_.isEmpty(largeUuid)) {
						result.transformLargeUuid = largeUuid;
						result.uri = `/asset/${largeUuid}`;
					} else if (typeof uuid !== 'undefined') {
						result.uri = `/asset/${uuid}`;
					}
					this.addDocument(result, context);
				} else {
					let errors = result.errors.length ? result.errors : ['There was a problem uploading your document.'];
					errors.forEach((message) => wmNotify({ message, type: 'danger' }));
					$uploadPlaceholder.remove();
				}
			}
		});

		return this;
	},

	addDocument: function (doc,  $uploadPlaceholder) {
		doc.workNumber = this.options.workNumber;
		doc.visibilityDescription = this.options.visibilitySettings[doc.visibilityCode].description;
		doc.createdOnDate = moment(doc.createdOn).format('M/DD/YY h:mma');
		var renderedDocument = new DocumentAssetView({
			model: doc,
			isAdmin: this.options.isAdmin,
			isActiveWorker: this.options.isActiveWorker
		}).render().el;

		if (_.isUndefined($uploadPlaceholder) || !($uploadPlaceholder instanceof $)) {
			$(this.$documentsList).append(renderedDocument)
				.find('.asset-description')
				.last()
				.autoresizeTextarea();
		} else {
			$uploadPlaceholder.replaceWith(renderedDocument);
		}
	},

	showFileChooser: function () {
		this.$uploadDocumentInput.trigger('click');
	}
});
