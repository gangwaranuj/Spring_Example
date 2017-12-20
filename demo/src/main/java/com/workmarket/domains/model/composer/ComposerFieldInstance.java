package com.workmarket.domains.model.composer;

public class ComposerFieldInstance extends ComposerField {
  private String value;

  public ComposerFieldInstance() {}

  public ComposerFieldInstance(final String type, final String uuid, final String key, final String value) {
    super(type, uuid, key);
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "ComposerFieldInstance{" +
      "value='" + value + '\'' +
      '}';
  }
}