package com.workmarket.web.recaptcha;

import com.google.common.base.Optional;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by nick on 5/15/13 6:19 PM
 */
@Component
public class ReCaptchaAdapterImpl implements ReCaptchaAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ReCaptchaAdapterImpl.class);

	@Value("${recaptcha.publicKey}")
	private String reCAPTCHAPublicKey;

	@Value("${recaptcha.privateKey}")
	private String reCAPTCHAPrivateKey;

	@Override
	public Optional<ReCaptchaResponse> checkAnswer(String address, String challenge, String userResponse) {

		ReCaptchaImpl reCaptcha;
		reCaptcha = new ReCaptchaImpl();
		reCaptcha.setPrivateKey(reCAPTCHAPrivateKey);

		ReCaptchaResponse response = null;
		try {
			response = reCaptcha.checkAnswer(address, challenge, userResponse);
		} catch (Exception e) {
			logger.error("Error accessing reCAPTCHA: ", e);
		}
		return Optional.fromNullable(response);
	}

	@Override
	public String getPublicKey() {
		return reCAPTCHAPublicKey;
	}
}
