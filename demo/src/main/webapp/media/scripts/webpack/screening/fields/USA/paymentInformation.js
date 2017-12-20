export const fields = {
	billingState: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Pick a state'
			}
		},
		required: true,
		submitAs: 'state'
	},
	billingZip: {
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
		required: true,
		submitAs: 'postalCode'
	}
};

export default fields;
