export default function (onSiteHourlyRate, offSiteHourlyRate, options) {
	if (onSiteHourlyRate > 0 || offSiteHourlyRate > 0) {
		return options.fn(this);
	}

	return options.inverse(this);
};