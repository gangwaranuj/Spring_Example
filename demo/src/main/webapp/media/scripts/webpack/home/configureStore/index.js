import { createStore, applyMiddleware } from 'redux';
import reduxthunk from 'redux-thunk';
import { createLogger } from 'redux-logger';
import { composeWithDevTools } from 'redux-devtools-extension/developmentOnly';
import reducer from './../reducers';

const middlewares = [reduxthunk];
const logger = createLogger();

if (process.env.NODE_ENV === 'development') {
	middlewares.push(logger);
}

const composeEnhancers = composeWithDevTools({
	serialize: true
});

const configureStore = () => {
	return createStore(
		reducer,
		composeEnhancers(
			applyMiddleware(...middlewares)
		)
	);
};

export default configureStore;
