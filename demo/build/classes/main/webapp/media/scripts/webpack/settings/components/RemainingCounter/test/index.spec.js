import React from 'react';
import { shallow } from 'enzyme';
import RemainingCounter from '../';

describe('<RemainingCounter />', () => {
	const shallowRenderComponent = (
		maxCharacters = 100,
		value = 'Guy Fieri is life'
	) => shallow(
		<RemainingCounter
			value={ value }
			maxCharacters={ maxCharacters }
		/>
	);

	it('should return paragraph element', () => {
		const wrapper = shallowRenderComponent();
		const pElement = wrapper.find('p');
		expect(pElement.exists()).toBeTruthy();
	});

	it('should render number of characters remaining', () => {
		const wrapper = shallowRenderComponent();
		const pElement = wrapper.find('p');
		const inputValue = 'Guy Fieri is life';
		const expectedValue = (100 - inputValue.length).toString();
		expect(pElement.text()).toEqual(expectedValue);
	});

	it('should render red text if past character limit', () => {
		const wrapper = shallowRenderComponent(1, 'du');
		const pElement = wrapper.find('p');
		expect(pElement.prop('style')).toEqual({ color: 'red' });
	});
});
