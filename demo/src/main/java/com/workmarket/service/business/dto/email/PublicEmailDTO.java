package com.workmarket.service.business.dto.email;

import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.utility.StringUtilities;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 * Created by nick on 5/14/13 10:47 AM
 */
public class PublicEmailDTO extends EMailDTO {

	public String getFormattedText() {
		return String.format("Message: %s<br/>", text);
	}

	@Override
	public void setText(String text) {
		this.text = StringUtilities.stripTags(text);
	}

	@Override
	@NotEmpty
	@Pattern(regexp = "^[a-zA-Z0-9'àáâäãåèéêëìíîïòóôöõøùúûüÿýñçčšžÀÁÂÄÃÅÈÉÊËÌÍÎÏÒÓÔÖÕØÙÚÛÜŸÝÑßÇŒÆČŠŽ∂ð\\-\\p{Space}]{2,50}+$")
	public String getFromName() {
		return fromName;
	}

	@Override
	@NotEmpty
	@Pattern(regexp = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
	public String getFromEmail() {
		return fromEmail;
	}

	@Override
	@NotEmpty
	public String getText() {
		return text;
	}
}
