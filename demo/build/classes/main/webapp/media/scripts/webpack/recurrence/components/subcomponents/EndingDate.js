import PropTypes from 'prop-types';
import React from 'react';
import {
  WMDatePicker
} from '@workmarket/front-end-components';
import moment from 'moment';
import {
	formatDate
} from '../../utils/visuals';

const EndingDate = ({
	endDate,
	handleEndDate,
	minDate,
	maxDate,
	disabled = false
}) => (
	<div style={ { display: 'table', marginLeft: '1em' } }>
		<span style={ { display: 'table-cell', verticalAlign: 'middle' } }> Ends </span>
		<WMDatePicker
			closeOnSelect
			cancelLabel={ <span> Cancel </span> }
			container={ 'dialog' }
			defaultDate={ endDate.toDate() }
			id={ 'dayOfMonth' }
			disableYearSelection
			disabled={ disabled }
			firstDayOfWeek={ 1 }
			formatDate={ formatDate }
			onChange={ (a, b) => handleEndDate(moment(b)) }
			style={ { marginLeft: '1em' } }
			dialogContainerStyle={ { zIndex: '10001' } }
			value={ endDate.toDate() }
			minDate={ minDate }
			maxDate={ maxDate }
		/>
	</div>
);

EndingDate.propTypes = {
  endDate: PropTypes.object, //eslint-disable-line
  minDate: PropTypes.object, //eslint-disable-line
  maxDate: PropTypes.object, //eslint-disable-line
	handleEndDate: PropTypes.func,
	disabled: PropTypes.bool
};

export default EndingDate;
