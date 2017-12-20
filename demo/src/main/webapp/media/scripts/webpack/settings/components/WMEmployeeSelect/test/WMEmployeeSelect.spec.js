import 'isomorphic-fetch';
import React from 'react';
import { shallow } from 'enzyme';
import nock from 'nock';
import {
	WMSelectField,
	WMMenuItem
} from '@workmarket/front-end-components';
import WMEmployeeSelect from '../';


describe('<WMEmployeeSelect />', () => {
	const shallowRenderComponent = () =>
		shallow(
			<WMEmployeeSelect />
		);

	const setupMockServer = ({ response }) => {
		return nock('http://localhost:8080')
			.get(/(\/companies\/).*/)
			.reply(200, response);
	};

	describe('Rendering ::', () => {
		it('should contain <WMSelectField />', () => {
			const wrapper = shallowRenderComponent();
			expect(wrapper.find(WMSelectField).length).toEqual(1);
		});

		it('should contain a <WMMenuItem /> with employee info', () => {
			const wrapper = shallowRenderComponent();
			const employeeList = [
				{
					id: 45,
					fullName: 'Guy Fieri'
				}
			];
			wrapper.setState({ employeeList });
			const menuItem = wrapper.find(WMMenuItem);
			expect(menuItem.length).toEqual(1);
			expect(menuItem.prop('value')).toEqual(employeeList[0].id);
			expect(menuItem.prop('primaryText')).toEqual(employeeList[0].fullName);
			expect(menuItem.prop('secondaryText')).toEqual(`ID: ${employeeList[0].id}`);
		});
	});

	it('should fetch employee list on mount', () => {
		const employeeList = [
			{
				id: 45,
				fullName: 'Guy Fieri'
			}
		];
		const mockServer = setupMockServer({
			response: employeeList
		});
		const wrapper = shallowRenderComponent();
		wrapper.instance().componentDidMount('http://localhost:8080');
		mockServer.isDone();
	});
});
