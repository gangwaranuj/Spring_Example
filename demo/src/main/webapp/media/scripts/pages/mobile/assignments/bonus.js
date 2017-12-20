var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.bonus = function () {

	return function () {
		FastClick.attach(document.body);

		$('#bonusForm').on('submit', function () {
			trackEvent('mobile','bonus', 'request');
		});
	};
};