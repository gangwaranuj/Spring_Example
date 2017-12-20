import React from 'react';
import { shallow } from 'enzyme';
import { WMTabs } from '@workmarket/front-end-components';
import { initialState } from '../../../reducers/index';
import TalentPoolManage from '../template';

describe('<TalentPoolManage />', () => {
	const renderComponent = (
		state = initialState,
		onChangeTab = () => {},
		onCloseDrawer = () => {},
		switchToInvite = () => {}
	) => shallow(
		<TalentPoolManage
			talentPoolData={ state.get('rootData') }
			onChangeTab={ onChangeTab }
			onCloseDrawer={ onCloseDrawer }
			switchToInvite={ switchToInvite }
		/>
	);
	let wrapper;

	beforeEach(() => {
		wrapper = renderComponent();
	});

	describe('Rendering', () => {
		it('should have a WMTabs', () => {
			const component = wrapper.find(WMTabs);
			expect(component).toHaveLength(1);
		});

		it('WMTabs should have 2 tabs', () => {
			const component = wrapper.find(WMTabs);
			expect(component.prop('tabs')).toHaveLength(2);
		});
	});
});
