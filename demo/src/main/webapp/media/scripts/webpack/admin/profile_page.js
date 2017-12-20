'use strict';

import $ from 'jquery';
import _ from 'underscore';
import wmMaskInput from '../funcs/wmMaskInput';
import wmSelect from '../funcs/wmSelect';
import wmModal from '../funcs/wmModal';
import ProfileListView from './profile_listing_view';
import 'datatables.net';

export default (profileId) => {
	new ProfileListView({
		profileId: profileId
	});

	$(document).on('click', '.delete-action', function () {
		var url = '/admin/manage/profiles/deleterating?user_id=' + profileId;
		var path = url + '&id=' + $(this).data('id');
		if (confirm('Are you sure you want to delete this review?')) {
			window.location = path;
		}
		return false;
	});

	$(document).on('click', '.unflag-action', function () {
		var url = '/admin/manage/profiles/unflag?user_id=' + profileId;
		var path = url + '&id=' + $(this).data('id');
		if (confirm('Are you sure you want to remove the flag on this review?')) {
			window.location = path;
		}
		return false;
	});

	$('#ratings_list').dataTable({
		'sPaginationType': 'full_numbers',
		'bLengthChange': true,
		'bFilter': false,
		'bStateSave': true,
		'bProcessing': true,
		'bServerSide': true,
		'aoColumns': [
			{ 'bSortable': false },
			null,
			null,
			{ 'bSortable': false },
			{ 'bSortable': false }
		],
		'sAjaxSource': '/admin/manage/profiles/getflaggedratings',
		'fnServerData': function (sSource, aoData, fnCallback) {
			aoData.push({ name: 'id', value: profileId });
			$.getJSON(sSource, aoData, function (json) {
				if (json.aaData.length === 0) {
					$('#table_ratings').hide();
					$('.table_ratings_msg').html('There are no rating currently flagged for review.');
				}
				for (var i = 0, size = json.aaData.length; i < size; i++) {
					json.aaData[i][0] = '<a class="delete-action" data-id="' + json.aaData[i][0] + '" href="javascript:void(0)">delete</a> ' +
						'<a class="unflag-action" data-id="' + json.aaData[i][0] + '" href="javascript:void(0)">unflag</a>';

					json.aaData[i][4] = '<a href="/assignments/details/' + json.aaData[i][4] + '">' + json.aaData[i][4] + '</a>';
				}
				fnCallback(json);
			});
		}
	});

	wmMaskInput({ selector: '#work_phone' });
	wmMaskInput({ selector: '#mobile_phone' });
	wmMaskInput({ selector: '#sms_phone' });

	$('#change_company_relation').on('click', function () {
		wmModal({
			autorun: true,
			title: 'Change Company',
			destroyOnClose: true,
			content: $('#popup_change_relation').html()
		});

		wmSelect({ selector: '#company_id'}, {
			create: false,
			maxItems: 1,
			openOnFocus: true,
			load: function (query, cb) {
				if (!query.length) { return cb(); }
				$.getJSON(
					'/admin/manage/profiles/suggest_company?term=' + encodeURIComponent(query),
					function (res) {
						cb(_.map(res, function(item) {
							return { value: item.id, text: item.value + ': ' + item.id }
						}));
					}
				);
			}
		});

		wmSelect({ selector: '#select_roles'}, {
			create: false,
			openOnFocus: true
		});
	});

	$('#edit_name_link').on('click', function () {
		wmModal({
			autorun: true,
			content: $('#popup_edit_name').html(),
			title: 'Edit User\'s Name',
			destroyOnClose: true
		});
	});

	$('#form_edit_name').ajaxForm({
		dataType: 'json',
		beforeSubmit: function () {
			$('#popup_edit_name').find('.message').hide();
			$('#popup_edit_name').find('.message div').html('');
		},
		success: function (responseText) {
			$('#popup_edit_name').find('a.disabled').removeClass('disabled');
			if (responseText.successful) {
				$('#user_fullname').html($('#first_name').val() + ' ' + $('#last_name').val());
				wmModal.destroy();
				$('#dynamic_messages div').html('The user\'s name has been successfully updated.');
				$('#dynamic_messages').removeClass('error').addClass('success').show();
			} else {
				var list = _.reduce(responseText.messages, function (memo, message) {
					return memo + '<li>' + message + '<\/li>';
				}, '<ul>');
				$('#popup_edit_name .alert-message').html(list + '<\/ul>');
				$('#popup_edit_name .alert-message').addClass('error').removeClass('success').show();
			}
		}
	});

	$('#suspend_profile_link').on('click', function () {
		if (confirm('Are you sure you want to suspend this user?')){
			$('#suspend_form').submit();
		}
	});


	$('#reset-password-action').on('click', (e) => {
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: e.target.href,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Reset Password',
						destroyOnClose: true,
						content: response
					});
				}
			}
		});

	});

	$('#confirm-account-action').on('click', (e) => {
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: e.target.href,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Confirm Account',
						destroyOnClose: true,
						content: response
					});
				}
			}
		});
	});

	$('#send_message').on('click', function () {
		wmModal({
			autorun: true,
			content: $('#send_message_popup').html(),
			title: 'Send message to worker'
		});
	});
};
