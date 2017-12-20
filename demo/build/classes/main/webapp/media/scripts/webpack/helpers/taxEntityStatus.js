export default (hasTaxEntity, hasVerifiedTaxEntity) => {
	if (!!hasVerifiedTaxEntity) {
		return '<p>Tax Information: <b>Verified</b></p>';
	} else if (!!hasTaxEntity) {
		return '<p>Tax Information: <b>Unverified</b></p>';
	}
};