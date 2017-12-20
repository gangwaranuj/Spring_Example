import PropTypes from 'prop-types';
import React from 'react';
import { Provider } from 'react-redux';
import WMDrugScreen from '../WMDrugScreen';
import { DRUG_SCREEN, BACKGROUND_CHECK } from '../../constants/screeningTypes';

const Root = ({ store, type }) => (
	<Provider store={ store }>
		<div>
			{
				type === DRUG_SCREEN &&
					<WMDrugScreen />
			}
		</div>
	</Provider>
);

Root.propTypes = {
	store: PropTypes.object.isRequired,
	type: PropTypes.oneOf([DRUG_SCREEN, BACKGROUND_CHECK])
};

export default Root;
