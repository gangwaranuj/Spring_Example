'use strict';
import $ from 'jquery';
import getCSRFToken from './getCSRFToken'

const ajaxSendInit = () => {
	$(document).ajaxSend(function (elm, xhr, s) {

		if (s.type === 'POST' || s.type === 'PUT' || s.type === 'DELETE') {
			xhr.setRequestHeader('X-CSRF-Token', getCSRFToken());
		}
	});
};

ajaxSendInit();

export default ajaxSendInit;
