import React from 'react';
import { shallow } from 'enzyme';
import { WMRaisedButton } from '@workmarket/front-end-components';
import IndustryRequirement from '../IndustryRequirement';
import { initialState } from '../../../reducers';

describe('<IndustryRequirement />', () => {
	const dummyFunc = jest.fn();
	const requirementComponentData = initialState.get('requirementsData').toJS();
	describe('Rendering consistency', () => {
		it('should render the same way every time with all props', () => {
			const snapshot = shallow(
				<IndustryRequirement.WrappedComponent
					handleChange={ dummyFunc }
					applyRequirement={ dummyFunc }
					requirementComponentData={ requirementComponentData }
				/>,
			);
			expect(snapshot).toMatchSnapshot();
		});

		describe('Interaction', () => {
			let wrapper;
			let button;

			const shallowRenderComponent = () => shallow(
				<IndustryRequirement.WrappedComponent
					handleChange={ dummyFunc }
					applyRequirement={ dummyFunc }
					requirementComponentData={ requirementComponentData }
				/>
			);

			beforeEach(() => {
				wrapper = shallowRenderComponent();
				button = wrapper.find(WMRaisedButton);
			});

			it('should have a <WMRaisedButton /> add button set to disabled', () => {
				const addButton = button.get(0);
				expect(addButton.props.disabled).toBeTruthy();
			});
		});
	});
});
