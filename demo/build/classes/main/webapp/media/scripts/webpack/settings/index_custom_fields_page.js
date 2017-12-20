

import $ from 'jquery';
import 'datatables.net';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import ajaxSendInit from '../funcs/ajaxSendInit';

export default (isActivePage) => {
	ajaxSendInit();
	const $customFieldList = $('#custom-field-list');
	let meta;

	const cellRenderer = (template) => {
		return (data, type, val, metaData) => {
			return $(template).tmpl({
				data,
				meta: meta[metaData.row]
			}).html();
		};
	};

	if (isActivePage) {
		$customFieldList.dataTable({
			sPaginationType: 'full_numbers',
			bLengthChange: false,
			bFilter: false,
			bStateSave: true,
			bProcessing: true,
			bServerSide: true,
			iDisplayLength: 200,
			sAjaxSource: '/settings/manage/load_customfields',
			aoColumnDefs: [
				{ bSortable: false, aTargets: [0, 1, 2, 3, 4] },
				{ fnRender: cellRenderer('#name-cell-tmpl'), aTargets: [0] },
				{ sClass: 'actions', mRender: cellRenderer('#required-cell-tmpl'), aTargets: [1] },
				{ sClass: 'actions', mRender: cellRenderer('#edit-cell-tmpl'), aTargets: [2] },
				{ sClass: 'actions', mRender: cellRenderer('#copy-cell-tmpl'), aTargets: [3] },
				{ sClass: 'actions', mRender: cellRenderer('#activate-cell-tmpl'), aTargets: [4] }
			],
			fnServerData (sSource, aoData, fnCallback) {
				$.getJSON(sSource, aoData, (json) => {
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});
	} else {
		$customFieldList.dataTable({
			sPaginationType: 'full_numbers',
			bLengthChange: false,
			bFilter: false,
			bStateSave: true,
			bProcessing: true,
			bServerSide: true,
			iDisplayLength: 200,
			sAjaxSource: '/settings/manage/load_inactive_customfields',
			aoColumnDefs: [
				{ bSortable: false, aTargets: [0, 1, 2, 3, 4] },
				{ fnRender: cellRenderer('#name-cell-tmpl'), aTargets: [0] },
				{ sClass: 'actions', mRender: cellRenderer('#edit-cell-tmpl'), aTargets: [1] },
				{ sClass: 'actions', mRender: cellRenderer('#copy-cell-tmpl'), aTargets: [2] },
				{ sClass: 'actions', mRender: cellRenderer('#delete-cell-tmpl'), aTargets: [3] },
				{ sClass: 'actions', mRender: cellRenderer('#activate-cell-tmpl'), aTargets: [4] }
			],
			fnServerData (sSource, aoData, fnCallback) {
				$.getJSON(sSource, aoData, (json) => {
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});
	}

	const cfAction = (link, url) => {
		$.ajax({
			url,
			type: 'POST',
			data: {
				id: $(link).data('id')
			},
			dataType: 'json',
			global: true,
			success: ({ successful, messages }) => {
				if (successful) {
					if ($(link).data('with-confirm') === 1) {
						wmNotify(messages);
					}
					$customFieldList.fnDraw();
					$('body').scrollTop(0);
				} else {
					wmNotify(messages);
				}
			}
		});
	};

	$customFieldList.on('click', '[data-action="toggle-activate"]', ({ currentTarget }) => {
		cfAction(currentTarget, '/settings/manage/custom_fields_activate');
	});

	$customFieldList.on('click', '[data-action="toggle-deactivate"]', ({ currentTarget }) => {
		cfAction(currentTarget, '/settings/manage/custom_fields_deactivate');
	});

	$customFieldList.on('click', '[data-action="toggle-required"]', ({ currentTarget }) => {
		const link = currentTarget;

		if ($(link).data('with-confirm') === 0) {
			cfAction(link, '/settings/manage/custom_fields_required');
		} else {
			wmModal({
				autorun: true,
				title: 'Require Custom Field Set',
				destroyOnClose: true,
				content: '<p>Are you sure you want to require this fieldset?</p><p>Warning: All existing templates will update to use this fieldset and overwrite previous template settings, removing all currently added custom field sets. This will not affect existing assignments.</p>',
				controls: [
					{
						text: 'No',
						close: true
					},
					{
						text: 'Add Custom Field Set to All Assignments',
						primary: true,
						close: true
					}
				],
				customHandlers: [
					{
						event: 'click',
						selector: '.wm-modal--control.-primary',
						callback: () => cfAction(link, '/settings/manage/custom_fields_required')
					}
				]
			});
			$('.wm-modal .-active').find('.-primary').data('id', currentTarget.getAttribute('data-id'));
		}
	});

	$customFieldList.on('click', '.delete', ({ currentTarget }) => {
		wmModal({
			autorun: true,
			title: 'Delete Custom Field Set',
			destroyOnClose: true,
			content: '<p>Are you sure you want to delete this custom field set?</p><p>It will be permanently deleted from all assignments and reports.</p>',
			controls: [
				{
					text: 'No',
					close: true
				},
				{
					text: 'Delete',
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
							url: '/settings/manage/custom_fields_remove',
							type: 'POST',
							data: { id: currentTarget.getAttribute('data-id') },
							dataType: 'json',
							success: ({ successful, messages }) => {
								if (successful) {
									$customFieldList.fnDraw();
									$('body').scrollTop(0);
								}
								wmNotify(messages);
							}
						});
					}
				}
			]
		});
		$('.wm-modal .-active').find('.-primary').data('id', currentTarget.getAttribute('data-id'));
	});


	$customFieldList.on('click', 'a[data-action="copy"]', ({ currentTarget }) => {
		const modal = wmModal({
			autorun: true,
			title: 'Copy Custom Field Set',
			destroyOnClose: true,
			content: '<p>Please name for your new copied Custom Field Set below.</p><input type="text" id="copy_name" name="copy_name" maxlength="256" class="span5" value="" placeholder="Enter here..." />',
			controls: [
				{
					text: 'No',
					close: true
				},
				{
					text: 'Complete Copy',
					primary: true
				}
			],
			customHandlers: [
				{
					event: 'click',
					selector: '.wm-modal--control.-primary',
					callback: () => {
						$.ajax({
							url: '/settings/manage/custom_fields_copy',
							type: 'POST',
							data: ({
								id: currentTarget.getAttribute('data-id'),
								name: $('#copy_name').val()
							}),
							dataType: 'json',
							success: ({ successful, messages }) => {
								// close the modal on success after the copy_name field can be accessed.
								modal.destroy();
								if (successful) {
									$customFieldList.fnDraw();
									$('body').scrollTop(0);
								}
								wmNotify(messages);
							}
						});
					}
				}
			]
		});
		$('.wm-modal .-active').find('.-primary').data('id', currentTarget.getAttribute('data-id'));
	});
};
