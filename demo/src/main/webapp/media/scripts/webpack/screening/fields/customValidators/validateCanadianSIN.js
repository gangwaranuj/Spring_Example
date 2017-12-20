/** Validator written for validate.js,
 * "Writing your own validator": https://validatejs.org/#custom-validator
 *
 * Validation scheme: https://en.wikipedia.org/wiki/Social_Insurance_Number#Validation
 */

const validateCanadianSIN = (value) => {
	const multipliers = [1, 2, 1, 2, 1, 2, 1, 2, 1];
	const sanitized = value.replace(/\D/g, '');

	if (sanitized.length !== 9) {
		return 'Enter a valid social insurance number';
	}

	const result = sanitized.split('')
		.map((digit, index) => {
			let product = digit * multipliers[index];

			if (product > 9) {
				product = product
					.toString()
					.split('')
					.map(productDigit => Number(productDigit))
					.reduce((total, productDigit) => total + productDigit, 0);
			}

			return product;
		})
		.reduce((total, digit) => total + digit, 0);

	if (result % 10 !== 0) {
		return 'Enter a valid social insurance number';
	}

	return undefined;
};

export default validateCanadianSIN;
