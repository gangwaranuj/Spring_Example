package com.workmarket.domains.model.composer;

public class ComposerField {
  private String type;
  private String uuid;
  private String key;

  public ComposerField() {}

  public ComposerField(final String type, final String uuid, final String key) {
    this.type = type;
    this.uuid = uuid;
    this.key = key;
  }

  public String getType() {
    return type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(final String uuid) {
    this.uuid = uuid;
  }

  public String getKey() {
    return key;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  @Override
  public String toString() {
    return "ComposerField{" +
      "type='" + type + '\'' +
      ", uuid='" + uuid + '\'' +
      ", key='" + key + '\'' +
      '}';
  }
}