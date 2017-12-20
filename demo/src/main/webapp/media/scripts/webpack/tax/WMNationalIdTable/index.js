import React from 'react';
import {
	WMTable,
	WMTableHeader,
	WMTableHeaderCell,
	WMTableBody,
	WMTableRow,
	WMTableCell
} from '@workmarket/front-end-components';
import codes from './data';

const WMNationalIdTable = () => (
	<WMTable
		striped
		hideDisabledCheckboxes
		maxHeight={ '200px' }
	>
		<WMTableHeader>
			<WMTableHeaderCell>Country Name</WMTableHeaderCell>
			<WMTableHeaderCell>ID Type</WMTableHeaderCell>
			<WMTableHeaderCell>National ID</WMTableHeaderCell>
		</WMTableHeader>
		<WMTableBody>
			{
				codes.map((code, index) => (
					<WMTableRow key={ index }>
						<WMTableCell>{ code.country }</WMTableCell>
						<WMTableCell>{ code.countryId }</WMTableCell>
						<WMTableCell>{ code.englishName }</WMTableCell>
					</WMTableRow>
				))
			}
		</WMTableBody>
	</WMTable>
);

export default WMNationalIdTable;
