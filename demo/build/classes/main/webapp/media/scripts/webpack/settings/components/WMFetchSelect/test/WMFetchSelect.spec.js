import 'isomorphic-fetch';
import React from 'react';
import { shallow } from 'enzyme';
import nock from 'nock';
import {
	WMSelectField,
	WMMenuItem
} from '@workmarket/front-end-components';
import WMFetchSelect from '../';

describe('<WMFetchSelect />', () => {
	const shallowRenderComponent = (
		fetchURL = '/poop'
	) =>
		shallow(
			<WMFetchSelect
				fetchURL={ fetchURL }
			/>
		);

	const setupMockServer = ({ response }) => {
		return nock('http://localhost:8080')
			.get(/.*/)
			.reply(200, response);
	};

	describe('Rendering ::', () => {
		it('should contain <WMSelectField />', () => {
			const wrapper = shallowRenderComponent();
			expect(wrapper.find(WMSelectField).length).toEqual(1);
		});

		it('should contain a <WMMenuItem /> with employee info', () => {
			const wrapper = shallowRenderComponent();
			const options = [
				[
					'Guy Fieri'
				]
			];
			wrapper.setState({ options });
			const menuItem = wrapper.find(WMMenuItem);
			expect(menuItem.length).toEqual(1);
			expect(menuItem.prop('value')).toEqual(options[0][0]);
			expect(menuItem.prop('primaryText')).toEqual(options[0][0]);
		});
	});

	it('should fetch employee list on mount', () => {
		const options = {
			results: [
				[
					'Guy Fieri'
				]
			]
		};
		const mockServer = setupMockServer({
			response: options
		});
		const wrapper = shallowRenderComponent();
		wrapper.instance().componentDidMount('http://localhost:8080');
		mockServer.isDone();
	});
});
