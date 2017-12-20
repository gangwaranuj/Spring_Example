import corePersonalInformation from './core/personalInformation';
import USAPersonalInformation from './USA/personalInformation';
import CANPersonalInformation from './CAN/personalInformation';
import corePaymentInformation from './core/paymentInformation';
import USAPaymentInformation from './USA/paymentInformation';
import CANPaymentInformation from './CAN/paymentInformation';

const personalInformation = {
	core: corePersonalInformation,
	USA: USAPersonalInformation,
	CAN: CANPersonalInformation
};
const paymentInformation = {
	core: corePaymentInformation,
	USA: USAPaymentInformation,
	CAN: CANPaymentInformation
};

export const all = Object.assign(
	{},
	personalInformation.core,
	personalInformation.USA,
	personalInformation.CAN,
	paymentInformation.core,
	paymentInformation.USA,
	paymentInformation.CAN
);

export const getFields = (country, paymentType) => {
	const base = Object.assign(
		{},
		personalInformation.core,
		personalInformation[country]
	);

	if (paymentType === 'cc') {
		return Object.assign(
			base,
			paymentInformation.core,
			paymentInformation[country]
		);
	} else {
		return Object.assign(
			base,
			{ paymentType: paymentInformation.core.paymentType }
		);
	}
};

export const getFieldsForSubmission = (country, paymentType) => {
	const submission = {
		screening: Object.assign(
			{},
			personalInformation.core,
			personalInformation[country]
		),
		payment: {}
	};

	if (paymentType === 'cc') {
		submission.payment = Object.assign(
			{},
			paymentInformation.core,
			paymentInformation[country]
		);
	} else {
		submission.payment = {
			paymentType: paymentInformation.core.paymentType
		};
	}

	return submission;
};

export default all;
