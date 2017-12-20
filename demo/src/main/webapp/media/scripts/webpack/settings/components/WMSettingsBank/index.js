import { connect } from 'react-redux';
import template from './template';
import * as actions from '../../actions';

const disabledFields = ['paymentMethod', 'country', 'bankAccountTypeCode', 'type'];

const mapDispatchToProps = (dispatch) => {
	return {
		onChangeField: (name, value) => {
			dispatch(actions.changeFundsField(name, value));
		},
		onBlurField: (name, value) => {
			dispatch(actions.blurFundsField(name, value));
		},
		onSubmitForm: (form) => {
			dispatch(actions.onSubmitFundsForm(form));
		}
	};
};

const mapStateToProps = (state) => {
	const isFormValid = () => {
		for (const [field] of state.funds.entries()) { // eslint-disable-line no-restricted-syntax
			if (field.get && typeof field.get === 'function') {
				if (
					(field.get('error') || !field.get('dirty')) &&
					disabledFields.indexOf(name) < 0
				) {
					return false;
				}
			}
		}

		return true;
	};

	return {
		info: state,
		formValid: isFormValid()
	};
};

const WMSettingsBank = connect(
	mapStateToProps,
	mapDispatchToProps
)(template);

export default WMSettingsBank;
