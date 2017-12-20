import React from 'react';
import { shallow } from 'enzyme';
import {
	WMCard,
	WMCardHeader,
	WMCardText,
	WMFontIcon
} from '@workmarket/front-end-components';
import WMSettingsFunds, { FundingOptionTile } from '../template';
import content from '../content';
import { initialFundsState } from '../../../reducers/addFundsViaCreditCardReducer';
import { initialSettingsState } from '../../../reducers/settingsReducer';
import baseStyles from '../../styles';

describe('<FundingOptionTile />', () => {
	const shallowRenderComponent = () => shallow(
		<FundingOptionTile
			{ ...content[0] }
		/>
	);

	describe('Rendering ::', () => {
		it('should render a title', () => {
			const wrapper = shallowRenderComponent();
			const title = wrapper.find('[data-component-identifier="funding-option-tile__title"]');
			expect(title.text()).toContain(content[0].title);
		});

		it('should render an icon', () => {
			const wrapper = shallowRenderComponent();
			const icon = wrapper.find(WMFontIcon).first();
			expect(icon.prop('children')).toEqual(content[0].icon);
		});

		it('should render a time icon', () => {
			const wrapper = shallowRenderComponent();
			const icon = wrapper.find(WMFontIcon).last();
			expect(icon.prop('children')).toEqual('timer');
		});

		it('should render a duration', () => {
			const wrapper = shallowRenderComponent();
			const time = wrapper.find('[data-component-identifier="funding-option-tile__time"]');
			expect(time.text()).toContain(content[0].time);
		});
	});
});

describe('<WMSettingsFunds />', () => {
	const shallowRenderComponent = () => shallow(
		<WMSettingsFunds
			onChangeField={ () => {} }
			onSubmitForm={ () => {} }
			info={ {
				creditCardFunds: initialFundsState,
				settings: initialSettingsState
			} }
		/>
	);

	describe('Rendering ::', () => {
		it('should be wrapped in <WMCard />', () => {
			const wrapper = shallowRenderComponent();
			const component = wrapper.find(WMCard);
			expect(component.type()).toEqual(WMCard);
		});

		it('should have a <WMCardHeader />', () => {
			const wrapper = shallowRenderComponent();
			const cardHeader = wrapper.find(WMCardHeader);
			expect(cardHeader.type()).toEqual(WMCardHeader);
		});

		it('should have a <WMCardHeader /> that acts as expander', () => {
			const wrapper = shallowRenderComponent();
			const cardHeader = wrapper.find(WMCardHeader);
			expect(cardHeader.prop('actAsExpander')).toBeTruthy();
		});

		it('should have a <WMCardHeader /> with expandable button', () => {
			const wrapper = shallowRenderComponent();
			const cardHeader = wrapper.find(WMCardHeader);
			expect(cardHeader.prop('showExpandableButton')).toBeTruthy();
		});

		it('should have a <WMCardText />', () => {
			const wrapper = shallowRenderComponent();
			const component = wrapper.find(WMCardText);
			expect(component.type()).toEqual(WMCardText);
		});

		it('should have an expandable <WMCardText />', () => {
			const wrapper = shallowRenderComponent();
			const cardText = wrapper.find(WMCardText);
			expect(cardText.prop('expandable')).toBeTruthy();
		});

		it('should have an icon', () => {
			const wrapper = shallowRenderComponent();
			const icon = wrapper.find('#wm-settings-funds__icon');
			expect(icon.length).toEqual(1);
			expect(icon.prop('src').indexOf('fund.svg')).not.toEqual(-1);
			expect(icon).toHaveProp('alt');
			expect(icon.prop('style')).toEqual(baseStyles.cardIcon);
		});

		describe('Funding Option Tiles ::', () => {
			it('should render tiles for each one found in "content"', () => {
				const wrapper = shallowRenderComponent();
				const numberOfTiles = content.length;
				expect(wrapper.find(FundingOptionTile).length).toEqual(numberOfTiles);
			});
		});
	});
});
