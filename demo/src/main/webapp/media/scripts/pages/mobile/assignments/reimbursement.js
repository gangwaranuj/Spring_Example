var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.reimbursement = function () {
	//... page scripting ...

	return function () {
		FastClick.attach(document.body);
		$('#reimbursement-form').on('submit', function () {
			trackEvent('mobile', 'reimbursement', 'request');
		});
	};
};
