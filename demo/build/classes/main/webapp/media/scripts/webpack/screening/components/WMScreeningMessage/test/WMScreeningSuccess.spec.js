import React from 'react';
import { shallow } from 'enzyme';
import WMScreeningMessage from '../index';
import {
	WMCard,
	WMCardText,
	WMCardActions,
	WMFontIcon,
	WMRaisedButton
} from '@workmarket/front-end-components';
import styles from '../styles';

describe('<WMScreeningMessage />', () => {
	let wrapper;
	const icon = 'tag_faces';
	const children = <div>Millennials, amiright?</div>;
	const buttonLabel = 'Do Something';
	const link = 'https://www.johnsonweld.com/';

	beforeEach(() => {
		wrapper = shallow(
			<WMScreeningMessage
				icon={ icon }
				buttonLabel={ buttonLabel }
				link={ link }
			>
				{ children }
			</WMScreeningMessage>
		);
	});

	describe('Rendering', () => {
		it('should have children', () => {
			const component = wrapper.find(WMCardText);
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

		describe('Icon', () => {
			it('should be <WMFontIcon />', () => {
				const component = wrapper.find(WMFontIcon);
				expect(component.length).toEqual(1);
				expect(component.type()).toEqual(WMFontIcon);
			});

			it('should have style', () => {
				const component = wrapper.find(WMFontIcon);
				expect(component.prop('style')).toEqual(styles.icon);
			});

			it('should have `className` of `material-icons`', () => {
				const component = wrapper.find(WMFontIcon);
				expect(component.prop('className')).toEqual('material-icons');
			});

			it('should have `color` of `#8d9092`', () => {
				const component = wrapper.find(WMFontIcon);
				expect(component.prop('color')).toEqual('#8d9092');
			});
		});

		describe('Copy', () => {
			it('should have a <WMCardText />', () => {
				const component = wrapper.find(WMCardText);
				expect(component).toHaveLength(1);
				expect(component.type()).toEqual(WMCardText);
			});
		});

		describe('Actions', () => {
			let actions;
			let button;

			beforeEach(() => {
				actions = wrapper.find(WMCardActions);
				button = actions.find(WMRaisedButton);
			});

			it('should have a <WMCardActions />', () => {
				expect(actions).toHaveLength(1);
				expect(actions.type()).toEqual(WMCardActions);
			});

			it('should have a <WMCardActions /> with style', () => {
				expect(actions.prop('style')).toEqual(styles.actions);
			});

			it('should have a <WMRaisedButton />', () => {
				expect(button).toHaveLength(1);
				expect(button.type()).toEqual(WMRaisedButton);
			});

			it('should have a <WMRaisedButton /> with a green background', () => {
				expect(button.prop('backgroundColor')).toEqual('#5bc75d');
			});

			it('should have a <WMRaisedButton /> with white text', () => {
				expect(button.prop('labelColor')).toEqual('#fff');
			});
		});
	});

	describe('Properties', () => {
		it('should accept `icon`', () => {
			const component = wrapper.find(WMFontIcon);
			expect(component.prop('children')).toEqual(icon);
		});

		it('should accept `buttonLabel`', () => {
			const actions = wrapper.find(WMCardActions);
			const button = actions.find(WMRaisedButton);
			expect(button.prop('label')).toEqual(buttonLabel);
		});

		it('should accept `link`', () => {
			const actions = wrapper.find(WMCardActions);
			const a = actions.find('a');
			expect(a.prop('href')).toEqual(link);
		});
	});
});
