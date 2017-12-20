import PropTypes from 'prop-types';
import React from 'react';
import {
	WMModal,
	WMAppBar,
	WMIconButton
} from '@workmarket/front-end-components';
import NavigationClose from 'material-ui/svg-icons/navigation/close';
import WMNationalIdTable from '../WMNationalIdTable';
import styles from './styles';

export const CloseIcon = ({
	closeModal
}) => (
	<WMIconButton
		iconStyle={ styles.modal.icon }
		onFocus={ closeModal }
	>
		<NavigationClose />
	</WMIconButton>
);

CloseIcon.propTypes = {
	closeModal: PropTypes.func.isRequired
};

const WMNationalIdModal = ({
	isOpen,
	openModal,
	closeModal
}) => (
	<span>
		<a
			data-component-identifier="wm-national-id-modal__trigger"
			onClick={ openModal }
		>What is this?</a>

		<WMModal
			open={ isOpen }
			bodyStyle={ styles.modal }
			onRequestClose={ closeModal }
		>
			<WMAppBar
				title="National ID"
				showMenuIconButton={ false }
				iconElementRight={ <CloseIcon closeModal={ closeModal } /> }
			/>

			<div
				data-component-identifier="wm-national-id-modal__body"
				style={ styles.modal.body }
			>
				<p>A national identification number is used by the governments of many countries as a means of identifying their citizens, permanent residents, and temporary residents for the purposes of work, taxation, government benefits, health care, and other governmentally-related functions. The number appears on identity documents issued by several of these countries.</p>
				<WMNationalIdTable />
			</div>
		</WMModal>
	</span>
);

WMNationalIdModal.propTypes = {
	isOpen: PropTypes.bool.isRequired,
	openModal: PropTypes.func.isRequired,
	closeModal: PropTypes.func.isRequired
};

export default WMNationalIdModal;
