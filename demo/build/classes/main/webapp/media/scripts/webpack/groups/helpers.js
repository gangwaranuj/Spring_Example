'use strict';

import Application from '../core';
import $ from 'jquery';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import qq from '../funcs/fileUploader';

export default () => {
	var uploaders = [];
	var groupId = $('input[name="groupId"]').val();
	var userId = $('input[name="userId"]').val();

	$('.file-remover').on('click', function (e) {
		$.ajax({
			type: 'POST',
			url: '/groups/manage/remove_reference/' + groupId,
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify({
				groupId: groupId,
				userId: userId,
				requiredDocumentId: $(e.target).data('requiredid'),
				referenceDocumentId: $(e.target).data('referencedid')
			}),
			success: function () {
				location.reload();
			}
		});
	});

	var registerUploadedDocument = function (jsonData, requiredDocumentId, expirationDateStr) {
		$.ajax({
			type: 'POST',
			url: '/groups/manage/register_uploaded_document/' + groupId,
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify({
				groupId,
				userId,
				uploadUuid: jsonData.uuid,
				mimeType: jsonData.mime_type,
				name: jsonData.file_name,
				requiredDocumentId,
				expirationDateStr
			}),
			success: function ({ successful }) {
				if (successful) {
					var uploadersInProgress = uploaders.reduce((memo, { _filesInProgress }) => memo + _filesInProgress, 0);
					if (!uploadersInProgress) {
						location.reload(true);
					}
				}
			},
			error: console.log
		});
	};

	var setupCalendarHandler = function (divId) {
		var elem = $('#' + divId);
		if (elem.data('expiration-required')) {
			$($('#document_datepicker').html()).appendTo(elem);
		}
	};

	var setupUploader = function (divId) {
		var elem = $('#' + divId);
		return new qq.FileUploader({
			element: document.getElementById(divId),
			action: '/upload/uploadqq',
			allowedExtensions: ['m4a','xls','xlsx','jpeg','pdf','docm','mp4','mp3','txt','tsv','xlsm','f4a','7z','f4v','office','zip','gz','rtf','mov','bmp','jpg','png','flv','m4v','doc','tar','docx','csv','gif','rtx'],
			sizeLimit: 150 * 1024 * 1024, // 150MB
			multiple: false,
			CSRFToken: Application.CSRFToken,
			template: $('#qq-uploader-inline-tmpl').html(),
			onSubmit: function (id, fileName) {
				// get rid of dynamically uploaded info element - only ever want to show one
				if (elem.find('.qq-upload-success')) { elem.find('.qq-upload-success').remove(); }
				if (elem.find('.qq-upload-fail')) { elem.find('.qq-upload-fail').remove(); }

				if (elem.children('input').length > 0 && elem.children('input[type=hidden]').val() === '') {
					wmModal({
						autorun: true,
						title: 'Missing Expiration Date',
						destroyOnClose: true,
						content: 'An expiration date is required for this document.'
					});
					return false;
				}
			},
			onComplete: function (id, fileName, responseJSON) {
				if (responseJSON.successful) {
					registerUploadedDocument(
						responseJSON,
						elem.data('assetid'),
						(elem.data('expiration-required') ? elem.children('input[type=hidden]').val() : null)
					);
					$('#group_apply_form').trigger('submit');
				} else {
					var tmp_str = '<ul>';
					for (var i in responseJSON.errors) {
						tmp_str += '<li>' + responseJSON.errors[i] + '<\/li>';
					}
					tmp_str += '<\/ul>';
					$('#dynamic_messages div').html(tmp_str);
					$('#dynamic_messages').addClass('error').show();
				}
			},
			showMessage: function (message) {
				wmNotify({
					message: message,
					type: 'danger'
				});
			}
		});
	};

	$('.file-uploader').each(function (index, elem) {
		uploaders.push(setupUploader($(elem).attr('id')));
		setupCalendarHandler($(elem).attr('id'));
	});

	// TODO - Micah - This is some bullshit to make datepicker user the proper format
	// for display and POST.
	$('.datepicker').each(function (index, elem) {
		var jqElem = $(elem);
		jqElem.datepicker({
			dateFormat: 'mm/dd/yy',
			altFormat: 'yy-mm-dd'
		});
		var realId = jqElem.attr('id') + '_real';
		var realElem = jqElem.siblings('input');
		realElem.attr('id', realId);
		jqElem.datepicker('option', 'altField', '#' + realId);
	});

	$('input[name="group_shared"]').on('click', function (e) {
		var $cb = $(e.currentTarget),
			groupId = $cb.data('group-id');

		if ($cb.prop('checked')) {
			$.post('/groups/sharing/' + groupId + '/share');
			$('.sharing-icon').show();
		} else {
			$.post('/groups/sharing/' + groupId + '/revoke');
			$('.sharing-icon').hide();
		}
	});
};
