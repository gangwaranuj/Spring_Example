import { connect } from 'react-redux';
import template from './template';

export const modalState = (state = false, { type }) => {
	switch (type) {
	case 'OPEN_MODAL':
		return true;

	case 'CLOSE_MODAL':
		return false;

	default:
		return state;
	}
};

export const mapStateToProps = (state) => ({ isOpen: state });

export const mapDispatchToProps = (dispatch) => ({
	openModal: () => dispatch({ type: 'OPEN_MODAL' }),
	closeModal: () => dispatch({ type: 'CLOSE_MODAL' })
});

export default connect(mapStateToProps, mapDispatchToProps)(template);
