export default function (isOwner, isInternal, assets, options) {
	if ((isOwner || isInternal) && assets) {
		return options.fn(this);
	}
	return options.inverse(this);
};