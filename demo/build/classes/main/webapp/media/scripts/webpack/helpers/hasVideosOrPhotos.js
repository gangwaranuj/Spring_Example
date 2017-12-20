export default function (hasVideo, hasPhoto, options) {
	if (!!hasVideo || !!hasPhoto) {
		return options.fn(this);
	}
	return options.inverse(this);
};
