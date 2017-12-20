const fundsFields = {
	type: {
		defaultValue: 'ach'
	},
	bankName: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Bank name is required.'
			}
		}
	},
	nameOnAccount: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Name on Account number is required.'
			}
		}
	},
	routingNumber: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Rounting number is required.'
			},
			length: {
				is: 9,
				wrongLength: 'Routing number must be 9 characters.'
			}
		}
	},
	accountNumber: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Account number is required.'
			},
			length: {
				minimum: 4,
				maximum: 17,
				wrongLength: 'Account number must be between 4 and 17 characters',
				message: 'Account number must be between 4 and 17 characters'
			},
			numericality: {
				onlyInteger: true,
				message: 'Account number can only contain numbers'
			}
		}
	},
	accountNumberConfirm: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Account number is required.'
			},
			length: {
				minimum: 4,
				maximum: 17,
				wrongLength: 'Account number must be between 4 and 17 characters',
				message: 'Account number must be between 4 and 17 characters'
			},
			numericality: {
				onlyInteger: true,
				message: 'Account number can only contain numbers'
			}
		}
	},
	paymentMethod: {
		defaultValue: 'Bank Account'
	},
	bankAccountTypeCode: {
		defaultValue: 'checking',
		constraints: {
			presence: {
				message: 'Account type is required.'
			}
		}
	},
	country: {
		defaultValue: 'USA'
	}
};

export default fundsFields;
