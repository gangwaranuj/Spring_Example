'use strict';

import $ from 'jquery';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import 'jquery-form/jquery.form';
import 'jquery.browser';
import 'datatables.net';

export default () => {

	var meta,
		$list = $('#companies_list'),
		datatableObj = $list.dataTable({
			'sPaginationType':'full_numbers',
			'bLengthChange':true,
			'bFilter':false,
			'bStateSave':false,
			'iDisplayLength':100,
			'aoColumnDefs':[
				{'mRender':renderNameCell, 'aTargets':[0]},
				{'mRender':renderUnlockLink, 'aTargets':[1]}
			],
			'bSort':true,
			'bProcessing':true,
			'bServerSide':true,
			'sAjaxSource':'/admin/locks/list',
			'fnServerData':function (sSource, aoData, fnCallback) {
				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});

	datatableObj.fnDraw();

	function renderNameCell(data, type, val, metaData) {
		return '<a href="/admin/manage/company/overview/' + meta[metaData.row].company_id + '">' + data + '</a>';
	}

	function renderUnlockLink(data, type, val, metaData) {
		return '<a class="unlock" href="javascript:void(0);" data-id="' + meta[metaData.row].company_id + '">Unlock</a>';
	}

	$list.on('click', '.unlock', function (event ) {
		var id = $(event.currentTarget).data('id'),
			$formUnlock = $('#form_unlock');

		// Clear form
		$formUnlock.resetForm();

		// Get form action url
		var action = $formUnlock.attr('action');

		// Replace id in form with passed id
		$formUnlock.attr('action', action.replace(/\/unlock.*$/, "/unlock/" + id))

		// Little hack for flawed browsers that try to download the JSON response (IE <= 8)
		var ua = $.browser;
		if (ua.msie && parseInt(ua.version.slice(0, 1)) <= 8) {
			$('#form_unlock').attr('action', action.replace(/\/unlock.*$/, "/unlock/" + id + '.html'));
		}

		// Init dialog box of unlock form from markup on page
		const unlockModal = wmModal({
			autorun: true,
			title: 'Unlock Company',
			destroyOnClose: true,
			content: $('#unlock_form_container').html()
		});

		$('#form_unlock').ajaxForm({
			dataType: 'json',
			data: {
				// Need to add a hidden field to tell request it is ajax
				// this is a hack because file uploads don't send proper ajax request headers
				'X_REQUESTED_WITH': 'XMLHttpRequest'
			},
			success: function (response) {
				if (response.successful) {
					unlockModal.destroy();
					wmNotify({
						message: response.messages
					});
					datatableObj.fnDraw();
				} else {
					wmNotify({
						type: 'danger',
						message: 'An error occurred trying to unlock the company.'
					});
				}
			}
		});
	});
};
