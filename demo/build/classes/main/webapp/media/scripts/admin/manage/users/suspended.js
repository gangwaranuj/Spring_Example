var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.manage = wm.pages.admin.manage || {};
wm.pages.admin.manage.users = wm.pages.admin.manage.users || {};

wm.pages.admin.manage.users.suspended = function () {
	return function() {
		$('#select-all').on('click', function () {
			$('#users_list input[type=checkbox]').prop('checked', $(this).is(':checked'))
		});

		$('#users_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': true,
			'iDisplayLength': 50,
			'bStateSave': true,
			'bProcessing': true,
			'bServerSide': true,
			'aoColumnDefs': [
				{'bSortable': false, 'aTargets': [0,1,2]},
				{'fnRender': renderNameCell, 'aTargets': [0]}
			],
			'sAjaxSource': '/admin/manage/users/suspended.json',
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				aoData.push();
				$.getJSON( sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json)
				});
			}
		});

		var renderNameCell = function(row) {
			return '<a href="/admin/manage/profiles/index/' + meta[row.iDataRow].id + '">' + row.aData[row.iDataColumn] + '</a>';
		};
	};
};