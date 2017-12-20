export const fields = {
	paymentType: {
		defaultValue: 'cc',
		required: true,
		constraints: {
			presence: {
				message: 'Enter a valid payment type'
			}
		}
	},
	firstNameOnCard: {
		filter: firstNameOnCard => firstNameOnCard.slice(0, 50),
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Enter a valid first name'
			}
		},
		required: true
	},
	lastNameOnCard: {
		filter: lastNameOnCard => lastNameOnCard.slice(0, 50),
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Enter a valid last name'
			}
		},
		required: true
	},
	cardNumber: {
		defaultValue: '',
		filter: (cardNumber) => {
			const filtered = cardNumber.replace(/\D/g, '');

			if ((/^(34|37).*$/).test(filtered)) {
				return filtered.slice(0, 15);
			}

			return filtered.slice(0, 16);
		},
		constraints: {
			presence: {
				message: 'Enter a valid credit card number'
			},
			format: {
				pattern: /^(34|37|4|5[1-5]).*$/,
				message: 'Enter a valid credit card number'
			},
			length: (value) => {
				if (value) {
					if ((/^(34|37).*$/).test(value)) return { is: 15 };
					if ((/^(4|5[1-5]).*$/).test(value)) return { is: 16 };
				}
				return { wrongLength: 'Enter a valid credit card number' };
			}
		},
		required: true
	},
	cardType: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Credit card type is required.'
			}
		},
		required: true
	},
	cardSecurityCode: {
		defaultValue: '',
		filter: securityCode => securityCode.replace(/\D/g, '').slice(0, 4),
		constraints: {
			presence: {
				message: 'Enter a valid security code'
			},
			format: {
				pattern: /^\d+$/,
				message: 'Enter a valid security code'
			},
			length: {
				minimum: 3,
				maximum: 4,
				tooShort: 'Enter a valid security code',
				tooLong: 'Enter a valid security code',
				wrongLength: 'Enter a valid security code'
			}
		},
		required: true
	},
	cardExpirationMonth: {
		constraints: {
			presence: {
				message: 'Pick a month'
			}
		},
		required: true
	},
	cardExpirationYear: {
		constraints: {
			presence: {
				message: 'Pick a year'
			}
		},
		required: true
	},
	billingAddress1: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Enter a valid address'
			}
		},
		required: true,
		submitAs: 'address1'
	},
	billingAddress2: {
		defaultValue: '',
		submitAs: 'address2'
	},
	billingCity: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Enter a valid city'
			}
		},
		required: true,
		submitAs: 'city'
	},
	billingCountry: {
		defaultValue: 'USA',
		constraints: {
			presence: {
				message: 'Pick a country'
			}
		},
		required: true,
		submitAs: 'country'
	}
};

export default fields;
