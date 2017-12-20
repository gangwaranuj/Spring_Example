package com.workmarket.data.solr.indexer;

import java.util.Collection;

public interface Indexer {

	void reindexAll();

	void reindexByUUID(final Collection<String> uuids);

	void reindexById(Collection<Long> ids);

	void reindexById(Long id);

	void reindexBetweenIds(long fromId, long toId);

	void deleteById(Collection<Long> ids);
}
