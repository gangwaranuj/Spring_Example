import { connect } from 'react-redux';
import template from './template';
import * as actions from '../../actions';

const mapDispatchToProps = (dispatch) => {
	return {
		onChangeField: (name, value) => {
			dispatch(actions.changeTaxField(name, value));
		},
		onBlurField: (name, value) => {
			dispatch(actions.blurTaxField(name, value));
		},
		onSubmitForm: (form) => {
			dispatch(actions.onSubmitTaxForm(form));
		}
	};
};

const mapStateToProps = (state) => {
	const isFormValid = () => {
		for (const [field] of state.tax.entries()) { // eslint-disable-line no-restricted-syntax
			if (field.get && typeof field.get === 'function') {
				if (field.get('error') || !field.get('dirty')) {
					return false;
				}
			}
		}

		if (state.tax.getIn(['taxCountry', 'value']) === 'usa' && !state.tax.getIn(['deliveryPolicyFlag', 'value'])) {
			return false;
		}

		return true;
	};
	const { settings, tax } = state;
	return {
		info: {
			settings,
			tax
		},
		formValid: isFormValid()
	};
};

const WMSettingsTax = connect(
	mapStateToProps,
	mapDispatchToProps
)(template);

export default WMSettingsTax;
