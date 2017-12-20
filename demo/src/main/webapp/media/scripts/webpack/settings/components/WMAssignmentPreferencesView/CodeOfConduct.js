/* eslint-disable react/require-default-props */
import PropTypes from 'prop-types';
import React from 'react';
import {
	WMRaisedButton,
	WMModal
} from '@workmarket/front-end-components';
import styles from './styles';

const CodeOfConduct = ({
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
			src={ `${mediaPrefix}/images/settings/assignment.code.conduct.jpg` }
			style={ styles.modalAgreement }
			alt="Payment Terms Img"
		/>
	</WMModal>
);

CodeOfConduct.propTypes = {
	open: PropTypes.bool,
	handleClose: PropTypes.func
};

export default CodeOfConduct;
