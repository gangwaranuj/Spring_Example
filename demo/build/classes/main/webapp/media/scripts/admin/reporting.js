var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};

wm.pages.admin.reporting = function () {
	$('#summary-list').dataTable({
		'sPaginationType': 'full_numbers',
		'bLengthChange': false,
		'iDisplayLength': 25,
		'bFilter': false,
		'bSort': false
	});

	$('#fromDate').datepicker({dateFormat: 'mm/dd/yy'});
	$('#toDate').datepicker({dateFormat: 'mm/dd/yy'});
};
