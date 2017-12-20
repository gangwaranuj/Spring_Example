var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.budget_increase = function () {
	//... page scripting ...

	return function () {
		FastClick.attach(document.body);
		$('#budgetIncreaseForm').on('submit', function () {
			trackEvent('mobile', 'budget increase', 'request');
		});
	};
};
