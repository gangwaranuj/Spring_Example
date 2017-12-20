import { connect } from 'react-redux';
import Component from '../components/add_schedule';
import {
	toggleScheduleRange,
	updateScheduleFrom,
	updateScheduleThrough,
	toggleConfirmationRequired,
	updateConfirmationLeadTime,
	toggleCheckinRequired,
	toggleCheckinCallRequired,
	updateCheckinContactName,
	updateCheckinContactPhone,
	toggleCheckoutNoteDisplayed,
	updateCheckoutNote
} from '../actions/schedule';

const mapStateToProps = ({ schedule }) => {
	return schedule;
};

const mapDispatchToProps = (dispatch) => {
	return {
		toggleScheduleRange: value => dispatch(toggleScheduleRange(value)),
		updateScheduleFrom: value => dispatch(updateScheduleFrom(value)),
		updateScheduleThrough: value => dispatch(updateScheduleThrough(value)),
		toggleConfirmationRequired: () => dispatch(toggleConfirmationRequired()),
		updateConfirmationLeadTime: value => dispatch(updateConfirmationLeadTime(value)),
		toggleCheckinRequired: () => dispatch(toggleCheckinRequired()),
		toggleCheckinCallRequired: () => dispatch(toggleCheckinCallRequired()),
		updateCheckinContactName: value => dispatch(updateCheckinContactName(value)),
		updateCheckinContactPhone: value => dispatch(updateCheckinContactPhone(value)),
		toggleCheckoutNoteDisplayed: () => dispatch(toggleCheckoutNoteDisplayed()),
		updateCheckoutNote: value => dispatch(updateCheckoutNote(value))
	};
};

export default connect(mapStateToProps, mapDispatchToProps)(Component);
