import React from 'react';
import nock from 'nock';
import { shallow } from 'enzyme';
import { WMRaisedButton, WMMenuItem } from '@workmarket/front-end-components';
import LicenseRequirement from '../LicenseRequirement';
import { initialState } from '../../../reducers';

const componentState = initialState.getIn(['requirementsData', 'license']);

const shallowRenderComponent = (
	handleChange = () => {},
	applyRequirement = () => {},
	license = componentState
) => shallow(
	<LicenseRequirement.WrappedComponent
		handleChange={ handleChange }
		applyRequirement={ applyRequirement }
		license={ license }
	/>
);

const setupMockServer = ({ response }) => {
	return nock('http://localhost:8080')
		.get(/.*/)
		.reply(200, response);
};

describe('<LicenseRequirement />', () => {
	describe('Rendering', () => {
		describe('State Select', () => {
			it('should render 1 <WMFormRow />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="requirements_stateRow"]');
				expect(component).toHaveLength(1);
			});

			it('should render 1 <WMStateProvince />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="requirements_stateSelect"]');
				expect(component).toHaveLength(1);
			});
		});

		describe('License Select when state is selected', () => {
			it('should render 1 <WMFormRow />', () => {
				const wrapper = shallowRenderComponent();
				wrapper.setState({ stateSelected: 'state' });
				const component = wrapper.find('[data-component-identifier="requirements_licenseRow"]');
				expect(component).toHaveLength(1);
			});

			it('should render 1 <WMSelectField />', () => {
				const wrapper = shallowRenderComponent();
				wrapper.setState({ stateSelected: 'state' });
				const component = wrapper.find('[data-component-identifier="requirements_licenseSelect"]');
				expect(component).toHaveLength(1);
			});

			it('should render 1 <WMMenuItem /> with license info />', () => {
				const wrapper = shallowRenderComponent();
				wrapper.setState({ stateSelected: 'state' });
				const licenseList = [
					{
						id: 2222,
						name: 'Single License'
					}
				];
				wrapper.setState({ licenseList });
				const component = wrapper.find('[data-component-identifier="requirements_licenseSelect"]');
				const menuItem = component.find(WMMenuItem);
				expect(menuItem.prop('value')).toEqual(licenseList[0].id);
				expect(menuItem.prop('primaryText')).toEqual(licenseList[0].name);
			});

			// TODO: @artivilla figure out why this doesn't update the state
			xit('should fetch license list', () => {
				const licenseList = [
					{
						id: 3424,
						name: 'Single Licence'
					}
				];
				const mockServer = setupMockServer({
					reponse: licenseList
				});
				const wrapper = shallowRenderComponent();
				wrapper.setState({ licenseList });
				mockServer.isDone();
			});
		});

		describe('Actions', () => {
			let wrapper;
			let buttons;

			beforeEach(() => {
				wrapper = shallowRenderComponent();
				buttons = wrapper.find(WMRaisedButton);
			});

			it('should have a <WMRaisedButton />', () => {
				expect(buttons).toHaveLength(1);
				expect(buttons.get(0).type).toEqual(WMRaisedButton);
			});

			it('should have a <WMRaisedButton /> with text', () => {
				expect(buttons.get(0).props.label).toEqual('Add this requirement');
			});

			it('should have a <WMRaisedButton /> add button set to default', () => {
				const addButton = buttons.get(0);
				expect(addButton.props.disabled).toEqual(true);
			});
		});
	});
});
