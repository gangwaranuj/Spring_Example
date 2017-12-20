import PropTypes from 'prop-types';
import React from 'react';
import { WMMessageBanner } from '@workmarket/front-end-components';
import { createChild } from '../../utils/visuals';

const ValidityBanner = ({ validity }) => (
	<WMMessageBanner status={ validity.valid ? 'notice' : 'error' } hideDismiss>
		{ createChild(validity) }
	</WMMessageBanner>
);

ValidityBanner.propTypes = {
	validity: PropTypes.shape({
		valid: PropTypes.bool,
		reason: PropTypes.string
	})
};

export default ValidityBanner;
