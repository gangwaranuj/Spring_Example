import PropTypes from 'prop-types';
import React from 'react';
import moment from 'moment';
import {
  WMDatePicker
} from '@workmarket/front-end-components';
import {
	formatDate
} from '../../utils/visuals';

const StartingDate = ({ startDate, handleStartDate, disabled = true }) => (
	<div style={ { display: 'table', marginLeft: '1em' } }>
		<span style={ { display: 'table-cell', verticalAlign: 'middle', marginRight: '1em' } }> Starts </span>
		<WMDatePicker
			disabled={ disabled }
			closeOnSelect
			cancelLabel={ <span> Cancel </span> }
			container={ 'dialog' }
			defaultDate={ startDate.toDate() }
			id={ 'dayOfMonth' }
			disableYearSelection
			firstDayOfWeek={ 1 }
			formatDate={ formatDate }
			style={ { marginLeft: '1em' } }
			dialogContainerStyle={ { zIndex: '10001' } }
			onChange={ (a, b) => handleStartDate(moment(b)) }
			value={ startDate.toDate() }
			minDate={ moment().toDate() }
		/>
	</div>
);

StartingDate.propTypes = {
	startDate: PropTypes.object, //eslint-disable-line
	handleStartDate: PropTypes.func,
	disabled: PropTypes.bool
};

export default StartingDate;
