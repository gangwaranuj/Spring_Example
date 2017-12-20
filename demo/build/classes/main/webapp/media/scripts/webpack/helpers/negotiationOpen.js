export default function (status, negotiationApprovalStatus, options) {
	// 0: PENDING;
	// 1: APPROVED;
	// 2: DECLINED;
	// 3: NOT_READY;
	// 4: OPT_OUT;
	// 5: PENDING_REMOVAL;
	// 6: REMOVED;
	if (status === 'unassigned') {
		if (negotiationApprovalStatus === 0) {
			return options.fn(this);
		} else {
			return options.inverse(this);
		}
	} else if (negotiationApprovalStatus !== 2) {
		return options.fn(this);
	}
	return options.inverse(this);
};
