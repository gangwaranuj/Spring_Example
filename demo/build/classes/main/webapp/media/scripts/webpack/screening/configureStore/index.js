import { createStore, applyMiddleware } from 'redux';
import thunk from 'redux-thunk';
import { fieldReducer } from './../reducers';
import validationMiddleware from './../reducers/validationMiddleware';
import submitValidationMiddleware from './../reducers/submitValidationMiddleware';
import filterMiddleware from './../reducers/filterMiddleware';
import { initialState, processDrugConfig } from './state';

const configureStore = (config) => {
	const hydratedState = initialState.merge(processDrugConfig(config));

	return createStore(
		fieldReducer,
		hydratedState,
		applyMiddleware(
			thunk,
			filterMiddleware,
			validationMiddleware,
			submitValidationMiddleware
		)
	);
};

export default configureStore;
