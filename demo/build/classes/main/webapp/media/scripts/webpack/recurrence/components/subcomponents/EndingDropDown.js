import PropTypes from 'prop-types';
/* eslint-disable jsx-a11y/no-static-element-interactions */

import React from 'react';
import {
  WMDropDownMenu
} from '@workmarket/front-end-components';
import {
  findDropDownMenuAndFix,
  populateDropDown
} from '../../utils/visuals';
import {
  dynamicallyRestrictPopulate
} from '../../utils';

const EndingDropDown = ({
  repetitions,
  handleRepeat,
  maxRepeat,
  frequency,
  startDate,
  frequencyModifier
}) => (
	<div style={ { display: 'table', marginLeft: '1em' } } onClick={ findDropDownMenuAndFix }>
		<span style={ { display: 'table-cell', verticalAlign: 'middle' } }> Ends after </span>
		<WMDropDownMenu
			value={ repetitions }
			onChange={ (e, i, v) => handleRepeat(v) }
			style={ { marginTop: '-.8em' } }
		>
			{ populateDropDown(
        	dynamicallyRestrictPopulate(startDate, maxRepeat, frequency, frequencyModifier)
        )
    	}
		</WMDropDownMenu>
		<span
			style={ { display: 'table-cell', verticalAlign: 'middle' } }
		>
			occurences
		</span>
	</div>
);

EndingDropDown.propTypes = {
	repetitions: PropTypes.string,
	handleRepeat: PropTypes.func,
	maxRepeat: PropTypes.string,
	frequency: PropTypes.string,
  startDate: PropTypes.object, //eslint-disable-line
	frequencyModifier: PropTypes.string
};

export default EndingDropDown;
