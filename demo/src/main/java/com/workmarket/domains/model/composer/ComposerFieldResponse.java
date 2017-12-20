package com.workmarket.domains.model.composer;

import java.util.List;

public class ComposerFieldResponse {

  private String uuid;
  private String key;
  private boolean success;
  private List<String> messages;

  public ComposerFieldResponse() {}

  public ComposerFieldResponse(final String uuid, final String key, final boolean success, final List<String> messages) {
    this.uuid = uuid;
    this.key = key;
    this.success = success;
    this.messages = messages;
  }

  public String getUuid() {
    return uuid;
  }

  public String getKey() {
    return key;
  }

  public boolean isSuccess() {
    return success;
  }

  public List<String> getMessages() {
    return messages;
  }

  @Override
  public String toString() {
    return "ComposerFieldResponse{" +
      "uuid='" + uuid + '\'' +
      ", key='" + key + '\'' +
      ", success=" + success +
      ", messages=" + messages +
      '}';
  }
}