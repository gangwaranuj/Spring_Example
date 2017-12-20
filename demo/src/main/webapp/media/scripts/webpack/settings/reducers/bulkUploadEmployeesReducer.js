import { Map } from 'immutable';
import * as types from '../constants/actionTypes';

const initialBulkEmployeesState = Map({
});
export { initialBulkEmployeesState };

const bulkUploadEmployees = (state = initialBulkEmployeesState, action) => {
	switch (action.type) {
	case types.BULK_UPLOAD_EMPLOYEES_START:
		return state.set(action.id, Map({
			file: action.file,
			abort: action.abort,
			progress: 0
		}));

	case types.BULK_UPLOAD_EMPLOYEES_PROGRESS:
		return state.setIn([action.id, 'progress'], action.progress);

	case types.BULK_UPLOAD_EMPLOYEES_SUCCESS:
		return state.setIn([action.id, 'progress'], 100);

	case types.BULK_UPLOAD_EMPLOYEES_ERROR:
		return state
			.setIn([action.id, 'progress'], false)
			.setIn([action.id, 'error'], 'Upload has failed. Please try again.');

	case types.BULK_UPLOAD_EMPLOYEES_ABORT:
		return state.setIn([action.id, 'aborted'], true);

	default:
		return state;
	}
};

export default bulkUploadEmployees;
