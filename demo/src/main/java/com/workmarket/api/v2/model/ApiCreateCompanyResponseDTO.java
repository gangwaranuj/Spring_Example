package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.domains.model.User;
import io.swagger.annotations.ApiModel;

@ApiModel("CreateCompanyResponse")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
public class ApiCreateCompanyResponseDTO {

  private String userUuid;
  private String userNumber;
  private String companyUuid;
  private String companyNumber;

  public ApiCreateCompanyResponseDTO() {
  }

  public ApiCreateCompanyResponseDTO(User user) {
    this.userUuid= user.getUuid();
    this.userNumber = user.getUserNumber();
    this.companyUuid = user.getCompany().getUuid();
    this.companyNumber = user.getCompany().getCompanyNumber();
  }

  public String getUserNumber() {
    return userNumber;
  }

  public void setUserNumber(String userNumber) {
    this.userNumber = userNumber;
  }

  public String getCompanyNumber() {
    return companyNumber;
  }

  public void setCompanyNumber(String companyNumber) {
    this.companyNumber = companyNumber;
  }

  public String getUserUuid() {
    return userUuid;
  }

  public void setUserUuid(String userUuid) {
    this.userUuid = userUuid;
  }

  public String getCompanyUuid() {
    return companyUuid;
  }

  public void setCompanyUuid(String companyUuid) {
    this.companyUuid = companyUuid;
  }
}
