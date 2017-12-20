package com.workmarket.service.business.event.group;

import com.workmarket.service.business.event.Event;

import java.util.List;

public class AddUsersToGroupEvent extends Event {
    private static final long serialVersionUID = -5034165378880900138L;
    Long groupId;
    Long inviteeUserId;
    List<Long> userIds;

    public AddUsersToGroupEvent(Long groupId, List<Long> userIds, Long inviteeUserId) {
        this.groupId = groupId;
        this.userIds = userIds;
        this.inviteeUserId = inviteeUserId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getInviteeUserId() {
        return inviteeUserId;
    }

    public void setInviteeUserId(Long inviteeUserId) {
        this.inviteeUserId = inviteeUserId;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
