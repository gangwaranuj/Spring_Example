import { connect } from 'react-redux';
import {
	WMAddEmployeeForm
} from '@workmarket/front-end-patterns';
import * as actions from '../../actions';

let workerRoleEnabled = false;
if (typeof config !== 'undefined' && typeof config.workerRoleEnabled !== 'undefined') {
	workerRoleEnabled = config.workerRoleEnabled;
}

const mapStateToProps = state => ({
	firstName: state.addEmployee.get('firstName'),
	lastName: state.addEmployee.get('lastName'),
	email: state.addEmployee.get('email'),
	workPhoneInternationalCode: state.addEmployee.get('workPhoneInternationalCode'),
	workPhone: state.addEmployee.get('workPhone'),
	workPhoneExtension: state.addEmployee.get('workPhoneExtension'),
	jobTitle: state.addEmployee.get('jobTitle'),
	industryId: state.addEmployee.get('industryId'),
	roleSettings: state.addEmployee.get('roleSettings'),
	permissionSettings: state.addEmployee.get('permissionSettings'),
	spendLimit: state.addEmployee.get('spendLimit'),
	errors: state.addEmployee.get('errors'),
	workerRoleCheckboxDisabled: state.addEmployee.get('workerRoleCheckboxDisabled'),
	workerRoleEnabled
});

const mapDispatchToProps = dispatch => ({
	onChange: (name, value) => dispatch(actions.changeAddEmployeeField(name, value)),
	workerDisabledCheck: () => dispatch(actions.workerDisabledCheck()),
	onSubmit: form => dispatch(actions.onSubmitAddEmployeeForm(form))
});

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(WMAddEmployeeForm);
