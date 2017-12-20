import { connect } from 'react-redux';
import Component from '../components/add_pricing';
import Actions from '../actions/pricing';

const mapStateToProps = ({ pricing }) => {
	return pricing;
};

const mapDispatchToProps = (dispatch) => {
	return {
		updatePricingMode: value => dispatch(Actions.updatePricingMode(value)),
		updatePricingType: value => dispatch(Actions.updatePricingType(value)),
		updatePricingFlatPrice: value => dispatch(Actions.updatePricingFlatPrice(value)),
		updatePricingPerHourPrice: value => dispatch(Actions.updatePricingPerHourPrice(value)),
		updatePricingMaxNumberOfHours: value => dispatch(Actions.updatePricingMaxNumberOfHours(value)),
		updatePricingPerUnitPrice: value => dispatch(Actions.updatePricingPerUnitPrice(value)),
		updatePricingMaxNumberOfUnits: value => dispatch(
			Actions.updatePricingMaxNumberOfUnits(Number.isNaN(+value) ? value : +value)
		),
		updatePricingInitialPerHourPrice: value => dispatch(
			Actions.updatePricingInitialPerHourPrice(value)
		),
		updatePricingInitialNumberOfHours: value => dispatch(
			Actions.updatePricingInitialNumberOfHours(value)
		),
		updatePricingAdditionalPerHourPrice: value => dispatch(
			Actions.updatePricingAdditionalPerHourPrice(value)
		),
		updatePricingMaxBlendedNumberOfHours: value => dispatch(
			Actions.updatePricingMaxBlendedNumberOfHours(value)
		),
		updatePaymentTermsDays: value => dispatch(
			Actions.updatePaymentTermsDays(Number.isNaN(+value) ? value : +value)
		),
		updatePricingOfflinePayment: value => dispatch(
			Actions.updatePricingOfflinePayment(value)
		),
		updatePricingDisablePriceNegotiation: value => dispatch(
			Actions.updatePricingDisablePriceNegotiation(value)
		)
	};
};

export default connect(mapStateToProps, mapDispatchToProps)(Component);
