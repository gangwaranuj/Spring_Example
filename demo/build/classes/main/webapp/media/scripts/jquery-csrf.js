/* exported getCSRFToken */

var getCSRFToken = function () {
	'use strict';

	var selector = 'meta[name="csrf-token"]';
	var $meta = $(selector);
	if ($meta.length === 0) {
		$meta = window.parent.$(selector);
	}

	return $meta.attr('content');
}

// auto-submit CSRF token on each request
$(document).ajaxSend(function (elm, xhr, s) {
	'use strict';

	if (s.type === 'POST' || s.type === 'PUT' || s.type === 'DELETE') {
		xhr.setRequestHeader('X-CSRF-Token', getCSRFToken());
	}
});
