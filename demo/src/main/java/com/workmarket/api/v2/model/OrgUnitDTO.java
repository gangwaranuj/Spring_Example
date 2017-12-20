package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.business.gen.Messages.OrgUnitPath;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;


@ApiModel(value = "OrgUnitDTO")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = OrgUnitDTO.Builder.class)
public class OrgUnitDTO {

  private final String uuid;
  private final String name;
  private final List<String> paths;

  private OrgUnitDTO(final Builder builder) {
    this.name = builder.name;
    this.uuid = builder.uuid;
    this.paths = builder.paths;
  }

  @ApiModelProperty(value = "Org unit name", example = "Magazine")
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @ApiModelProperty(value = "Unique UUID.", example = "e44cc935-0813-4198-baa3-b3cb70a32e7e")
  @JsonProperty("uuid")
  public String getUuid() {
    return uuid;
  }

  @ApiModelProperty(value = "Paths", example = "[COMPANY B, Magazine, Mag-T]")
  @JsonProperty("paths")
  public List<String> getPaths() {
    return paths;
  }

  public static final class Builder {
    private String uuid;
    private String name;
    private List<String> paths;

    public Builder(final OrgUnitPath orgUnitPath) {
      if (orgUnitPath != null) {
        this.name = orgUnitPath.getName();
        this.uuid = orgUnitPath.getUuid();
        this.paths = orgUnitPath.getPathList();
      }
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

    @JsonProperty("paths")
    public Builder paths(final List<String> paths) {
      this.paths = paths;
      return this;
    }

    public OrgUnitDTO build() {
      return new OrgUnitDTO(this);
    }
  }
}

