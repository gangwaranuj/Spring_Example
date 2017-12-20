'use strict';

import $ from 'jquery';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';
import 'datatables.net';
import 'jquery-form/jquery.form';
import '../dependencies/jquery.tmpl';
import wmModal from '../funcs/wmModal';

export default () => {
	$('#payment_accounts').dataTable({
		'sPaginationType': 'full_numbers',
		'bLengthChange': false,
		'bFilter': false,
		'iDisplayLength': 25,
		'aoColumnDefs': [
			{'bSortable': false, 'aTargets': [3]}
		]
	});

	let meta;

	$('#datatable').dataTable({
		'sPaginationType':'full_numbers',
		'bLengthChange':false,
		'bFilter':false,
		'aaSorting':[[0, 'asc']],
		'iDisplayLength':10,
		'aoColumnDefs': [
			{'bSortable':false, 'aTargets':[2,3,4,5]},
			{'sClass':'nowrap', 'aTargets':[4]},
			{
				'mRender': (data, type, val, metaData) => {
					return $('#cell-method-tmpl').tmpl({
						data: data,
						meta: meta[metaData.row]
					}).html();
				},
				'bSortable': false,
				'aTargets': [0]
			},
			{
				'mRender': (data, type, val, metaData) => {
					return $('#cell-type-tmpl').tmpl({
						data: data,
						meta: meta[metaData.row]
					}).html();
				},
				'bSortable': false,
				'aTargets': [2]
			},
			{
				'mRender': (data, type, val, metaData) => {
					return $('#cell-status-tmpl').tmpl({
						data: data,
						meta: meta[metaData.row]
					}).html();
				},
				'bSortable': false,
				'aTargets': [4]
			},
			{
				'mRender': (data, type, val, metaData) => {
					return $('#cell-action-tmpl').tmpl({
						data: data,
						meta: meta[metaData.row]
					}).html();
				},
				'bSortable': false,
				'aTargets': [5]
			}
		],
		'bProcessing':true,
		'bServerSide':true,
		'sAjaxSource':'/funds/accounts.json',
		'fnServerData':function (source, data, callback) {
			$.getJSON(source, data, function (json) {
				meta = json.aMeta;
				callback(json);

				$('[data-behavior=verify-modal]').on('click', (event) => {
					event.preventDefault();
					$.ajax({
						type: 'GET',
						url: event.currentTarget.href,
						context: this,
						success: function (response) {
							if (!_.isEmpty(response)) {
								wmModal({
									autorun: true,
									title: 'Verify Account',
									destroyOnClose: true,
									content: response,
									controls: [
										{
											text: 'Cancel',
											close: true,
											classList: ''
										},
										{
											text: 'Verify Account',
											primary: true
										}
									]
								});
								$('.wm-modal .-active').find('.-primary').on('click', () => {
									$('#verify_account_form').ajaxSubmit({
										dataType: 'json',
										success: function (response) {
											if (response.successful) {
												location.reload(true);
												return;
											}

											_.each(response.messages, function (theMessage) {
												wmNotify({
													message: theMessage,
													type: 'danger'
												});
											});
										}
									});
								});
							}
						}
					});

				});

				$('[data-behavior=remove-modal]').on('click', (event) => {
					event.preventDefault();
					$.ajax({
						type: 'GET',
						url: event.currentTarget.href,
						context: this,
						success: function (response) {
							if (!_.isEmpty(response)) {
								wmModal({
									autorun: true,
									title: 'Remove Account',
									destroyOnClose: true,
									content: response,
									controls: [
										{
											text: 'Cancel',
											close: true,
											classList: ''
										},
										{
											text: 'Remove Account',
											primary: true
										}
									]
								});
								$('.wm-modal .-active').find('.-primary').on('click', () => {
									  $('#delete-account').submit();
								});
							}
						}
					});
				});
			});
		}
	});
}
