package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.domains.onboarding.model.Qualification;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by jlevine on 14/12/2016.
 */
@ApiModel
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = SkillApiDTO.Builder.class)
public class SkillApiDTO {

  // TODO API - This field will always be null on egress
  // -- this is because the Data Structures used to pull profiles from the DB don't include it.
  // See com.workmarket.web.facade.ProfileFacade.getSkills() for example on what we're working with
  private final String uuid;
  private final Long id;
  private final String name;
  private final Qualification.Type type;

  private SkillApiDTO(final Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.uuid = builder.uuid;
    this.type = builder.type;
  }

  @ApiModelProperty(
      value = "Unique ID. This is a monolith understood identifier; use UUID if you can.",
      example = "123455")
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  @ApiModelProperty(value = "Skill name", example = "Project Management")
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @ApiModelProperty(
      value = "Unique UUID. This is the preferred ID for a Skill and is preferred over 'id'.",
      example = "e44cc935-0813-4198-baa3-b3cb70a32e7e")
  @JsonProperty("uuid")
  public String getUuid() {
    return uuid;
  }

  @ApiModelProperty(value = "Qualification type", example = "SKILL")
  @JsonProperty("type")
  public Qualification.Type getType() {
    return type;
  }

  public static final class Builder {
    private String uuid;
    private Long id;
    private String name;
    private Qualification.Type type = Qualification.Type.SKILL;

    public Builder() {
    }

    public Builder(final Long id, final String name, String uuid, Qualification.Type type) {
      this.id = id;
      this.name = name;
      this.uuid = uuid;
      this.type = type;
    }

    public Builder(SkillApiDTO skill) {
      this.id = skill.id;
      this.uuid = skill.uuid;
      this.name = skill.name;
      this.type = skill.type;
    }

    @JsonProperty("id")
    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    @JsonProperty("name")
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    @JsonProperty("uuid")
    public Builder uuid(final String uuid) {
      this.uuid = uuid;
      return this;
    }

    @JsonProperty("type")
    public Builder type(final Qualification.Type type) {
      this.type = type;
      return this;
    }

    public SkillApiDTO build() {
      return new SkillApiDTO(this);
    }
  }
}
