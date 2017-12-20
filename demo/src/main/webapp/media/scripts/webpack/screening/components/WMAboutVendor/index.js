import PropTypes from 'prop-types';
import React from 'react';
import { WMCard, WMCardText, WMCardMedia } from '@workmarket/front-end-components';
import styles from './styles';

const WMAboutVendor = ({
	children,
	prefix = `${mediaPrefix}`
}) => (
	<WMCard style={ styles.card }>
		<WMCardMedia style={ styles.media }>
			<img
				src={ `${prefix}/images/sterling-infosystems@3x.png` }
				role="presentation"
				alt="Sterling Drug Screening"
			/>
		</WMCardMedia>
		<WMCardText
			style={ styles.text }
			data-component-identifier="screening__vendorText"
		>
			{ children }
		</WMCardText>
	</WMCard>
);

WMAboutVendor.propTypes = {
	children: PropTypes.node.isRequired,
	prefix: PropTypes.string
};

export default WMAboutVendor;
