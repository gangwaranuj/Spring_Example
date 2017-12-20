import { connect } from 'react-redux';
import * as actions from '../../actions';
import template from './template';

export const mapStateToProps = (state) => {
	return {
		info: state
	};
};

export const mapDispatchToProps = (dispatch) => {
	return {
		onChangeField: (name, value) => {
			dispatch(actions.changeField(name, value));
		},
		onCheckField: () => {
			dispatch(actions.checkField());
		},
		onBlurField: (name) => {
			dispatch(actions.blurField(name));
		}
	};
};

const WMPaymentInformation = connect(
	mapStateToProps,
	mapDispatchToProps
)(template);

export default WMPaymentInformation;
