import React from 'react';
import { Provider } from 'react-redux';
import { shallow, mount } from 'enzyme';
import { WMFieldSet, WMSelectField, WMListItem } from '@workmarket/front-end-components';
import TalentPoolRequirements from '../template';
import { initialState } from '../../../reducers/index';
import * as requirementTypes from '../../../constants/requirementTypes';
import SatisfactionRatingRequirement from '../../RequirementComponents/SatisfactionRatingRequirement';
import configureStore from '../../../configureStore/index';

const mergedState = initialState.mergeDeep({
	requirementsData: {
		activeRequirementType: 'SatisfactionRatingRequirement',
		requirements: [
			{
				name: '50',
				$type: 'RatingRequirement',
				value: 50,
				$humanTypeName: 'Rating'
			}
		]
	}
});

const renderComponent = (
	state = mergedState,
	removeRequirement = () => {},
	setActiveRequirementType = () => {},
	onToggleActivateAutomaticEnforcement = () => {},
	saveRequirementSet = () => {},
	toggleRequirementNotifyOnExpiry = () => {},
	toggleRequirementRemoveOnExpiry = () => {}
) => mount(
	<Provider store={ configureStore() }>
		<TalentPoolRequirements
			hasESignatureEnabled={ true }
			talentPoolData={ state.get('requirementsData') }
			removeRequirement={ removeRequirement }
			setActiveRequirementType={ setActiveRequirementType }
			onToggleActivateAutomaticEnforcement={ onToggleActivateAutomaticEnforcement }
			saveRequirementSet={ saveRequirementSet }
			toggleRequirementNotifyOnExpiry={ toggleRequirementNotifyOnExpiry }
			toggleRequirementRemoveOnExpiry={ toggleRequirementRemoveOnExpiry }
		/>
	</Provider>
);

describe('<TalentPoolRequirements />', () => {
	let wrapper;
	const dummyFunc = jest.fn();
	const requirementComponentData = initialState.get('requirementsData');

	describe('Rendering consistency', () => {
		it('should render the same way every time with all props', () => {
			const snapshot = shallow(
				<TalentPoolRequirements
					hasESignatureEnabled={ true }
					talentPoolData={ requirementComponentData }
					removeRequirement={ dummyFunc }
					setActiveRequirementType={ dummyFunc }
					onToggleActivateAutomaticEnforcement={ dummyFunc }
					saveRequirementSet={ dummyFunc }
					toggleRequirementNotifyOnExpiry={ dummyFunc }
					toggleRequirementRemoveOnExpiry={ dummyFunc }
				/>
			);
			expect(snapshot).toMatchSnapshot();
		});
	});

	describe('Rendering `SatisfactionRatingRequirement` with rating set to 50', () => {
		beforeEach(() => {
			wrapper = renderComponent();
		});

		it('should have a WMFieldSet', () => {
			const component = wrapper.find(WMFieldSet);
			expect(component).toHaveLength(1);
		});
		it('should load requirement types', () => {
			const component = wrapper.find(WMSelectField);
			expect(component.prop('children')).toHaveLength(Object.keys(requirementTypes).length);
		});
		it('should load an individual requirement UI', () => {
			const component = wrapper.find(SatisfactionRatingRequirement);
			expect(component).toHaveLength(1);
		});
		it('should display added requirements', () => {
			const component = wrapper.find(WMListItem);
			expect(component.prop('primaryText')).toEqual('Rating: 50');
		});

		it('should have an empty state', () => {
			wrapper = renderComponent(initialState);
			const component = wrapper.find(WMListItem);
			expect(component).toHaveLength(0);
		});
	});
});
