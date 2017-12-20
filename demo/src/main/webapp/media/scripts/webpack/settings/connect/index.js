import * as actions from './../actions';

export const mapDispatchToProps = (dispatch) => {
	return {
		getOnboardingProgress: () => {
			dispatch(actions.getOnboardingProgress());
		},
		onChangeField: (name, value) => {
			dispatch(actions.changeField(name, value));
		},
		onChangeLocationField: (name, value) => {
			dispatch(actions.changeLocationField(name, value));
		},
		onChangeFundsField: (name, value) => {
			dispatch(actions.changeFundsField(name, value));
		},
		onGoogleAPILoaded: () => {
			dispatch(actions.googleAPILoaded());
		},
		onChangeGoogleAddress: (addressObj) => {
			Object.keys(addressObj).forEach((name) => {
				dispatch(actions.changeLocationField(name, addressObj[name]));
			});
		},
		onSubmitProfileForm: (form) => {
			dispatch(actions.onSubmitProfileForm(form));
		},
		onSubmitFundsForm: (form) => {
			dispatch(actions.onSubmitFundsForm(form));
		},
		onSubmitTaxForm: (form) => {
			dispatch(actions.onSubmitTaxForm(form));
		},
		getCompanyProfileInfo: () => {
			dispatch(actions.getCompanyProfileInfo());
		},
		getEmployeeList: () => {
			dispatch(actions.getEmployeeList());
		}
	};
};

export const mapStateToProps = info => ({ info });
