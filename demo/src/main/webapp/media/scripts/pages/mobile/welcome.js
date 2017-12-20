var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};

wm.pages.mobile.welcome = function () {
	$('.landing-page').on('click', '.cta-button', function (e) {
		window.location = $(e.currentTarget).attr('href');
	});
};
