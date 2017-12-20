'use strict';

import $ from 'jquery';

$.fn.autoresizeTextarea = function () {
	this.on('change keyup keydown paste cut autoresize', function () {
		$(this).height(0).height(this.scrollHeight);
	}).trigger('autoresize');
};
