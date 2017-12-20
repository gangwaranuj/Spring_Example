import React from 'react';
import { render } from 'react-dom';
import WMCompanyPagesSPA from 'wm-company-pages-spa';
import getCSRFToken from '../funcs/getCSRFToken';

const renderCompanyPage = (
	companyNumber,
	csrf = getCSRFToken()
) => {
	const companyPageDiv = document.getElementById('wm-company-page-app');

	render(
		<WMCompanyPagesSPA
			companyNumber={ companyNumber }
			csrf={ csrf }
		/>,
		companyPageDiv
	);
};
renderCompanyPage(config.companyNumber, getCSRFToken());
