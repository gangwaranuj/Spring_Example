'use strict';

import $ from 'jquery';
import wmSelect from '../funcs/wmSelect';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import getCSRFToken from '../funcs/getCSRFToken';
import CreatePrivateGroupModal from '../funcs/templates/private_group_form.hbs';
import '../dependencies/maxlength.min';
import qq from '../funcs/fileUploader';
import 'jquery-form/jquery.form';

export default function (options) {
	let uploaded_logo = false,
		uploaded_overview = false,
		uploader,
		createGroupModal,
		uploader_companylogo,
		allowedExtensions = ['m4a', 'xls', 'xlsx', 'jpeg', 'xlm', 'pdf', 'xl', 'docm', 'mp4', 'mp3', 'jpe', 'txt', 'xla','xlc', 'xlsm', 'mp2', 'f4a', 'mpga', 'qt', 'f4v', 'zip', 'text', 'form', 'log', 'bmp', 'mov', 'jpg', 'png','m4v', 'flv', 'mpg4', 'doc', 'docx', 'word', 'csv', 'xlt', 'gif'];

	$('#show-options').on('click', () => {
		if ($('#options').is(':visible')) {
			$('#options').hide();
			$('#show-options').html('View Advanced Options &#9660;');
		}
		else {
			$('#options').show();
			$('#show-options').html('Hide Advanced Options &#9650;');
		}
	});

	$('#show-bulk').on('click', () => {
		$('#send-bulk').show();
		$('#send-individual').hide();
		$('input[name=first_name]').prop('required', false);
		$('input[name=last_name]').prop('required', false);
		$('input[name=email]').prop('required', false);
	});

	$('#show-individual').on('click', () => {
		$('#send-individual').show();
		$('#send-bulk').hide();
		$('input[name=first_name]').prop('required', true);
		$('input[name=last_name]').prop('required', true);
		$('input[name=email]').prop('required', true);
	});

	$('#recruitingCampaigns').on('change', () => {
		if ($('#recruitingCampaigns').val()) {
			$('#options').hide();
			$('#show-options').hide();
		} else {
			if ($('#show-options').html().includes('Hide')) {
				$('#options').show();
			}
			$('#show-options').show();
		}
	});

	const company_logo = $('#current_company_logo');
	const upload_logo = $('#onetime_logo_preview');

	$('input[name=logo]').on('change', (e) => {
		var val = $(this).val();
		if (val == 'company') {
			selectCompanyLogo();
			upload_logo.hide();
		} else if (val == 'upload') {
			if ($(e.target).is('input[type=radio]'))
				$('.qq-upload-button input').trigger('click');
			company_logo.hide();
		} else {
			company_logo.hide();
			upload_logo.hide();
		}
	});

	$('#remove_upload_logo_preview').on('click', () => {
		$('img', upload_logo).attr('src', '');
		$('#logo_uuid').val('');
		upload_logo.hide();
		$('input[name=logo][value=none]').prop('checked', true);
	});

	(function uploadContactsCSV () {
		new qq.FileUploader({
			element: document.getElementById('contact-uploader'),
			action: '/upload/uploadqq',
			allowedExtensions: ["csv", ""],
			CSRFToken: getCSRFToken(),
			sizeLimit: 10 * 1024 * 1024, // 10MB
			multiple: false,
			template: $('#qq-uploader-contacts-tmpl').html(),
			onSubmit: function () {
				$('#dynamic_message').hide();
			},
			onComplete: function (id, fileName, data){
				$('#uploadCsv').val(data.uuid);
			},
			showMessage: function(message) {
				wmNotify({
					type: 'danger',
					message: message
				});
			}
		});
	})();

	uploader = new qq.FileUploader({
		element: document.getElementById('file-uploader'),
		action: '/upload/uploadqq',
		allowedExtensions,
		CSRFToken: getCSRFToken(),
		sizeLimit: 2 * 1024 * 1024, // 2MB
		multiple: false,
		template: $('#qq-uploader-tmpl').html(),
		onSubmit: function () {
			$('#dynamic_message').hide();
		},
		onComplete: function (id, fileName, data) {
			$(uploader._getItemByFileId(id)).remove();
			if (data.successful) {
				$('img', upload_logo).attr('src', '/upload/download/' + data.uuid);
				$('#logo_uuid').val(data.uuid);
				upload_logo.show();
				$('input[name=logo][value=upload]').prop('checked', true);
			}
		},
		showMessage: function (message) {
			wmNotify({
				message: message,
				type: 'danger'
			});
		}
	});

	let render_preview = function () {
		var m = $('#custom_message').val();
		m = $('<div>').append(m).text();
		m = m.replace(new RegExp("\n", "g"), '<br/>');

		$('#preview').html($('#preview_template').tmpl({
			'message': m,
			'first_name': $('#first_name').val() || 'Friend',
			'template': {
				companyName: options.companyName
			}
		}));

		if (!m) {
			$('#preview').find('hr:first').remove();
			$('#preview').find('h1:eq(1)').remove();
		}
	};

	$('#toggle_customize_invitation').on('click', function () {
		$('#customize_invitation')[$(this).is(':checked') ? 'show' : 'hide']();
	});

	render_preview();
	$('#custom_message').maxlength({feedback: '.charsLeft'}).keyup(render_preview);
	$('#first_name').keyup(render_preview);

	$('.preview-action').on('click',  () => {
		wmModal({
			autorun: true,
			title: 'Preview',
			destroyOnClose: true,
			content: $('#preview-container').html()
		});
	});

	let $selectizeGroups = wmSelect({ selector: '#user_groups_autosuggest' }, {
		create: false,
		valueField: 'name',
		labelField: 'value',
		searchField: 'value',
		maxItems: null,
		plugins: ['remove_button'],
		load: function (query, callback) {
			if (!query.length) {
				return callback();
			}

			$.ajax({
				url: '/invitations/suggest_groups?term=' + encodeURIComponent(query),
				type: 'GET',
				error: callback,
				success: function (res) {
					callback(res.slice(0, 10));
				}
			});
		},
		onItemAdd: function (value) {
			$('#sendForm').prepend('<input type="hidden" style="display:none;" value="' + value + '" name="user_group_ids">');
		},
		onItemRemove: function (value) {
			$('#sendForm').find('input').filter(function (index, element) {
				return element.name === 'user_group[id][]' && element.value === value;
			}).remove();
		}
	});

	$('#add-group-outlet').on('click', () => {
		createGroupModal = wmModal({
			autorun: true,
			title: 'Create Private Group',
			destroyOnClose: true,
			content: CreatePrivateGroupModal(),
			controls: [
				{
					text: 'Cancel',
					close: true,
					classList: ''
				},
				{
					text: 'Create Group',
					primary: true,
					classList: '.submit-create'
				}
			]
		});

		$('.-primary').on('click', () => {
			$('#create_quickform').ajaxSubmit({
				dataType: 'json',
				success: (data) => {
					createGroupModal.destroy();
					wmNotify({
						message: 'Success! Your group has been created and is now selected'
					});
					$selectizeGroups[0].selectize.addOption({
						name: data.id,
						value: data.name
					});
					$selectizeGroups[0].selectize.addItem(data.id);
				},
				error: () => {
					wmNotify({
						type: 'danger',
						message: 'There was an error creating the group. Please try again.'
					})
				}
			});
		});
	});

	$('#company_overview').on('click', function () {
		if (options.isEmptyCompanyOverview) {
			if ($('#company_overview').prop('checked') && uploaded_overview === false) {
				wmModal({
					autorun: true,
					title: 'Company Overview',
					destroyOnClose: true,
					content: $('#popup_overview').html()
				});
				$('#form_overview').ajaxForm({
					dataType: 'json',
					success: function (responseText) {
						$('#form_overview a.disabled').removeClass('disabled');
						if (responseText.successful) {
							uploaded_overview = true;
							$('#company_overview_add').val($('#overview_pop').val());
						}
						else {
							responseText.errors.forEach((message) => wmNotify({ message, type: 'danger' }));
						}
					}
				});
			}
		} else {
			if ($('#company_overview').is(':checked')) {
				$('#company_overview_add').prop('disabled', 'disabled').val($('#current_company_overview').html());
			} else {
				$('#company_overview_add').prop('disabled', false).val('');
			}
		}
	});

	function selectCompanyLogo() {
		if (options.isEmptyCompanyAvatar) {
			if (uploaded_logo == false) {
				const modal = wmModal({
					autorun: true,
					title: 'Company Logo',
					destroyOnClose: true,
					content: $('#popup_upload_photo').html()
				});

				uploader_companylogo = new qq.FileUploader({
					element: document.getElementById('file-uploader-companylogo'),
					action: '/account/logoupload',
					allowedExtensions: allowedExtensions,
					CSRFToken: getCSRFToken(),
					sizeLimit: 2 * 1024 * 1024, // 2MB
					multiple: false,
					template: $('#qq-uploader-tmpl').html(),
					onComplete: function (id, fileName, responseJSON) {
						$(uploader_companylogo._getItemByFileId(id)).remove();
						if (responseJSON.successful) {
							uploaded_logo = true;
							modal.destroy();
						}
						else {
							responseJSON.errors.forEach((message) => wmNotify({ message, type: 'danger' }));
						}
					},
					showMessage: function (message) {
						wmNotify({
							message: message,
							type: 'danger'
						});
					}
				});
			}
		} else {
			company_logo.show();
		}
	}
};
