var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.manage = wm.pages.admin.manage || {};
wm.pages.admin.manage.company = wm.pages.admin.manage.company || {};

wm.pages.admin.manage.company.search = function () {
	return function () {
		var meta;

		$('#companies_list').dataTable({
			'sPaginationType':'full_numbers',
			'bLengthChange':true,
			'bFilter':true,
			'bStateSave':false,
			'bProcessing':true,
			'bServerSide':true,
			'iDisplayLength':100,
			'aaSorting':[[5,'desc']],
			'aoColumnDefs':[
				{'sType':'date', 'aTargets': [5]},
				{'fnRender':renderNameCell, 'aTargets':[0]}
			],
			'bSort':true,
			'bProcessing':true,
			'sAjaxSource':'/admin/manage/company/runSearch',
			'fnServerData':function (sSource, aoData, fnCallback) {
				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json)
				});
			}
		}).fnDraw();

		function renderNameCell(row) {
			return '<a href="/admin/manage/company/overview/' + meta[row.iDataRow].company_id + '">' + row.aData[row.iDataColumn] + '</a>';
		}
	};
};