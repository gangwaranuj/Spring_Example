package com.workmarket.domains.model;

public class MessagePagination extends AbstractPagination<Message> implements Pagination<Message> {
	
	public enum FILTER_KEYS {}
	public enum SORTS {
		CREATED_ON
	}
}


