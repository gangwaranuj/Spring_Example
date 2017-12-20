export default function (allowTagging, isOwner, isLane4LimitedVisibility, options) {
	if (allowTagging && !isOwner && !isLane4LimitedVisibility) {
		return options.fn(this);
	}
	return options.inverse(this);
};