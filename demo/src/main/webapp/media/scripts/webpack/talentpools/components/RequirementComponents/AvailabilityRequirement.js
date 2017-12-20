import PropTypes from 'prop-types';
import React from 'react';
import { WMAvailability } from '@workmarket/front-end-patterns';
import { connect } from 'react-redux';
import moment from 'moment';
import * as actions from '../../actions';


const mapStateToProps = ({ requirementsData }) => ({
	availabilityEndTime: moment(requirementsData.get('availabilityEndTime')).toDate(),
	availabilityStartTime: moment(requirementsData.get('availabilityStartTime')).toDate(),
	day: requirementsData.get('day')
});

const mapDispatchToProps = (dispatch) => {
	return {
		onChangeField: (name, value) => {
			dispatch(actions.changeRequirementField(name, value));
		},
		addRequirement: (day, availabilityStartTime, availabilityEndTime) => {
			const fromTime = moment(availabilityStartTime).format('hh:mmA');
			const toTime = moment(availabilityEndTime).format('hh:mmA');
			const formattedName = `${day.name} - ${fromTime} to ${toTime}`;
			const weekdayObject = {
				name: formattedName,
				id: day.id
			};
			const data = {
				$type: 'AvailabilityRequirement',
				$humanTypeName: 'Availability',
				requirable: weekdayObject,
				dayOfWeek: day.id,
				fromTime,
				toTime
			};
			dispatch(actions.addRequirement(data, true, 'time'));
		}
	};
};

const AvailabilityRequirement = ({
	availabilityEndTime,
	availabilityStartTime,
	day,
	addRequirement,
	onChangeField
}) => (
	<WMAvailability
		availabilityEndTime={ availabilityEndTime }
		availabilityStartTime={ availabilityStartTime }
		day={ day }
		addRequirement={ addRequirement }
		dayLabel="Select Weekday"
		hide="abbreviation"
		onChangeField={ onChangeField }
		style={ { display: 'block', width: '525px', float: 'right' } }
		timePickerStyle={ { width: '100%' } }
		buttonWrapper={ { display: 'flex', float: 'right' } }
		autoOk
	/>
);

AvailabilityRequirement.propTypes = {
	availabilityEndTime: PropTypes.instanceOf(Date),
	availabilityStartTime: PropTypes.instanceOf(Date),
	day: PropTypes.object, // eslint-disable-line
	addRequirement: PropTypes.func.isRequired,
	onChangeField: PropTypes.func.isRequired
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(AvailabilityRequirement);
