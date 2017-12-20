import $ from 'jquery';
import _ from 'underscore';
import 'datatables.net';
import wmModal from '../funcs/wmModal';

export default function () {
	let meta;
	const $agreements = $('#agreements_list');

	$agreements.dataTable({
		sPaginationType: 'full_numbers',
		bLengthChange: false,
		bFilter: false,
		bStateSave: false,
		bProcessing: true,
		bServerSide: true,
		sAjaxSource: '/agreements/available',
		iDisplayLength: 50,
		aoColumnDefs: [
			{ bSortable: false, aTargets: [0, 1, 2, 3, 4, 5] },
			{
				mRender: (data, type, val) => {
					return `${val[0]}<small> (v${val[3]})</small>`;
				},
				bSortable: false,
				aTargets: [0]
			},
			{
				mRender: (data, type, val, metaData) => {
					return `<a aria-label="View Agreement" class="viewagreement tooltipped tooltipped-n" href="/agreements/get_agreement_text?id=${meta[metaData.row].asset}"><i class="wm-icon-follow icon-large muted"></i></a></span>`;
				},
				sClass: 'actions',
				bSortable: false,
				aTargets: [3]
			},
			{
				mRender: (data, type, val, metaData) => {
					return `<a class="tooltipped tooltipped-n" aria-label="Edit" href="/agreements/edit?id=${meta[metaData.row].id}&version=${meta[metaData.row].asset}"><i class="icon-edit icon-large muted"></i></a></span>`;
				},
				sClass: 'actions',
				bSortable: false,
				aTargets: [4]
			},
			{
				mRender: (data, type, val, metaData) => {
					return `<a class="delete_agreement tooltipped tooltipped-n" aria-label="Delete" href="/agreements/deactivate?id=${meta[metaData.row].id}"><i class="wm-icon-trash icon-large muted"></i></a>`;
				},
				sClass: 'actions',
				bSortable: false,
				aTargets: [5]
			}
		],
		fnServerData: (sSource, aoData, fnCallback) => {
			$.getJSON(sSource, aoData, (json) => {
				meta = json.aMeta;
				fnCallback(json);
			});
		}
	});

	$agreements.on('click', '.delete_agreement', () => {
		return confirm('Are you certain you want to delete this agreement?'); // eslint-disable-line no-alert
	});

	$agreements.on('click', '.viewagreement', (event) => {
		event.preventDefault();

		const getModalContent = $.ajax({
			type: 'GET',
			url: event.currentTarget.href
		});

		$.when(getModalContent).done((response) => {
			if (_.isEmpty(response)) {
				return;
			}

			wmModal({
				autorun: true,
				title: 'View Agreement',
				destroyOnClose: true,
				content: response
			});
		});
	});
}
