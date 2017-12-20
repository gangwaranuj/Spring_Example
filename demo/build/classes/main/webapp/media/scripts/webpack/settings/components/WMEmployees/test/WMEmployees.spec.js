import React from 'react';
import { shallow } from 'enzyme';
import {
	WMHeading,
	WMFlatButton,
	WMIconButton,
	WMFontIcon,
	WMDrawer,
	WMAppBar
} from '@workmarket/front-end-components';
import NavigationClose from 'material-ui/svg-icons/navigation/close';
import {
	WMEmployeeTable
} from '@workmarket/front-end-patterns';
import WMEmployees from '../template';
import WMAddEmployees from '../../WMAddEmployees';
import styles from '../styles';

const getId = id => `[data-attr-id="wm-employees__${id}"]`;

describe('<WMEmployees />', () => {
	let wrapper;
	const employees = [
		{ name: 'Tim McClure', roles: [], lastActive: new Date() }
	];

	describe('Rendering', () => {
		beforeEach(() => {
			wrapper = shallow(
				<WMEmployees
					employees={ employees }
					employeeAddCount={ 0 }
					bulkEmployeeAddCount={ 0 }
					getEmployees={ () => {} }
				/>
			);
		});

		describe('Wrapper', () => {
			it('should exist', () => {
				const component = wrapper.find(getId('wrapper'));
				expect(component.length).toEqual(1);
			});

			it('should have style', () => {
				const component = wrapper.find(getId('wrapper'));
				expect(component.prop('style')).toEqual(styles.page);
			});
		});

		describe('Navigation', () => {
			it('should exist', () => {
				const component = wrapper.find(getId('navigation'));
				expect(component.length).toEqual(1);
			});

			it('should have style', () => {
				const component = wrapper.find(getId('navigation'));
				expect(component.prop('style')).toEqual(styles.navigation);
			});

			it('should have two (2) buttons', () => {
				const buttons = wrapper.find(getId('navigation')).find(WMFlatButton);
				expect(buttons.length).toEqual(2);
			});

			it('should have two (2) buttons with style', () => {
				const buttons = wrapper.find(getId('navigation')).find(WMFlatButton);
				buttons.forEach(button => expect(button.prop('style')).toEqual(styles.button));
			});

			it('should both go to "/settings/onboarding"', () => {
				const buttons = wrapper.find(getId('navigation')).find(WMFlatButton);
				buttons.forEach(button => expect(button.prop('href')).toEqual('/settings/onboarding'));
			});

			describe('Back', () => {
				it('should say "BACK"', () => {
					const button = wrapper.find(getId('navigation')).find(WMFlatButton).at(0);
					expect(button.prop('label')).toEqual('BACK');
				});

				it('should have a left chevron icon', () => {
					const button = wrapper.find(getId('navigation')).find(WMFlatButton).at(0);
					expect(button.prop('icon').type).toEqual(WMFontIcon);
					expect(button.prop('icon').props.children).toEqual('chevron_left');
				});
			});

			describe('Done', () => {
				it('should say "DONE ADDING EMPLOYEES"', () => {
					const button = wrapper.find(getId('navigation')).find(WMFlatButton).at(1);
					expect(button.prop('label')).toEqual('DONE ADDING EMPLOYEES');
				});

				it('should have a right chevron icon', () => {
					const button = wrapper.find(getId('navigation')).find(WMFlatButton).at(1);
					expect(button.prop('icon').type).toEqual(WMFontIcon);
					expect(button.prop('icon').props.children).toEqual('chevron_right');
				});
			});
		});

		describe('Heading', () => {
			it('should exist', () => {
				const component = wrapper.find(WMHeading);
				expect(component.length).toEqual(1);
			});

			it('should have an "Add" button', () => {
				const component = wrapper.find(WMHeading).find(WMFlatButton);
				expect(component).toHaveLength(1);
				expect(component.prop('label')).toEqual('+ ADD');
			});
		});

		describe('<WMEmployeeTable />', () => {
			it('should exist', () => {
				const component = wrapper.find(WMEmployeeTable);
				expect(component.length).toEqual(1);
			});

			it('should have employees', () => {
				const component = wrapper.find(WMEmployeeTable);
				expect(component.prop('employees')).toEqual(employees);
			});
		});

		describe('Drawer', () => {
			it('should exist', () => {
				const component = wrapper.find(WMDrawer);
				expect(component.length).toEqual(1);
			});

			it('should have an <WMAppBar />', () => {
				const component = wrapper.find(WMDrawer).find(WMAppBar);
				expect(component.length).toEqual(1);
			});

			it('should have an <WMAppBar /> with a title', () => {
				const component = wrapper.find(WMDrawer).find(WMAppBar);
				expect(component.prop('title')).toEqual('Add Employees');
			});

			it('should have an <WMAppBar /> with a close button', () => {
				const appBar = wrapper.find(WMDrawer).find(WMAppBar);
				expect(appBar.prop('iconElementRight').type).toEqual(WMIconButton);
				expect(appBar.prop('iconElementRight').props.children.type).toEqual(NavigationClose);
			});

			it('should have an <WMAddEmployees />', () => {
				const component = wrapper.find(WMDrawer).find(WMAddEmployees);
				expect(component.length).toEqual(1);
			});
		});
	});

	describe('behavior', () => {
		describe('Add Button', () => {
			it('should open the drawer', () => {
				const component = wrapper.find(getId('add'));

				component.simulate('click');

				expect(wrapper.state().isAddEmployeesVisible).toBeTruthy();
				expect(wrapper.find(WMDrawer).prop('open')).toBeTruthy();
			});

			it('should set messaging flags to `false`', () => {
				const component = wrapper.find(getId('add'));

				component.simulate('click');

				expect(wrapper.state().bulkEmployeeAddSuccess).toBeFalsy();
				expect(wrapper.state().employeeAddSuccess).toBeFalsy();
			});
		});

		describe('#handleDrawerRequestChange', () => {
			it('should set `isAddEmployeesVisible` to `true` if `true` passed as param', () => {
				wrapper.instance().handleDrawerRequestChange(true);
				expect(wrapper.state().isAddEmployeesVisible).toBeTruthy();
			});

			it('should set `isAddEmployeesVisible` to `false` if `false` passed as param', () => {
				wrapper.instance().handleDrawerRequestChange(false);
				expect(wrapper.state().isAddEmployeesVisible).toBeFalsy();
			});

			it('should set messaging flags to `false`', () => {
				wrapper.instance().handleDrawerRequestChange(true);
				expect(wrapper.state().bulkEmployeeAddSuccess).toBeFalsy();
				expect(wrapper.state().employeeAddSuccess).toBeFalsy();
			});
		});

		describe('Messaging', () => {
			it('should show a success message when an employee has been added successfully', () => {
				wrapper.setState({ isAddEmployeesVisible: false, employeeAddSuccess: true });

				const message = wrapper.find(getId('single-add-success'));
				expect(message.length).toEqual(1);
			});

			it('should show a success message when a CSV file has been uploaded successfully', () => {
				wrapper.setState({ isAddEmployeesVisible: false, bulkEmployeeAddSuccess: true });

				const message = wrapper.find(getId('bulk-add-success'));
				expect(message.length).toEqual(1);
			});
		});
	});
});
