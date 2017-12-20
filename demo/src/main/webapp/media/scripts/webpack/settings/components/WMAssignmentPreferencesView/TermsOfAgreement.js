/* eslint-disable react/require-default-props */
import PropTypes from 'prop-types';
import React from 'react';
import {
	WMRaisedButton,
	WMModal
} from '@workmarket/front-end-components';
import styles from './styles';

const TermsOfAgreement = ({
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
			src={ `${mediaPrefix}/images/settings/assignment.terms.jpg` }
			style={ styles.modalAgreement }
			alt="Payment Terms Img"
		/>
	</WMModal>
);

TermsOfAgreement.propTypes = {
	open: PropTypes.bool,
	handleClose: PropTypes.func
};

export default TermsOfAgreement;

