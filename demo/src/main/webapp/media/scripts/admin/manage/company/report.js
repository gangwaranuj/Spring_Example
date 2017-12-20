var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.manage = wm.pages.admin.manage || {};
wm.pages.admin.manage.company = wm.pages.admin.manage.company || {};

wm.pages.admin.manage.company.report = function () {
	return function () {
		$('#fromDate').datepicker({dateFormat: 'mm/yy'});
		$('#toDate').datepicker({dateFormat: 'mm/yy'});
	}
};