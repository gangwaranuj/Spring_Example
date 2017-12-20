import { connect } from 'react-redux';
import Radium from 'radium';
import template from './template';
import * as actions from '../../actions';
import * as types from '../../constants/actionTypes';

const mapStateToProps = (state) => {
	return {
		state
	};
};

const mapDispatchToProps = (dispatch) => ({
	onDisabledSubmitForm: (isFormValid) => {
		if (!isFormValid) {
			dispatch({
				type: types.VALIDATE_ALL_FIELDS
			});
		}
	},
	onSubmitForm: (form) => {
		dispatch(actions.submitForm({
			form,
			url: '/worker/v2/services/drug-test'
		}));
	}
});

const WMCheckout = connect(
	mapStateToProps,
	mapDispatchToProps
)(template);

export default Radium(connect()(WMCheckout));
