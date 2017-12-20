var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.accounting = wm.pages.admin.accounting || {};

wm.pages.admin.accounting.locked_accounts = function (companyOverview, sAjaxSource) {
	return function() {
		var meta;

		function renderNameCell(row) {
			return '<a class="unlock" href="' + companyOverview + '/' + meta[row.iDataRow].company_id + '">' + row.aData[row.iDataColumn] + '</a>';
		}

		$('#locked_accounts').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': false,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'iDisplayLength': 50,
			'aaSorting': [[0,'asc']],
			'aoColumnDefs': [
				{'fnRender': renderNameCell, 'aTargets': [0]},
				{'bSortable': false, 'aTargets': [1]}
			],
			'sAjaxSource': sAjaxSource,
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json)
				});
			}
		});
	};
};