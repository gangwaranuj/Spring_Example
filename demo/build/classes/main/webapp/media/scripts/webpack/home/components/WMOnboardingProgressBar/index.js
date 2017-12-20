import { connect } from 'react-redux';
import template from './template';
import { getOnboardingProgress } from '../../actions';

const mapDispatchToProps = (dispatch) => {
	return {
		getOnboardingProgress: () => {
			dispatch(getOnboardingProgress());
		}
	};
};

const mapStateToProps = (state) => {
	return state;
};

const WMOnboardingProgressBar = connect(
	mapStateToProps,
	mapDispatchToProps
)(template);

export default WMOnboardingProgressBar;
