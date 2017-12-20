'use strict';

import React, { Component } from 'react';
import $ from 'jquery';
import _ from 'underscore';
import 'datatables.net';
import { addDocument, removeDocument } from '../actions/documents';
import { WMRaisedButton } from '@workmarket/front-end-components';
import qq from '../../funcs/fileUploader';
import Application from '../../core';
import wmSelect from '../../funcs/wmSelect';
import wmModal from '../../funcs/wmModal';
import wmNotify from '../../funcs/wmNotify';

// templates
import uploaderTemplate from '../../assignments/templates/documents/uploaderTemplate.hbs';
import uploadInprogressTemplate from '../templates/nonRequiredDocumentUploadPending.hbs';
import nonRequiredDocumentTemplate from '../templates/non_required_document.hbs';
import fileManagerTableTemplate from '../../assignments/templates/creation/file_manager_table.hbs';
import checkboxCellTemplate from '../../assignments/templates/creation/checkbox_cell.hbs';

export default class DocumentsComponent extends Component {
	componentDidMount () {
		this.$attachmentMessagesParent = $('.document-uploader--attachment_messages');
		this.$attachmentMessages = this.$attachmentMessagesParent.find('div');
		this.$documentsList = $('.document-uploader--document-list');
		this.defaultVisibilityType = 'assigned_worker';
		this.visibilitySettings = [
			{ id: 'assigned_worker', name: 'Assigned Worker Only' },
			{ id: 'public', name: 'Assigned and Potential Workers'},
			{ id: 'internal', name: 'Internal Only' }
		];
		this.uploadedAssetTemplate = nonRequiredDocumentTemplate;

		this.attachmentUploader = new qq.FileUploader({
			element: $('.document-uploader--uploader-container')[0],
			action: '/upload/uploadqq',
			CSRFToken: Application.CSRFToken,
			sizeLimit: 150 * 1024 * 1024, // 150MB
			multiple: true,
			template: uploaderTemplate(),
			fileTemplate: uploadInprogressTemplate().trim(),
			listElement: this.$documentsList[0],
			onProgress: (id, fileName, loaded, total) => {
				let $upload = $(this.getElementByQQFileId(id)),
					progress = Math.round(loaded / total * 100) + '%',
					$progressBar = $('.document-uploader--progress-bar-inner', $upload);

				$progressBar.css('width', progress);
			},
			onComplete: (id, filename, json) => {
				let $upload = $(this.getElementByQQFileId(id));

				if (json.successful) {
					const document = {
						index: id,
						id: json.id,
						uuid: json.uuid,
						description: json.description,
						name: filename,
						mime_type: json.mime_type,
						uploaded: true,
						visibilityType: this.defaultVisibilityType
					};

					$upload.remove();

					this.props.addDocument(document);

				} else {
					$upload.remove();
					this.$attachmentMessages.html(json.errors[0]);
					this.$attachmentMessagesParent.addClass('error').show();
				}
			}
		});
	}

	componentDidUpdate(prevProps) {
		if (prevProps.documents.length !== this.props.documents.length) {
			const assets = this.props.documents;
			assets.forEach(asset => $(`[data-uuid="${asset.uuid}"]`).remove());
			const docObj = {
				assets
			};
			this.addSelectedAssets(docObj, true);
		}
	}

	initFileManager = () => {
		$('.document-uploader--file-manager-container').html(fileManagerTableTemplate());
		let meta = null;

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
						return checkboxCellTemplate({
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

		$('#attach_assets_to_assignment').on('click', (event) => {
			event.preventDefault();

			let list = $('#attach_assets_to_assignment_form').serializeArray();
			this.processSelectedAssetsToAdd(list);
		});
	}

	getElementByQQFileId (id) {
		let document = this.$documentsList[0].firstChild;

		while (document){
			if (document.qqFileId === id) {
				break;
			}
			document = document.nextSibling;
		}

		return document;
	}

	processSelectedAssetsToAdd (assets) {
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
	}

	addSelectedAssets (data, silent) {
		if (!_.isUndefined(data.assets) && data.assets.length > 0) {
			_.each(data.assets, (item) => {
				const filename = _.isUndefined(item.name) ? '' : item.name;
				const document = {
					id: item.id,
					uuid: item.uuid,
					description: item.description,
					name: filename,
					mime_type: item.mime_type,
					uploaded: item.uploaded || false,
					visibilityType: item.visibilityType
				};

				const $generatedDocument = $(this.getGeneratedDocumentTemplate(document));

				$generatedDocument.find(`[name="document-${document.uuid}-description"]`).on('change', (e) => {
					this.props.updateDocument({
						uuid: document.uuid,
						description: e.target.value
					});
				});

				$generatedDocument.find('.document-uploader--option.-remove').on('click', (e) => {
					this.props.removeDocument(document.uuid);
					$generatedDocument.remove();
				});

				$generatedDocument.appendTo(this.$documentsList);

				this.setupVisibilityDropdown($generatedDocument, document.visibilityType);

				if (!silent) {
					this.props.addDocument(document);
				}
			});
		}
	}

	getGeneratedDocumentTemplate (documentModel) {
		const filename = _.isUndefined(documentModel.name) ? '' : documentModel.name;
		const visibilityType = _.isUndefined(documentModel.visibilityType) ? this.defaultVisibilityType : documentModel.visibilityType;
		const visibilityDescription = this.visibilitySettings.find(
			function (type) {
				return type.id == visibilityType }
		).name;

		let index = documentModel.index,
			extension = filename.split('.').pop();

		if (extension === filename) {
			// If the file does not have an extension, use "MISC" as the extension
			extension = 'MISC';
		}

		if (_.isUndefined(index)) {
			// qq.FileUploader maintains a uploaded files list
			// The length of that list is used to track file upload ids
			// Since we're using these ids to organize form input indices (ex. <input name="attachment[index].name" value="foo.jpg">),
			// we need to to add to this list whenever we add uploads from other sources (ex. File Manager)
			let fileObj = { data: [''], name: documentModel.filename, type: documentModel.mime_type };
			index = this.attachmentUploader._handler._files.push(fileObj) - 1;
		}

		this.extensionSwitch = function (extension) {
			let xCoordinate;
			if (!_.isUndefined(extension)) {
				if (_.size(extension) === 3) {
					if (extension === 'zip') {
						xCoordinate = 'x="4.5"';
					} else if (extension === 'gif') {
						xCoordinate = 'x="4"';
					} else if (extension === 'txt') {
						xCoordinate = 'x="3.4"';
					} else if (extension === 'png' || extension === 'bmp') {
						xCoordinate = 'x="2.7"';
					} else if (extension === 'pdf' || extension === 'xls' || extension === 'f4v' || extension === 'f4a' || extension === 'jpg') {
						xCoordinate = 'x="3"';
					} else if (extension === 'mov') {
						xCoordinate = 'x="1.5"';
					} else if (extension === 'mp4' || extension === 'm4v' || extension === 'm4a' || extension === 'mp3') {
						xCoordinate = 'x="2.5"';
					} else if (extension === "flv") {
						xCoordinate = 'x="3.5"';
					} else {
						xCoordinate = 'x="2.3"';
					}
					return xCoordinate + ' y="17.5" font-size="7pt"';
				} else {
					if (extension === 'xlsx' || extension === 'jpeg') {
						xCoordinate = 'x="3.5"';
					} else if (extension === 'docx') {
						xCoordinate = 'x="2"';
					} else if (extension === 'tiff') {
						xCoordinate = 'x="5"';
					} else {
						xCoordinate = 'x="3"';
					}
					return xCoordinate + 'y="17" font-size="5pt"';
				}
			}
		};

		const asset = {
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
			isUpload: documentModel.uploaded,
			visibilityCode: visibilityType,
			visibilityTypeDescription: visibilityDescription
		};

		return this.uploadedAssetTemplate({
			isAdminOrActiveResource: this.isAdminOrActiveResource,
			asset: asset
		});
	}

	setupVisibilityDropdown ($generatedDocument, visibilityCode) {
		if (!($generatedDocument instanceof $)) {
			$generatedDocument = $($generatedDocument);
		}

		const docUUID = $generatedDocument.attr('data-uuid');

		let documentRoot = `[data-uuid="${docUUID}"]`,
			visibilitySelector = '#document-visibility',
			visibilitySelect = wmSelect({ root: documentRoot, selector: visibilitySelector }, {
				options: this.visibilitySettings,
				items: [visibilityCode],
				valueField: 'id',
				labelField: 'name',
				openOnFocus: true,
				allowEmptyOption: false,
				onChange: (value) => {
					this.props.updateDocument({
						uuid: docUUID,
						visibilityType: value
					});
				}
			});
	}

	render () {
		return (
			<div>
				<div className="assignment-creation--document-upload">
					<div className="document-uploader--attachment-messages">
						<div />
					</div>
					<div className="document-uploader--uploader-container" />
					<WMRaisedButton
						className="assignment-creation--button"
						label="File Manager"
						onClick={ this.initFileManager }
						style={ { marginTop: '10px' } }
					/>
					<div className="document-uploader--file-manager-container" />
					<ul className="document-uploader--document-list" />
				</div>
			</div>
		);
	}
}


