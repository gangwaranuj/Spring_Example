'use strict';

import $ from 'jquery';
import _ from 'underscore';
import '../dependencies/jquery.wysiwyg';
import '../dependencies/jquery.wysiwyg.rmFormat';
import '../dependencies/jquery.wysiwyg.link';

var baseOptions = {
	css: mediaPrefix + '/wysiwyg.css',
	initialContent: '',
	autoSave: true,
	plugins:{
		rmFormat: {
			rmMsWordMarkup: {
				enabled: false,
				tags: {
					'a': { rmAttr: 'all' },
					'b': { rmAttr: 'all' },
					'div': { rmAttr: 'all' },
					'em': { rmAttr: 'all' },
					'span': {
						rmAttr: 'all',
						style: '',
						name: '',
						lang: ''
					},
					'strong': { rmAttr: 'all' },
					'p': {
						rmAttr: 'all',
						rmWhenNoAttr: false,
						rmWhenEmpty: false
					},
					'u': { rmAttr: 'all' }
				}
			}
		}
	},
	rmUnusedControls: true,
	resizeOptions: {
		minHeight: 200,
		minWidth: 496,
		maxWidth: 496
	},
	iFrameClass: 'wm-iframe',
	toolbarHtml: '<ul role="menu" class="toolbar new-toolbar"></ul>',
	controls: {
		bold: {
			visible: true,
			className: 'new-bold'
		},
		italic: {
			visible : true,
			className: 'new-italic'
		},
		underline: {
			visible : true,
			className: 'new-underline'
		},
		insertOrderedList: {
			visible : true,
			className: 'new-ordered-list'
		},
		insertUnorderedList: {
			visible : true,
			className: 'new-numbered-list'
		}
	}
};

var wysiwyg = $.fn.wysiwyg;
$.fn.wysiwyg = function (options={}) {
	_.defaults(options, baseOptions);
	return wysiwyg.call(this, options);
};

$('textarea[data-richtext="wysiwyg"]').wysiwyg();
