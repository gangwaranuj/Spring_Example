/* eslint-disable react/require-default-props */
import PropTypes from 'prop-types';
import React from 'react';
import {
	WMRaisedButton,
	WMModal
} from '@workmarket/front-end-components';
import styles from './styles';

const RateAssignments = ({
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
		autoDetectWindowHeight
		autoScrollBodyContent
	>
		<img
			src={ `${mediaPrefix}/images/settings/assignment.rating.jpg` }
			style={ styles.modalRatings }
			alt="Payment Terms Img"
		/>
	</WMModal>
);

RateAssignments.propTypes = {
	open: PropTypes.bool,
	handleClose: PropTypes.func
};

export default RateAssignments;
