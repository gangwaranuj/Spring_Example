/* Created by Gina on Apr. 10, 2014 */

var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.help = function () {
	var slideTransition = function (event) {
			var $link = $(event.target),
				newSlide = $($link.attr('href'));

			event.preventDefault();

			if (newSlide.length > 0) {
				//clearing the class first from current slide
				$('.active-slide')
					.removeClass('active-slide')
					.addClass('slide-exit')
					.bind('animationend webkitAnimationEnd MSAnimationEnd oAnimationEnd', function () {
						$(this).removeClass('slide-exit');
					});

				//give it to the link's target div
				newSlide.addClass('active-slide');

			}
	};
	return function () {
		var $links = $('.slide-change');
		$links.on('click', slideTransition);
	}
};