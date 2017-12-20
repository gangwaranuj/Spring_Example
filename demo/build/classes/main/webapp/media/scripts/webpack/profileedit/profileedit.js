import wmGooglePlaces from '../funcs/googlePlaces';
import wmMaskInput from '../funcs/wmMaskInput';

const maskOptions = {
	translation: {
		'~': {
			optional: true,
			pattern: /[-0-9 ]/
		}
	}
};

export default function () {
	wmMaskInput({ selector: '#workPhone, #mobilePhone' }, '~~~~~~~~~~~~~~', maskOptions);
	wmGooglePlaces();
}
