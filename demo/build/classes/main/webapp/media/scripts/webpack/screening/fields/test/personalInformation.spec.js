import fields from '../USA/personalInformation';

describe('Personal Information Field Definitions', () => {
	describe('Filters', () => {
		describe('SSN', () => {
			const filter = fields.SSN.filter;

			it('should filter out anything that is not a digit', () => {
				let numbers = '5234';
				let result = filter(`gfdgsdfubsdy${numbers}bsdfbkfjgdfg*&%$%^`);
				expect(result).toEqual(numbers);
			});
		});

		// TODO: Canadian SSN
	});
});
