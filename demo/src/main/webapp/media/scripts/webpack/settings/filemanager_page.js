import $ from 'jquery';
import _ from 'underscore';
import 'datatables.net';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import qq from '../funcs/fileUploader';

export default function () {
	let meta;

	const $documentsTableContainer = $('#documents_list');

	const $documentsTable = $documentsTableContainer.dataTable({
		sPaginationType: 'full_numbers',
		bLengthChange: false,
		bFilter: false,
		bStateSave: false,
		bProcessing: true,
		bServerSide: true,
		sAjaxSource: '/filemanager/available_documents',
		iDisplayLength: 50,
		aoColumnDefs: [
			{ bSortable: false, aTargets: [0, 1, 2, 3, 4, 5] },
			{
				mRender: (data, type, val, metaData) => {
					return `<a aria-label="Download" class="tooltipped tooltipped-n" href="/asset/download/${meta[metaData.row].uuid}"><i class="wm-icon-download icon-large muted"></i></a></span>`;
				},
				sClass: 'actions',
				aTargets: [3]
			},
			{
				mRender: (data, type, val, metaData) => {
					return `<a class="tooltipped tooltipped-n" aria-label="Edit" href="/filemanager/editasset?id=${meta[metaData.row].id}"><i class="wm-icon-edit icon-large muted"></i></a></span>`;
				},
				sClass: 'actions',
				aTargets: [4]
			},
			{
				mRender: (data, type, val, metaData) => {
					return `<a class="delete-file tooltipped tooltipped-n" aria-label="Delete" data-id="${meta[metaData.row].id}"><i class="wm-icon-trash icon-large muted"></i></a>`;
				},
				sClass: 'actions',
				aTargets: [5]
			}
		],
		fnServerData (sSource, aoData, fnCallback) {
			$.getJSON(sSource, aoData, (json) => {
				meta = json.aMeta;
				fnCallback(json);
			});
		}
	});

	$documentsTableContainer.on('click', '.delete-file', (event) => {
		if (confirm('Are you certain you want to delete this file?')) { // eslint-disable-line no-alert
			$.ajax({
				url: '/filemanager/deactivate',
				data: JSON.stringify({ id: $(event.currentTarget).data('id') }),
				dataType: 'json',
				contentType: 'application/json',
				type: 'POST',
				beforeSend (jqXHR) {
					jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
				}
			}).done((response) => {
				wmNotify({
					message: response.message
				});
			}).fail((response) => {
				wmNotify({
					message: response.message,
					type: 'danger'
				});
			});
		}
	});

	const uploader = new qq.FileUploader({
		element: document.getElementById('file-uploader'),
		action: '/upload/uploadqq',
		allowedExtensions: ['m4a', 'xls', 'xlsx', 'jpeg', 'pdf', 'docm', 'mp4', 'mp3', 'txt', 'tsv', 'xlsm', 'f4a', '7z', 'f4v', 'office', 'zip', 'gz', 'rtf', 'mov', 'bmp', 'jpg', 'png', 'flv', 'm4v', 'doc', 'tar', 'docx', 'csv', 'gif', 'rtx'],
		sizeLimit: 150 * 1024 * 1024, // 150MB
		multiple: false,
		CSRFToken: getCSRFToken(),
		template: $('#qq-uploader-tmpl').html(),
		onSubmit () {
			$('#dynamic_messages').hide();
		},
		onComplete (id, fileName, responseJSON) {
			$(uploader._getItemByFileId(id)).remove(); // eslint-disable-line no-underscore-dangle
			if (responseJSON.successful) {
				$('#upload_uuid').val(responseJSON.uuid);
				$('.qq-upload-list').html(`<li>${responseJSON.file_name}</li>`).show();
			} else {
				_.each(responseJSON.errors, (theMessage) => {
					wmNotify({
						message: theMessage,
						type: 'danger'
					});
				});
			}
		},
		showMessage (message) {
			wmNotify({
				message,
				type: 'danger'
			});
		}
	});

	$('#upload_file_button').on('click', () => {
		$('#upload_form_container').show();
	});

	function clearUploadForm () {
		$('#upload_form_container').hide();
		$('.qq-upload-list').html('').hide();
		$('#upload_form').trigger('reset');
	}

	function saveCompanyUpload () {
		// Get the serialized fields.
		const newproject = $('#upload_form').formSerialize();

		// Do the AJAX call to get the client locations
		$.ajax({
			url: '/filemanager/add',
			type: 'POST',
			data: ({ data: newproject }),
			dataType: 'json',
			context: this,
			beforeSend (jqXHR) {
				jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
			},
			success (data) {
				if (data.successful) {
					wmNotify({ message: 'Successfully added new file!' });
					clearUploadForm();
					$documentsTable.fnDraw();
				} else {
					_.each(data.errors, (theMessage) => {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}
		});
	}

	$('#cancel_upload_file_form').on('click', _.bind(clearUploadForm, this));

	// Submit the upload new file form.
	$('#submit_upload_file_form').on('click', _.bind(saveCompanyUpload, this));
}
