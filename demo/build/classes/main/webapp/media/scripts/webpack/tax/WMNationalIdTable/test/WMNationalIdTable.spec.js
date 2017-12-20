import React from 'react';
import { shallow } from 'enzyme';
import WMNationalIdTable from '../index';
import {
	WMTable,
	WMTableHeader,
	WMTableHeaderCell,
	WMTableBody,
	WMTableRow,
	WMTableCell
} from '@workmarket/front-end-components';
import codes from '../data';

describe('<WMNationalIdTable />', () => {
	describe('Rendering', () => {
		let wrapper;

		beforeEach(() => {
			wrapper = shallow(
				<WMNationalIdTable />
			);
		});

		it('should be wrapped in a <WMTable />', () => {
			expect(wrapper.type()).toEqual(WMTable);
		});

		describe('<WMTableHeader />', () => {
			it('should exist', () => {
				const header = wrapper.find(WMTableHeader);
				expect(header).toHaveLength(1);
			});

			it('should have three (3) cells', () => {
				const headerCells = wrapper.find(WMTableHeaderCell);
				expect(headerCells).toHaveLength(3);
			});
		});

		describe('<WMTableBody />', () => {
			it('should exist', () => {
				const header = wrapper.find(WMTableBody);
				expect(header).toHaveLength(1);
			});

			it('should have rows', () => {
				const rows = wrapper.find(WMTableRow);
				expect(rows).toHaveLength(codes.length);
			});

			it('should three (3) cells per row', () => {
				const row = wrapper.find(WMTableRow).at(0);
				const cells = row.find(WMTableCell);
				expect(cells).toHaveLength(3);
			});
		});
	});
});
