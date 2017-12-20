import React from 'react';
import { shallow } from 'enzyme';
import { WMCard, WMTextField, WMRaisedButton } from '@workmarket/front-end-components';
import TalentPoolMessages from '../template';
import { initialState } from '../../../reducers/index';

const groupId = 123;

const mergedState = initialState.mergeDeep({
	messagesData: {
		id: groupId,
		isActive: true,
		messages: [
			{
				sender_full_name: 'John Doe',
				date: '2011-03-30T07:32:15.000Z',
				subject: 'A message title',
				content: 'The content of the message'
			}
		]
	},
	messageFormData: {
		title: '',
		message: ''
	}
});


describe('<TalentPoolMessages />', () => {
	const renderComponent = (
		state = mergedState,
		onChangeField = () => {},
		onSubmitMessageForm = () => {},
		handleEditDetails = () => {}
	) => shallow(
		<TalentPoolMessages
			messagesData={ state.get('messagesData') }
			messageFormData={ state.get('messageFormData') }
			onChangeField={ onChangeField }
			onSubmitMessageForm={ onSubmitMessageForm }
			handleEditDetails={ handleEditDetails }
		/>
	);
	let wrapper;

	describe('Rendering', () => {
		it('should create a card row for each message', () => {
			wrapper = renderComponent();
			const component = wrapper.find(WMCard);
			expect(component).toHaveLength(1);
		});

		it('should have a WMTextField', () => {
			wrapper = renderComponent();
			const component = wrapper.find(WMTextField);
			expect(component).toHaveLength(2);
		});

		it('should have a send message button', () => {
			wrapper = renderComponent();
			const component = wrapper.find(WMRaisedButton);
			expect(component).toHaveLength(1);
			expect(component.prop('label')).toEqual('Send message');
		});
		it('should have an inactive state message', () => {
			wrapper = renderComponent(mergedState.setIn(['messagesData', 'isActive'], false));
			const component = wrapper.find('#inactiveTalentPoolState');
			expect(component).toHaveLength(1);
		});

	});
});
