export default function (publicGroups, privateGroups, options) {
	publicGroups  = publicGroups || [];
	privateGroups = privateGroups || [];
	if (publicGroups.length + privateGroups.length > 0) {
		return options.inverse(this);
	}
	return options.fn(this);
};