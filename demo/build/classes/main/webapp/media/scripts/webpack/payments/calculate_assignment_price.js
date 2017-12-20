'use strict';

import roundDecimals from '../helpers/roundDecimals.js'

const maxFee = 400;

export default ({
	type,
	mode,
	flatPrice,
	perHourPrice,
	maxNumberOfHours,
	perUnitPrice,
	maxNumberOfUnits,
	initialPerHourPrice,
	initialNumberOfHours,
	additionalPerHourPrice,
	maxBlendedNumberOfHours
}, fee) => {
	const flatPriceNum = Number.parseFloat(flatPrice);

	let grossPrice = 0,
		companyPrice, workerPrice;

	switch (type) {
		case 'FLAT':
			grossPrice = flatPriceNum || 0;
			break;
		case 'PER_HOUR':
			grossPrice = (perHourPrice || 0) * (maxNumberOfHours || 0);
			break;
		case 'PER_UNIT':
			grossPrice = (perUnitPrice || 0) * (maxNumberOfUnits || 0);
			break;
		case 'BLENDED_PER_HOUR':
			grossPrice = (initialPerHourPrice || 0) * (initialNumberOfHours || 0);
			if (additionalPerHourPrice) {
				grossPrice += additionalPerHourPrice * maxBlendedNumberOfHours;
			}
			break;
		default:
			grossPrice = flatPriceNum || 0;
			break;
	}

	const netCompanyPrice = grossPrice + Math.min(grossPrice * fee, maxFee);
	const netWorkerPrice = grossPrice / (1 + fee);

	companyPrice = mode === 'pay' ? roundDecimals(netCompanyPrice, 2) : roundDecimals(grossPrice, 2);
	workerPrice = mode === 'spend' ? netWorkerPrice : grossPrice;

	workerPrice = roundDecimals(Math.max(companyPrice - maxFee, workerPrice), 2);

	return { companyPrice, workerPrice, fee };
};
