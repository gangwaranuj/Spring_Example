'use strict';

export default (string) => {

	string = string ? string.toLowerCase() : '';

	// We'll take the string, remove all underscores and uppercase the following
	// character.
	return string.replace(/_([a-z])/g, function (str) { return str[1].toUpperCase(); });
};
