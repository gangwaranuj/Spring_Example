$.fn.autoresizeTextarea = function () {
	'use strict';

	this.on('change keyup keydown paste cut autoresize', function () {
		$(this).height(0).height(this.scrollHeight);
	}).trigger('autoresize');
};
