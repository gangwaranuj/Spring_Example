import React from 'react';
import { render } from 'react-dom';
import configureStore from './configureStore';
import Root from './components/Root';

const renderScreening = (type, config) => {
	const store = configureStore(config);

	render(
		<Root
			store={ store }
			type={ type }
		/>,
		document.querySelector('#wm-screening')
	);
};

export default renderScreening;
