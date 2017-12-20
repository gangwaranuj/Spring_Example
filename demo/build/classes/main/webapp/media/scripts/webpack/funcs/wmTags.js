'use strict';

import $ from 'jquery';
import _ from 'underscore';
import 'selectize';

export default function (options, selectizeOptions) {
	const settings = Object.assign({
		selector: '.wm-tags',
		root: document
	}, typeof options === 'object' ? options : {});

	const selectizeSettings = Object.assign({
		selectOnTab: true,
		plugins: ['remove_button'],
		create: (input) => {
			return {
				value: input,
				text: input
			};
		}
	}, typeof selectizeOptions === 'object' ? selectizeOptions : {});

	return $(settings.selector, settings.root).selectize(selectizeSettings);
};
