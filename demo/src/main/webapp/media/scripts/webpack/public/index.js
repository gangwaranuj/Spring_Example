import $ from 'jquery';
import GooglePlaces from '../funcs/googlePlaces';
import wmMaskInput from '../funcs/wmMaskInput';

switch (config.type) {
case 'signup':
	GooglePlaces();
	$('#firstName').focus();
	wmMaskInput({ selector: '#workPhone' });
	break;

case 'findwork':
	GooglePlaces();
	break;

default:
	throw new Error('You must specify a public config.type!');
}
