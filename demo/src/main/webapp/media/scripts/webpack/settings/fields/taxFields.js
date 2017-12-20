const taxNumberFilter = (value, state) => {
	let formattedValue = '';
	const { taxCountry } = state;
	if (taxCountry.value === 'usa') {
		formattedValue = value.replace(/\D/g, '');
		formattedValue = formattedValue.length > 2 ?
			`${formattedValue.slice(0, 2)}-${formattedValue.slice(2)}` :
			formattedValue;
		formattedValue = formattedValue.slice(0, 10);
	} else if (taxCountry.value === 'canada') {
		formattedValue = value.replace(/-/g, '');
		formattedValue = formattedValue.length > 9 ?
			`${formattedValue.slice(0, 9)}-${formattedValue.slice(9)}` :
			formattedValue;
		formattedValue = formattedValue.length > 12 ?
			`${formattedValue.slice(0, 12)}-${formattedValue.slice(12)}` :
			formattedValue;
		formattedValue = formattedValue.slice(0, 17);
	}
	return formattedValue;
};

const taxFields = {
	id: {
		defaultValue: null
	},
	companyNumber: {
		defaultValue: null
	},
	taxCountry: {
		defaultValue: 'usa'
	},
	activeFlag: {
		defaultValue: null
	},
	taxNumber: {
		defaultValue: '',
		filter: taxNumberFilter,
		constraints: {
			presence: {
				message: 'Tax number is required.'
			}
		}
	},
	taxEntityTypeCode: {
		defaultValue: 'c_corp'
	},
	activeDateString: {
		defaultValue: null
	},
	taxName: {
		defaultValue: ''
	},
	firstName: {
		defaultValue: ''
	},
	middleName: {
		defaultValue: ''
	},
	lastName: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Company name is required.'
			}
		}
	},
	businessFlag: {
		defaultValue: true
	},
	taxVerificationStatusCode: {
		defaultValue: null
	},
	verificationPending: {
		defaultValue: 1
	},
	deliveryPolicyFlag: {
		defaultValue: false
	},
	address: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Address is required.'
			}
		}
	},
	city: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'City is required.'
			}
		}
	},
	postalCode: {
		defaultValue: '',
		constraints: {
			presence: {
				message: 'Zip/Postal code is required.'
			}
		}
	},
	state: {
		defaultValue: ''
	},
	country: {
		defaultValue: ''
	},
	businessName: {
		defaultValue: ''
	},
	businessNameFlag: {
		defaultValue: false
	},
	effectiveDateString: {
		defaultValue: ''
	},
	signature: {
		defaultValue: ''
	},
	signatureDateString: {
		defaultValue: '',
		constraints: {
			dateTime: {
				dateOnly: true
			}
		}
	},
	countryOfIncorporation: {
		defaultValue: ''
	},
	foreignStatusAcceptedFlag: {
		defaultValue: false
	}
};

export default taxFields;
