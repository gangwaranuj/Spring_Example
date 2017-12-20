'use strict';

import $ from 'jquery';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import wmModal from '../funcs/wmModal';
import 'datatables.net';
import '../funcs/autoresizeTextarea';
import qq from '../funcs/fileUploader';
import NonRequiredDocumentTemplate from '../assignments/templates/details/non_required_document.hbs';
import DeliverableUnorderedListTemplate from '../assignments/templates/details/deliverableUnorderedList.hbs'
import NonRequireDocumentContainerTemplate from '../assignments/templates/documents/nonRequiredDocumentContainer.hbs';
import NonRequiredDocumentUploadPendingTemplate from '../assignments/templates/documents/nonRequiredDocumentUploadPending.hbs';
import UploaderButtonTemplate from '../assignments/templates/documents/uploaderButton.hbs';
import FileManagerTableTemplate from '../assignments/templates/creation/file_manager_table.hbs';
import CheckboxCellTemplate from '../assignments/templates/creation/checkbox_cell.hbs';
import moment from 'moment'

export default {

	init: function (attachmentsPayload, visibilitySettings, defaultVisibility, isAdminOrActiveResource) {
		var attachments = attachmentsPayload || {};

		this.defaultVisibilityType = defaultVisibility;
		this.visibilitySettings = visibilitySettings;
		this.isAdminOrActiveResource = isAdminOrActiveResource;

		// Templates
		this.uploadedAssetTemplate = NonRequiredDocumentTemplate;
		this.documentListTemplate = DeliverableUnorderedListTemplate;
		this.documentUploaderContainerTemplate = NonRequireDocumentContainerTemplate;
		this.uploadInprogressTemplate = NonRequiredDocumentUploadPendingTemplate;
		this.uploadButtonTemplate = UploaderButtonTemplate;

		var generatedDocumentList = this.documentListTemplate({ cssClass: 'documents' }),
			generatedUploader = this.documentUploaderContainerTemplate();

		// Document state
		this.$rootElement = $('#assignment-documents');
		this.$documentsContainer = this.$rootElement.find('#documents-container');

		$(generatedUploader).appendTo(this.$documentsContainer);
		$(generatedDocumentList).appendTo(this.$documentsContainer);

		this.$documentsList = this.$documentsContainer.find('ul.documents');
		this.$fileForm = this.$documentsContainer.find('.deliverable-upload');
		this.$attachmentMessagesParent = this.$documentsContainer.find('#attachment_messages');
		this.$attachmentMessages = this.$attachmentMessagesParent.find('div');

		this.attachmentUploader = new qq.FileUploader({
			element: this.$documentsContainer.find('.uploader-container')[0],
			action: '/upload/uploadqq',
			CSRFToken: getCSRFToken(),
			sizeLimit: 150 * 1024 * 1024, // 150MB
			multiple: true,
			template: this.uploadButtonTemplate(),
			fileTemplate: this.uploadInprogressTemplate().trim(),
			listElement: this.$documentsList[0],
			onProgress: _.bind(function(id, fileName, loaded, total) {
				var $upload = $(this.getElementByQQFileId(id));

				var progress = Math.round(loaded / total * 100) + '%';
				var $progressBar = $('.bar', $upload);

				$progressBar.css('width', progress).html('Uploading ...');
			}, this),
			onComplete: _.bind(function(id, filename, json) {
				var $upload = $(this.getElementByQQFileId(id));

				if (json.successful) {
					var document = {
						index: id,
						id: json.id,
						uuid: json.uuid,
						description: json.description,
						name: filename,
						mime_type: json.mime_type,
						is_upload: true,
						visibility_type: this.defaultVisibilityType,
						createdOnDate: moment(json.createdOn).format('M/DD/YY h:mma')
					};

					var $generatedDocument = $(this.getGeneratedDocumentTemplate(document));

					this.setupTextarea($generatedDocument);

					this.setupVisibilityDropdown($generatedDocument, this.defaultVisibilityType);

					$upload.replaceWith($generatedDocument);

				} else {
					$upload.remove();
					this.$attachmentMessages.html(json.errors[0]);
					this.$attachmentMessagesParent.addClass('error').show();
				}
			}, this)
		});

		// Find out the format of attachments. Change this line accordingly ...
		_.each(attachments, this.addDocument, this);

		this.$rootElement.on('mouseenter', '.deliverable-icon-container', function () {
			$(this).find('.deliverable-icon').addClass('darken');
		});

		this.$rootElement.on('mouseleave', '.deliverable-icon-container', function () {
			$(this).find('.deliverable-icon').removeClass('darken');
		});

		this.$rootElement.on('click', '.uploader-button', _.bind(function () {
			this.$rootElement.find('.uploader-input').trigger('click');
		}, this));

		this.$rootElement.on('click', '.remove', function () {
			var $elementToRemove = $(this).closest('.non-required-deliverable');
			$elementToRemove.fadeOut('fast', _.bind(function () {
				$elementToRemove.unbind();
				$elementToRemove.remove();
			}, this));
		});

		this.$rootElement.on('click', '.visibility-option', _.bind(this.setVisibilitySelection, this));

		$('#assignments_form').on('click', '#select_filemanager_files', _.bind(function () {
			this.documentsModal = wmModal({
				autorun: true,
				title: 'File Manager',
				destroyOnClose: true,
				content: FileManagerTableTemplate()
			});

			var meta = null;

			$('#documents_list').dataTable({
				'sPaginationType': 'full_numbers',
				'bLengthChange': false,
				'bFilter': false,
				'bStateSave': false,
				'bProcessing': true,
				'bServerSide': true,
				'sAjaxSource': '/filemanager/documents_for_list',
				'iDisplayLength': 25,
				'aoColumnDefs': [
					{'bSortable': false, 'aTargets': [0]},
					{
						'mRender': (data, type, val, metaData) => {
							return CheckboxCellTemplate({
								meta: meta[metaData.row]
							});
						},
						'aTargets': [0]
					}
				],
				'fnServerData': function ( sSource, aoData, fnCallback ) {
					$.getJSON( sSource, aoData, function (json) {
						meta = json.aMeta;
						fnCallback(json);
					});
				}
			});

			$('#attach_assets_to_assignment').on('click', _.bind(function (event) {
				event.preventDefault();

				var list = $('#attach_assets_to_assignment_form').serializeArray();
				this.processSelectedAssetsToAdd(list);
			}, this));
		}, this));
	},

	processSelectedAssetsToAdd: function (assets) {
		if (typeof assets === 'undefined' || assets.length === 0) {
			wmNotify({
				type: 'danger',
				message: 'You must select at least one asset to attach.'
			});
			return;
		}

		$.ajax({
			context: this,
			url: '/filemanager/get_asset_list',
			data: assets,
			type: 'post',
			dataType: 'json'
		}).success(function (data) {
			if (data.success) {
				if (typeof data.assets !== 'undefined' && data.assets.length > 0) {
					this.addSelectedAssets(data);
				} else {
					wmNotify({
						type: 'danger',
						message: 'An error occured and the selected assets could not be attached.'
					});
				}
			} else {
				_.each(data.errors, function (theMessage) {
					wmNotify({
						message: theMessage,
						type: 'danger'
					});
				});
			}
		});
	},

	addSelectedAssets: function(data) {
		if (!_.isUndefined(data.assets) && data.assets.length > 0) {
			var $documentsList = $('#assignment-documents').find('.documents');
			_.each(data.assets, function(item) {
				var filename = _.isUndefined(item.name) ? '' : item.name;
				var document = {
					id: item.id,
					uuid: item.uuid,
					description: item.description,
					name: filename,
					mime_type: item.mime_type,
					is_upload: false,
					visibility_type: this.defaultVisibilityType
				};
				var $generatedDocument = $(this.getGeneratedDocumentTemplate(document));

				this.setupTextarea($generatedDocument);
				this.setupVisibilityDropdown($generatedDocument, this.defaultVisibilityType);

				$generatedDocument.appendTo($documentsList);
				$documentsList.children().last().find('.asset-description').trigger('autoresize');
			}, this);
		}
		this.documentsModal.destroy();
	},

	getGeneratedDocumentTemplate: function (documentModel) {
		var filename = _.isUndefined(documentModel.name) ? '' : documentModel.name;
		var extension = filename.split('.').pop();
		if (extension === filename) {
			// If the file does not have an extension, use "MISC" as the extension
			extension = 'MISC';
		}

		var visibilityType = _.isUndefined(documentModel.visibility_type) ? this.defaultVisibilityType : documentModel.visibility_type;
		var visibilityDescription = this.visibilitySettings[visibilityType];

		var index = documentModel.index;
		if (_.isUndefined(index)) {
			// qq.FileUploader maintains a uploaded files list
			// The length of that list is used to track file upload ids
			// Since we're using these ids to organize form input indices (ex. <input name="attachment[index].name" value="foo.jpg">),
			// we need to to add to this list whenever we add uploads from other sources (ex. File Manager)
			var fileObj = { data: [''], name: documentModel.filename, type: documentModel.mime_type };
			index = this.attachmentUploader._handler._files.push(fileObj) - 1;
		}

		this.extensionSwitch = function (extension) {
			var xCoordinate;
			if (!_.isUndefined(extension)) {
				if (_.size(extension) === 3) {
					if (extension === "zip") {
						xCoordinate='x="4.5"'
					} else if (extension === "gif") {
						xCoordinate='x="4"'
					} else if (extension === "txt") {
						xCoordinate='x="3.4"'
					} else if (extension === "png" || extension === "bmp") {
						xCoordinate='x="2.7"'
					} else if (extension === "pdf" || extension === "xls" || extension === "f4v" || extension === "f4a" || extension === "jpg") {
						xCoordinate='x="3"'
					} else if (extension === "mov") {
						xCoordinate='x="1.5"'
					} else if (extension === "mp4" || extension === "m4v" || extension === "m4a" || extension === "mp3") {
						xCoordinate='x="2.5"'
					} else if (extension === "flv") {
						xCoordinate='x="3.5"'
					} else {
						xCoordinate='x="2.3"'
					}
					return xCoordinate + ' y="17.5" font-size="7pt"';
				} else {
					if (extension === "xlsx" || extension === "jpeg") {
						xCoordinate='x="3.5"'
					} else if (extension === "docx") {
						xCoordinate='x="2"'
					} else if (extension === "tiff") {
						xCoordinate='x="5"'
					} else {
						xCoordinate='x="3"'
					}
					return xCoordinate + 'y="17" font-size="5pt"'
				}
			}
		};

		var asset = {
			index: index,
			id: documentModel.id,
			uuid: documentModel.uuid,
			description: documentModel.description,
			name: filename,
			mimeType: documentModel.mime_type,
			containsImageMimeType: _.contains(['image/jpeg', 'image/pjpeg', 'image/jpg', 'image/tiff', 'image/png'], documentModel.mime_type),
			extension: extension,
			extensionSize: this.extensionSwitch(extension),
			upperExtension: extension.toUpperCase(),
			isUpload: documentModel.is_upload,
			visibilityCode: visibilityType,
			visibilityTypeDescription: visibilityDescription,
			createdOnDate: documentModel.createdOnDate
		};

		return this.uploadedAssetTemplate({
			isAdminOrActiveResource: this.isAdminOrActiveResource,
			asset: asset,
			visibilitySettings: [
				{'level':0, 'code':'internal', 'icon-code': 'internal', 'description':'Internal Only'},
				{'level':1, 'code':'assigned_worker', 'icon-code': 'assigned-worker','description':'Assigned Worker Only'},
				{'level':2, 'code':'public', 'icon-code': 'public', 'description':'Assigned and Potential Workers'}
			]
		});
	},

	getElementByQQFileId: function (id) {
		var document = this.$documentsList[0].firstChild;

		while (document){
			if (document.qqFileId === id) {
				return document;
			}
			document = document.nextSibling;
		}
	},

	setVisibilitySelection: function(e) {
		var dataSource = $(e.target).closest('a'),
			visibilityType = dataSource.data('visibility-code'),
			visibilityDescription = dataSource.data('visibility-description');

		var $assetVisibilityDropdownParent = $(e.target).parents('.asset-visibility.dropdown'),
			$assetVisibilityToggle = $assetVisibilityDropdownParent.find('.toggle-visibility'),
			$assetVisibilityDropdown = $assetVisibilityDropdownParent.find('.dropdown-menu'),
			$visibilitySelection = $assetVisibilityDropdownParent.find('.visibility-selection');

		$assetVisibilityToggle.children().hide();
		$assetVisibilityToggle.find('.' + visibilityType).show();
		$assetVisibilityDropdownParent.attr('aria-label', visibilityDescription);
		$visibilitySelection.val(visibilityType);

		$assetVisibilityDropdown.find('.checkmark').hide();
		$assetVisibilityDropdown.find('[data-visibility-code=' + visibilityType + ']').find('.checkmark').show();
	},

	addDocument: function(documentModel) {

		var $generatedDocument = $(this.getGeneratedDocumentTemplate(documentModel));
		this.setupTextarea($generatedDocument);
		this.setupVisibilityDropdown($generatedDocument, documentModel.visibility_type);
		this.$documentsList.append($generatedDocument);
		this.$documentsList.children().last().find('.asset-description').trigger('autoresize');
	},

	setupVisibilityDropdown: function ($generatedDocument, visibilityCode) {
		if (!($generatedDocument instanceof $)) {
			$generatedDocument = $($generatedDocument);
		}

		var fakeEvent = {};
		var visibilityType = _.isUndefined(visibilityCode) ? this.defaultVisibilityType : visibilityCode;
		var visibilityDescription = this.visibilitySettings[visibilityType];
		fakeEvent.target = $generatedDocument.find('span:contains("' + visibilityDescription + '")');

		this.setVisibilitySelection(fakeEvent);
	},

	setupTextarea: function ($generatedDocument) {
		$generatedDocument.find('.asset-description').autoresizeTextarea();
	}
};
