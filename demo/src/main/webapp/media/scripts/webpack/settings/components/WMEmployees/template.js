/* eslint-disable react/require-default-props */
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import {
	WMHeading,
	WMFlatButton,
	WMIconButton,
	WMFontIcon,
	WMDrawer,
	WMMessageBanner,
	WMAppBar
} from '@workmarket/front-end-components';
import NavigationClose from 'material-ui/svg-icons/navigation/close';
import {
	WMEmployeeTable
} from '@workmarket/front-end-patterns';
import styles from './styles';
import WMAddEmployees from '../WMAddEmployees';

class WMEmployees extends Component {
	constructor () {
		super();

		this.state = {
			isAddEmployeesVisible: false,
			employeeAddSuccess: false,
			bulkEmployeeAddSuccess: false
		};
	}

	componentDidMount () {
		this.props.getEmployees();
	}

	componentWillReceiveProps (nextProps) {
		if (nextProps.employeeAddCount !== this.props.employeeAddCount) {
			this.props.getEmployees();

			this.setState({
				isAddEmployeesVisible: false,
				employeeAddSuccess: true
			});
		} else if (nextProps.bulkEmployeeAddCount !== this.props.bulkEmployeeAddCount) {
			this.setState({
				isAddEmployeesVisible: false,
				bulkEmployeeAddSuccess: true
			});
		}
	}

	openAddEmployees = () => {
		this.setState({
			isAddEmployeesVisible: true,
			bulkEmployeeAddSuccess: false,
			employeeAddSuccess: false
		});
	}

	handleDrawerRequestChange = (open) => {
		this.setState({
			isAddEmployeesVisible: open,
			bulkEmployeeAddSuccess: false,
			employeeAddSuccess: false
		});
	}

	render () {
		return (
			<div
				data-attr-id="wm-employees__wrapper"
				style={ styles.page }
			>
				<div
					data-attr-id="wm-employees__navigation"
					style={ styles.navigation }
				>
					<WMFlatButton
						primary
						href="/settings/onboarding"
						label="BACK"
						style={ styles.button }
						icon={
							<WMFontIcon
								id="onboarding-employees__done-icon"
								className="material-icons"
							>
								chevron_left
							</WMFontIcon>
						}
					/>
					<WMFlatButton
						primary
						href="/settings/onboarding"
						labelPosition="before"
						label="DONE ADDING EMPLOYEES"
						style={ styles.button }
						icon={
							<WMFontIcon
								id="onboarding-employees__done-icon"
								className="material-icons"
							>
								chevron_right
							</WMFontIcon>
						}
					/>
				</div>

				<WMHeading level="1">
					Employees
					{ ' ' }
					<WMFlatButton
						data-attr-id="wm-employees__add"
						primary
						label="+ ADD"
						style={ styles.button }
						onClick={ this.openAddEmployees }
					/>
				</WMHeading>

				{
					this.state.employeeAddSuccess && !this.state.isAddEmployeesVisible &&
						<WMMessageBanner
							status="success"
							data-attr-id="wm-employees__single-add-success"
						>
							Employee added successfully.
						</WMMessageBanner>
				}
				{
					this.state.bulkEmployeeAddSuccess && !this.state.isAddEmployeesVisible &&
						<WMMessageBanner
							status="success"
							data-attr-id="wm-employees__bulk-add-success"
						>
							Your CSV file was uploaded successfully.
							You will be notified once it has been processed.
						</WMMessageBanner>
				}

				<WMEmployeeTable
					employees={ this.props.employees }
				/>

				<WMDrawer
					open={ this.state.isAddEmployeesVisible }
					docked={ false }
					openSecondary
					width={ 800 }
					onRequestChange={ this.handleDrawerRequestChange }
				>
					<WMAppBar
						title="Add Employees"
						showMenuIconButton={ false }
						iconElementRight={
							<WMIconButton
								iconStyle={ styles.appBar.icon }
								onFocus={ () => this.handleDrawerRequestChange(false) }
							>
								<NavigationClose />
							</WMIconButton>
						}
					/>
					<div
						data-attr-id="wm-add-employees__wrapper"
						style={ styles.drawerContent }
					>
						<WMAddEmployees
							key={ this.props.employeeAddCount }
						/>
					</div>
				</WMDrawer>
			</div>
		);
	}
}

WMEmployees.propTypes = {
	employees: PropTypes.arrayOf(PropTypes.shape({
		name: PropTypes.string,
		roles: PropTypes.arrayOf(PropTypes.string),
		lastActive: PropTypes.instanceOf(Date)
	})),
	employeeAddCount: PropTypes.number,
	bulkEmployeeAddCount: PropTypes.number,
	getEmployees: PropTypes.func.isRequired
};

export default WMEmployees;
