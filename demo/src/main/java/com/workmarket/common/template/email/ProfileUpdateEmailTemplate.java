package com.workmarket.common.template.email;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.request.PasswordResetRequest;

public class ProfileUpdateEmailTemplate extends EmailTemplate {

  private static final long serialVersionUID = -7883181160613657765L;

  private String fieldName;

  public ProfileUpdateEmailTemplate(Long toId, String fieldName) {
    super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, null);
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return this.fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

}
