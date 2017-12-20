import { saveMode } from '../creation';
import * as types from '../../constants/creationActionTypes';

describe('Assignment Creation Reducers :: ', () => {
	describe('Save mode reducer', () => {

		it('should return initial state', () => {
			const foo = void 0;
			expect(saveMode(foo, {})).toEqual('new');
		});

		it('should handle an update to save mode', () => {
			const value = 'update';
			const type = types.UPDATE_SAVE_MODE;
			expect(saveMode('', { type, value })).toEqual('update');
		});
	});
});
