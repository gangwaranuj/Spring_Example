'use strict';
import $ from 'jquery';
import _ from 'underscore';
import 'selectize';

export default (options, selectizeOptions) => {

	var settings = _.extend({
		selector: '.wm-select',
		root: document
	}, typeof options === 'object' ? options : {});

	var selectizeSettings = _.extend({
		onDropdownClose: ($dropdown) => {
			$($dropdown).find('.selected').not('.active').removeClass('selected');
		}
	}, typeof selectizeOptions === 'object' ? selectizeOptions : {});

	return $(settings.selector, settings.root).each(function () {
		var elementSettings = _.clone(selectizeSettings);
		if (this.hasAttribute('multiple')) {
			elementSettings = _.defaults(elementSettings, {
				plugins: ['remove_button'],
				maxItems: null
			});
		}
		$(this).selectize(elementSettings);
	});
};
