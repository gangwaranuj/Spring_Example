import PropTypes from 'prop-types';
import React from 'react';
import {
	WMCard,
	WMCardText,
	WMCardActions,
	WMFontIcon,
	WMRaisedButton
} from '@workmarket/front-end-components';
import styles from './styles';

const WMScreeningMessage = ({
	icon,
	children,
	buttonLabel,
	link
}) => (
	<WMCard style={ styles.card }>
		<WMFontIcon
			className="material-icons"
			color="#8d9092"
			id="wm-screening-success-icon"
			style={ styles.icon }
		>
			{ icon }
		</WMFontIcon>
		<WMCardText>
			{ children }
		</WMCardText>
		<WMCardActions style={ styles.actions }>
			<a href={ link }>
				<WMRaisedButton
					label={ buttonLabel }
					backgroundColor="#5bc75d"
					labelColor="#fff"
					style={ styles.button }
				/>
			</a>
		</WMCardActions>
	</WMCard>
);

WMScreeningMessage.propTypes = {
	icon: PropTypes.string.isRequired,
	children: PropTypes.node.isRequired,
	buttonLabel: PropTypes.string.isRequired,
	link: PropTypes.string.isRequired
};

export default WMScreeningMessage;
