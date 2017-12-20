import React from 'react';
import { shallow } from 'enzyme';
import { Map } from 'immutable';
import nock from 'nock';
import {
	WMTabs,
	WMTab
} from '@workmarket/front-end-components';
import { UnconnectedComponent as WMAddEmployees } from '../';
import WMAddEmployeeForm from '../WMAddEmployeeForm';
import WMImportEmployees from '../WMImportEmployees';
import styles from '../styles';

const serverIndustries = [
	{ id: 200, name: 'Potato Peeling' },
	{ id: 201, name: 'T crossing' }
];
const serverPhoneInternationalCodes = [
	{ id: 100, name: 'USA (+1)' },
	{ id: 101, name: 'Algeria (+213)' }
];
const setupMockServer = () => {
	return nock('http://localhost:8080')
		.get('/industries-list')
		.reply(200, serverIndustries)
		.get('/v2/constants/country_codes?fields=id,name')
		.reply(200, { results: serverPhoneInternationalCodes });
};

describe('<WMAddEmployees />', () => {
	let wrapper;
	const settings = Map({ submitting: false });
	const sampleIndustries = [
		{ id: 100, name: 'Dice Throwing' },
		{ id: 101, name: 'Cage Fighting' }
	];
	const sampleCSVUrl = '/download/sample_employees.csv';

	describe('Rendering', () => {
		beforeEach(() => {
			wrapper = shallow(
				<WMAddEmployees
					settings={ settings }
				/>
			);
		});

		describe('Tabs', () => {
			it('should have one (1) <WMTabs />', () => {
				const component = wrapper.find(WMTabs);
				expect(component.length).toEqual(1);
			});

			it('should have <WMTabs /> with style', () => {
				const component = wrapper.find(WMTabs);
				expect(component.prop('tabItemContainerStyle')).toEqual(styles.tabs);
				expect(component.prop('inkBarStyle')).toEqual(styles.inkBar);
			});

			it('should have two (2) <WMTab />', () => {
				const component = wrapper.find(WMTab);
				expect(component.length).toEqual(2);
			});

			it('should have <WMTab />s with style', () => {
				const tabs = wrapper.find(WMTab);

				tabs.forEach((tab) => {
					expect(tab.prop('style')).toEqual(styles.tab);
				});
			});

			it('should have <WMTab />s with labels', () => {
				const tabs = wrapper.find(WMTab);

				expect(tabs.at(0).prop('label')).toEqual('Add Individually');
				expect(tabs.at(1).prop('label')).toEqual('Import');
			});
		});

		it('should have a <WMAddEmployeeForm />', () => {
			const component = wrapper.find(WMAddEmployeeForm);
			expect(component.length).toEqual(1);
		});

		it('should have a <WMImportEmployees />', () => {
			const component = wrapper.find(WMImportEmployees);
			expect(component.length).toEqual(1);
		});
	});

	describe('Properties & State', () => {
		beforeEach(() => {
			setupMockServer();
			wrapper = shallow(
				<WMAddEmployees
					settings={ settings }
				/>
			);
		});

		afterEach(() => {
			nock.cleanAll();
		});

		describe('<WMAddEmployeeForm />', () => {
			it('should set `disabled`', () => {
				const component = wrapper.find(WMAddEmployeeForm);
				expect(component.prop('disabled')).toEqual(settings.get('submitting'));
			});

			it('should set `industries`', () => {
				wrapper.setState({ industries: sampleIndustries });
				const component = wrapper.find(WMAddEmployeeForm);
				expect(component.prop('industries')).toEqual(sampleIndustries);
			});
		});

		describe('<WMImportEmployees />', () => {
			it('should set `sampleCSVUrl`', () => {
				const component = wrapper.find(WMImportEmployees);
				expect(component.prop('sampleCSVUrl')).toEqual(sampleCSVUrl);
			});
		});

		describe('`industries`', () => {
			it('should get the industries on mount', () => {
				return wrapper.instance().getIndustries('http://localhost:8080')
					.then(() => {
						const state = wrapper.state();
						expect(state.industries).toEqual(serverIndustries);
					});
			});
		});

		describe('`phoneInternationalCodes`', () => {
			it('should get the international calling codes on mount', () => {
				return wrapper.instance().getPhoneInternationalCodes('http://localhost:8080')
					.then(() => {
						const state = wrapper.state();
						expect(state.phoneInternationalCodes).toEqual(serverPhoneInternationalCodes);
					});
			});
		});
	});
});
