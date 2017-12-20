import React from 'react';
import { shallow } from 'enzyme';
import { CloseIcon } from '../template';
import NavigationClose from 'material-ui/svg-icons/navigation/close';
import {
	WMIconButton
} from '@workmarket/front-end-components';
import styles from '../styles';

describe('WMNationalIdModal :: <CloseIcon />', () => {
	let closeModal = () => {};

	describe('Rendering', () => {
		let wrapper;

		beforeEach(() => {
			wrapper = shallow(
				<CloseIcon
					closeModal={ closeModal }
				/>
			);
		});

		it('should render a <WMIconButton />', () => {
			const button = wrapper.find(WMIconButton);
			expect(button).toHaveLength(1);
		});

		it('should render a <WMIconButton /> with style', () => {
			const button = wrapper.find(WMIconButton);
			expect(button.prop('iconStyle')).toEqual(styles.modal.icon);
		});

		it('should render a <NavigationClose />', () => {
			const icon = wrapper.find(NavigationClose);
			expect(icon).toHaveLength(1);
		});
	});

	describe('Properties', () => {
		let wrapper;

		beforeEach(() => {
			wrapper = shallow(
				<CloseIcon
					closeModal={ closeModal }
				/>
			);
		});

		it('should accept `closeModal`', () => {
			const icon = wrapper.find(WMIconButton);
			expect(icon.prop('onFocus')).toEqual(closeModal);
		});
	});

	describe('Behavior', () => {
		let wrapper;

		beforeEach(() => {
			closeModal = jest.fn();
			wrapper = shallow(
				<CloseIcon
					closeModal={ closeModal }
				/>
			);
		});

		it('should call `closeModal` when clicked', () => {
			const button = wrapper.find(WMIconButton);
			button.simulate('focus');
			expect(closeModal).toHaveBeenCalled();
		});
	});
});
