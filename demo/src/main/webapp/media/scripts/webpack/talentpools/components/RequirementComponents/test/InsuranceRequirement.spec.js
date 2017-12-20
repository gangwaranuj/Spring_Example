import React from 'react';
import nock from 'nock';
import { shallow } from 'enzyme';
import { WMRaisedButton, WMMenuItem } from '@workmarket/front-end-components';
import InsuranceRequirement from '../InsuranceRequirement';
import { initialState } from '../../../reducers';

describe('<InsuranceRequirement />', () => {
	const componentState = initialState.getIn(['requirementsData', 'insurance']);

	const shallowRenderComponent = (
		handleChange = () => {},
		applyRequirement = () => {},
		insurance = componentState
	) => shallow(
		<InsuranceRequirement.WrappedComponent
			handleChange={ handleChange }
			applyRequirement={ applyRequirement }
			insurance={ insurance }
		/>
	);

	const baseUrl = 'http://localhost:8080/';
	const industryEndPoint = '/industries-list';
	const insuranceEndPoint = '/profile-edit/insurancelist';
	const setupMockIndustryServer = ({ response }) => {
		return nock(baseUrl)
			.get(industryEndPoint)
			.reply(200, response);
	};
	const setupMockInsuranceServer = ({ response }) => {
		return nock(baseUrl)
			.get(insuranceEndPoint)
			.query(true)
			.reply(200, response);
	};

	describe('Rendering', () => {
		describe('Industry Select', () => {
			it('should render 1 <WMFormRow />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="requirements_industryRow"]');
				expect(component).toHaveLength(1);
			});

			it('should render 1 <WMStateProvince />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="requirements_industrySelect"]');
				expect(component).toHaveLength(1);
			});

			it('should render 1 <WMMenuItem /> with industry info />', () => {
				const wrapper = shallowRenderComponent();
				const industryList = [
					{
						id: 3424,
						name: 'Automobile'
					}
				];
				wrapper.setState({ industryList });
				const component = wrapper.find('[data-component-identifier="requirements_industrySelect"]');
				expect(component).toHaveLength(1);
				const menuItem = component.find(WMMenuItem);
				expect(menuItem.prop('value')).toEqual(industryList[0].id);
				expect(menuItem.prop('primaryText')).toEqual(industryList[0].name);
			});

			it('should fetch industry list on mount', () => {
				const industryList = [
					{
						id: 3424,
						name: 'Digital Signage'
					}
				];
				setupMockIndustryServer({
					response: industryList
				});
				const wrapper = shallowRenderComponent();
				return wrapper.instance().fetchIndustries('http://localhost:8080')
					.then(() => {
						const state = wrapper.state();
						expect(state.industryList).toEqual(industryList);
					});
			});
		});

		describe('Insurance Select when industry is selected', () => {
			it('should render 1 <WMFormRow />', () => {
				const wrapper = shallowRenderComponent();
				wrapper.setState({ industrySelected: 'automobiles' });
				const component = wrapper.find('[data-component-identifier="requirements_insuranceRow"]');
				expect(component).toHaveLength(1);
			});

			it('should render 1 <WMSelectField />', () => {
				const wrapper = shallowRenderComponent();
				wrapper.setState({ industrySelected: 'automobiles' });
				const component = wrapper.find('[data-component-identifier="requirements_insuranceSelect"]');
				expect(component).toHaveLength(1);
			});

			it('should render 1 <WMMenuItem /> with insurance info />', () => {
				const wrapper = shallowRenderComponent();
				wrapper.setState({ industrySelected: 'automobiles' });
				const insuranceList = [
					{
						id: 9234,
						name: 'Sweet Insurance'
					}
				];
				wrapper.setState({ insuranceList });
				const component = wrapper.find('[data-component-identifier="requirements_insuranceSelect"]');
				const menuItem = component.find(WMMenuItem);
				expect(menuItem.prop('value')).toEqual(insuranceList[0].id);
				expect(menuItem.prop('primaryText')).toEqual(insuranceList[0].name);
			});

			it('should fetch insurance list when industry is picked', () => {
				const insuranceList = [
					{
						id: 9234,
						name: 'Sweet Sweet Insurance'
					}
				];

				const wrapper = shallowRenderComponent();
				const component = wrapper.instance();
				component.fetchInsuranceStub = jest.fn(() => Promise.resolve({ list: insuranceList }));

				component.handleIndustryChange(3424)
					.then(() => {
						const state = wrapper.state();
						expect(state.industrySelected).toEqual(3424);
						expect(state.insuranceList).toEqual(insuranceList);
						expect(fetchInsuranceStub).toHaveBeenCalledTimes(1);
					});
			});

			it('should fetch insurance list from server', () => {
				const insuranceList = [
					{
						id: 9234,
						name: 'Sweet Sweet Insurance'
					}
				];
				setupMockInsuranceServer({
					response: insuranceList
				});
				const wrapper = shallowRenderComponent();

				return wrapper.instance().fetchInsuranceList('', 'http://localhost:8080')
					.then((data) => {
						expect(data).toEqual(insuranceList);
					});
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

			it('should have a <WMRaisedButton /> add button set to disabled', () => {
				const addButton = buttons.get(0);
				expect(addButton.props.disabled).toEqual(true);
			});
		});
	});
});
