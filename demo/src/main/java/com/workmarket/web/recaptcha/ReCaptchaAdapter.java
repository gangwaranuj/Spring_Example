package com.workmarket.web.recaptcha;

import com.google.common.base.Optional;
import net.tanesha.recaptcha.ReCaptchaResponse;

/**
 * Created by nick on 5/15/13 6:16 PM
 */
public interface ReCaptchaAdapter {

	Optional<ReCaptchaResponse> checkAnswer(String address, String challenge, String response);

	String getPublicKey();
}
