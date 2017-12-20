'use strict';

import $ from 'jquery';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import getCSRFToken from '../funcs/getCSRFToken';
import qq from '../funcs/fileUploader';
import 'jquery-form/jquery.form';

export default function () {
	var uploader = new qq.FileUploader({
			element: document.getElementById('file-uploader'),
			action: '/profile-edit/photoupload',
			allowedExtensions: ['jpg', 'jpeg', 'gif', 'png', 'bmp'],
			CSRFToken: getCSRFToken(),
			sizeLimit: 2 * 1024 * 1024, // 2MB
			multiple: false,
			template: $('#qq-uploader-tmpl').html(),
			onSubmit: function (){
			$('#photo_upload_messages').hide();
		},
		onComplete: function(id, fileName, json){
			$(uploader._getItemByFileId(id)).remove();
			if (json.successful) {
				if (json.data.assetUri) {
					$('.avatar_thumbnail').attr('src', json.data.assetUri).parent().find('div').show();
				}

				// Check to see if the add to file manager checkbox was ticked. If so, then show model to add name and description.
				if ($('#add_to_filemanager').prop('checked')) {
					$('#asset_id').val(json.data.assetId);
					wmModal({
						autorun: true,
						title: 'Add file to file manager',
						destroyOnClose: true,
						content: $('#addto_filemanager_form_container').html()
					});
				}
				else {
					window.location = '/profile-edit/photocrop';
				}
			} else {
				var tmp_str = '<ul>';
				for (var i in json.errors) {
					tmp_str+= '<li>'+json.errors[i]+'<\/li>';
				}
				tmp_str+= '<\/ul>';
				$('#photo_upload_messages div').html(tmp_str);
				$('#photo_upload_messages').addClass('error').show();
			}
		},
		showMessage: function (message) {
			wmNotify({
				message: message,
				type: 'danger'
			});
		}
	});

	$('#addto_filemanager_form_submit').on('click', function () {
		return $(this).closest('form').trigger('submit');
	});

	$('#addto_filemanager_form').ajaxForm({
		dataType: 'json',
		success: function (json) {
			$('#addto_filemanager_form a.disabled').removeClass('disabled');
			if (json && json.successful != true) {
				var tmp_str = "There was an error adding this file to the filemanager.\n";
				for (var i in json.errors) {
					tmp_str+= json.errors[i]+"\n";
				}
				alert(tmp_str);
			}

			window.location = '/profile-edit/photocrop';
		}
	});
}
