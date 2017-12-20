export default function (qualityValue, options) {
	if (qualityValue !== 'Not applicable') {
		return options.fn(this);
	}
	return options.inverse(this);
};