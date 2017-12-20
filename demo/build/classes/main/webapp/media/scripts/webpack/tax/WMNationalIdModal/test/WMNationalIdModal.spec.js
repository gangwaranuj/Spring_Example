import React from 'react';
import { shallow } from 'enzyme';
import WMNationalIdModal from '../template';
import WMNationalIdTable from '../../WMNationalIdTable';
import {
	WMModal,
	WMAppBar
} from '@workmarket/front-end-components';

describe('<WMNationalIdModal />', () => {
	const modalTrigger = '[data-component-identifier="wm-national-id-modal__trigger"]';
	const modalBody = '[data-component-identifier="wm-national-id-modal__body"]';
	const isOpen = false;
	let openModal = () => {};
	const closeModal = () => {};

	describe('Rendering', () => {
		let wrapper;

		beforeEach(() => {
			wrapper = shallow(
				<WMNationalIdModal
					isOpen={ isOpen }
					openModal={ openModal }
					closeModal={ closeModal }
				/>
			);
		});

		it('should be wrapped in a <span />', () => {
			expect(wrapper.type()).toEqual('span');
		});

		it('should have an <a />', () => {
			const modal = wrapper.find('a');
			expect(modal).toHaveLength(1);
		});

		describe('<WMModal />', () => {
			it('should exist', () => {
				const modal = wrapper.find(WMModal);
				expect(modal).toHaveLength(1);
			});

			it('should have an <WMAppBar />', () => {
				const appBar = wrapper.find(WMAppBar);
				expect(appBar).toHaveLength(1);
			});

			it('should have an body container', () => {
				const appBar = wrapper.find(modalBody);
				expect(appBar).toHaveLength(1);
			});

			it('should have an <WMNationalIdTable />', () => {
				const appBar = wrapper.find(WMNationalIdTable);
				expect(appBar).toHaveLength(1);
			});
		});
	});

	describe('Properties', () => {
		let wrapper;

		beforeEach(() => {
			wrapper = shallow(
				<WMNationalIdModal
					isOpen={ isOpen }
					openModal={ openModal }
					closeModal={ closeModal }
				/>
			);
		});

		it('should accept `isOpen`', () => {
			const modal = wrapper.find(WMModal);
			expect(modal.prop('open')).toEqual(isOpen);
		});

		it('should accept `openModal`', () => {
			const trigger = wrapper.find(modalTrigger);
			expect(trigger.prop('onClick')).toEqual(openModal);
		});

		it('should accept `closeModal`', () => {
			const modal = wrapper.find(WMModal);
			expect(modal.prop('onRequestClose')).toEqual(closeModal);
		});
	});

	describe('Behavior', () => {
		let wrapper;

		beforeEach(() => {
			openModal = jest.fn();
			wrapper = shallow(
				<WMNationalIdModal
					isOpen={ isOpen }
					openModal={ openModal }
					closeModal={ closeModal }
				/>
			);
		});

		it('should call `openModal` when the trigger is clicked', () => {
			const trigger = wrapper.find(modalTrigger);
			trigger.simulate('click');
			expect(openModal).toHaveBeenCalled();
		});
	});
});
