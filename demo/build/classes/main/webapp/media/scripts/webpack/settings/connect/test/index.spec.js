import { initialState } from '../../reducers';
import { mapStateToProps, mapDispatchToProps } from '../../connect';
import * as types from '../../constants/actionTypes';

describe('Settings Mapping Functions :: ', () => {
	describe('mapStateToProps', () => {
		let state;

		beforeEach(() => {
			state = initialState;
		});

		it('should return an object', () => {
			expect(typeof mapStateToProps(state)).toEqual('object');
		});

		it('should return an object with property `info`', () => {
			const result = mapStateToProps(state);
			expect(result).toHaveProperty('info');
			expect(result.info).toEqual(initialState);
		});
	});

	describe('mapDispatchToProps', () => {
		let dispatch;
		let result;

		beforeEach(() => {
			dispatch = jest.fn();
			result = mapDispatchToProps(dispatch);
		});

		it('should return an object', () => {
			expect(typeof mapDispatchToProps(dispatch)).toEqual('object');
		});

		it('should return an object with property `onChangeField`', () => {
			expect(result).toHaveProperty('onChangeField');
			expect(typeof result.onChangeField).toEqual('function');
		});

		it('should fire `dispatch` when `onChangeField` is called', () => {
			const name = 'website';
			const value = 'http://guyfieri.com';
			const type = types.CHANGE_FIELD;
			result.onChangeField(name, value);
			expect(dispatch).toHaveBeenCalledWith({ name, type, value });
		});

		it('should return an object with property `getOnboardingProgress`', () => {
			expect(result).toHaveProperty('getOnboardingProgress');
			expect(typeof result.getOnboardingProgress).toBe('function');
		});

		it('should fire `dispatch` when `getOnboardingProgress` is called', () => {
			result.getOnboardingProgress();
			expect(dispatch).toHaveBeenCalledTimes(1);
		});

		it('should return an object with property `onGoogleAPILoaded`', () => {
			expect(result).toHaveProperty('onGoogleAPILoaded');
			expect(typeof result.onGoogleAPILoaded).toBe('function');
		});

		it('should fire `dispatch` when `onGoogleAPILoaded` is called', () => {
			result.onGoogleAPILoaded();
			expect(dispatch).toHaveBeenCalledTimes(1);
		});

		it('should return an object with property `onChangeGoogleAddress`', () => {
			expect(result).toHaveProperty('onChangeGoogleAddress');
			expect(typeof result.onChangeGoogleAddress).toEqual('function');
		});

		it('should fire `dispatch` when `onChangeGoogleAddress` is called', () => {
			const field = 'addressLine1';
			const value = '240 W 37th Street';
			const type = types.CHANGE_LOCATION_FIELD;
			result.onChangeGoogleAddress({ addressLine1: value });
			expect(dispatch).toHaveBeenCalledWith({
				name: field,
				type,
				value
			});
		});

		it('should return an object with property `onChangeLocationField`', () => {
			expect(result).toHaveProperty('onChangeLocationField');
			expect(typeof result.onChangeLocationField).toEqual('function');
		});

		it('should fire `dispatch` when `onChangeLocationField` is called', () => {
			const name = 'addressLine1';
			const value = '240 W 37th Street';
			const type = types.CHANGE_LOCATION_FIELD;
			result.onChangeLocationField(name, value);
			expect(dispatch).toHaveBeenCalledWith({
				name,
				type,
				value
			});
		});

		it('should return an object with property `onSubmitProfileForm`', () => {
			expect(result).toHaveProperty('onSubmitProfileForm');
			expect(typeof result.onSubmitProfileForm).toBe('function');
		});

		it('should fire `dispatch` when `onSubmitProfileForm` is called', () => {
			result.onSubmitProfileForm();
			expect(dispatch).toHaveBeenCalled();
		});
	});
});
