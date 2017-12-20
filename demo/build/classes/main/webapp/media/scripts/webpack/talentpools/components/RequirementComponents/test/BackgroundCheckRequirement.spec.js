import React from 'react';
import { shallow } from 'enzyme';
import configureStore from '../../../configureStore';
import BackgroundCheckRequirement from '../BackgroundCheckRequirement';

describe('<BackgroundCheckRequirement />', () => {
	const dummyFunc = jest.fn();
	const store = configureStore();

	describe('Rendering consistency', () => {
		it('should render the same way every time with all props', () => {
			const snapshot = shallow(
				<BackgroundCheckRequirement.WrappedComponent
					store={ store }
					dispatch={ dummyFunc }
					applyRequirement={ dummyFunc }
				/>,
			);
			expect(snapshot).toMatchSnapshot();
		});
	});
});
