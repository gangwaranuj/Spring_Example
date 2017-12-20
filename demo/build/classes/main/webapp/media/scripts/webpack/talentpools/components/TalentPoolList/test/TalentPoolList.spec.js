import React from 'react';
import { shallow } from 'enzyme';
import { WMDrawer, WMTable, WMTableRow, WMTableHeaderCell, WMRaisedButton } from '@workmarket/front-end-components';
import TalentPoolList from '../template';
import { initialState } from '../../../reducers/index';

const mergedState = initialState.mergeDeep({
	rootData: {
		talentPools: [
			{
				requires_approval: false,
				name: '1 1 1 1 1 1 1 2 aaa newest test ok hi',
				description: 'awegaweg',
				isActive: true,
				industry: 1052,
				count: '0',
				pendingCount: '2',
				id: 5131,
				test: 'test',
				is_admin: true,
				open_membership: false,
				is_member: false,
				owner_company: 'CoName_6225',
				owner_id: '13791',
				owner_user_number: '2653612',
				owner_full_name: 'Greg Stipe',
				searchable: false,
				auto_generated: false,
				is_shared_by_me: false,
				is_shared_with_me: false,
				org_units: [{
					name: "Org unit name"
				}]
			}
		]
	}
});

describe('<TalentPoolList />', () => {
	const renderComponent = (
		state = mergedState,
		onToggleActive = () => {},
		onManageGroup = () => {},
		onOpenProfileModal = () => {},
		fetchTalentPools = () => {},
		newTalentPool = () => {},
		onCloseDrawer = () => {},
		onSort = () => {},
		enableOrgStructures = true
	) => shallow(
		<TalentPoolList
			talentPoolData={ state.get('rootData') }
			onToggleActive={ onToggleActive }
			onManageGroup={ onManageGroup }
			onOpenProfileModal={ onOpenProfileModal }
			fetchTalentPools={ fetchTalentPools }
			newTalentPool={ newTalentPool }
			onCloseDrawer={ onCloseDrawer }
			onSort={ onSort }
			enableOrgStructures={ enableOrgStructures }
		/>
	);

	let wrapper;

	beforeEach(() => {
		wrapper = renderComponent();
	});

	describe('Rendering', () => {
		it('should have a table', () => {
			const component = wrapper.find(WMTable);
			expect(component).toHaveLength(1);
		});
		it('should create a table row for each talent pool', () => {
			const component = wrapper.find(WMTableRow);
			expect(component).toHaveLength(1);
			const emptyState = wrapper.find('#emptyTalentPools');
			expect(emptyState).toHaveLength(0);
		});
		it('should have a drawer', () => {
			const component = wrapper.find(WMDrawer);
			expect(component).toHaveLength(1);
		});
		it('should have an empty state', () => {
			wrapper = renderComponent(initialState);
			const component = wrapper.find('#emptyTalentPools');
			expect(component).toHaveLength(1);
		});
		it('should have show sort controls', () => {
			wrapper = renderComponent(initialState);
			const downComponent = wrapper.find('#sort-down');
			expect(downComponent).toHaveLength(1);
			const upComponent = wrapper.find('#sort-up');
			expect(upComponent).toHaveLength(1);
		});
		it('should have a `Create Talent Pool` button', () => {
			wrapper = renderComponent(mergedState);
			const component = wrapper.find(WMRaisedButton);
			expect(component).toHaveLength(1);
			expect(component.prop('label')).toEqual('Create Talent Pool');
		});
	});

	describe('Rendering table column headers', () => {
		let headers;
		let component;
		let columnRow;

		it('should have the correct amount of table column headers and type `WMTableHeaderCell`', () => {
			headers = wrapper.find(WMTableHeaderCell);
			expect(headers).toHaveLength(8);
			expect(headers.get(0).type).toEqual(WMTableHeaderCell);
		});

		const headerColumns = [{ component: 'talentpools_headerCell_name', columnHeader: 'Name' },
								{ component: 'talentpools_headerCell_orgUnits', columnHeader: 'Org Unit' },
								{ component: 'talentpools_headerCell_type', columnHeader: 'Type' },
								{ component: 'talentpools_headerCell_owner', columnHeader: 'Owner' },
								{ component: 'talentpools_headerCell_members', columnHeader: 'Members' },
								{ component: 'talentpools_headerCell_applied', columnHeader: 'Applied' },
								{ component: 'talentpools_headerCell_invited', columnHeader: 'Invited' },
								{ component: 'talentpools_headerCell_active', columnHeader: 'Active' }];
		headerColumns.forEach((header) => {
			it(`should have a ${header.columnHeader} column header`, () => {
				component = wrapper.find(WMTableHeaderCell).find(`[data-component-identifier="${header.component}"]`);
				columnRow = component.at(0).prop('children');
				expect(columnRow[0].props.children).toEqual(`${header.columnHeader}`);
			});
		});
	});
});
