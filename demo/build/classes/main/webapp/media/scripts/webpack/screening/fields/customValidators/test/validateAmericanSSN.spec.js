import validate from 'validate.js';
import validateAmericanSSN from '../validateAmericanSSN';

validate.validators.americanSSN = validateAmericanSSN;

describe('Validate American Social Security Number (SSN)', () => {
	const validSSN = '574-53-5391';
	const inValidSSN = '900-52-8980';
	const inValidSameDigitSSN = '111-11-1111';
	const inValidSuffixSSN = '900-52-0000';
	const inValidSequentialSSN = '123-45-6789';
	const shortSSN = '574';
	const longSSN = '574-53-53911111';
	const errorMessage = 'Enter a valid social security number';
	const validationRules = {
		SSN: {
			americanSSN: true
		}
	};

	describe('(valid)', () => {
		it('should return `undefined` on valid SSN', () => {
			const toBeValidated = {
				SSN: validSSN
			};
			const validationResult = validate(
				toBeValidated,
				validationRules,
				{ fullMessages: false }
			);

			expect(validationResult).toBeUndefined();
		});
	});

	describe('(invalid)', () => {
		let validationResult;

		beforeEach(() => {
			const toBeValidated = {
				SSN: inValidSSN
			};
			validationResult = validate(
				toBeValidated,
				validationRules,
				{ fullMessages: false }
			);
		});

		it('should return an object', () => {
			expect(typeof validationResult).toBe('object');
		});

		it('should return an object with "SSN"', () => {
			expect(validationResult).toHaveProperty('SSN');
		});

		it('should have "SSN as an array"', () => {
			expect(Array.isArray(validationResult.SSN)).toBeTruthy();
		});

		it('should have error messages as strings', () => {
			expect(typeof validationResult.SSN[0]).toBe('string');
			expect(validationResult.SSN[0]).toEqual(errorMessage);
		});

		it('should have error messages for SSNs with all the same digit', () => {
			const toBeValidated = {
				SSN: inValidSameDigitSSN
			};
			validationResult = validate(
				toBeValidated,
				validationRules,
				{ fullMessages: false }
			);

			expect(validationResult.SSN[0]).toEqual(errorMessage);
		});

		it('should have error messages for sequential SSNs', () => {
			const toBeValidated = {
				SSN: inValidSequentialSSN
			};
			validationResult = validate(
				toBeValidated,
				validationRules,
				{ fullMessages: false }
			);

			expect(validationResult.SSN[0]).toEqual(errorMessage);
		});

		it('should have error messages for invalid suffixes', () => {
			const toBeValidated = {
				SSN: inValidSuffixSSN
			};
			validationResult = validate(
				toBeValidated,
				validationRules,
				{ fullMessages: false }
			);

			expect(validationResult.SSN[0]).toEqual(errorMessage);
		});

		it('should have error messages for short SSNs', () => {
			const toBeValidated = {
				SSN: shortSSN
			};
			validationResult = validate(
				toBeValidated,
				validationRules,
				{ fullMessages: false }
			);

			expect(validationResult.SSN[0]).toEqual(errorMessage);
		});

		it('should have error messages for short SSNs', () => {
			const toBeValidated = {
				SSN: longSSN
			};
			validationResult = validate(
				toBeValidated,
				validationRules,
				{ fullMessages: false }
			);

			expect(validationResult.SSN[0]).toEqual(errorMessage);
		});
	});
});
