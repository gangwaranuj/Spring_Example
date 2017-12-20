export default (limitedVisibility, lastName) => {
	if (!!limitedVisibility) {
		return lastName.substring(0,1);
	} else {
		return lastName;
	}
};