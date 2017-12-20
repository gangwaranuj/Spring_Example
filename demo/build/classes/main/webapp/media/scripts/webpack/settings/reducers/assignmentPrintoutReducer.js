import { Map } from 'immutable';
import * as types from '../constants/actionTypes';

const initialAssignmentPrintoutState = Map({
});
export { initialAssignmentPrintoutState };

const assignmentPrintoutUpload = (state = initialAssignmentPrintoutState, action) => {
	switch (action.type) {
	case types.ASSIGNMENT_PRINTOUT_UPLOAD_START:
		return state.set(action.id, Map({
			file: action.file,
			abort: action.abort,
			progress: 0
		}));

	case types.ASSIGNMENT_PRINTOUT_UPLOAD_PROGRESS:
		return state.setIn([action.id, 'progress'], action.progress);

	case types.ASSIGNMENT_PRINTOUT_UPLOAD_SUCCESS:
		return state.setIn([action.id, 'progress'], 100);

	case types.ASSIGNMENT_PRINTOUT_UPLOAD_ERROR:
		return state
			.setIn([action.id, 'progress'], false)
			.setIn([action.id, 'error'], 'Upload has failed. Please try again.');

	case types.ASSIGNMENT_PRINTOUT_UPLOAD_ABORT:
		return state.setIn([action.id, 'aborted'], true);


	case types.ASSIGNMENT_PRINTOUT_UPLOAD_REMOVE:
		return state.delete(action.id);

	default:
		return state;
	}
};

export default assignmentPrintoutUpload;
