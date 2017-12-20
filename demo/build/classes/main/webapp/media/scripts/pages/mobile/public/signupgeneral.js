var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.public = wm.pages.mobile.public || {};

wm.pages.mobile.public.signupgeneral = function () {
	return function () {
		$(googlePlaces.auto_complete);

		$('.termsLink').bind('tap click', function (e) {
			e.stopPropagation();
		});
	};
};
