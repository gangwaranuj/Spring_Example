export const fields = {
	billingProvince: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Pick a province'
			}
		},
		required: true
	},
	billingPostalCode: {
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
		required: true
	}
};

export default fields;
