'use strict';

import $ from 'jquery';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';
import wmSelect from '../funcs/wmSelect';
import wmModal from '../funcs/wmModal';
import CreatePrivateGroupModal from '../funcs/templates/private_group_form.hbs';
import CreateOverviewModal from './templates/overview.hbs';
import CompanyLogoForm from './templates/company_logo.hbs';
import 'jquery-form/jquery.form';
import getCSRFToken from '../funcs/getCSRFToken';
import qq from '../funcs/fileUploader';

export default (options) => {
	let libraryLogo = $('#library_asset_preview'),
		uploadLogo = $('#upload_preview'),
		companyLogo = $('#current_companyLogo'),
		uploadedLogo = false,
		uploadedOverview = false,
		uploader,
		createGroupModal,
		overviewModal,
		companyLogoModal;
	let selected = _.find($('.inputs-list.logos').find('input[type="radio"]'), function (input) {
		return $(input).attr('checked')
	});
	let $selected = $(selected);
	if ($selected.attr('value') === 'company') {
		$selected.parent().find('div img').show();
	} else if ($selected.attr('value') === 'upload') {
		$selected.parents('li').children('div').show();
	}
	var removeLibraryLogo = function () {
		$('img', libraryLogo).attr('src', '');
		$('#assetId').val('');
		libraryLogo.hide();
		$('input[name=assetType][value=none]').prop('checked', true);
	};
	var removeUploadLogo = function () {
		$('img', uploadLogo).attr('src', '');
		$('#uploadUuid').val('');
		uploadLogo.hide();
		$('input[name=assetType][value=none]').prop('checked', true);
	};

	$('input[name=assetType]').on('change', function (e) {
		var val = $(this).val();
		if (val === 'company') {
			selectCompanyLogo();
			libraryLogo.hide();
			uploadLogo.hide();
		} else if (val === 'upload') {
			if ($(e.target).is('input[type=radio]')) {
				$('label .qq-upload-button input').trigger('click');
			}
			companyLogo.hide();
			libraryLogo.hide();
		} else {
			companyLogo.hide();
			libraryLogo.hide();
			uploadLogo.hide();
		}
	});

	$('#remove_library_asset_preview').click(removeLibraryLogo);
	$('#remove_upload_preview').click(removeUploadLogo);
	uploader = new qq.FileUploader({
		element: document.getElementById('file-uploader'),
		action: '/upload/uploadqq',
		allowedExtensions: $('#image-allowed-extensions').html().trim().split(','),
		CSRFToken: getCSRFToken(),
		sizeLimit: 2 * 1024 * 1024, // 2MB
		multiple: false,
		template: $('#qq-uploader-tmpl').html(),
		onComplete: function (id, fileName, data) {
			$(uploader._getItemByFileId(id)).remove();
			if (data.successful) {
				$('input[name=assetType][value=upload]').prop('checked', true);
				$('img', uploadLogo).attr('src', '/upload/download/' + data.uuid);
				$('#uploadUuid').val(data.uuid);
				uploadLogo.show();
				companyLogo.hide();
				libraryLogo.hide();
			}
		},
		showMessage: function (message) {
			wmNotify({
				message: message,
				type: 'danger'
			});
		}
	});

	const $selectizeGroups = wmSelect({ selector: '#user_groups_autosuggest' }, {
		create: false,
		valueField: 'name',
		labelField: 'value',
		searchField: 'value',
		maxItems: 1,
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
			$('#recruitingForm').find('input').filter(function (index, element) {
				return element.name === 'user_group_ids';
			}).remove();
			$('#recruitingForm').prepend('<input type="hidden" style="display:none;" value="' + value + '" name="user_group_ids">');
		},
		onItemRemove: function (value) {
			$('#recruitingForm').find('input').filter(function (index, element) {
				return element.name === 'user_group[id][]' && element.value === value;
			}).remove();
		}
	});

	if (options.groupId !== 0) {
		$selectizeGroups[0].selectize.addOption({
			name: options.groupId,
			value: options.groupName
		});
		$selectizeGroups[0].selectize.addItem(options.groupId);
	}

	$('#add-group-outlet').on('click', () => {
		createGroupModal = wmModal({
			autorun: true,
			title: 'Create Private Talent Pool',
			destroyOnClose: true,
			content: CreatePrivateGroupModal(),
			controls: [
				{
					text: 'Cancel',
					close: true,
					classList: ''
				},
				{
					text: 'Create',
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
						message: 'Success! Your talent pool has been created and is now selected'
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
						message: 'There was an error creating the talent pool. Please try again.'
					})
				}
			});
		});
	});

	function selectCompanyLogo() {
		if (options.emptyCompanyAvatar) {
			if (uploadedLogo === false) {
				companyLogoModal = wmModal({
					autorun: true,
					title: 'Upload Company Logo',
					destroyOnClose: true,
					content: CompanyLogoForm()
				});
				$('input[name=assetType][value=none]').prop('checked', true);
			}
		} else {
			companyLogo.show();
		}
	}

	$('input[name=useCompanyOverview]').on('click', function () {
		if (options.emptyCompanyOverview) {
			if ($(this).is(':checked') && uploadedOverview === false) {
				overviewModal = wmModal({
					autorun: true,
					title: 'Create Company Overview',
					destroyOnClose: true,
					content: CreateOverviewModal(),
					controls: [
						{
							text: 'Cancel',
							close: true,
							classList: ''
						},
						{
							text: 'Save Overview',
							primary: true,
							classList: ''
						}
					]
				});

				$('.-primary').on('click', () => {
					$('#form_overview').ajaxSubmit({
						dataType: 'json',
						success: function (response) {
							$('#form_overview a.disabled').removeClass('disabled');
							if (response.successful === true) {
								uploadedOverview = true;
								$('#customCompanyOverview').val($('#overview_pop').val());
								overviewModal.destroy();
							} else {
								_.each(response.errors, function (theMessage) {
									wmNotify({
										message: theMessage,
										type: 'danger'
									});
								});
							}
						}
					});
				});
			}
		} else {
			selectCompanyOverview()
		}

		if (options.emptyCustomCompanyOverview) {
			selectCompanyOverview()
		}

		function selectCompanyOverview() {
			if ($('input[name=useCompanyOverview]').is(':checked')) {
				$('#customCompanyOverview').prop('disabled', 'disabled').val($('#company_overview').html());
			} else {
				$('#customCompanyOverview').prop('disabled', false).val('');
			}
		}
	});
};
