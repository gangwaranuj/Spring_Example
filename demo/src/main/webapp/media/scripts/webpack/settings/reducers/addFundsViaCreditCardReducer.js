import { Map } from 'immutable';
import * as types from '../constants/actionTypes';

const initialCreditCardFundsState = Map({
	cardNumber: '',
	nameOnCard: '',
	expirationMonth: (new Date()).getUTCMonth() + 1,
	expirationYear: (new Date()).getUTCFullYear(),
	securityCode: '',
	fundsToAdd: '',
	billingAddress1: '',
	billingAddress2: '',
	billingCity: '',
	billingProvince: '',
	billingPostalCode: '',
	billingState: '',
	billingZip: '',
	billingCountry: 'USA'
});
export { initialCreditCardFundsState };

const creditCardFunds = (state = Map(initialCreditCardFundsState), { type, name, value }) => {
	switch (type) {
	case types.CHANGE_CREDIT_CARD_FIELD:
		return state.set(name, value);

	default:
		return state;
	}
};

export default creditCardFunds;
