import React from 'react';
import { shallow } from 'enzyme';
import { WMModal } from '@workmarket/front-end-components';
import TalentPoolForm from '../template';
import { initialState } from '../../../reducers/index';

describe('<TalentPoolForm />', () => {
	const renderComponent = (
		enableOrgStructures = false,
		groupId = 0,
		state = initialState.mergeDeep({
			formData: {
				id: groupId,
				openMembership: 'true',
				isDeleteModalOpen: false,
				industries: [
					{
						name: 'Sales, Event, and Promotional Marketing',
						order: 0,
						deleted: false,
						created_on: '2011-03-30T07:32:15.000Z',
						modified_on: '2011-03-30T07:32:15.000Z',
						modifier_id: 1,
						creator_id: 1,
						id: 1045
					}
				],
				owners: {
					19262: 'API API'
				},
				orgMode: {
					uuid: 'org-001',
					name: 'Org A'
				},
				orgUnits: [
					{
						uuid: 'org-001',
						name: 'Org A',
					},
					{
						uuid: 'org-002',
						name: 'Org B',
						paths: ['Org A']
					},
					{
						uuid: 'org-003',
						name: 'Org C',
						paths: ['Org A', 'Org B']
					}
				],
				orgUnitUuids: ['org-002', 'org-001']
			}
		})
	) => shallow(
		<TalentPoolForm
			talentPoolFormData={ state.get('formData') }
			enableOrgStructures={ enableOrgStructures }
			defaultOrgUnitUuid="org-001"
		/>
	);
	let wrapper;

	describe('Rendering', () => {
		it('should render the same way with orgStructures disabled', () => {
			wrapper = renderComponent();
			expect(wrapper).toMatchSnapshot();
		});

		it('should render the same way with orgStructures enabled', () => {
			wrapper = renderComponent(true);
			expect(wrapper).toMatchSnapshot();
		})
	});
});
