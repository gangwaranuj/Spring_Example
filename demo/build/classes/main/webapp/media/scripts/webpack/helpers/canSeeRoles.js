export default function (isOwner, isAdmin, options) {
	if (!isOwner && isAdmin) {
		return options.fn(this);
	}

	return options.inverse(this);
};