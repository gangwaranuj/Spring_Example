import validate from 'validate.js';
import validateCanadianSIN from '../validateCanadianSIN';

validate.validators.canadianSIN = validateCanadianSIN;

describe('Validate Canadian Social Insurance Number (SIN)', () => {
	const validSIN = '046 454 286';
	const invalidSIN = '146 454 286';
	const shortSIN = '046';
	const longSIN = '046 454 286 111';
	const errorMessage = 'Enter a valid social insurance number';
	const validationRules = {
		SIN: {
			canadianSIN: true
		}
	};

	describe('(valid)', () => {
		it('should return `undefined` on valid SIN', () => {
			const toBeValidated = {
				SIN: validSIN
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
				SIN: invalidSIN
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

		it('should return an object with "SIN"', () => {
			expect(validationResult).toHaveProperty('SIN');
		});

		it('should have "SIN as an array"', () => {
			expect(Array.isArray(validationResult.SIN)).toBeTruthy();
		});

		it('should have error messages as strings', () => {
			expect(typeof validationResult.SIN[0]).toBe('string');
			expect(validationResult.SIN[0]).toEqual(errorMessage);
		});

		it('should have error messages for short SINs', () => {
			const toBeValidated = {
				SIN: shortSIN
			};
			validationResult = validate(
				toBeValidated,
				validationRules,
				{ fullMessages: false }
			);

			expect(validationResult.SIN[0]).toEqual(errorMessage);
		});

		it('should have error messages for short SINs', () => {
			const toBeValidated = {
				SIN: longSIN
			};
			validationResult = validate(
				toBeValidated,
				validationRules,
				{ fullMessages: false }
			);

			expect(validationResult.SIN[0]).toEqual(errorMessage);
		});
	});
});
