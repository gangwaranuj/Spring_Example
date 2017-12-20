import React from 'react';
import { shallow } from 'enzyme';
import TaxFormModal from '../TaxFormModal';

describe('<TaxFormModal /> ::', () => {
	const open = true;
	const taxName = 'Cowboys Cowboy Boot Emporium';
	const dbaName = 'Guy Fieris American Bar & Grille';
	const taxEntityTypeCode = 'c_corp';
	const address = '123 Anyplace Street';
	const addressTwo = 'Corsicana, TX 75110';
	const eid = '23-2349911';
	const signature = 'Cowboy McClure';
	const signatureDateString = '10/17/1989';
	const onSubmitForm = () => {};
	const onChangeField = () => {};
	const closeModal = () => {};


	describe('Rendering ::', () => {
		it('renders corrects', () => {
			const component = shallow(
				<TaxFormModal
					open={ open }
					taxName={ taxName }
					dbaName={ dbaName }
					taxEntityTypeCode={ taxEntityTypeCode }
					address={ address }
					addressTwo={ addressTwo }
					eid={ eid }
					signature={ signature }
					signatureDateString={ signatureDateString }
					onSubmitForm={ onSubmitForm }
					onChangeField={ onChangeField }
					closeModal={ closeModal }
				/>
			);
			expect(component).toMatchSnapshot();
		});
	});
});
