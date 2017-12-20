var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.notes = function () {

	return function () {
		FastClick.attach(document.body);

		$('#add-note-form').on('submit', function () {
			trackEvent('mobile','note', 'add');
		});
	};
};