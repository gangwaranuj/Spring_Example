import { connect } from 'react-redux';
import * as actions from '../../actions';
import template from './template';

const mapStateToProps = (state) => {
	const employees = state.settings.get('employees').map(({
		fullName,
		rolesString,
		latestActivityOn
	}) => {
		const employee = {
			name: fullName,
			roles: rolesString.split(', ')
		};

		if (latestActivityOn) {
			employee.lastActive = new Date(Number.parseInt(latestActivityOn, 10));
		}

		return employee;
	});

	return {
		employees,
		employeeAddCount: state.settings.get('employeeAddCount'),
		bulkEmployeeAddCount: state.settings.get('bulkEmployeeAddCount')
	};
};

const mapDispatchToProps = dispatch => ({
	getEmployees: () => dispatch(actions.getEmployees())
});

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(template);
