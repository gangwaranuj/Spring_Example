import fields from '../core/paymentInformation';

describe('Payment Information Field Definitions', () => {
	describe('Filters', () => {
		describe('Card Number', () => {
			const filter = fields.cardNumber.filter;

			it('should filter out anything that is not a digit', () => {
				let numbers = '333';
				let result = filter(`gfdgsdfubsdy${numbers}bsdfbkfjgdfg*&%$%^`);
				expect(result).toEqual(numbers);
			});

			it('should truncate the input to 16 digits', () => {
				let numbers = '444444444444444444444444444444444444';
				let result = filter(`gfdgsdfubsdy${numbers}bsdfbkfjgdfg*&%$%^`);
				expect(result).toEqual(numbers.slice(0, 16));
			});

			it('should truncate the input to 15 digits (if American Express)', () => {
				let numbers = '345555555555555555555555555555555555';
				let result = filter(`gfdgsdfubsdy${numbers}bsdfbkfjgdfg*&%$%^`);
				expect(result).toEqual(numbers.slice(0, 15));
			});
		});

		describe('Security Code', () => {
			const filter = fields.cardSecurityCode.filter;

			it('should filter out anything that is not a digit', () => {
				let numbers = '4242';
				let result = filter(`gfdgsdfubsdy${numbers}bsdfbkfjgdfg*&%$%^`);
				expect(result).toEqual(numbers);
			});

			it('should truncate the input to 4 digits', () => {
				let numbers = '4242424242424242';
				let result = filter(`gfdgsdfubsdy${numbers}bsdfbkfjgdfg*&%$%^`);
				expect(result).toEqual(numbers.slice(0, 4));
			});
		});
	});
});
