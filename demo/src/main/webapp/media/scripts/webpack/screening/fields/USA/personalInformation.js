export const fields = {
	state: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Pick a state'
			}
		},
		submitAs: 'state'
	},
	zip: {
		defaultValue: '',
		filter: (zip) => zip.replace(/\D/g, '').slice(0, 9),
		constraints: {
			presence: {
				message: 'Enter a valid zip code'
			},
			format: {
				pattern: /\d{5}(\d{4})?/,
				message: 'Enter a valid zip code'
			}
		},
		submitAs: 'postalCode'
	},
	SSN: {
		defaultValue: '',
		filter: (ssn) => ssn.replace(/\D/g, '').slice(0, 9),
		constraints: {
			americanSSN: true,
			presence: {
				message: 'Enter a valid social security number'
			},
			format: {
				pattern: /^\d+$/,
				message: 'Enter a valid social security number'
			},
			length: {
				is: 9,
				wrongLength: 'Enter a valid social security number'
			}
		},
		submitAs: 'workIdentificationNumber',
		required: true
	}
};

export default fields;
