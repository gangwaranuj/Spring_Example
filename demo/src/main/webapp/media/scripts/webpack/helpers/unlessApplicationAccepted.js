'use strict';

export default function (negotiationApprovalStatus, options) {
	if (negotiationApprovalStatus !== 1) {
		return options.fn(this);
	}
	return options.inverse(this);
};
