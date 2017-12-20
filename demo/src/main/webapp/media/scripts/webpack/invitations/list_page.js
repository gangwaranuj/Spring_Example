'use strict';

import $ from 'jquery';
import 'datatables.net';
import '../dependencies/jquery.tmpl';
import ProfileLinkTemplate from './templates/profile_link.hbs';

export default function (options) {
	let meta;

	const renderProfileLinkCell = (data, type, val, { row }) =>
		meta[row].invited_user_number ? ProfileLinkTemplate({ data, meta: meta[row] }) : data;
	const renderEmailLinkCell = (data) => `<a href="mailto:${data}">${data}</a>`;
	const renderCampaignLinkCell = (data, type, val, { row }) => {
		return `<a href="/campaigns/details/${meta[row].campaign_id}">${data}</a>`;
	};
	const renderReminderCell = (data, type, val, { row }) => {
		const { status, is_reminder_blocked, id } = meta[row];
		let response = '';
		if (status === 'sent' && is_reminder_blocked) {
			response = 'Awaiting Response';
		} else if (status === 'sent') {
			response = `<input name="invitation_ids[]" value="${id}" type="checkbox" />`;
		} else if (status === 'declined') {
			response = `<img src="${mediaPrefix}/images/icons/cross.png"/>`;
		} else if (status === 'registered' || status === 'insystem') {
			response = `<img src="${mediaPrefix}/images/icons/tick.png"/>`;
		}
		return response;
	};

	const datatable_obj = $('#invitations_list').dataTable({
		'sPaginationType':'full_numbers',
		'bLengthChange':true,
		'bFilter':false,
		'aaSorting':[
			[5, 'desc']
		],
		'iDisplayLength':50,
		'aoColumnDefs':[
			{ 'bSortable':false, 'aTargets':[ 0, 1, 2, 3, 4 ] },
			{ 'mRender':renderReminderCell, 'aTargets':[ 0 ] },
			{ 'mRender':renderProfileLinkCell, 'aTargets':[ 1 ] },
			{ 'mRender':renderEmailLinkCell, 'aTargets':[ 2 ] },
			{ 'mRender':renderCampaignLinkCell, 'aTargets':[ 3 ] }
		],
		"sDom":'<"custom-table-header-wrapper"l<"custom-table-header-outlet">>frtip',
		'bProcessing':true,
		'bServerSide':true,
		'sAjaxSource':'/invitations/populate?rand=' + options.nowTime,
		'fnServerData':function (sSource, aoData, fnCallback) {
			$.getJSON(sSource, aoData, function (json) {
				$('.custom-table-header-outlet').html($('#custom-header-outlet-tmpl').tmpl());
				meta = json.aMeta;
				fnCallback(json);
			});
		}
	});

	$('#invitations_list').on('.profile_link', 'click', (event) => {
		event.preventDefault();
		var $profileBody = $('.profile-body');
		var $profilePopup = $('#user-profile-popup');

		$profileBody.empty();
		$profilePopup.modal('show');
		$profilePopup.find('.profile-spinner').show();
		$.get(`${$(event.currentTarget).attr('href')}?popup=1`, (result) => {
			$profilePopup.find('.profile-spinner').hide();
			$profileBody.html(result);
		});
	});
};
