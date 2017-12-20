'use strict';

export default (string, limit) => {

	var defaultFill = '&hellip;',
		minimumLimit = 4;

	string = string || '';
	limit = limit || minimumLimit;
	limit = (limit < minimumLimit ? minimumLimit : limit);

	return string.length > limit ? string.substring(0, limit - 3) + defaultFill : string;
};
