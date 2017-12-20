import React from 'react';
import { shallow } from 'enzyme';
import { WMModal } from '@workmarket/front-end-components';
import WMOnboardingProgressBar from '../../WMOnboardingProgressBar';
import content from '../../WMHome/content';
import WMHome from '../';
import WMHomeTile from '../../WMHomeTile';

describe('WMHome ::', () => {
	const shallowRenderComponent = (showProgressBar = true) => {
		return shallow(
			<WMHome
				showProgressBar={ showProgressBar }
			/>
		);
	};

	describe('Rendering ::', () => {
		let component;

		beforeEach(() => {
			component = shallowRenderComponent();
		});

		it('should render a WMOnboardingProgressBar if showProgressBar === true', () => {
			expect(component.find(WMOnboardingProgressBar)).toHaveLength(1);
		});

		it('should not render a WMOnboardingProgressBar if showProgressBar !== true', () => {
			const noBarComponent = shallowRenderComponent(false);
			expect(noBarComponent.find(WMOnboardingProgressBar)).toHaveLength(0);
		});

		it('should render WMHomeTiles for each obj in content', () => {
			expect(component.find(WMHomeTile)).toHaveLength(content.length);
		});

		it('should render a WMModal', () => {
			expect(component.find(WMModal)).toHaveLength(1);
		});
	});

	it('should pass showVideo func to WMOnboardingProgressBar', () => {
		const component = shallowRenderComponent();
		expect(component.find(WMOnboardingProgressBar).prop('showVideo')).toEqual(component.instance().showVideo);
	});

	describe('Tile Properties ::', () => {
		let component;
		let sampleTile;
		const tileContent = content[0];

		beforeEach(() => {
			component = shallowRenderComponent();
			sampleTile = component.find(WMHomeTile).first();
		});

		Object.keys(tileContent).forEach((key) => {
			const value = tileContent[key];

			it(`should pass a ${key} property to the tile`, () => {
				expect(sampleTile.prop(key)).toEqual(value);
			});
		});
	});

	describe('WMModal ::', () => {
		let component;

		beforeEach(() => {
			component = shallowRenderComponent();
		});

		it('should not be open initially', () => {
			const modal = component.find(WMModal);
			expect(modal.prop('open')).toBeFalsy();
		});

		it('should open if modalOpen state is true', () => {
			component.setState({ modalOpen: true });
			const modal = component.find(WMModal);
			expect(modal.prop('open')).toBeTruthy();
		});

		it('should set WMHome state on close', () => {
			component.setState({ modalOpen: true, videoURL: 'http://cinco.com' });
			const modal = component.find(WMModal);
			const closeFunc = modal.prop('onRequestClose');
			closeFunc();
			expect(component.state('modalOpen')).toBeFalsy();
			expect(component.state('videoURL')).toEqual('');
		});

		it('should contain an iframe', () => {
			component.setState({ modalOpen: true, videoURL: 'http://cinco.com' });
			const modal = component.find(WMModal);
			const iframe = modal.find('iframe');
			expect(iframe).toBeDefined();
			expect(iframe.prop('src')).toEqual('http://cinco.com');
		});
	});
});
