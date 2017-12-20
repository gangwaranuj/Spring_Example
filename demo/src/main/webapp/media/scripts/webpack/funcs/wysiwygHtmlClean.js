'use strict';

import $ from 'jquery';
import keyCodes from './keyCodes';
import '../dependencies/jquery.wysiwyg';

let ctrlDown = false,
	plainTextarea = false,
	url = '/assignments/clean_html',
	listenerDoc;

let cleanHtml = (event, selector) => {
	if (ctrlDown && (event.keyCode === keyCodes.V)) {
		let htmlContent = $(selector).val();

		$.ajax({
			dataType: 'json',
			type: 'POST',
			data: { htmlContent },
			url,
			success(data) {
				if (plainTextarea) {
					$(selector).val(data.cleanHtml);
				} else {
					$($(selector).wysiwyg('document')).find('body').html(data.cleanHtml);
				}
			},
			error(jqXHR, status, errorThrown) {
				throw Error('JSONFixture could not be loaded: ' + url + ' (status: ' + status + ', message: ' + errorThrown.message + ')');
			}
		});
	}
};

export default (selector='#desc-text') => {
	plainTextarea = $('#autotaskId').length > 0  || !$(selector).wysiwyg('document');
	if (plainTextarea) {
		listenerDoc = $(selector);
	} else {
		listenerDoc = $(selector).wysiwyg('document');
	}

	listenerDoc.on({
		keydown(event) {
			if (event.keyCode === keyCodes.CONTROL || event.keyCode === event.metaKey) {
				ctrlDown = true;
			}
		},
		keyup(event) {
			if (event.keyCode === keyCodes.CONTROL || event.keyCode === event.metaKey) {
				ctrlDown = false;
			}
			cleanHtml(event, selector);
		}
	});

	return listenerDoc;
};
