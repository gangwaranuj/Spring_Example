import PropTypes from 'prop-types';
/* eslint-disable jsx-a11y/no-static-element-interactions */

import React from 'react';
import {
  WMDropDownMenu,
	WMMenuItem
} from '@workmarket/front-end-components';
import {
  findDropDownMenuAndFix,
  populateDropDown,
  unitHash
} from '../../utils/visuals';

const FrequencyDropDown = ({
	handleFrequency,
	frequency,
	maxFrequency,
	handleFrequencyModifier,
	frequencyModifier
}) => (
	<div style={ { display: 'table', marginLeft: '1em' } } onClick={ findDropDownMenuAndFix }>
		<span style={ { display: 'table-cell', verticalAlign: 'middle' } }> Repeats </span>
		<WMDropDownMenu
			value={ frequency }
			onChange={ (e, i, v) => handleFrequency(v) }
			style={ { marginTop: '-.8em' } }
		>
			<WMMenuItem primaryText="Daily" value="Daily" />
			<WMMenuItem primaryText="Weekly" value="Weekly" />
			<WMMenuItem primaryText="Monthly" value="Monthly" />
		</WMDropDownMenu>
		<span style={ { display: 'table-cell', verticalAlign: 'middle' } }> every </span>
		<WMDropDownMenu
			autoWidth value={ frequencyModifier }
			onChange={ (e, i, v) => handleFrequencyModifier(v) }
			style={ { marginTop: '-.8em' } }
		>
			{
        populateDropDown(maxFrequency, 1)
      }
		</WMDropDownMenu>
		<span style={ { display: 'table-cell', verticalAlign: 'middle' } }> {`${unitHash[frequency]}`} </span>
	</div>
);

FrequencyDropDown.propTypes = {
	handleFrequency: PropTypes.func,
	frequency: PropTypes.string,
	handleFrequencyModifier: PropTypes.func,
	maxFrequency: PropTypes.number,
	frequencyModifier: PropTypes.string
};

export default FrequencyDropDown;
