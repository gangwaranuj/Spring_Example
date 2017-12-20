'use strict';

export default function (string) {
	return (string || '').replace(/([A-Z])/g, ' $1').replace(/(^.)|(\s.)/g, function (str) { return str.toUpperCase(); });
};
