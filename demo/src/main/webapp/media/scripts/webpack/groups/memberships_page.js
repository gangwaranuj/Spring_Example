import $ from 'jquery';
import 'datatables.net';
import '../dependencies/jquery.tmpl';

export default () => {
	var meta;

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
			{ mRender: cellRenderer('#cell-owner-tmpl'), aTargets: [1] },
			{ mRender: cellRenderer('#cell-status-tmpl'), aTargets: [2] },
			{ mRender: cellRenderer('#cell-approved-tmpl'), aTargets: [3] }
		],
		sAjaxSource: '/groups/view/load_group_associations?type=memberships',
		fnServerData: function (sSource, aoData, fnCallback) {
			$.getJSON(sSource, aoData, (json) => {
				meta = json.aMeta;
				fnCallback(json);
			});
		}
	});
};
