'use strict';
import $ from 'jquery';

export default () => {

	const selector = 'meta[name="csrf-token"]';
	let $meta = $(selector);
	if ($meta.length === 0) {
		$meta = window.parent.$(selector);
	}

	return $meta.attr('content');
};
