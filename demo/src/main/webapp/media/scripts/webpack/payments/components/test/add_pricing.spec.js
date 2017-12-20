import React from 'react';
import { shallow } from 'enzyme';
import AddPricingComponent from '../add_pricing';

describe('<AddPricingComponent />', () => {
	const setup = () => {
		const stub = jest.fn();
		const props = {
			updatePricingMode: stub,
			updatePricingType: stub,
			updatePricingFlatPrice: stub,
			updatePricingPerHourPrice: stub,
			updatePricingMaxNumberOfHours: stub,
			updatePricingPerUnitPrice: stub,
			updatePricingMaxNumberOfUnits: stub,
			updatePricingInitialPerHourPrice: stub,
			updatePricingInitialNumberOfHours: stub,
			updatePricingAdditionalPerHourPrice: stub,
			updatePricingMaxBlendedNumberOfHours: stub,
			updatePaymentTermsDays: stub,
			setModuleValidation: stub
		};

		return {
			props
		};
	};

	describe('Rendering', () => {
		xit('should render calculation table if transation fee > 0', () => {
			const { props } = setup();
			const wrapper = shallow(<AddPricingComponent { ...props } />);
			wrapper.setState({ fee: 37 });
			expect(wrapper.find('.assignment-creation--payments-table').length).toEqual(1);
		});

		xit('should not render calculation table is transaction fee = 0', () => {
			const { props } = setup();
			const wrapper = shallow(<AddPricingComponent { ...props } />);
			wrapper.setState({ fee: 0 });
			expect(wrapper.find('.assignment-creation--payments-table').length).toEqual(0);
		});
	});
});
