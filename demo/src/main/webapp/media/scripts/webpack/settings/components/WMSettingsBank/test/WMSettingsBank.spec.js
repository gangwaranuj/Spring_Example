import React from 'react';
import { shallow } from 'enzyme';
import {
	WMCard,
	WMCardHeader,
	WMCardText,
	WMCardActions,
	WMRaisedButton,
	WMFormRow,
	WMTextField,
	WMSelectField,
	WMRadioButton,
	WMRadioButtonGroup,
	WMFontIcon,
	WMMenuItem,
	WMMessageBanner
} from '@workmarket/front-end-components';
import WMSettingsBank from '../template';
import WMFetchSelect from '../../WMFetchSelect';
import { initialFundsState } from '../../../reducers/fundsReducer';
import { initialSettingsState } from '../../../reducers/settingsReducer';
import baseStyles from '../../styles';

describe('<WMSettingsBank />', () => {
	const shallowRenderComponent = (
		info = {
			funds: initialFundsState,
			settings: initialSettingsState
		},
		onSubmitFundsForm = () => {},
		onChangeFundsField = () => {},
		onBlurField = () => {},
		formValid = true
	) => shallow(
		<WMSettingsBank
			info={ info }
			onSubmitFundsForm={ onSubmitFundsForm }
			onChangeFundsField={ onChangeFundsField }
			onBlurField={ onBlurField }
			formValid={ formValid }
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
			const icon = wrapper.find('#wm-settings-bank__icon');
			expect(icon.length).toEqual(1);
			expect(icon.prop('src').indexOf('link.bank.account.svg')).not.toEqual(-1);
			expect(icon).toHaveProp('alt');
			expect(icon.prop('style')).toEqual(baseStyles.cardIcon);
		});

		it('should not have a "check_circle" <WMFontIcon />', () => {
			const wrapper = shallowRenderComponent();
			const icons = wrapper.find('#wm-settings-profile-completed-icon');
			expect(icons.length).toEqual(0);
		});

		const componentFields = [
			{
				name: 'Bank Name',
				row: 'settings__bankNameRow',
				textField: 'settings__bankName'
			},
			{
				name: 'Routing Number',
				row: 'settings__routingNumberRow',
				textField: 'settings__routingNumber'
			},
			{
				name: 'Account Number',
				row: 'settings__accountNumberRow',
				textField: 'settings__accountNumber'
			},
			{
				name: 'Confirm Account Number',
				row: 'settings__confirmAccountNumberRow',
				textField: 'settings__confirmAccountNumber'
			}
		];

		componentFields.forEach((component) => {
			describe(component.name, () => {
				const row = component.row;

				it('should render 1 <WMFormRow />', () => {
					const wrapper = shallowRenderComponent();
					const rowComponent = wrapper.find(`[data-component-identifier="${row}"]`);
					expect(rowComponent.type()).toEqual(WMFormRow);
				});

				it('should render 1 <WMTextField />', () => {
					const wrapper = shallowRenderComponent();
					const rowComponent = wrapper.find(`[data-component-identifier="${row}"]`);
					expect(rowComponent.find(WMTextField).exists()).toBeTruthy();
				});
			});
		});

		describe('Country Select', () => {
			it('should render 1 <WMFormRow />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="settings__countryRow"]');
				expect(component.type()).toEqual(WMFormRow);
			});

			it('should render 1 <WMSelectField />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="settings__country"]');
				expect(component.type()).toEqual(WMSelectField);
			});

			it('should be disabled', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="settings__country"]');
				expect(component.prop('disabled')).toBeTruthy();
			});

			it('should have 1 WMMenuItem for USA', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="settings__country"]');
				const menuItem = component.find(WMMenuItem);
				expect(menuItem.prop('value')).toEqual('USA');
				expect(menuItem.prop('primaryText')).toEqual('USA');
			});
		});

		describe('Payment Method Select', () => {
			it('should render 1 <WMFormRow />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="settings__paymentMethodRow"]');
				expect(component.type()).toEqual(WMFormRow);
			});

			it('should render 1 <WMSelectField />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="settings__paymentMethod"]');
				expect(component.type()).toEqual(WMSelectField);
			});

			it('should be disabled', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="settings__paymentMethod"]');
				expect(component.prop('disabled')).toBeTruthy();
			});

			it('should have 1 WMMenuItem for Bank Account', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="settings__paymentMethod"]');
				const menuItem = component.find(WMMenuItem);
				expect(menuItem.prop('value')).toEqual('Bank Account');
				expect(menuItem.prop('primaryText')).toEqual('Bank Account');
			});
		});

		describe('Name On Account Select', () => {
			it('should render 1 <WMFormRow />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find(
					'[data-component-identifier="settings__nameOnAccountTypeRow"]'
				);
				expect(component.type()).toEqual(WMFormRow);
			});

			it('should render 1 <WMFetchSelect />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find(WMFetchSelect);
				expect(component.length).toEqual(1);
			});

			it('should have a fetchURL prop', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find(WMFetchSelect);
				expect(component.prop('fetchURL')).toEqual('/employer/v2/settings/funds/accounts/admins');
			});

			it('should have a onSelectChange prop', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find(WMFetchSelect);
				expect(component).toHaveProp('onSelectChange');
			});
		});

		describe('Account Type Radios', () => {
			it('should render 1 <WMRadioButtonGroup />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find(
					'[data-component-identifier="settings__fundsAccountTypeGroup"]'
				);
				expect(component.type()).toEqual(WMRadioButtonGroup);
				expect(component.length).toEqual(1);
			});

			it('should render 2 <WMRadioButton />s within <WMRadioButtonGroup />', () => {
				const wrapper = shallowRenderComponent();
				const row = wrapper.find('[data-component-identifier="settings__fundsAccountTypeGroup"]');
				const fields = row.find(WMRadioButton);

				expect(fields.length).toEqual(2);
				fields.forEach(field => expect(field.type()).toEqual(WMRadioButton));
			});

			it('should render a radio button for checking and savings', () => {
				const wrapper = shallowRenderComponent();
				const row = wrapper.find('[data-component-identifier="settings__fundsAccountTypeGroup"]');
				const fields = row.find(WMRadioButton);
				const checkingField = fields.get(0);
				expect(checkingField.props.value).toEqual('checking');
				const savingsField = fields.get(1);
				expect(savingsField.props.value).toEqual('savings');
			});
		});

		it('should render a check image', () => {
			const wrapper = shallowRenderComponent();
			const checkImage = wrapper.find('#settings__checkImage');
			expect(checkImage.exists()).toBeTruthy();
			expect(checkImage.prop('src')).toEqual('/media/images/samplecheck.gif');
		});

		describe('Submit button', () => {
			it('should render 1 <WMCardActions />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="settings__actions"]');
				expect(component.type()).toEqual(WMCardActions);
			});

			it('should render 1 <WMRaisedButton />', () => {
				const wrapper = shallowRenderComponent();
				const component = wrapper.find('[data-component-identifier="settings__fundsSubmit"]');
				expect(component.type()).toEqual(WMRaisedButton);
				expect(component.prop('primary')).toEqual(true);
			});
		});

		describe('Funds Submitted ::', () => {
			let wrapper;
			const settingsState = initialSettingsState.set('fundsSubmitted', true);
			const info = {
				settings: settingsState,
				funds: initialFundsState
			};

			beforeEach(() => {
				wrapper = shallowRenderComponent(info);
			});

			it('should have a <WMCardHeader /> without expandable button', () => {
				const cardHeader = wrapper.find(WMCardHeader);
				expect(cardHeader.prop('showExpandableButton')).toBeFalsy();
			});

			it('should have a "check_circle" <WMFontIcon />', () => {
				const submittedIcon = wrapper.find('#wm-settings-profile-completed-icon');
				expect(submittedIcon.exists()).toBeTruthy();
				expect(submittedIcon.type()).toEqual(WMFontIcon);
				expect(submittedIcon.prop('children')).toEqual('check_circle');
			});

			it('should allow expanded state to be true if funds card not submitted', () => {
				wrapper = shallowRenderComponent();
				wrapper.instance().handleExpandChange(true);
				expect(wrapper.state('expanded')).toBeTruthy();
			});

			it('should not set expanded state to true if funds card gets submitted', () => {
				wrapper.instance().handleExpandChange(true);
				expect(wrapper.state('expanded')).toBeFalsy();
			});

			it('should set expanded state to false if funds are submitted', () => {
				wrapper = shallowRenderComponent();
				wrapper.setState({ expanded: true });
				expect(wrapper.state('expanded')).toBeTruthy();
				wrapper.instance().componentWillReceiveProps({ info });
				expect(wrapper.state('expanded')).toBeFalsy();
			});

			it('should not allow card to expand if card has been submitted', () => {
				wrapper.instance().handleExpandChange();
				const card = wrapper.find(WMCard);
				expect(card.prop('expanded')).toBeFalsy();
			});
		});

		describe('Funds Error ::', () => {
			let wrapper;
			const settingsState = initialSettingsState.set('fundsError', ['Something went wrong']);
			const info = {
				settings: settingsState,
				funds: initialFundsState
			};

			beforeEach(() => {
				wrapper = shallowRenderComponent(info);
			});

			it('should display a <WMMessageBanner />', () => {
				const banner = wrapper.find(WMMessageBanner);
				expect(banner.exists()).toBeTruthy();
				expect(banner.prop('status')).toEqual('error');
				expect(banner.prop('children')).toEqual(settingsState.get('fundsError')[0]);
			});
		});
	});
});
