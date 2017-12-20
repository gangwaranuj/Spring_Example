package com.workmarket.service.external;


import com.workmarket.service.external.vo.GoogleRecaptchaResponse;

public interface GoogleRecaptchaAdapter {
	GoogleRecaptchaResponse verify(String response);
}
