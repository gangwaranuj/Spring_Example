import { Map } from 'immutable';
import { initialState, processDrugConfig } from '../state';
import fields from '../../fields';

describe('Screening Order Form: InitialState', () => {
	it('should return an Immutable.js Map', () => {
		expect(initialState).toBeInstanceOf(Map);
	});

	Object.keys(fields).concat(['isFormValid']).forEach(name => {
		it(`should have the \`${name}\` field`, () => {
			expect(initialState.get(name)).toBeDefined();
		});
	});

	Object.keys(fields).forEach(name => {
		it(`should have a \`value\` property for the \`${name}\` field`, () => {
			expect(initialState.get(name).get('value')).toEqual(fields[name].defaultValue);
		});
	});

	Object.keys(fields).forEach(name => {
		it(`should have an empty \`error\` property for the \`${name}\` field`, () => {
			expect(initialState.get(name).get('error')).toEqual('');
		});
	});

	Object.keys(fields).forEach(name => {
		it(`should have a \`blurred\` property set to \`false\` for the \`${name}\` field`, () => {
			expect(initialState.get(name).get('blurred')).toBeFalsy();
		});
	});

	Object.keys(fields).forEach(name => {
		it(`should have a \`dirty\` property set to \`false\` for the \`${name}\` field`, () => {
			expect(initialState.get(name).get('dirty')).toEqual(fields[name].defaultValue ? true : false);
		});
	});
});

describe('Screening Order Form: processDrugConfig', () => {
	let config;
	const price = '50.00';
	const availableFunds = '25000.00';

	beforeEach(() => {
		config = {
			mode: 'screening',
			price,
			availableFunds,
			isInternational: false,
			drugTestPassed: false,
			drugTestFailed: false,
			drugTestPending: true
		};
	});

	it('should return an Immutable.js Map', () => {
		expect(processDrugConfig(config)).toBeInstanceOf(Map);
	});

	it('should have `isInternational` as false', () => {
		expect(processDrugConfig(config).get('isInternational')).toBeFalsy();
	});

	it('should have `drugTestPassed` as false', () => {
		expect(processDrugConfig(config).get('drugTestPassed')).toBeFalsy();
	});

	it('should have `drugTestFailed` as false', () => {
		expect(processDrugConfig(config).get('drugTestFailed')).toBeFalsy();
	});

	it('should have `drugTestPending` as true', () => {
		expect(processDrugConfig(config).get('drugTestPending')).toBeTruthy();
	});

	it('should have `price`', () => {
		expect(processDrugConfig(config).get('price')).toEqual(price);
	});

	describe('`hasSufficientFunds`', () => {
		it(`should be \`true\` if you have more than $${price}`, () => {
			expect(processDrugConfig(config).get('hasSufficientFunds')).toBeTruthy();
		});

		it(`should be \`false\` if you have less than $${price}`, () => {
			config.availableFunds = '1.00';
			expect(processDrugConfig(config).get('hasSufficientFunds')).toBeFalsy();
		});
	});

	describe('`canOrder`', () => {
		it('should be `false` if user is international and drug test is pending', () => {
			config.isInternational = true;
			expect(processDrugConfig(config).get('canOrder')).toBeFalsy();
		});

		it('should be `false` if user is not international and drug test is pending', () => {
			expect(processDrugConfig(config).get('canOrder')).toBeFalsy();
		});

		it('should be `false` if user is international and drug test is not pending', () => {
			config.isInternational = true;
			expect(processDrugConfig(config).get('canOrder')).toBeFalsy();
		});

		it('should be `true` if user is not international and drug test is not pending', () => {
			config.drugTestPending = false;
			expect(processDrugConfig(config).get('canOrder')).toBeTruthy();
		});
	});
});
