export const fields = {
	firstName: {
		filter: firstName => firstName.slice(0, 50),
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Enter a valid first name'
			}
		},
		required: true
	},
	lastName: {
		filter: lastName => lastName.slice(0, 50),
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Enter a valid last name'
			}
		},
		required: true
	},
	address1: {
		filter: address1 => address1.slice(0, 200),
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Enter a valid street address'
			}
		},
		required: true
	},
	address2: {
		filter: address2 => address2.slice(0, 100),
		defaultValue: ''
	},
	city: {
		filter: city => city.slice(0, 100),
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Enter a valid city'
			}
		},
		required: true
	},
	country: {
		defaultValue: 'USA',
		constraints: {
			presence: {
				message: 'Pick a country'
			}
		},
		required: true
	},
	birthDay: {
		constraints: {
			presence: {
				message: 'Pick a day'
			}
		},
		required: true
	},
	birthMonth: {
		constraints: {
			presence: {
				message: 'Pick a month'
			}
		},
		required: true
	},
	birthYear: {
		constraints: {
			presence: {
				message: 'Pick a year'
			}
		},
		required: true
	},
	email: {
		filter: email => email.slice(0, 100),
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Enter a valid email address'
			},
			email: {
				message: 'Enter a valid email address'
			}
		},
		required: true
	}
};

export default fields;
