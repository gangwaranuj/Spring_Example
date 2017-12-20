'use strict';

import $ from 'jquery';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import '../dependencies/maxlength.min';
import qq from '../funcs/fileUploader';
import '../dependencies/jquery.tmpl';


export default function () {
	var uploader;

	$('#overview').maxlength({
		feedback: '.charsLeft'
	});

	$('#resume_list').on('click', '[data-action="remove-resume"]', function (e) {
		e.preventDefault();
		var messages = $('#resume-messages');
		messages.hide();
		var assetId = $(e.target).attr('data-asset-id');
		$.ajax({
			type: 'DELETE',
			url: '/profile-edit/resume/' + assetId,
			dataType: 'json',
			success: function(data) {
				if (data.successful) {
					$('#resume_' + assetId).remove();
				} else {
					messages.addClass('error').removeClass('success').show();
					messages.html('There was a problem deleting your resume.');
				}
			}});
	});

	uploader = new qq.FileUploader({
		element: document.getElementById('file-uploader'),
		action: '/profile-edit/resumeupload',
		allowedExtensions: ['tsv','pdf','docm','doc','docx','csv','rtf','txt','rtx'],
		CSRFToken: getCSRFToken(),
		sizeLimit: 10 * 1024 * 1024, // 10MB
		multiple: true,
		template: $('#qq-uploader-tmpl').html(),
		onSubmit: function() {
			$('#resume-messages').hide();
		},
		onComplete: function(id, fileName, responseJSON) {

			$(uploader._getItemByFileId(id)).remove();
			if (responseJSON.successful) {
				$('#resume_list_item').tmpl({id: responseJSON.id, filename: responseJSON.filename, uuid: responseJSON.uuid}).appendTo($('#resume_list'));
			} else {
				var tmp_str = '<ul>';
				for (var i in responseJSON.errors) {
					tmp_str += '<li>' + responseJSON.errors[i] + '<\/li>';
				}
				tmp_str += '<\/ul>';
				var messages = $('#resume-messages');
				messages.addClass('alert alert-error').removeClass('success').show();
				messages.html(tmp_str);
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
