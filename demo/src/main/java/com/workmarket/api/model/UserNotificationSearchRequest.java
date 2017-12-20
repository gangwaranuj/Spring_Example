package com.workmarket.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.notification.Direction;
import com.workmarket.notification.OrderField;
import com.workmarket.notification.user.vo.UserNotificationStatus;

@JsonDeserialize(builder = UserNotificationSearchRequest.UserNotificationSearchRequestBuilder.class)
public class UserNotificationSearchRequest {
  public static final int LIMIT_DEFAULT = 15;
  public static final int OFFSET_DEFAULT = 0;

  private final Boolean archived;
  private final Boolean viewed;
  private final OrderField order;
  private final Direction direction;
  private final String type; // message type
  private final UserNotificationStatus status;
  private final int offset; // zero based
  private final int limit;

  public UserNotificationSearchRequest(final UserNotificationSearchRequestBuilder builder) {
    this.limit = builder.getLimit();
    this.offset = builder.getOffset();
    this.archived = builder.getArchived();
    this.viewed = builder.getViewed();
    this.order = builder.getOrder();
    this.direction = builder.getDirection();
    this.type = builder.getType();
    this.status = builder.getStatus();
  }

  public static UserNotificationSearchRequestBuilder builder() {
    return new UserNotificationSearchRequestBuilder();
  }

  public static class UserNotificationSearchRequestBuilder {
    private Boolean archived;
    private Boolean viewed;
    private OrderField order;
    private Direction direction;
    private String type; // message type
    private UserNotificationStatus status;
    private int offset; // zero based
    private int limit;

    public UserNotificationSearchRequest build() {
      return new UserNotificationSearchRequest(this);
    }

    public Boolean getArchived() {
      return archived;
    }

    @JsonProperty("archived") public UserNotificationSearchRequestBuilder setArchived(final Boolean archived) {
      this.archived = archived;
      return this;
    }

    public Boolean getViewed() {
      return viewed;
    }

    @JsonProperty("viewed") public UserNotificationSearchRequestBuilder setViewed(final Boolean viewed) {
      this.viewed = viewed;
      return this;
    }

    public OrderField getOrder() {
      return order;
    }

    @JsonProperty("order") public UserNotificationSearchRequestBuilder setOrder(final OrderField order) {
      this.order = order;
      return this;
    }

    public Direction getDirection() {
      return direction;
    }

    @JsonProperty("direction") public UserNotificationSearchRequestBuilder setDirection(final Direction direction) {
      this.direction = direction;
      return this;
    }

    public String getType() {
      return type;
    }

    @JsonProperty("type") public UserNotificationSearchRequestBuilder setType(final String type) {
      this.type = type;
      return this;
    }

    public UserNotificationStatus getStatus() {
      return status;
    }

    @JsonProperty("status") public UserNotificationSearchRequestBuilder setStatus(final UserNotificationStatus status) {
      this.status = status;
      return this;
    }

    public int getOffset() {
      return offset;
    }

    @JsonProperty("offset") public UserNotificationSearchRequestBuilder setOffset(final int offset) {
      this.offset = offset;
      return this;
    }

    public int getLimit() {
      return limit;
    }

    @JsonProperty("limit") public UserNotificationSearchRequestBuilder setLimit(final int limit) {
      this.limit = limit;
      return this;
    }
  }

  public OrderField getOrder() {
    return order;
  }

  public Direction getDirection() {
    return direction;
  }

  public String getType() {
    return type;
  }

  public UserNotificationStatus getStatus() {
    return status;
  }

  public Boolean getArchived() {
    return archived;
  }

  public Boolean getViewed() {
    return viewed;
  }

  public int getOffset() {
    return offset;
  }

  public int getLimit() {
    return limit;
  }
}
