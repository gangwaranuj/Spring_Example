export const fields = {
	province: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Pick a province'
			}
		},
		submitAs: 'state',
		required: true
	},
	postalCode: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Enter a valid postal code'
			},
			format: {
				pattern: /^[ABCEGHJKLMNPRSTVXY]\d[ABCEGHJKLMNPRSTVWXYZ]( )?\d[ABCEGHJKLMNPRSTVWXYZ]\d$/,
				message: 'Enter a valid postal code'
			}
		},
		submitAs: 'postalCode',
		required: true
	},
	SIN: {
		defaultValue: '',
		filter: (ssn) => ssn.replace(/\D/g, '').slice(0, 9),
		constraints: {
			canadianSIN: true,
			presence: {
				message: 'Enter a valid social insurance number'
			}
		},
		submitAs: 'workIdentificationNumber',
		required: true
	}
};

export default fields;
