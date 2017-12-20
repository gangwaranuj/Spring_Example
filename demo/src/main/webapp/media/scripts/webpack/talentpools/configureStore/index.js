import { combineReducers, createStore, applyMiddleware } from 'redux';
import reduxthunk from 'redux-thunk';
import { createLogger } from 'redux-logger';
import { composeWithDevTools } from 'redux-devtools-extension/developmentOnly';
import { rootData, requirementsData, formData, messagesData, messageFormData } from './../reducers/index';
import serverEvents from './../middleware/serverEvents';

const middlewares = [reduxthunk, serverEvents];
const logger = createLogger();

if (process.env.NODE_ENV === 'development') {
	middlewares.push(logger);
}

const composeEnhancers = composeWithDevTools({
	serialize: true
});

const configureStore = () => {
	return createStore(
		combineReducers({
			rootData,
			requirementsData,
			formData,
			messagesData,
			messageFormData
		}),
		composeEnhancers(
			applyMiddleware(...middlewares)
		)
	);
};

export default configureStore;
