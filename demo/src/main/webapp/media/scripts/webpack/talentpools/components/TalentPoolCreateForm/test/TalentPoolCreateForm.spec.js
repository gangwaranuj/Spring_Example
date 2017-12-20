import React from 'react';
import { shallow } from 'enzyme';
import TalentPoolCreateForm from '../template';
import { initialState } from '../../../reducers/index';

describe('<TalentPoolCreateForm />', () => {
	const renderComponent = (
		enableOrgStructures = false,
		groupId = 0,
		state = initialState.mergeDeep({
			formData: {
				id: groupId,
				openMembership: true,
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
				orgUnitUuids: ['org-001']
			}
		})
	) => shallow(
		<TalentPoolCreateForm
			talentPoolFormData={ state.get('formData') }
			defaultOrgUnitUuid={ state.get('formData').get('orgMode').get('uuid') }
			enableOrgStructures={ enableOrgStructures }
		/>
	);
	let wrapper;

	describe('Rendering', () => {
		it('should render basic form', () => {
			wrapper = renderComponent();
			expect(wrapper).toMatchSnapshot();
		});

		it('should render correctly when org units enabled', () => {
			wrapper = renderComponent(true);
			expect(wrapper).toMatchSnapshot();
		})
	});
});
