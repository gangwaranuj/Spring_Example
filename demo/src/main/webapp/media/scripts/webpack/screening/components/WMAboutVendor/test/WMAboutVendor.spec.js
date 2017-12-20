import React from 'react';
import { shallow, mount } from 'enzyme';
import {
	WMCard,
	WMCardText,
	WMCardMedia
} from '@workmarket/front-end-components';
import WMAboutVendor from '../index';
import styles from '../styles';

describe('<WMAboutVendor />', () => {
	let wrapper;
	const children = 'Lorem ipsum';

	beforeAll(() => {
		global.window.mediaPrefix = '/globalsrule/';
	});

	beforeEach(() => {
		wrapper = shallow(
			<WMAboutVendor
				prefix={ global.window.mediaPrefix }
			>
				{ children }
			</WMAboutVendor>
		);
	});

	afterAll(() => {
		global.window.mediaPrefix = null;
	});

	describe('Rendering', () => {
		it('should have children', () => {
			const component = wrapper.find('[data-component-identifier="screening__vendorText"]');
			expect(component.prop('children')).toEqual(children);
		});

		describe('Card', () => {
			it('should be <WMCard />', () => {
				const component = wrapper.find(WMCard);
				expect(component).toHaveLength(1);
				expect(component.type()).toEqual(WMCard);
			});

			it('should have style', () => {
				const component = wrapper.find(WMCard);
				expect(component.prop('style')).toEqual(styles.card);
			});
		});

		describe('Media', () => {
			it('should be <WMCardMedia />', () => {
				const component = wrapper.find(WMCardMedia);
				expect(component).toHaveLength(1);
				expect(component.type()).toEqual(WMCardMedia);
			});

			it('should have style', () => {
				const component = wrapper.find(WMCardMedia);
				expect(component.prop('style')).toEqual(styles.media);
			});

			it('should have an `img`', () => {
				const component = wrapper.find('img');
				expect(component).toHaveLength(1);
			});
		});

		describe('Text', () => {
			it('should have a <WMCardText />', () => {
				const components = wrapper.find(WMCardText);
				expect(components).toHaveLength(1);
				components.forEach(component => expect(component.type()).toEqual(WMCardText));
			});
		});

		describe('Properties', () => {
			beforeAll(() => {
				global.window.mediaPrefix = '/globalsrule/';
			});

			beforeEach(() => {
				wrapper = mount(
					<WMAboutVendor
						prefix={ global.window.mediaPrefix }
					>
						{ children }
					</WMAboutVendor>
				);
			});

			afterAll(() => {
				global.window.mediaPrefix = null;
			});

			it('should accept `children`', () => {
				const component = wrapper.find(WMAboutVendor);
				expect(component.prop('children')).toEqual(children);
			});

			it('should accept `prefix`', () => {
				const component = wrapper.find(WMAboutVendor);
				expect(component.prop('prefix')).toEqual(global.window.mediaPrefix);
			});
		});
	});
});
