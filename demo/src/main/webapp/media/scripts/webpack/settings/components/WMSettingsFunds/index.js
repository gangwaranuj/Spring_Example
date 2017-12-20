import { connect } from 'react-redux';
import template from './template';
import * as actions from '../../actions';

const mapDispatchToProps = (dispatch) => {
	return {
		onChangeField: (name, value) => {
			dispatch(actions.changeCreditCardField(name, value));
		},
		onSubmitForm: (form) => {
			dispatch(actions.onSubmitAddFundsViaCreditCardForm(form));
		}
	};
};

const mapStateToProps = (state) => {
	return {
		info: state
	};
};

const WMSettingsFunds = connect(
	mapStateToProps,
	mapDispatchToProps
)(template);

export default WMSettingsFunds;
