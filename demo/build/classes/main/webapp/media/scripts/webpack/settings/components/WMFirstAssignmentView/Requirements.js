/* eslint-disable react/require-default-props */
import PropTypes from 'prop-types';
import React from 'react';
import {
	WMRaisedButton,
	WMModal
} from '@workmarket/front-end-components';
import styles from './styles';

const Requirements = ({
	open,
	handleClose
}) => (
	<WMModal
		title="Sample Assignment"
		open={ open }
		onRequestClose={ handleClose }
		actions={
			<WMRaisedButton
				primary
				label="Dismiss"
				onClick={ handleClose }
			/>
		}
		autoScrollBodyContent
	>
		<img
			src={ `${mediaPrefix}/images/settings/assignment.requirements.jpg` }
			style={ styles.modalContent }
			alt="Requirements Img"
		/>
	</WMModal>
);

Requirements.propTypes = {
	open: PropTypes.bool,
	handleClose: PropTypes.func
};

export default Requirements;
