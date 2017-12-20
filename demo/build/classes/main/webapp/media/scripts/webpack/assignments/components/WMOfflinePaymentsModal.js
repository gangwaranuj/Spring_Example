import PropTypes from 'prop-types';
import React from 'react';
import {
	WMModal,
	WMHeading,
	WMRaisedButton,
	WMFlatButton
} from '@workmarket/front-end-components';

const getConfirmationCopy = (isWorker, companyName) => {
	let confirmCopy = 'This assignment is set for off-platform payment. As such, ';
	if (isWorker) {
		confirmCopy += `${companyName} is responsible for all payment and payment discussions outside the Work Market platform.`;
	} else {
		confirmCopy += 'you are responsible for all payment and payment discussions between you and the worker outside the Work Market platform.';
	}
	return confirmCopy;
};

const WMOfflinePaymentsModal = ({
	isWorker,
	ctaLabel,
	onAgree,
	onClose,
	companyName
}) => {
	return (
		<WMModal
			title="Off-Platform Payment Reminder"
			open
			actions={ [
				<WMFlatButton
					label="Cancel"
					primary
					onClick={ onClose }
				/>,
				<WMRaisedButton
					label={ ctaLabel }
					primary
					onClick={ () => {
						onAgree();
						onClose();
					} }
				/>
			] }
			onRequestClose={ onClose }
		>
			<WMHeading
				level="3"
			>
				{ getConfirmationCopy(isWorker, companyName) }
			</WMHeading>
		</WMModal>
	);
};

export default WMOfflinePaymentsModal;

WMOfflinePaymentsModal.propTypes = {
	isWorker: PropTypes.bool.isRequired,
	ctaLabel: PropTypes.string.isRequired,
	onAgree: PropTypes.func.isRequired,
	onClose: PropTypes.func.isRequired,
	companyName: PropTypes.string
};

WMOfflinePaymentsModal.defaultProps = {
	companyName: ''
};
