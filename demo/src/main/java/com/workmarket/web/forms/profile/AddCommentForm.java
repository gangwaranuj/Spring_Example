package com.workmarket.web.forms.profile;

import com.workmarket.service.business.dto.UserCommentDTO;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class AddCommentForm {
	@NotEmpty
	private String id;  // this is actually user number

	@NotNull
	private String comment;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

}
