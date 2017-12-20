export default function (type, options) {
	if (type === 'ASSET') {
		return options.fn(this);
	} else if (type === 'LINK') {
		return options.inverse(this);
	}
};