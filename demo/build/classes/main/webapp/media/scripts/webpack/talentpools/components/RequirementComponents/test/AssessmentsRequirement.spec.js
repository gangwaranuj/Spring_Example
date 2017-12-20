import React from 'react';
import nock from 'nock';
import { shallow } from 'enzyme';
import { WMRaisedButton, WMMenuItem, WMSelectField } from '@workmarket/front-end-components';
import AssessmentsRequirement from '../AssessmentsRequirement';
import { initialState } from '../../../reducers';

describe('<AssessmentsRequirement />', () => {
	const componentState = initialState.getIn(['requirementsData', 'assessmentId']);

	const shallowRenderComponent = (
		handleChange = () => {},
		applyRequirement = () => {},
		assessmentId = componentState
	) => shallow(
		<AssessmentsRequirement.WrappedComponent
			handleChange={ handleChange }
			applyRequirement={ applyRequirement }
			insurance={ assessmentId }
		/>
	);

	const setupMockServer = ({ response }) => {
		return nock('http://localhost:8080')
			.get(/.*/)
			.reply(200, response);
	};

	describe('Rendering', () => {
		describe('Assessments Select', () => {
			it('should render 1 <WMFormRow />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="requirements_Row"]');
				expect(component).toHaveLength(1);
			});

			it('should render 1 <WMSelectField />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="requirements_assessmentSelect"]');
				expect(component).toHaveLength(1);
				expect(component.type()).toEqual(WMSelectField);
			});

			it('should render 1 <WMMenuItem /> with assessments info />', () => {
				const wrapper = shallowRenderComponent();
				const assessmentList = [
					{
						id: 4,
						name: 'Test 4'
					}
				];
				wrapper.setState({ assessmentList });
				const component = wrapper.find('[data-component-identifier="requirements_assessmentSelect"]');
				expect(component).toHaveLength(1);
				const menuItem = component.find(WMMenuItem);
				expect(menuItem.prop('value')).toEqual(assessmentList[0].id);
				expect(menuItem.prop('primaryText')).toEqual(assessmentList[0].name);
			});

			it('should fetch assessments list on mount', () => {
				const assessmentList = [
					{
						id: 3424,
						name: 'Digital Signage'
					}
				];
				setupMockServer({
					response: assessmentList
				});
				const wrapper = shallowRenderComponent();
				return wrapper.instance().fetchTests('http://localhost:8080')
					.then(() => {
						const state = wrapper.state();
						expect(state.assessmentList).toEqual(assessmentList);
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
				expect(addButton.props.disabled).toBeTruthy();
			});
		});
	});
});
