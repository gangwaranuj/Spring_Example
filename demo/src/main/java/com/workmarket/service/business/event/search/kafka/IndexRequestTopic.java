package com.workmarket.service.business.event.search.kafka;

/**
 * A list of cores to be indexed.
 */
public enum IndexRequestTopic {
	/**
	 * UNKNOWN.
	 */
	UNKNOWN("unknown"),
	/**
	 * INDEX_USER_REQUEST.
	 */
	INDEX_USER_REQUEST("index_user_request"),
	/**
	 * INDEX_WORK_REQUEST.
	 */
	INDEX_WORK_REQUEST("index_work_request"),
	/**
	 * INDEX_COMPANY_REQUEST.
	 */
	INDEX_COMPANY_REQUEST("index_company_request"),
	/**
	 * INDEX_GROUP_REQUEST.
	 */
	INDEX_GROUP_REQUEST("index_group_request"),
	/**
	 * ORG_MEMBERSHIP_UPDATE.
	 */
	ORG_MEMBERSHIP_UPDATE("org-membership-update");

	private final String indexRequestTopic;

	/**
	 * Ctor.
	 *
	 * @param indexRequestTopic
	 */
	IndexRequestTopic(final String indexRequestTopic) {
		this.indexRequestTopic = indexRequestTopic;
	}

	/**
	 * Gets IndexRequestTopic enum given a string.
	 *
	 * @param indexRequestTopic
	 * @return IndexRequestTopic
	 */
	public static IndexRequestTopic getIndexRequestTopic(final String indexRequestTopic) {
		for (final IndexRequestTopic topic : values()) {
			if (topic.getIndexRequestTopic().equalsIgnoreCase(indexRequestTopic)) {
				return topic;
			}
		}
		return UNKNOWN;
	}

	/**
	 * Gets the indexRequestTopic of the kafka message.
	 *
	 * @return String
	 */
	public String getIndexRequestTopic() {
		return indexRequestTopic;
	}
}
