import $ from 'jquery';
import 'datatables.net';
import wmNotify from '../funcs/wmNotify';
import '../dependencies/jquery.tmpl';

export default () => {
	let meta;

	const cellRenderer = (template) => {
		return (data, type, val, metaData) => {
			return $(template).tmpl({
				data,
				meta: meta[metaData.row]
			}).html();
		};
	};

	$('#group_list').dataTable({
		sPaginationType: 'full_numbers',
		bLengthChange: false,
		bFilter: false,
		iDisplayLength: 25,
		bProcessing: true,
		bServerSide: true,
		aoColumnDefs: [
			{ mRender: cellRenderer('#cell-name-tmpl'), aTargets: [0] },
			{ mRender: cellRenderer('#cell-text-tmpl'), aTargets: [1] },
			{ mRender: cellRenderer('#cell-groupactions-tmpl'), bSortable: false, aTargets: [2] }
		],
		sAjaxSource: '/groups/view/load_group_invitations',
		fnServerData: (sSource, aoData, fnCallback) => {
			$.getJSON(sSource, aoData, (json) => {
				meta = json.aMeta;
				fnCallback(json);
			});
		},
		fnInitComplete: () => {
			$('[data-action="decline"]').on('click', (e) => {
				e.preventDefault();
				const link = $(e.target);
				const id = link.attr('data-value');
				$.ajax({
					url: `/groups/${id}/decline`,
					success: ({ successful, messages }) => {
						if (successful) {
							link.replaceWith('<span>Declined</span>');
						} else {
							messages.forEach(message => wmNotify({ message, type: 'danger' }));
						}
					}
				});
			});
		}
	});
};
