import $ from 'jquery';
import 'datatables.net';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import ajaxSendInit from '../funcs/ajaxSendInit';

export default (isActiveTemplatesPage) => {
	ajaxSendInit();
	let meta;
	const cellRenderer = (template) => {
		return (data, type, val, metaData) => {
			return $(template).tmpl({
				data,
				meta: meta[metaData.row]
			}).html();
		};
	};

	if (isActiveTemplatesPage) {
		const $templatesList = $('#templates_list_active');

		$templatesList.dataTable({
			sPaginationType: 'full_numbers',
			bLengthChange: false,
			iDisplayLength: 50,
			bFilter: false,
			bStateSave: true,
			bProcessing: true,
			bServerSide: true,
			aaSorting: [[0, 'asc']],
			aoColumns: [null, null, null, { bSortable: false }],
			aoColumnDefs: [
				{ bSortable: false, aTargets: [2, 3, 4] },
				{ mRender: cellRenderer('#name-cell-tmpl'), aTargets: [0] },
				{ sClass: 'actions', mRender: cellRenderer('#edit-cell-tmpl'), aTargets: [2] },
				{ sClass: 'actions', mRender: cellRenderer('#copy-cell-tmpl'), aTargets: [3] },
				{ sClass: 'actions', mRender: cellRenderer('#active-cell-tmpl'), aTargets: [4] }
			],
			sAjaxSource: '/settings/manage/templates_list',
			fnServerData (sSource, aoData, fnCallback) {
				aoData.push();
				$.getJSON(sSource, aoData, (json) => {
					if (json.aaData.length === 0) {
						$('#table_templates').hide();
						$('.table_templates_msg').html('<p>You currently have no active templates.</p>');
					}
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});

		$templatesList.on('click', '.active', ({ currentTarget }) => {
			wmModal({
				autorun: true,
				title: 'Deactivate Template',
				destroyOnClose: true,
				content: 'Are you sure you want to deactivate this template?',
				controls: [
					{
						text: 'No',
						close: true
					},
					{
						text: 'Yes',
						primary: true,
						close: true
					}
				],
				customHandlers: [
					{
						event: 'click',
						selector: '.wm-modal--control.-primary',
						callback: () => {
							$.ajax({
								url: '/settings/manage/templates_status_update',
								type: 'POST',
								data: ({
									id: currentTarget.getAttribute('data-id'),
									status: false
								}),
								dataType: 'json',
								success: ({ successful, messages }) => {
									if (successful) {
										$templatesList.fnDraw();
										$('body').scrollTop(0);
									}
									wmNotify(messages);
								},
								error: () => wmNotify('Failed to deactivate template.')
							});
						}
					}
				]
			});
			$('.wm-modal .-active').find('.-primary').data('id', currentTarget.getAttribute('data-id'));
		});
	} else {
		const $templatesList = $('#templates_list_inactive');

		$templatesList.dataTable({
			sPaginationType: 'full_numbers',
			bLengthChange: false,
			iDisplayLength: 50,
			bFilter: false,
			bStateSave: true,
			bProcessing: true,
			bServerSide: true,
			aaSorting: [[0, 'asc']],
			aoColumns: [null, null, null, { bSortable: false }],
			aoColumnDefs: [
				{ bSortable: false, aTargets: [2, 3, 4, 5] },
				{ mRender: cellRenderer('#name-cell-tmpl'), aTargets: [0] },
				{ sClass: 'actions', mRender: cellRenderer('#edit-cell-tmpl'), aTargets: [2] },
				{ sClass: 'actions', mRender: cellRenderer('#copy-cell-tmpl'), aTargets: [3] },
				{ sClass: 'actions', mRender: cellRenderer('#active-cell-tmpl'), aTargets: [4] },
				{ sClass: 'actions', mRender: cellRenderer('#delete-cell-tmpl'), aTargets: [5] }
			],
			sAjaxSource: '/settings/manage/inactive_templates_list',
			fnServerData (sSource, aoData, fnCallback) {
				aoData.push();
				$.getJSON(sSource, aoData, (json) => {
					if (json.aaData.length === 0) {
						$('#table_templates').hide();
						$('.table_templates_msg').html('<p>You have no deactivated templates. <a href="/settings/manage/templates">Back to Active Templates</a></p>');
					}
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});

		$templatesList.on('click', '.delete', ({ currentTarget }) => {
			wmModal({
				autorun: true,
				title: 'Delete Template',
				destroyOnClose: true,
				content: 'Are you sure you want to delete this template?',
				controls: [
					{
						text: 'No',
						close: true
					},
					{
						text: 'Yes',
						primary: true,
						close: true
					}
				],
				customHandlers: [
					{
						event: 'click',
						selector: '.wm-modal--control.-primary',
						callback: () => {
							$.ajax({
								url: '/settings/manage/templates_delete',
								type: 'POST',
								data: ({
									id: currentTarget.getAttribute('data-id')
								}),
								dataType: 'json',
								success: (response) => {
									if (response.successful) {
										$templatesList.fnDraw();
										$('body').scrollTop(0);
										wmNotify({ message: response.messages[0] });
									} else {
										wmNotify({
											message: response.messages[0],
											type: 'danger'
										});
									}
								},
								error: () => {
									wmNotify({
										message: 'Failed to delete template.',
										type: 'danger'
									});
								}
							});
						}
					}
				]
			});
			$('.wm-modal .-active').find('.-primary').data('id', currentTarget.getAttribute('data-id'));
		});

		$templatesList.on('click', '.inactive', (e) => {
			$.ajax({
				url: '/settings/manage/templates_status_update',
				type: 'POST',
				data: ({
					id: $(e.currentTarget).attr('data-id'),
					status: true
				}),
				dataType: 'json',
				success (response) {
					if (response.successful) {
						$templatesList.fnDraw();
						$('body').scrollTop(0);
						wmNotify({ message: response.messages[0] });
					} else {
						wmNotify({
							message: 'Failed to activate template.',
							type: 'danger'
						});
					}
				},
				error () {
					wmNotify({
						message: 'Failed to activate template.',
						type: 'danger'
					});
				}
			});
		});
	}
};
