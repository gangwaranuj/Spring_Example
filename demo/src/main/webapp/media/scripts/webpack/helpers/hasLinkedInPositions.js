export default function (linkedInVerified, linkedInPositions, options) {
	if (linkedInVerified && linkedInPositions && linkedInPositions.length > 0) {
		return options.fn(this);
	}
	return options.inverse(this);
};