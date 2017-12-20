import PropTypes from 'prop-types';
import React from 'react';
import { WMSelectField, WMMenuItem } from '@workmarket/front-end-components';
import Application from '../../../core';

class WMEmployeeSelect extends React.Component {
	constructor (props) {
		super(props);
		this.state = { employeeList: [] };
	}

	componentDidMount (
		urlRoot = ''
	) {
		const { companyId } = Application.UserInfo;
		fetch(`${urlRoot}/companies/${companyId}/employees`, { credentials: 'same-origin' })
		.then(res => res.json())
		.then((res) => {
			this.setState({ employeeList: res });
		});
	}

	render () {
		const { employeeList } = this.state;
		const renderEmployees = employeeList.map(employee => (
			<WMMenuItem
				key={ employee.id }
				value={ employee.id }
				primaryText={ employee.fullName }
				secondaryText={ `ID: ${employee.id}` }
			/>)
		);

		return (
			<WMSelectField
				onChange={
					(event, index, value) => this.props.onSelectChange(event, index, value)
				}
				{ ...this.props }
			>
				{ renderEmployees }
			</WMSelectField>
		);
	}
}

export default WMEmployeeSelect;

WMEmployeeSelect.propTypes = {
	onSelectChange: PropTypes.func.isRequired
};
