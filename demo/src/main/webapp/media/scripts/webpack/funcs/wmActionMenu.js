'use strict';
import $ from 'jquery';
import _ from 'underscore';
import 'selectize';

export default (options, selectizeOptions) => {

	var settings = _.extend({
		selector: '.action-menu',
		root: document
	}, typeof options === 'object' ? options : {});

	var selectizeSettings = _.extend({
		onDropdownOpen: function ($dropdown) {
			var windowRightEdge = window.innerWidth,
				dropdownRightEdge = $dropdown.get(0).getBoundingClientRect().right;

			if (dropdownRightEdge > windowRightEdge) {
				$dropdown.addClass('-left');
			}
		},
		onDropdownClose: function ($dropdown) {
			$dropdown.removeClass('-left');
			this.blur();
		}
	}, typeof selectizeOptions === 'object' ? selectizeOptions : {});

	return $(settings.selector, settings.root).selectize(selectizeSettings);
};
