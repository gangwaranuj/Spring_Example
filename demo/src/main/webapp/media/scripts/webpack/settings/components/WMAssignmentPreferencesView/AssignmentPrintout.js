/* eslint-disable react/require-default-props */
import PropTypes from 'prop-types';
import React from 'react';
import {
	WMRaisedButton,
	WMModal
} from '@workmarket/front-end-components';
import styles from './styles';

const AssignmentPrintout = ({
	open,
	handleClose
}) => (
	<WMModal
		title="Sample Assignment Printout"
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
			src={ `${mediaPrefix}/images/settings/assignment.printout.jpg` }
			style={ styles.modalPrintout }
			alt="Payment Terms Img"
		/>
	</WMModal>
);

AssignmentPrintout.propTypes = {
	open: PropTypes.bool,
	handleClose: PropTypes.func
};

export default AssignmentPrintout;
