'use strict';

export default (selector) => {

	var element = null;

	if (selector) {
		element = document.querySelector(selector);
		element.scrollTop = element.scrollHeight;
	}

	return element;
};
