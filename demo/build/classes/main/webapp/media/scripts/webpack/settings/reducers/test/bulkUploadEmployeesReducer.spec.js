import { Map } from 'immutable';
import bulkUploadEmployees, { initialBulkEmployeesState } from '../bulkUploadEmployeesReducer';
import * as types from '../../constants/actionTypes';

describe('Bulk Upload Employee Reducer', () => {
	const uuid = 'd60c8fc3-13e8-4329-b48e-fa4c7564684d';
	const file = new File(
		['An awesome file.'],
		'filename-1.txt',
		{
			type: 'text/plain',
			lastModified: new Date()
		}
	);
	const abort = jest.fn();
	const progress = 15;
	const errorMessage = 'Upload has failed. Please try again.';
	const uploadStartState = Map({
		[uuid]: Map({
			file,
			abort,
			progress: 0
		})
	});
	const uploadProgressState = Map({
		[uuid]: Map({
			file,
			abort,
			progress
		})
	});

	it('should return the initial state', () => {
		expect(bulkUploadEmployees(undefined, {})).toEqual(initialBulkEmployeesState);
	});

	it('should handle `BULK_UPLOAD_EMPLOYEES_START` action', () => {
		const action = {
			type: types.BULK_UPLOAD_EMPLOYEES_START,
			id: uuid,
			file,
			abort
		};

		expect(bulkUploadEmployees(undefined, action)).toEqual(uploadStartState);
	});

	it('should handle `BULK_UPLOAD_EMPLOYEES_PROGRESS` action', () => {
		const action = {
			type: types.BULK_UPLOAD_EMPLOYEES_PROGRESS,
			id: uuid,
			progress
		};

		expect(bulkUploadEmployees(uploadStartState, action)).toEqual(uploadProgressState);
	});

	it('should handle `BULK_UPLOAD_EMPLOYEES_SUCCESS` action', () => {
		const action = {
			type: types.BULK_UPLOAD_EMPLOYEES_SUCCESS,
			id: uuid,
			progress
		};
		const expectedState = Map({
			[uuid]: Map({
				file,
				abort,
				progress: 100
			})
		});

		expect(bulkUploadEmployees(uploadProgressState, action)).toEqual(expectedState);
	});

	it('should handle `BULK_UPLOAD_EMPLOYEES_ERROR` action', () => {
		const action = {
			type: types.BULK_UPLOAD_EMPLOYEES_ERROR,
			id: uuid,
			error: [errorMessage]
		};
		const expectedState = Map({
			[uuid]: Map({
				file,
				abort,
				progress: false,
				error: errorMessage
			})
		});

		expect(bulkUploadEmployees(uploadProgressState, action)).toEqual(expectedState);
	});

	it('should handle `BULK_UPLOAD_EMPLOYEES_ABORT` action', () => {
		const action = {
			type: types.BULK_UPLOAD_EMPLOYEES_ABORT,
			id: uuid
		};
		const expectedState = Map({
			[uuid]: Map({
				file,
				abort,
				progress,
				aborted: true
			})
		});

		expect(bulkUploadEmployees(uploadProgressState, action)).toEqual(expectedState);
	});
});
