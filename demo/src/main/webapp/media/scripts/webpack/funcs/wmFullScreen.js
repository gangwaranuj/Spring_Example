'use strict';

import Template from '../funcs/templates/fullscreen.hbs';
import $ from 'jquery';
import _ from 'underscore';

export default function (options) {
	var settings = _.extend({
		template: Template,
		el: 'body'
	}, typeof options === 'object' ? options : {});

	var api = {
		close: function () {
			$('.wm-fullscreen--overlay').remove();
		}
	};

	var notification = settings.template(settings);
	$(settings.el).append(notification);
	$('.wm-fullscreen--overlay').height($(document).height());

	// close events
	$('.fullscreen-close, .wm-fullscreen--overlay').on( 'click', () => {
		$('.wm-fullscreen--overlay').remove();
	});
	$(document).on( 'keyup', e => {
		if (e.keyCode === 27) {
			$('.wm-fullscreen--overlay').remove();
		}
	});

	return api;
};
