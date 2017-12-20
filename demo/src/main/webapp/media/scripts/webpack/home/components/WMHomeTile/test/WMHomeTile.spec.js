/* eslint-disable jest/no-disabled-tests */
import React from 'react';
import { shallow } from 'enzyme';
import {
	commonStyles,
	WMCard,
	WMCardText,
	WMFontIcon,
	WMRaisedButton
} from '@workmarket/front-end-components';
import content from '../../WMHome/content';
import styles from '../../WMHomeTile/styles';
import WMHomeTile from '../';

const { baseColors } = commonStyles.colors;
const testContent = content[0];

describe('<WMHomeTile /> ::', () => {
	const shallowRenderComponent = (
		tileContent = testContent,
		showVideo = () => {}
	) => {
		const props = Object.assign({}, tileContent);
		props.showVideo = showVideo;
		return shallow(
			<WMHomeTile
				{ ...props }
			/>
		);
	};

	describe('Rendering ::', () => {
		let component;

		beforeEach(() => {
			component = shallowRenderComponent();
		});

		it('should be wrapped in a WMCard', () => {
			expect(component.find(WMCard).length).toEqual(1);
		});

		it('should have a WMCardText', () => {
			expect(component.find(WMCardText).length).toEqual(1);
		});

		it('should have an illustration from the content provided', () => {
			const tileImage = component.find('img');
			expect(tileImage.prop('src')).toEqual(testContent.icon);
			expect(tileImage.prop('alt')).toEqual(testContent.title);
			expect(tileImage.prop('style')).toEqual(styles.icon);
		});

		it('should have a div with the tile title', () => {
			expect(component.find(WMCardText).children().text()).toContain(testContent.title);
		});

		it('should include copy for the tile', () => {
			expect(component.find(WMCardText).children().text()).toContain(testContent.contentCopy);
		});

		it('should have a button', () => {
			const button = component.find(WMRaisedButton);
			expect(button.prop('backgroundColor')).toEqual('#53b3f3');
			expect(button.prop('label')).toEqual(testContent.buttonText);
			expect(button.prop('labelColor')).toEqual(baseColors.white);
			expect(button.prop('style')).toEqual(styles.cardButton);
		});

		describe('Button ::', () => {
			it('should be a buttonLink if content gives a string', () => {
				const button = component.find(WMRaisedButton);
				expect(button.prop('linkButton')).toEqual(true);
				expect(button.prop('href')).toEqual(testContent.buttonLink);
			});

			// TODO[tim-mc]: Reenable after Assignment Creation is sorted out
			xit('should not be a buttonLink if content gives a func', () => {
				const funcComponent = shallowRenderComponent(content[2], () => {});
				const button = funcComponent.find(WMRaisedButton);
				expect(button.prop('linkButton')).toBeTruthy();
				expect(button.prop('onClick').exists()).toBeTruthy();
			});
		});

		describe('Video Link ::', () => {
			it('should fire showVideo when clicked', () => {
				const showVideoStub = jest.fn();
				const tileComponent = shallowRenderComponent(content[0], showVideoStub);
				const cardInner = tileComponent.find(WMCardText).children();
				const videoLinkContainer = cardInner.children().last();
				videoLinkContainer.simulate('click');
				expect(showVideoStub).toHaveBeenCalled();
			});

			it('should have a play icon', () => {
				const cardInner = component.find(WMCardText).children();
				const videoLinkContainer = cardInner.children().last();
				const icon = videoLinkContainer.find(WMFontIcon);
				expect(icon.exists()).toBeTruthy();
				expect(icon.children().text()).toEqual('play_circle_outline');
				expect(icon.prop('color')).toEqual('#53b3f3');
			});
		});
	});
});
