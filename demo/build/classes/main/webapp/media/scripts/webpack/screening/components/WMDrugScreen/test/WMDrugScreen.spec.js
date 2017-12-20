import React from 'react';
import { shallow } from 'enzyme';
import WMDrugScreen from '../template';
import WMScreeningMessage from '../../WMScreeningMessage';
import WMPersonalInformation from '../../WMPersonalInformation';
import WMPaymentInformation from '../../WMPaymentInformation';
import { processDrugConfig } from '../../../configureStore/state';

const config = {
	mode: 'screening',
	price: '50.00',
	availableFunds: '20000.00',
	isInternational: false,
	drugTestPassed: false,
	drugTestFailed: false,
	drugTestPending: false
};

const shallowRenderComponent = (
	state = processDrugConfig(config),
	formValid = false
) => shallow(
	<WMDrugScreen
		state={ state }
		formValid={ formValid }
	/>
);

describe('<WMDrugScreen />', () => {
	describe('Rendering', () => {
		let wrapper;

		beforeEach(() => {
			wrapper = shallowRenderComponent();
		});

		it('should be wrapped', () => {
			const component = wrapper.find('#wm-drug-screen');
			expect(component).toHaveLength(1);
		});

		describe('Order Form', () => {
			it('should show the form if you can order', () => {
				const component = wrapper.find('#wm-drug-screen__order-form');
				expect(component).toHaveLength(1);
			});

			it('should have an <h1 />', () => {
				const component = wrapper.find('h1');
				expect(component).toHaveLength(1);
			});

			it('should have <p />', () => {
				const component = wrapper.find('p');
				expect(component.length).toBeGreaterThanOrEqual(1);
			});

			it('should have <WMPersonalInformation />', () => {
				const component = wrapper.find(WMPersonalInformation);
				expect(component).toHaveLength(1);
				expect(component.type()).toEqual(WMPersonalInformation);
			});

			it('should have <WMPaymentInformation />', () => {
				const component = wrapper.find(WMPaymentInformation);
				expect(component.length).toBeGreaterThanOrEqual(1);
				expect(component.type()).toEqual(WMPaymentInformation);
			});

			it('should show you a message if your previous test failed', () => {
				const newConfig = Object.assign({}, config, { drugTestFailed: true });
				const newState = processDrugConfig(newConfig);
				wrapper = shallowRenderComponent(newState);
				const component = wrapper.find('[data-component-identifier="wm-drug-screen__failed"]');

				expect(component.length).toBeGreaterThanOrEqual(1);
			});

			it('should show you a message if your previous test passed', () => {
				const newConfig = Object.assign({}, config, { drugTestPassed: true });
				const newState = processDrugConfig(newConfig);
				wrapper = shallowRenderComponent(newState);
				const component = wrapper.find('[data-component-identifier="wm-drug-screen__passed"]');

				expect(component.length).toBeGreaterThanOrEqual(1);
			});
		});

		describe('Test Pending', () => {
			let component;

			beforeEach(() => {
				const newConfig = Object.assign({}, config, { drugTestPending: true });
				const newState = processDrugConfig(newConfig);
				wrapper = shallowRenderComponent(newState);
				component = wrapper.find(WMScreeningMessage);
			});

			it('should show a <WMScreeningMessage />', () => {
				expect(component).toHaveLength(1);
				expect(component.type()).toEqual(WMScreeningMessage);
			});

			it('should have `icon`', () => {
				expect(component.prop('icon')).toEqual('tag_faces');
			});

			it('should have `buttonLabel`', () => {
				expect(component.prop('buttonLabel')).toEqual('Back to Worker Services');
			});

			it('should have `link` icon', () => {
				expect(component.prop('link')).toEqual('/workerservices');
			});

			it('should have `children`', () => {
				expect(component.prop('children')).toBeDefined();
			});
		});

		describe('Internation User', () => {
			let component;

			beforeEach(() => {
				const newConfig = Object.assign({}, config, { isInternational: true });
				const newState = processDrugConfig(newConfig);
				wrapper = shallowRenderComponent(newState);
				component = wrapper.find(WMScreeningMessage);
			});

			it('should show a <WMScreeningMessage />', () => {
				expect(component).toHaveLength(1);
			});

			it('should have `icon`', () => {
				expect(component.prop('icon')).toEqual('person_pin_circle');
			});

			it('should have `buttonLabel`', () => {
				expect(component.prop('buttonLabel')).toEqual('Find Work');
			});

			it('should have `link` icon', () => {
				expect(component.prop('link')).toEqual('/worker/browse');
			});

			it('should have `children`', () => {
				expect(component.prop('children')).toBeDefined();
			});
		});
	});
});
