export default function (esignatures, options) {
	esignatures = esignatures || [];
	if (esignatures.length > 0) {
		return options.fn(this);
	}
	return options.inverse(this);
};
