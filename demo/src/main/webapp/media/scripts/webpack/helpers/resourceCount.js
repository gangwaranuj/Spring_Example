export default (collection) => {
	if (collection && collection.length > 0) {
		return collection.length;
	}
	return 0;
};