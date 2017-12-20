export default function (isOwner, isGroupOwner, availabilityCode, options) {
	if (isOwner || (isGroupOwner && (availabilityCode === 'group')) || availabilityCode === 'all') {
		return options.fn(this);
	} else {
		return options.inverse(this);
	}
};
