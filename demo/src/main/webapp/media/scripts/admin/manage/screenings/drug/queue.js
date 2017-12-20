var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.manage = wm.pages.admin.manage || {};
wm.pages.admin.manage.screenings = wm.pages.admin.manage.screenings || {};
wm.pages.admin.manage.screenings.drug = wm.pages.admin.manage.screenings.drug || {};

wm.pages.admin.manage.screenings.drug.queue = function (queueList) {
	var
		meta,
		table,
		renderNameCell,
		renderActionCell;

	renderNameCell = function(row) {
		return $('#name-cell-tmpl').tmpl({
			'data': row.aData[row.iDataColumn],
			'meta': meta[row.iDataRow]
		}).html();
	};

	renderActionCell = function(row) {
		return $('#action-cell-tmpl').tmpl(meta[row.iDataRow]).html();
	};

	return function () {
		table = $('#screenings_list_table').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'iDisplayLength': 50,
			'bFilter': false,
			'bStateSave': true,
			'bProcessing': true,
			'bServerSide': true,
			'aoColumnDefs': [
				{'bSortable': false, 'aTargets': [0, 1, 2, 3, 4, 5]},
				{'fnRender': renderNameCell, 'aTargets': [2]},
				{'fnRender': renderActionCell, 'aTargets': [5]}
			],
			'sAjaxSource': queueList,
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				// aoData.push();
				$.each($('#screenings_filter_form').serializeArray(), function (i, item) {
					aoData.push(item);
				});
				$.getJSON( sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json)
				});
			}
		});

		$('#screenings_filter_form input, #screenings_filter_form select').on('change', function () {
			table.fnDraw();
		});
	};
};