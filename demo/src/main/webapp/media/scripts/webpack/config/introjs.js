import $ from 'jquery';
import IntroJs from 'intro.js';
import _ from 'underscore';
import 'jquery.cookie';

export default (introName) => {
	// eslint-disable-next-line new-cap
	const intro = new IntroJs.introJs();
	intro.name = introName;

	intro.setOptions({
		/* Next button label in tooltip box */
		nextLabel: 'Next &rarr;',
		/* Previous button label in tooltip box */
		prevLabel: '&larr; Back',
		/* Skip button label in tooltip box */
		skipLabel: 'Read Later',
		/* Done button label in tooltip box */
		doneLabel: 'Finish',
		/* Default tooltip box position */
		tooltipPosition: 'bottom',
		/* Next CSS class for tooltip boxes */
		tooltipClass: 'wm-introjs',
		/* Close introduction when pressing Escape button? */
		exitOnEsc: true,
		/* Close introduction when clicking on overlay layer? */
		exitOnOverlayClick: true,
		/* Show step numbers in introduction? */
		showStepNumbers: false,
		/* Let user use keyboard to navigate the tour? */
		keyboardNavigation: true,
		/* Show tour control buttons? */
		showButtons: true,
		/* Show tour bullets? */
		showBullets: true,
		/* Scroll to highlighted element? */
		scrollToElement: true,
		/* Set the overlay opacity */
		overlayOpacity: 0.8
	});

	// When user completes a joyride, we set a cookie and post the visit to the
	// visited_resources table
	const _oncomplete = intro.oncomplete;
	intro.oncomplete = _.compose(_oncomplete, (func) => {
		return function () {
			// Record visit to this resource
			$.ajax({
				url: `/tracking/merge?resourceName=${intro.name}`
			});

			// Set cookie to avoid unnecessary ajax request
			$.cookie(intro.name, 'viewed', { expires: 365, path: '/' });

			// Google analytics
			ga('send', 'pageview', `/intro/${intro.name}/finish`);

			func();
		};
	});
	intro.oncomplete(_.noop);

	// Send virtual pageview for each step of the joyride that is hit
	const _onafterchage = intro.onafterchange;
	intro.onafterchange = _.compose(_onafterchage, (func) => {
		return function (e) {
			// Google analytics
			ga('send', 'pageview', `/intro/${intro.name}/${intro._currentStep}`);
			func(e);
		};
	});
	intro.onafterchange(_.noop);

	// Watch once ensures the joyride does not run if the user has already seen it
	intro.watchOnce = function () {
		// Check for cookie first... that's fast and cheap
		if ($.cookie(intro.name)) {
			// User has already viewed this joyride
			return;
		}

		// Request list of viewed joyrides from server next...
		$.ajax({
			url: '/tracking',
			success (response) {
				// Find intro.name in the visited resources list?
				if (_.contains(response.visitedList, intro.name)) {
					// User has already viewed this
					return;
				}

				// User does not appear to have viewed this... show it!
				intro.start();
			}
		});
	};

	return intro;
};

