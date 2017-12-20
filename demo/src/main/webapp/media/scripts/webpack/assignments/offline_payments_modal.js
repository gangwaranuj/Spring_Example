import React from 'react';
import ReactDOM, { render } from 'react-dom';
import WMOfflinePaymentsModal from './components/WMOfflinePaymentsModal';

const launchOfflinePaymentsModal = (
	isWorker,
	ctaLabel,
	onAgree,
	onClose,
	companyName
) => {
	const offlinePaymentsModalDiv = document.createElement('div');
	offlinePaymentsModalDiv.setAttribute('id', 'offlinePaymentsModal');
	document.body.appendChild(offlinePaymentsModalDiv);
	const unmountModal = () => {
		ReactDOM.unmountComponentAtNode(offlinePaymentsModalDiv);
		onClose();
	};

	render(
		<WMOfflinePaymentsModal
			companyName={ companyName }
			isWorker={ isWorker }
			onAgree={ onAgree }
			onClose={ unmountModal }
			ctaLabel={ ctaLabel }
		/>,
		offlinePaymentsModalDiv
	);
};

export default launchOfflinePaymentsModal;
