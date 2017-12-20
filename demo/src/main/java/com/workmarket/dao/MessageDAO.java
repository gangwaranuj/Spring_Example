package com.workmarket.dao;

import com.workmarket.domains.model.Message;
import com.workmarket.domains.model.MessagePagination;

public interface MessageDAO extends DAOInterface<Message> {

	MessagePagination findAllSentMessagesByUserGroup(Long groupId, MessagePagination pagination);

	Integer countAllSentMessagesByUserGroup(Long groupId);

}
