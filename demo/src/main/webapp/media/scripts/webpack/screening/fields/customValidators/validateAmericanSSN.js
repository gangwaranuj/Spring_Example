/** Validator written for validate.js,
 * "Writing your own validator": https://validatejs.org/#custom-validator
 */

const INVALID_SSN_PREFIXES = ['000', '666', '9'];
const INVALID_SSN_SUFFIXES = ['0000'];

const validateAmericanSSN = (value) => {
	const sanitized = value.replace(/\D/g, '');
	const errorMessage = 'Enter a valid social security number';

	if (sanitized.length !== 9) {
		return errorMessage;
	}

	const isAllSameDigit = sanitized.match(/(\d)\1{8}/);

	if (isAllSameDigit) {
		return errorMessage;
	}

	const hasInvalidPrefix = INVALID_SSN_PREFIXES.some((prefix) => {
		return sanitized.indexOf(prefix) === 0;
	});

	if (hasInvalidPrefix) {
		return errorMessage;
	}

	const hasInvalidSuffix = INVALID_SSN_SUFFIXES.some((suffix) => {
		return sanitized.indexOf(suffix) === 5;
	});

	if (hasInvalidSuffix) {
		return errorMessage;
	}

	const isSequential = sanitized.match(/123456789/);

	if (isSequential !== null) {
		return errorMessage;
	}

	const isReserved = sanitized.match(/98765432\d/);

	if (isReserved !== null) {
		return errorMessage;
	}

	return undefined;
};

export default validateAmericanSSN;
