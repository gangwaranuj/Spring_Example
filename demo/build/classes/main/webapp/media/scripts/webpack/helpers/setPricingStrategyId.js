'use strict';

export default (type) => {
	var pricingId;
	switch (type) {
		case 'FLAT':
			pricingId = 1;
			break;
		case 'PER_HOUR':
			pricingId = 2;
			break;
		case 'PER_UNIT':
			pricingId = 3;
			break;
		case 'BLENDED_PER_HOUR':
			pricingId = 4;
			break;
		case 'BLENDED_PER_UNIT':
			pricingId = 5;
			break;
		case 'NONE':
			pricingId = 6;
			break;
		case 'INTERNAL':
			pricingId = 7;
			break;
	}
	return pricingId;
};
