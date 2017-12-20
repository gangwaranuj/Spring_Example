import React from 'react';
import nock from 'nock';
import { shallow } from 'enzyme';
import { WMFormRow, WMSelectField, WMRaisedButton, WMMenuItem } from '@workmarket/front-end-components';
import CertificationRequirement from '../CertificationRequirement';
import { initialState } from '../../../reducers';

describe('<CertificationRequirement />', () => {
	const shallowRenderComponent = (
		handleChange = () => {},
		applyRequirement = () => {},
		certification = initialState.get('requirementsData').toJS().certification
	) => shallow(
		<CertificationRequirement.WrappedComponent
			handleChange={ handleChange }
			applyRequirement={ applyRequirement }
			certification={ certification }
		/>
	);

	const baseUrl = 'http://localhost:8080';
	const industryEndPoint = '/industries-list';
	const providerEndPoint = '/profile-edit/certificationslist';
	const certificateEndPoint = '/profile-edit/certificationslist';
	const setupMockIndustryServer = ({ response }) => {
		return nock(baseUrl)
			.get(industryEndPoint)
			.reply(200, response);
	};
	const setupMockProviderServer = ({ response }) => {
		return nock(baseUrl)
			.get(providerEndPoint)
			.query(true)
			.reply(200, response);
	};
	const setupMockCertificateServer = ({ response }) => {
		return nock(baseUrl)
			.get(certificateEndPoint)
			.query(true)
			.reply(200, response);
	};

	describe('Rendering', () => {
		describe('Requirements', () => {
			let wrapper;

			beforeEach(() => {
				wrapper = shallowRenderComponent();
			});

			it('should render 2 <WMFormRow /> when industry and provider are not selected ther should be 2 WMFormRows', () => {
				const components = wrapper.find(WMFormRow);
				expect(components).toHaveLength(2);
			});

			it('should render 5 <WMFormRow /> when industry and provider are selected there should be 5 WMFormRows', () => {
				wrapper.setState({ industrySelected: 'automobiles' });
				wrapper.setState({ providerSelected: 'apple' });
				const components = wrapper.find(WMFormRow);
				expect(components).toHaveLength(5);
			});

			it('should render 1 <WMSelectField /> when industry and provider are not selected there should be 1 WMSelectFields', () => {
				const components = wrapper.find(WMSelectField);
				expect(components).toHaveLength(1);
			});

			it('should render 3 <WMSelectField /> when industry and provider are selected there should be 3 WMSelectFields', () => {
				wrapper.setState({ industrySelected: 'automobiles' });
				wrapper.setState({ providerSelected: 'apple' });
				const components = wrapper.find(WMSelectField);
				expect(components).toHaveLength(3);
			});

			it('should render 1 <WMMenuItem /> with industry info on initial state', () => {
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

			it('should render 1 <WMMenuItem /> with provider info when industry is selected', () => {
				const providerList = [
					{
						id: 1112,
						name: 'Apple'
					}
				];
				wrapper.setState({ industrySelected: 2343 });
				wrapper.setState({ providerList });
				const component = wrapper.find('[data-component-identifier="requirements_providerSelect"]');
				expect(component).toHaveLength(1);
				const menuItem = component.find(WMMenuItem);
				expect(menuItem.prop('value')).toEqual(providerList[0].id);
				expect(menuItem.prop('primaryText')).toEqual(providerList[0].name);
			});

			it('should render 1 <WMMenuItem /> with certification info when industry and provider are selected', () => {
				const certificationList = [
					{
						id: 1112,
						name: 'Apple'
					}
				];
				wrapper.setState({ industrySelected: 2343 });
				wrapper.setState({ providerSelected: 1123 });
				wrapper.setState({ certificationList });
				const component = wrapper.find('[data-component-identifier="requirements_certificationSelect"]');
				expect(component).toHaveLength(1);
				const menuItem = component.find(WMMenuItem);
				expect(menuItem.prop('value')).toEqual(certificationList[0].id);
				expect(menuItem.prop('primaryText')).toEqual(certificationList[0].name);
			});
		});

		describe('Fetching data calls', () => {
			let wrapper;

			beforeEach(() => {
				wrapper = shallowRenderComponent();
			});

			afterEach(() => {
				nock.cleanAll();
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
				return wrapper.instance().fetchIndustries(baseUrl)
				.then(() => {
					const state = wrapper.state();
					expect(state.industryList).toEqual(industryList);
				});
			});

			// it('should fetch providers list when industry is selected', () => {
			// 	const providerList = [
			// 		{
			// 			id: 1112,
			// 			name: 'Apple'
			// 		},
			// 		{
			// 			id: 1123,
			// 			name: 'AT&T'
			// 		}
			// 	];
			// 	const component = wrapper.instance();
			// 	component.fetchProvidersStub = jest.fn(() => Promise.resolve(providerList));

			// 	return component.handleIndustryChange(3424)
			// 	.then(() => {
			// 		const state = wrapper.state();
			// 		expect(state.industrySelected).toEqual(3424);
			// 		expect(state.providerList).toEqual(providerList);
			// 		expect(component.fetchProvidersStub).toHaveBeenCalledTimes(1);
			// 	});
			// });

			it('should fetch provider list from server', () => {
				const providerList = [
					{
						id: 9234,
						name: 'Apple'
					}
				];
				setupMockProviderServer({
					response: providerList
				});

				return wrapper.instance().fetchProvidersList(2343, baseUrl)
				.then((data) => {
					expect(data).toEqual(providerList);
				});
			});

			// it('should fetch certification list when provider is selected', () => {

			// 	const certificationList = [
			// 		{
			// 			id: 9234,
			// 			name: 'Aperture 4 OS X'
			// 		}
			// 	];
			// 	const component = wrapper.instance();
			// 	component.fetchCertificationStub = jest.fn(() => Promise.resolve({ list: certificationList }));

			// 	return component.handleProviderChange(3424, baseUrl)
			// 		.then(() => {
			// 			const state = wrapper.state();
			// 			expect(state.providerSelected).toEqual(3424);
			// 			expect(state.certificationList).toEqual(certificationList);
			// 			expect(component.fetchCertificationStub).toHaveBeenCalledTimes(1);
			// 		});
			// });

			it('should fetch certificate list from server', () => {
				const certificationList = [
					{
						id: 9234,
						name: 'Aperture 4 OS X'
					}
				];
				setupMockCertificateServer({
					response: certificationList
				});

				return wrapper.instance().fetchCertificationList(234, 234, baseUrl)
				.then((data) => {
					expect(data).toEqual(certificationList);
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
