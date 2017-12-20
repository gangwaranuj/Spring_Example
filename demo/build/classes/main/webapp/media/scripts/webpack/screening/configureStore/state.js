import { Map } from 'immutable';
import fields from '../fields';

let initialState = Map({
	isFormValid: false,
	submitting: false,
	submissionAttempt: false,
	internalError: false,
	paymentError: false
});

Object.keys(fields).forEach(name => {
	initialState = initialState.set(
		name,
		Map({
			value: fields[name].defaultValue,
			error: '',
			dirty: fields[name].defaultValue ? true : false,
			blurred: false
		})
	);
});

const populateFields = (value) =>
	Map({
		value,
		error: '',
		dirty: Boolean(value),
		blurred: false
	});

export { initialState };

export const processDrugConfig = ({
	price,
	firstName,
	lastName,
	address1,
	address2,
	city,
	state,
	country,
	email,
	postalCode,
	availableFunds,
	isInternational,
	drugTestPassed,
	drugTestFailed,
	drugTestPending
}) =>
	Map({
		price,
		firstName: populateFields(firstName),
		lastName: populateFields(lastName),
		address1: populateFields(address1),
		address2: populateFields(address2),
		city: populateFields(city),
		state: populateFields(state),
		country: populateFields(country),
		email: populateFields(email),
		zip: populateFields(postalCode),
		hasSufficientFunds: Number.parseFloat(availableFunds) >= Number.parseFloat(price),
		isInternational,
		drugTestPassed,
		drugTestFailed,
		drugTestPending,
		canOrder: !isInternational && !drugTestPending
	});
