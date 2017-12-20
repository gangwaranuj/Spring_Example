'use strict';

export default function (number) {
	if (number >= 10) {
		return number.toFixed();
	}
	return number.toFixed(1);
};
